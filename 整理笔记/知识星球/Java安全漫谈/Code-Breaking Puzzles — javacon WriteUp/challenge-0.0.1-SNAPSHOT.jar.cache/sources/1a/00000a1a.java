package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeProcessorExternal;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/AbstractHttp11Protocol.class */
public abstract class AbstractHttp11Protocol<S> extends AbstractProtocol<S> {
    protected static final StringManager sm = StringManager.getManager(AbstractHttp11Protocol.class);
    private final CompressionConfig compressionConfig;
    private String relaxedPathChars;
    private String relaxedQueryChars;
    private boolean allowHostHeaderMismatch;
    private boolean rejectIllegalHeaderName;
    private int maxSavePostSize;
    private int maxHttpHeaderSize;
    private int connectionUploadTimeout;
    private boolean disableUploadTimeout;
    private Pattern restrictedUserAgents;
    private String server;
    private boolean serverRemoveAppProvidedValues;
    private int maxTrailerSize;
    private int maxExtensionSize;
    private int maxSwallowSize;
    private boolean secure;
    private Set<String> allowedTrailerHeaders;
    private final List<UpgradeProtocol> upgradeProtocols;
    private final Map<String, UpgradeProtocol> httpUpgradeProtocols;
    private final Map<String, UpgradeProtocol> negotiatedProtocols;
    private SSLHostConfig defaultSSLHostConfig;

