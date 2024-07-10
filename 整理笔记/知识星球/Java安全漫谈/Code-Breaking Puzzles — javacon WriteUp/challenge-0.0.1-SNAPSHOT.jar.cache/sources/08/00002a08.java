package org.thymeleaf.util;

import ch.qos.logback.classic.spi.CallerData;
import java.util.regex.Pattern;
import org.slf4j.Marker;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/PatternUtils.class */
public final class PatternUtils {
    public static Pattern strPatternToPattern(String pattern) {
        String pat = pattern.replace(".", "\\.").replace("(", "\\(").replace(")", "\\)").replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "\\[").replace("]", "\\]").replace(CallerData.NA, "\\?").replace(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX, "\\$").replace(Marker.ANY_NON_NULL_MARKER, "\\+").replace("*", "(?:.*?)");
        return Pattern.compile('^' + pat + '$');
    }

    private PatternUtils() {
    }
}