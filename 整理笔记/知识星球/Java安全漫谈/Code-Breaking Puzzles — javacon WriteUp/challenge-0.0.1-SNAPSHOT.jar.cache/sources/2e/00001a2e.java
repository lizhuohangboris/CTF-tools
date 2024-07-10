package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/logback/ColorConverter.class */
public class ColorConverter extends CompositeConverter<ILoggingEvent> {
    private static final Map<String, AnsiElement> ELEMENTS;
    private static final Map<Integer, AnsiElement> LEVELS;

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
        ansiLevels.put(Level.ERROR_INTEGER, AnsiColor.RED);
        ansiLevels.put(Level.WARN_INTEGER, AnsiColor.YELLOW);
        LEVELS = Collections.unmodifiableMap(ansiLevels);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.pattern.CompositeConverter
    public String transform(ILoggingEvent event, String in) {
        AnsiElement element = ELEMENTS.get(getFirstOption());
        if (element == null) {
            AnsiElement element2 = LEVELS.get(event.getLevel().toInteger());
            element = element2 != null ? element2 : AnsiColor.GREEN;
        }
        return toAnsiString(in, element);
    }

    protected String toAnsiString(String in, AnsiElement element) {
        return AnsiOutput.toString(element, in);
    }
}