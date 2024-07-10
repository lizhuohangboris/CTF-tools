package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/ConversionUtils.class */
abstract class ConversionUtils {
    ConversionUtils() {
    }

    @Nullable
    public static Object invokeConverter(GenericConverter converter, @Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return converter.convert(source, sourceType, targetType);
        } catch (ConversionFailedException ex) {
            throw ex;
        } catch (Throwable ex2) {
            throw new ConversionFailedException(sourceType, targetType, source, ex2);
        }
    }

    public static boolean canConvertElements(@Nullable TypeDescriptor sourceElementType, @Nullable TypeDescriptor targetElementType, ConversionService conversionService) {
        if (targetElementType == null || sourceElementType == null || conversionService.canConvert(sourceElementType, targetElementType) || sourceElementType.getType().isAssignableFrom(targetElementType.getType())) {
            return true;
        }
        return false;
    }

    public static Class<?> getEnumType(Class<?> targetType) {
        Class<?> enumType;
        Class<?> cls = targetType;
        while (true) {
            enumType = cls;
            if (enumType == null || enumType.isEnum()) {
                break;
            }
            cls = enumType.getSuperclass();
        }
        Assert.notNull(enumType, () -> {
            return "The target type " + targetType.getName() + " does not refer to an enum";
        });
        return enumType;
    }
}