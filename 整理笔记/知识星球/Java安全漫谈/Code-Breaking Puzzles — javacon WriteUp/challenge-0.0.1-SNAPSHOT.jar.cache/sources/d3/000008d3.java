package org.apache.catalina.session;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/StandardSessionFacade.class */
public class StandardSessionFacade implements HttpSession {
    private final HttpSession session;

    public StandardSessionFacade(HttpSession session) {
        this.session = session;
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public long getCreationTime() {
        return this.session.getCreationTime();
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public String getId() {
        return this.session.getId();
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public long getLastAccessedTime() {
        return this.session.getLastAccessedTime();
    }

    @Override // javax.servlet.http.HttpSession
    public ServletContext getServletContext() {
        return this.session.getServletContext();
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public void setMaxInactiveInterval(int interval) {
        this.session.setMaxInactiveInterval(interval);
    }

    @Override // javax.servlet.http.HttpSession, org.apache.catalina.Session
    public int getMaxInactiveInterval() {
        return this.session.getMaxInactiveInterval();
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return this.session.getSessionContext();
    }

    @Override // javax.servlet.http.HttpSession
    public Object getAttribute(String name) {
        return this.session.getAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public Object getValue(String name) {
        return this.session.getAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    public Enumeration<String> getAttributeNames() {
        return this.session.getAttributeNames();
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public String[] getValueNames() {
        return this.session.getValueNames();
    }

    @Override // javax.servlet.http.HttpSession
    public void setAttribute(String name, Object value) {
        this.session.setAttribute(name, value);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public void putValue(String name, Object value) {
        this.session.setAttribute(name, value);
    }

    @Override // javax.servlet.http.HttpSession
    public void removeAttribute(String name) {
        this.session.removeAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    @Deprecated
    public void removeValue(String name) {
        this.session.removeAttribute(name);
    }

    @Override // javax.servlet.http.HttpSession
    public void invalidate() {
        this.session.invalidate();
    }

    @Override // javax.servlet.http.HttpSession
    public boolean isNew() {
        return this.session.isNew();
    }
}