package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/AbstractConditionalEnumConverter.class */
abstract class AbstractConditionalEnumConverter implements ConditionalConverter {
    private final ConversionService conversionService;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractConditionalEnumConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.convert.converter.ConditionalConverter
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        for (Class<?> interfaceType : ClassUtils.getAllInterfacesForClassAsSet(sourceType.getType())) {
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(interfaceType), targetType)) {
                return false;
            }
        }
        return true;
    }
}