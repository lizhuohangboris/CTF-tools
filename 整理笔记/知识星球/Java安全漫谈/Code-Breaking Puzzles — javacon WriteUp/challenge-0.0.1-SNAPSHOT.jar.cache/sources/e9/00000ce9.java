package org.apache.tomcat.util.http.fileupload.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/servlet/ServletFileUpload.class */
public class ServletFileUpload extends FileUpload {
    private static final String POST_METHOD = "POST";

    public static final boolean isMultipartContent(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }

    public ServletFileUpload() {
    }

    public ServletFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public Map<String, List<FileItem>> parseParameterMap(HttpServletRequest request) throws FileUploadException {
        return parseParameterMap(new ServletRequestContext(request));
    }

    public FileItemIterator getItemIterator(HttpServletRequest request) throws FileUploadException, IOException {
        return super.getItemIterator(new ServletRequestContext(request));
    }
}