package org.apache.catalina.users;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MemoryUserDatabase.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryGroupCreationFactory.class */
public class MemoryGroupCreationFactory extends AbstractObjectCreationFactory {
    private final MemoryUserDatabase database;

    public MemoryGroupCreationFactory(MemoryUserDatabase database) {
        this.database = database;
    }

    @Override // org.apache.tomcat.util.digester.AbstractObjectCreationFactory, org.apache.tomcat.util.digester.ObjectCreationFactory
    public Object createObject(Attributes attributes) {
        String rolename;
        String groupname = attributes.getValue("groupname");
        if (groupname == null) {
            groupname = attributes.getValue("name");
        }
        String description = attributes.getValue("description");
        String roles = attributes.getValue("roles");
        Group group = this.database.createGroup(groupname, description);
        if (roles != null) {
            while (roles.length() > 0) {
                int comma = roles.indexOf(44);
                if (comma >= 0) {
                    rolename = roles.substring(0, comma).trim();
                    roles = roles.substring(comma + 1);
                } else {
                    rolename = roles.trim();
                    roles = "";
                }
                if (rolename.length() > 0) {
                    Role role = this.database.findRole(rolename);
                    if (role == null) {
                        role = this.database.createRole(rolename, null);
                    }
                    group.addRole(role);
                }
            }
        }
        return group;
    }
}