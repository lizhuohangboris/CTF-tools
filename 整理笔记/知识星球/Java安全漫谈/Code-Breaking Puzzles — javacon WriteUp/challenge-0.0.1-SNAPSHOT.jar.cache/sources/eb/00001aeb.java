package org.springframework.boot.web.servlet;

import javax.servlet.MultipartConfigElement;
import org.springframework.util.unit.DataSize;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/MultipartConfigFactory.class */
public class MultipartConfigFactory {
    private String location;
    private DataSize maxFileSize;
    private DataSize maxRequestSize;
    private DataSize fileSizeThreshold;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Deprecated
    public void setMaxFileSize(long maxFileSize) {
        setMaxFileSize(DataSize.ofBytes(maxFileSize));
    }

    @Deprecated
    public void setMaxFileSize(String maxFileSize) {
        setMaxFileSize(DataSize.parse(maxFileSize));
    }

    public void setMaxRequestSize(DataSize maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    @Deprecated
    public void setMaxRequestSize(long maxRequestSize) {
        setMaxRequestSize(DataSize.ofBytes(maxRequestSize));
    }

    @Deprecated
    public void setMaxRequestSize(String maxRequestSize) {
        setMaxRequestSize(DataSize.parse(maxRequestSize));
    }

    public void setFileSizeThreshold(DataSize fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    @Deprecated
    public void setFileSizeThreshold(int fileSizeThreshold) {
        setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold));
    }

    @Deprecated
    public void setFileSizeThreshold(String fileSizeThreshold) {
        setFileSizeThreshold(DataSize.parse(fileSizeThreshold));
    }

    public MultipartConfigElement createMultipartConfig() {
        long maxFileSizeBytes = convertToBytes(this.maxFileSize, -1);
        long maxRequestSizeBytes = convertToBytes(this.maxRequestSize, -1);
        long fileSizeThresholdBytes = convertToBytes(this.fileSizeThreshold, 0);
        return new MultipartConfigElement(this.location, maxFileSizeBytes, maxRequestSizeBytes, (int) fileSizeThresholdBytes);
    }

    private long convertToBytes(DataSize size, int defaultValue) {
        if (size != null && !size.isNegative()) {
            return size.toBytes();
        }
        return defaultValue;
    }
}