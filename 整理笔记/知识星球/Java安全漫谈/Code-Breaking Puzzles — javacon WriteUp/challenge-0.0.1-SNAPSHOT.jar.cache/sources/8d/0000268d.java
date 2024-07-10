package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/ResourceTransformer.class */
public interface ResourceTransformer {
    Resource transform(HttpServletRequest httpServletRequest, Resource resource, ResourceTransformerChain resourceTransformerChain) throws IOException;
}