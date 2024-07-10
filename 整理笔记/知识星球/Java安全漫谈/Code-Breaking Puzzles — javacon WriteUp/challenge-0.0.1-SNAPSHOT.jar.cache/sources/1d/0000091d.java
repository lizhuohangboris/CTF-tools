package org.apache.catalina.startup;

import java.util.HashMap;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/SetAllPropertiesRule.class */
public class SetAllPropertiesRule extends Rule {
    protected final HashMap<String, String> excludes = new HashMap<>();

    public SetAllPropertiesRule() {
    }

    public SetAllPropertiesRule(String[] exclude) {
        for (int i = 0; i < exclude.length; i++) {
            if (exclude[i] != null) {
                this.excludes.put(exclude[i], exclude[i]);
            }
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String nameX, Attributes attributes) throws Exception {
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (!this.excludes.containsKey(name) && !this.digester.isFakeAttribute(this.digester.peek(), name) && !IntrospectionUtils.setProperty(this.digester.peek(), name, value) && this.digester.getRulesValidation()) {
                this.digester.getLogger().warn("[SetAllPropertiesRule]{" + this.digester.getMatch() + "} Setting property '" + name + "' to '" + value + "' did not find a matching property.");
            }
        }
    }
}