package org.apache.catalina.security;

import java.security.BasicPermission;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/security/DeployXmlPermission.class */
public class DeployXmlPermission extends BasicPermission {
    private static final long serialVersionUID = 1;

    public DeployXmlPermission(String name) {
        super(name);
    }

    public DeployXmlPermission(String name, String actions) {
        super(name, actions);
    }
}