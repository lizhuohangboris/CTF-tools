package org.apache.tomcat.util.http;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/CookieProcessorBase.class */
public abstract class CookieProcessorBase implements CookieProcessor {
    private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
    protected static final ThreadLocal<DateFormat> COOKIE_DATE_FORMAT = new ThreadLocal<DateFormat>() { // from class: org.apache.tomcat.util.http.CookieProcessorBase.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat(CookieProcessorBase.COOKIE_DATE_PATTERN, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };
    protected static final String ANCIENT_DATE = COOKIE_DATE_FORMAT.get().format(new Date((long) AbstractComponentTracker.LINGERING_TIMEOUT));
}