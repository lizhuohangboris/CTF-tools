package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/ObjectToStringConverter.class */
final class ObjectToStringConverter implements Converter<Object, String> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.convert.converter.Converter
    public String convert(Object source) {
        return source.toString();
    }
}