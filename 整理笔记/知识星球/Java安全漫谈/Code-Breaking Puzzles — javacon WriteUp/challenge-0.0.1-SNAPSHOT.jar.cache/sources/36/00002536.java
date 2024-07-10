package org.springframework.web.multipart.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/commons/CommonsMultipartFile.class */
public class CommonsMultipartFile implements MultipartFile, Serializable {
    protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);
    private final FileItem fileItem;
    private final long size;
    private boolean preserveFilename = false;

    public CommonsMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }

    public final FileItem getFileItem() {
        return this.fileItem;
    }

    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public String getName() {
        return this.fileItem.getFieldName();
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            return "";
        }
        if (this.preserveFilename) {
            return filename;
        }
        int unixSep = filename.lastIndexOf(47);
        int winSep = filename.lastIndexOf(92);
        int pos = winSep > unixSep ? winSep : unixSep;
        if (pos != -1) {
            return filename.substring(pos + 1);
        }
        return filename;
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public long getSize() {
        return this.size;
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public byte[] getBytes() {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return bytes != null ? bytes : new byte[0];
    }

    @Override // org.springframework.web.multipart.MultipartFile, org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        InputStream inputStream = this.fileItem.getInputStream();
        return inputStream != null ? inputStream : StreamUtils.emptyInput();
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }
        if (dest.exists() && !dest.delete()) {
            throw new IOException("Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
        }
        try {
            this.fileItem.write(dest);
            LogFormatUtils.traceDebug(logger, traceOn -> {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = isAvailable() ? "copied" : "moved";
                }
                return "Part '" + getName() + "',  filename '" + getOriginalFilename() + "'" + (traceOn.booleanValue() ? ", stored " + getStorageDescription() : "") + ": " + action + " to [" + dest.getAbsolutePath() + "]";
            });
        } catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        } catch (IOException | IllegalStateException ex2) {
            throw ex2;
        } catch (Exception ex3) {
            throw new IOException("File transfer failed", ex3);
        }
    }

    @Override // org.springframework.web.multipart.MultipartFile
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }
        FileCopyUtils.copy(this.fileItem.getInputStream(), Files.newOutputStream(dest, new OpenOption[0]));
    }

    protected boolean isAvailable() {
        if (this.fileItem.isInMemory()) {
            return true;
        }
        if (this.fileItem instanceof DiskFileItem) {
            return this.fileItem.getStoreLocation().exists();
        }
        return this.fileItem.getSize() == this.size;
    }

    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        }
        if (this.fileItem instanceof DiskFileItem) {
            return "at [" + this.fileItem.getStoreLocation().getAbsolutePath() + "]";
        }
        return "on disk";
    }
}