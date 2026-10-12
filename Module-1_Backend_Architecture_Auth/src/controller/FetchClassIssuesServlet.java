package campusvoice.controller;

import campusvoice.dao.ClassDAO;
import campusvoice.dao.IssueDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.ClassModel;
import campusvoice.model.Issue;
import campusvoice.model.User;
import campusvoice.model.VerificationResult;
import campusvoice.service.VerificationEngine;
import campusvoice.util.JsonUtil;
import campusvoice.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * FetchClassIssuesServlet - Peer Verification Feed
 * Member 1 Responsibility (Week 7):
 * Returns active verification polls restricted to the authenticated student's registered class.
 * Includes user voting status without exposing individual voting choices.
 */
@WebServlet(name = "FetchClassIssuesServlet", urlPatterns = {"/api/issues/class"})
public class FetchClassIssuesServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();
    private final ClassDAO classDAO = new ClassDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final VerificationEngine verificationEngine = new VerificationEngine();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Active student session required.")));
            return;
        }

        Integer classId = currentUser.getClassId();
        if (classId == null || classId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NO_CLASS", "User is not registered to an active class.")));
            return;
        }

        List<Issue> issues = issueDAO.findActiveByClassId(classId);
        ClassModel cm = classDAO.findById(classId);
        int classStrength = (cm != null && cm.getTotalStrength() > 0) ? cm.getTotalStrength() : 60;

        for (Issue issue : issues) {
            // Check if current student has already cast a vote on this issue
            String tokenHash = TokenUtil.generateVoterTokenHash(currentUser.getStudentId(), issue.getIssueId());
            boolean hasVoted = voteDAO.hasUserVoted(issue.getIssueId(), tokenHash);
            issue.setUserHasVoted(hasVoted);

            // Compute current aggregate metrics
            Map<String, Integer> counts = voteDAO.getVoteCounts(issue.getIssueId());
            VerificationResult vr = verificationEngine.evaluate(
                    issue.getIssueId(),
                    classStrength,
                    counts,
                    issue.getStatus()
            );
            issue.setVerificationResult(vr);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(issues, "Fetched " + issues.size() + " active class issues for peer voting.")));
    }
}
