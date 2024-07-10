package org.springframework.core.io;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ResourceEditor.class */
public class ResourceEditor extends PropertyEditorSupport {
    private final ResourceLoader resourceLoader;
    @Nullable
    private PropertyResolver propertyResolver;
    private final boolean ignoreUnresolvablePlaceholders;

    public ResourceEditor() {
        this(new DefaultResourceLoader(), null);
    }

    public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver) {
        this(resourceLoader, propertyResolver, true);
    }

    public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public void setAsText(String text) {
        if (StringUtils.hasText(text)) {
            String locationToUse = resolvePath(text).trim();
            setValue(this.resourceLoader.getResource(locationToUse));
            return;
        }
        setValue(null);
    }

    protected String resolvePath(String path) {
        if (this.propertyResolver == null) {
            this.propertyResolver = new StandardEnvironment();
        }
        return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver.resolveRequiredPlaceholders(path);
    }

    @Nullable
    public String getAsText() {
        Resource value = (Resource) getValue();
        if (value != null) {
            try {
                return value.getURL().toExternalForm();
            } catch (IOException e) {
                return null;
            }
        }
        return "";
    }
}