package org.springframework.core.log;

import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/log/LogFormatUtils.class */
public abstract class LogFormatUtils {
    public static String formatValue(@Nullable Object value, boolean limitLength) {
        String str;
        if (value == null) {
            return "";
        }
        if (value instanceof CharSequence) {
            str = "\"" + value + "\"";
        } else {
            try {
                str = value.toString();
            } catch (Throwable ex) {
                str = ex.toString();
            }
        }
        return (!limitLength || str.length() <= 100) ? str : str.substring(0, 100) + " (truncated)...";
    }

    public static void traceDebug(Log logger, Function<Boolean, String> messageFactory) {
        if (logger.isDebugEnabled()) {
            String logMessage = messageFactory.apply(Boolean.valueOf(logger.isTraceEnabled()));
            if (logger.isTraceEnabled()) {
                logger.trace(logMessage);
            } else {
                logger.debug(logMessage);
            }
        }
    }
}