package org.apache.catalina.util;

import javax.servlet.http.HttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/RequestUtil.class */
public final class RequestUtil {
    public static StringBuffer getRequestURL(HttpServletRequest request) {
        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80;
        }
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getRequestURI());
        return url;
    }
}