package org.springframework.format.number.money;

import java.util.Locale;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/format/number/money/MonetaryAmountFormatter.class */
public class MonetaryAmountFormatter implements Formatter<MonetaryAmount> {
    @Nullable
    private String formatName;

    public MonetaryAmountFormatter() {
    }

    public MonetaryAmountFormatter(String formatName) {
        this.formatName = formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    @Override // org.springframework.format.Printer
    public String print(MonetaryAmount object, Locale locale) {
        return getMonetaryAmountFormat(locale).format(object);
    }

    @Override // org.springframework.format.Parser
    public MonetaryAmount parse(String text, Locale locale) {
        return getMonetaryAmountFormat(locale).parse(text);
    }

    protected MonetaryAmountFormat getMonetaryAmountFormat(Locale locale) {
        if (this.formatName != null) {
            return MonetaryFormats.getAmountFormat(this.formatName, new String[0]);
        }
        return MonetaryFormats.getAmountFormat(locale, new String[0]);
    }
}