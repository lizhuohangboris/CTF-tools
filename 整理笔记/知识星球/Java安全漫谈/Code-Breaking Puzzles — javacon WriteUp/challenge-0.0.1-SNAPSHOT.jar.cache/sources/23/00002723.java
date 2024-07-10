package org.springframework.web.servlet.view.tiles3;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import javax.servlet.ServletContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/SpringWildcardServletTilesApplicationContext.class */
public class SpringWildcardServletTilesApplicationContext extends ServletApplicationContext {
    private final ResourcePatternResolver resolver;

    public SpringWildcardServletTilesApplicationContext(ServletContext servletContext) {
        super(servletContext);
        this.resolver = new ServletContextResourcePatternResolver(servletContext);
    }

    @Nullable
    public ApplicationResource getResource(String localePath) {
        Collection<ApplicationResource> urlSet = getResources(localePath);
        if (!CollectionUtils.isEmpty((Collection<?>) urlSet)) {
            return urlSet.iterator().next();
        }
        return null;
    }

    @Nullable
    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        Collection<ApplicationResource> urlSet = getResources(base.getLocalePath(locale));
        if (!CollectionUtils.isEmpty((Collection<?>) urlSet)) {
            return urlSet.iterator().next();
        }
        return null;
    }

    public Collection<ApplicationResource> getResources(String path) {
        try {
            Resource[] resources = this.resolver.getResources(path);
            if (ObjectUtils.isEmpty((Object[]) resources)) {
                ((ServletContext) getContext()).log("No resources found for path pattern: " + path);
                return Collections.emptyList();
            }
            Collection<ApplicationResource> resourceList = new ArrayList<>(resources.length);
            for (Resource resource : resources) {
                try {
                    URL url = resource.getURL();
                    resourceList.add(new URLApplicationResource(url.toExternalForm(), url));
                } catch (IOException ex) {
                    throw new IllegalArgumentException("No URL for " + resource, ex);
                }
            }
            return resourceList;
        } catch (IOException ex2) {
            ((ServletContext) getContext()).log("Resource retrieval failed for path: " + path, ex2);
            return Collections.emptyList();
        }
    }
}