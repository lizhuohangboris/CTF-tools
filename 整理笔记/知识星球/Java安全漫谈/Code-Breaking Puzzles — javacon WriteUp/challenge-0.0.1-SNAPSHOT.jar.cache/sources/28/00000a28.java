package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Adapter;
import org.apache.coyote.ErrorState;
import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.filters.BufferedInputFilter;
import org.apache.coyote.http11.filters.ChunkedInputFilter;
import org.apache.coyote.http11.filters.ChunkedOutputFilter;
import org.apache.coyote.http11.filters.GzipOutputFilter;
import org.apache.coyote.http11.filters.IdentityInputFilter;
import org.apache.coyote.http11.filters.IdentityOutputFilter;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.coyote.http11.filters.VoidInputFilter;
import org.apache.coyote.http11.filters.VoidOutputFilter;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileKeepAliveState;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11Processor.class */
public class Http11Processor extends AbstractProcessor {
    private static final Log log = LogFactory.getLog(Http11Processor.class);
    private static final StringManager sm = StringManager.getManager(Http11Processor.class);
    private final AbstractHttp11Protocol<?> protocol;
    private final Http11InputBuffer inputBuffer;
    private final Http11OutputBuffer outputBuffer;
    private final HttpParser httpParser;
    private int pluggableFilterIndex;
    private volatile boolean keepAlive;
    private boolean openSocket;
    private boolean readComplete;
    private boolean http11;
    private boolean http09;
    private boolean contentDelimitation;
    private UpgradeToken upgradeToken;
    private SendfileDataBase sendfileData;

