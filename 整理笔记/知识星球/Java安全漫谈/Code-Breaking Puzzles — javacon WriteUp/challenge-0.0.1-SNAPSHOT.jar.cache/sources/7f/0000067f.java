package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;

/* compiled from: HttpServlet.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/NoBodyResponse.class */
class NoBodyResponse extends HttpServletResponseWrapper {
    private final NoBodyOutputStream noBody;
    private PrintWriter writer;
    private boolean didSetContentLength;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoBodyResponse(HttpServletResponse r) {
        super(r);
        this.noBody = new NoBodyOutputStream();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContentLength() {
        if (!this.didSetContentLength) {
            if (this.writer != null) {
                this.writer.flush();
            }
            super.setContentLength(this.noBody.getContentLength());
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLength(int len) {
        super.setContentLength(len);
        this.didSetContentLength = true;
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        super.setContentLengthLong(len);
        this.didSetContentLength = true;
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        super.setHeader(name, value);
        checkHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        super.addHeader(name, value);
        checkHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        super.setIntHeader(name, value);
        checkHeader(name);
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        super.addIntHeader(name, value);
        checkHeader(name);
    }

    private void checkHeader(String name) {
        if ("content-length".equalsIgnoreCase(name)) {
            this.didSetContentLength = true;
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public ServletOutputStream getOutputStream() throws IOException {
        return this.noBody;
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (this.writer == null) {
            OutputStreamWriter w = new OutputStreamWriter(this.noBody, getCharacterEncoding());
            this.writer = new PrintWriter(w);
        }
        return this.writer;
    }
}