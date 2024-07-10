package org.apache.naming.factory;

import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceEnvRef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/ResourceEnvFactory.class */
public class ResourceEnvFactory extends FactoryBase {
    @Override // org.apache.naming.factory.FactoryBase
    protected boolean isReferenceTypeSupported(Object obj) {
        return obj instanceof ResourceEnvRef;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected ObjectFactory getDefaultFactory(Reference ref) {
        return null;
    }

    @Override // org.apache.naming.factory.FactoryBase
    protected Object getLinked(Reference ref) {
        return null;
    }
}