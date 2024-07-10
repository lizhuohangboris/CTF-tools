package org.apache.catalina.users;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.thymeleaf.spring5.processor.SpringInputPasswordFieldTagProcessor;
import org.xml.sax.Attributes;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MemoryUserDatabase.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/users/MemoryUserCreationFactory.class */
public class MemoryUserCreationFactory extends AbstractObjectCreationFactory {
    private final MemoryUserDatabase database;

    public MemoryUserCreationFactory(MemoryUserDatabase database) {
        this.database = database;
    }

    @Override // org.apache.tomcat.util.digester.AbstractObjectCreationFactory, org.apache.tomcat.util.digester.ObjectCreationFactory
    public Object createObject(Attributes attributes) {
        String rolename;
        String groupname;
        String username = attributes.getValue("username");
        if (username == null) {
            username = attributes.getValue("name");
        }
        String password = attributes.getValue(SpringInputPasswordFieldTagProcessor.PASSWORD_INPUT_TYPE_ATTR_VALUE);
        String fullName = attributes.getValue("fullName");
        if (fullName == null) {
            fullName = attributes.getValue("fullname");
        }
        String groups = attributes.getValue(ConstraintHelper.GROUPS);
        String roles = attributes.getValue("roles");
        User user = this.database.createUser(username, password, fullName);
        if (groups != null) {
            while (groups.length() > 0) {
                int comma = groups.indexOf(44);
                if (comma >= 0) {
                    groupname = groups.substring(0, comma).trim();
                    groups = groups.substring(comma + 1);
                } else {
                    groupname = groups.trim();
                    groups = "";
                }
                if (groupname.length() > 0) {
                    Group group = this.database.findGroup(groupname);
                    if (group == null) {
                        group = this.database.createGroup(groupname, null);
                    }
                    user.addGroup(group);
                }
            }
        }
        if (roles != null) {
            while (roles.length() > 0) {
                int comma2 = roles.indexOf(44);
                if (comma2 >= 0) {
                    rolename = roles.substring(0, comma2).trim();
                    roles = roles.substring(comma2 + 1);
                } else {
                    rolename = roles.trim();
                    roles = "";
                }
                if (rolename.length() > 0) {
                    Role role = this.database.findRole(rolename);
                    if (role == null) {
                        role = this.database.createRole(rolename, null);
                    }
                    user.addRole(role);
                }
            }
        }
        return user;
    }
}