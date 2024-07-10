package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/CredentialHandler.class */
public interface CredentialHandler {
    boolean matches(String str, String str2);

    String mutate(String str);
}