package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileItemIterator.class */
public interface FileItemIterator {
    boolean hasNext() throws FileUploadException, IOException;

    FileItemStream next() throws FileUploadException, IOException;
}