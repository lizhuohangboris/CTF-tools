package org.apache.coyote;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ReadListener;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/Request.class */
public final class Request {
    private static final StringManager sm = StringManager.getManager(Request.class);
    private static final int INITIAL_COOKIE_SIZE = 4;
    private int remotePort;
    private int localPort;
    private Response response;
    private volatile ActionHook hook;
    volatile ReadListener listener;
    private int serverPort = -1;
    private final MessageBytes serverNameMB = MessageBytes.newInstance();
    private final MessageBytes schemeMB = MessageBytes.newInstance();
    private final MessageBytes methodMB = MessageBytes.newInstance();
    private final MessageBytes uriMB = MessageBytes.newInstance();
    private final MessageBytes decodedUriMB = MessageBytes.newInstance();
    private final MessageBytes queryMB = MessageBytes.newInstance();
    private final MessageBytes protoMB = MessageBytes.newInstance();
    private final MessageBytes remoteAddrMB = MessageBytes.newInstance();
    private final MessageBytes localNameMB = MessageBytes.newInstance();
    private final MessageBytes remoteHostMB = MessageBytes.newInstance();
    private final MessageBytes localAddrMB = MessageBytes.newInstance();
    private final MimeHeaders headers = new MimeHeaders();
    private final Map<String, String> trailerFields = new HashMap();
    private final Map<String, String> pathParameters = new HashMap();
    private final Object[] notes = new Object[32];
    private InputBuffer inputBuffer = null;
    private final UDecoder urlDecoder = new UDecoder();
    private long contentLength = -1;
    private MessageBytes contentTypeMB = null;
    private Charset charset = null;
    private String characterEncoding = null;
    private boolean expectation = false;
    private final ServerCookies serverCookies = new ServerCookies(4);
    private final Parameters parameters = new Parameters();
    private final MessageBytes remoteUser = MessageBytes.newInstance();
    private boolean remoteUserNeedsAuthorization = false;
    private final MessageBytes authType = MessageBytes.newInstance();
    private final HashMap<String, Object> attributes = new HashMap<>();
    private long bytesRead = 0;
    private long startTime = -1;
    private int available = 0;
    private final RequestInfo reqProcessorMX = new RequestInfo(this);
    private boolean sendfile = true;
    private final AtomicBoolean allDataReadEventSent = new AtomicBoolean(false);

    public Request() {
        this.parameters.setQuery(this.queryMB);
        this.parameters.setURLDecoder(this.urlDecoder);
    }

    public ReadListener getReadListener() {
        return this.listener;
    }

