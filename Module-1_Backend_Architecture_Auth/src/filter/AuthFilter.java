package campusvoice.filter;

import campusvoice.model.ApiResponse;
import campusvoice.model.User;
import campusvoice.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Role-Based Authentication & Authorization Filter
 * Member 1 & 2 Responsibility (Week 5):
 * 1. Checks HTTP Session for authenticated user credentials.
 * 2. Allows public endpoints (Login, Public Issue Tracking by ID, Static Resources).
 * 3. Enforces Student login on submission & voting routes.
 * 4. Prevents unauthorized route tampering on Admin routes (/api/admin/*).
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/api/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // Allow CORS preflight OPTIONS requests unconditionally
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Publicly accessible endpoints without login
        if (isPublicEndpoint(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHENTICATED", "Session expired or authentication required. Please log in.");
            return;
        }

        // Role-based protection: Admin endpoints require ADMIN role
        if (relativePath.startsWith("/api/admin")) {
            if (!currentUser.isAdmin()) {
                sendJsonError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Access denied: Administrator privileges required.");
                return;
            }
        }

        // If authenticated and authorized, proceed
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/auth/login") ||
               path.equals("/api/auth/register") ||
               path.equals("/api/auth/forgot-password") ||
               path.equals("/api/issues/track") ||
               path.startsWith("/api/public") ||
               path.equals("/api/health");
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String errorCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> apiResp = ApiResponse.error(errorCode, message);
        response.getWriter().write(JsonUtil.toJson(apiResp));
        response.getWriter().flush();
    }

    @Override
    public void destroy() {}
}
