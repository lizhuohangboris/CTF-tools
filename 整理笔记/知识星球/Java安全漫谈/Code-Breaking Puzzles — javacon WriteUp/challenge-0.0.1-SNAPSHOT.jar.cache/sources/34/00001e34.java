package org.springframework.core.convert.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.springframework.core.convert.converter.Converter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/PropertiesToStringConverter.class */
final class PropertiesToStringConverter implements Converter<Properties, String> {
    @Override // org.springframework.core.convert.converter.Converter
    public String convert(Properties source) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(256);
            source.store(os, (String) null);
            return os.toString("ISO-8859-1");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to store [" + source + "] into String", ex);
        }
    }
}