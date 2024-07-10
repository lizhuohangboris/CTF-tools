package org.springframework.beans.factory.xml;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/BeansDtdResolver.class */
public class BeansDtdResolver implements EntityResolver {
    private static final String DTD_EXTENSION = ".dtd";
    private static final String DTD_NAME = "spring-beans";
    private static final Log logger = LogFactory.getLog(BeansDtdResolver.class);

    @Override // org.xml.sax.EntityResolver
    @Nullable
    public InputSource resolveEntity(String publicId, @Nullable String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]");
        }
        if (systemId != null && systemId.endsWith(".dtd")) {
            int lastPathSeparator = systemId.lastIndexOf(47);
            int dtdNameStart = systemId.indexOf(DTD_NAME, lastPathSeparator);
            if (dtdNameStart != -1) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Trying to locate [spring-beans.dtd] in Spring jar on classpath");
                }
                try {
                    Resource resource = new ClassPathResource("spring-beans.dtd", getClass());
                    InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Found beans DTD [" + systemId + "] in classpath: spring-beans.dtd");
                    }
                    return source;
                } catch (IOException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in classpath", ex);
                        return null;
                    }
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    public String toString() {
        return "EntityResolver for spring-beans DTD";
    }
}