package org.apache.tomcat.util.http.fileupload;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/FileUpload.class */
public class FileUpload extends FileUploadBase {
    private FileItemFactory fileItemFactory;

    public FileUpload() {
    }

    public FileUpload(FileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileUploadBase
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileUploadBase
    public void setFileItemFactory(FileItemFactory factory) {
        this.fileItemFactory = factory;
    }
}