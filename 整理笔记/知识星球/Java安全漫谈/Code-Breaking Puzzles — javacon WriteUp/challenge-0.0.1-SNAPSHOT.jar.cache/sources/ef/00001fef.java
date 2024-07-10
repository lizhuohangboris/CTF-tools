package org.springframework.format.datetime.standard;

import java.text.ParseException;
import java.time.Month;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/standard/MonthFormatter.class */
class MonthFormatter implements Formatter<Month> {
    @Override // org.springframework.format.Parser
    public Month parse(String text, Locale locale) throws ParseException {
        return Month.valueOf(text.toUpperCase());
    }

    @Override // org.springframework.format.Printer
    public String print(Month object, Locale locale) {
        return object.toString();
    }
}