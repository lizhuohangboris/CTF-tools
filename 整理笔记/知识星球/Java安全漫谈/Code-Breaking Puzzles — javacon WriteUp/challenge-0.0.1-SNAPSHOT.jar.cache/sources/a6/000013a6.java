package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/TypeConverterSupport.class */
public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {
    @Nullable
    TypeConverterDelegate typeConverterDelegate;

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
        return (T) doConvert(value, requiredType, null, null);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam) throws TypeMismatchException {
        return (T) doConvert(value, requiredType, methodParam, null);
    }

    @Override // org.springframework.beans.TypeConverter
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable Field field) throws TypeMismatchException {
        return (T) doConvert(value, requiredType, null, field);
    }

    @Nullable
    private <T> T doConvert(@Nullable Object value, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam, @Nullable Field field) throws TypeMismatchException {
        Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
        try {
            try {
                if (field != null) {
                    return (T) this.typeConverterDelegate.convertIfNecessary(value, requiredType, field);
                }
                return (T) this.typeConverterDelegate.convertIfNecessary(value, requiredType, methodParam);
            } catch (IllegalStateException | ConverterNotFoundException ex) {
                throw new ConversionNotSupportedException(value, (Class<?>) requiredType, (Throwable) ex);
            }
        } catch (IllegalArgumentException | ConversionException ex2) {
            throw new TypeMismatchException(value, (Class<?>) requiredType, (Throwable) ex2);
        }
    }
}