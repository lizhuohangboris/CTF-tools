package org.springframework.beans.factory.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.AbstractResource;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionResource.class */
public class BeanDefinitionResource extends AbstractResource {
    private final BeanDefinition beanDefinition;

    public BeanDefinitionResource(BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinition = beanDefinition;
    }

    public final BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return false;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        return false;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException("Resource cannot be opened because it points to " + getDescription());
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "BeanDefinition defined in " + this.beanDefinition.getResourceDescription();
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(Object other) {
        return this == other || ((other instanceof BeanDefinitionResource) && ((BeanDefinitionResource) other).beanDefinition.equals(this.beanDefinition));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.beanDefinition.hashCode();
    }
}