package org.springframework.boot.logging.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@ConverterKeys({"wEx", "wThrowable", "wException"})
@Plugin(name = "WhitespaceThrowablePatternConverter", category = "Converter")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/log4j2/WhitespaceThrowablePatternConverter.class */
public final class WhitespaceThrowablePatternConverter extends ThrowablePatternConverter {
    private WhitespaceThrowablePatternConverter(Configuration configuration, String[] options) {
        super("WhitespaceThrowable", "throwable", options, configuration);
    }

    public void format(LogEvent event, StringBuilder buffer) {
        if (event.getThrown() != null) {
            buffer.append(this.options.getSeparator());
            super.format(event, buffer);
            buffer.append(this.options.getSeparator());
        }
    }

    public static WhitespaceThrowablePatternConverter newInstance(Configuration configuration, String[] options) {
        return new WhitespaceThrowablePatternConverter(configuration, options);
    }
}