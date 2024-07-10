package org.apache.catalina.startup;

import org.apache.catalina.Container;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: Catalina.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/SetParentClassLoaderRule.class */
final class SetParentClassLoaderRule extends Rule {
    ClassLoader parentClassLoader;

    public SetParentClassLoaderRule(ClassLoader parentClassLoader) {
        this.parentClassLoader = null;
        this.parentClassLoader = parentClassLoader;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("Setting parent class loader");
        }
        Container top = (Container) this.digester.peek();
        top.setParentClassLoader(this.parentClassLoader);
    }
}