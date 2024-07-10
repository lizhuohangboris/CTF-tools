package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/IStandardConversionService.class */
public interface IStandardConversionService {
    <T> T convert(IExpressionContext iExpressionContext, Object obj, Class<T> cls);
}