package org.springframework.boot.autoconfigure.web.servlet;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/DispatcherServletPath.class */
public interface DispatcherServletPath {
    String getPath();

    default String getRelativePath(String path) {
        String prefix = getPrefix();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return prefix + path;
    }

    default String getPrefix() {
        String result = getPath();
        int index = result.indexOf(42);
        if (index != -1) {
            result = result.substring(0, index);
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    default String getServletUrlMapping() {
        if (getPath().equals("") || getPath().equals("/")) {
            return "/";
        }
        if (getPath().contains("*")) {
            return getPath();
        }
        if (getPath().endsWith("/")) {
            return getPath() + "*";
        }
        return getPath() + "/*";
    }
}