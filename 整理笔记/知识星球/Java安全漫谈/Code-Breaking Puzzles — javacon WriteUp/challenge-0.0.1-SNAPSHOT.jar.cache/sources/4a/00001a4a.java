package org.springframework.boot.security.servlet;

import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/security/servlet/ApplicationContextRequestMatcher.class */
public abstract class ApplicationContextRequestMatcher<C> implements RequestMatcher {
    private final Class<? extends C> contextClass;
    private volatile Supplier<C> context;
    private final Object contextLock = new Object();

    protected abstract boolean matches(HttpServletRequest request, Supplier<C> context);

    public ApplicationContextRequestMatcher(Class<? extends C> contextClass) {
        Assert.notNull(contextClass, "Context class must not be null");
        this.contextClass = contextClass;
    }

    public final boolean matches(HttpServletRequest request) {
        return matches(request, getContext(request));
    }

    private Supplier<C> getContext(HttpServletRequest request) {
        if (this.context == null) {
            synchronized (this.contextLock) {
                if (this.context == null) {
                    Supplier<C> createdContext = createContext(request);
                    initialized(createdContext);
                    this.context = createdContext;
                }
            }
        }
        return this.context;
    }

    protected void initialized(Supplier<C> context) {
    }

    private Supplier<C> createContext(HttpServletRequest request) {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        if (this.contextClass.isInstance(context)) {
            return () -> {
                return context;
            };
        }
        return () -> {
            return context.getBean(this.contextClass);
        };
    }
}