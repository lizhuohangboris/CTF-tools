package org.springframework.web.multipart.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/StandardMultipartHttpServletRequest.class */
public class StandardMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {
    @Nullable
    private Set<String> multipartParameterNames;

    public StandardMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
        this(request, false);
    }

    public StandardMultipartHttpServletRequest(HttpServletRequest request, boolean lazyParsing) throws MultipartException {
        super(request);
        if (!lazyParsing) {
            parseRequest(request);
        }
    }

    private void parseRequest(HttpServletRequest request) {
        try {
            Collection<Part> parts = request.getParts();
            this.multipartParameterNames = new LinkedHashSet(parts.size());
            MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<>(parts.size());
            for (Part part : parts) {
                String headerValue = part.getHeader(HttpHeaders.CONTENT_DISPOSITION);
                ContentDisposition disposition = ContentDisposition.parse(headerValue);
                String filename = disposition.getFilename();
                if (filename != null) {
                    if (filename.startsWith("=?") && filename.endsWith("?=")) {
                        filename = MimeDelegate.decode(filename);
                    }
                    files.add(part.getName(), new StandardMultipartFile(part, filename));
                } else {
                    this.multipartParameterNames.add(part.getName());
                }
            }
            setMultipartFiles(files);
        } catch (Throwable ex) {
            handleParseFailure(ex);
        }
    }

    protected void handleParseFailure(Throwable ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains(InputTag.SIZE_ATTRIBUTE) && msg.contains("exceed")) {
            throw new MaxUploadSizeExceededException(-1L, ex);
        }
        throw new MultipartException("Failed to parse multipart servlet request", ex);
    }

    @Override // org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
    public void initializeMultipart() {
        parseRequest(getRequest());
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        if (this.multipartParameterNames == null) {
            initializeMultipart();
        }
        if (this.multipartParameterNames.isEmpty()) {
            return super.getParameterNames();
        }
        Set<String> paramNames = new LinkedHashSet<>();
        Enumeration<String> paramEnum = super.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            paramNames.add(paramEnum.nextElement());
        }
        paramNames.addAll(this.multipartParameterNames);
        return Collections.enumeration(paramNames);
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        if (this.multipartParameterNames == null) {
            initializeMultipart();
        }
        if (this.multipartParameterNames.isEmpty()) {
            return super.getParameterMap();
        }
        Map<String, String[]> paramMap = new LinkedHashMap<>(super.getParameterMap());
        for (String paramName : this.multipartParameterNames) {
            if (!paramMap.containsKey(paramName)) {
                paramMap.put(paramName, getParameterValues(paramName));
            }
        }
        return paramMap;
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public String getMultipartContentType(String paramOrFileName) {
        try {
            Part part = getPart(paramOrFileName);
            if (part != null) {
                return part.getContentType();
            }
            return null;
        } catch (Throwable ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }

    @Override // org.springframework.web.multipart.MultipartHttpServletRequest
    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        try {
            Part part = getPart(paramOrFileName);
            if (part != null) {
                HttpHeaders headers = new HttpHeaders();
                for (String headerName : part.getHeaderNames()) {
                    headers.put(headerName, (List<String>) new ArrayList(part.getHeaders(headerName)));
                }
                return headers;
            }
            return null;
        } catch (Throwable ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/StandardMultipartHttpServletRequest$StandardMultipartFile.class */
    public static class StandardMultipartFile implements MultipartFile, Serializable {
        private final Part part;
        private final String filename;

        public StandardMultipartFile(Part part, String filename) {
            this.part = part;
            this.filename = filename;
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public String getName() {
            return this.part.getName();
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public String getOriginalFilename() {
            return this.filename;
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public String getContentType() {
            return this.part.getContentType();
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public boolean isEmpty() {
            return this.part.getSize() == 0;
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public long getSize() {
            return this.part.getSize();
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public byte[] getBytes() throws IOException {
            return FileCopyUtils.copyToByteArray(this.part.getInputStream());
        }

        @Override // org.springframework.web.multipart.MultipartFile, org.springframework.core.io.InputStreamSource
        public InputStream getInputStream() throws IOException {
            return this.part.getInputStream();
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public void transferTo(File dest) throws IOException, IllegalStateException {
            this.part.write(dest.getPath());
            if (dest.isAbsolute() && !dest.exists()) {
                FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest.toPath(), new OpenOption[0]));
            }
        }

        @Override // org.springframework.web.multipart.MultipartFile
        public void transferTo(Path dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest, new OpenOption[0]));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/StandardMultipartHttpServletRequest$MimeDelegate.class */
    public static class MimeDelegate {
        private MimeDelegate() {
        }

        public static String decode(String value) {
            try {
                return MimeUtility.decodeText(value);
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}