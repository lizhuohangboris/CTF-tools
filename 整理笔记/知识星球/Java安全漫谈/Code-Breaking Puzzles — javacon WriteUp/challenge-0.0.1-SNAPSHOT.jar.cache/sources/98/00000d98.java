package org.apache.tomcat.util.net.openssl.ciphers;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/ciphers/Authentication.class */
public enum Authentication {
    RSA,
    DSS,
    aNULL,
    DH,
    ECDH,
    KRB5,
    ECDSA,
    PSK,
    GOST94,
    GOST01,
    FZA,
    SRP,
    ANY
}