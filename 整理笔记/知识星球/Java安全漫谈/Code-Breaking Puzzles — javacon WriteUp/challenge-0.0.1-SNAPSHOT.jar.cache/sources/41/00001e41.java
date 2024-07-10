package org.springframework.core.convert.support;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/StringToPropertiesConverter.class */
final class StringToPropertiesConverter implements Converter<String, Properties> {
    @Override // org.springframework.core.convert.converter.Converter
    public Properties convert(String source) {
        try {
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(source.getBytes(StandardCharsets.ISO_8859_1)));
            return props;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse [" + source + "] into Properties", ex);
        }
    }
}