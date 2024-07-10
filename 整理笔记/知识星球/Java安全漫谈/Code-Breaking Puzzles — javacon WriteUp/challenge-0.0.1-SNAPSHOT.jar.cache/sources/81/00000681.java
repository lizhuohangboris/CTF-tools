package javax.servlet.http;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/PushBuilder.class */
public interface PushBuilder {
    PushBuilder method(String str);

    PushBuilder queryString(String str);

    PushBuilder sessionId(String str);

    PushBuilder setHeader(String str, String str2);

    PushBuilder addHeader(String str, String str2);

    PushBuilder removeHeader(String str);

    PushBuilder path(String str);

    void push();

    String getMethod();

    String getQueryString();

    String getSessionId();

    Set<String> getHeaderNames();

    String getHeader(String str);

    String getPath();
}