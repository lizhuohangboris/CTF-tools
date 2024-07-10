package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/SetNextRule.class */
public class SetNextRule extends Rule {
    protected String methodName;
    protected String paramType;
    protected boolean useExactMatch = false;

    public SetNextRule(String methodName, String paramType) {
        this.methodName = null;
        this.paramType = null;
        this.methodName = methodName;
        this.paramType = paramType;
    }

    public boolean isExactMatch() {
        return this.useExactMatch;
    }

    public void setExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (parent == null) {
                this.digester.log.debug("[SetNextRule]{" + this.digester.match + "} Call [NULL PARENT]." + this.methodName + "(" + child + ")");
            } else {
                this.digester.log.debug("[SetNextRule]{" + this.digester.match + "} Call " + parent.getClass().getName() + "." + this.methodName + "(" + child + ")");
            }
        }
        IntrospectionUtils.callMethod1(parent, this.methodName, child, this.paramType, this.digester.getClassLoader());
    }

    public String toString() {
        return "SetNextRule[methodName=" + this.methodName + ", paramType=" + this.paramType + "]";
    }
}