    public AbstractHttp11Protocol(AbstractEndpoint<S, ?> endpoint) {
        super(endpoint);
        this.compressionConfig = new CompressionConfig();
        this.relaxedPathChars = null;
        this.relaxedQueryChars = null;
        this.allowHostHeaderMismatch = false;
        this.rejectIllegalHeaderName = true;
        this.maxSavePostSize = 4096;
        this.maxHttpHeaderSize = 8192;
        this.connectionUploadTimeout = 300000;
        this.disableUploadTimeout = true;
        this.restrictedUserAgents = null;
        this.serverRemoveAppProvidedValues = false;
        this.maxTrailerSize = 8192;
        this.maxExtensionSize = 8192;
        this.maxSwallowSize = 2097152;
        this.allowedTrailerHeaders = Collections.newSetFromMap(new ConcurrentHashMap());
        this.upgradeProtocols = new ArrayList();
        this.httpUpgradeProtocols = new HashMap();
        this.negotiatedProtocols = new HashMap();
        this.defaultSSLHostConfig = null;
        setConnectionTimeout(Constants.DEFAULT_CONNECTION_TIMEOUT);
        AbstractProtocol.ConnectionHandler<S> cHandler = new AbstractProtocol.ConnectionHandler<>(this);
        setHandler(cHandler);
        getEndpoint().setHandler(cHandler);
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public void init() throws Exception {
        for (UpgradeProtocol upgradeProtocol : this.upgradeProtocols) {
            configureUpgradeProtocol(upgradeProtocol);
        }
        super.init();
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getProtocolName() {
        return "Http";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.coyote.AbstractProtocol
    public AbstractEndpoint<S, ?> getEndpoint() {
        return super.getEndpoint();
    }

    public String getRelaxedPathChars() {
        return this.relaxedPathChars;
    }

    public void setRelaxedPathChars(String relaxedPathChars) {
        this.relaxedPathChars = relaxedPathChars;
    }

    public String getRelaxedQueryChars() {
        return this.relaxedQueryChars;
    }

    public void setRelaxedQueryChars(String relaxedQueryChars) {
        this.relaxedQueryChars = relaxedQueryChars;
    }

    public boolean getAllowHostHeaderMismatch() {
        return this.allowHostHeaderMismatch;
    }

    public void setAllowHostHeaderMismatch(boolean allowHostHeaderMismatch) {
        this.allowHostHeaderMismatch = allowHostHeaderMismatch;
    }

    public boolean getRejectIllegalHeaderName() {
        return this.rejectIllegalHeaderName;
    }

    public void setRejectIllegalHeaderName(boolean rejectIllegalHeaderName) {
        this.rejectIllegalHeaderName = rejectIllegalHeaderName;
    }

    public int getMaxSavePostSize() {
        return this.maxSavePostSize;
    }

    public void setMaxSavePostSize(int maxSavePostSize) {
        this.maxSavePostSize = maxSavePostSize;
    }

    public int getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(int valueI) {
        this.maxHttpHeaderSize = valueI;
    }

    public int getConnectionUploadTimeout() {
        return this.connectionUploadTimeout;
    }

    public void setConnectionUploadTimeout(int timeout) {
        this.connectionUploadTimeout = timeout;
    }

    public boolean getDisableUploadTimeout() {
        return this.disableUploadTimeout;
    }

    public void setDisableUploadTimeout(boolean isDisabled) {
        this.disableUploadTimeout = isDisabled;
    }

    public void setCompression(String compression) {
        this.compressionConfig.setCompression(compression);
    }

    public String getCompression() {
        return this.compressionConfig.getCompression();
    }

    protected int getCompressionLevel() {
        return this.compressionConfig.getCompressionLevel();
    }

    public String getNoCompressionUserAgents() {
        return this.compressionConfig.getNoCompressionUserAgents();
    }

    protected Pattern getNoCompressionUserAgentsPattern() {
        return this.compressionConfig.getNoCompressionUserAgentsPattern();
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.compressionConfig.setNoCompressionUserAgents(noCompressionUserAgents);
    }

    public String getCompressibleMimeType() {
        return this.compressionConfig.getCompressibleMimeType();
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressionConfig.setCompressibleMimeType(valueS);
    }

    public String[] getCompressibleMimeTypes() {
        return this.compressionConfig.getCompressibleMimeTypes();
    }

    public int getCompressionMinSize() {
        return this.compressionConfig.getCompressionMinSize();
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionConfig.setCompressionMinSize(compressionMinSize);
    }

    public boolean useCompression(Request request, Response response) {
        return this.compressionConfig.useCompression(request, response);
    }

    public String getRestrictedUserAgents() {
        if (this.restrictedUserAgents == null) {
            return null;
        }
        return this.restrictedUserAgents.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pattern getRestrictedUserAgentsPattern() {
        return this.restrictedUserAgents;
    }

    public void setRestrictedUserAgents(String restrictedUserAgents) {
        if (restrictedUserAgents == null || restrictedUserAgents.length() == 0) {
            this.restrictedUserAgents = null;
        } else {
            this.restrictedUserAgents = Pattern.compile(restrictedUserAgents);
        }
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean getServerRemoveAppProvidedValues() {
        return this.serverRemoveAppProvidedValues;
    }

    public void setServerRemoveAppProvidedValues(boolean serverRemoveAppProvidedValues) {
        this.serverRemoveAppProvidedValues = serverRemoveAppProvidedValues;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxExtensionSize() {
        return this.maxExtensionSize;
    }

    public void setMaxExtensionSize(int maxExtensionSize) {
        this.maxExtensionSize = maxExtensionSize;
    }

    public int getMaxSwallowSize() {
        return this.maxSwallowSize;
    }

    public void setMaxSwallowSize(int maxSwallowSize) {
        this.maxSwallowSize = maxSwallowSize;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public void setSecure(boolean b) {
        this.secure = b;
    }

    public void setAllowedTrailerHeaders(String commaSeparatedHeaders) {
        Set<String> toRemove = new HashSet<>();
        toRemove.addAll(this.allowedTrailerHeaders);
        if (commaSeparatedHeaders != null) {
            String[] headers = commaSeparatedHeaders.split(",");
            for (String header : headers) {
                String trimmedHeader = header.trim().toLowerCase(Locale.ENGLISH);
                if (toRemove.contains(trimmedHeader)) {
                    toRemove.remove(trimmedHeader);
                } else {
                    this.allowedTrailerHeaders.add(trimmedHeader);
                }
            }
            this.allowedTrailerHeaders.removeAll(toRemove);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Set<String> getAllowedTrailerHeadersInternal() {
        return this.allowedTrailerHeaders;
    }

    public String getAllowedTrailerHeaders() {
        List<String> copy = new ArrayList<>(this.allowedTrailerHeaders.size());
        copy.addAll(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    public void addAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    public void removeAllowedTrailerHeader(String header) {
        if (header != null) {
            this.allowedTrailerHeaders.remove(header.trim().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        this.upgradeProtocols.add(upgradeProtocol);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public UpgradeProtocol[] findUpgradeProtocols() {
        return (UpgradeProtocol[]) this.upgradeProtocols.toArray(new UpgradeProtocol[0]);
    }

    private void configureUpgradeProtocol(UpgradeProtocol upgradeProtocol) {
        String httpUpgradeName = upgradeProtocol.getHttpUpgradeName(getEndpoint().isSSLEnabled());
        boolean httpUpgradeConfigured = false;
        if (httpUpgradeName != null && httpUpgradeName.length() > 0) {
            this.httpUpgradeProtocols.put(httpUpgradeName, upgradeProtocol);
            httpUpgradeConfigured = true;
            getLog().info(sm.getString("abstractHttp11Protocol.httpUpgradeConfigured", getName(), httpUpgradeName));
        }
        String alpnName = upgradeProtocol.getAlpnName();
        if (alpnName != null && alpnName.length() > 0) {
            if (getEndpoint().isAlpnSupported()) {
                this.negotiatedProtocols.put(alpnName, upgradeProtocol);
                getEndpoint().addNegotiatedProtocol(alpnName);
                getLog().info(sm.getString("abstractHttp11Protocol.alpnConfigured", getName(), alpnName));
            } else if (!httpUpgradeConfigured) {
                getLog().error(sm.getString("abstractHttp11Protocol.alpnWithNoAlpn", upgradeProtocol.getClass().getName(), alpnName, getName()));
            }
        }
    }

    @Override // org.apache.coyote.AbstractProtocol
    public UpgradeProtocol getNegotiatedProtocol(String negotiatedName) {
        return this.negotiatedProtocols.get(negotiatedName);
    }

    @Override // org.apache.coyote.AbstractProtocol
    public UpgradeProtocol getUpgradeProtocol(String upgradedName) {
        return this.httpUpgradeProtocols.get(upgradedName);
    }

    public boolean isSSLEnabled() {
        return getEndpoint().isSSLEnabled();
    }

    public void setSSLEnabled(boolean SSLEnabled) {
        getEndpoint().setSSLEnabled(SSLEnabled);
    }

    public boolean getUseSendfile() {
        return getEndpoint().getUseSendfile();
    }

    public void setUseSendfile(boolean useSendfile) {
        getEndpoint().setUseSendfile(useSendfile);
    }

    public int getMaxKeepAliveRequests() {
        return getEndpoint().getMaxKeepAliveRequests();
    }

    public void setMaxKeepAliveRequests(int mkar) {
        getEndpoint().setMaxKeepAliveRequests(mkar);
    }

    public String getDefaultSSLHostConfigName() {
        return getEndpoint().getDefaultSSLHostConfigName();
    }

    public void setDefaultSSLHostConfigName(String defaultSSLHostConfigName) {
        getEndpoint().setDefaultSSLHostConfigName(defaultSSLHostConfigName);
        if (this.defaultSSLHostConfig != null) {
            this.defaultSSLHostConfig.setHostName(defaultSSLHostConfigName);
        }
    }

    @Override // org.apache.coyote.ProtocolHandler
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {
        getEndpoint().addSslHostConfig(sslHostConfig);
    }

    @Override // org.apache.coyote.ProtocolHandler
    public SSLHostConfig[] findSslHostConfigs() {
        return getEndpoint().findSslHostConfigs();
    }

    public void reloadSslHostConfigs() {
        getEndpoint().reloadSslHostConfigs();
    }

    public void reloadSslHostConfig(String hostName) {
        getEndpoint().reloadSslHostConfig(hostName);
    }

    private void registerDefaultSSLHostConfig() {
        if (this.defaultSSLHostConfig == null) {
            SSLHostConfig[] findSslHostConfigs = findSslHostConfigs();
            int length = findSslHostConfigs.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                SSLHostConfig sslHostConfig = findSslHostConfigs[i];
                if (!getDefaultSSLHostConfigName().equals(sslHostConfig.getHostName())) {
                    i++;
                } else {
                    this.defaultSSLHostConfig = sslHostConfig;
                    break;
                }
            }
            if (this.defaultSSLHostConfig == null) {
                this.defaultSSLHostConfig = new SSLHostConfig();
                this.defaultSSLHostConfig.setHostName(getDefaultSSLHostConfigName());
                getEndpoint().addSslHostConfig(this.defaultSSLHostConfig);
            }
        }
    }

    public String getSslEnabledProtocols() {
        registerDefaultSSLHostConfig();
        return StringUtils.join(this.defaultSSLHostConfig.getEnabledProtocols());
    }

    public void setSslEnabledProtocols(String enabledProtocols) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(enabledProtocols);
    }

    public String getSSLProtocol() {
        registerDefaultSSLHostConfig();
        return StringUtils.join(this.defaultSSLHostConfig.getEnabledProtocols());
    }

    public void setSSLProtocol(String sslProtocol) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setProtocols(sslProtocol);
    }

    public String getKeystoreFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreFile();
    }

    public void setKeystoreFile(String keystoreFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreFile(keystoreFile);
    }

    public String getSSLCertificateChainFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateChainFile();
    }

    public void setSSLCertificateChainFile(String certificateChainFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateChainFile(certificateChainFile);
    }

    public String getSSLCertificateFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateFile();
    }

    public void setSSLCertificateFile(String certificateFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateFile(certificateFile);
    }

    public String getSSLCertificateKeyFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyFile();
    }

    public void setSSLCertificateKeyFile(String certificateKeyFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyFile(certificateKeyFile);
    }

    public String getAlgorithm() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getKeyManagerAlgorithm();
    }

    public void setAlgorithm(String keyManagerAlgorithm) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setKeyManagerAlgorithm(keyManagerAlgorithm);
    }

    public String getClientAuth() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerification().toString();
    }

    public void setClientAuth(String certificateVerification) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }

    public String getSSLVerifyClient() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerification().toString();
    }

    public void setSSLVerifyClient(String certificateVerification) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerification(certificateVerification);
    }

    public int getTrustMaxCertLength() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }

