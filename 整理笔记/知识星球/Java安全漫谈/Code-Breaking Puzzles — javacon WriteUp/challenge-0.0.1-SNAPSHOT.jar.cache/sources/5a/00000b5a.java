package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.naming.factory.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerRegistry.class */
public class LoggerRegistry<T extends ExtendedLogger> {
    private static final String DEFAULT_FACTORY_KEY = AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.getName();
    private final MapFactory<T> factory;
    private final Map<String, Map<String, T>> map;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerRegistry$MapFactory.class */
    public interface MapFactory<T extends ExtendedLogger> {
        Map<String, T> createInnerMap();

        Map<String, Map<String, T>> createOuterMap();

        void putIfAbsent(Map<String, T> map, String str, T t);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerRegistry$ConcurrentMapFactory.class */
    public static class ConcurrentMapFactory<T extends ExtendedLogger> implements MapFactory<T> {
        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public Map<String, T> createInnerMap() {
            return new ConcurrentHashMap();
        }

        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public Map<String, Map<String, T>> createOuterMap() {
            return new ConcurrentHashMap();
        }

        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public void putIfAbsent(Map<String, T> innerMap, String name, T logger) {
            ((ConcurrentMap) innerMap).putIfAbsent(name, logger);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/LoggerRegistry$WeakMapFactory.class */
    public static class WeakMapFactory<T extends ExtendedLogger> implements MapFactory<T> {
        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public Map<String, T> createInnerMap() {
            return new WeakHashMap();
        }

        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public Map<String, Map<String, T>> createOuterMap() {
            return new WeakHashMap();
        }

        @Override // org.apache.logging.log4j.spi.LoggerRegistry.MapFactory
        public void putIfAbsent(Map<String, T> innerMap, String name, T logger) {
            innerMap.put(name, logger);
        }
    }

    public LoggerRegistry() {
        this(new ConcurrentMapFactory());
    }

    public LoggerRegistry(MapFactory<T> factory) {
        this.factory = (MapFactory) Objects.requireNonNull(factory, Constants.FACTORY);
        this.map = factory.createOuterMap();
    }

    private static String factoryClassKey(Class<? extends MessageFactory> messageFactoryClass) {
        return messageFactoryClass == null ? DEFAULT_FACTORY_KEY : messageFactoryClass.getName();
    }

    private static String factoryKey(MessageFactory messageFactory) {
        return messageFactory == null ? DEFAULT_FACTORY_KEY : messageFactory.getClass().getName();
    }

    public T getLogger(String name) {
        return getOrCreateInnerMap(DEFAULT_FACTORY_KEY).get(name);
    }

    public T getLogger(String name, MessageFactory messageFactory) {
        return getOrCreateInnerMap(factoryKey(messageFactory)).get(name);
    }

    public Collection<T> getLoggers() {
        return getLoggers(new ArrayList());
    }

    public Collection<T> getLoggers(Collection<T> destination) {
        for (Map<String, T> inner : this.map.values()) {
            destination.addAll(inner.values());
        }
        return destination;
    }

    private Map<String, T> getOrCreateInnerMap(String factoryName) {
        Map<String, T> inner = this.map.get(factoryName);
        if (inner == null) {
            inner = this.factory.createInnerMap();
            this.map.put(factoryName, inner);
        }
        return inner;
    }

    public boolean hasLogger(String name) {
        return getOrCreateInnerMap(DEFAULT_FACTORY_KEY).containsKey(name);
    }

    public boolean hasLogger(String name, MessageFactory messageFactory) {
        return getOrCreateInnerMap(factoryKey(messageFactory)).containsKey(name);
    }

    public boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass) {
        return getOrCreateInnerMap(factoryClassKey(messageFactoryClass)).containsKey(name);
    }

    public void putIfAbsent(String name, MessageFactory messageFactory, T logger) {
        this.factory.putIfAbsent(getOrCreateInnerMap(factoryKey(messageFactory)), name, logger);
    }
}