package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToEnumConverterFactory.class */
final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {
    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum(ConversionUtils.getEnumType(targetType));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToEnumConverterFactory$StringToEnum.class */
    private class StringToEnum<T extends Enum> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }
}