package org.springframework.objenesis;

import org.springframework.core.SpringProperties;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.strategy.InstantiatorStrategy;
import org.springframework.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/SpringObjenesis.class */
public class SpringObjenesis implements Objenesis {
    public static final String IGNORE_OBJENESIS_PROPERTY_NAME = "spring.objenesis.ignore";
    private final InstantiatorStrategy strategy;
    private final ConcurrentReferenceHashMap<Class<?>, ObjectInstantiator<?>> cache;
    private volatile Boolean worthTrying;

    public SpringObjenesis() {
        this(null);
    }

    public SpringObjenesis(InstantiatorStrategy strategy) {
        this.cache = new ConcurrentReferenceHashMap<>();
        this.strategy = strategy != null ? strategy : new StdInstantiatorStrategy();
        if (SpringProperties.getFlag(IGNORE_OBJENESIS_PROPERTY_NAME)) {
            this.worthTrying = Boolean.FALSE;
        }
    }

    public boolean isWorthTrying() {
        return this.worthTrying != Boolean.FALSE;
    }

    public <T> T newInstance(Class<T> clazz, boolean useCache) {
        if (!useCache) {
            return newInstantiatorOf(clazz).newInstance();
        }
        return getInstantiatorOf(clazz).newInstance();
    }

    @Override // org.springframework.objenesis.Objenesis
    public <T> T newInstance(Class<T> clazz) {
        return getInstantiatorOf(clazz).newInstance();
    }

    @Override // org.springframework.objenesis.Objenesis
    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        ObjectInstantiator<?> instantiator = this.cache.get(clazz);
        if (instantiator == null) {
            ObjectInstantiator<?> newInstantiatorOf = newInstantiatorOf(clazz);
            instantiator = this.cache.putIfAbsent(clazz, newInstantiatorOf);
            if (instantiator == null) {
                instantiator = newInstantiatorOf;
            }
        }
        return instantiator;
    }

    protected <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> clazz) {
        Boolean currentWorthTrying = this.worthTrying;
        try {
            ObjectInstantiator<T> instantiator = this.strategy.newInstantiatorOf(clazz);
            if (currentWorthTrying == null) {
                this.worthTrying = Boolean.TRUE;
            }
            return instantiator;
        } catch (NoClassDefFoundError err) {
            if (currentWorthTrying == null) {
                this.worthTrying = Boolean.FALSE;
            }
            throw new ObjenesisException(err);
        } catch (ObjenesisException ex) {
            if (currentWorthTrying == null) {
                Throwable cause = ex.getCause();
                if ((cause instanceof ClassNotFoundException) || (cause instanceof IllegalAccessException)) {
                    this.worthTrying = Boolean.FALSE;
                }
            }
            throw ex;
        }
    }
}