package org.apache.tomcat.util.descriptor.web;

import java.util.EnumSet;
import javax.servlet.SessionTrackingMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/SessionConfig.class */
public class SessionConfig {
    private Integer sessionTimeout;
    private String cookieName;
    private String cookieDomain;
    private String cookiePath;
    private String cookieComment;
    private Boolean cookieHttpOnly;
    private Boolean cookieSecure;
    private Integer cookieMaxAge;
    private final EnumSet<SessionTrackingMode> sessionTrackingModes = EnumSet.noneOf(SessionTrackingMode.class);

    public Integer getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(String sessionTimeout) {
        this.sessionTimeout = Integer.valueOf(sessionTimeout);
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookiePath() {
        return this.cookiePath;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookieComment() {
        return this.cookieComment;
    }

    public void setCookieComment(String cookieComment) {
        this.cookieComment = cookieComment;
    }

    public Boolean getCookieHttpOnly() {
        return this.cookieHttpOnly;
    }

    public void setCookieHttpOnly(String cookieHttpOnly) {
        this.cookieHttpOnly = Boolean.valueOf(cookieHttpOnly);
    }

    public Boolean getCookieSecure() {
        return this.cookieSecure;
    }

    public void setCookieSecure(String cookieSecure) {
        this.cookieSecure = Boolean.valueOf(cookieSecure);
    }

    public Integer getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    public void setCookieMaxAge(String cookieMaxAge) {
        this.cookieMaxAge = Integer.valueOf(cookieMaxAge);
    }

    public EnumSet<SessionTrackingMode> getSessionTrackingModes() {
        return this.sessionTrackingModes;
    }

    public void addSessionTrackingMode(String sessionTrackingMode) {
        this.sessionTrackingModes.add(SessionTrackingMode.valueOf(sessionTrackingMode));
    }
}