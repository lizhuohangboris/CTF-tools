package org.apache.catalina.ssi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.WebUtils;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIServlet.class */
public class SSIServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    protected int debug = 0;
    protected boolean buffered = false;
    protected Long expires = null;
    protected boolean isVirtualWebappRelative = false;
    protected String inputEncoding = null;
    protected String outputEncoding = UriEscape.DEFAULT_ENCODING;
    protected boolean allowExec = false;

    @Override // javax.servlet.GenericServlet
    public void init() throws ServletException {
        if (getServletConfig().getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(getServletConfig().getInitParameter("debug"));
        }
        this.isVirtualWebappRelative = Boolean.parseBoolean(getServletConfig().getInitParameter("isVirtualWebappRelative"));
        if (getServletConfig().getInitParameter("expires") != null) {
            this.expires = Long.valueOf(getServletConfig().getInitParameter("expires"));
        }
        this.buffered = Boolean.parseBoolean(getServletConfig().getInitParameter("buffered"));
        this.inputEncoding = getServletConfig().getInitParameter("inputEncoding");
        if (getServletConfig().getInitParameter("outputEncoding") != null) {
            this.outputEncoding = getServletConfig().getInitParameter("outputEncoding");
        }
        this.allowExec = Boolean.parseBoolean(getServletConfig().getInitParameter("allowExec"));
        if (this.debug > 0) {
            log("SSIServlet.init() SSI invoker started with 'debug'=" + this.debug);
        }
    }

    @Override // javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (this.debug > 0) {
            log("SSIServlet.doGet()");
        }
        requestHandler(req, res);
    }

    @Override // javax.servlet.http.HttpServlet
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        if (this.debug > 0) {
            log("SSIServlet.doPost()");
        }
        requestHandler(req, res);
    }

    protected void requestHandler(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletContext servletContext = getServletContext();
        String path = SSIServletRequestUtil.getRelativePath(req);
        if (this.debug > 0) {
            log("SSIServlet.requestHandler()\nServing " + (this.buffered ? "buffered " : "unbuffered ") + "resource '" + path + "'");
        }
        if (path == null || path.toUpperCase(Locale.ENGLISH).startsWith("/WEB-INF") || path.toUpperCase(Locale.ENGLISH).startsWith("/META-INF")) {
            res.sendError(404, path);
            log("Can't serve file: " + path);
            return;
        }
        URL resource = servletContext.getResource(path);
        if (resource == null) {
            res.sendError(404, path);
            log("Can't find file: " + path);
            return;
        }
        String resourceMimeType = servletContext.getMimeType(path);
        if (resourceMimeType == null) {
            resourceMimeType = "text/html";
        }
        res.setContentType(resourceMimeType + WebUtils.CONTENT_TYPE_CHARSET_PREFIX + this.outputEncoding);
        if (this.expires != null) {
            res.setDateHeader(HttpHeaders.EXPIRES, new Date().getTime() + (this.expires.longValue() * 1000));
        }
        processSSI(req, res, resource);
    }

    protected void processSSI(HttpServletRequest req, HttpServletResponse res, URL resource) throws IOException {
        PrintWriter printWriter;
        InputStreamReader isr;
        SSIExternalResolver ssiExternalResolver = new SSIServletExternalResolver(getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, this.inputEncoding);
        SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug, this.allowExec);
        StringWriter stringWriter = null;
        if (this.buffered) {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
        } else {
            printWriter = res.getWriter();
        }
        URLConnection resourceInfo = resource.openConnection();
        InputStream resourceInputStream = resourceInfo.getInputStream();
        String encoding = resourceInfo.getContentEncoding();
        if (encoding == null) {
            encoding = this.inputEncoding;
        }
        if (encoding == null) {
            isr = new InputStreamReader(resourceInputStream);
        } else {
            isr = new InputStreamReader(resourceInputStream, encoding);
        }
        BufferedReader bufferedReader = new BufferedReader(isr);
        long lastModified = ssiProcessor.process(bufferedReader, resourceInfo.getLastModified(), printWriter);
        if (lastModified > 0) {
            res.setDateHeader("last-modified", lastModified);
        }
        if (this.buffered) {
            printWriter.flush();
            String text = stringWriter.toString();
            res.getWriter().write(text);
        }
        bufferedReader.close();
    }
}