package org.springframework.cglib.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/MethodWrapper.class */
public class MethodWrapper {
    private static final MethodWrapperKey KEY_FACTORY = (MethodWrapperKey) KeyFactory.create(MethodWrapperKey.class);

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/MethodWrapper$MethodWrapperKey.class */
    public interface MethodWrapperKey {
        Object newInstance(String str, String[] strArr, String str2);
    }

    private MethodWrapper() {
    }

    public static Object create(Method method) {
        return KEY_FACTORY.newInstance(method.getName(), ReflectUtils.getNames(method.getParameterTypes()), method.getReturnType().getName());
    }

    public static Set createSet(Collection methods) {
        Set set = new HashSet();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            set.add(create((Method) it.next()));
        }
        return set;
    }
}