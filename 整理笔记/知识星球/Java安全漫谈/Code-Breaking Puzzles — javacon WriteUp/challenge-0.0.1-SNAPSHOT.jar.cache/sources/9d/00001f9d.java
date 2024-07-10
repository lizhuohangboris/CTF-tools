package org.springframework.format.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/DateFormatter.class */
public class DateFormatter implements Formatter<Date> {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Map<DateTimeFormat.ISO, String> ISO_PATTERNS;
    @Nullable
    private String pattern;
    @Nullable
    private String stylePattern;
    @Nullable
    private DateTimeFormat.ISO iso;
    @Nullable
    private TimeZone timeZone;
    private int style = 2;
    private boolean lenient = false;

    static {
        Map<DateTimeFormat.ISO, String> formats = new EnumMap<>(DateTimeFormat.ISO.class);
        formats.put(DateTimeFormat.ISO.DATE, "yyyy-MM-dd");
        formats.put(DateTimeFormat.ISO.TIME, "HH:mm:ss.SSSXXX");
        formats.put(DateTimeFormat.ISO.DATE_TIME, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        ISO_PATTERNS = Collections.unmodifiableMap(formats);
    }

    public DateFormatter() {
    }

    public DateFormatter(String pattern) {
        this.pattern = pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIso(DateTimeFormat.ISO iso) {
        this.iso = iso;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setStylePattern(String stylePattern) {
        this.stylePattern = stylePattern;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    @Override // org.springframework.format.Printer
    public String print(Date date, Locale locale) {
        return getDateFormat(locale).format(date);
    }

    @Override // org.springframework.format.Parser
    public Date parse(String text, Locale locale) throws ParseException {
        return getDateFormat(locale).parse(text);
    }

    protected DateFormat getDateFormat(Locale locale) {
        DateFormat dateFormat = createDateFormat(locale);
        if (this.timeZone != null) {
            dateFormat.setTimeZone(this.timeZone);
        }
        dateFormat.setLenient(this.lenient);
        return dateFormat;
    }

    private DateFormat createDateFormat(Locale locale) {
        if (StringUtils.hasLength(this.pattern)) {
            return new SimpleDateFormat(this.pattern, locale);
        }
        if (this.iso != null && this.iso != DateTimeFormat.ISO.NONE) {
            String pattern = ISO_PATTERNS.get(this.iso);
            if (pattern == null) {
                throw new IllegalStateException("Unsupported ISO format " + this.iso);
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(UTC);
            return format;
        } else if (StringUtils.hasLength(this.stylePattern)) {
            int dateStyle = getStylePatternForChar(0);
            int timeStyle = getStylePatternForChar(1);
            if (dateStyle != -1 && timeStyle != -1) {
                return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }
            if (dateStyle != -1) {
                return DateFormat.getDateInstance(dateStyle, locale);
            }
            if (timeStyle != -1) {
                return DateFormat.getTimeInstance(timeStyle, locale);
            }
            throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
        } else {
            return DateFormat.getDateInstance(this.style, locale);
        }
    }

    private int getStylePatternForChar(int index) {
        if (this.stylePattern != null && this.stylePattern.length() > index) {
            switch (this.stylePattern.charAt(index)) {
                case '-':
                    return -1;
                case 'F':
                    return 0;
                case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                    return 1;
                case 'M':
                    return 2;
                case 'S':
                    return 3;
            }
        }
        throw new IllegalStateException("Unsupported style pattern '" + this.stylePattern + "'");
    }
}