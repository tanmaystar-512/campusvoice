package campusvoice.controller;

import campusvoice.dao.ClassDAO;
import campusvoice.dao.IssueDAO;
import campusvoice.dao.StatusHistoryDAO;
import campusvoice.dao.VoteDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.ClassModel;
import campusvoice.model.Issue;
import campusvoice.model.StatusHistory;
import campusvoice.model.VerificationResult;
import campusvoice.service.VerificationEngine;
import campusvoice.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TrackIssueServlet - Public Issue Tracking Portal
 * Member 1 & 2 Responsibility (Week 9):
 * 
 * CORE REQUIREMENT:
 * Public status lookup using unique Issue ID (e.g. CV-1847) without requiring login.
 * Returns progress timeline, current verification scores, and resolution remarks.
 */
@WebServlet(name = "TrackIssueServlet", urlPatterns = {"/api/issues/track"})
public class TrackIssueServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();
    private final ClassDAO classDAO = new ClassDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final StatusHistoryDAO historyDAO = new StatusHistoryDAO();
    private final VerificationEngine verificationEngine = new VerificationEngine();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String issueId = request.getParameter("id");
        if (issueId == null || issueId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("MISSING_ID", "Issue ID parameter 'id' is required (e.g. ?id=CV-1847).")));
            return;
        }

        Issue issue = issueDAO.findById(issueId.trim().toUpperCase());
        if (issue == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", "No grievance found with Issue ID: " + issueId)));
            return;
        }

        // Fetch verification metrics
        ClassModel cm = classDAO.findById(issue.getClassId());
        int strength = (cm != null && cm.getTotalStrength() > 0) ? cm.getTotalStrength() : 60;
        Map<String, Integer> voteCounts = voteDAO.getVoteCounts(issue.getIssueId());
        VerificationResult verificationResult = verificationEngine.evaluate(issue.getIssueId(), strength, voteCounts, issue.getStatus());

        // Fetch lifecycle audit trail
        List<StatusHistory> history = historyDAO.findByIssueId(issue.getIssueId());

        // Map status to milestone step index (1 to 5)
        int milestoneStep = mapStatusToMilestone(issue.getStatus());

        Map<String, Object> trackingData = new HashMap<>();
        trackingData.put("issue", issue);
        trackingData.put("verification", verificationResult);
        trackingData.put("history", history);
        trackingData.put("milestoneStep", milestoneStep);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(trackingData, "Issue tracking details retrieved successfully.")));
    }

    private int mapStatusToMilestone(String status) {
        if (status == null) return 1;
        switch (status.toUpperCase()) {
            case "REPORTED":
                return 1;
            case "UNDER_VERIFICATION":
                return 2;
            case "CLASS_VERIFIED":
                return 3;
            case "UNDER_INVESTIGATION":
                return 4;
            case "RESOLVED":
                return 5;
            case "REOPENED":
                return 2; // Returns to verification / investigation cycle
            default:
                return 1;
        }
    }
}
