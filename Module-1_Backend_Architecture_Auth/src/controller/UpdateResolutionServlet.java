package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.IssueDAO;
import campusvoice.model.ApiResponse;
import campusvoice.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UpdateResolutionServlet - Management Department Assignment & Status Updates
 * Member 1 & 5 Responsibility (Weeks 10 & 11):
 * Allows administrators to assign departments, update grievance statuses,
 * and record official resolution remarks.
 */
@WebServlet(name = "UpdateResolutionServlet", urlPatterns = {"/api/admin/issues/update"})
public class UpdateResolutionServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processUpdate(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processUpdate(request, response);
    }

    private void processUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        String issueId = null;
        String department = null;
        String status = null;
        String remarks = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("issueId")) issueId = json.get("issueId").getAsString();
                    if (json.has("department")) department = json.get("department").getAsString();
                    if (json.has("status")) status = json.get("status").getAsString();
                    if (json.has("remarks")) remarks = json.get("remarks").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("MALFORMED_JSON", "Invalid JSON body.")));
                    return;
                }
            }
        } else {
            issueId = request.getParameter("issueId");
            department = request.getParameter("department");
            status = request.getParameter("status");
            remarks = request.getParameter("remarks");
        }

        if (issueId == null || status == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "issueId and status are required.")));
            return;
        }

        boolean updated = issueDAO.updateResolution(issueId.trim(), department, status.trim(), remarks);

        if (updated) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(null, "Issue " + issueId + " resolution status updated to " + status)));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("UPDATE_FAILED", "Failed to update issue status. Check issue ID.")));
        }
    }
}
