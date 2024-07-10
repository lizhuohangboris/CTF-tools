package org.apache.catalina.realm;

import java.security.cert.X509Certificate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/X509SubjectDnRetriever.class */
public class X509SubjectDnRetriever implements X509UsernameRetriever {
    @Override // org.apache.catalina.realm.X509UsernameRetriever
    public String getUsername(X509Certificate clientCert) {
        return clientCert.getSubjectDN().getName();
    }
}