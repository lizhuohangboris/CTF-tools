package org.apache.tomcat.util.net.openssl.ciphers;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/ciphers/KeyExchange.class */
enum KeyExchange {
    EECDH,
    RSA,
    DHr,
    DHd,
    EDH,
    PSK,
    FZA,
    KRB5,
    ECDHr,
    ECDHe,
    GOST,
    SRP,
    RSAPSK,
    ECDHEPSK,
    DHEPSK,
    ANY
}