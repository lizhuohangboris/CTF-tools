package org.springframework.boot.web.server;

import java.security.KeyStore;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/SslStoreProvider.class */
public interface SslStoreProvider {
    KeyStore getKeyStore() throws Exception;

    KeyStore getTrustStore() throws Exception;
}