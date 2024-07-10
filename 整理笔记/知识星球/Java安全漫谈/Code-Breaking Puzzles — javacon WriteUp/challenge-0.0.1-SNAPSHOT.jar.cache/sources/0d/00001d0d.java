package org.springframework.context.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/AbstractApplicationEventMulticaster.class */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private BeanFactory beanFactory;
    private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);
    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap(64);
    private Object retrievalMutex = this.defaultRetriever;

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            if (this.beanClassLoader == null) {
                this.beanClassLoader = cbf.getBeanClassLoader();
            }
            this.retrievalMutex = cbf.getSingletonMutex();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public BeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void addApplicationListener(ApplicationListener<?> listener) {
        synchronized (this.retrievalMutex) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }
            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void addApplicationListenerBean(String listenerBeanName) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListener(ApplicationListener<?> listener) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeApplicationListenerBean(String listenerBeanName) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    @Override // org.springframework.context.event.ApplicationEventMulticaster
    public void removeAllListeners() {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        Collection<ApplicationListener<?>> applicationListeners;
        synchronized (this.retrievalMutex) {
            applicationListeners = this.defaultRetriever.getApplicationListeners();
        }
        return applicationListeners;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        }
        if (this.beanClassLoader == null || (ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
            synchronized (this.retrievalMutex) {
                ListenerRetriever retriever2 = this.retrieverCache.get(cacheKey);
                if (retriever2 != null) {
                    return retriever2.getApplicationListeners();
                }
                ListenerRetriever retriever3 = new ListenerRetriever(true);
                Collection<ApplicationListener<?>> listeners = retrieveApplicationListeners(eventType, sourceType, retriever3);
                this.retrieverCache.put(cacheKey, retriever3);
                return listeners;
            }
        }
        return retrieveApplicationListeners(eventType, sourceType, null);
    }

    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever) {
        Set<ApplicationListener<?>> listeners;
        Set<String> listenerBeans;
        List<ApplicationListener<?>> allListeners = new ArrayList<>();
        synchronized (this.retrievalMutex) {
            listeners = new LinkedHashSet<>(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<>(this.defaultRetriever.applicationListenerBeans);
        }
        for (ApplicationListener<?> listener : listeners) {
            if (supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.applicationListeners.add(listener);
                }
                allListeners.add(listener);
            }
        }
        if (!listenerBeans.isEmpty()) {
            BeanFactory beanFactory = getBeanFactory();
            for (String listenerBeanName : listenerBeans) {
                try {
                    Class<?> listenerType = beanFactory.getType(listenerBeanName);
                    if (listenerType == null || supportsEvent(listenerType, eventType)) {
                        ApplicationListener<?> listener2 = (ApplicationListener) beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!allListeners.contains(listener2) && supportsEvent(listener2, eventType, sourceType)) {
                            if (retriever != null) {
                                if (beanFactory.isSingleton(listenerBeanName)) {
                                    retriever.applicationListeners.add(listener2);
                                } else {
                                    retriever.applicationListenerBeans.add(listenerBeanName);
                                }
                            }
                            allListeners.add(listener2);
                        }
                    }
                } catch (NoSuchBeanDefinitionException e) {
                }
            }
        }
        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null && retriever.applicationListenerBeans.isEmpty()) {
            retriever.applicationListeners.clear();
            retriever.applicationListeners.addAll(allListeners);
        }
        return allListeners;
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        ResolvableType declaredEventType;
        return GenericApplicationListener.class.isAssignableFrom(listenerType) || SmartApplicationListener.class.isAssignableFrom(listenerType) || (declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType)) == null || declaredEventType.isAssignableFrom(eventType);
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener);
        return smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/AbstractApplicationEventMulticaster$ListenerCacheKey.class */
    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {
        private final ResolvableType eventType;
        @Nullable
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
            Assert.notNull(eventType, "Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey) other;
            return this.eventType.equals(otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
        }

        public int hashCode() {
            return (this.eventType.hashCode() * 29) + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return other.sourceType == null ? 0 : -1;
                } else if (other.sourceType == null) {
                    return 1;
                } else {
                    result = this.sourceType.getName().compareTo(other.sourceType.getName());
                }
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/AbstractApplicationEventMulticaster$ListenerRetriever.class */
    public class ListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet();
        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList<>(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener<?> listener = (ApplicationListener) beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    } catch (NoSuchBeanDefinitionException e) {
                    }
                }
            }
            if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }
            return allListeners;
        }
    }
}