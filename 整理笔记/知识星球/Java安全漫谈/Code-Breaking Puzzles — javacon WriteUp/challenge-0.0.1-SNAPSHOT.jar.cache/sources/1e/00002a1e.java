package org.thymeleaf.util;

import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/Validate.class */
public final class Validate {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String object, String message) {
        if (StringUtils.isEmptyOrWhitespace(object)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection<?> object, String message) {
        if (object == null || object.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] object, String message) {
        if (object == null || object.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void containsNoNulls(Iterable<?> collection, String message) {
        for (Object object : collection) {
            notNull(object, message);
        }
    }

    public static void containsNoEmpties(Iterable<String> collection, String message) {
        for (String object : collection) {
            notEmpty(object, message);
        }
    }

    public static void containsNoNulls(Object[] array, String message) {
        for (Object object : array) {
            notNull(object, message);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    private Validate() {
    }
}