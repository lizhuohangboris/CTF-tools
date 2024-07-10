package org.springframework.web.multipart.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/StandardServletMultipartResolver.class */
public class StandardServletMultipartResolver implements MultipartResolver {
    private boolean resolveLazily = false;

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public boolean isMultipart(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), FileUploadBase.MULTIPART);
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new StandardMultipartHttpServletRequest(request, this.resolveLazily);
    }

    @Override // org.springframework.web.multipart.MultipartResolver
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest) request).isResolved()) {
            try {
                for (Part part : request.getParts()) {
                    if (request.getFile(part.getName()) != null) {
                        part.delete();
                    }
                }
            } catch (Throwable ex) {
                LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
            }
        }
    }
}