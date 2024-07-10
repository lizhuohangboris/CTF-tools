package org.springframework.boot.web.servlet;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializerBeans.class */
public class ServletContextInitializerBeans extends AbstractCollection<ServletContextInitializer> {
    private static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";
    private static final Log logger = LogFactory.getLog(ServletContextInitializerBeans.class);
    private final Set<Object> seen = new HashSet();
    private final MultiValueMap<Class<?>, ServletContextInitializer> initializers = new LinkedMultiValueMap();
    private final List<Class<? extends ServletContextInitializer>> initializerTypes;
    private List<ServletContextInitializer> sortedList;

    @FunctionalInterface
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializerBeans$RegistrationBeanAdapter.class */
    public interface RegistrationBeanAdapter<T> {
        RegistrationBean createRegistrationBean(String name, T source, int totalNumberOfSourceBeans);
    }

    @SafeVarargs
    public ServletContextInitializerBeans(ListableBeanFactory beanFactory, Class<? extends ServletContextInitializer>... initializerTypes) {
        List<Class<? extends ServletContextInitializer>> singletonList;
        if (initializerTypes.length != 0) {
            singletonList = Arrays.asList(initializerTypes);
        } else {
            singletonList = Collections.singletonList(ServletContextInitializer.class);
        }
        this.initializerTypes = singletonList;
        addServletContextInitializerBeans(beanFactory);
        addAdaptableBeans(beanFactory);
        List<ServletContextInitializer> sortedInitializers = (List) this.initializers.values().stream().flatMap(value -> {
            return value.stream().sorted(AnnotationAwareOrderComparator.INSTANCE);
        }).collect(Collectors.toList());
        this.sortedList = Collections.unmodifiableList(sortedInitializers);
    }

    private void addServletContextInitializerBeans(ListableBeanFactory beanFactory) {
        for (Class<? extends ServletContextInitializer> initializerType : this.initializerTypes) {
            for (Map.Entry<String, ? extends ServletContextInitializer> initializerBean : getOrderedBeansOfType(beanFactory, initializerType)) {
                addServletContextInitializerBean(initializerBean.getKey(), (ServletContextInitializer) initializerBean.getValue(), beanFactory);
            }
        }
    }

    private void addServletContextInitializerBean(String beanName, ServletContextInitializer initializer, ListableBeanFactory beanFactory) {
        if (initializer instanceof ServletRegistrationBean) {
            Servlet source = ((ServletRegistrationBean) initializer).getServlet();
            addServletContextInitializerBean(Servlet.class, beanName, initializer, beanFactory, source);
        } else if (initializer instanceof FilterRegistrationBean) {
            Filter source2 = ((FilterRegistrationBean) initializer).getFilter();
            addServletContextInitializerBean(Filter.class, beanName, initializer, beanFactory, source2);
        } else if (initializer instanceof DelegatingFilterProxyRegistrationBean) {
            String source3 = ((DelegatingFilterProxyRegistrationBean) initializer).getTargetBeanName();
            addServletContextInitializerBean(Filter.class, beanName, initializer, beanFactory, source3);
        } else if (initializer instanceof ServletListenerRegistrationBean) {
            EventListener source4 = ((ServletListenerRegistrationBean) initializer).getListener();
            addServletContextInitializerBean(EventListener.class, beanName, initializer, beanFactory, source4);
        } else {
            addServletContextInitializerBean(ServletContextInitializer.class, beanName, initializer, beanFactory, initializer);
        }
    }

    private void addServletContextInitializerBean(Class<?> type, String beanName, ServletContextInitializer initializer, ListableBeanFactory beanFactory, Object source) {
        this.initializers.add(type, initializer);
        if (source != null) {
            this.seen.add(source);
        }
        if (logger.isDebugEnabled()) {
            String resourceDescription = getResourceDescription(beanName, beanFactory);
            int order = getOrder(initializer);
            logger.debug("Added existing " + type.getSimpleName() + " initializer bean '" + beanName + "'; order=" + order + ", resource=" + resourceDescription);
        }
    }

