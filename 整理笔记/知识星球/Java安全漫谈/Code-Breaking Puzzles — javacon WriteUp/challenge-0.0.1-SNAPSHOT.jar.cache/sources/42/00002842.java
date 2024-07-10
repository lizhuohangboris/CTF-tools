package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Bools.class */
public final class Bools {
    public Boolean isTrue(Object target) {
        return Boolean.valueOf(EvaluationUtils.evaluateAsBoolean(target));
    }

    public Boolean[] arrayIsTrue(Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isTrue(target[i]);
        }
        return result;
    }

    public List<Boolean> listIsTrue(List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(isTrue(element));
        }
        return result;
    }

    public Set<Boolean> setIsTrue(Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(isTrue(element));
        }
        return result;
    }

    public Boolean isFalse(Object target) {
        return Boolean.valueOf(!EvaluationUtils.evaluateAsBoolean(target));
    }

    public Boolean[] arrayIsFalse(Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isFalse(target[i]);
        }
        return result;
    }

    public List<Boolean> listIsFalse(List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(isFalse(element));
        }
        return result;
    }

    public Set<Boolean> setIsFalse(Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(isFalse(element));
        }
        return result;
    }

    public Boolean arrayAnd(Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object aTarget : target) {
            if (!isTrue(aTarget).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Boolean listAnd(List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object element : target) {
            if (!isTrue(element).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Boolean setAnd(Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object element : target) {
            if (!isTrue(element).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public Boolean arrayOr(Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object aTarget : target) {
            if (isTrue(aTarget).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Boolean listOr(List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object element : target) {
            if (isTrue(element).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Boolean setOr(Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (Object element : target) {
            if (isTrue(element).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}