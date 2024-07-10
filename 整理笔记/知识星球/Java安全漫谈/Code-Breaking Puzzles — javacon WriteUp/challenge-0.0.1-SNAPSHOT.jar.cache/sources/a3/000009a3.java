package org.apache.catalina.valves;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/SSLValve.class */
public class SSLValve extends ValveBase {
    private static final Log log = LogFactory.getLog(SSLValve.class);
    private String sslClientCertHeader;
    private String sslCipherHeader;
    private String sslSessionIdHeader;
    private String sslCipherUserKeySizeHeader;

    public SSLValve() {
        super(true);
        this.sslClientCertHeader = "ssl_client_cert";
        this.sslCipherHeader = "ssl_cipher";
        this.sslSessionIdHeader = "ssl_session_id";
        this.sslCipherUserKeySizeHeader = "ssl_cipher_usekeysize";
    }

    public String getSslClientCertHeader() {
        return this.sslClientCertHeader;
    }

    public void setSslClientCertHeader(String sslClientCertHeader) {
        this.sslClientCertHeader = sslClientCertHeader;
    }

    public String getSslCipherHeader() {
        return this.sslCipherHeader;
    }

    public void setSslCipherHeader(String sslCipherHeader) {
        this.sslCipherHeader = sslCipherHeader;
    }

    public String getSslSessionIdHeader() {
        return this.sslSessionIdHeader;
    }

    public void setSslSessionIdHeader(String sslSessionIdHeader) {
        this.sslSessionIdHeader = sslSessionIdHeader;
    }

    public String getSslCipherUserKeySizeHeader() {
        return this.sslCipherUserKeySizeHeader;
    }

    public void setSslCipherUserKeySizeHeader(String sslCipherUserKeySizeHeader) {
        this.sslCipherUserKeySizeHeader = sslCipherUserKeySizeHeader;
    }

    public String mygetHeader(Request request, String header) {
        String strcert0 = request.getHeader(header);
        if (strcert0 == null || "(null)".equals(strcert0)) {
            return null;
        }
        return strcert0;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        CertificateFactory cf;
        String headerValue = mygetHeader(request, this.sslClientCertHeader);
        if (headerValue != null) {
            String headerValue2 = headerValue.trim();
            if (headerValue2.length() > 27) {
                String body = headerValue2.substring(27);
                String strcerts = "-----BEGIN CERTIFICATE-----\n".concat(body);
                ByteArrayInputStream bais = new ByteArrayInputStream(strcerts.getBytes(StandardCharsets.ISO_8859_1));
                X509Certificate[] jsseCerts = null;
                String providerName = (String) request.getConnector().getProperty("clientCertProvider");
                try {
                    if (providerName == null) {
                        cf = CertificateFactory.getInstance("X.509");
                    } else {
                        cf = CertificateFactory.getInstance("X.509", providerName);
                    }
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(bais);
                    jsseCerts = new X509Certificate[]{cert};
                } catch (NoSuchProviderException e) {
                    log.error(sm.getString("sslValve.invalidProvider", providerName), e);
                } catch (CertificateException e2) {
                    log.warn(sm.getString("sslValve.certError", strcerts), e2);
                }
                request.setAttribute("javax.servlet.request.X509Certificate", jsseCerts);
            }
        }
        String headerValue3 = mygetHeader(request, this.sslCipherHeader);
        if (headerValue3 != null) {
            request.setAttribute("javax.servlet.request.cipher_suite", headerValue3);
        }
        String headerValue4 = mygetHeader(request, this.sslSessionIdHeader);
        if (headerValue4 != null) {
            request.setAttribute("javax.servlet.request.ssl_session_id", headerValue4);
        }
        String headerValue5 = mygetHeader(request, this.sslCipherUserKeySizeHeader);
        if (headerValue5 != null) {
            request.setAttribute("javax.servlet.request.key_size", Integer.valueOf(headerValue5));
        }
        getNext().invoke(request, response);
    }
}