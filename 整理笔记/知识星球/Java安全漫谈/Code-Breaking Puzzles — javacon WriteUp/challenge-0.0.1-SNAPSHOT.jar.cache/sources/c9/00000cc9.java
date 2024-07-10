package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileItemStream.class */
public interface FileItemStream extends FileItemHeadersSupport {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileItemStream$ItemSkippedException.class */
    public static class ItemSkippedException extends IOException {
        private static final long serialVersionUID = -7280778431581963740L;
    }

    InputStream openStream() throws IOException;

    String getContentType();

    String getName();

    String getFieldName();

    boolean isFormField();
}