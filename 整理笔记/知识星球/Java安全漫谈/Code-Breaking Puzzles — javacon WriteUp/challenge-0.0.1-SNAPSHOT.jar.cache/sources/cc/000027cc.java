package org.thymeleaf.dialect;

import org.thymeleaf.expression.IExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/IExpressionObjectDialect.class */
public interface IExpressionObjectDialect extends IDialect {
    IExpressionObjectFactory getExpressionObjectFactory();
}