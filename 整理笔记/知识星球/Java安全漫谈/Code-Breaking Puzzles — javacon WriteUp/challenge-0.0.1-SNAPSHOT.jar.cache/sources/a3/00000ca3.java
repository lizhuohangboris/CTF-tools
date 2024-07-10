package org.apache.tomcat.util.digester;

import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/ObjectCreationFactory.class */
public interface ObjectCreationFactory {
    Object createObject(Attributes attributes) throws Exception;

    Digester getDigester();

    void setDigester(Digester digester);
}