    private String getResourceDescription(String beanName, ListableBeanFactory beanFactory) {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            return registry.getBeanDefinition(beanName).getResourceDescription();
        }
        return "unknown";
    }

    protected void addAdaptableBeans(ListableBeanFactory beanFactory) {
        MultipartConfigElement multipartConfig = getMultipartConfig(beanFactory);
        addAsRegistrationBean(beanFactory, Servlet.class, new ServletRegistrationBeanAdapter(multipartConfig));
        addAsRegistrationBean(beanFactory, Filter.class, new FilterRegistrationBeanAdapter());
        for (Class<?> listenerType : ServletListenerRegistrationBean.getSupportedTypes()) {
            addAsRegistrationBean(beanFactory, EventListener.class, listenerType, new ServletListenerRegistrationBeanAdapter());
        }
    }

    private MultipartConfigElement getMultipartConfig(ListableBeanFactory beanFactory) {
        List<Map.Entry<String, MultipartConfigElement>> beans = getOrderedBeansOfType(beanFactory, MultipartConfigElement.class);
        if (beans.isEmpty()) {
            return null;
        }
        return beans.get(0).getValue();
    }

    protected <T> void addAsRegistrationBean(ListableBeanFactory beanFactory, Class<T> type, RegistrationBeanAdapter<T> adapter) {
        addAsRegistrationBean(beanFactory, type, type, adapter);
    }

    private <T, B extends T> void addAsRegistrationBean(ListableBeanFactory beanFactory, Class<T> type, Class<B> beanType, RegistrationBeanAdapter<T> adapter) {
        List<Map.Entry<String, B>> beans = getOrderedBeansOfType(beanFactory, beanType, this.seen);
        for (Map.Entry<String, B> bean : beans) {
            if (this.seen.add(bean.getValue())) {
                int order = getOrder(bean.getValue());
                String beanName = bean.getKey();
                RegistrationBean registration = adapter.createRegistrationBean(beanName, bean.getValue(), beans.size());
                registration.setOrder(order);
                this.initializers.add(type, registration);
                if (logger.isDebugEnabled()) {
                    logger.debug("Created " + type.getSimpleName() + " initializer for bean '" + beanName + "'; order=" + order + ", resource=" + getResourceDescription(beanName, beanFactory));
                }
            }
        }
    }

    private int getOrder(Object value) {
        return new AnnotationAwareOrderComparator() { // from class: org.springframework.boot.web.servlet.ServletContextInitializerBeans.1
            {
                ServletContextInitializerBeans.this = this;
            }

            @Override // org.springframework.core.OrderComparator
            public int getOrder(Object obj) {
                return super.getOrder(obj);
            }
        }.getOrder(value);
    }

    private <T> List<Map.Entry<String, T>> getOrderedBeansOfType(ListableBeanFactory beanFactory, Class<T> type) {
        return getOrderedBeansOfType(beanFactory, type, Collections.emptySet());
    }

    private <T> List<Map.Entry<String, T>> getOrderedBeansOfType(ListableBeanFactory beanFactory, Class<T> type, Set<?> excludes) {
        Comparator<? super Map.Entry<String, T>> comparator = o1, o2 -> {
            return AnnotationAwareOrderComparator.INSTANCE.compare(o1.getValue(), o2.getValue());
        };
        String[] names = beanFactory.getBeanNamesForType(type, true, false);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String name : names) {
            if (!excludes.contains(name) && !ScopedProxyUtils.isScopedTarget(name)) {
                Object bean = beanFactory.getBean(name, type);
                if (!excludes.contains(bean)) {
                    linkedHashMap.put(name, bean);
                }
            }
        }
        List<Map.Entry<String, T>> beans = new ArrayList<>();
        beans.addAll(linkedHashMap.entrySet());
        beans.sort(comparator);
        return beans;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<ServletContextInitializer> iterator() {
        return this.sortedList.iterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        return this.sortedList.size();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializerBeans$ServletRegistrationBeanAdapter.class */
    public static class ServletRegistrationBeanAdapter implements RegistrationBeanAdapter<Servlet> {
        private final MultipartConfigElement multipartConfig;

        ServletRegistrationBeanAdapter(MultipartConfigElement multipartConfig) {
            this.multipartConfig = multipartConfig;
        }

        @Override // org.springframework.boot.web.servlet.ServletContextInitializerBeans.RegistrationBeanAdapter
        public RegistrationBean createRegistrationBean(String name, Servlet source, int totalNumberOfSourceBeans) {
            String url = totalNumberOfSourceBeans != 1 ? "/" + name + "/" : "/";
            if (name.equals("dispatcherServlet")) {
                url = "/";
            }
            ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>(source, url);
            bean.setName(name);
            bean.setMultipartConfig(this.multipartConfig);
            return bean;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializerBeans$FilterRegistrationBeanAdapter.class */
    public static class FilterRegistrationBeanAdapter implements RegistrationBeanAdapter<Filter> {
        private FilterRegistrationBeanAdapter() {
        }

        @Override // org.springframework.boot.web.servlet.ServletContextInitializerBeans.RegistrationBeanAdapter
        public RegistrationBean createRegistrationBean(String name, Filter source, int totalNumberOfSourceBeans) {
            FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(source, new ServletRegistrationBean[0]);
            bean.setName(name);
            return bean;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/ServletContextInitializerBeans$ServletListenerRegistrationBeanAdapter.class */
    public static class ServletListenerRegistrationBeanAdapter implements RegistrationBeanAdapter<EventListener> {
        private ServletListenerRegistrationBeanAdapter() {
        }

        @Override // org.springframework.boot.web.servlet.ServletContextInitializerBeans.RegistrationBeanAdapter
        public RegistrationBean createRegistrationBean(String name, EventListener source, int totalNumberOfSourceBeans) {
            return new ServletListenerRegistrationBean(source);
        }
    }
}