package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/ResourceTransformerChain.class */
public interface ResourceTransformerChain {
    ResourceResolverChain getResolverChain();

    Resource transform(HttpServletRequest httpServletRequest, Resource resource) throws IOException;
}