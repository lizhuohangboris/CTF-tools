package org.springframework.core.convert.support;

import java.util.Currency;
import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToCurrencyConverter.class */
class StringToCurrencyConverter implements Converter<String, Currency> {
    @Override // org.springframework.core.convert.converter.Converter
    public Currency convert(String source) {
        return Currency.getInstance(source);
    }
}