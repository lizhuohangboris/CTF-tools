package org.springframework.web.multipart.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/RequestPartServletServerHttpRequest.class */
public class RequestPartServletServerHttpRequest extends ServletServerHttpRequest {
    private final MultipartHttpServletRequest multipartRequest;
    private final String partName;
    private final HttpHeaders headers;

    public RequestPartServletServerHttpRequest(HttpServletRequest request, String partName) throws MissingServletRequestPartException {
        super(request);
        this.multipartRequest = MultipartResolutionDelegate.asMultipartHttpServletRequest(request);
        this.partName = partName;
        HttpHeaders headers = this.multipartRequest.getMultipartHeaders(this.partName);
        if (headers == null) {
            throw new MissingServletRequestPartException(partName);
        }
        this.headers = headers;
    }

    @Override // org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.HttpInputMessage
    public InputStream getBody() throws IOException {
        if (this.multipartRequest instanceof StandardMultipartHttpServletRequest) {
            try {
                return this.multipartRequest.getPart(this.partName).getInputStream();
            } catch (Exception ex) {
                throw new MultipartException("Could not parse multipart servlet request", ex);
            }
        }
        MultipartFile file = this.multipartRequest.getFile(this.partName);
        if (file != null) {
            return file.getInputStream();
        }
        String paramValue = this.multipartRequest.getParameter(this.partName);
        return new ByteArrayInputStream(paramValue.getBytes(determineCharset()));
    }

    private Charset determineCharset() {
        Charset charset;
        MediaType contentType = getHeaders().getContentType();
        if (contentType != null && (charset = contentType.getCharset()) != null) {
            return charset;
        }
        String encoding = this.multipartRequest.getCharacterEncoding();
        return encoding != null ? Charset.forName(encoding) : FORM_CHARSET;
    }
}