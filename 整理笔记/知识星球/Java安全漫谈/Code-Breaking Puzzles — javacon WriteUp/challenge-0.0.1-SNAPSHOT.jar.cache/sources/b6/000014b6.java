package org.springframework.beans.factory.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.unbescape.uri.UriEscape;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/ResourceEntityResolver.class */
public class ResourceEntityResolver extends DelegatingEntityResolver {
    private static final Log logger = LogFactory.getLog(ResourceEntityResolver.class);
    private final ResourceLoader resourceLoader;

    public ResourceEntityResolver(ResourceLoader resourceLoader) {
        super(resourceLoader.getClassLoader());
        this.resourceLoader = resourceLoader;
    }

    @Override // org.springframework.beans.factory.xml.DelegatingEntityResolver, org.xml.sax.EntityResolver
    @Nullable
    public InputSource resolveEntity(String publicId, @Nullable String systemId) throws SAXException, IOException {
        InputSource source = super.resolveEntity(publicId, systemId);
        if (source == null && systemId != null) {
            String resourcePath = null;
            try {
                String decodedSystemId = URLDecoder.decode(systemId, UriEscape.DEFAULT_ENCODING);
                String givenUrl = new URL(decodedSystemId).toString();
                String systemRootUrl = new File("").toURI().toURL().toString();
                if (givenUrl.startsWith(systemRootUrl)) {
                    resourcePath = givenUrl.substring(systemRootUrl.length());
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not resolve XML entity [" + systemId + "] against system root URL", ex);
                }
                resourcePath = systemId;
            }
            if (resourcePath != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Trying to locate XML entity [" + systemId + "] as resource [" + resourcePath + "]");
                }
                Resource resource = this.resourceLoader.getResource(resourcePath);
                source = new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found XML entity [" + systemId + "]: " + resource);
                }
            }
        }
        return source;
    }
}