package org.springframework.boot.autoconfigure.webservices.client;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.boot.webservices.client.WebServiceTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.CollectionUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
@ConditionalOnClass({WebServiceTemplate.class, Unmarshaller.class, Marshaller.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/webservices/client/WebServiceTemplateAutoConfiguration.class */
public class WebServiceTemplateAutoConfiguration {
    private final ObjectProvider<WebServiceTemplateCustomizer> webServiceTemplateCustomizers;

    public WebServiceTemplateAutoConfiguration(ObjectProvider<WebServiceTemplateCustomizer> webServiceTemplateCustomizers) {
        this.webServiceTemplateCustomizers = webServiceTemplateCustomizers;
    }

    @ConditionalOnMissingBean
    @Bean
    public WebServiceTemplateBuilder webServiceTemplateBuilder() {
        WebServiceTemplateBuilder builder = new WebServiceTemplateBuilder(new WebServiceTemplateCustomizer[0]);
        List<WebServiceTemplateCustomizer> customizers = (List) this.webServiceTemplateCustomizers.orderedStream().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(customizers)) {
            builder = builder.customizers(customizers);
        }
        return builder;
    }
}