package org.springframework.web.multipart.commons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/commons/CommonsFileUploadSupport.class */
public abstract class CommonsFileUploadSupport {
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean uploadTempDirSpecified = false;
    private boolean preserveFilename = false;
    private final DiskFileItemFactory fileItemFactory = newFileItemFactory();
    private final FileUpload fileUpload = newFileUpload(getFileItemFactory());

    protected abstract FileUpload newFileUpload(FileItemFactory fileItemFactory);

    public DiskFileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    public FileUpload getFileUpload() {
        return this.fileUpload;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        this.fileUpload.setSizeMax(maxUploadSize);
    }

    public void setMaxUploadSizePerFile(long maxUploadSizePerFile) {
        this.fileUpload.setFileSizeMax(maxUploadSizePerFile);
    }

    public void setMaxInMemorySize(int maxInMemorySize) {
        this.fileItemFactory.setSizeThreshold(maxInMemorySize);
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.fileUpload.setHeaderEncoding(defaultEncoding);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getDefaultEncoding() {
        String encoding = getFileUpload().getHeaderEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        return encoding;
    }

    public void setUploadTempDir(Resource uploadTempDir) throws IOException {
        if (!uploadTempDir.exists() && !uploadTempDir.getFile().mkdirs()) {
            throw new IllegalArgumentException("Given uploadTempDir [" + uploadTempDir + "] could not be created");
        }
        this.fileItemFactory.setRepository(uploadTempDir.getFile());
        this.uploadTempDirSpecified = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isUploadTempDirSpecified() {
        return this.uploadTempDirSpecified;
    }

    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }

    protected DiskFileItemFactory newFileItemFactory() {
        return new DiskFileItemFactory();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileUpload prepareFileUpload(@Nullable String encoding) {
        FileUpload fileUpload = getFileUpload();
        FileUpload actualFileUpload = fileUpload;
        if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
            actualFileUpload = newFileUpload(getFileItemFactory());
            actualFileUpload.setSizeMax(fileUpload.getSizeMax());
            actualFileUpload.setFileSizeMax(fileUpload.getFileSizeMax());
            actualFileUpload.setHeaderEncoding(encoding);
        }
        return actualFileUpload;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
        String value;
        MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<>();
        Map<String, String[]> multipartParameters = new HashMap<>();
        Map<String, String> multipartParameterContentTypes = new HashMap<>();
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                String partEncoding = determineEncoding(fileItem.getContentType(), encoding);
                try {
                    value = fileItem.getString(partEncoding);
                } catch (UnsupportedEncodingException e) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Could not decode multipart item '" + fileItem.getFieldName() + "' with encoding '" + partEncoding + "': using platform default");
                    }
                    value = fileItem.getString();
                }
                String[] curParam = multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    multipartParameters.put(fileItem.getFieldName(), new String[]{value});
                } else {
                    String[] newParam = StringUtils.addStringToArray(curParam, value);
                    multipartParameters.put(fileItem.getFieldName(), newParam);
                }
                multipartParameterContentTypes.put(fileItem.getFieldName(), fileItem.getContentType());
            } else {
                CommonsMultipartFile file = createMultipartFile(fileItem);
                multipartFiles.add(file.getName(), file);
                LogFormatUtils.traceDebug(this.logger, traceOn -> {
                    return "Part '" + file.getName() + "', size " + file.getSize() + " bytes, filename='" + file.getOriginalFilename() + "'" + (traceOn.booleanValue() ? ", storage=" + file.getStorageDescription() : "");
                });
            }
        }
        return new MultipartParsingResult(multipartFiles, multipartParameters, multipartParameterContentTypes);
    }

    protected CommonsMultipartFile createMultipartFile(FileItem fileItem) {
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        multipartFile.setPreserveFilename(this.preserveFilename);
        return multipartFile;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cleanupFileItems(MultiValueMap<String, MultipartFile> multipartFiles) {
        Iterator<MultipartFile> it = multipartFiles.values().iterator();
        while (it.hasNext()) {
            List<MultipartFile> files = (List) it.next();
            for (MultipartFile file : files) {
                if (file instanceof CommonsMultipartFile) {
                    CommonsMultipartFile cmf = (CommonsMultipartFile) file;
                    cmf.getFileItem().delete();
                    LogFormatUtils.traceDebug(this.logger, traceOn -> {
                        return "Cleaning up part '" + cmf.getName() + "', filename '" + cmf.getOriginalFilename() + "'" + (traceOn.booleanValue() ? ", stored " + cmf.getStorageDescription() : "");
                    });
                }
            }
        }
    }

    private String determineEncoding(String contentTypeHeader, String defaultEncoding) {
        if (!StringUtils.hasText(contentTypeHeader)) {
            return defaultEncoding;
        }
        MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
        Charset charset = contentType.getCharset();
        return charset != null ? charset.name() : defaultEncoding;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/commons/CommonsFileUploadSupport$MultipartParsingResult.class */
    protected static class MultipartParsingResult {
        private final MultiValueMap<String, MultipartFile> multipartFiles;
        private final Map<String, String[]> multipartParameters;
        private final Map<String, String> multipartParameterContentTypes;

        public MultipartParsingResult(MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
            this.multipartFiles = mpFiles;
            this.multipartParameters = mpParams;
            this.multipartParameterContentTypes = mpParamContentTypes;
        }

        public MultiValueMap<String, MultipartFile> getMultipartFiles() {
            return this.multipartFiles;
        }

        public Map<String, String[]> getMultipartParameters() {
            return this.multipartParameters;
        }

        public Map<String, String> getMultipartParameterContentTypes() {
            return this.multipartParameterContentTypes;
        }
    }
}