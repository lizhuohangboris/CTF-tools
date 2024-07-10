package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/PrioritizedParameterNameDiscoverer.class */
public class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer {
    private final List<ParameterNameDiscoverer> parameterNameDiscoverers = new LinkedList();

    public void addDiscoverer(ParameterNameDiscoverer pnd) {
        this.parameterNameDiscoverers.add(pnd);
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) {
        for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            String[] result = pnd.getParameterNames(method);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            String[] result = pnd.getParameterNames(ctor);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}