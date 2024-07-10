package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Parser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/DateTimeParser.class */
public final class DateTimeParser implements Parser<DateTime> {
    private final DateTimeFormatter formatter;

    public DateTimeParser(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.format.Parser
    public DateTime parse(String text, Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseDateTime(text);
    }
}