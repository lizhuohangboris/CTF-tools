package org.thymeleaf.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ObjectUtils.class */
public final class ObjectUtils {
    public static <T> T nullSafe(T target, T defaultValue) {
        return target != null ? target : defaultValue;
    }

    @Deprecated
    public static boolean evaluateAsBoolean(Object condition) {
        return EvaluationUtils.evaluateAsBoolean(condition);
    }

    @Deprecated
    public static BigDecimal evaluateAsNumber(Object object) {
        return EvaluationUtils.evaluateAsNumber(object);
    }

    @Deprecated
    public static List<Object> convertToIterable(Object value) {
        return EvaluationUtils.evaluateAsList(value);
    }

    @Deprecated
    public static List<Object> convertToList(Object value) {
        if (value == null) {
            return Collections.singletonList(null);
        }
        return EvaluationUtils.evaluateAsList(value);
    }

    @Deprecated
    public static Object[] convertToArray(Object value) {
        return EvaluationUtils.evaluateAsArray(value);
    }

    private ObjectUtils() {
    }
}