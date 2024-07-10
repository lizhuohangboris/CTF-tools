package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/WebFluxRegistrations.class */
public interface WebFluxRegistrations {
    default RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return null;
    }

    default RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return null;
    }
}