package campusvoice.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global Request Logging Filter
 * Member 1 Responsibility (Week 3):
 * Provides centralized logging of HTTP requests, status codes, and execution duration.
 */
@WebFilter(filterName = "LoggingFilter", urlPatterns = {"/*"})
public class LoggingFilter implements Filter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String ip = httpRequest.getRemoteAddr();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = httpResponse.getStatus();
            System.out.printf("[%s] [CampusVoice-Filter] %s %s - Status: %d - IP: %s (%d ms)%n",
                    timestamp, method, uri, status, ip, duration);
        }
    }

    @Override
    public void destroy() {}
}
