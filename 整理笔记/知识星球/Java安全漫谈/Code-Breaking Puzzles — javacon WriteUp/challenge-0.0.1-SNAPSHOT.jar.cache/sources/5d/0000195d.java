package org.springframework.boot.convert;

import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/convert/CharArrayFormatter.class */
final class CharArrayFormatter implements Formatter<char[]> {
    @Override // org.springframework.format.Printer
    public String print(char[] object, Locale locale) {
        return new String(object);
    }

    @Override // org.springframework.format.Parser
    public char[] parse(String text, Locale locale) throws ParseException {
        return text.toCharArray();
    }
}