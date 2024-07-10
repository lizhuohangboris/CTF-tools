package org.apache.tomcat.util.http.fileupload.disk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.http.fileupload.DeferredFileOutputStream;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.apache.tomcat.websocket.BasicAuthenticator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/fileupload/disk/DiskFileItem.class */
public class DiskFileItem implements FileItem {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final String UID = UUID.randomUUID().toString().replace('-', '_');
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private String fieldName;
    private final String contentType;
    private boolean isFormField;
    private final String fileName;
    private final int sizeThreshold;
    private final File repository;
    private byte[] cachedContent;
    private transient DeferredFileOutputStream dfos;
    private transient File tempFile;
    private FileItemHeaders headers;
    private long size = -1;
    private String defaultCharset = "ISO-8859-1";

    public DiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository) {
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public InputStream getInputStream() throws IOException {
        if (!isInMemory()) {
            return new FileInputStream(this.dfos.getFile());
        }
        if (this.cachedContent == null) {
            this.cachedContent = this.dfos.getData();
        }
        return new ByteArrayInputStream(this.cachedContent);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getContentType() {
        return this.contentType;
    }

    public String getCharSet() {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(getContentType(), ';');
        return params.get(BasicAuthenticator.charsetparam);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getName() {
        return Streams.checkFileName(this.fileName);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public boolean isInMemory() {
        if (this.cachedContent != null) {
            return true;
        }
        return this.dfos.isInMemory();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public long getSize() {
        if (this.size >= 0) {
            return this.size;
        }
        if (this.cachedContent != null) {
            return this.cachedContent.length;
        }
        if (this.dfos.isInMemory()) {
            return this.dfos.getData().length;
        }
        return this.dfos.getFile().length();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public byte[] get() {
        if (isInMemory()) {
            if (this.cachedContent == null && this.dfos != null) {
                this.cachedContent = this.dfos.getData();
            }
            return this.cachedContent;
        }
        byte[] fileData = new byte[(int) getSize()];
        InputStream fis = null;
        try {
            fis = new FileInputStream(this.dfos.getFile());
            IOUtils.readFully(fis, fileData);
            IOUtils.closeQuietly(fis);
        } catch (IOException e) {
            fileData = null;
            IOUtils.closeQuietly(fis);
        } catch (Throwable th) {
            IOUtils.closeQuietly(fis);
            throw th;
        }
        return fileData;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getString(String charset) throws UnsupportedEncodingException {
        return new String(get(), charset);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getString() {
        byte[] rawdata = get();
        String charset = getCharSet();
        if (charset == null) {
            charset = this.defaultCharset;
        }
        try {
            return new String(rawdata, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(rawdata);
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void write(File file) throws Exception {
        FileOutputStream fout;
        if (isInMemory()) {
            fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(get());
                fout.close();
                IOUtils.closeQuietly(fout);
                return;
            } finally {
                IOUtils.closeQuietly(fout);
            }
        }
        File outputFile = getStoreLocation();
        if (outputFile != null) {
            this.size = outputFile.length();
            if (!outputFile.renameTo(file)) {
                BufferedInputStream in = null;
                BufferedOutputStream fout2 = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(outputFile));
                    fout2 = new BufferedOutputStream(new FileOutputStream(file));
                    IOUtils.copy(in, fout2);
                    fout2.close();
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(fout2);
                    return;
                } catch (Throwable th) {
                    IOUtils.closeQuietly(in);
                    throw th;
                }
            }
            return;
        }
        throw new FileUploadException("Cannot write uploaded file to disk!");
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void delete() {
        this.cachedContent = null;
        File outputFile = getStoreLocation();
        if (outputFile != null && !isInMemory() && outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public String getFieldName() {
        return this.fieldName;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public boolean isFormField() {
        return this.isFormField;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public void setFormField(boolean state) {
        this.isFormField = state;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItem
    public OutputStream getOutputStream() throws IOException {
        if (this.dfos == null) {
            File outputFile = getTempFile();
            this.dfos = new DeferredFileOutputStream(this.sizeThreshold, outputFile);
        }
        return this.dfos;
    }

    public File getStoreLocation() {
        if (this.dfos == null || isInMemory()) {
            return null;
        }
        return this.dfos.getFile();
    }

    protected void finalize() {
        File outputFile;
        if (this.dfos != null && !this.dfos.isInMemory() && (outputFile = this.dfos.getFile()) != null && outputFile.exists()) {
            outputFile.delete();
        }
    }

    protected File getTempFile() {
        if (this.tempFile == null) {
            File tempDir = this.repository;
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }
            String tempFileName = String.format("upload_%s_%s.tmp", UID, getUniqueId());
            this.tempFile = new File(tempDir, tempFileName);
        }
        return this.tempFile;
    }

    private static String getUniqueId() {
        int current = COUNTER.getAndIncrement();
        String id = Integer.toString(current);
        if (current < 100000000) {
            id = ("00000000" + id).substring(id.length());
        }
        return id;
    }

    public String toString() {
        return String.format("name=%s, StoreLocation=%s, size=%s bytes, isFormField=%s, FieldName=%s", getName(), getStoreLocation(), Long.valueOf(getSize()), Boolean.valueOf(isFormField()), getFieldName());
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public void setHeaders(FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }

    public String getDefaultCharset() {
        return this.defaultCharset;
    }

    public void setDefaultCharset(String charset) {
        this.defaultCharset = charset;
    }
}