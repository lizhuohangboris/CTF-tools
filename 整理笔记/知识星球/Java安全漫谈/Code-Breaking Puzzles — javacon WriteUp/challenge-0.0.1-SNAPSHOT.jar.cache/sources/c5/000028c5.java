package org.thymeleaf.spring5.expression;

import org.springframework.expression.EvaluationContext;
import org.thymeleaf.expression.IExpressionObjects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/IThymeleafEvaluationContext.class */
public interface IThymeleafEvaluationContext extends EvaluationContext {
    boolean isVariableAccessRestricted();

    void setVariableAccessRestricted(boolean z);

    IExpressionObjects getExpressionObjects();

    void setExpressionObjects(IExpressionObjects iExpressionObjects);
}