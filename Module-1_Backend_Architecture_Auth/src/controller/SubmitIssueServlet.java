package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.IssueDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.Issue;
import campusvoice.model.ProblemMetadata;
import campusvoice.model.User;
import campusvoice.util.IssueIdGenerator;
import campusvoice.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SubmitIssueServlet - Anonymous Grievance Submission
 * Member 1 Responsibility (Week 6):
 * 
 * CORE REQUIREMENT:
 * Decouples student identity from issue persistence.
 * Automatically generates problem-specific values (Category Prefix, SLA, Assigned Department, Severity).
 */
@WebServlet(name = "SubmitIssueServlet", urlPatterns = {"/api/issues"})
public class SubmitIssueServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Active session required to submit an issue.")));
            return;
        }

        Integer classId = currentUser.getClassId();
        if (classId == null || classId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_CLASS", "User must be registered with an eligible class section.")));
            return;
        }

        String category = null;
        String title = null;
        String description = null;
        String priority = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("category")) category = json.get("category").getAsString();
                    if (json.has("title")) title = json.get("title").getAsString();
                    if (json.has("description")) description = json.get("description").getAsString();
                    if (json.has("priority")) priority = json.get("priority").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("MALFORMED_JSON", "Invalid request body.")));
                    return;
                }
            }
        } else {
            category = request.getParameter("category");
            title = request.getParameter("title");
            description = request.getParameter("description");
            priority = request.getParameter("priority");
        }

        // Field validations
        if (title == null || title.trim().length() < 3) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Issue title is required and must have at least 3 characters.")));
            return;
        }

        if (description == null || description.trim().length() < 5) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Issue description is required and must have at least 5 characters.")));
            return;
        }

        if (category == null || category.trim().isEmpty()) {
            category = "General";
        }

        // Problem-Specific Value Generation
        ProblemMetadata meta = ProblemMetadata.forCategory(category);
        if (priority == null || priority.trim().isEmpty()) {
            priority = meta.getSuggestedPriority();
        }

        // Generate problem-specific unique Issue ID (e.g. CV-LAB-4821, CV-CLS-1847)
        String issueId = generateUniqueIssueId(category);

        // Create issue model strictly WITHOUT student identity
        Issue newIssue = new Issue();
        newIssue.setIssueId(issueId);
        newIssue.setClassId(classId);
        newIssue.setCategory(meta.getCategory());
        newIssue.setTitle(title.trim());
        newIssue.setDescription(description.trim());
        newIssue.setPriority(priority.toUpperCase());
        newIssue.setStatus("UNDER_VERIFICATION");
        newIssue.setDepartmentAssigned(meta.getTargetDepartment());

        boolean created = issueDAO.createIssue(newIssue);

        if (created) {
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("issueId", issueId);
            resultData.put("status", "UNDER_VERIFICATION");
            resultData.put("category", meta.getCategory());
            resultData.put("title", title);
            resultData.put("classId", classId);
            resultData.put("assignedDepartment", meta.getTargetDepartment());
            resultData.put("slaHours", meta.getSlaHours());
            resultData.put("severityScore", meta.getBaseSeverity());

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(resultData, "Issue submitted anonymously! Save your Issue ID: " + issueId + " to track progress.")));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("DB_ERROR", "Failed to save grievance. Please try again.")));
        }
    }

    private String generateUniqueIssueId(String category) {
        for (int attempts = 0; attempts < 10; attempts++) {
            String generated = IssueIdGenerator.generateId(category);
            if (issueDAO.findById(generated) == null) {
                return generated;
            }
        }
        return "CV-" + (System.currentTimeMillis() % 10000);
    }
}
