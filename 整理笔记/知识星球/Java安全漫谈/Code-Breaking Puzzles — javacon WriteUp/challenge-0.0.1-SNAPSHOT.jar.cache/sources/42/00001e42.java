package org.springframework.core.convert.support;

import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToTimeZoneConverter.class */
class StringToTimeZoneConverter implements Converter<String, TimeZone> {
    @Override // org.springframework.core.convert.converter.Converter
    public TimeZone convert(String source) {
        return StringUtils.parseTimeZoneString(source);
    }
}