package org.apache.catalina.ssi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIFilter.class */
public class SSIFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    protected int debug = 0;
    protected Long expires = null;
    protected boolean isVirtualWebappRelative = false;
    protected Pattern contentTypeRegEx = null;
    protected final Pattern shtmlRegEx = Pattern.compile("text/x-server-parsed-html(;.*)?");
    protected boolean allowExec = false;

    @Override // javax.servlet.GenericFilter
    public void init() throws ServletException {
        if (getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(getInitParameter("debug"));
        }
        if (getInitParameter("contentType") != null) {
            this.contentTypeRegEx = Pattern.compile(getInitParameter("contentType"));
        } else {
            this.contentTypeRegEx = this.shtmlRegEx;
        }
        this.isVirtualWebappRelative = Boolean.parseBoolean(getInitParameter("isVirtualWebappRelative"));
        if (getInitParameter("expires") != null) {
            this.expires = Long.valueOf(getInitParameter("expires"));
        }
        this.allowExec = Boolean.parseBoolean(getInitParameter("allowExec"));
        if (this.debug > 0) {
            getServletContext().log("SSIFilter.init() SSI invoker started with 'debug'=" + this.debug);
        }
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
        ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(res, basos);
        chain.doFilter(req, responseIncludeWrapper);
        responseIncludeWrapper.flushOutputStreamOrWriter();
        byte[] bytes = basos.toByteArray();
        String contentType = responseIncludeWrapper.getContentType();
        if (contentType != null && this.contentTypeRegEx.matcher(contentType).matches()) {
            String encoding = res.getCharacterEncoding();
            SSIExternalResolver ssiExternalResolver = new SSIServletExternalResolver(getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, encoding);
            SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug, this.allowExec);
            Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
            ByteArrayOutputStream ssiout = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(ssiout, encoding));
            long lastModified = ssiProcessor.process(reader, responseIncludeWrapper.getLastModified(), writer);
            writer.flush();
            bytes = ssiout.toByteArray();
            if (this.expires != null) {
                res.setDateHeader("expires", new Date().getTime() + (this.expires.longValue() * 1000));
            }
            if (lastModified > 0) {
                res.setDateHeader("last-modified", lastModified);
            }
            res.setContentLength(bytes.length);
            Matcher shtmlMatcher = this.shtmlRegEx.matcher(responseIncludeWrapper.getContentType());
            if (shtmlMatcher.matches()) {
                String enc = shtmlMatcher.group(1);
                res.setContentType("text/html" + (enc != null ? enc : ""));
            }
        }
        OutputStream out = null;
        try {
            out = res.getOutputStream();
        } catch (IllegalStateException e) {
        }
        if (out == null) {
            res.getWriter().write(new String(bytes));
        } else {
            out.write(bytes);
        }
    }
}