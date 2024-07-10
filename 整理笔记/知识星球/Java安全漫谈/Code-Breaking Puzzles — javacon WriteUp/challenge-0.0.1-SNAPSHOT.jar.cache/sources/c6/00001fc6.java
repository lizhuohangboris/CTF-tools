package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.MonthDay;
import org.springframework.format.Formatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/MonthDayFormatter.class */
class MonthDayFormatter implements Formatter<MonthDay> {
    @Override // org.springframework.format.Parser
    public MonthDay parse(String text, Locale locale) throws ParseException {
        return MonthDay.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(MonthDay object, Locale locale) {
        return object.toString();
    }
}