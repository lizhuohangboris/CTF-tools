package org.apache.catalina.ssi;

import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.RequestUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIServletRequestUtil.class */
public class SSIServletRequestUtil {
    public static String getRelativePath(HttpServletRequest request) {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            String result = (String) request.getAttribute("javax.servlet.include.path_info");
            if (result == null) {
                result = (String) request.getAttribute("javax.servlet.include.servlet_path");
            }
            return (result == null || result.equals("")) ? "/" : "/";
        }
        String result2 = request.getPathInfo();
        if (result2 == null) {
            result2 = request.getServletPath();
        }
        return RequestUtil.normalize((result2 == null || result2.equals("")) ? "/" : "/");
    }
}