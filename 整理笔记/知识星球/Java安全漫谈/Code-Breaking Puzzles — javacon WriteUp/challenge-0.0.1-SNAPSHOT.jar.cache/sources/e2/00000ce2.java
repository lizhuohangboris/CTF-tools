package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/RequestContext.class */
public interface RequestContext {
    String getCharacterEncoding();

    String getContentType();

    InputStream getInputStream() throws IOException;
}