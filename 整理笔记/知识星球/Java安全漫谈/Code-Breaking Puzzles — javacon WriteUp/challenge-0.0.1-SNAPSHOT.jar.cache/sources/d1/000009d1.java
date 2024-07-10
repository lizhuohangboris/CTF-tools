package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/JarResource.class */
public class JarResource extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog(JarResource.class);

    public JarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, ResourceUtils.JAR_URL_PREFIX + baseUrl + ResourceUtils.JAR_URL_SEPARATOR, jarEntry, baseUrl);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.webresources.AbstractResource
    public Log getLog() {
        return log;
    }
}