    public void setReadListener(ReadListener listener) {
        if (listener == null) {
            throw new NullPointerException(sm.getString("request.nullReadListener"));
        }
        if (getReadListener() != null) {
            throw new IllegalStateException(sm.getString("request.readListenerSet"));
        }
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.ASYNC_IS_ASYNC, result);
        if (!result.get()) {
            throw new IllegalStateException(sm.getString("request.notAsync"));
        }
        this.listener = listener;
    }

    public boolean sendAllDataReadEvent() {
        return this.allDataReadEventSent.compareAndSet(false, true);
    }

    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    public boolean isTrailerFieldsReady() {
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.IS_TRAILER_FIELDS_READY, result);
        return result.get();
    }

    public Map<String, String> getTrailerFields() {
        return this.trailerFields;
    }

    public UDecoder getURLDecoder() {
        return this.urlDecoder;
    }

    public MessageBytes scheme() {
        return this.schemeMB;
    }

    public MessageBytes method() {
        return this.methodMB;
    }

    public MessageBytes requestURI() {
        return this.uriMB;
    }

    public MessageBytes decodedURI() {
        return this.decodedUriMB;
    }

    public MessageBytes queryString() {
        return this.queryMB;
    }

    public MessageBytes protocol() {
        return this.protoMB;
    }

    public MessageBytes serverName() {
        return this.serverNameMB;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public MessageBytes remoteAddr() {
        return this.remoteAddrMB;
    }

    public MessageBytes remoteHost() {
        return this.remoteHostMB;
    }

    public MessageBytes localName() {
        return this.localNameMB;
    }

    public MessageBytes localAddr() {
        return this.localAddrMB;
    }

    public int getRemotePort() {
        return this.remotePort;
    }

    public void setRemotePort(int port) {
        this.remotePort = port;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public String getCharacterEncoding() {
        if (this.characterEncoding == null) {
            this.characterEncoding = getCharsetFromContentType(getContentType());
        }
        return this.characterEncoding;
    }

    public Charset getCharset() throws UnsupportedEncodingException {
        if (this.charset == null) {
            getCharacterEncoding();
            if (this.characterEncoding != null) {
                this.charset = B2CConverter.getCharset(this.characterEncoding);
            }
        }
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
        this.characterEncoding = charset.name();
    }

    public void setContentLength(long len) {
        this.contentLength = len;
    }

    public int getContentLength() {
        long length = getContentLengthLong();
        if (length < 2147483647L) {
            return (int) length;
        }
        return -1;
    }

    public long getContentLengthLong() {
        if (this.contentLength > -1) {
            return this.contentLength;
        }
        MessageBytes clB = this.headers.getUniqueValue("content-length");
        this.contentLength = (clB == null || clB.isNull()) ? -1L : clB.getLong();
        return this.contentLength;
    }

    public String getContentType() {
        contentType();
        if (this.contentTypeMB == null || this.contentTypeMB.isNull()) {
            return null;
        }
        return this.contentTypeMB.toString();
    }

    public void setContentType(String type) {
        this.contentTypeMB.setString(type);
    }

    public MessageBytes contentType() {
        if (this.contentTypeMB == null) {
            this.contentTypeMB = this.headers.getValue("content-type");
        }
        return this.contentTypeMB;
    }

    public void setContentType(MessageBytes mb) {
        this.contentTypeMB = mb;
    }

    public String getHeader(String name) {
        return this.headers.getHeader(name);
    }

    public void setExpectation(boolean expectation) {
        this.expectation = expectation;
    }

    public boolean hasExpectation() {
        return this.expectation;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        this.response = response;
        response.setRequest(this);
    }

    public void setHook(ActionHook hook) {
        this.hook = hook;
    }

    public void action(ActionCode actionCode, Object param) {
        if (this.hook != null) {
            if (param == null) {
                this.hook.action(actionCode, this);
            } else {
                this.hook.action(actionCode, param);
            }
        }
    }

    public ServerCookies getCookies() {
        return this.serverCookies;
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public void addPathParameter(String name, String value) {
        this.pathParameters.put(name, value);
    }

    public String getPathParameter(String name) {
        return this.pathParameters.get(name);
    }

    public void setAttribute(String name, Object o) {
        this.attributes.put(name, o);
    }

    public HashMap<String, Object> getAttributes() {
        return this.attributes;
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public MessageBytes getRemoteUser() {
        return this.remoteUser;
    }

    public boolean getRemoteUserNeedsAuthorization() {
        return this.remoteUserNeedsAuthorization;
    }

    public void setRemoteUserNeedsAuthorization(boolean remoteUserNeedsAuthorization) {
        this.remoteUserNeedsAuthorization = remoteUserNeedsAuthorization;
    }

    public MessageBytes getAuthType() {
        return this.authType;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public boolean getSendfile() {
        return this.sendfile;
    }

    public void setSendfile(boolean sendfile) {
        this.sendfile = sendfile;
    }

    public boolean isFinished() {
        AtomicBoolean result = new AtomicBoolean(false);
        action(ActionCode.REQUEST_BODY_FULLY_READ, result);
        return result.get();
    }

    public boolean getSupportsRelativeRedirects() {
        if (protocol().equals("") || protocol().equals(org.apache.coyote.http11.Constants.HTTP_10)) {
            return false;
        }
        return true;
    }

    public InputBuffer getInputBuffer() {
        return this.inputBuffer;
    }

    public void setInputBuffer(InputBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
    }

    public int doRead(ApplicationBufferHandler handler) throws IOException {
        int n = this.inputBuffer.doRead(handler);
        if (n > 0) {
            this.bytesRead += n;
        }
        return n;
    }

    public String toString() {
        return "R( " + requestURI().toString() + ")";
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public final void setNote(int pos, Object value) {
        this.notes[pos] = value;
    }

    public final Object getNote(int pos) {
        return this.notes[pos];
    }

    public void recycle() {
        this.bytesRead = 0L;
        this.contentLength = -1L;
        this.contentTypeMB = null;
        this.charset = null;
        this.characterEncoding = null;
        this.expectation = false;
        this.headers.recycle();
        this.trailerFields.clear();
        this.serverNameMB.recycle();
        this.serverPort = -1;
        this.localAddrMB.recycle();
        this.localNameMB.recycle();
        this.localPort = -1;
        this.remoteAddrMB.recycle();
        this.remoteHostMB.recycle();
        this.remotePort = -1;
        this.available = 0;
        this.sendfile = true;
        this.serverCookies.recycle();
        this.parameters.recycle();
        this.pathParameters.clear();
        this.uriMB.recycle();
        this.decodedUriMB.recycle();
        this.queryMB.recycle();
        this.methodMB.recycle();
        this.protoMB.recycle();
        this.schemeMB.recycle();
        this.remoteUser.recycle();
        this.remoteUserNeedsAuthorization = false;
        this.authType.recycle();
        this.attributes.clear();
        this.listener = null;
        this.allDataReadEventSent.set(false);
        this.startTime = -1L;
    }

    public void updateCounters() {
        this.reqProcessorMX.updateCounters();
    }

    public RequestInfo getRequestProcessor() {
        return this.reqProcessorMX;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }

    public boolean isProcessing() {
        return this.reqProcessorMX.getStage() == 3;
    }

    private static String getCharsetFromContentType(String contentType) {
        int start;
        if (contentType == null || (start = contentType.indexOf("charset=")) < 0) {
            return null;
        }
        String encoding = contentType.substring(start + 8);
        int end = encoding.indexOf(59);
        if (end >= 0) {
            encoding = encoding.substring(0, end);
        }
        String encoding2 = encoding.trim();
        if (encoding2.length() > 2 && encoding2.startsWith("\"") && encoding2.endsWith("\"")) {
            encoding2 = encoding2.substring(1, encoding2.length() - 1);
        }
        return encoding2.trim();
    }
}