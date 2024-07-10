package org.springframework.boot.autoconfigure.jersey;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.DynamicRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.filter.RequestContextFilter;

@AutoConfigureBefore({DispatcherServletAutoConfiguration.class})
@EnableConfigurationProperties({JerseyProperties.class})
@AutoConfigureAfter({JacksonAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
@ConditionalOnClass(name = {"org.glassfish.jersey.server.spring.SpringComponentProvider", "javax.servlet.ServletRegistration"})
@ConditionalOnBean(type = {"org.glassfish.jersey.server.ResourceConfig"})
@AutoConfigureOrder(Integer.MIN_VALUE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration.class */
public class JerseyAutoConfiguration implements ServletContextAware {
    private static final Log logger = LogFactory.getLog(JerseyAutoConfiguration.class);
    private final JerseyProperties jersey;
    private final ResourceConfig config;
    private final ObjectProvider<ResourceConfigCustomizer> customizers;
    private String path;

    public JerseyAutoConfiguration(JerseyProperties jersey, ResourceConfig config, ObjectProvider<ResourceConfigCustomizer> customizers) {
        this.jersey = jersey;
        this.config = config;
        this.customizers = customizers;
    }

    @PostConstruct
    public void path() {
        resolveApplicationPath();
        customize();
    }

    private void resolveApplicationPath() {
        if (StringUtils.hasLength(this.jersey.getApplicationPath())) {
            this.path = parseApplicationPath(this.jersey.getApplicationPath());
        } else {
            this.path = findApplicationPath(AnnotationUtils.findAnnotation(this.config.getApplication().getClass(), (Class<ApplicationPath>) ApplicationPath.class));
        }
    }

    private void customize() {
        this.customizers.orderedStream().forEach(customizer -> {
            customizer.customize(this.config);
        });
    }

    @ConditionalOnMissingBean
    @Bean
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestContextFilter());
        registration.setOrder(this.jersey.getFilter().getOrder() - 1);
        registration.setName("requestContextFilter");
        return registration;
    }

    @ConditionalOnMissingBean(name = {"jerseyFilterRegistration"})
    @ConditionalOnProperty(prefix = "spring.jersey", name = {"type"}, havingValue = "filter")
    @Bean
    public FilterRegistrationBean<ServletContainer> jerseyFilterRegistration() {
        FilterRegistrationBean<ServletContainer> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ServletContainer(this.config));
        registration.setUrlPatterns(Arrays.asList(this.path));
        registration.setOrder(this.jersey.getFilter().getOrder());
        registration.addInitParameter("jersey.config.servlet.filter.contextPath", stripPattern(this.path));
        addInitParameters(registration);
        registration.setName("jerseyFilter");
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    private String stripPattern(String path) {
        if (path.endsWith("/*")) {
            path = path.substring(0, path.lastIndexOf("/*"));
        }
        return path;
    }

    @ConditionalOnMissingBean(name = {"jerseyServletRegistration"})
    @ConditionalOnProperty(prefix = "spring.jersey", name = {"type"}, havingValue = "servlet", matchIfMissing = true)
    @Bean
    public ServletRegistrationBean<ServletContainer> jerseyServletRegistration() {
        ServletRegistrationBean<ServletContainer> registration = new ServletRegistrationBean<>(new ServletContainer(this.config), this.path);
        addInitParameters(registration);
        registration.setName(getServletRegistrationName());
        registration.setLoadOnStartup(this.jersey.getServlet().getLoadOnStartup());
        return registration;
    }

    private String getServletRegistrationName() {
        return ClassUtils.getUserClass(this.config.getClass()).getName();
    }

    private void addInitParameters(DynamicRegistrationBean<?> registration) {
        Map<String, String> init = this.jersey.getInit();
        registration.getClass();
        init.forEach(this::addInitParameter);
    }

    private static String findApplicationPath(ApplicationPath annotation) {
        if (annotation == null) {
            return "/*";
        }
        return parseApplicationPath(annotation.value());
    }

    private static String parseApplicationPath(String applicationPath) {
        if (!applicationPath.startsWith("/")) {
            applicationPath = "/" + applicationPath;
        }
        return applicationPath.equals("/") ? "/*" : applicationPath + "/*";
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        String servletRegistrationName = getServletRegistrationName();
        ServletRegistration registration = servletContext.getServletRegistration(servletRegistrationName);
        if (registration != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Configuring existing registration for Jersey servlet '" + servletRegistrationName + "'");
            }
            registration.setInitParameters(this.jersey.getInit());
        }
    }

    @Order(Integer.MIN_VALUE)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JerseyWebApplicationInitializer.class */
    public static final class JerseyWebApplicationInitializer implements WebApplicationInitializer {
        @Override // org.springframework.web.WebApplicationInitializer
        public void onStartup(ServletContext servletContext) throws ServletException {
            servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "<NONE>");
        }
    }

    @ConditionalOnClass({JacksonFeature.class})
    @Configuration
    @ConditionalOnSingleCandidate(ObjectMapper.class)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer.class */
    static class JacksonResourceConfigCustomizer {
        private static final String JAXB_ANNOTATION_INTROSPECTOR_CLASS_NAME = "com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector";

        JacksonResourceConfigCustomizer() {
        }

        @Bean
        public ResourceConfigCustomizer resourceConfigCustomizer(final ObjectMapper objectMapper) {
            addJaxbAnnotationIntrospectorIfPresent(objectMapper);
            return config -> {
                config.register(JacksonFeature.class);
                config.register(new ObjectMapperContextResolver(objectMapper), new Class[]{ContextResolver.class});
            };
        }

        private void addJaxbAnnotationIntrospectorIfPresent(ObjectMapper objectMapper) {
            if (!ClassUtils.isPresent(JAXB_ANNOTATION_INTROSPECTOR_CLASS_NAME, getClass().getClassLoader())) {
                return;
            }
            new ObjectMapperCustomizer().addJaxbAnnotationIntrospector(objectMapper);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer$ObjectMapperCustomizer.class */
        public static final class ObjectMapperCustomizer {
            private ObjectMapperCustomizer() {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void addJaxbAnnotationIntrospector(ObjectMapper objectMapper) {
                JaxbAnnotationIntrospector jaxbAnnotationIntrospector = new JaxbAnnotationIntrospector(objectMapper.getTypeFactory());
                objectMapper.setAnnotationIntrospectors(createPair(objectMapper.getSerializationConfig(), jaxbAnnotationIntrospector), createPair(objectMapper.getDeserializationConfig(), jaxbAnnotationIntrospector));
            }

            private AnnotationIntrospector createPair(MapperConfig<?> config, JaxbAnnotationIntrospector jaxbAnnotationIntrospector) {
                return AnnotationIntrospector.pair(config.getAnnotationIntrospector(), jaxbAnnotationIntrospector);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration$JacksonResourceConfigCustomizer$ObjectMapperContextResolver.class */
        public static final class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
            private final ObjectMapper objectMapper;

            /* renamed from: getContext  reason: collision with other method in class */
            public /* bridge */ /* synthetic */ Object m1307getContext(Class type) {
                return getContext((Class<?>) type);
            }

            private ObjectMapperContextResolver(ObjectMapper objectMapper) {
                this.objectMapper = objectMapper;
            }

            public ObjectMapper getContext(Class<?> type) {
                return this.objectMapper;
            }
        }
    }
}