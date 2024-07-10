package org.springframework.format.datetime.joda;

import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/DateTimeFormatterFactory.class */
public class DateTimeFormatterFactory {
    @Nullable
    private String pattern;
    @Nullable
    private DateTimeFormat.ISO iso;
    @Nullable
    private String style;
    @Nullable
    private TimeZone timeZone;

    public DateTimeFormatterFactory() {
    }

    public DateTimeFormatterFactory(String pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIso(DateTimeFormat.ISO iso) {
        this.iso = iso;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public DateTimeFormatter createDateTimeFormatter() {
        return createDateTimeFormatter(org.joda.time.format.DateTimeFormat.mediumDateTime());
    }

    public DateTimeFormatter createDateTimeFormatter(DateTimeFormatter fallbackFormatter) {
        DateTimeFormatter dateTimeFormatter = null;
        if (StringUtils.hasLength(this.pattern)) {
            dateTimeFormatter = org.joda.time.format.DateTimeFormat.forPattern(this.pattern);
        } else if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            switch (this.iso) {
                case DATE:
                    dateTimeFormatter = ISODateTimeFormat.date();
                    break;
                case TIME:
                    dateTimeFormatter = ISODateTimeFormat.time();
                    break;
                case DATE_TIME:
                    dateTimeFormatter = ISODateTimeFormat.dateTime();
                    break;
                default:
                    throw new IllegalStateException("Unsupported ISO format: " + this.iso);
            }
        } else if (StringUtils.hasLength(this.style)) {
            dateTimeFormatter = org.joda.time.format.DateTimeFormat.forStyle(this.style);
        }
        if (dateTimeFormatter != null && this.timeZone != null) {
            dateTimeFormatter = dateTimeFormatter.withZone(DateTimeZone.forTimeZone(this.timeZone));
        }
        return dateTimeFormatter != null ? dateTimeFormatter : fallbackFormatter;
    }
}