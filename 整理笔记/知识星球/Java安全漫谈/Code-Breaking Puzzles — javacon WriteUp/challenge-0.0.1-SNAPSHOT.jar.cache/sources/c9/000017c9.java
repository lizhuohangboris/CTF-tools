package org.springframework.boot.autoconfigure.security.servlet;

import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.security.servlet.ApplicationContextRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/PathRequest.class */
public final class PathRequest {
    private PathRequest() {
    }

    public static StaticResourceRequest toStaticResources() {
        return StaticResourceRequest.INSTANCE;
    }

    public static H2ConsoleRequestMatcher toH2Console() {
        return new H2ConsoleRequestMatcher();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/servlet/PathRequest$H2ConsoleRequestMatcher.class */
    public static final class H2ConsoleRequestMatcher extends ApplicationContextRequestMatcher<H2ConsoleProperties> {
        private volatile RequestMatcher delegate;

        private H2ConsoleRequestMatcher() {
            super(H2ConsoleProperties.class);
        }

        @Override // org.springframework.boot.security.servlet.ApplicationContextRequestMatcher
        protected void initialized(Supplier<H2ConsoleProperties> h2ConsoleProperties) {
            this.delegate = new AntPathRequestMatcher(h2ConsoleProperties.get().getPath() + "/**");
        }

        @Override // org.springframework.boot.security.servlet.ApplicationContextRequestMatcher
        protected boolean matches(HttpServletRequest request, Supplier<H2ConsoleProperties> context) {
            return this.delegate.matches(request);
        }
    }
}