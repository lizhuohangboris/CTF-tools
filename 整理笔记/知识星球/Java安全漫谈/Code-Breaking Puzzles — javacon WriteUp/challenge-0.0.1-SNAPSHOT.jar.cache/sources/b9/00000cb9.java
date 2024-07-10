package org.apache.tomcat.util.http;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/RequestUtil.class */
public class RequestUtil {
    private RequestUtil() {
    }

    public static String normalize(String path) {
        return normalize(path, true);
    }

    public static String normalize(String path, boolean replaceBackSlash) {
        if (path == null) {
            return null;
        }
        String normalized = path;
        if (replaceBackSlash && normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        boolean addedTrailingSlash = false;
        if (normalized.endsWith("/.") || normalized.endsWith("/..")) {
            normalized = normalized + "/";
            addedTrailingSlash = true;
        }
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            int index2 = normalized.indexOf("/./");
            if (index2 < 0) {
                break;
            }
            normalized = normalized.substring(0, index2) + normalized.substring(index2 + 2);
        }
        while (true) {
            int index3 = normalized.indexOf("/../");
            if (index3 >= 0) {
                if (index3 == 0) {
                    return null;
                }
                int index22 = normalized.lastIndexOf(47, index3 - 1);
                normalized = normalized.substring(0, index22) + normalized.substring(index3 + 3);
            } else {
                if (normalized.length() > 1 && addedTrailingSlash) {
                    normalized = normalized.substring(0, normalized.length() - 1);
                }
                return normalized;
            }
        }
    }
}