package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Parser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/LocalDateTimeParser.class */
public final class LocalDateTimeParser implements Parser<LocalDateTime> {
    private final DateTimeFormatter formatter;

    public LocalDateTimeParser(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.format.Parser
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).parseLocalDateTime(text);
    }
}