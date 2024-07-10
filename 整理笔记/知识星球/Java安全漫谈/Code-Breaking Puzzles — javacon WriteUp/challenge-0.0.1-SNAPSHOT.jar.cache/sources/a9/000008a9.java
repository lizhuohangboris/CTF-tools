package org.apache.catalina.realm;

import java.security.cert.X509Certificate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/X509UsernameRetriever.class */
public interface X509UsernameRetriever {
    String getUsername(X509Certificate x509Certificate);
}