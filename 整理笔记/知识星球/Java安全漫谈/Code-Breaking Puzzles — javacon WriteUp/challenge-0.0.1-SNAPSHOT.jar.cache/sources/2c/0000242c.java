package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/DefaultDataBinderFactory.class */
public class DefaultDataBinderFactory implements WebDataBinderFactory {
    @Nullable
    private final WebBindingInitializer initializer;

    public DefaultDataBinderFactory(@Nullable WebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    @Override // org.springframework.web.bind.support.WebDataBinderFactory
    public final WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception {
        WebDataBinder dataBinder = createBinderInstance(target, objectName, webRequest);
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder, webRequest);
        }
        initBinder(dataBinder, webRequest);
        return dataBinder;
    }

    protected WebDataBinder createBinderInstance(@Nullable Object target, String objectName, NativeWebRequest webRequest) throws Exception {
        return new WebRequestDataBinder(target, objectName);
    }

    protected void initBinder(WebDataBinder dataBinder, NativeWebRequest webRequest) throws Exception {
    }
}