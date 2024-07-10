package javax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletResponseWrapper.class */
public class ServletResponseWrapper implements ServletResponse {
    private ServletResponse response;

    public ServletResponseWrapper(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }

    @Override // javax.servlet.ServletResponse
    public void setCharacterEncoding(String charset) {
        this.response.setCharacterEncoding(charset);
    }

    @Override // javax.servlet.ServletResponse
    public String getCharacterEncoding() {
        return this.response.getCharacterEncoding();
    }

    @Override // javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.response.getOutputStream();
    }

    @Override // javax.servlet.ServletResponse
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLength(int len) {
        this.response.setContentLength(len);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentLengthLong(long length) {
        this.response.setContentLengthLong(length);
    }

    @Override // javax.servlet.ServletResponse
    public void setContentType(String type) {
        this.response.setContentType(type);
    }

    @Override // javax.servlet.ServletResponse
    public String getContentType() {
        return this.response.getContentType();
    }

    @Override // javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        this.response.setBufferSize(size);
    }

    @Override // javax.servlet.ServletResponse
    public int getBufferSize() {
        return this.response.getBufferSize();
    }

    @Override // javax.servlet.ServletResponse
    public void flushBuffer() throws IOException {
        this.response.flushBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public boolean isCommitted() {
        return this.response.isCommitted();
    }

    @Override // javax.servlet.ServletResponse
    public void reset() {
        this.response.reset();
    }

    @Override // javax.servlet.ServletResponse
    public void resetBuffer() {
        this.response.resetBuffer();
    }

    @Override // javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        this.response.setLocale(loc);
    }

    @Override // javax.servlet.ServletResponse
    public Locale getLocale() {
        return this.response.getLocale();
    }

    public boolean isWrapperFor(ServletResponse wrapped) {
        if (this.response == wrapped) {
            return true;
        }
        if (this.response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) this.response).isWrapperFor(wrapped);
        }
        return false;
    }

    public boolean isWrapperFor(Class<?> wrappedType) {
        if (wrappedType.isAssignableFrom(this.response.getClass())) {
            return true;
        }
        if (this.response instanceof ServletResponseWrapper) {
            return ((ServletResponseWrapper) this.response).isWrapperFor(wrappedType);
        }
        return false;
    }
}