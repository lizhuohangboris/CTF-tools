package org.apache.tomcat.util.http.parser;

import java.nio.charset.StandardCharsets;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/parser/Cookie.class */
public class Cookie {
    private static final Log log = LogFactory.getLog(Cookie.class);
    private static final UserDataHelper invalidCookieVersionLog = new UserDataHelper(log);
    private static final UserDataHelper invalidCookieLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http.parser");
    private static final boolean[] isCookieOctet = new boolean[256];
    private static final boolean[] isText = new boolean[256];
    private static final byte[] VERSION_BYTES = "$Version".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] PATH_BYTES = "$Path".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] DOMAIN_BYTES = "$Domain".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final byte TAB_BYTE = 9;
    private static final byte SPACE_BYTE = 32;
    private static final byte QUOTE_BYTE = 34;
    private static final byte COMMA_BYTE = 44;
    private static final byte FORWARDSLASH_BYTE = 47;
    private static final byte SEMICOLON_BYTE = 59;
    private static final byte EQUALS_BYTE = 61;
    private static final byte SLASH_BYTE = 92;
    private static final byte DEL_BYTE = Byte.MAX_VALUE;

    static {
        for (int i = 0; i < 256; i++) {
            if (i < 33 || i == 34 || i == 44 || i == 59 || i == 92 || i == 127) {
                isCookieOctet[i] = false;
            } else {
                isCookieOctet[i] = true;
            }
        }
        for (int i2 = 0; i2 < 256; i2++) {
            if (i2 < 9 || ((i2 > 9 && i2 < 32) || i2 == 127)) {
                isText[i2] = false;
            } else {
                isText[i2] = true;
            }
        }
    }

    private Cookie() {
    }

    public static void parseCookie(byte[] bytes, int offset, int len, ServerCookies serverCookies) {
        ByteBuffer bb = new ByteBuffer(bytes, offset, len);
        skipLWS(bb);
        int mark = bb.position();
        SkipResult skipResult = skipBytes(bb, VERSION_BYTES);
        if (skipResult != SkipResult.FOUND) {
            parseCookieRfc6265(bb, serverCookies);
            return;
        }
        skipLWS(bb);
        SkipResult skipResult2 = skipByte(bb, (byte) 61);
        if (skipResult2 != SkipResult.FOUND) {
            bb.position(mark);
            parseCookieRfc6265(bb, serverCookies);
            return;
        }
        skipLWS(bb);
        ByteBuffer value = readCookieValue(bb);
        if (value != null && value.remaining() == 1) {
            int version = value.get() - 48;
            if (version == 1 || version == 0) {
                skipLWS(bb);
                byte b = bb.get();
                if (b == 59 || b == 44) {
                    parseCookieRfc2109(bb, serverCookies, version);
                    return;
                }
                return;
            }
            value.rewind();
            logInvalidVersion(value);
            return;
        }
        logInvalidVersion(value);
    }

    public static String unescapeCookieValueRfc2109(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        if (input.charAt(0) != '\"' && input.charAt(input.length() - 1) != '\"') {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        char[] chars = input.toCharArray();
        boolean escaped = false;
        for (int i = 1; i < input.length() - 1; i++) {
            if (chars[i] == '\\') {
                escaped = true;
            } else if (escaped) {
                escaped = false;
                if (chars[i] < 128) {
                    sb.append(chars[i]);
                } else {
                    sb.append('\\');
                    sb.append(chars[i]);
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    private static void parseCookieRfc6265(ByteBuffer bb, ServerCookies serverCookies) {
        boolean moreToProcess = true;
        while (moreToProcess) {
            skipLWS(bb);
            ByteBuffer name = readToken(bb);
            ByteBuffer value = null;
            skipLWS(bb);
            if (skipByte(bb, (byte) 61) == SkipResult.FOUND) {
                skipLWS(bb);
                value = readCookieValueRfc6265(bb);
                if (value == null) {
                    logInvalidHeader(bb);
                    skipUntilSemiColon(bb);
                } else {
                    skipLWS(bb);
                }
            }
            SkipResult skipResult = skipByte(bb, (byte) 59);
            if (skipResult != SkipResult.FOUND) {
                if (skipResult == SkipResult.NOT_FOUND) {
                    logInvalidHeader(bb);
                    skipUntilSemiColon(bb);
                } else {
                    moreToProcess = false;
                }
            }
            if (name.hasRemaining()) {
                ServerCookie sc = serverCookies.addCookie();
                sc.getName().setBytes(name.array(), name.position(), name.remaining());
                if (value == null) {
                    sc.getValue().setBytes(EMPTY_BYTES, 0, EMPTY_BYTES.length);
                } else {
                    sc.getValue().setBytes(value.array(), value.position(), value.remaining());
                }
            }
        }
    }

    private static void parseCookieRfc2109(ByteBuffer bb, ServerCookies serverCookies, int version) {
        boolean moreToProcess = true;
        while (moreToProcess) {
            skipLWS(bb);
            boolean parseAttributes = true;
            ByteBuffer name = readToken(bb);
            ByteBuffer value = null;
            ByteBuffer path = null;
            ByteBuffer domain = null;
            skipLWS(bb);
            SkipResult skipResult = skipByte(bb, (byte) 61);
            if (skipResult == SkipResult.FOUND) {
                skipLWS(bb);
                value = readCookieValueRfc2109(bb, false);
                if (value == null) {
                    skipInvalidCookie(bb);
                } else {
                    skipLWS(bb);
                }
            }
            SkipResult skipResult2 = skipByte(bb, (byte) 44);
            if (skipResult2 == SkipResult.FOUND) {
                parseAttributes = false;
            }
            SkipResult skipResult3 = skipByte(bb, (byte) 59);
            if (skipResult3 == SkipResult.EOF) {
                parseAttributes = false;
                moreToProcess = false;
            } else if (skipResult3 == SkipResult.NOT_FOUND) {
                skipInvalidCookie(bb);
            }
            if (parseAttributes) {
                SkipResult skipResult4 = skipBytes(bb, PATH_BYTES);
                if (skipResult4 == SkipResult.FOUND) {
                    skipLWS(bb);
                    SkipResult skipResult5 = skipByte(bb, (byte) 61);
                    if (skipResult5 != SkipResult.FOUND) {
                        skipInvalidCookie(bb);
                    } else {
                        path = readCookieValueRfc2109(bb, true);
                        if (path == null) {
                            skipInvalidCookie(bb);
                        } else {
                            skipLWS(bb);
                            SkipResult skipResult6 = skipByte(bb, (byte) 44);
                            if (skipResult6 == SkipResult.FOUND) {
                                parseAttributes = false;
                            }
                            SkipResult skipResult7 = skipByte(bb, (byte) 59);
                            if (skipResult7 == SkipResult.EOF) {
                                parseAttributes = false;
                                moreToProcess = false;
                            } else if (skipResult7 == SkipResult.NOT_FOUND) {
                                skipInvalidCookie(bb);
                            }
                        }
                    }
                }
            }
            if (parseAttributes) {
                SkipResult skipResult8 = skipBytes(bb, DOMAIN_BYTES);
                if (skipResult8 == SkipResult.FOUND) {
                    skipLWS(bb);
                    SkipResult skipResult9 = skipByte(bb, (byte) 61);
                    if (skipResult9 != SkipResult.FOUND) {
                        skipInvalidCookie(bb);
                    } else {
                        domain = readCookieValueRfc2109(bb, false);
                        if (domain == null) {
                            skipInvalidCookie(bb);
                        } else {
                            SkipResult skipResult10 = skipByte(bb, (byte) 44);
                            if (skipResult10 == SkipResult.FOUND) {
                            }
                            SkipResult skipResult11 = skipByte(bb, (byte) 59);
                            if (skipResult11 == SkipResult.EOF) {
                                moreToProcess = false;
                            } else if (skipResult11 == SkipResult.NOT_FOUND) {
                                skipInvalidCookie(bb);
                            }
                        }
                    }
                }
            }
            if (name.hasRemaining() && value != null && value.hasRemaining()) {
                ServerCookie sc = serverCookies.addCookie();
                sc.setVersion(version);
                sc.getName().setBytes(name.array(), name.position(), name.remaining());
                sc.getValue().setBytes(value.array(), value.position(), value.remaining());
                if (domain != null) {
                    sc.getDomain().setBytes(domain.array(), domain.position(), domain.remaining());
                }
                if (path != null) {
                    sc.getPath().setBytes(path.array(), path.position(), path.remaining());
                }
            }
        }
    }

    private static void skipInvalidCookie(ByteBuffer bb) {
        logInvalidHeader(bb);
        skipUntilSemiColonOrComma(bb);
    }

    private static void skipLWS(ByteBuffer bb) {
        while (bb.hasRemaining()) {
            byte b = bb.get();
            if (b != 9 && b != 32) {
                bb.rewind();
                return;
            }
        }
    }

    private static void skipUntilSemiColon(ByteBuffer bb) {
        while (bb.hasRemaining() && bb.get() != 59) {
        }
    }

    private static void skipUntilSemiColonOrComma(ByteBuffer bb) {
        byte b;
        while (bb.hasRemaining() && (b = bb.get()) != 59 && b != 44) {
        }
    }

    private static SkipResult skipByte(ByteBuffer bb, byte target) {
        if (!bb.hasRemaining()) {
            return SkipResult.EOF;
        }
        if (bb.get() == target) {
            return SkipResult.FOUND;
        }
        bb.rewind();
        return SkipResult.NOT_FOUND;
    }

    private static SkipResult skipBytes(ByteBuffer bb, byte[] target) {
        int mark = bb.position();
        for (byte b : target) {
            if (!bb.hasRemaining()) {
                bb.position(mark);
                return SkipResult.EOF;
            } else if (bb.get() != b) {
                bb.position(mark);
                return SkipResult.NOT_FOUND;
            }
        }
        return SkipResult.FOUND;
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x0096, code lost:
        return new org.apache.tomcat.util.http.parser.Cookie.ByteBuffer(r7.bytes, r0, r10 - r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.apache.tomcat.util.http.parser.Cookie.ByteBuffer readCookieValue(org.apache.tomcat.util.http.parser.Cookie.ByteBuffer r7) {
        /*
            r0 = 0
            r8 = r0
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L1b
            r0 = r7
            byte r0 = r0.get()
            r1 = 34
            if (r0 != r1) goto L17
            r0 = 1
            r8 = r0
            goto L1b
        L17:
            r0 = r7
            r0.rewind()
        L1b:
            r0 = r7
            int r0 = r0.position()
            r9 = r0
            r0 = r7
            int r0 = r0.limit()
            r10 = r0
        L25:
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L87
            r0 = r7
            byte r0 = r0.get()
            r11 = r0
            boolean[] r0 = org.apache.tomcat.util.http.parser.Cookie.isCookieOctet
            r1 = r11
            r2 = 255(0xff, float:3.57E-43)
            r1 = r1 & r2
            r0 = r0[r1]
            if (r0 == 0) goto L42
            goto L84
        L42:
            r0 = r11
            r1 = 59
            if (r0 == r1) goto L5e
            r0 = r11
            r1 = 44
            if (r0 == r1) goto L5e
            r0 = r11
            r1 = 32
            if (r0 == r1) goto L5e
            r0 = r11
            r1 = 9
            if (r0 != r1) goto L6d
        L5e:
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            r0 = r7
            r1 = r10
            r0.position(r1)
            goto L87
        L6d:
            r0 = r8
            if (r0 == 0) goto L82
            r0 = r11
            r1 = 34
            if (r0 != r1) goto L82
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            goto L87
        L82:
            r0 = 0
            return r0
        L84:
            goto L25
        L87:
            org.apache.tomcat.util.http.parser.Cookie$ByteBuffer r0 = new org.apache.tomcat.util.http.parser.Cookie$ByteBuffer
            r1 = r0
            r2 = r7
            byte[] r2 = org.apache.tomcat.util.http.parser.Cookie.ByteBuffer.access$000(r2)
            r3 = r9
            r4 = r10
            r5 = r9
            int r4 = r4 - r5
            r1.<init>(r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.Cookie.readCookieValue(org.apache.tomcat.util.http.parser.Cookie$ByteBuffer):org.apache.tomcat.util.http.parser.Cookie$ByteBuffer");
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x008f, code lost:
        return new org.apache.tomcat.util.http.parser.Cookie.ByteBuffer(r7.bytes, r0, r10 - r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.apache.tomcat.util.http.parser.Cookie.ByteBuffer readCookieValueRfc6265(org.apache.tomcat.util.http.parser.Cookie.ByteBuffer r7) {
        /*
            r0 = 0
            r8 = r0
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L1b
            r0 = r7
            byte r0 = r0.get()
            r1 = 34
            if (r0 != r1) goto L17
            r0 = 1
            r8 = r0
            goto L1b
        L17:
            r0 = r7
            r0.rewind()
        L1b:
            r0 = r7
            int r0 = r0.position()
            r9 = r0
            r0 = r7
            int r0 = r0.limit()
            r10 = r0
        L25:
            r0 = r7
            boolean r0 = r0.hasRemaining()
            if (r0 == 0) goto L80
            r0 = r7
            byte r0 = r0.get()
            r11 = r0
            boolean[] r0 = org.apache.tomcat.util.http.parser.Cookie.isCookieOctet
            r1 = r11
            r2 = 255(0xff, float:3.57E-43)
            r1 = r1 & r2
            r0 = r0[r1]
            if (r0 == 0) goto L42
            goto L7d
        L42:
            r0 = r11
            r1 = 59
            if (r0 == r1) goto L57
            r0 = r11
            r1 = 32
            if (r0 == r1) goto L57
            r0 = r11
            r1 = 9
            if (r0 != r1) goto L66
        L57:
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            r0 = r7
            r1 = r10
            r0.position(r1)
            goto L80
        L66:
            r0 = r8
            if (r0 == 0) goto L7b
            r0 = r11
            r1 = 34
            if (r0 != r1) goto L7b
            r0 = r7
            int r0 = r0.position()
            r1 = 1
            int r0 = r0 - r1
            r10 = r0
            goto L80
        L7b:
            r0 = 0
            return r0
        L7d:
            goto L25
        L80:
            org.apache.tomcat.util.http.parser.Cookie$ByteBuffer r0 = new org.apache.tomcat.util.http.parser.Cookie$ByteBuffer
            r1 = r0
            r2 = r7
            byte[] r2 = org.apache.tomcat.util.http.parser.Cookie.ByteBuffer.access$000(r2)
            r3 = r9
            r4 = r10
            r5 = r9
            int r4 = r4 - r5
            r1.<init>(r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.parser.Cookie.readCookieValueRfc6265(org.apache.tomcat.util.http.parser.Cookie$ByteBuffer):org.apache.tomcat.util.http.parser.Cookie$ByteBuffer");
    }

    private static ByteBuffer readCookieValueRfc2109(ByteBuffer bb, boolean allowForwardSlash) {
        if (!bb.hasRemaining()) {
            return null;
        }
        if (bb.peek() == 34) {
            return readQuotedString(bb);
        }
        if (allowForwardSlash) {
            return readTokenAllowForwardSlash(bb);
        }
        return readToken(bb);
    }

    private static ByteBuffer readToken(ByteBuffer bb) {
        int start = bb.position();
        int end = bb.limit();
        while (true) {
            if (!bb.hasRemaining()) {
                break;
            } else if (!HttpParser.isToken(bb.get())) {
                end = bb.position() - 1;
                bb.position(end);
                break;
            }
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readTokenAllowForwardSlash(ByteBuffer bb) {
        int start = bb.position();
        int end = bb.limit();
        while (true) {
            if (!bb.hasRemaining()) {
                break;
            }
            byte b = bb.get();
            if (b != 47 && !HttpParser.isToken(b)) {
                end = bb.position() - 1;
                bb.position(end);
                break;
            }
        }
        return new ByteBuffer(bb.bytes, start, end - start);
    }

    private static ByteBuffer readQuotedString(ByteBuffer bb) {
        int start = bb.position();
        bb.get();
        boolean z = false;
        while (true) {
            boolean escaped = z;
            if (bb.hasRemaining()) {
                byte b = bb.get();
                if (b == 92) {
                    z = true;
                } else if (escaped && b > -1) {
                    z = false;
                } else if (b == 34) {
                    return new ByteBuffer(bb.bytes, start, bb.position() - start);
                } else {
                    if (isText[b & 255]) {
                        z = false;
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void logInvalidHeader(ByteBuffer bb) {
        UserDataHelper.Mode logMode = invalidCookieLog.getNextMode();
        if (logMode != null) {
            String headerValue = new String(bb.array(), bb.position(), bb.limit() - bb.position(), StandardCharsets.UTF_8);
            String message = sm.getString("cookie.invalidCookieValue", headerValue);
            switch (logMode) {
                case INFO_THEN_DEBUG:
                    message = message + sm.getString("cookie.fallToDebug");
                    break;
                case INFO:
                    break;
                case DEBUG:
                    log.debug(message);
                    return;
                default:
                    return;
            }
            log.info(message);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void logInvalidVersion(ByteBuffer value) {
        String version;
        UserDataHelper.Mode logMode = invalidCookieVersionLog.getNextMode();
        if (logMode != null) {
            if (value == null) {
                version = sm.getString("cookie.valueNotPresent");
            } else {
                version = new String(value.bytes, value.position(), value.limit() - value.position(), StandardCharsets.UTF_8);
            }
            String message = sm.getString("cookie.invalidCookieVersion", version);
            switch (logMode) {
                case INFO_THEN_DEBUG:
                    message = message + sm.getString("cookie.fallToDebug");
                    break;
                case INFO:
                    break;
                case DEBUG:
                    log.debug(message);
                    return;
                default:
                    return;
            }
            log.info(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/parser/Cookie$ByteBuffer.class */
    public static class ByteBuffer {
        private final byte[] bytes;
        private int limit;
        private int position;

        public ByteBuffer(byte[] bytes, int offset, int len) {
            this.position = 0;
            this.bytes = bytes;
            this.position = offset;
            this.limit = offset + len;
        }

        public int position() {
            return this.position;
        }

        public void position(int position) {
            this.position = position;
        }

        public int limit() {
            return this.limit;
        }

        public int remaining() {
            return this.limit - this.position;
        }

        public boolean hasRemaining() {
            return this.position < this.limit;
        }

        public byte get() {
            byte[] bArr = this.bytes;
            int i = this.position;
            this.position = i + 1;
            return bArr[i];
        }

        public byte peek() {
            return this.bytes[this.position];
        }

        public void rewind() {
            this.position--;
        }

        public byte[] array() {
            return this.bytes;
        }

        public String toString() {
            return "position [" + this.position + "], limit [" + this.limit + "]";
        }
    }
}