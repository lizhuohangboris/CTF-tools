package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToCharacterConverter.class */
final class StringToCharacterConverter implements Converter<String, Character> {
    @Override // org.springframework.core.convert.converter.Converter
    public Character convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        if (source.length() > 1) {
            throw new IllegalArgumentException("Can only convert a [String] with length of 1 to a [Character]; string value '" + source + "'  has length of " + source.length());
        }
        return Character.valueOf(source.charAt(0));
    }
}