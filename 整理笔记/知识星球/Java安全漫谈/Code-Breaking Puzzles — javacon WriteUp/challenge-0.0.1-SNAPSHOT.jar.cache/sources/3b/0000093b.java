package org.apache.catalina.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/ConcurrentDateFormat.class */
public class ConcurrentDateFormat {
    private final String format;
    private final Locale locale;
    private final TimeZone timezone;
    private final Queue<SimpleDateFormat> queue = new ConcurrentLinkedQueue();
    public static final String RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final ConcurrentDateFormat FORMAT_RFC1123 = new ConcurrentDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US, GMT);

    public static String formatRfc1123(Date date) {
        return FORMAT_RFC1123.format(date);
    }

    public ConcurrentDateFormat(String format, Locale locale, TimeZone timezone) {
        this.format = format;
        this.locale = locale;
        this.timezone = timezone;
        SimpleDateFormat initial = createInstance();
        this.queue.add(initial);
    }

    public String format(Date date) {
        SimpleDateFormat sdf = this.queue.poll();
        if (sdf == null) {
            sdf = createInstance();
        }
        String result = sdf.format(date);
        this.queue.add(sdf);
        return result;
    }

    private SimpleDateFormat createInstance() {
        SimpleDateFormat sdf = new SimpleDateFormat(this.format, this.locale);
        sdf.setTimeZone(this.timezone);
        return sdf;
    }
}