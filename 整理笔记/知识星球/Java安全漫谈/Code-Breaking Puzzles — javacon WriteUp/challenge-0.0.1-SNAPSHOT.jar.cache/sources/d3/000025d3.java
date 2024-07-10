package org.springframework.web.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector.class */
public class HandlerMappingIntrospector implements CorsConfigurationSource, ApplicationContextAware, InitializingBean {
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private List<HandlerMapping> handlerMappings;

    public HandlerMappingIntrospector() {
    }

    @Deprecated
    public HandlerMappingIntrospector(ApplicationContext context) {
        this.handlerMappings = initHandlerMappings(context);
    }

    public List<HandlerMapping> getHandlerMappings() {
        return this.handlerMappings != null ? this.handlerMappings : Collections.emptyList();
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.handlerMappings == null) {
            Assert.notNull(this.applicationContext, "No ApplicationContext");
            this.handlerMappings = initHandlerMappings(this.applicationContext);
        }
    }

    @Nullable
    public MatchableHandlerMapping getMatchableHandlerMapping(HttpServletRequest request) throws Exception {
        Assert.notNull(this.handlerMappings, "Handler mappings not initialized");
        HttpServletRequest wrapper = new RequestAttributeChangeIgnoringWrapper(request);
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            Object handler = handlerMapping.getHandler(wrapper);
            if (handler != null) {
                if (handlerMapping instanceof MatchableHandlerMapping) {
                    return (MatchableHandlerMapping) handlerMapping;
                }
                throw new IllegalStateException("HandlerMapping is not a MatchableHandlerMapping");
            }
        }
        return null;
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        HandlerInterceptor[] interceptors;
        Assert.notNull(this.handlerMappings, "Handler mappings not initialized");
        HttpServletRequest wrapper = new RequestAttributeChangeIgnoringWrapper(request);
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            HandlerExecutionChain handler = null;
            try {
                handler = handlerMapping.getHandler(wrapper);
            } catch (Exception e) {
            }
            if (handler != null) {
                if (handler.getInterceptors() != null) {
                    for (HandlerInterceptor interceptor : handler.getInterceptors()) {
                        if (interceptor instanceof CorsConfigurationSource) {
                            return ((CorsConfigurationSource) interceptor).getCorsConfiguration(wrapper);
                        }
                    }
                }
                if (handler.getHandler() instanceof CorsConfigurationSource) {
                    return ((CorsConfigurationSource) handler.getHandler()).getCorsConfiguration(wrapper);
                }
            }
        }
        return null;
    }

    private static List<HandlerMapping> initHandlerMappings(ApplicationContext applicationContext) {
        Map<String, HandlerMapping> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, HandlerMapping.class, true, false);
        if (!beans.isEmpty()) {
            List<HandlerMapping> mappings = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(mappings);
            return Collections.unmodifiableList(mappings);
        }
        return Collections.unmodifiableList(initFallback(applicationContext));
    }

    private static List<HandlerMapping> initFallback(ApplicationContext applicationContext) {
        try {
            Resource resource = new ClassPathResource("DispatcherServlet.properties", DispatcherServlet.class);
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            String value = props.getProperty(HandlerMapping.class.getName());
            String[] names = StringUtils.commaDelimitedListToStringArray(value);
            List<HandlerMapping> result = new ArrayList<>(names.length);
            for (String name : names) {
                try {
                    Class<?> clazz = ClassUtils.forName(name, DispatcherServlet.class.getClassLoader());
                    Object mapping = applicationContext.getAutowireCapableBeanFactory().createBean(clazz);
                    result.add((HandlerMapping) mapping);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Could not find default HandlerMapping [" + name + "]");
                }
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + ex.getMessage());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$RequestAttributeChangeIgnoringWrapper.class */
    private static class RequestAttributeChangeIgnoringWrapper extends HttpServletRequestWrapper {
        public RequestAttributeChangeIgnoringWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public void setAttribute(String name, Object value) {
        }
    }
}