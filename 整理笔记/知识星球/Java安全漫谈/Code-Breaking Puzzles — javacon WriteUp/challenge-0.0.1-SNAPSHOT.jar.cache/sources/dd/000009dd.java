package org.apache.catalina.webresources;

import org.apache.catalina.WebResourceRoot;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/webresources/VirtualResource.class */
public class VirtualResource extends EmptyResource {
    private final String name;

    public VirtualResource(WebResourceRoot root, String webAppPath, String name) {
        super(root, webAppPath);
        this.name = name;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public boolean isVirtual() {
        return true;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public boolean isDirectory() {
        return true;
    }

    @Override // org.apache.catalina.webresources.EmptyResource, org.apache.catalina.WebResource
    public String getName() {
        return this.name;
    }
}