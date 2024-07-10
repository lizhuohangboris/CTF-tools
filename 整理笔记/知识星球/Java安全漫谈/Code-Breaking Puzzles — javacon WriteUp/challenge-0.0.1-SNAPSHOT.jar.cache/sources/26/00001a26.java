package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ExtendedThrowablePatternConverter;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@ConverterKeys({"xwEx", "xwThrowable", "xwException"})
@Plugin(name = "ExtendedWhitespaceThrowablePatternConverter", category = "Converter")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/log4j2/ExtendedWhitespaceThrowablePatternConverter.class */
public final class ExtendedWhitespaceThrowablePatternConverter extends ThrowablePatternConverter {
    private final ExtendedThrowablePatternConverter delegate;

    private ExtendedWhitespaceThrowablePatternConverter(Configuration configuration, String[] options) {
        super("WhitespaceExtendedThrowable", "throwable", options, configuration);
        this.delegate = ExtendedThrowablePatternConverter.newInstance(configuration, options);
    }

    public void format(LogEvent event, StringBuilder buffer) {
        if (event.getThrown() != null) {
            buffer.append(this.options.getSeparator());
            this.delegate.format(event, buffer);
            buffer.append(this.options.getSeparator());
        }
    }

    public static ExtendedWhitespaceThrowablePatternConverter newInstance(Configuration configuration, String[] options) {
        return new ExtendedWhitespaceThrowablePatternConverter(configuration, options);
    }
}