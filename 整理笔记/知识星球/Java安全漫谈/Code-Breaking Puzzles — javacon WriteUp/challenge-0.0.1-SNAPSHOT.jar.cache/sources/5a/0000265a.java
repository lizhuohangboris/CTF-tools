package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.AbstractWebArgumentResolverAdapter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/ServletWebArgumentResolverAdapter.class */
public class ServletWebArgumentResolverAdapter extends AbstractWebArgumentResolverAdapter {
    public ServletWebArgumentResolverAdapter(WebArgumentResolver adaptee) {
        super(adaptee);
    }

    @Override // org.springframework.web.method.annotation.AbstractWebArgumentResolverAdapter
    protected NativeWebRequest getWebRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes instanceof ServletRequestAttributes, "No ServletRequestAttributes");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return new ServletWebRequest(servletRequestAttributes.getRequest());
    }
}