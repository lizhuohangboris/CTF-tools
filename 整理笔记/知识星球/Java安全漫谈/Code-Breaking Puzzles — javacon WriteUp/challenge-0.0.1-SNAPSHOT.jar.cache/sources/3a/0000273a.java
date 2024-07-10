package org.springframework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.FastByteArrayOutputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/ContentCachingResponseWrapper.class */
public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {
    private final FastByteArrayOutputStream content;
    @Nullable
    private ServletOutputStream outputStream;
    @Nullable
    private PrintWriter writer;
    private int statusCode;
    @Nullable
    private Integer contentLength;

    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
        this.content = new FastByteArrayOutputStream(1024);
        this.statusCode = 200;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setStatus(int sc, String sm) {
        super.setStatus(sc, sm);
        this.statusCode = sc;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc);
        } catch (IllegalStateException e) {
            super.setStatus(sc);
        }
        this.statusCode = sc;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        copyBodyToResponse(false);
        try {
            super.sendError(sc, msg);
        } catch (IllegalStateException e) {
            super.setStatus(sc, msg);
        }
        this.statusCode = sc;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        copyBodyToResponse(false);
        super.sendRedirect(location);
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.outputStream == null) {
            this.outputStream = new ResponseServletOutputStream(getResponse().getOutputStream());
        }
        return this.outputStream;
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        if (this.writer == null) {
            String characterEncoding = getCharacterEncoding();
            this.writer = characterEncoding != null ? new ResponsePrintWriter(characterEncoding) : new ResponsePrintWriter("ISO-8859-1");
        }
        return this.writer;
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void flushBuffer() throws IOException {
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLength(int len) {
        if (len > this.content.size()) {
            this.content.resize(len);
        }
        this.contentLength = Integer.valueOf(len);
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (len > 2147483647L) {
            throw new IllegalArgumentException("Content-Length exceeds ContentCachingResponseWrapper's maximum (2147483647): " + len);
        }
        int lenInt = (int) len;
        if (lenInt > this.content.size()) {
            this.content.resize(lenInt);
        }
        this.contentLength = Integer.valueOf(lenInt);
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (size > this.content.size()) {
            this.content.resize(size);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void resetBuffer() {
        this.content.reset();
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void reset() {
        super.reset();
        this.content.reset();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public byte[] getContentAsByteArray() {
        return this.content.toByteArray();
    }

    public InputStream getContentInputStream() {
        return this.content.getInputStream();
    }

    public int getContentSize() {
        return this.content.size();
    }

    public void copyBodyToResponse() throws IOException {
        copyBodyToResponse(true);
    }

    protected void copyBodyToResponse(boolean complete) throws IOException {
        if (this.content.size() > 0) {
            HttpServletResponse rawResponse = (HttpServletResponse) getResponse();
            if ((complete || this.contentLength != null) && !rawResponse.isCommitted()) {
                rawResponse.setContentLength(complete ? this.content.size() : this.contentLength.intValue());
                this.contentLength = null;
            }
            this.content.writeTo(rawResponse.getOutputStream());
            this.content.reset();
            if (complete) {
                super.flushBuffer();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/ContentCachingResponseWrapper$ResponseServletOutputStream.class */
    private class ResponseServletOutputStream extends ServletOutputStream {
        private final ServletOutputStream os;

        public ResponseServletOutputStream(ServletOutputStream os) {
            this.os = os;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            ContentCachingResponseWrapper.this.content.write(b, off, len);
        }

        @Override // javax.servlet.ServletOutputStream
        public boolean isReady() {
            return this.os.isReady();
        }

        @Override // javax.servlet.ServletOutputStream
        public void setWriteListener(WriteListener writeListener) {
            this.os.setWriteListener(writeListener);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/ContentCachingResponseWrapper$ResponsePrintWriter.class */
    private class ResponsePrintWriter extends PrintWriter {
        public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException {
            super(new OutputStreamWriter(ContentCachingResponseWrapper.this.content, characterEncoding));
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
        }

        @Override // java.io.PrintWriter, java.io.Writer
        public void write(int c) {
            super.write(c);
            super.flush();
        }
    }
}