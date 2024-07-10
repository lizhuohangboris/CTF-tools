package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import org.apache.tomcat.util.digester.CallParamRule;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/CallParamMultiRule.class */
final class CallParamMultiRule extends CallParamRule {
    public CallParamMultiRule(int paramIndex) {
        super(paramIndex);
    }

    @Override // org.apache.tomcat.util.digester.CallParamRule, org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[]) this.digester.peekParams();
            ArrayList<String> params = (ArrayList) parameters[this.paramIndex];
            if (params == null) {
                params = new ArrayList<>();
                parameters[this.paramIndex] = params;
            }
            params.add(this.bodyTextStack.pop());
        }
    }
}