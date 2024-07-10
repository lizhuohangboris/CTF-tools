package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.FieldPosition;
import java.util.BitSet;
import java.util.Date;
import javax.servlet.http.Cookie;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/LegacyCookieProcessor.class */
public final class LegacyCookieProcessor extends CookieProcessorBase {
    private static final Log log = LogFactory.getLog(LegacyCookieProcessor.class);
    private static final UserDataHelper userDataLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http");
    private static final char[] V0_SEPARATORS = {',', ';', ' ', '\t'};
    private static final BitSet V0_SEPARATOR_FLAGS = new BitSet(128);
    private static final char[] HTTP_SEPARATORS = {'\t', ' ', '\"', '(', ')', ',', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '{', '}'};
    private final boolean STRICT_SERVLET_COMPLIANCE = Boolean.getBoolean("org.apache.catalina.STRICT_SERVLET_COMPLIANCE");
    private boolean allowEqualsInValue = false;
    private boolean allowNameOnly = false;
    private boolean allowHttpSepsInV0 = false;
    private boolean alwaysAddExpires;
    private final BitSet httpSeparatorFlags;
    private final BitSet allowedWithoutQuotes;

    static {
        char[] cArr;
        for (char c : V0_SEPARATORS) {
            V0_SEPARATOR_FLAGS.set(c);
        }
    }

