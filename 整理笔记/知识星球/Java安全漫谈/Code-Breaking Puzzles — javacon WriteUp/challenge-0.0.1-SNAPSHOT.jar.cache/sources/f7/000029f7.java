package org.thymeleaf.util;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/EvaluationUtil.class */
public final class EvaluationUtil {
    @Deprecated
    public static boolean evaluateAsBoolean(Object condition) {
        return EvaluationUtils.evaluateAsBoolean(condition);
    }

    @Deprecated
    public static BigDecimal evaluateAsNumber(Object object) {
        return EvaluationUtils.evaluateAsNumber(object);
    }

    @Deprecated
    public static List<Object> evaluateAsList(Object value) {
        return EvaluationUtils.evaluateAsList(value);
    }

    @Deprecated
    public static Object[] evaluateAsArray(Object value) {
        return EvaluationUtils.evaluateAsArray(value);
    }

    private EvaluationUtil() {
    }
}