package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/RelativeOrderingRule.class */
public final class RelativeOrderingRule extends Rule {
    boolean isRelativeOrderingSet = false;
    private final boolean fragment;

    public RelativeOrderingRule(boolean fragment) {
        this.fragment = fragment;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (!this.fragment) {
            this.digester.getLogger().warn(WebRuleSet.sm.getString("webRuleSet.relativeOrdering"));
        }
        if (this.isRelativeOrderingSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.relativeOrderingCount"));
        }
        this.isRelativeOrderingSet = true;
    }
}