package org.springframework.web.multipart.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/AbstractMultipartHttpServletRequest.class */
public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper implements MultipartHttpServletRequest {
    @Nullable
    private MultiValueMap<String, MultipartFile> multipartFiles;

    public AbstractMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override // javax.servlet.ServletRequestWrapper
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override // org.springframework.web.multipart.MultipartHttpServletRequest
    public HttpMethod getRequestMethod() {
        return HttpMethod.resolve(getRequest().getMethod());
    }

    @Override // org.springframework.web.multipart.MultipartHttpServletRequest
    public HttpHeaders getRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, (List<String>) Collections.list(getHeaders(headerName)));
        }
        return headers;
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public Iterator<String> getFileNames() {
        return getMultipartFiles().keySet().iterator();
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public MultipartFile getFile(String name) {
        return getMultipartFiles().getFirst(name);
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public List<MultipartFile> getFiles(String name) {
        List<MultipartFile> multipartFiles = (List) getMultipartFiles().get(name);
        if (multipartFiles != null) {
            return multipartFiles;
        }
        return Collections.emptyList();
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public Map<String, MultipartFile> getFileMap() {
        return getMultipartFiles().toSingleValueMap();
    }

    @Override // org.springframework.web.multipart.MultipartRequest
    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return getMultipartFiles();
    }

    public boolean isResolved() {
        return this.multipartFiles != null;
    }

    public final void setMultipartFiles(MultiValueMap<String, MultipartFile> multipartFiles) {
        this.multipartFiles = new LinkedMultiValueMap(Collections.unmodifiableMap(multipartFiles));
    }

    protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
        if (this.multipartFiles == null) {
            initializeMultipart();
        }
        return this.multipartFiles;
    }

    public void initializeMultipart() {
        throw new IllegalStateException("Multipart request not initialized");
    }
}