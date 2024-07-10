package org.thymeleaf.spring5.expression;

import java.util.Map;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/RequestDataValues.class */
public class RequestDataValues {
    private ITemplateContext context;

    public RequestDataValues(ITemplateContext context) {
        this.context = context;
    }

    public String action(String action, String httpMethod) {
        return RequestDataValueProcessorUtils.processAction(this.context, action, httpMethod);
    }

    public String url(String url) {
        return RequestDataValueProcessorUtils.processUrl(this.context, url);
    }

    public String formFieldValue(String name, String value, String type) {
        return RequestDataValueProcessorUtils.processFormFieldValue(this.context, name, value, type);
    }

    public Map<String, String> extraHiddenFields() {
        return RequestDataValueProcessorUtils.getExtraHiddenFields(this.context);
    }
}