package org.springframework.web.util;

import javax.servlet.ServletException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/NestedServletException.class */
public class NestedServletException extends ServletException {
    private static final long serialVersionUID = -5292377985529381145L;

    static {
        NestedExceptionUtils.class.getName();
    }

    public NestedServletException(String msg) {
        super(msg);
    }

    public NestedServletException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Override // java.lang.Throwable
    @Nullable
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }
}