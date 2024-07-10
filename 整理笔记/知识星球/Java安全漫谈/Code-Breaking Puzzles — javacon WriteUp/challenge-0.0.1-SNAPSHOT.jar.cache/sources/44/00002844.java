package org.thymeleaf.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Conversions.class */
public final class Conversions {
    private final IExpressionContext context;

    public Conversions(IExpressionContext context) {
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }

    public Object convert(Object target, String className) {
        try {
            Class<?> clazz = ClassLoaderUtils.loadClass(className);
            return convert(target, clazz);
        } catch (ClassNotFoundException e) {
            try {
                Class<?> clazz2 = ClassLoaderUtils.loadClass("java.lang." + className);
                return convert(target, clazz2);
            } catch (ClassNotFoundException e2) {
                throw new IllegalArgumentException("Cannot convert to class '" + className + "'", e);
            }
        }
    }

    public Object convert(Object target, Class<?> clazz) {
        IStandardConversionService conversionService = StandardExpressions.getConversionService(this.context.getConfiguration());
        return conversionService.convert(this.context, target, clazz);
    }
}