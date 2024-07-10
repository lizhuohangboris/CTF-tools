package org.thymeleaf.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/LoggingUtils.class */
public final class LoggingUtils {
    public static String loggifyTemplateName(String template) {
        if (template == null) {
            return null;
        }
        if (template.length() <= 120) {
            return template.replace('\n', ' ');
        }
        return template.substring(0, 35).replace('\n', ' ') + "[...]" + template.substring(template.length() - 80).replace('\n', ' ');
    }

    private LoggingUtils() {
    }
}