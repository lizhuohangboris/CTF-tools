package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheServletWebConfiguration.class */
class MustacheServletWebConfiguration {
    private final MustacheProperties mustache;

    protected MustacheServletWebConfiguration(MustacheProperties mustache) {
        this.mustache = mustache;
    }

    @ConditionalOnMissingBean
    @Bean
    public MustacheViewResolver mustacheViewResolver(Mustache.Compiler mustacheCompiler) {
        MustacheViewResolver resolver = new MustacheViewResolver(mustacheCompiler);
        this.mustache.applyToMvcViewResolver(resolver);
        resolver.setCharset(this.mustache.getCharsetName());
        resolver.setOrder(2147483637);
        return resolver;
    }
}