package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/IgnoreAnnotationsRule.class */
final class IgnoreAnnotationsRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        WebXml webxml = (WebXml) this.digester.peek(this.digester.getCount() - 1);
        String value = attributes.getValue("metadata-complete");
        if ("true".equals(value)) {
            webxml.setMetadataComplete(true);
        } else if ("false".equals(value)) {
            webxml.setMetadataComplete(false);
        }
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug(webxml.getClass().getName() + ".setMetadataComplete( " + webxml.isMetadataComplete() + ")");
        }
    }
}