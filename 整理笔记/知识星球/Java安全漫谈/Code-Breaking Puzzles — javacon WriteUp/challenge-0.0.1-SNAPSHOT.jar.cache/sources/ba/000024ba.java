package org.springframework.web.context.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.request.WebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils.class */
public abstract class WebApplicationContextUtils {
    private static final boolean jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());

    static /* synthetic */ ServletRequestAttributes access$400() {
        return currentRequestAttributes();
    }

    public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
        WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
        }
        return wac;
    }

    @Nullable
    public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
        return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    @Nullable
    public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Assert.notNull(sc, "ServletContext must not be null");
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw ((RuntimeException) attr);
        }
        if (attr instanceof Error) {
            throw ((Error) attr);
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Exception) attr);
        }
        if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (WebApplicationContext) attr;
    }

    @Nullable
    public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
        WebApplicationContext wac = getWebApplicationContext(sc);
        if (wac == null) {
            Enumeration<String> attrNames = sc.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String attrName = attrNames.nextElement();
                Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof WebApplicationContext) {
                    if (wac != null) {
                        throw new IllegalStateException("No unique WebApplicationContext found: more than one DispatcherServlet registered with publishContext=true?");
                    }
                    wac = (WebApplicationContext) attrValue;
                }
            }
        }
        return wac;
    }

    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
        registerWebApplicationScopes(beanFactory, null);
    }

    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory, @Nullable ServletContext sc) {
        beanFactory.registerScope("request", new RequestScope());
        beanFactory.registerScope("session", new SessionScope());
        if (sc != null) {
            ServletContextScope appScope = new ServletContextScope(sc);
            beanFactory.registerScope("application", appScope);
            sc.setAttribute(ServletContextScope.class.getName(), appScope);
        }
        beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
        beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
        beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
        beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
        if (jsfPresent) {
            FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
        }
    }

    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext sc) {
        registerEnvironmentBeans(bf, sc, null);
    }

    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        if (servletContext != null && !bf.containsBean("servletContext")) {
            bf.registerSingleton("servletContext", servletContext);
        }
        if (servletConfig != null && !bf.containsBean(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME)) {
            bf.registerSingleton(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME, servletConfig);
        }
        if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
            Map<String, String> parameterMap = new HashMap<>();
            if (servletContext != null) {
                Enumeration<?> paramNameEnum = servletContext.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    String paramName = paramNameEnum.nextElement();
                    parameterMap.put(paramName, servletContext.getInitParameter(paramName));
                }
            }
            if (servletConfig != null) {
                Enumeration<?> paramNameEnum2 = servletConfig.getInitParameterNames();
                while (paramNameEnum2.hasMoreElements()) {
                    String paramName2 = paramNameEnum2.nextElement();
                    parameterMap.put(paramName2, servletConfig.getInitParameter(paramName2));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME, Collections.unmodifiableMap(parameterMap));
        }
        if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
            Map<String, Object> attributeMap = new HashMap<>();
            if (servletContext != null) {
                Enumeration<?> attrNameEnum = servletContext.getAttributeNames();
                while (attrNameEnum.hasMoreElements()) {
                    String attrName = attrNameEnum.nextElement();
                    attributeMap.put(attrName, servletContext.getAttribute(attrName));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME, Collections.unmodifiableMap(attributeMap));
        }
    }

    public static void initServletPropertySources(MutablePropertySources propertySources, ServletContext servletContext) {
        initServletPropertySources(propertySources, servletContext, null);
    }

    public static void initServletPropertySources(MutablePropertySources sources, @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        Assert.notNull(sources, "'propertySources' must not be null");
        if (servletContext != null && sources.contains(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) && (sources.get(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME) instanceof PropertySource.StubPropertySource)) {
            sources.replace(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME, new ServletContextPropertySource(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME, servletContext));
        }
        if (servletConfig != null && sources.contains(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME) && (sources.get(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME) instanceof PropertySource.StubPropertySource)) {
            sources.replace(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME, new ServletConfigPropertySource(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME, servletConfig));
        }
    }

    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes) requestAttr;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils$RequestObjectFactory.class */
    public static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable {
        private RequestObjectFactory() {
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public ServletRequest getObject() {
            return WebApplicationContextUtils.access$400().getRequest();
        }

        public String toString() {
            return "Current HttpServletRequest";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils$ResponseObjectFactory.class */
    public static class ResponseObjectFactory implements ObjectFactory<ServletResponse>, Serializable {
        private ResponseObjectFactory() {
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public ServletResponse getObject() {
            ServletResponse response = WebApplicationContextUtils.access$400().getResponse();
            if (response == null) {
                throw new IllegalStateException("Current servlet response not available - consider using RequestContextFilter instead of RequestContextListener");
            }
            return response;
        }

        public String toString() {
            return "Current HttpServletResponse";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils$SessionObjectFactory.class */
    public static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable {
        private SessionObjectFactory() {
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public HttpSession getObject() {
            return WebApplicationContextUtils.access$400().getRequest().getSession();
        }

        public String toString() {
            return "Current HttpSession";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils$WebRequestObjectFactory.class */
    public static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {
        private WebRequestObjectFactory() {
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public WebRequest getObject() {
            ServletRequestAttributes requestAttr = WebApplicationContextUtils.access$400();
            return new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
        }

        public String toString() {
            return "Current ServletWebRequest";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/WebApplicationContextUtils$FacesDependencyRegistrar.class */
    public static class FacesDependencyRegistrar {
        private FacesDependencyRegistrar() {
        }

        public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
            beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>() { // from class: org.springframework.web.context.support.WebApplicationContextUtils.FacesDependencyRegistrar.1
                @Override // org.springframework.beans.factory.ObjectFactory
                public FacesContext getObject() {
                    return FacesContext.getCurrentInstance();
                }

                public String toString() {
                    return "Current JSF FacesContext";
                }
            });
            beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>() { // from class: org.springframework.web.context.support.WebApplicationContextUtils.FacesDependencyRegistrar.2
                @Override // org.springframework.beans.factory.ObjectFactory
                public ExternalContext getObject() {
                    return FacesContext.getCurrentInstance().getExternalContext();
                }

                public String toString() {
                    return "Current JSF ExternalContext";
                }
            });
        }
    }
}