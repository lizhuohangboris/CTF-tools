package org.springframework.web.bind;

import javax.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/ServletRequestDataBinder.class */
public class ServletRequestDataBinder extends WebDataBinder {
    public ServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(ServletRequest request) {
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        MultipartRequest multipartRequest = (MultipartRequest) WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        }
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
    }

    public void closeNoCatch() throws ServletRequestBindingException {
        if (getBindingResult().hasErrors()) {
            throw new ServletRequestBindingException("Errors binding onto object '" + getBindingResult().getObjectName() + "'", new BindException(getBindingResult()));
        }
    }
}