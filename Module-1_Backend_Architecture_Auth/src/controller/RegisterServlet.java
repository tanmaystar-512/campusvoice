package campusvoice.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import campusvoice.dao.UserDAO;
import campusvoice.model.ApiResponse;
import campusvoice.model.User;
import campusvoice.util.JsonUtil;
import campusvoice.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RegisterServlet - Custom User Registration
 * Allows students/administrators to register with custom User ID, Name, Email, Class, and Password.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/api/auth/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String studentId = null;
        String password = null;
        String fullName = null;
        String email = null;
        String role = "STUDENT";
        Integer classId = 1;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("studentId")) studentId = json.get("studentId").getAsString();
                    if (json.has("password")) password = json.get("password").getAsString();
                    if (json.has("fullName")) fullName = json.get("fullName").getAsString();
                    if (json.has("email")) email = json.get("email").getAsString();
                    if (json.has("role")) role = json.get("role").getAsString();
                    if (json.has("classId")) classId = json.get("classId").getAsInt();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_JSON", "Malformed JSON request body")));
                    return;
                }
            }
        } else {
            studentId = request.getParameter("studentId");
            password = request.getParameter("password");
            fullName = request.getParameter("fullName");
            email = request.getParameter("email");
            if (request.getParameter("role") != null) role = request.getParameter("role");
            if (request.getParameter("classId") != null) {
                try { classId = Integer.parseInt(request.getParameter("classId")); } catch (Exception ignored) {}
            }
        }

        if (studentId == null || studentId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "User ID / Student ID and password are required")));
            return;
        }

        if (password.trim().length() < 4) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Password must be at least 4 characters long")));
            return;
        }

        if (userDAO.isStudentIdTaken(studentId.trim())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("USER_EXISTS", "User ID / Student ID already registered")));
            return;
        }

        if (email != null && !email.trim().isEmpty() && userDAO.isEmailTaken(email.trim())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("EMAIL_EXISTS", "Email address already registered")));
            return;
        }

        User newUser = new User();
        newUser.setStudentId(studentId.trim());
        newUser.setPasswordHash(PasswordUtil.hashPassword(password.trim()));
        newUser.setFullName(fullName != null ? fullName.trim() : studentId.trim());
        newUser.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : studentId.trim().toLowerCase() + "@campus.edu");
        newUser.setRole(role != null ? role.toUpperCase() : "STUDENT");
        newUser.setClassId(classId);

        boolean registered = userDAO.createUser(newUser);

        if (registered) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(null, "Account created successfully! You can now log in.")));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("DB_ERROR", "Failed to register account. Please try again.")));
        }
    }
}
