package javax.servlet.http;

import java.util.Enumeration;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSessionContext.class */
public interface HttpSessionContext {
    @Deprecated
    HttpSession getSession(String str);

    @Deprecated
    Enumeration<String> getIds();
}