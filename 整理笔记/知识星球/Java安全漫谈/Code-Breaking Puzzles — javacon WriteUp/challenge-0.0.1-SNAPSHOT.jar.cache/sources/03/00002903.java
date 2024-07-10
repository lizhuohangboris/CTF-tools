package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AbstractStandardConversionService.class */
public abstract class AbstractStandardConversionService implements IStandardConversionService {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.thymeleaf.standard.expression.IStandardConversionService
    public final <T> T convert(IExpressionContext context, Object object, Class<T> targetClass) {
        Validate.notNull(targetClass, "Target class cannot be null");
        if (targetClass.equals(String.class)) {
            if (object == 0 || (object instanceof String)) {
                return object;
            }
            return (T) convertToString(context, object);
        }
        return (T) convertOther(context, object, targetClass);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String convertToString(IExpressionContext context, Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T convertOther(IExpressionContext context, Object object, Class<T> targetClass) {
        throw new IllegalArgumentException("No available conversion for target class \"" + targetClass.getName() + "\"");
    }
}