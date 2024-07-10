package org.springframework.cglib.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cglib.core.Customizer;
import org.springframework.cglib.core.KeyFactoryCustomizer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/internal/CustomizerRegistry.class */
public class CustomizerRegistry {
    private final Class[] customizerTypes;
    private Map<Class, List<KeyFactoryCustomizer>> customizers = new HashMap();

    public CustomizerRegistry(Class[] customizerTypes) {
        this.customizerTypes = customizerTypes;
    }

    public void add(KeyFactoryCustomizer customizer) {
        Class[] clsArr;
        Class<?> cls = customizer.getClass();
        for (Class type : this.customizerTypes) {
            if (type.isAssignableFrom(cls)) {
                List<KeyFactoryCustomizer> list = this.customizers.get(type);
                if (list == null) {
                    Map<Class, List<KeyFactoryCustomizer>> map = this.customizers;
                    List<KeyFactoryCustomizer> arrayList = new ArrayList<>();
                    list = arrayList;
                    map.put(type, arrayList);
                }
                list.add(customizer);
            }
        }
    }

    public <T> List<T> get(Class<T> klass) {
        List<T> list = (List<T>) this.customizers.get(klass);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Deprecated
    public static CustomizerRegistry singleton(Customizer customizer) {
        CustomizerRegistry registry = new CustomizerRegistry(new Class[]{Customizer.class});
        registry.add(customizer);
        return registry;
    }
}