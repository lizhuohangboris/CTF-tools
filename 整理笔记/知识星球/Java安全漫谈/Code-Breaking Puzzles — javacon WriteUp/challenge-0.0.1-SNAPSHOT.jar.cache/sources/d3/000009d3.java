package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/JarResourceSet.class */
public class JarResourceSet extends AbstractSingleArchiveResourceSet {
    public JarResourceSet() {
    }

    public JarResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) throws IllegalArgumentException {
        super(root, webAppMount, base, internalPath);
    }

    @Override // org.apache.catalina.webresources.AbstractArchiveResourceSet
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new JarResource(this, webAppPath, getBaseUrlString(), jarEntry);
    }
}