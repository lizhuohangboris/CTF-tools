package org.springframework.web.servlet.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/MvcNamespaceUtils.class */
public abstract class MvcNamespaceUtils {
    private static final String BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME = BeanNameUrlHandlerMapping.class.getName();
    private static final String SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME = SimpleControllerHandlerAdapter.class.getName();
    private static final String HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME = HttpRequestHandlerAdapter.class.getName();
    private static final String URL_PATH_HELPER_BEAN_NAME = "mvcUrlPathHelper";
    private static final String PATH_MATCHER_BEAN_NAME = "mvcPathMatcher";
    private static final String CORS_CONFIGURATION_BEAN_NAME = "mvcCorsConfigurations";
    private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";

    public static void registerDefaultComponents(ParserContext parserContext, @Nullable Object source) {
        registerBeanNameUrlHandlerMapping(parserContext, source);
        registerHttpRequestHandlerAdapter(parserContext, source);
        registerSimpleControllerHandlerAdapter(parserContext, source);
        registerHandlerMappingIntrospector(parserContext, source);
    }

    public static RuntimeBeanReference registerUrlPathHelper(@Nullable RuntimeBeanReference urlPathHelperRef, ParserContext parserContext, @Nullable Object source) {
        if (urlPathHelperRef != null) {
            if (parserContext.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME)) {
                parserContext.getRegistry().removeAlias(URL_PATH_HELPER_BEAN_NAME);
            }
            parserContext.getRegistry().registerAlias(urlPathHelperRef.getBeanName(), URL_PATH_HELPER_BEAN_NAME);
        } else if (!parserContext.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME) && !parserContext.getRegistry().containsBeanDefinition(URL_PATH_HELPER_BEAN_NAME)) {
            RootBeanDefinition urlPathHelperDef = new RootBeanDefinition(UrlPathHelper.class);
            urlPathHelperDef.setSource(source);
            urlPathHelperDef.setRole(2);
            parserContext.getRegistry().registerBeanDefinition(URL_PATH_HELPER_BEAN_NAME, urlPathHelperDef);
            parserContext.registerComponent(new BeanComponentDefinition(urlPathHelperDef, URL_PATH_HELPER_BEAN_NAME));
        }
        return new RuntimeBeanReference(URL_PATH_HELPER_BEAN_NAME);
    }

    public static RuntimeBeanReference registerPathMatcher(@Nullable RuntimeBeanReference pathMatcherRef, ParserContext parserContext, @Nullable Object source) {
        if (pathMatcherRef != null) {
            if (parserContext.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME)) {
                parserContext.getRegistry().removeAlias(PATH_MATCHER_BEAN_NAME);
            }
            parserContext.getRegistry().registerAlias(pathMatcherRef.getBeanName(), PATH_MATCHER_BEAN_NAME);
        } else if (!parserContext.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME) && !parserContext.getRegistry().containsBeanDefinition(PATH_MATCHER_BEAN_NAME)) {
            RootBeanDefinition pathMatcherDef = new RootBeanDefinition(AntPathMatcher.class);
            pathMatcherDef.setSource(source);
            pathMatcherDef.setRole(2);
            parserContext.getRegistry().registerBeanDefinition(PATH_MATCHER_BEAN_NAME, pathMatcherDef);
            parserContext.registerComponent(new BeanComponentDefinition(pathMatcherDef, PATH_MATCHER_BEAN_NAME));
        }
        return new RuntimeBeanReference(PATH_MATCHER_BEAN_NAME);
    }

    private static void registerBeanNameUrlHandlerMapping(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME)) {
            RootBeanDefinition mappingDef = new RootBeanDefinition(BeanNameUrlHandlerMapping.class);
            mappingDef.setSource(source);
            mappingDef.setRole(2);
            mappingDef.getPropertyValues().add("order", 2);
            RuntimeBeanReference corsRef = registerCorsConfigurations(null, context, source);
            mappingDef.getPropertyValues().add("corsConfigurations", corsRef);
            context.getRegistry().registerBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME, mappingDef);
            context.registerComponent(new BeanComponentDefinition(mappingDef, BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME));
        }
    }

    private static void registerHttpRequestHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition adapterDef = new RootBeanDefinition(HttpRequestHandlerAdapter.class);
            adapterDef.setSource(source);
            adapterDef.setRole(2);
            context.getRegistry().registerBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME, adapterDef);
            context.registerComponent(new BeanComponentDefinition(adapterDef, HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    private static void registerSimpleControllerHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition(SimpleControllerHandlerAdapter.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME, beanDef);
            context.registerComponent(new BeanComponentDefinition(beanDef, SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    public static RuntimeBeanReference registerCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations, ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(CORS_CONFIGURATION_BEAN_NAME)) {
            RootBeanDefinition corsDef = new RootBeanDefinition(LinkedHashMap.class);
            corsDef.setSource(source);
            corsDef.setRole(2);
            if (corsConfigurations != null) {
                corsDef.getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
            }
            context.getReaderContext().getRegistry().registerBeanDefinition(CORS_CONFIGURATION_BEAN_NAME, corsDef);
            context.registerComponent(new BeanComponentDefinition(corsDef, CORS_CONFIGURATION_BEAN_NAME));
        } else if (corsConfigurations != null) {
            context.getRegistry().getBeanDefinition(CORS_CONFIGURATION_BEAN_NAME).getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
        }
        return new RuntimeBeanReference(CORS_CONFIGURATION_BEAN_NAME);
    }

    private static void registerHandlerMappingIntrospector(ParserContext parserContext, @Nullable Object source) {
        if (!parserContext.getRegistry().containsBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition(HandlerMappingIntrospector.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            beanDef.setLazyInit(true);
            parserContext.getRegistry().registerBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME, beanDef);
            parserContext.registerComponent(new BeanComponentDefinition(beanDef, HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME));
        }
    }

    @Nullable
    public static Object getContentNegotiationManager(ParserContext context) {
        String name = AnnotationDrivenBeanDefinitionParser.HANDLER_MAPPING_BEAN_NAME;
        if (context.getRegistry().containsBeanDefinition(name)) {
            BeanDefinition handlerMappingBeanDef = context.getRegistry().getBeanDefinition(name);
            return handlerMappingBeanDef.getPropertyValues().get("contentNegotiationManager");
        } else if (context.getRegistry().containsBeanDefinition(AnnotationDrivenBeanDefinitionParser.CONTENT_NEGOTIATION_MANAGER_BEAN_NAME)) {
            return new RuntimeBeanReference(AnnotationDrivenBeanDefinitionParser.CONTENT_NEGOTIATION_MANAGER_BEAN_NAME);
        } else {
            return null;
        }
    }
}