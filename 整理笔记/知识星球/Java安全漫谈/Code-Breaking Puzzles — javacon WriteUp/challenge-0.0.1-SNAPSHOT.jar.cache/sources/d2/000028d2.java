package org.thymeleaf.spring5.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.AbstractStandardConversionService;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SpringStandardConversionService.class */
public final class SpringStandardConversionService extends AbstractStandardConversionService {
    private static final TypeDescriptor TYPE_STRING = TypeDescriptor.valueOf(String.class);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.standard.expression.AbstractStandardConversionService
    public String convertToString(IExpressionContext context, Object object) {
        if (object == null) {
            return null;
        }
        TypeDescriptor objectTypeDescriptor = TypeDescriptor.forObject(object);
        TypeConverter typeConverter = getSpringConversionService(context);
        if (typeConverter == null || !typeConverter.canConvert(objectTypeDescriptor, TYPE_STRING)) {
            return super.convertToString(context, object);
        }
        return (String) typeConverter.convertValue(object, objectTypeDescriptor, TYPE_STRING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.standard.expression.AbstractStandardConversionService
    public <T> T convertOther(IExpressionContext context, Object object, Class<T> targetClass) {
        if (object == null) {
            return null;
        }
        TypeDescriptor objectTypeDescriptor = TypeDescriptor.forObject(object);
        TypeDescriptor targetTypeDescriptor = TypeDescriptor.valueOf(targetClass);
        TypeConverter typeConverter = getSpringConversionService(context);
        if (typeConverter == null || !typeConverter.canConvert(objectTypeDescriptor, targetTypeDescriptor)) {
            return (T) super.convertOther(context, object, targetClass);
        }
        return (T) typeConverter.convertValue(object, objectTypeDescriptor, targetTypeDescriptor);
    }

    private static TypeConverter getSpringConversionService(IExpressionContext context) {
        EvaluationContext evaluationContext = (EvaluationContext) context.getVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);
        if (evaluationContext != null) {
            return evaluationContext.getTypeConverter();
        }
        return null;
    }
}