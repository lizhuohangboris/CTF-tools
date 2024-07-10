package javax.servlet.http;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/WebConnection.class */
public interface WebConnection extends AutoCloseable {
    ServletInputStream getInputStream() throws IOException;

    ServletOutputStream getOutputStream() throws IOException;
}