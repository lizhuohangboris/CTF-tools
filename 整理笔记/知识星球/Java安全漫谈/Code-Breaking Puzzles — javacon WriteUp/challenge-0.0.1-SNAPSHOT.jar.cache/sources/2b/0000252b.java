package org.springframework.web.method.support;

import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/support/UriComponentsContributor.class */
public interface UriComponentsContributor {
    boolean supportsParameter(MethodParameter methodParameter);

    void contributeMethodArgument(MethodParameter methodParameter, Object obj, UriComponentsBuilder uriComponentsBuilder, Map<String, Object> map, ConversionService conversionService);
}