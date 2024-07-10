package org.apache.coyote.http11;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11InputBuffer.class */
public class Http11InputBuffer implements InputBuffer, ApplicationBufferHandler {
    private static final Log log = LogFactory.getLog(Http11InputBuffer.class);
    private static final StringManager sm = StringManager.getManager(Http11InputBuffer.class);
    private static final byte[] CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private final Request request;
    private final MimeHeaders headers;
    private final boolean rejectIllegalHeaderName;
    private ByteBuffer byteBuffer;
    private int end;
    private SocketWrapperBase<?> wrapper;
    private int parsingRequestLinePhase;
    private boolean parsingRequestLineEol;
    private int parsingRequestLineStart;
    private int parsingRequestLineQPos;
    private final HttpParser httpParser;
    private final int headerBufferSize;
    private int socketReadBufferSize;
    private final HeaderParseData headerData = new HeaderParseData();
    private InputFilter[] filterLibrary = new InputFilter[0];
    private InputFilter[] activeFilters = new InputFilter[0];
    private int lastActiveFilter = -1;
    private boolean parsingHeader = true;
    private boolean parsingRequestLine = true;
    private HeaderParsePosition headerParsePos = HeaderParsePosition.HEADER_START;
    private boolean swallowInput = true;
    private InputBuffer inputStreamInputBuffer = new SocketInputBuffer();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParsePosition.class */
    public enum HeaderParsePosition {
        HEADER_START,
        HEADER_NAME,
        HEADER_VALUE_START,
        HEADER_VALUE,
        HEADER_MULTI_LINE,
        HEADER_SKIPLINE
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParseStatus.class */
    public enum HeaderParseStatus {
        DONE,
        HAVE_MORE_HEADERS,
        NEED_MORE_DATA
    }