    public LegacyCookieProcessor() {
        char[] cArr;
        String separators;
        char[] charArray;
        this.alwaysAddExpires = !this.STRICT_SERVLET_COMPLIANCE;
        this.httpSeparatorFlags = new BitSet(128);
        this.allowedWithoutQuotes = new BitSet(128);
        for (char c : HTTP_SEPARATORS) {
            this.httpSeparatorFlags.set(c);
        }
        boolean b = this.STRICT_SERVLET_COMPLIANCE;
        if (b) {
            this.httpSeparatorFlags.set(47);
        }
        if (getAllowHttpSepsInV0()) {
            separators = BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS;
        } else {
            separators = "()<>@,;:\\\"/[]?={} \t";
        }
        this.allowedWithoutQuotes.set(32, 127);
        for (char ch2 : separators.toCharArray()) {
            this.allowedWithoutQuotes.clear(ch2);
        }
        if (!getAllowHttpSepsInV0() && !getForwardSlashIsSeparator()) {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getAllowEqualsInValue() {
        return this.allowEqualsInValue;
    }

    public void setAllowEqualsInValue(boolean allowEqualsInValue) {
        this.allowEqualsInValue = allowEqualsInValue;
    }

    public boolean getAllowNameOnly() {
        return this.allowNameOnly;
    }

    public void setAllowNameOnly(boolean allowNameOnly) {
        this.allowNameOnly = allowNameOnly;
    }

    public boolean getAllowHttpSepsInV0() {
        return this.allowHttpSepsInV0;
    }

    public void setAllowHttpSepsInV0(boolean allowHttpSepsInV0) {
        this.allowHttpSepsInV0 = allowHttpSepsInV0;
        char[] seps = "()<>@:\\\"[]?={}\t".toCharArray();
        for (char sep : seps) {
            if (allowHttpSepsInV0) {
                this.allowedWithoutQuotes.set(sep);
            } else {
                this.allowedWithoutQuotes.clear(sep);
            }
        }
        if (getForwardSlashIsSeparator() && !allowHttpSepsInV0) {
            this.allowedWithoutQuotes.clear(47);
        } else {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getForwardSlashIsSeparator() {
        return this.httpSeparatorFlags.get(47);
    }

    public void setForwardSlashIsSeparator(boolean forwardSlashIsSeparator) {
        if (forwardSlashIsSeparator) {
            this.httpSeparatorFlags.set(47);
        } else {
            this.httpSeparatorFlags.clear(47);
        }
        if (forwardSlashIsSeparator && !getAllowHttpSepsInV0()) {
            this.allowedWithoutQuotes.clear(47);
        } else {
            this.allowedWithoutQuotes.set(47);
        }
    }

    public boolean getAlwaysAddExpires() {
        return this.alwaysAddExpires;
    }

    public void setAlwaysAddExpires(boolean alwaysAddExpires) {
        this.alwaysAddExpires = alwaysAddExpires;
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public Charset getCharset() {
        return StandardCharsets.ISO_8859_1;
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public void parseCookieHeader(MimeHeaders headers, ServerCookies serverCookies) {
        if (headers == null) {
            return;
        }
        int findHeader = headers.findHeader(HttpHeaders.COOKIE, 0);
        while (true) {
            int pos = findHeader;
            if (pos >= 0) {
                MessageBytes cookieValue = headers.getValue(pos);
                if (cookieValue != null && !cookieValue.isNull()) {
                    if (cookieValue.getType() != 2) {
                        Exception e = new Exception();
                        log.debug("Cookies: Parsing cookie as String. Expected bytes.", e);
                        cookieValue.toBytes();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Cookies: Parsing b[]: " + cookieValue.toString());
                    }
                    ByteChunk bc = cookieValue.getByteChunk();
                    processCookieHeader(bc.getBytes(), bc.getOffset(), bc.getLength(), serverCookies);
                }
                findHeader = headers.findHeader(HttpHeaders.COOKIE, pos + 1);
            } else {
                return;
            }
        }
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public String generateHeader(Cookie cookie) {
        int version = cookie.getVersion();
        String value = cookie.getValue();
        String path = cookie.getPath();
        String domain = cookie.getDomain();
        String comment = cookie.getComment();
        if (version == 0 && (needsQuotes(value, 0) || comment != null || needsQuotes(path, 0) || needsQuotes(domain, 0))) {
            version = 1;
        }
        StringBuffer buf = new StringBuffer();
        buf.append(cookie.getName());
        buf.append("=");
        maybeQuote(buf, value, version);
        if (version == 1) {
            buf.append("; Version=1");
            if (comment != null) {
                buf.append("; Comment=");
                maybeQuote(buf, comment, version);
            }
        }
        if (domain != null) {
            buf.append("; Domain=");
            maybeQuote(buf, domain, version);
        }
        int maxAge = cookie.getMaxAge();
        if (maxAge >= 0) {
            if (version > 0) {
                buf.append("; Max-Age=");
                buf.append(maxAge);
            }
            if (version == 0 || getAlwaysAddExpires()) {
                buf.append("; Expires=");
                if (maxAge == 0) {
                    buf.append(ANCIENT_DATE);
                } else {
                    COOKIE_DATE_FORMAT.get().format(new Date(System.currentTimeMillis() + (maxAge * 1000)), buf, new FieldPosition(0));
                }
            }
        }
        if (path != null) {
            buf.append("; Path=");
            maybeQuote(buf, path, version);
        }
        if (cookie.getSecure()) {
            buf.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            buf.append("; HttpOnly");
        }
        return buf.toString();
    }

    private void maybeQuote(StringBuffer buf, String value, int version) {
        if (value == null || value.length() == 0) {
            buf.append("\"\"");
        } else if (alreadyQuoted(value)) {
            buf.append('\"');
            escapeDoubleQuotes(buf, value, 1, value.length() - 1);
            buf.append('\"');
        } else if (needsQuotes(value, version)) {
            buf.append('\"');
            escapeDoubleQuotes(buf, value, 0, value.length());
            buf.append('\"');
        } else {
            buf.append(value);
        }
    }

    private static void escapeDoubleQuotes(StringBuffer b, String s, int beginIndex, int endIndex) {
        if (s.indexOf(34) == -1 && s.indexOf(92) == -1) {
            b.append(s);
            return;
        }
        for (int i = beginIndex; i < endIndex; i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                b.append('\\').append('\\');
            } else if (c == '\"') {
                b.append('\\').append('\"');
            } else {
                b.append(c);
            }
        }
    }

    private boolean needsQuotes(String value, int version) {
        if (value == null) {
            return false;
        }
        int i = 0;
        int len = value.length();
        if (alreadyQuoted(value)) {
            i = 0 + 1;
            len--;
        }
        while (i < len) {
            char c = value.charAt(i);
            if ((c < ' ' && c != '\t') || c >= 127) {
                throw new IllegalArgumentException("Control character in cookie value or attribute.");
            }
            if (version != 0 || this.allowedWithoutQuotes.get(c)) {
                if (version != 1 || !isHttpSeparator(c)) {
                    i++;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private static boolean alreadyQuoted(String value) {
        return value.length() >= 2 && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"';
    }

    /* JADX WARN: Removed duplicated region for block: B:142:0x00db A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00dc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void processCookieHeader(byte[] r8, int r9, int r10, org.apache.tomcat.util.http.ServerCookies r11) {
        /*
            Method dump skipped, instructions count: 967
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.LegacyCookieProcessor.processCookieHeader(byte[], int, int, org.apache.tomcat.util.http.ServerCookies):void");
    }

    private final int getTokenEndPosition(byte[] bytes, int off, int end, int version, boolean isName) {
        int pos = off;
        while (pos < end && (!isHttpSeparator((char) bytes[pos]) || ((version == 0 && getAllowHttpSepsInV0() && bytes[pos] != 61 && !isV0Separator((char) bytes[pos])) || (!isName && bytes[pos] == 61 && getAllowEqualsInValue())))) {
            pos++;
        }
        if (pos > end) {
            return end;
        }
        return pos;
    }

    private boolean isHttpSeparator(char c) {
        if ((c < ' ' || c >= 127) && c != '\t') {
            throw new IllegalArgumentException("Control character in cookie value or attribute.");
        }
        return this.httpSeparatorFlags.get(c);
    }

    private static boolean isV0Separator(char c) {
        if ((c < ' ' || c >= 127) && c != '\t') {
            throw new IllegalArgumentException("Control character in cookie value or attribute.");
        }
        return V0_SEPARATOR_FLAGS.get(c);
    }

    private static final int getQuotedValueEndPosition(byte[] bytes, int off, int end) {
        int pos = off;
        while (pos < end) {
            if (bytes[pos] == 34) {
                return pos;
            }
            if (bytes[pos] == 92 && pos < end - 1) {
                pos += 2;
            } else {
                pos++;
            }
        }
        return end;
    }

    private static final boolean equals(String s, byte[] b, int start, int end) {
        int blen = end - start;
        if (b == null || blen != s.length()) {
            return false;
        }
        int boff = start;
        for (int i = 0; i < blen; i++) {
            int i2 = boff;
            boff++;
            if (b[i2] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static final boolean isWhiteSpace(byte c) {
        if (c == 32 || c == 9 || c == 10 || c == 13 || c == 12) {
            return true;
        }
        return false;
    }

    private static final void unescapeDoubleQuotes(ByteChunk bc) {
        if (bc == null || bc.getLength() == 0 || bc.indexOf('\"', 0) == -1) {
            return;
        }
        byte[] original = bc.getBuffer();
        int len = bc.getLength();
        byte[] copy = new byte[len];
        System.arraycopy(original, bc.getStart(), copy, 0, len);
        int src = 0;
        int dest = 0;
        while (src < len) {
            if (copy[src] == 92 && src < len && copy[src + 1] == 34) {
                src++;
            }
            copy[dest] = copy[src];
            dest++;
            src++;
        }
        bc.setBytes(copy, 0, dest);
    }
}