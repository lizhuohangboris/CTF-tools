package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/EnumToIntegerConverter.class */
final class EnumToIntegerConverter extends AbstractConditionalEnumConverter implements Converter<Enum<?>, Integer> {
    public EnumToIntegerConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.core.convert.converter.Converter
    public Integer convert(Enum<?> source) {
        return Integer.valueOf(source.ordinal());
    }
}