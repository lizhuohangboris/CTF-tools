package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/accept/ContentNegotiationManagerFactoryBean.class */
public class ContentNegotiationManagerFactoryBean implements FactoryBean<ContentNegotiationManager>, ServletContextAware, InitializingBean {
    @Nullable
    private List<ContentNegotiationStrategy> strategies;
    @Nullable
    private Boolean useRegisteredExtensionsOnly;
    @Nullable
    private ContentNegotiationStrategy defaultNegotiationStrategy;
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    @Nullable
    private ServletContext servletContext;
    private boolean favorPathExtension = true;
    private boolean favorParameter = false;
    private boolean ignoreAcceptHeader = false;
    private Map<String, MediaType> mediaTypes = new HashMap();
    private boolean ignoreUnknownPathExtensions = true;
    private String parameterName = "format";

    public void setStrategies(@Nullable List<ContentNegotiationStrategy> strategies) {
        this.strategies = strategies != null ? new ArrayList(strategies) : null;
    }

    public void setFavorPathExtension(boolean favorPathExtension) {
        this.favorPathExtension = favorPathExtension;
    }

    public void setMediaTypes(Properties mediaTypes) {
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            mediaTypes.forEach(key, value -> {
                String extension = ((String) key).toLowerCase(Locale.ENGLISH);
                MediaType mediaType = MediaType.valueOf((String) value);
                this.mediaTypes.put(extension, mediaType);
            });
        }
    }

    public void addMediaType(String fileExtension, MediaType mediaType) {
        this.mediaTypes.put(fileExtension, mediaType);
    }

    public void addMediaTypes(@Nullable Map<String, MediaType> mediaTypes) {
        if (mediaTypes != null) {
            this.mediaTypes.putAll(mediaTypes);
        }
    }

    public void setIgnoreUnknownPathExtensions(boolean ignore) {
        this.ignoreUnknownPathExtensions = ignore;
    }

    @Deprecated
    public void setUseJaf(boolean useJaf) {
        setUseRegisteredExtensionsOnly(!useJaf);
    }

    public void setUseRegisteredExtensionsOnly(boolean useRegisteredExtensionsOnly) {
        this.useRegisteredExtensionsOnly = Boolean.valueOf(useRegisteredExtensionsOnly);
    }

    private boolean useRegisteredExtensionsOnly() {
        return this.useRegisteredExtensionsOnly != null && this.useRegisteredExtensionsOnly.booleanValue();
    }

    public void setFavorParameter(boolean favorParameter) {
        this.favorParameter = favorParameter;
    }

    public void setParameterName(String parameterName) {
        Assert.notNull(parameterName, "parameterName is required");
        this.parameterName = parameterName;
    }

    public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
        this.ignoreAcceptHeader = ignoreAcceptHeader;
    }

    public void setDefaultContentType(MediaType contentType) {
        this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentType);
    }

    public void setDefaultContentTypes(List<MediaType> contentTypes) {
        this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentTypes);
    }

    public void setDefaultContentTypeStrategy(ContentNegotiationStrategy strategy) {
        this.defaultNegotiationStrategy = strategy;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        build();
    }

    public ContentNegotiationManager build() {
        PathExtensionContentNegotiationStrategy strategy;
        List<ContentNegotiationStrategy> strategies = new ArrayList<>();
        if (this.strategies != null) {
            strategies.addAll(this.strategies);
        } else {
            if (this.favorPathExtension) {
                if (this.servletContext != null && !useRegisteredExtensionsOnly()) {
                    strategy = new ServletPathExtensionContentNegotiationStrategy(this.servletContext, this.mediaTypes);
                } else {
                    strategy = new PathExtensionContentNegotiationStrategy(this.mediaTypes);
                }
                strategy.setIgnoreUnknownExtensions(this.ignoreUnknownPathExtensions);
                if (this.useRegisteredExtensionsOnly != null) {
                    strategy.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly.booleanValue());
                }
                strategies.add(strategy);
            }
            if (this.favorParameter) {
                ParameterContentNegotiationStrategy strategy2 = new ParameterContentNegotiationStrategy(this.mediaTypes);
                strategy2.setParameterName(this.parameterName);
                if (this.useRegisteredExtensionsOnly != null) {
                    strategy2.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly.booleanValue());
                } else {
                    strategy2.setUseRegisteredExtensionsOnly(true);
                }
                strategies.add(strategy2);
            }
            if (!this.ignoreAcceptHeader) {
                strategies.add(new HeaderContentNegotiationStrategy());
            }
            if (this.defaultNegotiationStrategy != null) {
                strategies.add(this.defaultNegotiationStrategy);
            }
        }
        this.contentNegotiationManager = new ContentNegotiationManager(strategies);
        return this.contentNegotiationManager;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public ContentNegotiationManager getObject() {
        return this.contentNegotiationManager;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return ContentNegotiationManager.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}