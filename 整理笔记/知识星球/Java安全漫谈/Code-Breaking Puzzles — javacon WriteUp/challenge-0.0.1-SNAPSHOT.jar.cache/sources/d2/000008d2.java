package org.apache.catalina.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/* compiled from: StandardSession.java */
@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardSessionContext.class */
final class StandardSessionContext implements HttpSessionContext {
    private static final List<String> emptyString = Collections.emptyList();

    @Override // javax.servlet.http.HttpSessionContext
    @Deprecated
    public Enumeration<String> getIds() {
        return Collections.enumeration(emptyString);
    }

    @Override // javax.servlet.http.HttpSessionContext
    @Deprecated
    public HttpSession getSession(String id) {
        return null;
    }
}