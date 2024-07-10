package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/ServletDefCreateRule.class */
final class ServletDefCreateRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        ServletDef servletDef = new ServletDef();
        this.digester.push(servletDef);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("new " + servletDef.getClass().getName());
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        ServletDef servletDef = (ServletDef) this.digester.pop();
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("pop " + servletDef.getClass().getName());
        }
    }
}