package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/SoapHeaderRule.class */
final class SoapHeaderRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String text) throws Exception {
        String namespaceuri = null;
        String localpart = text;
        int colon = text.indexOf(58);
        if (colon >= 0) {
            String prefix = text.substring(0, colon);
            namespaceuri = this.digester.findNamespaceURI(prefix);
            localpart = text.substring(colon + 1);
        }
        ContextHandler contextHandler = (ContextHandler) this.digester.peek();
        contextHandler.addSoapHeaders(localpart, namespaceuri);
    }
}