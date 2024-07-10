package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/JodaTimeContextHolder.class */
public final class JodaTimeContextHolder {
    private static final ThreadLocal<JodaTimeContext> jodaTimeContextHolder = new NamedThreadLocal("JodaTimeContext");

    private JodaTimeContextHolder() {
    }

    public static void resetJodaTimeContext() {
        jodaTimeContextHolder.remove();
    }

    public static void setJodaTimeContext(@Nullable JodaTimeContext jodaTimeContext) {
        if (jodaTimeContext == null) {
            resetJodaTimeContext();
        } else {
            jodaTimeContextHolder.set(jodaTimeContext);
        }
    }

    @Nullable
    public static JodaTimeContext getJodaTimeContext() {
        return jodaTimeContextHolder.get();
    }

    public static DateTimeFormatter getFormatter(DateTimeFormatter formatter, @Nullable Locale locale) {
        DateTimeFormatter formatterToUse = locale != null ? formatter.withLocale(locale) : formatter;
        JodaTimeContext context = getJodaTimeContext();
        return context != null ? context.getFormatter(formatterToUse) : formatterToUse;
    }
}