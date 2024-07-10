package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.CallMethodRule;
import org.xml.sax.SAXException;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/CallMethodMultiRule.class */
final class CallMethodMultiRule extends CallMethodRule {
    final int multiParamIndex;

    public CallMethodMultiRule(String methodName, int paramCount, int multiParamIndex) {
        super(methodName, paramCount);
        this.multiParamIndex = multiParamIndex;
    }

    @Override // org.apache.tomcat.util.digester.CallMethodRule, org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object[] parameters;
        Object target;
        if (this.paramCount > 0) {
            parameters = (Object[]) this.digester.popParams();
        } else {
            parameters = new Object[0];
            super.end(namespace, name);
        }
        ArrayList<?> multiParams = (ArrayList) parameters[this.multiParamIndex];
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i = 0; i < this.paramTypes.length; i++) {
            if (i != this.multiParamIndex) {
                if (parameters[i] == null || ((parameters[i] instanceof String) && !String.class.isAssignableFrom(this.paramTypes[i]))) {
                    paramValues[i] = IntrospectionUtils.convert((String) parameters[i], this.paramTypes[i]);
                } else {
                    paramValues[i] = parameters[i];
                }
            }
        }
        if (this.targetOffset >= 0) {
            target = this.digester.peek(this.targetOffset);
        } else {
            target = this.digester.peek(this.digester.getCount() + this.targetOffset);
        }
        if (target == null) {
            throw new SAXException("[CallMethodRule]{} Call target is null (targetOffset=" + this.targetOffset + ",stackdepth=" + this.digester.getCount() + ")");
        } else if (multiParams == null) {
            paramValues[this.multiParamIndex] = null;
            IntrospectionUtils.callMethodN(target, this.methodName, paramValues, this.paramTypes);
        } else {
            for (int j = 0; j < multiParams.size(); j++) {
                Object param = multiParams.get(j);
                if (param == null || ((param instanceof String) && !String.class.isAssignableFrom(this.paramTypes[this.multiParamIndex]))) {
                    paramValues[this.multiParamIndex] = IntrospectionUtils.convert((String) param, this.paramTypes[this.multiParamIndex]);
                } else {
                    paramValues[this.multiParamIndex] = param;
                }
                IntrospectionUtils.callMethodN(target, this.methodName, paramValues, this.paramTypes);
            }
        }
    }
}