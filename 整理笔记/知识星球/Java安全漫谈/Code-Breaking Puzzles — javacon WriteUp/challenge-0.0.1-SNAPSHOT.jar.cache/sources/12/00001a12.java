package org.springframework.boot.loader.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:org/springframework/boot/loader/util/SystemPropertyUtils.class */
public abstract class SystemPropertyUtils {
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String VALUE_SEPARATOR = ":";
    private static final String SIMPLE_PREFIX = "${".substring(1);

    public static String resolvePlaceholders(String text) {
        if (text == null) {
            return text;
        }
        return parseStringValue(null, text, text, new HashSet());
    }

    public static String resolvePlaceholders(Properties properties, String text) {
        if (text == null) {
            return text;
        }
        return parseStringValue(properties, text, text, new HashSet());
    }

    private static String parseStringValue(Properties properties, String value, String current, Set<String> visitedPlaceholders) {
        int indexOf;
        int separatorIndex;
        StringBuilder buf = new StringBuilder(current);
        int startIndex = current.indexOf("${");
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + "${".length(), endIndex);
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                }
                String placeholder2 = parseStringValue(properties, value, placeholder, visitedPlaceholders);
                String propVal = resolvePlaceholder(properties, value, placeholder2);
                if (propVal == null && ":" != 0 && (separatorIndex = placeholder2.indexOf(":")) != -1) {
                    String actualPlaceholder = placeholder2.substring(0, separatorIndex);
                    String defaultValue = placeholder2.substring(separatorIndex + ":".length());
                    propVal = resolvePlaceholder(properties, value, actualPlaceholder);
                    if (propVal == null) {
                        propVal = defaultValue;
                    }
                }
                if (propVal != null) {
                    String propVal2 = parseStringValue(properties, value, propVal, visitedPlaceholders);
                    buf.replace(startIndex, endIndex + "}".length(), propVal2);
                    indexOf = buf.indexOf("${", startIndex + propVal2.length());
                } else {
                    indexOf = buf.indexOf("${", endIndex + "}".length());
                }
                startIndex = indexOf;
                visitedPlaceholders.remove(placeholder);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    private static String resolvePlaceholder(Properties properties, String text, String placeholderName) {
        String propVal = getProperty(placeholderName, null, text);
        if (propVal != null) {
            return propVal;
        }
        if (properties != null) {
            return properties.getProperty(placeholderName);
        }
        return null;
    }

    public static String getProperty(String key) {
        return getProperty(key, null, "");
    }

    public static String getProperty(String key, String defaultValue) {
        return getProperty(key, defaultValue, "");
    }

    public static String getProperty(String key, String defaultValue, String text) {
        try {
            String propVal = System.getProperty(key);
            if (propVal == null) {
                propVal = System.getenv(key);
            }
            if (propVal == null) {
                String name = key.replace('.', '_');
                propVal = System.getenv(name);
            }
            if (propVal == null) {
                String name2 = key.toUpperCase(Locale.ENGLISH).replace('.', '_');
                propVal = System.getenv(name2);
            }
            if (propVal != null) {
                return propVal;
            }
        } catch (Throwable ex) {
            System.err.println("Could not resolve key '" + key + "' in '" + text + "' as system property or in environment: " + ex);
        }
        return defaultValue;
    }

    private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + "${".length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (substringMatch(buf, index, "}")) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index += "}".length();
                } else {
                    return index;
                }
            } else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
                withinNestedPlaceholder++;
                index += SIMPLE_PREFIX.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    private static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}