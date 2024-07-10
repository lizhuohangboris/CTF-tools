package org.springframework.boot.autoconfigure.hateoas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.plugin.core.Plugin;
import org.springframework.web.bind.annotation.RequestMapping;

@EnableConfigurationProperties({HateoasProperties.class})
@Configuration
@ConditionalOnClass({Resource.class, RequestMapping.class, Plugin.class})
@AutoConfigureAfter({WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class})
@ConditionalOnWebApplication
@Import({HypermediaHttpMessageConverterConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaAutoConfiguration.class */
public class HypermediaAutoConfiguration {

    @EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
    @Configuration
    @ConditionalOnClass({ObjectMapper.class})
    @ConditionalOnMissingBean({LinkDiscoverers.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaAutoConfiguration$HypermediaConfiguration.class */
    protected static class HypermediaConfiguration {
        protected HypermediaConfiguration() {
        }
    }

    @ConditionalOnMissingBean({EntityLinks.class})
    @Configuration
    @EnableEntityLinks
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/hateoas/HypermediaAutoConfiguration$EntityLinksConfiguration.class */
    protected static class EntityLinksConfiguration {
        protected EntityLinksConfiguration() {
        }
    }
}