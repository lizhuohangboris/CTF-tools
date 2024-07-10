package org.apache.catalina.filters;

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/AddDefaultCharsetFilter.class */
public class AddDefaultCharsetFilter extends FilterBase {
    private final Log log = LogFactory.getLog(AddDefaultCharsetFilter.class);
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private String encoding;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    @Override // org.apache.catalina.filters.FilterBase, javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (this.encoding == null || this.encoding.length() == 0 || this.encoding.equalsIgnoreCase("default")) {
            this.encoding = "ISO-8859-1";
        } else if (this.encoding.equalsIgnoreCase("system")) {
            this.encoding = Charset.defaultCharset().name();
        } else if (!Charset.isSupported(this.encoding)) {
            throw new IllegalArgumentException(sm.getString("addDefaultCharset.unsupportedCharset", this.encoding));
        }
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            ResponseWrapper wrapped = new ResponseWrapper((HttpServletResponse) response, this.encoding);
            chain.doFilter(request, wrapped);
            return;
        }
        chain.doFilter(request, response);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/AddDefaultCharsetFilter$ResponseWrapper.class */
    public static class ResponseWrapper extends HttpServletResponseWrapper {
        private String encoding;

        public ResponseWrapper(HttpServletResponse response, String encoding) {
            super(response);
            this.encoding = encoding;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void setContentType(String ct) {
            if (ct != null && ct.startsWith("text/")) {
                if (!ct.contains("charset=")) {
                    super.setContentType(ct + WebUtils.CONTENT_TYPE_CHARSET_PREFIX + this.encoding);
                    return;
                }
                super.setContentType(ct);
                this.encoding = getCharacterEncoding();
                return;
            }
            super.setContentType(ct);
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void setHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                setContentType(value);
            } else {
                super.setHeader(name, value);
            }
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void addHeader(String name, String value) {
            if (name.trim().equalsIgnoreCase("content-type")) {
                setContentType(value);
            } else {
                super.addHeader(name, value);
            }
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void setCharacterEncoding(String charset) {
            super.setCharacterEncoding(charset);
            this.encoding = charset;
        }
    }
}