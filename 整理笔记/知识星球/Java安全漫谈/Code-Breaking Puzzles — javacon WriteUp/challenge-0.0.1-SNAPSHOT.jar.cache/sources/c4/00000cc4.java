package org.apache.tomcat.util.http.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileItem.class */
public interface FileItem extends FileItemHeadersSupport {
    InputStream getInputStream() throws IOException;

    String getContentType();

    String getName();

    boolean isInMemory();

    long getSize();

    byte[] get();

    String getString(String str) throws UnsupportedEncodingException;

    String getString();

    void write(File file) throws Exception;

    void delete();

    String getFieldName();

    void setFieldName(String str);

    boolean isFormField();

    void setFormField(boolean z);

    OutputStream getOutputStream() throws IOException;
}