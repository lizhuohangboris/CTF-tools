package org.springframework.boot.webservices.client;

import org.springframework.ws.client.core.WebServiceTemplate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/webservices/client/WebServiceTemplateCustomizer.class */
public interface WebServiceTemplateCustomizer {
    void customize(WebServiceTemplate webServiceTemplate);
}