package org.springframework.format.datetime.joda;

import java.text.ParseException;
import java.util.Locale;
import org.joda.time.YearMonth;
import org.springframework.format.Formatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/datetime/joda/YearMonthFormatter.class */
class YearMonthFormatter implements Formatter<YearMonth> {
    @Override // org.springframework.format.Parser
    public YearMonth parse(String text, Locale locale) throws ParseException {
        return YearMonth.parse(text);
    }

    @Override // org.springframework.format.Printer
    public String print(YearMonth object, Locale locale) {
        return object.toString();
    }
}