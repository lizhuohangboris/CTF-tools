package org.apache.tomcat.util.descriptor.web;

import java.lang.reflect.Method;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/SetPublicIdRule.class */
final class SetPublicIdRule extends Rule {
    private String method;

    public SetPublicIdRule(String method) {
        this.method = null;
        this.method = method;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Object top = this.digester.peek();
        Class<?>[] paramClasses = {"String".getClass()};
        String[] paramValues = {this.digester.getPublicId()};
        try {
            Method m = top.getClass().getMethod(this.method, paramClasses);
            m.invoke(top, paramValues);
            if (this.digester.getLogger().isDebugEnabled()) {
                this.digester.getLogger().debug("" + top.getClass().getName() + "." + this.method + "(" + paramValues[0] + ")");
            }
        } catch (NoSuchMethodException e) {
            this.digester.getLogger().error("Can't find method " + this.method + " in " + top + " CLASS " + top.getClass());
        }
    }
}