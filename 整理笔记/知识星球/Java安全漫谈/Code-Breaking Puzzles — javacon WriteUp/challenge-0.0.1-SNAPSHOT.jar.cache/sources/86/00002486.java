package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/RequestAttributes.class */
public interface RequestAttributes {
    public static final int SCOPE_REQUEST = 0;
    public static final int SCOPE_SESSION = 1;
    public static final String REFERENCE_REQUEST = "request";
    public static final String REFERENCE_SESSION = "session";

    @Nullable
    Object getAttribute(String str, int i);

    void setAttribute(String str, Object obj, int i);

    void removeAttribute(String str, int i);

    String[] getAttributeNames(int i);

    void registerDestructionCallback(String str, Runnable runnable, int i);

    @Nullable
    Object resolveReference(String str);

    String getSessionId();

    Object getSessionMutex();
}