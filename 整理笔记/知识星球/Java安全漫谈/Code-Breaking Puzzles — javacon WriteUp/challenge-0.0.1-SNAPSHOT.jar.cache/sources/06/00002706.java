package org.springframework.web.servlet.view.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerConfigurer.class */
public class FreeMarkerConfigurer extends FreeMarkerConfigurationFactory implements FreeMarkerConfig, InitializingBean, ResourceLoaderAware, ServletContextAware {
    @Nullable
    private Configuration configuration;
    @Nullable
    private TaglibFactory taglibFactory;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.taglibFactory = new TaglibFactory(servletContext);
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws IOException, TemplateException {
        if (this.configuration == null) {
            this.configuration = createConfiguration();
        }
    }

    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
    }

    @Override // org.springframework.web.servlet.view.freemarker.FreeMarkerConfig
    public Configuration getConfiguration() {
        Assert.state(this.configuration != null, "No Configuration available");
        return this.configuration;
    }

    @Override // org.springframework.web.servlet.view.freemarker.FreeMarkerConfig
    public TaglibFactory getTaglibFactory() {
        Assert.state(this.taglibFactory != null, "No TaglibFactory available");
        return this.taglibFactory;
    }
}