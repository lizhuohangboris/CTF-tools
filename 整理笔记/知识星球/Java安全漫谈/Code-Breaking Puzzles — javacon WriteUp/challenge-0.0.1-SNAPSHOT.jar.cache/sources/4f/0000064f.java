package javax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletResponse.class */
public interface ServletResponse {
    String getCharacterEncoding();

    String getContentType();

    ServletOutputStream getOutputStream() throws IOException;

    PrintWriter getWriter() throws IOException;

    void setCharacterEncoding(String str);

    void setContentLength(int i);

    void setContentLengthLong(long j);

    void setContentType(String str);

    void setBufferSize(int i);

    int getBufferSize();

    void flushBuffer() throws IOException;

    void resetBuffer();

    boolean isCommitted();

    void reset();

    void setLocale(Locale locale);

    Locale getLocale();
}