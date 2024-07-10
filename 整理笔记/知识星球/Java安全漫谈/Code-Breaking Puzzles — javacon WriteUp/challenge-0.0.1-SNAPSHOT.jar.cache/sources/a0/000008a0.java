package org.apache.catalina.realm;

import org.apache.tomcat.util.digester.Rule;
import org.thymeleaf.spring5.processor.SpringInputPasswordFieldTagProcessor;
import org.xml.sax.Attributes;

/* compiled from: MemoryRuleSet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/MemoryUserRule.class */
final class MemoryUserRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String username = attributes.getValue("username");
        if (username == null) {
            username = attributes.getValue("name");
        }
        String password = attributes.getValue(SpringInputPasswordFieldTagProcessor.PASSWORD_INPUT_TYPE_ATTR_VALUE);
        String roles = attributes.getValue("roles");
        MemoryRealm realm = (MemoryRealm) this.digester.peek(this.digester.getCount() - 1);
        realm.addUser(username, password, roles);
    }
}