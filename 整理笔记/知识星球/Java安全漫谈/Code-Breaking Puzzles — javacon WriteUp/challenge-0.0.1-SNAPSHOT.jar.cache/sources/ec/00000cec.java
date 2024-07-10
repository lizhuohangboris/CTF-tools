package org.apache.tomcat.util.http.fileupload.util;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/util/Closeable.class */
public interface Closeable {
    void close() throws IOException;

    boolean isClosed() throws IOException;
}