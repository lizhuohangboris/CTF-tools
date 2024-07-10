package org.springframework.web.bind;

import org.springframework.web.util.NestedServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/ServletRequestBindingException.class */
public class ServletRequestBindingException extends NestedServletException {
    public ServletRequestBindingException(String msg) {
        super(msg);
    }

    public ServletRequestBindingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}