package org.springframework.context;

import org.springframework.beans.FatalBeanException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/ApplicationContextException.class */
public class ApplicationContextException extends FatalBeanException {
    public ApplicationContextException(String msg) {
        super(msg);
    }

    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }
}