package org.springframework.boot.autoconfigure.mustache;

import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mustache")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheProperties.class */
public class MustacheProperties extends AbstractTemplateViewResolverProperties {
    public static final String DEFAULT_PREFIX = "classpath:/templates/";
    public static final String DEFAULT_SUFFIX = ".mustache";
    private String prefix;
    private String suffix;

    public MustacheProperties() {
        super("classpath:/templates/", DEFAULT_SUFFIX);
        this.prefix = "classpath:/templates/";
        this.suffix = DEFAULT_SUFFIX;
    }

    @Override // org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties
    public String getPrefix() {
        return this.prefix;
    }

    @Override // org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties
    public String getSuffix() {
        return this.suffix;
    }

    @Override // org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}