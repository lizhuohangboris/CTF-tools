package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Objects.class */
public final class Objects {
    public <T> T nullSafe(T target, T defaultValue) {
        return (T) ObjectUtils.nullSafe(target, defaultValue);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T[] arrayNullSafe(T[] target, T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        T[] result = (T[]) ((Object[]) target.clone());
        for (int i = 0; i < target.length; i++) {
            result[i] = nullSafe(target[i], defaultValue);
        }
        return result;
    }

    public <T> List<T> listNullSafe(List<T> target, T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        ArrayList arrayList = new ArrayList(target.size() + 2);
        for (T element : target) {
            arrayList.add(nullSafe(element, defaultValue));
        }
        return arrayList;
    }

    public <T> Set<T> setNullSafe(Set<T> target, T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        LinkedHashSet linkedHashSet = new LinkedHashSet(target.size() + 2);
        for (T element : target) {
            linkedHashSet.add(nullSafe(element, defaultValue));
        }
        return linkedHashSet;
    }
}