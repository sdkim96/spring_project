package web.web1.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CustomRequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CustomRequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("Request URL: {}, Method: {}", request.getRequestURL(), request.getMethod());
        
        // 요청 헤더 로깅 (예시)
        log.info("Authorization Header: {}", request.getHeader("Authorization"));

        filterChain.doFilter(servletRequest, servletResponse);
    }

    // init 및 destroy 메소드는 필터 인터페이스를 구현하기 위해 필요하지만, 여기서는 구현할 내용이 없습니다.
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
