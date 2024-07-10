package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/TaglibLocationRule.class */
final class TaglibLocationRule extends Rule {
    final boolean isServlet24OrLater;

    public TaglibLocationRule(boolean isServlet24OrLater) {
        this.isServlet24OrLater = isServlet24OrLater;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        WebXml webXml = (WebXml) this.digester.peek(this.digester.getCount() - 1);
        boolean havePublicId = webXml.getPublicId() != null;
        if (havePublicId == this.isServlet24OrLater) {
            throw new IllegalArgumentException("taglib definition not consistent with specification version");
        }
    }
}