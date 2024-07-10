package org.thymeleaf.spring5.util;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.thymeleaf.util.ContentTypeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/SpringContentTypeUtils.class */
public final class SpringContentTypeUtils {
    public static String computeViewContentType(HttpServletRequest request, String defaultContentType, Charset defaultCharset) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        MediaType negotiatedMediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
        if (negotiatedMediaType != null && negotiatedMediaType.isConcrete()) {
            Charset negotiatedCharset = negotiatedMediaType.getCharset();
            if (negotiatedCharset != null) {
                return negotiatedMediaType.toString();
            }
            return ContentTypeUtils.combineContentTypeAndCharset(negotiatedMediaType.toString(), defaultCharset);
        }
        String combinedContentType = ContentTypeUtils.combineContentTypeAndCharset(defaultContentType, defaultCharset);
        Charset combinedCharset = ContentTypeUtils.computeCharsetFromContentType(combinedContentType);
        String requestPathContentType = ContentTypeUtils.computeContentTypeForRequestPath(request.getRequestURI(), combinedCharset);
        if (requestPathContentType != null) {
            return requestPathContentType;
        }
        return combinedContentType;
    }

    private SpringContentTypeUtils() {
    }
}