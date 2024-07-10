package org.springframework.web.multipart.support;

import javax.servlet.ServletException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/MissingServletRequestPartException.class */
public class MissingServletRequestPartException extends ServletException {
    private final String partName;

    public MissingServletRequestPartException(String partName) {
        super("Required request part '" + partName + "' is not present");
        this.partName = partName;
    }

    public String getRequestPartName() {
        return this.partName;
    }
}