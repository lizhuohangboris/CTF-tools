package org.apache.coyote.http2;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.coyote.Adapter;
import org.apache.coyote.CompressionConfig;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeProcessorInternal;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.net.SocketWrapperBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http2/Http2Protocol.class */
public class Http2Protocol implements UpgradeProtocol {
    static final long DEFAULT_READ_TIMEOUT = 10000;
    static final long DEFAULT_KEEP_ALIVE_TIMEOUT = -1;
    static final long DEFAULT_WRITE_TIMEOUT = 10000;
    static final long DEFAULT_MAX_CONCURRENT_STREAMS = 200;
    static final int DEFAULT_MAX_CONCURRENT_STREAM_EXECUTION = 20;
    static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
    private static final String HTTP_UPGRADE_NAME = "h2c";
    private static final String ALPN_NAME = "h2";
    private static final byte[] ALPN_IDENTIFIER = ALPN_NAME.getBytes(StandardCharsets.UTF_8);
    private long readTimeout = AbstractComponentTracker.LINGERING_TIMEOUT;
    private long keepAliveTimeout = -1;
    private long writeTimeout = AbstractComponentTracker.LINGERING_TIMEOUT;
    private long maxConcurrentStreams = DEFAULT_MAX_CONCURRENT_STREAMS;
    private int maxConcurrentStreamExecution = 20;
    private int initialWindowSize = 65535;
    private Set<String> allowedTrailerHeaders = Collections.newSetFromMap(new ConcurrentHashMap());
    private int maxHeaderCount = 100;
    private int maxHeaderSize = 8192;
    private int maxTrailerCount = 100;
    private int maxTrailerSize = 8192;
    private boolean initiatePingDisabled = false;
    private boolean useSendfile = true;
    private final CompressionConfig compressionConfig = new CompressionConfig();

    @Override // org.apache.coyote.UpgradeProtocol
    public String getHttpUpgradeName(boolean isSSLEnabled) {
        if (isSSLEnabled) {
            return null;
        }
        return HTTP_UPGRADE_NAME;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public byte[] getAlpnIdentifier() {
        return ALPN_IDENTIFIER;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public String getAlpnName() {
        return ALPN_NAME;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public Processor getProcessor(SocketWrapperBase<?> socketWrapper, Adapter adapter) {
        UpgradeProcessorInternal processor = new UpgradeProcessorInternal(socketWrapper, new UpgradeToken(getInternalUpgradeHandler(socketWrapper, adapter, null), null, null));
        return processor;
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> socketWrapper, Adapter adapter, Request coyoteRequest) {
        return socketWrapper.hasAsyncIO() ? new Http2AsyncUpgradeHandler(this, adapter, coyoteRequest) : new Http2UpgradeHandler(this, adapter, coyoteRequest);
    }

    @Override // org.apache.coyote.UpgradeProtocol
    public boolean accept(Request request) {
        boolean found;
        Enumeration<String> settings = request.getMimeHeaders().values("HTTP2-Settings");
        int count = 0;
        while (settings.hasMoreElements()) {
            count++;
            settings.nextElement();
        }
        if (count != 1) {
            return false;
        }
        Enumeration<String> connection = request.getMimeHeaders().values("Connection");
        boolean z = false;
        while (true) {
            found = z;
            if (!connection.hasMoreElements() || found) {
                break;
            }
            z = connection.nextElement().contains("HTTP2-Settings");
        }
        return found;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getMaxConcurrentStreams() {
        return this.maxConcurrentStreams;
    }

    public void setMaxConcurrentStreams(long maxConcurrentStreams) {
        this.maxConcurrentStreams = maxConcurrentStreams;
    }

    public int getMaxConcurrentStreamExecution() {
        return this.maxConcurrentStreamExecution;
    }

    public void setMaxConcurrentStreamExecution(int maxConcurrentStreamExecution) {
        this.maxConcurrentStreamExecution = maxConcurrentStreamExecution;
    }

    public int getInitialWindowSize() {
        return this.initialWindowSize;
    }

    public void setInitialWindowSize(int initialWindowSize) {
        this.initialWindowSize = initialWindowSize;
    }

    public boolean getUseSendfile() {
        return this.useSendfile;
    }

    public void setUseSendfile(boolean useSendfile) {
        this.useSendfile = useSendfile;
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

    public String getAllowedTrailerHeaders() {
        List<String> copy = new ArrayList<>(this.allowedTrailerHeaders.size());
        copy.addAll(this.allowedTrailerHeaders);
        return StringUtils.join(copy);
    }

    public boolean isTrailerHeaderAllowed(String headerName) {
        return this.allowedTrailerHeaders.contains(headerName);
    }

    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxHeaderSize() {
        return this.maxHeaderSize;
    }

    public void setMaxTrailerCount(int maxTrailerCount) {
        this.maxTrailerCount = maxTrailerCount;
    }

    public int getMaxTrailerCount() {
        return this.maxTrailerCount;
    }

    public void setMaxTrailerSize(int maxTrailerSize) {
        this.maxTrailerSize = maxTrailerSize;
    }

    public int getMaxTrailerSize() {
        return this.maxTrailerSize;
    }

    public void setInitiatePingDisabled(boolean initiatePingDisabled) {
        this.initiatePingDisabled = initiatePingDisabled;
    }

    public boolean getInitiatePingDisabled() {
        return this.initiatePingDisabled;
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
}