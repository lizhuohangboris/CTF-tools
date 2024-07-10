package org.apache.tomcat.util.security;

import java.security.Permission;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/security/PermissionCheck.class */
public interface PermissionCheck {
    boolean check(Permission permission);
}