    public Http11InputBuffer(Request request, int headerBufferSize, boolean rejectIllegalHeaderName, HttpParser httpParser) {
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.request = request;
        this.headers = request.getMimeHeaders();
        this.headerBufferSize = headerBufferSize;
        this.rejectIllegalHeaderName = rejectIllegalHeaderName;
        this.httpParser = httpParser;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addFilter(InputFilter filter) {
        if (filter == null) {
            throw new NullPointerException(sm.getString("iib.filter.npe"));
        }
        InputFilter[] newFilterLibrary = (InputFilter[]) Arrays.copyOf(this.filterLibrary, this.filterLibrary.length + 1);
        newFilterLibrary[this.filterLibrary.length] = filter;
        this.filterLibrary = newFilterLibrary;
        this.activeFilters = new InputFilter[this.filterLibrary.length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InputFilter[] getFilters() {
        return this.filterLibrary;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addActiveFilter(InputFilter filter) {
        if (this.lastActiveFilter == -1) {
            filter.setBuffer(this.inputStreamInputBuffer);
        } else {
            for (int i = 0; i <= this.lastActiveFilter; i++) {
                if (this.activeFilters[i] == filter) {
                    return;
                }
            }
            filter.setBuffer(this.activeFilters[this.lastActiveFilter]);
        }
        InputFilter[] inputFilterArr = this.activeFilters;
        int i2 = this.lastActiveFilter + 1;
        this.lastActiveFilter = i2;
        inputFilterArr[i2] = filter;
        filter.setRequest(this.request);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSwallowInput(boolean swallowInput) {
        this.swallowInput = swallowInput;
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.lastActiveFilter == -1) {
            return this.inputStreamInputBuffer.doRead(handler);
        }
        return this.activeFilters[this.lastActiveFilter].doRead(handler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycle() {
        this.wrapper = null;
        this.request.recycle();
        for (int i = 0; i <= this.lastActiveFilter; i++) {
            this.activeFilters[i].recycle();
        }
        this.byteBuffer.limit(0).position(0);
        this.lastActiveFilter = -1;
        this.parsingHeader = true;
        this.swallowInput = true;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLine = true;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void nextRequest() {
        this.request.recycle();
        if (this.byteBuffer.position() > 0) {
            if (this.byteBuffer.remaining() > 0) {
                this.byteBuffer.compact();
                this.byteBuffer.flip();
            } else {
                this.byteBuffer.position(0).limit(0);
            }
        }
        for (int i = 0; i <= this.lastActiveFilter; i++) {
            this.activeFilters[i].recycle();
        }
        this.lastActiveFilter = -1;
        this.parsingHeader = true;
        this.swallowInput = true;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLine = true;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean parseRequestLine(boolean keptAlive, int connectionTimeout, int keepAliveTimeout) throws IOException {
        if (!this.parsingRequestLine) {
            return true;
        }
        if (this.parsingRequestLinePhase < 2) {
            while (true) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit()) {
                    if (keptAlive) {
                        this.wrapper.setReadTimeout(keepAliveTimeout);
                    }
                    if (!fill(false)) {
                        this.parsingRequestLinePhase = 1;
                        return false;
                    }
                    this.wrapper.setReadTimeout(connectionTimeout);
                }
                if (!keptAlive && this.byteBuffer.position() == 0 && this.byteBuffer.limit() >= CLIENT_PREFACE_START.length - 1) {
                    boolean prefaceMatch = true;
                    for (int i = 0; i < CLIENT_PREFACE_START.length && prefaceMatch; i++) {
                        if (CLIENT_PREFACE_START[i] != this.byteBuffer.get(i)) {
                            prefaceMatch = false;
                        }
                    }
                    if (prefaceMatch) {
                        this.parsingRequestLinePhase = -1;
                        return false;
                    }
                }
                if (this.request.getStartTime() < 0) {
                    this.request.setStartTime(System.currentTimeMillis());
                }
                byte chr = this.byteBuffer.get();
                if (chr != 13 && chr != 10) {
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                    this.parsingRequestLineStart = this.byteBuffer.position();
                    this.parsingRequestLinePhase = 2;
                    if (log.isDebugEnabled()) {
                        log.debug("Received [" + new String(this.byteBuffer.array(), this.byteBuffer.position(), this.byteBuffer.remaining(), StandardCharsets.ISO_8859_1) + "]");
                    }
                }
            }
        }
        if (this.parsingRequestLinePhase == 2) {
            boolean space = false;
            while (!space) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos = this.byteBuffer.position();
                byte chr2 = this.byteBuffer.get();
                if (chr2 == 32 || chr2 == 9) {
                    space = true;
                    this.request.method().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, pos - this.parsingRequestLineStart);
                } else if (!HttpParser.isToken(chr2)) {
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                    this.request.protocol().setString(Constants.HTTP_11);
                    throw new IllegalArgumentException(sm.getString("iib.invalidmethod"));
                }
            }
            this.parsingRequestLinePhase = 3;
        }
        if (this.parsingRequestLinePhase == 3) {
            boolean space2 = true;
            while (space2) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                byte chr3 = this.byteBuffer.get();
                if (chr3 != 32 && chr3 != 9) {
                    space2 = false;
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                }
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 4;
        }
        if (this.parsingRequestLinePhase == 4) {
            int end = 0;
            boolean space3 = false;
            while (!space3) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos2 = this.byteBuffer.position();
                byte chr4 = this.byteBuffer.get();
                if (chr4 == 32 || chr4 == 9) {
                    space3 = true;
                    end = pos2;
                } else if (chr4 == 13 || chr4 == 10) {
                    this.parsingRequestLineEol = true;
                    space3 = true;
                    end = pos2;
                } else if (chr4 == 63 && this.parsingRequestLineQPos == -1) {
                    this.parsingRequestLineQPos = pos2;
                } else if (this.parsingRequestLineQPos != -1 && !this.httpParser.isQueryRelaxed(chr4)) {
                    this.request.protocol().setString(Constants.HTTP_11);
                    throw new IllegalArgumentException(sm.getString("iib.invalidRequestTarget"));
                } else if (this.httpParser.isNotRequestTargetRelaxed(chr4)) {
                    this.request.protocol().setString(Constants.HTTP_11);
                    throw new IllegalArgumentException(sm.getString("iib.invalidRequestTarget"));
                }
            }
            if (this.parsingRequestLineQPos >= 0) {
                this.request.queryString().setBytes(this.byteBuffer.array(), this.parsingRequestLineQPos + 1, (end - this.parsingRequestLineQPos) - 1);
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.parsingRequestLineQPos - this.parsingRequestLineStart);
            } else {
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, end - this.parsingRequestLineStart);
            }
            this.parsingRequestLinePhase = 5;
        }
        if (this.parsingRequestLinePhase == 5) {
            boolean space4 = true;
            while (space4) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                byte chr5 = this.byteBuffer.get();
                if (chr5 != 32 && chr5 != 9) {
                    space4 = false;
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                }
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 6;
            this.end = 0;
        }
        if (this.parsingRequestLinePhase == 6) {
            while (!this.parsingRequestLineEol) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos3 = this.byteBuffer.position();
                byte chr6 = this.byteBuffer.get();
                if (chr6 == 13) {
                    this.end = pos3;
                } else if (chr6 == 10) {
                    if (this.end == 0) {
                        this.end = pos3;
                    }
                    this.parsingRequestLineEol = true;
                } else if (!HttpParser.isHttpProtocol(chr6)) {
                    throw new IllegalArgumentException(sm.getString("iib.invalidHttpProtocol"));
                }
            }
            if (this.end - this.parsingRequestLineStart > 0) {
                this.request.protocol().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.end - this.parsingRequestLineStart);
            } else {
                this.request.protocol().setString("");
            }
            this.parsingRequestLine = false;
            this.parsingRequestLinePhase = 0;
            this.parsingRequestLineEol = false;
            this.parsingRequestLineStart = 0;
            return true;
        }
        throw new IllegalStateException("Invalid request line parse phase:" + this.parsingRequestLinePhase);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean parseHeaders() throws IOException {
        HeaderParseStatus status;
        if (!this.parsingHeader) {
            throw new IllegalStateException(sm.getString("iib.parseheaders.ise.error"));
        }
        HeaderParseStatus headerParseStatus = HeaderParseStatus.HAVE_MORE_HEADERS;
        do {
            status = parseHeader();
            if (this.byteBuffer.position() > this.headerBufferSize || this.byteBuffer.capacity() - this.byteBuffer.position() < this.socketReadBufferSize) {
                throw new IllegalArgumentException(sm.getString("iib.requestheadertoolarge.error"));
            }
        } while (status == HeaderParseStatus.HAVE_MORE_HEADERS);
        if (status == HeaderParseStatus.DONE) {
            this.parsingHeader = false;
            this.end = this.byteBuffer.position();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getParsingRequestLinePhase() {
        return this.parsingRequestLinePhase;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void endRequest() throws IOException {
        if (this.swallowInput && this.lastActiveFilter != -1) {
            int extraBytes = (int) this.activeFilters[this.lastActiveFilter].end();
            this.byteBuffer.position(this.byteBuffer.position() - extraBytes);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int available(boolean read) {
        int available;
        int available2 = this.byteBuffer.remaining();
        if (available2 == 0 && this.lastActiveFilter >= 0) {
            for (int i = 0; available2 == 0 && i <= this.lastActiveFilter; i++) {
                available2 = this.activeFilters[i].available();
            }
        }
        if (available2 > 0 || !read) {
            return available2;
        }
        try {
            fill(false);
            available = this.byteBuffer.remaining();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("iib.available.readFail"), ioe);
            }
            available = 1;
        }
        return available;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFinished() {
        if (this.byteBuffer.limit() <= this.byteBuffer.position() && this.lastActiveFilter >= 0) {
            return this.activeFilters[this.lastActiveFilter].isFinished();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer getLeftover() {
        int available = this.byteBuffer.remaining();
        if (available > 0) {
            return ByteBuffer.wrap(this.byteBuffer.array(), this.byteBuffer.position(), available);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isChunking() {
        for (int i = 0; i < this.lastActiveFilter; i++) {
            if (this.activeFilters[i] == this.filterLibrary[1]) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(SocketWrapperBase<?> socketWrapper) {
        this.wrapper = socketWrapper;
        this.wrapper.setAppReadBufHandler(this);
        int bufLength = this.headerBufferSize + this.wrapper.getSocketBufferHandler().getReadBuffer().capacity();
        if (this.byteBuffer == null || this.byteBuffer.capacity() < bufLength) {
            this.byteBuffer = ByteBuffer.allocate(bufLength);
            this.byteBuffer.position(0).limit(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean fill(boolean block) throws IOException {
        if (this.parsingHeader) {
            if (this.byteBuffer.limit() >= this.headerBufferSize) {
                if (this.parsingRequestLine) {
                    this.request.protocol().setString(Constants.HTTP_11);
                }
                throw new IllegalArgumentException(sm.getString("iib.requestheadertoolarge.error"));
            }
        } else {
            this.byteBuffer.limit(this.end).position(this.end);
        }
        this.byteBuffer.mark();
        if (this.byteBuffer.position() < this.byteBuffer.limit()) {
            this.byteBuffer.position(this.byteBuffer.limit());
        }
        this.byteBuffer.limit(this.byteBuffer.capacity());
        int nRead = this.wrapper.read(block, this.byteBuffer);
        this.byteBuffer.limit(this.byteBuffer.position()).reset();
        if (nRead > 0) {
            return true;
        }
        if (nRead == -1) {
            throw new EOFException(sm.getString("iib.eof.error"));
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:101:0x02c6, code lost:
        if (r0 == 9) goto L137;
     */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x02c9, code lost:
        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_START;
     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x02d3, code lost:
        r7.byteBuffer.put(r7.headerData.realPos, r0);
        r7.headerData.realPos++;
        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x02fa, code lost:
        r7.headerData.headerValue.setBytes(r7.byteBuffer.array(), r7.headerData.start, r7.headerData.lastSignificantChar - r7.headerData.start);
        r7.headerData.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x032b, code lost:
        return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.HAVE_MORE_HEADERS;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0153, code lost:
        if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_SKIPLINE) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x015a, code lost:
        return skipLine();
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0162, code lost:
        if (r7.headerParsePos == org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x016c, code lost:
        if (r7.headerParsePos == org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0176, code lost:
        if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0180, code lost:
        if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START) goto L85;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0191, code lost:
        if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0199, code lost:
        if (fill(false) != false) goto L75;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x019f, code lost:
        return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01a0, code lost:
        r0 = r7.byteBuffer.get();
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x01ab, code lost:
        if (r0 == 32) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x01b1, code lost:
        if (r0 == 9) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01b4, code lost:
        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE;
        r7.byteBuffer.position(r7.byteBuffer.position() - 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01d6, code lost:
        if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE) goto L121;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01d9, code lost:
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01dc, code lost:
        if (r9 != false) goto L119;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01ed, code lost:
        if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01f5, code lost:
        if (fill(false) != false) goto L97;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01fb, code lost:
        return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01fc, code lost:
        r0 = r7.byteBuffer.get();
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0207, code lost:
        if (r0 != 13) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0210, code lost:
        if (r0 != 10) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0213, code lost:
        r9 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x021b, code lost:
        if (r0 == 32) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0221, code lost:
        if (r0 != 9) goto L111;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0224, code lost:
        r7.byteBuffer.put(r7.headerData.realPos, r0);
        r7.headerData.realPos++;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0244, code lost:
        r7.byteBuffer.put(r7.headerData.realPos, r0);
        r7.headerData.realPos++;
        r7.headerData.lastSignificantChar = r7.headerData.realPos;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0272, code lost:
        r7.headerData.realPos = r7.headerData.lastSignificantChar;
        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x0295, code lost:
        if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L128;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x029d, code lost:
        if (fill(false) != false) goto L128;
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x02a3, code lost:
        return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x02a4, code lost:
        r0 = r7.byteBuffer.get(r7.byteBuffer.position());
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x02ba, code lost:
        if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE) goto L140;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x02c0, code lost:
        if (r0 == 32) goto L137;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus parseHeader() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 812
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http11.Http11InputBuffer.parseHeader():org.apache.coyote.http11.Http11InputBuffer$HeaderParseStatus");
    }

    private HeaderParseStatus skipLine() throws IOException {
        this.headerParsePos = HeaderParsePosition.HEADER_SKIPLINE;
        boolean eol = false;
        while (!eol) {
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            int pos = this.byteBuffer.position();
            byte chr = this.byteBuffer.get();
            if (chr != 13) {
                if (chr == 10) {
                    eol = true;
                } else {
                    this.headerData.lastSignificantChar = pos;
                }
            }
        }
        if (this.rejectIllegalHeaderName || log.isDebugEnabled()) {
            String message = sm.getString("iib.invalidheader", new String(this.byteBuffer.array(), this.headerData.start, (this.headerData.lastSignificantChar - this.headerData.start) + 1, StandardCharsets.ISO_8859_1));
            if (this.rejectIllegalHeaderName) {
                throw new IllegalArgumentException(message);
            }
            log.debug(message);
        }
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        return HeaderParseStatus.HAVE_MORE_HEADERS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParseData.class */
    public static class HeaderParseData {
        int start;
        int realPos;
        int lastSignificantChar;
        MessageBytes headerValue;

        private HeaderParseData() {
            this.start = 0;
            this.realPos = 0;
            this.lastSignificantChar = 0;
            this.headerValue = null;
        }

        public void recycle() {
            this.start = 0;
            this.realPos = 0;
            this.lastSignificantChar = 0;
            this.headerValue = null;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/Http11InputBuffer$SocketInputBuffer.class */
    private class SocketInputBuffer implements InputBuffer {
        private SocketInputBuffer() {
        }

        @Override // org.apache.coyote.InputBuffer
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            if (Http11InputBuffer.this.byteBuffer.position() < Http11InputBuffer.this.byteBuffer.limit() || Http11InputBuffer.this.fill(true)) {
                int length = Http11InputBuffer.this.byteBuffer.remaining();
                handler.setByteBuffer(Http11InputBuffer.this.byteBuffer.duplicate());
                Http11InputBuffer.this.byteBuffer.position(Http11InputBuffer.this.byteBuffer.limit());
                return length;
            }
            return -1;
        }
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.byteBuffer = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
        if (this.byteBuffer.capacity() >= size) {
            this.byteBuffer.limit(size);
        }
        ByteBuffer temp = ByteBuffer.allocate(size);
        temp.put(this.byteBuffer);
        this.byteBuffer = temp;
        this.byteBuffer.mark();
    }
}