package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/EnumToStringConverter.class */
final class EnumToStringConverter extends AbstractConditionalEnumConverter implements Converter<Enum<?>, String> {
    public EnumToStringConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.core.convert.converter.Converter
    public String convert(Enum<?> source) {
        return source.name();
    }
}