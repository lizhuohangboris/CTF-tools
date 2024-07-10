package org.springframework.web.multipart.support;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/MultipartFilter.class */
public class MultipartFilter extends OncePerRequestFilter {
    public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";
    private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();
    private String multipartResolverBeanName = DEFAULT_MULTIPART_RESOLVER_BEAN_NAME;

    public void setMultipartResolverBeanName(String multipartResolverBeanName) {
        this.multipartResolverBeanName = multipartResolverBeanName;
    }

    protected String getMultipartResolverBeanName() {
        return this.multipartResolverBeanName;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MultipartResolver multipartResolver = lookupMultipartResolver(request);
        HttpServletRequest processedRequest = request;
        if (multipartResolver.isMultipart(processedRequest)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Resolving multipart request");
            }
            processedRequest = multipartResolver.resolveMultipart(processedRequest);
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace("Not a multipart request");
        }
        try {
            filterChain.doFilter(processedRequest, response);
            if (processedRequest instanceof MultipartHttpServletRequest) {
                multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
            }
        } catch (Throwable th) {
            if (processedRequest instanceof MultipartHttpServletRequest) {
                multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
            }
            throw th;
        }
    }

    protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
        return lookupMultipartResolver();
    }

    protected MultipartResolver lookupMultipartResolver() {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        String beanName = getMultipartResolverBeanName();
        if (wac != null && wac.containsBean(beanName)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
            }
            return (MultipartResolver) wac.getBean(beanName, MultipartResolver.class);
        }
        return this.defaultMultipartResolver;
    }
}