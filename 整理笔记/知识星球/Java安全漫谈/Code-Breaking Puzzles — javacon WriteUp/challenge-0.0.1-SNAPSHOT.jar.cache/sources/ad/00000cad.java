package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import javax.servlet.http.Cookie;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/CookieProcessor.class */
public interface CookieProcessor {
    void parseCookieHeader(MimeHeaders mimeHeaders, ServerCookies serverCookies);

    String generateHeader(Cookie cookie);

    Charset getCharset();
}