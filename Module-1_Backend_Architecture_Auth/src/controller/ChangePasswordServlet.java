package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.UserDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.User;
import campusvoice.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ChangePasswordServlet
 * Allows users to change their password by validating their current password.
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/api/auth/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String studentId = (currentUser != null) ? currentUser.getStudentId() : null;
        String oldPassword = null;
        String newPassword = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (studentId == null && json.has("studentId")) {
                        studentId = json.get("studentId").getAsString();
                    }
                    if (json.has("oldPassword")) oldPassword = json.get("oldPassword").getAsString();
                    if (json.has("newPassword")) newPassword = json.get("newPassword").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_JSON", "Malformed JSON request body")));
                    return;
                }
            }
        } else {
            if (studentId == null) studentId = request.getParameter("studentId");
            oldPassword = request.getParameter("oldPassword");
            newPassword = request.getParameter("newPassword");
        }

        if (studentId == null || studentId.trim().isEmpty() ||
            oldPassword == null || oldPassword.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Current password and New password are required")));
            return;
        }

        if (newPassword.trim().length() < 4) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "New password must be at least 4 characters long")));
            return;
        }

        boolean changed = userDAO.changePassword(studentId.trim(), oldPassword.trim(), newPassword.trim());

        if (changed) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(null, "Password changed successfully!")));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("AUTH_FAILED", "Incorrect current password.")));
        }
    }
}
