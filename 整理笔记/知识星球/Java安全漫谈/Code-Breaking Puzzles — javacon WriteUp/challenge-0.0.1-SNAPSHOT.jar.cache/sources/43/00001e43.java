package org.springframework.core.convert.support;

import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToUUIDConverter.class */
final class StringToUUIDConverter implements Converter<String, UUID> {
    @Override // org.springframework.core.convert.converter.Converter
    public UUID convert(String source) {
        if (StringUtils.hasLength(source)) {
            return UUID.fromString(source.trim());
        }
        return null;
    }
}