package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.tomcat.util.http.FastHttpDateFormat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ResponseIncludeWrapper.class */
public class ResponseIncludeWrapper extends HttpServletResponseWrapper {
    private static final String LAST_MODIFIED = "last-modified";
    protected long lastModified;
    protected final ServletOutputStream captureServletOutputStream;
    protected ServletOutputStream servletOutputStream;
    protected PrintWriter printWriter;

    public ResponseIncludeWrapper(HttpServletResponse response, ServletOutputStream captureServletOutputStream) {
        super(response);
        this.lastModified = -1L;
        this.captureServletOutputStream = captureServletOutputStream;
    }

    public void flushOutputStreamOrWriter() throws IOException {
        if (this.servletOutputStream != null) {
            this.servletOutputStream.flush();
        }
        if (this.printWriter != null) {
            this.printWriter.flush();
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        if (this.servletOutputStream == null) {
            if (this.printWriter == null) {
                setCharacterEncoding(getCharacterEncoding());
                this.printWriter = new PrintWriter(new OutputStreamWriter(this.captureServletOutputStream, getCharacterEncoding()));
            }
            return this.printWriter;
        }
        throw new IllegalStateException();
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.printWriter == null) {
            if (this.servletOutputStream == null) {
                this.servletOutputStream = this.captureServletOutputStream;
            }
            return this.servletOutputStream;
        }
        throw new IllegalStateException();
    }

    public long getLastModified() {
        return this.lastModified;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long value) {
        super.addDateHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            this.lastModified = value;
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            long lastModified = FastHttpDateFormat.parseDate(value);
            if (lastModified != -1) {
                this.lastModified = lastModified;
            }
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long value) {
        super.setDateHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            this.lastModified = value;
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        String lname = name.toLowerCase(Locale.ENGLISH);
        if (lname.equals(LAST_MODIFIED)) {
            long lastModified = FastHttpDateFormat.parseDate(value);
            if (lastModified != -1) {
                this.lastModified = lastModified;
            }
        }
    }
}