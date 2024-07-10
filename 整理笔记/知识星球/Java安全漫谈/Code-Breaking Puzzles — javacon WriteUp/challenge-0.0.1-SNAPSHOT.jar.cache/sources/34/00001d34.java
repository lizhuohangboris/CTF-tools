package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/i18n/SimpleTimeZoneAwareLocaleContext.class */
public class SimpleTimeZoneAwareLocaleContext extends SimpleLocaleContext implements TimeZoneAwareLocaleContext {
    @Nullable
    private final TimeZone timeZone;

    public SimpleTimeZoneAwareLocaleContext(@Nullable Locale locale, @Nullable TimeZone timeZone) {
        super(locale);
        this.timeZone = timeZone;
    }

    @Override // org.springframework.context.i18n.TimeZoneAwareLocaleContext
    @Nullable
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override // org.springframework.context.i18n.SimpleLocaleContext
    public String toString() {
        return super.toString() + " " + (this.timeZone != null ? this.timeZone.toString() : "-");
    }
}