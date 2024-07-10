package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/SetOverrideRule.class */
final class SetOverrideRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        ContextEnvironment envEntry = (ContextEnvironment) this.digester.peek();
        envEntry.setOverride(false);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug(envEntry.getClass().getName() + ".setOverride(false)");
        }
    }
}