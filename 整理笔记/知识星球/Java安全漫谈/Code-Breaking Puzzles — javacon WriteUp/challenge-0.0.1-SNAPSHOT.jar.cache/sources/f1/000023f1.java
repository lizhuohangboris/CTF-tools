package org.springframework.web.accept;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/accept/HeaderContentNegotiationStrategy.class */
public class HeaderContentNegotiationStrategy implements ContentNegotiationStrategy {
    @Override // org.springframework.web.accept.ContentNegotiationStrategy
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        String[] headerValueArray = request.getHeaderValues(HttpHeaders.ACCEPT);
        if (headerValueArray == null) {
            return MEDIA_TYPE_ALL_LIST;
        }
        List<String> headerValues = Arrays.asList(headerValueArray);
        try {
            List<MediaType> mediaTypes = MediaType.parseMediaTypes(headerValues);
            MediaType.sortBySpecificityAndQuality(mediaTypes);
            return !CollectionUtils.isEmpty(mediaTypes) ? mediaTypes : MEDIA_TYPE_ALL_LIST;
        } catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotAcceptableException("Could not parse 'Accept' header " + headerValues + ": " + ex.getMessage());
        }
    }
}