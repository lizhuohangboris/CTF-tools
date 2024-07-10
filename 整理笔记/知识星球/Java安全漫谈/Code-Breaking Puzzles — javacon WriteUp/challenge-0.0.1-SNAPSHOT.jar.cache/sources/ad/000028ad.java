package org.thymeleaf.spring5.context;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.expression.IThymeleafEvaluationContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/SpringContextUtils.class */
public class SpringContextUtils {
    public static final String WEB_SESSION_ATTRIBUTE_NAME = "thymeleafWebSession";

    public static ApplicationContext getApplicationContext(ITemplateContext context) {
        IThymeleafEvaluationContext evaluationContext;
        if (context == null || (evaluationContext = (IThymeleafEvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME)) == null || !(evaluationContext instanceof ThymeleafEvaluationContext)) {
            return null;
        }
        return ((ThymeleafEvaluationContext) evaluationContext).getApplicationContext();
    }

    public static IThymeleafRequestContext getRequestContext(IExpressionContext context) {
        if (context == null) {
            return null;
        }
        return (IThymeleafRequestContext) context.getVariable(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT);
    }

    private SpringContextUtils() {
    }
}