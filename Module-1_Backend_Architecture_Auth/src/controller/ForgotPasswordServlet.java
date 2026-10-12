package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.UserDAO;
import campusvoice.model.ApiResponse;
import campusvoice.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ForgotPasswordServlet
 * Resets user password after verifying Student ID / Username and registered Email address.
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/api/auth/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String studentId = null;
        String email = null;
        String newPassword = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("studentId")) studentId = json.get("studentId").getAsString();
                    if (json.has("email")) email = json.get("email").getAsString();
                    if (json.has("newPassword")) newPassword = json.get("newPassword").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_JSON", "Malformed JSON request body")));
                    return;
                }
            }
        } else {
            studentId = request.getParameter("studentId");
            email = request.getParameter("email");
            newPassword = request.getParameter("newPassword");
        }

        if (studentId == null || studentId.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Student ID, Email, and New Password are required")));
            return;
        }

        if (newPassword.trim().length() < 4) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "New password must be at least 4 characters long")));
            return;
        }

        boolean reset = userDAO.resetPassword(studentId.trim(), email.trim(), newPassword.trim());

        if (reset) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(null, "Password reset successfully! Please log in with your new password.")));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VERIFICATION_FAILED", "Verification failed. Student ID and registered Email do not match.")));
        }
    }
}
