package org.springframework.expression.spel.support;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/support/StandardTypeConverter.class */
public class StandardTypeConverter implements TypeConverter {
    private final ConversionService conversionService;

    public StandardTypeConverter() {
        this.conversionService = DefaultConversionService.getSharedInstance();
    }

    public StandardTypeConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.expression.TypeConverter
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType);
    }

    @Override // org.springframework.expression.TypeConverter
    @Nullable
    public Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        String name;
        try {
            return this.conversionService.convert(value, sourceType, targetType);
        } catch (ConversionException ex) {
            SpelMessage spelMessage = SpelMessage.TYPE_CONVERSION_ERROR;
            Object[] objArr = new Object[2];
            if (sourceType != null) {
                name = sourceType.toString();
            } else {
                name = value != null ? value.getClass().getName() : BeanDefinitionParserDelegate.NULL_ELEMENT;
            }
            objArr[0] = name;
            objArr[1] = targetType.toString();
            throw new SpelEvaluationException(ex, spelMessage, objArr);
        }
    }
}