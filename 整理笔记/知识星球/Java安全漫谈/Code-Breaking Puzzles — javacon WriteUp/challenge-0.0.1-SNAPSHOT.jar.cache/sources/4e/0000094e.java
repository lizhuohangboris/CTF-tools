package org.apache.catalina.util;

import javax.servlet.SessionCookieConfig;
import org.apache.catalina.Context;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/SessionConfig.class */
public class SessionConfig {
    private static final String DEFAULT_SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String DEFAULT_SESSION_PARAMETER_NAME = "jsessionid";

    public static String getSessionCookieName(Context context) {
        String result = getConfiguredSessionCookieName(context);
        if (result == null) {
            result = DEFAULT_SESSION_COOKIE_NAME;
        }
        return result;
    }

    public static String getSessionUriParamName(Context context) {
        String result = getConfiguredSessionCookieName(context);
        if (result == null) {
            result = DEFAULT_SESSION_PARAMETER_NAME;
        }
        return result;
    }

    private static String getConfiguredSessionCookieName(Context context) {
        if (context != null) {
            String cookieName = context.getSessionCookieName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
            SessionCookieConfig scc = context.getServletContext().getSessionCookieConfig();
            String cookieName2 = scc.getName();
            if (cookieName2 != null && cookieName2.length() > 0) {
                return cookieName2;
            }
            return null;
        }
        return null;
    }

    private SessionConfig() {
    }
}