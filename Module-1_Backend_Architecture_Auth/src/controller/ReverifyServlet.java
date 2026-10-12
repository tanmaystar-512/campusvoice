package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.IssueDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.Issue;
import campusvoice.model.User;
import campusvoice.service.ReverificationEngine;
import campusvoice.util.JsonUtil;
import campusvoice.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * ReverifyServlet - Post-Resolution Student Re-Verification Loop
 * Member 1 & 2 Responsibility (Week 11):
 * Students vote on whether an issue marked as 'RESOLVED' is truly resolved:
 * Responses: 'RESOLVED', 'PARTIALLY_RESOLVED', 'STILL_FACING'.
 * If majority report 'STILL_FACING', status automatically resets to 'REOPENED'.
 */
@WebServlet(name = "ReverifyServlet", urlPatterns = {"/api/issues/reverify"})
public class ReverifyServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final ReverificationEngine reverificationEngine = new ReverificationEngine();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Login required to participate in re-verification.")));
            return;
        }

        String issueId = null;
        String reverifyOption = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("issueId")) issueId = json.get("issueId").getAsString();
                    if (json.has("response")) reverifyOption = json.get("response").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("MALFORMED_JSON", "Invalid JSON payload.")));
                    return;
                }
            }
        } else {
            issueId = request.getParameter("issueId");
            reverifyOption = request.getParameter("response");
        }

        if (issueId == null || reverifyOption == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "issueId and response (RESOLVED, PARTIALLY_RESOLVED, STILL_FACING) are required.")));
            return;
        }

        Issue issue = issueDAO.findById(issueId.trim());
        if (issue == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", "Issue not found.")));
            return;
        }

        String tokenHash = TokenUtil.generateVoterTokenHash(currentUser.getStudentId(), issue.getIssueId());

        if (voteDAO.hasUserReverified(issue.getIssueId(), tokenHash)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("ALREADY_SUBMITTED", "You have already submitted re-verification feedback for this issue.")));
            return;
        }

        boolean recorded = voteDAO.recordReverification(issue.getIssueId(), reverifyOption.trim().toUpperCase(), tokenHash);
        if (!recorded) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("RECORD_FAILED", "Failed to record feedback.")));
            return;
        }

        // Evaluate if majority still facing problem
        boolean reopened = reverificationEngine.evaluateAndProcess(issue.getIssueId());
        Map<String, Integer> feedbackCounts = voteDAO.getReverificationCounts(issue.getIssueId());

        String message = reopened ?
                "Feedback recorded! Because the majority still face the issue, it has been reopened." :
                "Thank you! Your resolution verification feedback has been recorded.";

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(feedbackCounts, message)));
    }
}
