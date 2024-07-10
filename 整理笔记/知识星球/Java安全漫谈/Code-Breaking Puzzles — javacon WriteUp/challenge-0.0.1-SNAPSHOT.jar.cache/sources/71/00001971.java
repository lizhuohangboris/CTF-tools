package org.springframework.boot.convert;

import java.util.EnumSet;
import java.util.stream.IntStream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/convert/StringToEnumIgnoringCaseConverterFactory.class */
final class StringToEnumIgnoringCaseConverterFactory implements ConverterFactory<String, Enum> {
    @Override // org.springframework.core.convert.converter.ConverterFactory
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        Class<T> cls;
        Class<T> cls2 = targetType;
        while (true) {
            cls = cls2;
            if (cls == null || cls.isEnum()) {
                break;
            }
            cls2 = cls.getSuperclass();
        }
        Assert.notNull(cls, () -> {
            return "The target type " + targetType.getName() + " does not refer to an enum";
        });
        return new StringToEnum(cls);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/convert/StringToEnumIgnoringCaseConverterFactory$StringToEnum.class */
    private class StringToEnum<T extends Enum> implements Converter<String, T> {
        private final Class<T> enumType;

        StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override // org.springframework.core.convert.converter.Converter
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            String source2 = source.trim();
            try {
                return (T) Enum.valueOf(this.enumType, source2);
            } catch (Exception e) {
                return findEnum(source2);
            }
        }

        private T findEnum(String source) {
            String name = getLettersAndDigits(source);
            for (T candidate : EnumSet.allOf(this.enumType)) {
                if (getLettersAndDigits(candidate.name()).equals(name)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("No enum constant " + this.enumType.getCanonicalName() + "." + source);
        }

        private String getLettersAndDigits(String name) {
            StringBuilder canonicalName = new StringBuilder(name.length());
            IntStream map = name.chars().map(c -> {
                return (char) c;
            }).filter(Character::isLetterOrDigit).map(Character::toLowerCase);
            canonicalName.getClass();
            map.forEach(this::append);
            return canonicalName.toString();
        }
    }
}