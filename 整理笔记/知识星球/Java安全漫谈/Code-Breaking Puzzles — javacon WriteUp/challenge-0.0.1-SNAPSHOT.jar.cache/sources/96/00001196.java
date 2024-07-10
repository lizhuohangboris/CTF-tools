package org.hibernate.validator.internal.util.privilegedactions;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/privilegedactions/GetInstancesFromServiceLoader.class */
public class GetInstancesFromServiceLoader<T> implements PrivilegedAction<List<T>> {
    private final ClassLoader primaryClassLoader;
    private final Class<T> clazz;

    private GetInstancesFromServiceLoader(ClassLoader primaryClassLoader, Class<T> clazz) {
        this.primaryClassLoader = primaryClassLoader;
        this.clazz = clazz;
    }

    public static <T> GetInstancesFromServiceLoader<T> action(ClassLoader primaryClassLoader, Class<T> serviceClass) {
        return new GetInstancesFromServiceLoader<>(primaryClassLoader, serviceClass);
    }

    @Override // java.security.PrivilegedAction
    public List<T> run() {
        List<T> instances = loadInstances(this.primaryClassLoader);
        if (instances.isEmpty() && GetInstancesFromServiceLoader.class.getClassLoader() != this.primaryClassLoader) {
            instances = loadInstances(GetInstancesFromServiceLoader.class.getClassLoader());
        }
        return instances;
    }

    private List<T> loadInstances(ClassLoader classloader) {
        ServiceLoader<T> loader = ServiceLoader.load(this.clazz, classloader);
        Iterator<T> iterator = loader.iterator();
        List<T> instances = new ArrayList<>();
        while (iterator.hasNext()) {
            try {
                instances.add(iterator.next());
            } catch (ServiceConfigurationError e) {
            }
        }
        return instances;
    }
}