package org.apache.catalina.filters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RequestDumperFilter.class */
public class RequestDumperFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    private static final String NON_HTTP_REQ_MSG = "Not available. Non-http request.";
    private static final String NON_HTTP_RES_MSG = "Not available. Non-http response.";
    private static final ThreadLocal<Timestamp> timestamp = new ThreadLocal<Timestamp>() { // from class: org.apache.catalina.filters.RequestDumperFilter.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Timestamp initialValue() {
            return new Timestamp();
        }
    };
    private transient Log log = LogFactory.getLog(RequestDumperFilter.class);

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = null;
        HttpServletResponse hResponse = null;
        if (request instanceof HttpServletRequest) {
            hRequest = (HttpServletRequest) request;
        }
        if (response instanceof HttpServletResponse) {
            hResponse = (HttpServletResponse) response;
        }
        doLog("START TIME        ", getTimestamp());
        if (hRequest == null) {
            doLog("        requestURI", NON_HTTP_REQ_MSG);
            doLog("          authType", NON_HTTP_REQ_MSG);
        } else {
            doLog("        requestURI", hRequest.getRequestURI());
            doLog("          authType", hRequest.getAuthType());
        }
        doLog(" characterEncoding", request.getCharacterEncoding());
        doLog("     contentLength", Long.toString(request.getContentLengthLong()));
        doLog("       contentType", request.getContentType());
        if (hRequest == null) {
            doLog("       contextPath", NON_HTTP_REQ_MSG);
            doLog("            cookie", NON_HTTP_REQ_MSG);
            doLog("            header", NON_HTTP_REQ_MSG);
        } else {
            doLog("       contextPath", hRequest.getContextPath());
            Cookie[] cookies = hRequest.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    doLog("            cookie", cookies[i].getName() + "=" + cookies[i].getValue());
                }
            }
            Enumeration<String> hnames = hRequest.getHeaderNames();
            while (hnames.hasMoreElements()) {
                String hname = hnames.nextElement();
                Enumeration<String> hvalues = hRequest.getHeaders(hname);
                while (hvalues.hasMoreElements()) {
                    String hvalue = hvalues.nextElement();
                    doLog("            header", hname + "=" + hvalue);
                }
            }
        }
        doLog("            locale", request.getLocale().toString());
        if (hRequest == null) {
            doLog("            method", NON_HTTP_REQ_MSG);
        } else {
            doLog("            method", hRequest.getMethod());
        }
        Enumeration<String> pnames = request.getParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = pnames.nextElement();
            String[] pvalues = request.getParameterValues(pname);
            StringBuilder result = new StringBuilder(pname);
            result.append('=');
            for (int i2 = 0; i2 < pvalues.length; i2++) {
                if (i2 > 0) {
                    result.append(", ");
                }
                result.append(pvalues[i2]);
            }
            doLog("         parameter", result.toString());
        }
        if (hRequest == null) {
            doLog("          pathInfo", NON_HTTP_REQ_MSG);
        } else {
            doLog("          pathInfo", hRequest.getPathInfo());
        }
        doLog("          protocol", request.getProtocol());
        if (hRequest == null) {
            doLog("       queryString", NON_HTTP_REQ_MSG);
        } else {
            doLog("       queryString", hRequest.getQueryString());
        }
        doLog("        remoteAddr", request.getRemoteAddr());
        doLog("        remoteHost", request.getRemoteHost());
        if (hRequest == null) {
            doLog("        remoteUser", NON_HTTP_REQ_MSG);
            doLog("requestedSessionId", NON_HTTP_REQ_MSG);
        } else {
            doLog("        remoteUser", hRequest.getRemoteUser());
            doLog("requestedSessionId", hRequest.getRequestedSessionId());
        }
        doLog("            scheme", request.getScheme());
        doLog("        serverName", request.getServerName());
        doLog("        serverPort", Integer.toString(request.getServerPort()));
        if (hRequest == null) {
            doLog("       servletPath", NON_HTTP_REQ_MSG);
        } else {
            doLog("       servletPath", hRequest.getServletPath());
        }
        doLog("          isSecure", Boolean.valueOf(request.isSecure()).toString());
        doLog("------------------", "--------------------------------------------");
        chain.doFilter(request, response);
        doLog("------------------", "--------------------------------------------");
        if (hRequest == null) {
            doLog("          authType", NON_HTTP_REQ_MSG);
        } else {
            doLog("          authType", hRequest.getAuthType());
        }
        doLog("       contentType", response.getContentType());
        if (hResponse == null) {
            doLog("            header", NON_HTTP_RES_MSG);
        } else {
            Iterable<String> rhnames = hResponse.getHeaderNames();
            for (String rhname : rhnames) {
                Iterable<String> rhvalues = hResponse.getHeaders(rhname);
                for (String rhvalue : rhvalues) {
                    doLog("            header", rhname + "=" + rhvalue);
                }
            }
        }
        if (hRequest == null) {
            doLog("        remoteUser", NON_HTTP_REQ_MSG);
        } else {
            doLog("        remoteUser", hRequest.getRemoteUser());
        }
        if (hResponse == null) {
            doLog("            status", NON_HTTP_RES_MSG);
        } else {
            doLog("            status", Integer.toString(hResponse.getStatus()));
        }
        doLog("END TIME          ", getTimestamp());
        doLog("==================", "============================================");
    }

    private void doLog(String attribute, String value) {
        StringBuilder sb = new StringBuilder(80);
        sb.append(Thread.currentThread().getName());
        sb.append(' ');
        sb.append(attribute);
        sb.append('=');
        sb.append(value);
        this.log.info(sb.toString());
    }

    private String getTimestamp() {
        Timestamp ts = timestamp.get();
        long currentTime = System.currentTimeMillis();
        if (ts.date.getTime() + 999 < currentTime) {
            ts.date.setTime(currentTime - (currentTime % 1000));
            ts.update();
        }
        return ts.dateString;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.log = LogFactory.getLog(RequestDumperFilter.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RequestDumperFilter$Timestamp.class */
    public static final class Timestamp {
        private final Date date;
        private final SimpleDateFormat format;
        private String dateString;

        private Timestamp() {
            this.date = new Date(0L);
            this.format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            this.dateString = this.format.format(this.date);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void update() {
            this.dateString = this.format.format(this.date);
        }
    }
}