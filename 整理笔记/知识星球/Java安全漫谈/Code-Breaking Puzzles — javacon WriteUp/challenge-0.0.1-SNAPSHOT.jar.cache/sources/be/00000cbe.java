package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.FieldPosition;
import java.util.BitSet;
import java.util.Date;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.Cookie;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/Rfc6265CookieProcessor.class */
public class Rfc6265CookieProcessor extends CookieProcessorBase {
    private static final Log log = LogFactory.getLog(Rfc6265CookieProcessor.class);
    private static final StringManager sm = StringManager.getManager(Rfc6265CookieProcessor.class.getPackage().getName());
    private static final BitSet domainValid = new BitSet(128);

    static {
        char c = '0';
        while (true) {
            char c2 = c;
            if (c2 > '9') {
                break;
            }
            domainValid.set(c2);
            c = (char) (c2 + 1);
        }
        char c3 = 'a';
        while (true) {
            char c4 = c3;
            if (c4 > 'z') {
                break;
            }
            domainValid.set(c4);
            c3 = (char) (c4 + 1);
        }
        char c5 = 'A';
        while (true) {
            char c6 = c5;
            if (c6 <= 'Z') {
                domainValid.set(c6);
                c5 = (char) (c6 + 1);
            } else {
                domainValid.set(46);
                domainValid.set(45);
                return;
            }
        }
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
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
                        if (log.isDebugEnabled()) {
                            Exception e = new Exception();
                            log.debug("Cookies: Parsing cookie as String. Expected bytes.", e);
                        }
                        cookieValue.toBytes();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Cookies: Parsing b[]: " + cookieValue.toString());
                    }
                    ByteChunk bc = cookieValue.getByteChunk();
                    Cookie.parseCookie(bc.getBytes(), bc.getOffset(), bc.getLength(), serverCookies);
                }
                findHeader = headers.findHeader(HttpHeaders.COOKIE, pos + 1);
            } else {
                return;
            }
        }
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public String generateHeader(javax.servlet.http.Cookie cookie) {
        StringBuffer header = new StringBuffer();
        header.append(cookie.getName());
        header.append('=');
        String value = cookie.getValue();
        if (value != null && value.length() > 0) {
            validateCookieValue(value);
            header.append(value);
        }
        int maxAge = cookie.getMaxAge();
        if (maxAge > -1) {
            header.append("; Max-Age=");
            header.append(maxAge);
            header.append("; Expires=");
            if (maxAge == 0) {
                header.append(ANCIENT_DATE);
            } else {
                COOKIE_DATE_FORMAT.get().format(new Date(System.currentTimeMillis() + (maxAge * 1000)), header, new FieldPosition(0));
            }
        }
        String domain = cookie.getDomain();
        if (domain != null && domain.length() > 0) {
            validateDomain(domain);
            header.append("; Domain=");
            header.append(domain);
        }
        String path = cookie.getPath();
        if (path != null && path.length() > 0) {
            validatePath(path);
            header.append("; Path=");
            header.append(path);
        }
        if (cookie.getSecure()) {
            header.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            header.append("; HttpOnly");
        }
        return header.toString();
    }

    private void validateCookieValue(String value) {
        int start = 0;
        int end = value.length();
        if (end > 1 && value.charAt(0) == '\"' && value.charAt(end - 1) == '\"') {
            start = 1;
            end--;
        }
        char[] chars = value.toCharArray();
        for (int i = start; i < end; i++) {
            char c = chars[i];
            if (c < '!' || c == '\"' || c == ',' || c == ';' || c == '\\' || c == 127) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidCharInValue", Integer.toString(c)));
            }
        }
    }

    private void validateDomain(String domain) {
        int cur = -1;
        char[] chars = domain.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int prev = cur;
            cur = chars[i];
            if (!domainValid.get(cur)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
            if ((prev == 46 || prev == -1) && (cur == 46 || cur == 45)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
            if (prev == 45 && cur == 46) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
        }
        if (cur == 46 || cur == 45) {
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
        }
    }

    private void validatePath(String path) {
        char[] chars = path.toCharArray();
        for (char ch2 : chars) {
            if (ch2 < ' ' || ch2 > '~' || ch2 == ';') {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidPath", path));
            }
        }
    }
}