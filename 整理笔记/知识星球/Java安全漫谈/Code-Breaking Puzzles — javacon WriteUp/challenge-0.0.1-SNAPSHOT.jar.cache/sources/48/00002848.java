package org.thymeleaf.expression;

import java.util.Set;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/IExpressionObjectFactory.class */
public interface IExpressionObjectFactory {
    Set<String> getAllExpressionObjectNames();

    Object buildObject(IExpressionContext iExpressionContext, String str);

    boolean isCacheable(String str);
}