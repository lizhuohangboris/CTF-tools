package org.springframework.web.accept;

import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/accept/ServletPathExtensionContentNegotiationStrategy.class */
public class ServletPathExtensionContentNegotiationStrategy extends PathExtensionContentNegotiationStrategy {
    private final ServletContext servletContext;

    public ServletPathExtensionContentNegotiationStrategy(ServletContext context) {
        this(context, null);
    }

    public ServletPathExtensionContentNegotiationStrategy(ServletContext servletContext, @Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        Assert.notNull(servletContext, "ServletContext is required");
        this.servletContext = servletContext;
    }

    @Override // org.springframework.web.accept.AbstractMappingContentNegotiationStrategy
    @Nullable
    public MediaType handleNoMatch(NativeWebRequest webRequest, String extension) throws HttpMediaTypeNotAcceptableException {
        MediaType superMediaType;
        MediaType mediaType = null;
        String mimeType = this.servletContext.getMimeType("file." + extension);
        if (StringUtils.hasText(mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if ((mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) && (superMediaType = super.handleNoMatch(webRequest, extension)) != null) {
            mediaType = superMediaType;
        }
        return mediaType;
    }

    @Override // org.springframework.web.accept.PathExtensionContentNegotiationStrategy
    public MediaType getMediaTypeForResource(Resource resource) {
        MediaType superMediaType;
        MediaType mediaType = null;
        String mimeType = this.servletContext.getMimeType(resource.getFilename());
        if (StringUtils.hasText(mimeType)) {
            mediaType = MediaType.parseMediaType(mimeType);
        }
        if ((mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) && (superMediaType = super.getMediaTypeForResource(resource)) != null) {
            mediaType = superMediaType;
        }
        return mediaType;
    }
}