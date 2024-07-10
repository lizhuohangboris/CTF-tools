package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.result.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheReactiveWebConfiguration.class */
class MustacheReactiveWebConfiguration {
    private final MustacheProperties mustache;

    protected MustacheReactiveWebConfiguration(MustacheProperties mustache) {
        this.mustache = mustache;
    }

    @ConditionalOnMissingBean
    @Bean
    public MustacheViewResolver mustacheViewResolver(Mustache.Compiler mustacheCompiler) {
        MustacheViewResolver resolver = new MustacheViewResolver(mustacheCompiler);
        resolver.setPrefix(this.mustache.getPrefix());
        resolver.setSuffix(this.mustache.getSuffix());
        resolver.setViewNames(this.mustache.getViewNames());
        resolver.setRequestContextAttribute(this.mustache.getRequestContextAttribute());
        resolver.setCharset(this.mustache.getCharsetName());
        resolver.setOrder(2147483637);
        return resolver;
    }
}