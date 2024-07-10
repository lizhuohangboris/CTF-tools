package org.springframework.boot.context.properties.bind;

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindConverter.class */
public final class BindConverter {
    private static final Set<Class<?>> EXCLUDED_EDITORS;
    private static BindConverter sharedInstance;
    private final ConversionService conversionService;

    static {
        Set<Class<?>> excluded = new HashSet<>();
        excluded.add(FileEditor.class);
        EXCLUDED_EDITORS = Collections.unmodifiableSet(excluded);
    }

    private BindConverter(ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        List<ConversionService> conversionServices = getConversionServices(conversionService, propertyEditorInitializer);
        this.conversionService = new CompositeConversionService(conversionServices);
    }

    private List<ConversionService> getConversionServices(ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        List<ConversionService> services = new ArrayList<>();
        services.add(new TypeConverterConversionService(propertyEditorInitializer));
        services.add(conversionService);
        if (!(conversionService instanceof ApplicationConversionService)) {
            services.add(ApplicationConversionService.getSharedInstance());
        }
        return services;
    }

    public boolean canConvert(Object value, ResolvableType type, Annotation... annotations) {
        return this.conversionService.canConvert(TypeDescriptor.forObject(value), new ResolvableTypeDescriptor(type, annotations));
    }

    public <T> T convert(Object result, Bindable<T> target) {
        return (T) convert(result, target.getType(), target.getAnnotations());
    }

    public <T> T convert(Object value, ResolvableType type, Annotation... annotations) {
        if (value == null) {
            return null;
        }
        return (T) this.conversionService.convert(value, TypeDescriptor.forObject(value), new ResolvableTypeDescriptor(type, annotations));
    }

    public static BindConverter get(ConversionService conversionService, Consumer<PropertyEditorRegistry> propertyEditorInitializer) {
        if (conversionService == ApplicationConversionService.getSharedInstance() && propertyEditorInitializer == null) {
            if (sharedInstance == null) {
                sharedInstance = new BindConverter(conversionService, propertyEditorInitializer);
            }
            return sharedInstance;
        }
        return new BindConverter(conversionService, propertyEditorInitializer);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindConverter$ResolvableTypeDescriptor.class */
    public static class ResolvableTypeDescriptor extends TypeDescriptor {
        ResolvableTypeDescriptor(ResolvableType resolvableType, Annotation[] annotations) {
            super(resolvableType, null, annotations);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindConverter$CompositeConversionService.class */
    public static class CompositeConversionService implements ConversionService {
        private final List<ConversionService> delegates;

        CompositeConversionService(List<ConversionService> delegates) {
            this.delegates = delegates;
        }

        @Override // org.springframework.core.convert.ConversionService
        public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
            Assert.notNull(targetType, "Target type to convert to cannot be null");
            return canConvert(sourceType != null ? TypeDescriptor.valueOf(sourceType) : null, TypeDescriptor.valueOf(targetType));
        }

        @Override // org.springframework.core.convert.ConversionService
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            for (ConversionService service : this.delegates) {
                if (service.canConvert(sourceType, targetType)) {
                    return true;
                }
            }
            return false;
        }

        @Override // org.springframework.core.convert.ConversionService
        public <T> T convert(Object source, Class<T> targetType) {
            Assert.notNull(targetType, "Target type to convert to cannot be null");
            return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
        }

        @Override // org.springframework.core.convert.ConversionService
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            ConversionService delegate;
            for (int i = 0; i < this.delegates.size() - 1; i++) {
                try {
                    delegate = this.delegates.get(i);
                } catch (ConversionException e) {
                }
                if (!delegate.canConvert(sourceType, targetType)) {
                    continue;
                } else {
                    return delegate.convert(source, sourceType, targetType);
                }
            }
            return this.delegates.get(this.delegates.size() - 1).convert(source, sourceType, targetType);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindConverter$TypeConverterConversionService.class */
    public static class TypeConverterConversionService extends GenericConversionService {
        TypeConverterConversionService(Consumer<PropertyEditorRegistry> initializer) {
            addConverter(new TypeConverterConverter(createTypeConverter(initializer)));
            ApplicationConversionService.addDelimitedStringConverters(this);
        }

        private SimpleTypeConverter createTypeConverter(Consumer<PropertyEditorRegistry> initializer) {
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            if (initializer != null) {
                initializer.accept(typeConverter);
            }
            return typeConverter;
        }

        @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.ConversionService
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (targetType.isArray() && targetType.getElementTypeDescriptor().isPrimitive()) {
                return false;
            }
            return super.canConvert(sourceType, targetType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/BindConverter$TypeConverterConverter.class */
    public static class TypeConverterConverter implements ConditionalGenericConverter {
        private final SimpleTypeConverter typeConverter;

        TypeConverterConverter(SimpleTypeConverter typeConverter) {
            this.typeConverter = typeConverter;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object.class));
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return getPropertyEditor(targetType.getType()) != null;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            SimpleTypeConverter typeConverter = this.typeConverter;
            return typeConverter.convertIfNecessary(source, targetType.getType());
        }

        private PropertyEditor getPropertyEditor(Class<?> type) {
            SimpleTypeConverter typeConverter = this.typeConverter;
            if (type == null || type == Object.class || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
                return null;
            }
            PropertyEditor editor = typeConverter.getDefaultEditor(type);
            if (editor == null) {
                editor = typeConverter.findCustomEditor(type, null);
            }
            if (editor == null && String.class != type) {
                editor = BeanUtils.findEditorByConvention(type);
            }
            if (editor == null || BindConverter.EXCLUDED_EDITORS.contains(editor.getClass())) {
                return null;
            }
            return editor;
        }
    }
}