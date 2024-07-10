package org.springframework.util.function;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/function/SupplierUtils.class */
public abstract class SupplierUtils {
    @Nullable
    public static <T> T resolve(@Nullable Supplier<T> supplier) {
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }
}