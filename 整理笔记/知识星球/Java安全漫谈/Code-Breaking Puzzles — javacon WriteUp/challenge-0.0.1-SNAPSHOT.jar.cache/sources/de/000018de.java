package org.springframework.boot.context.annotation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.springframework.core.PriorityOrdered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/annotation/UserConfigurations.class */
public class UserConfigurations extends Configurations implements PriorityOrdered {
    @Override // org.springframework.boot.context.annotation.Configurations
    protected /* bridge */ /* synthetic */ Configurations merge(Set mergedClasses) {
        return merge((Set<Class<?>>) mergedClasses);
    }

    protected UserConfigurations(Collection<Class<?>> classes) {
        super(classes);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override // org.springframework.boot.context.annotation.Configurations
    protected UserConfigurations merge(Set<Class<?>> mergedClasses) {
        return new UserConfigurations(mergedClasses);
    }

    public static UserConfigurations of(Class<?>... classes) {
        return new UserConfigurations(Arrays.asList(classes));
    }
}