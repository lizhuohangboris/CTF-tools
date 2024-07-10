package org.apache.naming;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/NameParserImpl.class */
public class NameParserImpl implements NameParser {
    public Name parse(String name) throws NamingException {
        return new CompositeName(name);
    }
}