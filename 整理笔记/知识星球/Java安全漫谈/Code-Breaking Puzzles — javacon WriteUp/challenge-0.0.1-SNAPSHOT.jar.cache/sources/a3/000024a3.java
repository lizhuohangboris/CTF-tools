package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/support/AbstractRefreshableWebApplicationContext.class */
public abstract class AbstractRefreshableWebApplicationContext extends AbstractRefreshableConfigApplicationContext implements ConfigurableWebApplicationContext, ThemeSource {
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServletConfig servletConfig;
    @Nullable
    private String namespace;
    @Nullable
    private ThemeSource themeSource;

    public AbstractRefreshableWebApplicationContext() {
        setDisplayName("Root WebApplicationContext");
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.web.context.WebApplicationContext
    @Nullable
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setServletConfig(@Nullable ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        if (servletConfig != null && this.servletContext == null) {
            setServletContext(servletConfig.getServletContext());
        }
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    @Nullable
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    public void setNamespace(@Nullable String namespace) {
        this.namespace = namespace;
        if (namespace != null) {
            setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
        }
    }

    @Override // org.springframework.web.context.ConfigurableWebApplicationContext
    @Nullable
    public String getNamespace() {
        return this.namespace;
    }

    @Override // org.springframework.context.support.AbstractRefreshableConfigApplicationContext, org.springframework.web.context.ConfigurableWebApplicationContext
    public String[] getConfigLocations() {
        return super.getConfigLocations();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext, org.springframework.context.ApplicationContext
    public String getApplicationName() {
        return this.servletContext != null ? this.servletContext.getContextPath() : "";
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        beanFactory.ignoreDependencyInterface(ServletConfigAware.class);
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        Assert.state(this.servletContext != null, "No ServletContext available");
        return new ServletContextResource(this.servletContext, path);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new ServletContextResourcePatternResolver(this);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    public void onRefresh() {
        this.themeSource = UiApplicationContextUtils.initThemeSource(this);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    public void initPropertySources() {
        ConfigurableEnvironment env = getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment) env).initPropertySources(this.servletContext, this.servletConfig);
        }
    }

    @Override // org.springframework.ui.context.ThemeSource
    @Nullable
    public Theme getTheme(String themeName) {
        Assert.state(this.themeSource != null, "No ThemeSource available");
        return this.themeSource.getTheme(themeName);
    }
}