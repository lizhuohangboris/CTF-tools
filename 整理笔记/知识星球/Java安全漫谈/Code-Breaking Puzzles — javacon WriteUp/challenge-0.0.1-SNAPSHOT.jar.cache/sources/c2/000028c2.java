package org.thymeleaf.spring5.context.webmvc;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webmvc/SpringWebMvcThymeleafRequestDataValueProcessor.class */
class SpringWebMvcThymeleafRequestDataValueProcessor implements IThymeleafRequestDataValueProcessor {
    private final RequestDataValueProcessor requestDataValueProcessor;
    private final HttpServletRequest httpServletRequest;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringWebMvcThymeleafRequestDataValueProcessor(RequestDataValueProcessor requestDataValueProcessor, HttpServletRequest httpServletRequest) {
        this.requestDataValueProcessor = requestDataValueProcessor;
        this.httpServletRequest = httpServletRequest;
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processAction(String action, String httpMethod) {
        if (this.requestDataValueProcessor == null) {
            return action;
        }
        return this.requestDataValueProcessor.processAction(this.httpServletRequest, action, httpMethod);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processFormFieldValue(String name, String value, String type) {
        if (this.requestDataValueProcessor == null) {
            return value;
        }
        return this.requestDataValueProcessor.processFormFieldValue(this.httpServletRequest, name, value, type);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public Map<String, String> getExtraHiddenFields() {
        if (this.requestDataValueProcessor == null) {
            return null;
        }
        return this.requestDataValueProcessor.getExtraHiddenFields(this.httpServletRequest);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processUrl(String url) {
        if (this.requestDataValueProcessor == null) {
            return url;
        }
        return this.requestDataValueProcessor.processUrl(this.httpServletRequest, url);
    }
}