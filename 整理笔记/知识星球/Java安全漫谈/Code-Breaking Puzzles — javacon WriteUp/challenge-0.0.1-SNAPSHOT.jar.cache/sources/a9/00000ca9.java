package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/SetPropertiesRule.class */
public class SetPropertiesRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String theName, Attributes attributes) throws Exception {
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            if (top != null) {
                this.digester.log.debug("[SetPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties");
            } else {
                this.digester.log.debug("[SetPropertiesRule]{" + this.digester.match + "} Set NULL properties");
            }
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "'");
            }
            if (!this.digester.isFakeAttribute(top, name) && !IntrospectionUtils.setProperty(top, name, value) && this.digester.getRulesValidation()) {
                this.digester.log.warn("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "' did not find a matching property.");
            }
        }
    }

    public String toString() {
        return "SetPropertiesRule[]";
    }
}