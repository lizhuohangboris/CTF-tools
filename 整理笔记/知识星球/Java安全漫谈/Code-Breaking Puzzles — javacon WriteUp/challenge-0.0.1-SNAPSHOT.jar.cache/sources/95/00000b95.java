package org.apache.logging.log4j.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/Strings.class */
public final class Strings {
    public static final String EMPTY = "";
    public static final String LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator", "\n");

    private Strings() {
    }

    public static String dquote(String str) {
        return '\"' + str + '\"';
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String quote(String str) {
        return '\'' + str + '\'';
    }

    public String toRootUpperCase(String str) {
        return str.toUpperCase(Locale.ROOT);
    }

    public static String trimToNull(String str) {
        String ts = str == null ? null : str.trim();
        if (isEmpty(ts)) {
            return null;
        }
        return ts;
    }

    public static String join(Iterable<?> iterable, char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(Iterator<?> iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }
}