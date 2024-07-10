package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/ContextTransaction.class */
public class ContextTransaction implements Serializable {
    private static final long serialVersionUID = 1;
    private final Map<String, Object> properties = new HashMap();

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public void removeProperty(String name) {
        this.properties.remove(name);
    }

    public Iterator<String> listProperties() {
        return this.properties.keySet().iterator();
    }

    public String toString() {
        return "Transaction[]";
    }
}