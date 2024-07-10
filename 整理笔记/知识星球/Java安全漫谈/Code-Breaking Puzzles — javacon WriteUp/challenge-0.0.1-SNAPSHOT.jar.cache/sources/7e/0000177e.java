package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@EnableConfigurationProperties({MustacheProperties.class})
@Configuration
@ConditionalOnClass({Mustache.class})
@Import({MustacheServletWebConfiguration.class, MustacheReactiveWebConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheAutoConfiguration.class */
public class MustacheAutoConfiguration {
    private static final Log logger = LogFactory.getLog(MustacheAutoConfiguration.class);
    private final MustacheProperties mustache;
    private final Environment environment;
    private final ApplicationContext applicationContext;

    public MustacheAutoConfiguration(MustacheProperties mustache, Environment environment, ApplicationContext applicationContext) {
        this.mustache = mustache;
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void checkTemplateLocationExists() {
        if (this.mustache.isCheckTemplateLocation()) {
            TemplateLocation location = new TemplateLocation(this.mustache.getPrefix());
            if (!location.exists(this.applicationContext)) {
                logger.warn("Cannot find template location: " + location + " (please add some templates, check your Mustache configuration, or set spring.mustache.check-template-location=false)");
            }
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader mustacheTemplateLoader) {
        return Mustache.compiler().withLoader(mustacheTemplateLoader).withCollector(collector());
    }

    private Mustache.Collector collector() {
        MustacheEnvironmentCollector collector = new MustacheEnvironmentCollector();
        collector.setEnvironment(this.environment);
        return collector;
    }

    @ConditionalOnMissingBean({Mustache.TemplateLoader.class})
    @Bean
    public MustacheResourceTemplateLoader mustacheTemplateLoader() {
        MustacheResourceTemplateLoader loader = new MustacheResourceTemplateLoader(this.mustache.getPrefix(), this.mustache.getSuffix());
        loader.setCharset(this.mustache.getCharsetName());
        return loader;
    }
}