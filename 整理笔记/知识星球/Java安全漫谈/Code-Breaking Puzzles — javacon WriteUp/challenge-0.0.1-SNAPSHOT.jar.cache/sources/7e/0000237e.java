package org.springframework.util.comparator;

import java.util.Comparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/comparator/InstanceComparator.class */
public class InstanceComparator<T> implements Comparator<T> {
    private final Class<?>[] instanceOrder;

    public InstanceComparator(Class<?>... instanceOrder) {
        Assert.notNull(instanceOrder, "'instanceOrder' array must not be null");
        this.instanceOrder = instanceOrder;
    }

    @Override // java.util.Comparator
    public int compare(T o1, T o2) {
        int i1 = getOrder(o1);
        int i2 = getOrder(o2);
        if (i1 < i2) {
            return -1;
        }
        return i1 == i2 ? 0 : 1;
    }

    private int getOrder(@Nullable T object) {
        if (object != null) {
            for (int i = 0; i < this.instanceOrder.length; i++) {
                if (this.instanceOrder[i].isInstance(object)) {
                    return i;
                }
            }
        }
        return this.instanceOrder.length;
    }
}