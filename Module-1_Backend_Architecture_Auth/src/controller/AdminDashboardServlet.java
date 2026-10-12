package campusvoice.controller;

import campusvoice.dao.IssueDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.Issue;
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
 * AdminDashboardServlet - Management Overview & Issue Filtering
 * Member 1 Responsibility (Week 10):
 * Delivers aggregated statistical metrics for KPI cards and filterable issue listings.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/api/admin/dashboard", "/api/admin/issues"})
public class AdminDashboardServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String path = request.getServletPath();

        if (path.endsWith("/dashboard")) {
            handleDashboardMetrics(request, response);
        } else {
            handleFilteredIssues(request, response);
        }
    }

    private void handleDashboardMetrics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Integer> counts = issueDAO.getDashboardCounts();

        Map<String, Object> kpiData = new HashMap<>();
        kpiData.put("totalIssues", counts.getOrDefault("TOTAL", 0));
        kpiData.put("underVerification", counts.getOrDefault("UNDER_VERIFICATION", 0) + counts.getOrDefault("REPORTED", 0));
        kpiData.put("classVerified", counts.getOrDefault("CLASS_VERIFIED", 0));
        kpiData.put("underInvestigation", counts.getOrDefault("UNDER_INVESTIGATION", 0));
        kpiData.put("resolved", counts.getOrDefault("RESOLVED", 0));
        kpiData.put("reopened", counts.getOrDefault("REOPENED", 0));

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(kpiData, "Admin dashboard metrics calculated successfully.")));
    }

    private void handleFilteredIssues(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("status");
        String category = request.getParameter("category");
        String department = request.getParameter("department");
        String classIdParam = request.getParameter("classId");

        Integer classId = null;
        if (classIdParam != null && !classIdParam.trim().isEmpty()) {
            try {
                classId = Integer.parseInt(classIdParam.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<Issue> issues = issueDAO.findAll(status, category, department, classId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtil.toJson(ApiResponse.success(issues, "Found " + issues.size() + " matching grievances.")));
    }
}
