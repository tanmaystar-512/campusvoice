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
import java.util.HashMap;
import java.util.Map;

/**
 * LoginServlet - Authentication & Session Management
 * Member 1 Responsibility (Week 5):
 * Authenticates student / administrator credentials and establishes a secure HTTP session.
 * Decouples user identity from subsequent anonymous reporting activities.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/api/auth/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String studentId = null;
        String password = null;

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            String body = JsonUtil.readRequestBody(request.getReader());
            if (body != null && !body.trim().isEmpty()) {
                try {
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    if (json.has("studentId")) studentId = json.get("studentId").getAsString();
                    if (json.has("password")) password = json.get("password").getAsString();
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_JSON", "Malformed JSON request body")));
                    return;
                }
            }
        } else {
            studentId = request.getParameter("studentId");
            password = request.getParameter("password");
        }

        if (studentId == null || studentId.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", "Student ID / Username and password are required")));
            return;
        }

        User user = userDAO.authenticate(studentId, password);

        if (user != null) {
            // Invalidate any existing session and issue a new secure session
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout
            session.setAttribute("currentUser", user);
            session.setAttribute("studentId", user.getStudentId());
            session.setAttribute("role", user.getRole());
            session.setAttribute("classId", user.getClassId());

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("userId", user.getUserId());
            sessionData.put("studentId", user.getStudentId());
            sessionData.put("fullName", user.getFullName());
            sessionData.put("email", user.getEmail());
            sessionData.put("role", user.getRole());
            sessionData.put("classId", user.getClassId());
            sessionData.put("sessionId", session.getId());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.success(sessionData, "Authentication successful")));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JsonUtil.toJson(ApiResponse.error("INVALID_CREDENTIALS", "Invalid credentials. Please verify your Student ID and password.")));
        }
    }
}
