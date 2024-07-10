package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/NameRule.class */
public final class NameRule extends Rule {
    boolean isNameSet = false;

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isNameSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.nameCount"));
        }
        this.isNameSet = true;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String text) throws Exception {
        super.body(namespace, name, text);
        ((WebXml) this.digester.peek()).setName(text);
    }
}