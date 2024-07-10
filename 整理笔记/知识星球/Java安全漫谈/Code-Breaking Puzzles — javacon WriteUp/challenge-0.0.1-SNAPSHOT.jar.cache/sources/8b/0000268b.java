package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/resource/ResourceResolver.class */
public interface ResourceResolver {
    @Nullable
    Resource resolveResource(@Nullable HttpServletRequest httpServletRequest, String str, List<? extends Resource> list, ResourceResolverChain resourceResolverChain);

    @Nullable
    String resolveUrlPath(String str, List<? extends Resource> list, ResourceResolverChain resourceResolverChain);
}