package org.springframework.format.datetime.joda;

import java.util.Locale;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.Printer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/ReadablePartialPrinter.class */
public final class ReadablePartialPrinter implements Printer<ReadablePartial> {
    private final DateTimeFormatter formatter;

    public ReadablePartialPrinter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override // org.springframework.format.Printer
    public String print(ReadablePartial partial, Locale locale) {
        return JodaTimeContextHolder.getFormatter(this.formatter, locale).print(partial);
    }
}