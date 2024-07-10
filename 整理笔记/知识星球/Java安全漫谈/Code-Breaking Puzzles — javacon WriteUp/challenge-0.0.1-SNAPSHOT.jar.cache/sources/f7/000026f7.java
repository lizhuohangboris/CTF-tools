package org.springframework.web.servlet.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/ResourceBundleViewResolver.class */
public class ResourceBundleViewResolver extends AbstractCachingViewResolver implements Ordered, InitializingBean, DisposableBean {
    public static final String DEFAULT_BASENAME = "views";
    @Nullable
    private String defaultParentView;
    @Nullable
    private Locale[] localesToInitialize;
    private String[] basenames = {DEFAULT_BASENAME};
    private ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();
    private int order = Integer.MAX_VALUE;
    private final Map<Locale, BeanFactory> localeCache = new HashMap();
    private final Map<List<ResourceBundle>, ConfigurableApplicationContext> bundleCache = new HashMap();

    public void setBasename(String basename) {
        setBasenames(basename);
    }

    public void setBasenames(String... basenames) {
        this.basenames = basenames;
    }

    public void setBundleClassLoader(ClassLoader classLoader) {
        this.bundleClassLoader = classLoader;
    }

    protected ClassLoader getBundleClassLoader() {
        return this.bundleClassLoader;
    }

    public void setDefaultParentView(String defaultParentView) {
        this.defaultParentView = defaultParentView;
    }

    public void setLocalesToInitialize(Locale... localesToInitialize) {
        this.localesToInitialize = localesToInitialize;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws BeansException {
        Locale[] localeArr;
        if (this.localesToInitialize != null) {
            for (Locale locale : this.localesToInitialize) {
                initFactory(locale);
            }
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected View loadView(String viewName, Locale locale) throws Exception {
        BeanFactory factory = initFactory(locale);
        try {
            return (View) factory.getBean(viewName, View.class);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    protected synchronized BeanFactory initFactory(Locale locale) throws BeansException {
        String[] strArr;
        BeanFactory cachedFactory;
        BeanFactory cachedFactory2;
        if (isCache() && (cachedFactory2 = this.localeCache.get(locale)) != null) {
            return cachedFactory2;
        }
        List<ResourceBundle> bundles = new LinkedList<>();
        for (String basename : this.basenames) {
            ResourceBundle bundle = getBundle(basename, locale);
            bundles.add(bundle);
        }
        if (isCache() && (cachedFactory = this.bundleCache.get(bundles)) != null) {
            this.localeCache.put(locale, cachedFactory);
            return cachedFactory;
        }
        GenericWebApplicationContext factory = new GenericWebApplicationContext();
        factory.setParent(getApplicationContext());
        factory.setServletContext(getServletContext());
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(factory);
        reader.setDefaultParentBean(this.defaultParentView);
        for (ResourceBundle bundle2 : bundles) {
            reader.registerBeanDefinitions(bundle2);
        }
        factory.refresh();
        if (isCache()) {
            this.localeCache.put(locale, factory);
            this.bundleCache.put(bundles, factory);
        }
        return factory;
    }

    protected ResourceBundle getBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, getBundleClassLoader());
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws BeansException {
        for (ConfigurableApplicationContext factory : this.bundleCache.values()) {
            factory.close();
        }
        this.localeCache.clear();
        this.bundleCache.clear();
    }
}