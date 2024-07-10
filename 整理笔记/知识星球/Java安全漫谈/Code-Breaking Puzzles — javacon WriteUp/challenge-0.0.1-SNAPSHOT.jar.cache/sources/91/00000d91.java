package org.apache.tomcat.util.net.openssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.util.net.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/openssl/OpenSSLProtocols.class */
public class OpenSSLProtocols {
    private List<String> openSSLProtocols = new ArrayList();

    public OpenSSLProtocols(String preferredJSSEProto) {
        Collections.addAll(this.openSSLProtocols, Constants.SSL_PROTO_TLSv1_2, Constants.SSL_PROTO_TLSv1_1, Constants.SSL_PROTO_TLSv1, Constants.SSL_PROTO_SSLv3, Constants.SSL_PROTO_SSLv2);
        if (this.openSSLProtocols.contains(preferredJSSEProto)) {
            this.openSSLProtocols.remove(preferredJSSEProto);
            this.openSSLProtocols.add(0, preferredJSSEProto);
        }
    }

    public String[] getProtocols() {
        return (String[]) this.openSSLProtocols.toArray(new String[this.openSSLProtocols.size()]);
    }
}