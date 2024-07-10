package org.apache.tomcat.util.net.openssl.ciphers;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/ciphers/Encryption.class */
enum Encryption {
    AES128,
    AES128CCM,
    AES128CCM8,
    AES128GCM,
    AES256,
    AES256CCM,
    AES256CCM8,
    AES256GCM,
    ARIA128GCM,
    ARIA256GCM,
    CAMELLIA256,
    CAMELLIA128,
    CHACHA20POLY1305,
    TRIPLE_DES,
    DES,
    IDEA,
    eGOST2814789CNT,
    SEED,
    FZA,
    RC4,
    RC2,
    eNULL
}