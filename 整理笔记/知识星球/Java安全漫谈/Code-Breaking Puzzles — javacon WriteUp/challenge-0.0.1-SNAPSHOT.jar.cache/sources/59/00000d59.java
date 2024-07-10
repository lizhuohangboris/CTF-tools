package org.apache.tomcat.util.net;

import java.io.IOException;
import java.security.cert.X509Certificate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SSLSupport.class */
public interface SSLSupport {
    public static final String CIPHER_SUITE_KEY = "javax.servlet.request.cipher_suite";
    public static final String KEY_SIZE_KEY = "javax.servlet.request.key_size";
    public static final String CERTIFICATE_KEY = "javax.servlet.request.X509Certificate";
    public static final String SESSION_ID_KEY = "javax.servlet.request.ssl_session_id";
    public static final String SESSION_MGR = "javax.servlet.request.ssl_session_mgr";
    public static final String PROTOCOL_VERSION_KEY = "org.apache.tomcat.util.net.secure_protocol_version";

    String getCipherSuite() throws IOException;

    X509Certificate[] getPeerCertificateChain() throws IOException;

    Integer getKeySize() throws IOException;

    String getSessionId() throws IOException;

    String getProtocol() throws IOException;
}