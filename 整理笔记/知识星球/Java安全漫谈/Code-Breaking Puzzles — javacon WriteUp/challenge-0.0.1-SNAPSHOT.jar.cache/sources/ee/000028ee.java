package org.thymeleaf.spring5.requestdata;

import java.util.Map;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/requestdata/RequestDataValueProcessorUtils.class */
public final class RequestDataValueProcessorUtils {
    public static String processAction(ITemplateContext context, String action, String httpMethod) {
        IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return action;
        }
        return thymeleafRequestContext.getRequestDataValueProcessor().processAction(action, httpMethod);
    }

    public static String processFormFieldValue(ITemplateContext context, String name, String value, String type) {
        IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return value;
        }
        return thymeleafRequestContext.getRequestDataValueProcessor().processFormFieldValue(name, value, type);
    }

    public static Map<String, String> getExtraHiddenFields(ITemplateContext context) {
        IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return null;
        }
        return thymeleafRequestContext.getRequestDataValueProcessor().getExtraHiddenFields();
    }

    public static String processUrl(ITemplateContext context, String url) {
        IThymeleafRequestContext thymeleafRequestContext = SpringContextUtils.getRequestContext(context);
        if (thymeleafRequestContext == null) {
            return url;
        }
        return thymeleafRequestContext.getRequestDataValueProcessor().processUrl(url);
    }

    private RequestDataValueProcessorUtils() {
    }
}