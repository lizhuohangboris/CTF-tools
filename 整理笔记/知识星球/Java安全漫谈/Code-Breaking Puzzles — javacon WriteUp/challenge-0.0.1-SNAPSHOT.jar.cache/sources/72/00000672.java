package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSession.class */
public interface HttpSession {
    long getCreationTime();

    String getId();

    long getLastAccessedTime();

    ServletContext getServletContext();

    void setMaxInactiveInterval(int i);

    int getMaxInactiveInterval();

    @Deprecated
    HttpSessionContext getSessionContext();

    Object getAttribute(String str);

    @Deprecated
    Object getValue(String str);

    Enumeration<String> getAttributeNames();

    @Deprecated
    String[] getValueNames();

    void setAttribute(String str, Object obj);

    @Deprecated
    void putValue(String str, Object obj);

    void removeAttribute(String str);

    @Deprecated
    void removeValue(String str);

    void invalidate();

    boolean isNew();
}