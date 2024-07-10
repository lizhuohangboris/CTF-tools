package org.apache.catalina.session;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/PersistentManager.class */
public final class PersistentManager extends PersistentManagerBase {
    private static final String name = "PersistentManager";

    @Override // org.apache.catalina.session.PersistentManagerBase, org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }
}