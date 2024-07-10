package org.apache.catalina;

import java.security.Principal;
import org.ietf.jgss.GSSCredential;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/TomcatPrincipal.class */
public interface TomcatPrincipal extends Principal {
    Principal getUserPrincipal();

    GSSCredential getGssCredential();

    void logout() throws Exception;
}