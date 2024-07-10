package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/SetCharacterEncodingFilter.class */
public class SetCharacterEncodingFilter extends FilterBase {
    private final Log log = LogFactory.getLog(SetCharacterEncodingFilter.class);
    private String encoding = null;
    private boolean ignore = false;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isIgnore() {
        return this.ignore;
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String characterEncoding;
        if ((this.ignore || request.getCharacterEncoding() == null) && (characterEncoding = selectEncoding(request)) != null) {
            request.setCharacterEncoding(characterEncoding);
        }
        chain.doFilter(request, response);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    protected String selectEncoding(ServletRequest request) {
        return this.encoding;
    }
}