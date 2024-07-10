package ch.qos.logback.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/util/CachingDateFormatter.class */
public class CachingDateFormatter {
    long lastTimestamp = -1;
    String cachedStr = null;
    final SimpleDateFormat sdf;

    public CachingDateFormatter(String pattern) {
        this.sdf = new SimpleDateFormat(pattern);
    }

    public final String format(long now) {
        String str;
        synchronized (this) {
            if (now != this.lastTimestamp) {
                this.lastTimestamp = now;
                this.cachedStr = this.sdf.format(new Date(now));
            }
            str = this.cachedStr;
        }
        return str;
    }

    public void setTimeZone(TimeZone tz) {
        this.sdf.setTimeZone(tz);
    }
}