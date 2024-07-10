package org.apache.tomcat.util.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSessionContext;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/AbstractJsseEndpoint.class */
public abstract class AbstractJsseEndpoint<S, U> extends AbstractEndpoint<S, U> {
    private String sslImplementationName = null;
    private int sniParseLimit = 65536;
    private SSLImplementation sslImplementation = null;

    protected abstract NetworkChannel getServerSocket();

    public String getSslImplementationName() {
        return this.sslImplementationName;
    }

    public void setSslImplementationName(String s) {
        this.sslImplementationName = s;
    }

    public SSLImplementation getSslImplementation() {
        return this.sslImplementation;
    }

    public int getSniParseLimit() {
        return this.sniParseLimit;
    }

    public void setSniParseLimit(int sniParseLimit) {
        this.sniParseLimit = sniParseLimit;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SSLHostConfig.Type getSslConfigType() {
        if (OpenSSLImplementation.class.getName().equals(this.sslImplementationName)) {
            return SSLHostConfig.Type.EITHER;
        }
        return SSLHostConfig.Type.JSSE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initialiseSsl() throws Exception {
        if (isSSLEnabled()) {
            this.sslImplementation = SSLImplementation.getInstance(getSslImplementationName());
            for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                sslHostConfig.setConfigType(getSslConfigType());
                createSSLContext(sslHostConfig);
            }
            if (this.sslHostConfigs.get(getDefaultSSLHostConfigName()) == null) {
                throw new IllegalArgumentException(sm.getString("endpoint.noSslHostConfig", getDefaultSSLHostConfigName(), getName()));
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void createSSLContext(SSLHostConfig sslHostConfig) throws IllegalArgumentException {
        boolean firstCertificate = true;
        for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            SSLUtil sslUtil = this.sslImplementation.getSSLUtil(certificate);
            if (firstCertificate) {
                firstCertificate = false;
                sslHostConfig.setEnabledProtocols(sslUtil.getEnabledProtocols());
                sslHostConfig.setEnabledCiphers(sslUtil.getEnabledCiphers());
            }
            try {
                SSLContext sslContext = sslUtil.createSSLContext(this.negotiableProtocols);
                sslContext.init(sslUtil.getKeyManagers(), sslUtil.getTrustManagers(), null);
                SSLSessionContext sessionContext = sslContext.getServerSessionContext();
                if (sessionContext != null) {
                    sslUtil.configureSessionContext(sessionContext);
                }
                certificate.setSslContext(sslContext);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroySsl() throws Exception {
        if (isSSLEnabled()) {
            for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                releaseSSLContext(sslHostConfig);
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void releaseSSLContext(SSLHostConfig sslHostConfig) {
        SSLContext sslContext;
        for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            if (certificate.getSslContext() != null && (sslContext = certificate.getSslContext()) != null) {
                sslContext.destroy();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SSLEngine createSSLEngine(String sniHostName, List<Cipher> clientRequestedCiphers, List<String> clientRequestedApplicationProtocols) {
        SSLHostConfig sslHostConfig = getSSLHostConfig(sniHostName);
        SSLHostConfigCertificate certificate = selectCertificate(sslHostConfig, clientRequestedCiphers);
        SSLContext sslContext = certificate.getSslContext();
        if (sslContext == null) {
            throw new IllegalStateException(sm.getString("endpoint.jsse.noSslContext", sniHostName));
        }
        SSLEngine engine = sslContext.createSSLEngine();
        switch (sslHostConfig.getCertificateVerification()) {
            case NONE:
                engine.setNeedClientAuth(false);
                engine.setWantClientAuth(false);
                break;
            case OPTIONAL:
            case OPTIONAL_NO_CA:
                engine.setWantClientAuth(true);
                break;
            case REQUIRED:
                engine.setNeedClientAuth(true);
                break;
        }
        engine.setUseClientMode(false);
        engine.setEnabledCipherSuites(sslHostConfig.getEnabledCiphers());
        engine.setEnabledProtocols(sslHostConfig.getEnabledProtocols());
        SSLParameters sslParameters = engine.getSSLParameters();
        sslParameters.setUseCipherSuitesOrder(sslHostConfig.getHonorCipherOrder());
        if (JreCompat.isJre9Available() && clientRequestedApplicationProtocols != null && clientRequestedApplicationProtocols.size() > 0 && this.negotiableProtocols.size() > 0) {
            List<String> commonProtocols = new ArrayList<>();
            commonProtocols.addAll(this.negotiableProtocols);
            commonProtocols.retainAll(clientRequestedApplicationProtocols);
            if (commonProtocols.size() > 0) {
                String[] commonProtocolsArray = (String[]) commonProtocols.toArray(new String[commonProtocols.size()]);
                JreCompat.getInstance().setApplicationProtocols(sslParameters, commonProtocolsArray);
            }
        }
        engine.setSSLParameters(sslParameters);
        return engine;
    }

    private SSLHostConfigCertificate selectCertificate(SSLHostConfig sslHostConfig, List<Cipher> clientCiphers) {
        Set<SSLHostConfigCertificate> certificates = sslHostConfig.getCertificates(true);
        if (certificates.size() == 1) {
            return certificates.iterator().next();
        }
        Collection<? extends Cipher> serverCiphers = sslHostConfig.getCipherList();
        List<Cipher> candidateCiphers = new ArrayList<>();
        if (sslHostConfig.getHonorCipherOrder()) {
            candidateCiphers.addAll(serverCiphers);
            candidateCiphers.retainAll(clientCiphers);
        } else {
            candidateCiphers.addAll(clientCiphers);
            candidateCiphers.retainAll(serverCiphers);
        }
        for (Cipher candidate : candidateCiphers) {
            for (SSLHostConfigCertificate certificate : certificates) {
                if (certificate.getType().isCompatibleWith(candidate.getAu())) {
                    return certificate;
                }
            }
        }
        return certificates.iterator().next();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean isAlpnSupported() {
        if (!isSSLEnabled()) {
            return false;
        }
        try {
            SSLImplementation sslImplementation = SSLImplementation.getInstance(getSslImplementationName());
            return sslImplementation.isAlpnSupported();
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void unbind() throws Exception {
        for (SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
            for (SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
                certificate.setSslContext(null);
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected final InetSocketAddress getLocalAddress() throws IOException {
        NetworkChannel serverSock = getServerSocket();
        if (serverSock == null) {
            return null;
        }
        SocketAddress sa = serverSock.getLocalAddress();
        if (sa instanceof InetSocketAddress) {
            return (InetSocketAddress) sa;
        }
        return null;
    }
}