package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;
import java.util.List;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/DateConverter.class */
public class DateConverter extends ClassicConverter {
    long lastTimestamp = -1;
    String timestampStrCache = null;
    CachingDateFormatter cachingDateFormatter = null;

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        String datePattern = getFirstOption();
        if (datePattern == null) {
            datePattern = CoreConstants.ISO8601_PATTERN;
        }
        if (datePattern.equals(CoreConstants.ISO8601_STR)) {
            datePattern = CoreConstants.ISO8601_PATTERN;
        }
        try {
            this.cachingDateFormatter = new CachingDateFormatter(datePattern);
        } catch (IllegalArgumentException e) {
            addWarn("Could not instantiate SimpleDateFormat with pattern " + datePattern, e);
            this.cachingDateFormatter = new CachingDateFormatter(CoreConstants.ISO8601_PATTERN);
        }
        List<String> optionList = getOptionList();
        if (optionList != null && optionList.size() > 1) {
            TimeZone tz = TimeZone.getTimeZone(optionList.get(1));
            this.cachingDateFormatter.setTimeZone(tz);
        }
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent le) {
        long timestamp = le.getTimeStamp();
        return this.cachingDateFormatter.format(timestamp);
    }
}