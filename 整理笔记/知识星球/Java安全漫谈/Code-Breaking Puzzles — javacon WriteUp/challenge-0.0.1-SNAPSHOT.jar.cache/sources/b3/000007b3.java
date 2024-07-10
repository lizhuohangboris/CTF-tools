package org.apache.catalina.connector;

import java.io.Serializable;
import java.security.Principal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/connector/CoyotePrincipal.class */
public class CoyotePrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 1;
    protected final String name;

    public CoyotePrincipal(String name) {
        this.name = name;
    }

    @Override // java.security.Principal
    public String getName() {
        return this.name;
    }

    @Override // java.security.Principal
    public String toString() {
        return "CoyotePrincipal[" + this.name + "]";
    }
}