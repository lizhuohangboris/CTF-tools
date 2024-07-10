package org.springframework.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/PropertyPlaceholderHelper.class */
public class PropertyPlaceholderHelper {
    private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);
    private static final Map<String, String> wellKnownSimplePrefixes = new HashMap(4);
    private final String placeholderPrefix;
    private final String placeholderSuffix;
    private final String simplePrefix;
    @Nullable
    private final String valueSeparator;
    private final boolean ignoreUnresolvablePlaceholders;

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/PropertyPlaceholderHelper$PlaceholderResolver.class */
    public interface PlaceholderResolver {
        @Nullable
        String resolvePlaceholder(String str);
    }

    static {
        wellKnownSimplePrefixes.put("}", "{");
        wellKnownSimplePrefixes.put("]", PropertyAccessor.PROPERTY_KEY_PREFIX);
        wellKnownSimplePrefixes.put(")", "(");
    }

    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
        this(placeholderPrefix, placeholderSuffix, null, true);
    }

    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix, @Nullable String valueSeparator, boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
        Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        } else {
            this.simplePrefix = this.placeholderPrefix;
        }
        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public String replacePlaceholders(String value, Properties properties) {
        Assert.notNull(properties, "'properties' must not be null");
        properties.getClass();
        return replacePlaceholders(value, this::getProperty);
    }

    public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
        Assert.notNull(value, "'value' must not be null");
        return parseStringValue(value, placeholderResolver, new HashSet());
    }

    protected String parseStringValue(String value, PlaceholderResolver placeholderResolver, Set<String> visitedPlaceholders) {
        int indexOf;
        int separatorIndex;
        StringBuilder result = new StringBuilder(value);
        int startIndex = value.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                }
                String placeholder2 = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                String propVal = placeholderResolver.resolvePlaceholder(placeholder2);
                if (propVal == null && this.valueSeparator != null && (separatorIndex = placeholder2.indexOf(this.valueSeparator)) != -1) {
                    String actualPlaceholder = placeholder2.substring(0, separatorIndex);
                    String defaultValue = placeholder2.substring(separatorIndex + this.valueSeparator.length());
                    propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                    if (propVal == null) {
                        propVal = defaultValue;
                    }
                }
                if (propVal != null) {
                    String propVal2 = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                    result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal2);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Resolved placeholder '" + placeholder2 + "'");
                    }
                    indexOf = result.indexOf(this.placeholderPrefix, startIndex + propVal2.length());
                } else if (this.ignoreUnresolvablePlaceholders) {
                    indexOf = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder2 + "' in value \"" + value + "\"");
                }
                startIndex = indexOf;
                visitedPlaceholders.remove(placeholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index += this.placeholderSuffix.length();
                } else {
                    return index;
                }
            } else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
                withinNestedPlaceholder++;
                index += this.simplePrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }
}