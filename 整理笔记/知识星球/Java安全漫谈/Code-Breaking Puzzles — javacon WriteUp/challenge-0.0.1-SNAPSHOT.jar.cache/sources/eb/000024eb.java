package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/RelativeRedirectFilter.class */
public class RelativeRedirectFilter extends OncePerRequestFilter {
    private HttpStatus redirectStatus = HttpStatus.SEE_OTHER;

    public void setRedirectStatus(HttpStatus status) {
        Assert.notNull(status, "Property 'redirectStatus' is required");
        Assert.isTrue(status.is3xxRedirection(), "Not a redirect status code");
        this.redirectStatus = status;
    }

    public HttpStatus getRedirectStatus() {
        return this.redirectStatus;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, RelativeRedirectResponseWrapper.wrapIfNecessary(response, this.redirectStatus));
    }
}