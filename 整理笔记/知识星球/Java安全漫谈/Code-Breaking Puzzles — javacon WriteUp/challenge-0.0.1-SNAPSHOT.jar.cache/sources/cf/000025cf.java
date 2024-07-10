package org.springframework.web.servlet.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/ConversionServiceExposingInterceptor.class */
public class ConversionServiceExposingInterceptor extends HandlerInterceptorAdapter {
    private final ConversionService conversionService;

    public ConversionServiceExposingInterceptor(ConversionService conversionService) {
        Assert.notNull(conversionService, "The ConversionService may not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.web.servlet.handler.HandlerInterceptorAdapter, org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        request.setAttribute(ConversionService.class.getName(), this.conversionService);
        return true;
    }
}