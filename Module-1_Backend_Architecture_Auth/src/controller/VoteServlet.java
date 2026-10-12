package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.IssueDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.Issue;
import campusvoice.model.User;
import campusvoice.model.VerificationResult;
import campusvoice.model.VoteOption;
import campusvoice.service.TriggerService;
import campusvoice.util.JsonUtil;
import campusvoice.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * VoteServlet - Peer Verification Voting Endpoint
 * Member 1 & 2 Responsibility (Weeks 7-8):
 * 1. Checks student session & class eligibility.
 * 2. Generates irreversible one-way voter token hash.
 * 3. Prevents duplicate votes.
 * 4. Records vote anonymously.
 * 5. Calls TriggerService to recalculate validity and promote to 'CLASS_VERIFIED' if threshold is met.
 */
@WebServlet(name = "VoteServlet", urlPatterns = {"/api/issues/vote"})
public class VoteServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final TriggerService triggerService = new TriggerService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Please log in to participate in class verification.")));
            return;
        }

        String issueId = null;
        String voteResponseStr = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("issueId")) issueId = json.get("issueId").getAsString();
                    if (json.has("response")) voteResponseStr = json.get("response").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("MALFORMED_JSON", "Invalid JSON payload.")));
                    return;
                }
            }
        } else {
            issueId = request.getParameter("issueId");
            voteResponseStr = request.getParameter("response");
        }

        if (issueId == null || voteResponseStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "issueId and response (FREQUENTLY, SOMETIMES, NEVER) are required.")));
            return;
        }

        VoteOption voteOption = VoteOption.fromString(voteResponseStr.trim());
        if (voteOption == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_OPTION", "Response must be FREQUENTLY, SOMETIMES, or NEVER.")));
            return;
        }

        Issue issue = issueDAO.findById(issueId.trim());
        if (issue == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", "Issue not found.")));
            return;
        }

        // Verify that the voting student belongs to the class where the issue occurred
        if (currentUser.getClassId() == null || currentUser.getClassId() != issue.getClassId()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_ELIGIBLE", "You are not enrolled in the class associated with this issue.")));
            return;
        }

        // Generate irreversible token hash for privacy & duplicate prevention
        String tokenHash = TokenUtil.generateVoterTokenHash(currentUser.getStudentId(), issue.getIssueId());

        if (voteDAO.hasUserVoted(issue.getIssueId(), tokenHash)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("DUPLICATE_VOTE", "You have already cast a vote for this issue.")));
            return;
        }

        boolean saved = voteDAO.recordVote(issue.getIssueId(), voteOption, tokenHash);
        if (!saved) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VOTE_FAILED", "Unable to record vote. Duplicate vote or database error.")));
            return;
        }

        // Automatically trigger verification recalculation post-vote
        VerificationResult verificationResult = triggerService.evaluateAndTrigger(issue.getIssueId());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(verificationResult, "Your anonymous vote has been recorded and verification metrics recalculated.")));
    }
}
