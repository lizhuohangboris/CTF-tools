package org.springframework.web.bind.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/WebRequestDataBinder.class */
public class WebRequestDataBinder extends WebDataBinder {
    public WebRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(WebRequest request) {
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (isMultipartRequest(request) && (request instanceof NativeWebRequest)) {
            MultipartRequest multipartRequest = (MultipartRequest) ((NativeWebRequest) request).getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            } else {
                HttpServletRequest servletRequest = (HttpServletRequest) ((NativeWebRequest) request).getNativeRequest(HttpServletRequest.class);
                if (servletRequest != null) {
                    bindParts(servletRequest, mpvs);
                }
            }
        }
        doBind(mpvs);
    }

    private boolean isMultipartRequest(WebRequest request) {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        return contentType != null && StringUtils.startsWithIgnoreCase(contentType, "multipart");
    }

    private void bindParts(HttpServletRequest request, MutablePropertyValues mpvs) {
        try {
            MultiValueMap<String, Part> map = new LinkedMultiValueMap<>();
            for (Part part : request.getParts()) {
                map.add(part.getName(), part);
            }
            map.forEach(key, values -> {
                if (values.size() == 1) {
                    Part part2 = (Part) values.get(0);
                    if (isBindEmptyMultipartFiles() || part2.getSize() > 0) {
                        mpvs.add(key, part2);
                        return;
                    }
                    return;
                }
                mpvs.add(key, values);
            });
        } catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public void closeNoCatch() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
    }
}