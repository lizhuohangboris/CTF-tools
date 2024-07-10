package org.springframework.boot.web.servlet.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/filter/ApplicationContextHeaderFilter.class */
public class ApplicationContextHeaderFilter extends OncePerRequestFilter {
    public static final String HEADER_NAME = "X-Application-Context";
    private final ApplicationContext applicationContext;

    public ApplicationContextHeaderFilter(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HEADER_NAME, this.applicationContext.getId());
        filterChain.doFilter(request, response);
    }
}