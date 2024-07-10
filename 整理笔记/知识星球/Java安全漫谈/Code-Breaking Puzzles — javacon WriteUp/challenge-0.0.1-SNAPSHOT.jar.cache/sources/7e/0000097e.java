package org.apache.catalina.valves;

import ch.qos.logback.classic.spi.CallerData;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.valves.AbstractAccessLogValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.web.servlet.tags.BindTag;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve.class */
public class ExtendedAccessLogValve extends AccessLogValve {
    private static final Log log = LogFactory.getLog(ExtendedAccessLogValve.class);
    protected static final String extendedAccessLogInfo = "org.apache.catalina.valves.ExtendedAccessLogValve/2.1";

    static String wrap(Object value) {
        if (value == null || "-".equals(value)) {
            return "-";
        }
        try {
            String svalue = value.toString();
            StringBuilder buffer = new StringBuilder(svalue.length() + 2);
            buffer.append('\"');
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < svalue.length()) {
                    int j = svalue.indexOf(34, i2);
                    if (j == -1) {
                        buffer.append(svalue.substring(i2));
                        i = svalue.length();
                    } else {
                        buffer.append(svalue.substring(i2, j + 1));
                        buffer.append('\"');
                        i = j + 1;
                    }
                } else {
                    buffer.append('\"');
                    return buffer.toString();
                }
            }
        } catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            return "-";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.AccessLogValve
    public synchronized void open() {
        super.open();
        if (this.currentLogFile.length() == 0) {
            this.writer.println("#Fields: " + this.pattern);
            this.writer.println("#Version: 2.0");
            this.writer.println("#Software: " + ServerInfo.getServerInfo());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$DateElement.class */
    public static class DateElement implements AbstractAccessLogValve.AccessLogElement {
        private static final long INTERVAL = 86400000;
        private static final ThreadLocal<ElementTimestampStruct> currentDate = new ThreadLocal<ElementTimestampStruct>() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.DateElement.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public ElementTimestampStruct initialValue() {
                return new ElementTimestampStruct("yyyy-MM-dd");
            }
        };

        protected DateElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            ElementTimestampStruct eds = currentDate.get();
            long millis = eds.currentTimestamp.getTime();
            if (date.getTime() > (millis + 86400000) - 1 || date.getTime() < millis) {
                eds.currentTimestamp.setTime(date.getTime() - (date.getTime() % 86400000));
                eds.currentTimestampString = eds.currentTimestampFormat.format(eds.currentTimestamp);
            }
            buf.append((CharSequence) eds.currentTimestampString);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$TimeElement.class */
    public static class TimeElement implements AbstractAccessLogValve.AccessLogElement {
        private static final long INTERVAL = 1000;
        private static final ThreadLocal<ElementTimestampStruct> currentTime = new ThreadLocal<ElementTimestampStruct>() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.TimeElement.1
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public ElementTimestampStruct initialValue() {
                return new ElementTimestampStruct("HH:mm:ss");
            }
        };

        protected TimeElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            ElementTimestampStruct eds = currentTime.get();
            long millis = eds.currentTimestamp.getTime();
            if (date.getTime() > (millis + 1000) - 1 || date.getTime() < millis) {
                eds.currentTimestamp.setTime(date.getTime() - (date.getTime() % 1000));
                eds.currentTimestampString = eds.currentTimestampFormat.format(eds.currentTimestamp);
            }
            buf.append((CharSequence) eds.currentTimestampString);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$RequestHeaderElement.class */
    public static class RequestHeaderElement implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public RequestHeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getHeader(this.header)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$ResponseHeaderElement.class */
    public static class ResponseHeaderElement implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) ExtendedAccessLogValve.wrap(response.getHeader(this.header)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$ServletContextElement.class */
    public static class ServletContextElement implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public ServletContextElement(String attribute) {
            this.attribute = attribute;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getContext().getServletContext().getAttribute(this.attribute)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$CookieElement.class */
    public static class CookieElement implements AbstractAccessLogValve.AccessLogElement {
        private final String name;

        public CookieElement(String name) {
            this.name = name;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Cookie[] c = request.getCookies();
            for (int i = 0; c != null && i < c.length; i++) {
                if (this.name.equals(c[i].getName())) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(c[i].getValue()));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$ResponseAllHeaderElement.class */
    public static class ResponseAllHeaderElement implements AbstractAccessLogValve.AccessLogElement {
        private final String header;

        public ResponseAllHeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (null != response) {
                Iterator<String> iter = response.getHeaders(this.header).iterator();
                if (iter.hasNext()) {
                    StringBuilder buffer = new StringBuilder();
                    boolean first = true;
                    while (iter.hasNext()) {
                        if (first) {
                            first = false;
                        } else {
                            buffer.append(",");
                        }
                        buffer.append(iter.next());
                    }
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(buffer.toString()));
                    return;
                }
                return;
            }
            buf.append("-");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$RequestAttributeElement.class */
    public static class RequestAttributeElement implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public RequestAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getAttribute(this.attribute)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$SessionAttributeElement.class */
    public static class SessionAttributeElement implements AbstractAccessLogValve.AccessLogElement {
        private final String attribute;

        public SessionAttributeElement(String attribute) {
            this.attribute = attribute;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            HttpSession session;
            if (request != null && (session = request.getSession(false)) != null) {
                buf.append((CharSequence) ExtendedAccessLogValve.wrap(session.getAttribute(this.attribute)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$RequestParameterElement.class */
    public static class RequestParameterElement implements AbstractAccessLogValve.AccessLogElement {
        private final String parameter;

        public RequestParameterElement(String parameter) {
            this.parameter = parameter;
        }

        private String urlEncode(String value) {
            if (null == value || value.length() == 0) {
                return null;
            }
            try {
                return URLEncoder.encode(value, UriEscape.DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) ExtendedAccessLogValve.wrap(urlEncode(request.getParameter(this.parameter))));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$PatternTokenizer.class */
    public static class PatternTokenizer {
        private final StringReader sr;
        private StringBuilder buf = new StringBuilder();
        private boolean ended = false;
        private boolean subToken;
        private boolean parameter;

        public PatternTokenizer(String str) {
            this.sr = new StringReader(str);
        }

        public boolean hasSubToken() {
            return this.subToken;
        }

        public boolean hasParameter() {
            return this.parameter;
        }

        public String getToken() throws IOException {
            if (this.ended) {
                return null;
            }
            this.subToken = false;
            this.parameter = false;
            int read = this.sr.read();
            while (true) {
                int c = read;
                if (c != -1) {
                    switch (c) {
                        case 32:
                            String result = this.buf.toString();
                            this.buf = new StringBuilder();
                            this.buf.append((char) c);
                            return result;
                        case 40:
                            String result2 = this.buf.toString();
                            this.buf = new StringBuilder();
                            this.parameter = true;
                            return result2;
                        case 41:
                            this.buf.toString();
                            this.buf = new StringBuilder();
                            break;
                        case 45:
                            String result3 = this.buf.toString();
                            this.buf = new StringBuilder();
                            this.subToken = true;
                            return result3;
                        default:
                            this.buf.append((char) c);
                            break;
                    }
                    read = this.sr.read();
                } else {
                    this.ended = true;
                    if (this.buf.length() != 0) {
                        return this.buf.toString();
                    }
                    return null;
                }
            }
        }

        public String getParameter() throws IOException {
            if (!this.parameter) {
                return null;
            }
            this.parameter = false;
            int read = this.sr.read();
            while (true) {
                int c = read;
                if (c != -1) {
                    if (c == 41) {
                        String result = this.buf.toString();
                        this.buf = new StringBuilder();
                        return result;
                    }
                    this.buf.append((char) c);
                    read = this.sr.read();
                } else {
                    return null;
                }
            }
        }

        public String getWhiteSpaces() throws IOException {
            int c;
            if (isEnded()) {
                return "";
            }
            StringBuilder whiteSpaces = new StringBuilder();
            if (this.buf.length() > 0) {
                whiteSpaces.append((CharSequence) this.buf);
                this.buf = new StringBuilder();
            }
            int read = this.sr.read();
            while (true) {
                c = read;
                if (!Character.isWhitespace((char) c)) {
                    break;
                }
                whiteSpaces.append((char) c);
                read = this.sr.read();
            }
            if (c == -1) {
                this.ended = true;
            } else {
                this.buf.append((char) c);
            }
            return whiteSpaces.toString();
        }

        public boolean isEnded() {
            return this.ended;
        }

        public String getRemains() throws IOException {
            StringBuilder remains = new StringBuilder();
            int read = this.sr.read();
            while (true) {
                int c = read;
                if (c != -1) {
                    remains.append((char) c);
                    read = this.sr.read();
                } else {
                    return remains.toString();
                }
            }
        }
    }

    @Override // org.apache.catalina.valves.AbstractAccessLogValve
    protected AbstractAccessLogValve.AccessLogElement[] createLogElements() {
        if (log.isDebugEnabled()) {
            log.debug("decodePattern, pattern =" + this.pattern);
        }
        List<AbstractAccessLogValve.AccessLogElement> list = new ArrayList<>();
        PatternTokenizer tokenizer = new PatternTokenizer(this.pattern);
        try {
            tokenizer.getWhiteSpaces();
            if (tokenizer.isEnded()) {
                log.info("pattern was just empty or whitespace");
                return null;
            }
            for (String token = tokenizer.getToken(); token != null; token = tokenizer.getToken()) {
                if (log.isDebugEnabled()) {
                    log.debug("token = " + token);
                }
                AbstractAccessLogValve.AccessLogElement element = getLogElement(token, tokenizer);
                if (element == null) {
                    break;
                }
                list.add(element);
                String whiteSpaces = tokenizer.getWhiteSpaces();
                if (whiteSpaces.length() > 0) {
                    list.add(new AbstractAccessLogValve.StringElement(whiteSpaces));
                }
                if (tokenizer.isEnded()) {
                    break;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("finished decoding with element size of: " + list.size());
            }
            return (AbstractAccessLogValve.AccessLogElement[]) list.toArray(new AbstractAccessLogValve.AccessLogElement[0]);
        } catch (IOException e) {
            log.error("parse error", e);
            return null;
        }
    }

    protected AbstractAccessLogValve.AccessLogElement getLogElement(String token, PatternTokenizer tokenizer) throws IOException {
        if (SpringInputGeneralFieldTagProcessor.DATE_INPUT_TYPE_ATTR_VALUE.equals(token)) {
            return new DateElement();
        }
        if (SpringInputGeneralFieldTagProcessor.TIME_INPUT_TYPE_ATTR_VALUE.equals(token)) {
            if (tokenizer.hasSubToken()) {
                if ("taken".equals(tokenizer.getToken())) {
                    return new AbstractAccessLogValve.ElapsedTimeElement(false);
                }
            } else {
                return new TimeElement();
            }
        } else if ("bytes".equals(token)) {
            return new AbstractAccessLogValve.ByteSentElement(true);
        } else {
            if ("cached".equals(token)) {
                return new AbstractAccessLogValve.StringElement("-");
            }
            if ("c".equals(token)) {
                String nextToken = tokenizer.getToken();
                if ("ip".equals(nextToken)) {
                    return new AbstractAccessLogValve.RemoteAddrElement();
                }
                if ("dns".equals(nextToken)) {
                    return new AbstractAccessLogValve.HostElement();
                }
            } else if ("s".equals(token)) {
                String nextToken2 = tokenizer.getToken();
                if ("ip".equals(nextToken2)) {
                    return new AbstractAccessLogValve.LocalAddrElement(getIpv6Canonical());
                }
                if ("dns".equals(nextToken2)) {
                    return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.1
                        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                            String value;
                            try {
                                value = InetAddress.getLocalHost().getHostName();
                            } catch (Throwable e) {
                                ExceptionUtils.handleThrowable(e);
                                value = "localhost";
                            }
                            buf.append((CharSequence) value);
                        }
                    };
                }
            } else if ("cs".equals(token)) {
                return getClientToServerElement(tokenizer);
            } else {
                if ("sc".equals(token)) {
                    return getServerToClientElement(tokenizer);
                }
                if ("sr".equals(token) || "rs".equals(token)) {
                    return getProxyElement(tokenizer);
                }
                if ("x".equals(token)) {
                    return getXParameterElement(tokenizer);
                }
            }
        }
        log.error("unable to decode with rest of chars starting: " + token);
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getClientToServerElement(PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            String token = tokenizer.getToken();
            if ("method".equals(token)) {
                return new AbstractAccessLogValve.MethodElement();
            }
            if ("uri".equals(token)) {
                if (tokenizer.hasSubToken()) {
                    String token2 = tokenizer.getToken();
                    if ("stem".equals(token2)) {
                        return new AbstractAccessLogValve.RequestURIElement();
                    }
                    if ("query".equals(token2)) {
                        return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.2
                            @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                            public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                                String query = request.getQueryString();
                                if (query != null) {
                                    buf.append((CharSequence) query);
                                } else {
                                    buf.append('-');
                                }
                            }
                        };
                    }
                } else {
                    return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.3
                        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                            String query = request.getQueryString();
                            if (query == null) {
                                buf.append((CharSequence) request.getRequestURI());
                                return;
                            }
                            buf.append((CharSequence) request.getRequestURI());
                            buf.append('?');
                            buf.append((CharSequence) request.getQueryString());
                        }
                    };
                }
            }
        } else if (tokenizer.hasParameter()) {
            String parameter = tokenizer.getParameter();
            if (parameter == null) {
                log.error("No closing ) found for in decode");
                return null;
            }
            return new RequestHeaderElement(parameter);
        }
        log.error("The next characters couldn't be decoded: " + tokenizer.getRemains());
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getServerToClientElement(PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            String token = tokenizer.getToken();
            if (BindTag.STATUS_VARIABLE_NAME.equals(token)) {
                return new AbstractAccessLogValve.HttpStatusCodeElement();
            }
            if ("comment".equals(token)) {
                return new AbstractAccessLogValve.StringElement(CallerData.NA);
            }
        } else if (tokenizer.hasParameter()) {
            String parameter = tokenizer.getParameter();
            if (parameter == null) {
                log.error("No closing ) found for in decode");
                return null;
            }
            return new ResponseHeaderElement(parameter);
        }
        log.error("The next characters couldn't be decoded: " + tokenizer.getRemains());
        return null;
    }

    protected AbstractAccessLogValve.AccessLogElement getProxyElement(PatternTokenizer tokenizer) throws IOException {
        if (tokenizer.hasSubToken()) {
            tokenizer.getToken();
            return new AbstractAccessLogValve.StringElement("-");
        } else if (!tokenizer.hasParameter()) {
            log.error("The next characters couldn't be decoded: " + ((String) null));
            return null;
        } else {
            tokenizer.getParameter();
            return new AbstractAccessLogValve.StringElement("-");
        }
    }

    protected AbstractAccessLogValve.AccessLogElement getXParameterElement(PatternTokenizer tokenizer) throws IOException {
        if (!tokenizer.hasSubToken()) {
            log.error("x param in wrong format. Needs to be 'x-#(...)' read the docs!");
            return null;
        }
        String token = tokenizer.getToken();
        if ("threadname".equals(token)) {
            return new AbstractAccessLogValve.ThreadNameElement();
        }
        if (!tokenizer.hasParameter()) {
            log.error("x param in wrong format. Needs to be 'x-#(...)' read the docs!");
            return null;
        }
        String parameter = tokenizer.getParameter();
        if (parameter == null) {
            log.error("No closing ) found for in decode");
            return null;
        } else if ("A".equals(token)) {
            return new ServletContextElement(parameter);
        } else {
            if ("C".equals(token)) {
                return new CookieElement(parameter);
            }
            if ("R".equals(token)) {
                return new RequestAttributeElement(parameter);
            }
            if ("S".equals(token)) {
                return new SessionAttributeElement(parameter);
            }
            if ("H".equals(token)) {
                return getServletRequestElement(parameter);
            }
            if ("P".equals(token)) {
                return new RequestParameterElement(parameter);
            }
            if ("O".equals(token)) {
                return new ResponseAllHeaderElement(parameter);
            }
            log.error("x param for servlet request, couldn't decode value: " + token);
            return null;
        }
    }

    protected AbstractAccessLogValve.AccessLogElement getServletRequestElement(String parameter) {
        if ("authType".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.4
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getAuthType()));
                }
            };
        }
        if ("remoteUser".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.5
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getRemoteUser()));
                }
            };
        }
        if ("requestedSessionId".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.6
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getRequestedSessionId()));
                }
            };
        }
        if ("requestedSessionIdFromCookie".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.7
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap("" + request.isRequestedSessionIdFromCookie()));
                }
            };
        }
        if ("requestedSessionIdValid".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.8
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap("" + request.isRequestedSessionIdValid()));
                }
            };
        }
        if ("contentLength".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.9
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap("" + request.getContentLengthLong()));
                }
            };
        }
        if ("characterEncoding".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.10
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getCharacterEncoding()));
                }
            };
        }
        if ("locale".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.11
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getLocale()));
                }
            };
        }
        if ("protocol".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.12
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap(request.getProtocol()));
                }
            };
        }
        if ("scheme".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.13
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) request.getScheme());
                }
            };
        }
        if ("secure".equals(parameter)) {
            return new AbstractAccessLogValve.AccessLogElement() { // from class: org.apache.catalina.valves.ExtendedAccessLogValve.14
                @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
                public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
                    buf.append((CharSequence) ExtendedAccessLogValve.wrap("" + request.isSecure()));
                }
            };
        }
        log.error("x param for servlet request, couldn't decode value: " + parameter);
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/ExtendedAccessLogValve$ElementTimestampStruct.class */
    public static class ElementTimestampStruct {
        private final Date currentTimestamp = new Date(0);
        private final SimpleDateFormat currentTimestampFormat;
        private String currentTimestampString;

        ElementTimestampStruct(String format) {
            this.currentTimestampFormat = new SimpleDateFormat(format, Locale.US);
            this.currentTimestampFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    }
}