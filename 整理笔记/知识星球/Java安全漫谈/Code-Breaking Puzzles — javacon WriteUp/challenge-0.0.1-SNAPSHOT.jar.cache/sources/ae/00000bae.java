package org.apache.naming;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/ResourceEnvRef.class */
public class ResourceEnvRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceEnvFactory";

    public ResourceEnvRef(String resourceType) {
        super(resourceType);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceEnvFactory";
    }
}