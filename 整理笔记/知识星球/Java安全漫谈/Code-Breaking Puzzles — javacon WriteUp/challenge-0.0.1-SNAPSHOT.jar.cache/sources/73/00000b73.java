package org.apache.logging.log4j.util;

import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/EnglishEnums.class */
public final class EnglishEnums {
    private EnglishEnums() {
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        return (T) valueOf(enumType, name, null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name, T defaultValue) {
        return name == null ? defaultValue : (T) Enum.valueOf(enumType, name.toUpperCase(Locale.ENGLISH));
    }
}