    public void setTrustMaxCertLength(int certificateVerificationDepth) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }

    public int getSSLVerifyDepth() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateVerificationDepth();
    }

    public void setSSLVerifyDepth(int certificateVerificationDepth) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateVerificationDepth(certificateVerificationDepth);
    }

    public boolean getUseServerCipherSuitesOrder() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }

    public void setUseServerCipherSuitesOrder(boolean honorCipherOrder) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }

    public boolean getSSLHonorCipherOrder() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getHonorCipherOrder();
    }

    public void setSSLHonorCipherOrder(boolean honorCipherOrder) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setHonorCipherOrder(honorCipherOrder);
    }

    public String getCiphers() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }

    public void setCiphers(String ciphers) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }

    public String getSSLCipherSuite() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCiphers();
    }

    public void setSSLCipherSuite(String ciphers) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCiphers(ciphers);
    }

    public String getKeystorePass() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystorePassword();
    }

    public void setKeystorePass(String certificateKeystorePassword) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystorePassword(certificateKeystorePassword);
    }

    public String getKeyPass() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }

    public void setKeyPass(String certificateKeyPassword) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }

    public String getSSLPassword() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyPassword();
    }

    public void setSSLPassword(String certificateKeyPassword) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyPassword(certificateKeyPassword);
    }

    public String getCrlFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }

    public void setCrlFile(String certificateRevocationListFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }

    public String getSSLCARevocationFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListFile();
    }

    public void setSSLCARevocationFile(String certificateRevocationListFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListFile(certificateRevocationListFile);
    }

    public String getSSLCARevocationPath() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateRevocationListPath();
    }

    public void setSSLCARevocationPath(String certificateRevocationListPath) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateRevocationListPath(certificateRevocationListPath);
    }

    public String getKeystoreType() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreType();
    }

    public void setKeystoreType(String certificateKeystoreType) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreType(certificateKeystoreType);
    }

    public String getKeystoreProvider() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeystoreProvider();
    }

    public void setKeystoreProvider(String certificateKeystoreProvider) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeystoreProvider(certificateKeystoreProvider);
    }

    public String getKeyAlias() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCertificateKeyAlias();
    }

    public void setKeyAlias(String certificateKeyAlias) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCertificateKeyAlias(certificateKeyAlias);
    }

    public String getTruststoreAlgorithm() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreAlgorithm();
    }

    public void setTruststoreAlgorithm(String truststoreAlgorithm) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreAlgorithm(truststoreAlgorithm);
    }

    public String getTruststoreFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreFile();
    }

    public void setTruststoreFile(String truststoreFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreFile(truststoreFile);
    }

    public String getTruststorePass() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststorePassword();
    }

    public void setTruststorePass(String truststorePassword) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststorePassword(truststorePassword);
    }

    public String getTruststoreType() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreType();
    }

    public void setTruststoreType(String truststoreType) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreType(truststoreType);
    }

    public String getTruststoreProvider() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTruststoreProvider();
    }

    public void setTruststoreProvider(String truststoreProvider) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTruststoreProvider(truststoreProvider);
    }

    public String getSslProtocol() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSslProtocol();
    }

    public void setSslProtocol(String sslProtocol) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSslProtocol(sslProtocol);
    }

    public int getSessionCacheSize() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionCacheSize();
    }

    public void setSessionCacheSize(int sessionCacheSize) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionCacheSize(sessionCacheSize);
    }

    public int getSessionTimeout() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getSessionTimeout();
    }

    public void setSessionTimeout(int sessionTimeout) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setSessionTimeout(sessionTimeout);
    }

    public String getSSLCACertificatePath() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificatePath();
    }

    public void setSSLCACertificatePath(String caCertificatePath) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificatePath(caCertificatePath);
    }

    public String getSSLCACertificateFile() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getCaCertificateFile();
    }

    public void setSSLCACertificateFile(String caCertificateFile) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setCaCertificateFile(caCertificateFile);
    }

    public boolean getSSLDisableCompression() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableCompression();
    }

    public void setSSLDisableCompression(boolean disableCompression) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableCompression(disableCompression);
    }

    public boolean getSSLDisableSessionTickets() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getDisableSessionTickets();
    }

    public void setSSLDisableSessionTickets(boolean disableSessionTickets) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setDisableSessionTickets(disableSessionTickets);
    }

    public String getTrustManagerClassName() {
        registerDefaultSSLHostConfig();
        return this.defaultSSLHostConfig.getTrustManagerClassName();
    }

    public void setTrustManagerClassName(String trustManagerClassName) {
        registerDefaultSSLHostConfig();
        this.defaultSSLHostConfig.setTrustManagerClassName(trustManagerClassName);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createProcessor() {
        Http11Processor processor = new Http11Processor(this, this.adapter);
        return processor;
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Processor createUpgradeProcessor(SocketWrapperBase<?> socket, UpgradeToken upgradeToken) {
        HttpUpgradeHandler httpUpgradeHandler = upgradeToken.getHttpUpgradeHandler();
        if (httpUpgradeHandler instanceof InternalHttpUpgradeHandler) {
            return new UpgradeProcessorInternal(socket, upgradeToken);
        }
        return new UpgradeProcessorExternal(socket, upgradeToken);
    }
}