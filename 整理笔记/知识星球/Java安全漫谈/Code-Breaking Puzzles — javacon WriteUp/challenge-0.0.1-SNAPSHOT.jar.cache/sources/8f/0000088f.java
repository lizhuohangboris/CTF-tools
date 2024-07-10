package org.apache.catalina.realm;

import java.security.Principal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/AuthenticatedUserRealm.class */
public class AuthenticatedUserRealm extends RealmBase {
    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        return new GenericPrincipal(username, null, null);
    }
}