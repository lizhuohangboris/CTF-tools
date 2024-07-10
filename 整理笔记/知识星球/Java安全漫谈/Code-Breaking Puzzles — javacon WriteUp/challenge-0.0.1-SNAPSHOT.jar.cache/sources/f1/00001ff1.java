package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.springframework.format.Parser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/standard/TemporalAccessorParser.class */
public final class TemporalAccessorParser implements Parser<TemporalAccessor> {
    private final Class<? extends TemporalAccessor> temporalAccessorType;
    private final DateTimeFormatter formatter;

    public TemporalAccessorParser(Class<? extends TemporalAccessor> temporalAccessorType, DateTimeFormatter formatter) {
        this.temporalAccessorType = temporalAccessorType;
        this.formatter = formatter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.format.Parser
    public TemporalAccessor parse(String text, Locale locale) throws ParseException {
        DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(this.formatter, locale);
        if (LocalDate.class == this.temporalAccessorType) {
            return LocalDate.parse(text, formatterToUse);
        }
        if (LocalTime.class == this.temporalAccessorType) {
            return LocalTime.parse(text, formatterToUse);
        }
        if (LocalDateTime.class == this.temporalAccessorType) {
            return LocalDateTime.parse(text, formatterToUse);
        }
        if (ZonedDateTime.class == this.temporalAccessorType) {
            return ZonedDateTime.parse(text, formatterToUse);
        }
        if (OffsetDateTime.class == this.temporalAccessorType) {
            return OffsetDateTime.parse(text, formatterToUse);
        }
        if (OffsetTime.class == this.temporalAccessorType) {
            return OffsetTime.parse(text, formatterToUse);
        }
        throw new IllegalStateException("Unsupported TemporalAccessor type: " + this.temporalAccessorType);
    }
}