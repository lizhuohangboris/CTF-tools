package org.springframework.boot.logging.log4j2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

@ConverterKeys({"clr", SpringInputGeneralFieldTagProcessor.COLOR_INPUT_TYPE_ATTR_VALUE})
@Plugin(name = SpringInputGeneralFieldTagProcessor.COLOR_INPUT_TYPE_ATTR_VALUE, category = "Converter")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/log4j2/ColorConverter.class */
public final class ColorConverter extends LogEventPatternConverter {
    private static final Map<String, AnsiElement> ELEMENTS;
    private static final Map<Integer, AnsiElement> LEVELS;
    private final List<PatternFormatter> formatters;
    private final AnsiElement styling;

    static {
        Map<String, AnsiElement> ansiElements = new HashMap<>();
        ansiElements.put("faint", AnsiStyle.FAINT);
        ansiElements.put("red", AnsiColor.RED);
        ansiElements.put("green", AnsiColor.GREEN);
        ansiElements.put("yellow", AnsiColor.YELLOW);
        ansiElements.put("blue", AnsiColor.BLUE);
        ansiElements.put("magenta", AnsiColor.MAGENTA);
        ansiElements.put("cyan", AnsiColor.CYAN);
        ELEMENTS = Collections.unmodifiableMap(ansiElements);
        Map<Integer, AnsiElement> ansiLevels = new HashMap<>();
        ansiLevels.put(Integer.valueOf(Level.FATAL.intLevel()), AnsiColor.RED);
        ansiLevels.put(Integer.valueOf(Level.ERROR.intLevel()), AnsiColor.RED);
        ansiLevels.put(Integer.valueOf(Level.WARN.intLevel()), AnsiColor.YELLOW);
        LEVELS = Collections.unmodifiableMap(ansiLevels);
    }

    private ColorConverter(List<PatternFormatter> formatters, AnsiElement styling) {
        super("style", "style");
        this.formatters = formatters;
        this.styling = styling;
    }

    public static ColorConverter newInstance(Configuration config, String[] options) {
        if (options.length < 1) {
            LOGGER.error("Incorrect number of options on style. Expected at least 1, received {}", Integer.valueOf(options.length));
            return null;
        } else if (options[0] == null) {
            LOGGER.error("No pattern supplied on style");
            return null;
        } else {
            PatternParser parser = PatternLayout.createPatternParser(config);
            List<PatternFormatter> formatters = parser.parse(options[0]);
            AnsiElement element = options.length != 1 ? ELEMENTS.get(options[1]) : null;
            return new ColorConverter(formatters, element);
        }
    }

    public boolean handlesThrowable() {
        for (PatternFormatter formatter : this.formatters) {
            if (formatter.handlesThrowable()) {
                return true;
            }
        }
        return super.handlesThrowable();
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        StringBuilder buf = new StringBuilder();
        for (PatternFormatter formatter : this.formatters) {
            formatter.format(event, buf);
        }
        if (buf.length() > 0) {
            AnsiElement element = this.styling;
            if (element == null) {
                AnsiElement element2 = LEVELS.get(Integer.valueOf(event.getLevel().intLevel()));
                element = element2 != null ? element2 : AnsiColor.GREEN;
            }
            appendAnsiString(toAppendTo, buf.toString(), element);
        }
    }

    protected void appendAnsiString(StringBuilder toAppendTo, String in, AnsiElement element) {
        toAppendTo.append(AnsiOutput.toString(element, in));
    }
}