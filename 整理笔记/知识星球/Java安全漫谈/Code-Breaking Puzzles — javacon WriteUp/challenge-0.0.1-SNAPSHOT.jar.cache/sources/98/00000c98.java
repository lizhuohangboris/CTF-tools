package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/AbstractObjectCreationFactory.class */
public abstract class AbstractObjectCreationFactory implements ObjectCreationFactory {
    private Digester digester = null;

    @Override // org.apache.tomcat.util.digester.ObjectCreationFactory
    public abstract Object createObject(Attributes attributes) throws Exception;

    @Override // org.apache.tomcat.util.digester.ObjectCreationFactory
    public Digester getDigester() {
        return this.digester;
    }

    @Override // org.apache.tomcat.util.digester.ObjectCreationFactory
    public void setDigester(Digester digester) {
        this.digester = digester;
    }
}