    public Http11Processor(AbstractHttp11Protocol<?> protocol, Adapter adapter) {
        super(adapter);
        this.pluggableFilterIndex = Integer.MAX_VALUE;
        this.keepAlive = true;
        this.openSocket = false;
        this.readComplete = true;
        this.http11 = true;
        this.http09 = false;
        this.contentDelimitation = true;
        this.upgradeToken = null;
        this.sendfileData = null;
        this.protocol = protocol;
        this.httpParser = new HttpParser(protocol.getRelaxedPathChars(), protocol.getRelaxedQueryChars());
        this.inputBuffer = new Http11InputBuffer(this.request, protocol.getMaxHttpHeaderSize(), protocol.getRejectIllegalHeaderName(), this.httpParser);
        this.request.setInputBuffer(this.inputBuffer);
        this.outputBuffer = new Http11OutputBuffer(this.response, protocol.getMaxHttpHeaderSize());
        this.response.setOutputBuffer(this.outputBuffer);
        this.inputBuffer.addFilter(new IdentityInputFilter(protocol.getMaxSwallowSize()));
        this.outputBuffer.addFilter(new IdentityOutputFilter());
        this.inputBuffer.addFilter(new ChunkedInputFilter(protocol.getMaxTrailerSize(), protocol.getAllowedTrailerHeadersInternal(), protocol.getMaxExtensionSize(), protocol.getMaxSwallowSize()));
        this.outputBuffer.addFilter(new ChunkedOutputFilter());
        this.inputBuffer.addFilter(new VoidInputFilter());
        this.outputBuffer.addFilter(new VoidOutputFilter());
        this.inputBuffer.addFilter(new BufferedInputFilter());
        this.outputBuffer.addFilter(new GzipOutputFilter());
        this.pluggableFilterIndex = this.inputBuffer.getFilters().length;
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x0069, code lost:
        continue;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int findBytes(org.apache.tomcat.util.buf.ByteChunk r4, byte[] r5) {
        /*
            r0 = r5
            r1 = 0
            r0 = r0[r1]
            r6 = r0
            r0 = r4
            byte[] r0 = r0.getBuffer()
            r7 = r0
            r0 = r4
            int r0 = r0.getStart()
            r8 = r0
            r0 = r4
            int r0 = r0.getEnd()
            r9 = r0
            r0 = r5
            int r0 = r0.length
            r10 = r0
            r0 = r8
            r11 = r0
        L1d:
            r0 = r11
            r1 = r9
            r2 = r10
            int r1 = r1 - r2
            if (r0 > r1) goto L6f
            r0 = r7
            r1 = r11
            r0 = r0[r1]
            int r0 = org.apache.tomcat.util.buf.Ascii.toLower(r0)
            r1 = r6
            if (r0 == r1) goto L35
            goto L69
        L35:
            r0 = r11
            r1 = 1
            int r0 = r0 + r1
            r12 = r0
            r0 = 1
            r13 = r0
        L3e:
            r0 = r13
            r1 = r10
            if (r0 >= r1) goto L69
            r0 = r7
            r1 = r12
            int r12 = r12 + 1
            r0 = r0[r1]
            int r0 = org.apache.tomcat.util.buf.Ascii.toLower(r0)
            r1 = r5
            r2 = r13
            int r13 = r13 + 1
            r1 = r1[r2]
            if (r0 == r1) goto L5c
            goto L69
        L5c:
            r0 = r13
            r1 = r10
            if (r0 != r1) goto L3e
            r0 = r11
            r1 = r8
            int r0 = r0 - r1
            return r0
        L69:
            int r11 = r11 + 1
            goto L1d
        L6f:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http11.Http11Processor.findBytes(org.apache.tomcat.util.buf.ByteChunk, byte[]):int");
    }

    private static boolean statusDropsConnection(int status) {
        return status == 400 || status == 408 || status == 411 || status == 413 || status == 414 || status == 500 || status == 503 || status == 501;
    }

    private void addInputFilter(InputFilter[] inputFilters, String encodingName) {
        String encodingName2 = encodingName.trim().toLowerCase(Locale.ENGLISH);
        if (!encodingName2.equals(JmxUtils.IDENTITY_OBJECT_NAME_KEY)) {
            if (encodingName2.equals(Constants.CHUNKED)) {
                this.inputBuffer.addActiveFilter(inputFilters[1]);
                this.contentDelimitation = true;
                return;
            }
            for (int i = this.pluggableFilterIndex; i < inputFilters.length; i++) {
                if (inputFilters[i].getEncodingName().toString().equals(encodingName2)) {
                    this.inputBuffer.addActiveFilter(inputFilters[i]);
                    return;
                }
            }
            this.response.setStatus(501);
            setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("http11processor.request.prepare") + " Unsupported transfer encoding [" + encodingName2 + "]");
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public AbstractEndpoint.Handler.SocketState service(SocketWrapperBase<?> socketWrapper) throws IOException {
        SendfileState sendfileState;
        boolean foundUpgrade;
        String requestedProtocol;
        UpgradeProtocol upgradeProtocol;
        RequestInfo rp = this.request.getRequestProcessor();
        rp.setStage(1);
        setSocketWrapper(socketWrapper);
        this.inputBuffer.init(socketWrapper);
        this.outputBuffer.init(socketWrapper);
        this.keepAlive = true;
        this.openSocket = false;
        this.readComplete = true;
        boolean keptAlive = false;
        SendfileState sendfileState2 = SendfileState.DONE;
        while (true) {
            sendfileState = sendfileState2;
            if (!getErrorState().isError() && this.keepAlive && !isAsync() && this.upgradeToken == null && sendfileState == SendfileState.DONE && !this.protocol.isPaused()) {
                try {
                    if (!this.inputBuffer.parseRequestLine(keptAlive, this.protocol.getConnectionTimeout(), this.protocol.getKeepAliveTimeout())) {
                        if (this.inputBuffer.getParsingRequestLinePhase() == -1) {
                            return AbstractEndpoint.Handler.SocketState.UPGRADING;
                        }
                        if (handleIncompleteRequestLineRead()) {
                        }
                    }
                    if (this.protocol.isPaused()) {
                        this.response.setStatus(503);
                        setErrorState(ErrorState.CLOSE_CLEAN, null);
                    } else {
                        keptAlive = true;
                        this.request.getMimeHeaders().setLimit(this.protocol.getMaxHeaderCount());
                        if (!this.inputBuffer.parseHeaders()) {
                            this.openSocket = true;
                            this.readComplete = false;
                        } else if (!this.protocol.getDisableUploadTimeout()) {
                            socketWrapper.setReadTimeout(this.protocol.getConnectionUploadTimeout());
                        }
                    }
                } catch (IOException e) {
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("http11processor.header.parse"), e);
                    }
                    setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    UserDataHelper.Mode logMode = this.userDataHelper.getNextMode();
                    if (logMode != null) {
                        String message = sm.getString("http11processor.header.parse");
                        switch (logMode) {
                            case INFO_THEN_DEBUG:
                                message = message + sm.getString("http11processor.fallToDebug");
                            case INFO:
                                log.info(message, t);
                                break;
                            case DEBUG:
                                log.debug(message, t);
                                break;
                        }
                    }
                    this.response.setStatus(400);
                    setErrorState(ErrorState.CLOSE_CLEAN, t);
                }
                Enumeration<String> connectionValues = this.request.getMimeHeaders().values("Connection");
                boolean z = false;
                while (true) {
                    foundUpgrade = z;
                    if (connectionValues.hasMoreElements() && !foundUpgrade) {
                        z = connectionValues.nextElement().toLowerCase(Locale.ENGLISH).contains(org.apache.tomcat.websocket.Constants.CONNECTION_HEADER_VALUE);
                    }
                }
                if (foundUpgrade && (upgradeProtocol = this.protocol.getUpgradeProtocol((requestedProtocol = this.request.getHeader("Upgrade")))) != null && upgradeProtocol.accept(this.request)) {
                    this.response.setStatus(101);
                    this.response.setHeader("Connection", "Upgrade");
                    this.response.setHeader("Upgrade", requestedProtocol);
                    action(ActionCode.CLOSE, null);
                    getAdapter().log(this.request, this.response, 0L);
                    InternalHttpUpgradeHandler upgradeHandler = upgradeProtocol.getInternalUpgradeHandler(socketWrapper, getAdapter(), cloneRequest(this.request));
                    UpgradeToken upgradeToken = new UpgradeToken(upgradeHandler, null, null);
                    action(ActionCode.UPGRADE, upgradeToken);
                    return AbstractEndpoint.Handler.SocketState.UPGRADING;
                }
                if (getErrorState().isIoAllowed()) {
                    rp.setStage(2);
                    try {
                        prepareRequest();
                    } catch (Throwable t2) {
                        ExceptionUtils.handleThrowable(t2);
                        if (log.isDebugEnabled()) {
                            log.debug(sm.getString("http11processor.request.prepare"), t2);
                        }
                        this.response.setStatus(500);
                        setErrorState(ErrorState.CLOSE_CLEAN, t2);
                    }
                }
                int maxKeepAliveRequests = this.protocol.getMaxKeepAliveRequests();
                if (maxKeepAliveRequests == 1) {
                    this.keepAlive = false;
                } else if (maxKeepAliveRequests > 0 && socketWrapper.decrementKeepAlive() <= 0) {
                    this.keepAlive = false;
                }
                if (getErrorState().isIoAllowed()) {
                    try {
                        rp.setStage(3);
                        getAdapter().service(this.request, this.response);
                        if (this.keepAlive && !getErrorState().isError() && !isAsync() && statusDropsConnection(this.response.getStatus())) {
                            setErrorState(ErrorState.CLOSE_CLEAN, null);
                        }
                    } catch (InterruptedIOException e2) {
                        setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e2);
                    } catch (HeadersTooLargeException e3) {
                        log.error(sm.getString("http11processor.request.process"), e3);
                        if (this.response.isCommitted()) {
                            setErrorState(ErrorState.CLOSE_NOW, e3);
                        } else {
                            this.response.reset();
                            this.response.setStatus(500);
                            setErrorState(ErrorState.CLOSE_CLEAN, e3);
                            this.response.setHeader("Connection", Constants.CLOSE);
                        }
                    } catch (Throwable t3) {
                        ExceptionUtils.handleThrowable(t3);
                        log.error(sm.getString("http11processor.request.process"), t3);
                        this.response.setStatus(500);
                        setErrorState(ErrorState.CLOSE_CLEAN, t3);
                        getAdapter().log(this.request, this.response, 0L);
                    }
                }
                rp.setStage(4);
                if (!isAsync()) {
                    endRequest();
                }
                rp.setStage(5);
                if (getErrorState().isError()) {
                    this.response.setStatus(500);
                }
                if (!isAsync() || getErrorState().isError()) {
                    this.request.updateCounters();
                    if (getErrorState().isIoAllowed()) {
                        this.inputBuffer.nextRequest();
                        this.outputBuffer.nextRequest();
                    }
                }
                if (!this.protocol.getDisableUploadTimeout()) {
                    int connectionTimeout = this.protocol.getConnectionTimeout();
                    if (connectionTimeout > 0) {
                        socketWrapper.setReadTimeout(connectionTimeout);
                    } else {
                        socketWrapper.setReadTimeout(0L);
                    }
                }
                rp.setStage(6);
                sendfileState2 = processSendfile(socketWrapper);
            }
        }
        rp.setStage(7);
        if (getErrorState().isError() || this.protocol.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        if (isUpgrade()) {
            return AbstractEndpoint.Handler.SocketState.UPGRADING;
        }
        if (sendfileState == SendfileState.PENDING) {
            return AbstractEndpoint.Handler.SocketState.SENDFILE;
        }
        if (this.openSocket) {
            if (this.readComplete) {
                return AbstractEndpoint.Handler.SocketState.OPEN;
            }
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.CLOSED;
    }

    private Request cloneRequest(Request source) throws IOException {
        Request dest = new Request();
        dest.decodedURI().duplicate(source.decodedURI());
        dest.method().duplicate(source.method());
        dest.getMimeHeaders().duplicate(source.getMimeHeaders());
        dest.requestURI().duplicate(source.requestURI());
        return dest;
    }

    private boolean handleIncompleteRequestLineRead() {
        this.openSocket = true;
        if (this.inputBuffer.getParsingRequestLinePhase() > 1) {
            if (this.protocol.isPaused()) {
                this.response.setStatus(503);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
                return false;
            }
            this.readComplete = false;
            return true;
        }
        return true;
    }

    private void checkExpectationAndResponseStatus() {
        if (this.request.hasExpectation()) {
            if (this.response.getStatus() < 200 || this.response.getStatus() > 299) {
                this.inputBuffer.setSwallowInput(false);
                this.keepAlive = false;
            }
        }
    }

    private void prepareRequest() {
        MessageBytes transferEncodingValueMB;
        MessageBytes userAgentValueMB;
        MessageBytes expectMB;
        this.http11 = true;
        this.http09 = false;
        this.contentDelimitation = false;
        if (this.protocol.isSSLEnabled()) {
            this.request.scheme().setString("https");
        }
        MessageBytes protocolMB = this.request.protocol();
        if (protocolMB.equals(Constants.HTTP_11)) {
            protocolMB.setString(Constants.HTTP_11);
        } else if (protocolMB.equals(Constants.HTTP_10)) {
            this.http11 = false;
            this.keepAlive = false;
            protocolMB.setString(Constants.HTTP_10);
        } else if (protocolMB.equals("")) {
            this.http09 = true;
            this.http11 = false;
            this.keepAlive = false;
        } else {
            this.http11 = false;
            this.response.setStatus(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED);
            setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("http11processor.request.prepare") + " Unsupported HTTP version \"" + protocolMB + "\"");
            }
        }
        MimeHeaders headers = this.request.getMimeHeaders();
        MessageBytes connectionValueMB = headers.getValue("Connection");
        if (connectionValueMB != null) {
            ByteChunk connectionValueBC = connectionValueMB.getByteChunk();
            if (findBytes(connectionValueBC, Constants.CLOSE_BYTES) != -1) {
                this.keepAlive = false;
            } else if (findBytes(connectionValueBC, Constants.KEEPALIVE_BYTES) != -1) {
                this.keepAlive = true;
            }
        }
        if (this.http11 && (expectMB = headers.getValue("expect")) != null) {
            if (expectMB.indexOfIgnoreCase("100-continue", 0) != -1) {
                this.inputBuffer.setSwallowInput(false);
                this.request.setExpectation(true);
            } else {
                this.response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
        }
        Pattern restrictedUserAgents = this.protocol.getRestrictedUserAgentsPattern();
        if (restrictedUserAgents != null && ((this.http11 || this.keepAlive) && (userAgentValueMB = headers.getValue("user-agent")) != null)) {
            String userAgentValue = userAgentValueMB.toString();
            if (restrictedUserAgents.matcher(userAgentValue).matches()) {
                this.http11 = false;
                this.keepAlive = false;
            }
        }
        MessageBytes hostValueMB = null;
        try {
            hostValueMB = headers.getUniqueValue("host");
        } catch (IllegalArgumentException e) {
            this.response.setStatus(400);
            setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("http11processor.request.multipleHosts"));
            }
        }
        if (this.http11 && hostValueMB == null) {
            this.response.setStatus(400);
            setErrorState(ErrorState.CLOSE_CLEAN, null);
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("http11processor.request.noHostHeader"));
            }
        }
        ByteChunk uriBC = this.request.requestURI().getByteChunk();
        byte[] uriB = uriBC.getBytes();
        if (uriBC.startsWithIgnoreCase("http", 0)) {
            int pos = 4;
            if (uriBC.startsWithIgnoreCase("s", 4)) {
                pos = 4 + 1;
            }
            if (uriBC.startsWith("://", pos)) {
                int pos2 = pos + 3;
                int uriBCStart = uriBC.getStart();
                int slashPos = uriBC.indexOf('/', pos2);
                int atPos = uriBC.indexOf('@', pos2);
                if (slashPos > -1 && atPos > slashPos) {
                    atPos = -1;
                }
                if (slashPos == -1) {
                    slashPos = uriBC.getLength();
                    this.request.requestURI().setBytes(uriB, uriBCStart + 6, 1);
                } else {
                    this.request.requestURI().setBytes(uriB, uriBCStart + slashPos, uriBC.getLength() - slashPos);
                }
                if (atPos != -1) {
                    while (true) {
                        if (pos2 >= atPos) {
                            break;
                        }
                        byte c = uriB[uriBCStart + pos2];
                        if (HttpParser.isUserInfo(c)) {
                            pos2++;
                        } else {
                            this.response.setStatus(400);
                            setErrorState(ErrorState.CLOSE_CLEAN, null);
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("http11processor.request.invalidUserInfo"));
                            }
                        }
                    }
                    pos2 = atPos + 1;
                }
                if (this.http11) {
                    if (hostValueMB != null && !hostValueMB.getByteChunk().equals(uriB, uriBCStart + pos2, slashPos - pos2)) {
                        if (this.protocol.getAllowHostHeaderMismatch()) {
                            hostValueMB = headers.setValue("host");
                            hostValueMB.setBytes(uriB, uriBCStart + pos2, slashPos - pos2);
                        } else {
                            this.response.setStatus(400);
                            setErrorState(ErrorState.CLOSE_CLEAN, null);
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("http11processor.request.inconsistentHosts"));
                            }
                        }
                    }
                } else {
                    hostValueMB = headers.setValue("host");
                    hostValueMB.setBytes(uriB, uriBCStart + pos2, slashPos - pos2);
                }
            } else {
                this.response.setStatus(400);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("http11processor.request.invalidScheme"));
                }
            }
        }
        int i = uriBC.getStart();
        while (true) {
            if (i >= uriBC.getEnd()) {
                break;
            } else if (this.httpParser.isAbsolutePathRelaxed(uriB[i])) {
                i++;
            } else {
                this.response.setStatus(400);
                setErrorState(ErrorState.CLOSE_CLEAN, null);
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("http11processor.request.invalidUri"));
                }
            }
        }
        InputFilter[] inputFilters = this.inputBuffer.getFilters();
        if (this.http11 && (transferEncodingValueMB = headers.getValue("transfer-encoding")) != null) {
            String transferEncodingValue = transferEncodingValueMB.toString();
            int startPos = 0;
            int commaPos = transferEncodingValue.indexOf(44);
            while (commaPos != -1) {
                String encodingName = transferEncodingValue.substring(startPos, commaPos);
                addInputFilter(inputFilters, encodingName);
                startPos = commaPos + 1;
                commaPos = transferEncodingValue.indexOf(44, startPos);
            }
            String encodingName2 = transferEncodingValue.substring(startPos);
            addInputFilter(inputFilters, encodingName2);
        }
        long contentLength = this.request.getContentLengthLong();
        if (contentLength >= 0) {
            if (this.contentDelimitation) {
                headers.removeHeader("content-length");
                this.request.setContentLength(-1L);
            } else {
                this.inputBuffer.addActiveFilter(inputFilters[0]);
                this.contentDelimitation = true;
            }
        }
        parseHost(hostValueMB);
        if (!this.contentDelimitation) {
            this.inputBuffer.addActiveFilter(inputFilters[2]);
            this.contentDelimitation = true;
        }
        if (!getErrorState().isIoAllowed()) {
            getAdapter().log(this.request, this.response, 0L);
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void prepareResponse() throws IOException {
        boolean entityBody = true;
        this.contentDelimitation = false;
        OutputFilter[] outputFilters = this.outputBuffer.getFilters();
        if (this.http09) {
            this.outputBuffer.addActiveFilter(outputFilters[0]);
            this.outputBuffer.commit();
            return;
        }
        int statusCode = this.response.getStatus();
        if (statusCode < 200 || statusCode == 204 || statusCode == 205 || statusCode == 304) {
            this.outputBuffer.addActiveFilter(outputFilters[2]);
            entityBody = false;
            this.contentDelimitation = true;
            if (statusCode == 205) {
                this.response.setContentLength(0L);
            } else {
                this.response.setContentLength(-1L);
            }
        }
        MessageBytes methodMB = this.request.method();
        if (methodMB.equals(WebContentGenerator.METHOD_HEAD)) {
            this.outputBuffer.addActiveFilter(outputFilters[2]);
            this.contentDelimitation = true;
        }
        if (this.protocol.getUseSendfile()) {
            prepareSendfile(outputFilters);
        }
        boolean useCompression = false;
        if (entityBody && this.sendfileData == null) {
            useCompression = this.protocol.useCompression(this.request, this.response);
        }
        MimeHeaders headers = this.response.getMimeHeaders();
        if (entityBody || statusCode == 204) {
            String contentType = this.response.getContentType();
            if (contentType != null) {
                headers.setValue(HttpHeaders.CONTENT_TYPE).setString(contentType);
            }
            String contentLanguage = this.response.getContentLanguage();
            if (contentLanguage != null) {
                headers.setValue(HttpHeaders.CONTENT_LANGUAGE).setString(contentLanguage);
            }
        }
        long contentLength = this.response.getContentLengthLong();
        boolean connectionClosePresent = false;
        if (this.http11 && this.response.getTrailerFields() != null) {
            this.outputBuffer.addActiveFilter(outputFilters[1]);
            this.contentDelimitation = true;
            headers.addValue("Transfer-Encoding").setString(Constants.CHUNKED);
        } else if (contentLength != -1) {
            headers.setValue(HttpHeaders.CONTENT_LENGTH).setLong(contentLength);
            this.outputBuffer.addActiveFilter(outputFilters[0]);
            this.contentDelimitation = true;
        } else {
            connectionClosePresent = isConnectionClose(headers);
            if (this.http11 && entityBody && !connectionClosePresent) {
                this.outputBuffer.addActiveFilter(outputFilters[1]);
                this.contentDelimitation = true;
                headers.addValue("Transfer-Encoding").setString(Constants.CHUNKED);
            } else {
                this.outputBuffer.addActiveFilter(outputFilters[0]);
            }
        }
        if (useCompression) {
            this.outputBuffer.addActiveFilter(outputFilters[3]);
        }
        if (headers.getValue(HttpHeaders.DATE) == null) {
            headers.addValue(HttpHeaders.DATE).setString(FastHttpDateFormat.getCurrentDate());
        }
        if (entityBody && !this.contentDelimitation) {
            this.keepAlive = false;
        }
        checkExpectationAndResponseStatus();
        if (this.keepAlive && statusDropsConnection(statusCode)) {
            this.keepAlive = false;
        }
        if (!this.keepAlive) {
            if (!connectionClosePresent) {
                headers.addValue("Connection").setString(Constants.CLOSE);
            }
        } else if (!this.http11 && !getErrorState().isError()) {
            headers.addValue("Connection").setString(Constants.KEEPALIVE);
        }
        String server = this.protocol.getServer();
        if (server == null) {
            if (this.protocol.getServerRemoveAppProvidedValues()) {
                headers.removeHeader("server");
            }
        } else {
            headers.setValue(HttpHeaders.SERVER).setString(server);
        }
        try {
            this.outputBuffer.sendStatus();
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                this.outputBuffer.sendHeader(headers.getName(i), headers.getValue(i));
            }
            this.outputBuffer.endHeaders();
            this.outputBuffer.commit();
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.outputBuffer.resetHeaderBuffer();
            throw t;
        }
    }

    private static boolean isConnectionClose(MimeHeaders headers) {
        MessageBytes connection = headers.getValue("Connection");
        if (connection == null) {
            return false;
        }
        return connection.equals(Constants.CLOSE);
    }

    private void prepareSendfile(OutputFilter[] outputFilters) {
        String fileName = (String) this.request.getAttribute("org.apache.tomcat.sendfile.filename");
        if (fileName == null) {
            this.sendfileData = null;
            return;
        }
        this.outputBuffer.addActiveFilter(outputFilters[2]);
        this.contentDelimitation = true;
        long pos = ((Long) this.request.getAttribute("org.apache.tomcat.sendfile.start")).longValue();
        long end = ((Long) this.request.getAttribute("org.apache.tomcat.sendfile.end")).longValue();
        this.sendfileData = this.socketWrapper.createSendfileData(fileName, pos, end - pos);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected void populateHost() {
        this.request.action(ActionCode.REQ_LOCALPORT_ATTRIBUTE, this.request);
        this.request.setServerPort(this.request.getLocalPort());
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean flushBufferedWrite() throws IOException {
        if (this.outputBuffer.hasDataToWrite() && this.outputBuffer.flushBuffer(false)) {
            this.outputBuffer.registerWriteInterest();
            return true;
        }
        return false;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        if (!this.keepAlive) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        endRequest();
        this.inputBuffer.nextRequest();
        this.outputBuffer.nextRequest();
        if (this.socketWrapper.isReadPending()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }

    @Override // org.apache.coyote.AbstractProcessorLight
    public Log getLog() {
        return log;
    }

    private void endRequest() {
        if (getErrorState().isError()) {
            this.inputBuffer.setSwallowInput(false);
        } else {
            checkExpectationAndResponseStatus();
        }
        if (getErrorState().isIoAllowed()) {
            try {
                this.inputBuffer.endRequest();
            } catch (IOException e) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.response.setStatus(500);
                setErrorState(ErrorState.CLOSE_NOW, t);
                log.error(sm.getString("http11processor.request.finish"), t);
            }
        }
        if (getErrorState().isIoAllowed()) {
            try {
                action(ActionCode.COMMIT, null);
                this.outputBuffer.end();
            } catch (IOException e2) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e2);
            } catch (Throwable t2) {
                ExceptionUtils.handleThrowable(t2);
                setErrorState(ErrorState.CLOSE_NOW, t2);
                log.error(sm.getString("http11processor.response.finish"), t2);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void finishResponse() throws IOException {
        this.outputBuffer.end();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void ack() {
        if (!this.response.isCommitted() && this.request.hasExpectation()) {
            this.inputBuffer.setSwallowInput(true);
            try {
                this.outputBuffer.sendAck();
            } catch (IOException e) {
                setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void flush() throws IOException {
        this.outputBuffer.flush();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final int available(boolean doRead) {
        return this.inputBuffer.available(doRead);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setRequestBody(ByteChunk body) {
        InputFilter savedBody = new SavedRequestInputFilter(body);
        Http11InputBuffer internalBuffer = (Http11InputBuffer) this.request.getInputBuffer();
        internalBuffer.addActiveFilter(savedBody);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void setSwallowResponse() {
        this.outputBuffer.responseFinished = true;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void disableSwallowRequest() {
        this.inputBuffer.setSwallowInput(false);
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void sslReHandShake() throws IOException {
        if (this.sslSupport != null) {
            InputFilter[] inputFilters = this.inputBuffer.getFilters();
            ((BufferedInputFilter) inputFilters[3]).setLimit(this.protocol.getMaxSavePostSize());
            this.inputBuffer.addActiveFilter(inputFilters[3]);
            this.socketWrapper.doClientAuth(this.sslSupport);
            try {
                Object sslO = this.sslSupport.getPeerCertificateChain();
                if (sslO != null) {
                    this.request.setAttribute("javax.servlet.request.X509Certificate", sslO);
                }
            } catch (IOException ioe) {
                log.warn(sm.getString("http11processor.socket.ssl"), ioe);
            }
        }
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isRequestBodyFullyRead() {
        return this.inputBuffer.isFinished();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void registerReadInterest() {
        this.socketWrapper.registerReadInterest();
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final boolean isReady() {
        return this.outputBuffer.isReady();
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public UpgradeToken getUpgradeToken() {
        return this.upgradeToken;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected final void doHttpUpgrade(UpgradeToken upgradeToken) {
        this.upgradeToken = upgradeToken;
        this.outputBuffer.responseFinished = true;
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public ByteBuffer getLeftoverInput() {
        return this.inputBuffer.getLeftover();
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public boolean isUpgrade() {
        return this.upgradeToken != null;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsReady() {
        if (this.inputBuffer.isChunking()) {
            return this.inputBuffer.isFinished();
        }
        return true;
    }

    @Override // org.apache.coyote.AbstractProcessor
    protected boolean isTrailerFieldsSupported() {
        if (!this.http11) {
            return false;
        }
        if (!this.response.isCommitted()) {
            return true;
        }
        return this.outputBuffer.isChunking();
    }

    private SendfileState processSendfile(SocketWrapperBase<?> socketWrapper) {
        this.openSocket = this.keepAlive;
        SendfileState result = SendfileState.DONE;
        if (this.sendfileData != null && !getErrorState().isError()) {
            if (this.keepAlive) {
                if (available(false) == 0) {
                    this.sendfileData.keepAliveState = SendfileKeepAliveState.OPEN;
                } else {
                    this.sendfileData.keepAliveState = SendfileKeepAliveState.PIPELINED;
                }
            } else {
                this.sendfileData.keepAliveState = SendfileKeepAliveState.NONE;
            }
            result = socketWrapper.processSendfile(this.sendfileData);
            switch (result) {
                case ERROR:
                    if (log.isDebugEnabled()) {
                        log.debug(sm.getString("http11processor.sendfile.error"));
                    }
                    setErrorState(ErrorState.CLOSE_CONNECTION_NOW, null);
                    break;
            }
            this.sendfileData = null;
        }
        return result;
    }

    @Override // org.apache.coyote.AbstractProcessor, org.apache.coyote.Processor
    public final void recycle() {
        getAdapter().checkRecycled(this.request, this.response);
        super.recycle();
        this.inputBuffer.recycle();
        this.outputBuffer.recycle();
        this.upgradeToken = null;
        this.socketWrapper = null;
        this.sendfileData = null;
        this.sslSupport = null;
    }

    @Override // org.apache.coyote.Processor
    public void pause() {
    }
}