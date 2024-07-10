package org.springframework.core.convert.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService.class */
public class GenericConversionService implements ConfigurableConversionService {
    private static final GenericConverter NO_OP_CONVERTER = new NoOpConverter("NO_OP");
    private static final GenericConverter NO_MATCH = new NoOpConverter("NO_MATCH");
    private final Converters converters = new Converters();
    private final Map<ConverterCacheKey, GenericConverter> converterCache = new ConcurrentReferenceHashMap(64);

    @Override // org.springframework.core.convert.converter.ConverterRegistry
    public void addConverter(Converter<?, ?> converter) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(converter.getClass(), Converter.class);
        if (typeInfo == null && (converter instanceof DecoratingProxy)) {
            typeInfo = getRequiredTypeInfo(((DecoratingProxy) converter).getDecoratedClass(), Converter.class);
        }
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type <S> and target type <T> for your Converter [" + converter.getClass().getName() + "]; does the class parameterize those types?");
        }
        addConverter(new ConverterAdapter(converter, typeInfo[0], typeInfo[1]));
    }

    @Override // org.springframework.core.convert.converter.ConverterRegistry
    public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter) {
        addConverter(new ConverterAdapter(converter, ResolvableType.forClass(sourceType), ResolvableType.forClass(targetType)));
    }

    @Override // org.springframework.core.convert.converter.ConverterRegistry
    public void addConverter(GenericConverter converter) {
        this.converters.add(converter);
        invalidateCache();
    }

    @Override // org.springframework.core.convert.converter.ConverterRegistry
    public void addConverterFactory(ConverterFactory<?, ?> factory) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(factory.getClass(), ConverterFactory.class);
        if (typeInfo == null && (factory instanceof DecoratingProxy)) {
            typeInfo = getRequiredTypeInfo(((DecoratingProxy) factory).getDecoratedClass(), ConverterFactory.class);
        }
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type <S> and target type <T> for your ConverterFactory [" + factory.getClass().getName() + "]; does the class parameterize those types?");
        }
        addConverter(new ConverterFactoryAdapter(factory, new GenericConverter.ConvertiblePair(typeInfo[0].toClass(), typeInfo[1].toClass())));
    }

    @Override // org.springframework.core.convert.converter.ConverterRegistry
    public void removeConvertible(Class<?> sourceType, Class<?> targetType) {
        this.converters.remove(sourceType, targetType);
        invalidateCache();
    }

    @Override // org.springframework.core.convert.ConversionService
    public boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return canConvert(sourceType != null ? TypeDescriptor.valueOf(sourceType) : null, TypeDescriptor.valueOf(targetType));
    }

    @Override // org.springframework.core.convert.ConversionService
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        if (sourceType == null) {
            return true;
        }
        GenericConverter converter = getConverter(sourceType, targetType);
        return converter != null;
    }

    public boolean canBypassConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        if (sourceType == null) {
            return true;
        }
        GenericConverter converter = getConverter(sourceType, targetType);
        return converter == NO_OP_CONVERTER;
    }

    @Override // org.springframework.core.convert.ConversionService
    @Nullable
    public <T> T convert(@Nullable Object source, Class<T> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(targetType));
    }

    @Override // org.springframework.core.convert.ConversionService
    @Nullable
    public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        if (sourceType == null) {
            Assert.isTrue(source == null, "Source must be [null] if source type == [null]");
            return handleResult(null, targetType, convertNullSource(null, targetType));
        } else if (source != null && !sourceType.getObjectType().isInstance(source)) {
            throw new IllegalArgumentException("Source to convert from must be an instance of [" + sourceType + "]; instead it was a [" + source.getClass().getName() + "]");
        } else {
            GenericConverter converter = getConverter(sourceType, targetType);
            if (converter != null) {
                Object result = ConversionUtils.invokeConverter(converter, source, sourceType, targetType);
                return handleResult(sourceType, targetType, result);
            }
            return handleConverterNotFound(source, sourceType, targetType);
        }
    }

    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor targetType) {
        return convert(source, TypeDescriptor.forObject(source), targetType);
    }

    public String toString() {
        return this.converters.toString();
    }

    @Nullable
    protected Object convertNullSource(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.getObjectType() == Optional.class) {
            return Optional.empty();
        }
        return null;
    }

    @Nullable
    protected GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConverterCacheKey key = new ConverterCacheKey(sourceType, targetType);
        GenericConverter converter = this.converterCache.get(key);
        if (converter != null) {
            if (converter != NO_MATCH) {
                return converter;
            }
            return null;
        }
        GenericConverter converter2 = this.converters.find(sourceType, targetType);
        if (converter2 == null) {
            converter2 = getDefaultConverter(sourceType, targetType);
        }
        if (converter2 != null) {
            this.converterCache.put(key, converter2);
            return converter2;
        }
        this.converterCache.put(key, NO_MATCH);
        return null;
    }

    @Nullable
    protected GenericConverter getDefaultConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.isAssignableTo(targetType)) {
            return NO_OP_CONVERTER;
        }
        return null;
    }

    @Nullable
    private ResolvableType[] getRequiredTypeInfo(Class<?> converterClass, Class<?> genericIfc) {
        ResolvableType resolvableType = ResolvableType.forClass(converterClass).as(genericIfc);
        ResolvableType[] generics = resolvableType.getGenerics();
        if (generics.length < 2) {
            return null;
        }
        Class<?> sourceType = generics[0].resolve();
        Class<?> targetType = generics[1].resolve();
        if (sourceType == null || targetType == null) {
            return null;
        }
        return generics;
    }

    private void invalidateCache() {
        this.converterCache.clear();
    }

    @Nullable
    private Object handleConverterNotFound(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            assertNotPrimitiveTargetType(sourceType, targetType);
            return null;
        } else if ((sourceType == null || sourceType.isAssignableTo(targetType)) && targetType.getObjectType().isInstance(source)) {
            return source;
        } else {
            throw new ConverterNotFoundException(sourceType, targetType);
        }
    }

    @Nullable
    private Object handleResult(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType, @Nullable Object result) {
        if (result == null) {
            assertNotPrimitiveTargetType(sourceType, targetType);
        }
        return result;
    }

    private void assertNotPrimitiveTargetType(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (targetType.isPrimitive()) {
            throw new ConversionFailedException(sourceType, targetType, null, new IllegalArgumentException("A null value cannot be assigned to a primitive type"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$ConverterAdapter.class */
    public final class ConverterAdapter implements ConditionalGenericConverter {
        private final Converter<Object, Object> converter;
        private final GenericConverter.ConvertiblePair typeInfo;
        private final ResolvableType targetType;

        public ConverterAdapter(Converter<?, ?> converter, ResolvableType sourceType, ResolvableType targetType) {
            GenericConversionService.this = r7;
            this.converter = converter;
            this.typeInfo = new GenericConverter.ConvertiblePair(sourceType.toClass(), targetType.toClass());
            this.targetType = targetType;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (this.typeInfo.getTargetType() != targetType.getObjectType()) {
                return false;
            }
            ResolvableType rt = targetType.getResolvableType();
            if ((rt.getType() instanceof Class) || rt.isAssignableFrom(this.targetType) || this.targetType.hasUnresolvableGenerics()) {
                return !(this.converter instanceof ConditionalConverter) || ((ConditionalConverter) this.converter).matches(sourceType, targetType);
            }
            return false;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converter.convert(source);
        }

        public String toString() {
            return this.typeInfo + " : " + this.converter;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$ConverterFactoryAdapter.class */
    private final class ConverterFactoryAdapter implements ConditionalGenericConverter {
        private final ConverterFactory<Object, Object> converterFactory;
        private final GenericConverter.ConvertiblePair typeInfo;

        public ConverterFactoryAdapter(ConverterFactory<?, ?> converterFactory, GenericConverter.ConvertiblePair typeInfo) {
            GenericConversionService.this = r4;
            this.converterFactory = converterFactory;
            this.typeInfo = typeInfo;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.typeInfo);
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            boolean matches = true;
            if (this.converterFactory instanceof ConditionalConverter) {
                matches = ((ConditionalConverter) this.converterFactory).matches(sourceType, targetType);
            }
            if (matches) {
                Converter<?, ?> converter = this.converterFactory.getConverter(targetType.getType());
                if (converter instanceof ConditionalConverter) {
                    matches = ((ConditionalConverter) converter).matches(sourceType, targetType);
                }
            }
            return matches;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return GenericConversionService.this.convertNullSource(sourceType, targetType);
            }
            return this.converterFactory.getConverter(targetType.getObjectType()).convert(source);
        }

        public String toString() {
            return this.typeInfo + " : " + this.converterFactory;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$ConverterCacheKey.class */
    public static final class ConverterCacheKey implements Comparable<ConverterCacheKey> {
        private final TypeDescriptor sourceType;
        private final TypeDescriptor targetType;

        public ConverterCacheKey(TypeDescriptor sourceType, TypeDescriptor targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ConverterCacheKey)) {
                return false;
            }
            ConverterCacheKey otherKey = (ConverterCacheKey) other;
            return this.sourceType.equals(otherKey.sourceType) && this.targetType.equals(otherKey.targetType);
        }

        public int hashCode() {
            return (this.sourceType.hashCode() * 29) + this.targetType.hashCode();
        }

        public String toString() {
            return "ConverterCacheKey [sourceType = " + this.sourceType + ", targetType = " + this.targetType + "]";
        }

        @Override // java.lang.Comparable
        public int compareTo(ConverterCacheKey other) {
            int result = this.sourceType.getResolvableType().toString().compareTo(other.sourceType.getResolvableType().toString());
            if (result == 0) {
                result = this.targetType.getResolvableType().toString().compareTo(other.targetType.getResolvableType().toString());
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$Converters.class */
    public static class Converters {
        private final Set<GenericConverter> globalConverters;
        private final Map<GenericConverter.ConvertiblePair, ConvertersForPair> converters;

        private Converters() {
            this.globalConverters = new LinkedHashSet();
            this.converters = new LinkedHashMap(36);
        }

        public void add(GenericConverter converter) {
            Set<GenericConverter.ConvertiblePair> convertibleTypes = converter.getConvertibleTypes();
            if (convertibleTypes == null) {
                Assert.state(converter instanceof ConditionalConverter, "Only conditional converters may return null convertible types");
                this.globalConverters.add(converter);
                return;
            }
            for (GenericConverter.ConvertiblePair convertiblePair : convertibleTypes) {
                ConvertersForPair convertersForPair = getMatchableConverters(convertiblePair);
                convertersForPair.add(converter);
            }
        }

        private ConvertersForPair getMatchableConverters(GenericConverter.ConvertiblePair convertiblePair) {
            ConvertersForPair convertersForPair = this.converters.get(convertiblePair);
            if (convertersForPair == null) {
                convertersForPair = new ConvertersForPair();
                this.converters.put(convertiblePair, convertersForPair);
            }
            return convertersForPair;
        }

        public void remove(Class<?> sourceType, Class<?> targetType) {
            this.converters.remove(new GenericConverter.ConvertiblePair(sourceType, targetType));
        }

        @Nullable
        public GenericConverter find(TypeDescriptor sourceType, TypeDescriptor targetType) {
            List<Class<?>> sourceCandidates = getClassHierarchy(sourceType.getType());
            List<Class<?>> targetCandidates = getClassHierarchy(targetType.getType());
            for (Class<?> sourceCandidate : sourceCandidates) {
                for (Class<?> targetCandidate : targetCandidates) {
                    GenericConverter.ConvertiblePair convertiblePair = new GenericConverter.ConvertiblePair(sourceCandidate, targetCandidate);
                    GenericConverter converter = getRegisteredConverter(sourceType, targetType, convertiblePair);
                    if (converter != null) {
                        return converter;
                    }
                }
            }
            return null;
        }

        @Nullable
        private GenericConverter getRegisteredConverter(TypeDescriptor sourceType, TypeDescriptor targetType, GenericConverter.ConvertiblePair convertiblePair) {
            GenericConverter converter;
            ConvertersForPair convertersForPair = this.converters.get(convertiblePair);
            if (convertersForPair != null && (converter = convertersForPair.getConverter(sourceType, targetType)) != null) {
                return converter;
            }
            for (GenericConverter globalConverter : this.globalConverters) {
                if (((ConditionalConverter) globalConverter).matches(sourceType, targetType)) {
                    return globalConverter;
                }
            }
            return null;
        }

        private List<Class<?>> getClassHierarchy(Class<?> type) {
            List<Class<?>> hierarchy = new ArrayList<>(20);
            Set<Class<?>> visited = new HashSet<>(20);
            addToClassHierarchy(0, ClassUtils.resolvePrimitiveIfNecessary(type), false, hierarchy, visited);
            boolean array = type.isArray();
            for (int i = 0; i < hierarchy.size(); i++) {
                Class<?> candidate = hierarchy.get(i);
                Class<?> candidate2 = array ? candidate.getComponentType() : ClassUtils.resolvePrimitiveIfNecessary(candidate);
                Class<?> superclass = candidate2.getSuperclass();
                if (superclass != null && superclass != Object.class && superclass != Enum.class) {
                    addToClassHierarchy(i + 1, candidate2.getSuperclass(), array, hierarchy, visited);
                }
                addInterfacesToClassHierarchy(candidate2, array, hierarchy, visited);
            }
            if (Enum.class.isAssignableFrom(type)) {
                addToClassHierarchy(hierarchy.size(), Enum.class, array, hierarchy, visited);
                addToClassHierarchy(hierarchy.size(), Enum.class, false, hierarchy, visited);
                addInterfacesToClassHierarchy(Enum.class, array, hierarchy, visited);
            }
            addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
            addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
            return hierarchy;
        }

        private void addInterfacesToClassHierarchy(Class<?> type, boolean asArray, List<Class<?>> hierarchy, Set<Class<?>> visited) {
            Class<?>[] interfaces;
            for (Class<?> implementedInterface : type.getInterfaces()) {
                addToClassHierarchy(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
            }
        }

        private void addToClassHierarchy(int index, Class<?> type, boolean asArray, List<Class<?>> hierarchy, Set<Class<?>> visited) {
            if (asArray) {
                type = Array.newInstance(type, 0).getClass();
            }
            if (visited.add(type)) {
                hierarchy.add(index, type);
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ConversionService converters =\n");
            for (String converterString : getConverterStrings()) {
                builder.append('\t').append(converterString).append('\n');
            }
            return builder.toString();
        }

        private List<String> getConverterStrings() {
            List<String> converterStrings = new ArrayList<>();
            for (ConvertersForPair convertersForPair : this.converters.values()) {
                converterStrings.add(convertersForPair.toString());
            }
            Collections.sort(converterStrings);
            return converterStrings;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$ConvertersForPair.class */
    public static class ConvertersForPair {
        private final LinkedList<GenericConverter> converters;

        private ConvertersForPair() {
            this.converters = new LinkedList<>();
        }

        public void add(GenericConverter converter) {
            this.converters.addFirst(converter);
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x0011  */
        @org.springframework.lang.Nullable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public org.springframework.core.convert.converter.GenericConverter getConverter(org.springframework.core.convert.TypeDescriptor r5, org.springframework.core.convert.TypeDescriptor r6) {
            /*
                r4 = this;
                r0 = r4
                java.util.LinkedList<org.springframework.core.convert.converter.GenericConverter> r0 = r0.converters
                java.util.Iterator r0 = r0.iterator()
                r7 = r0
            L8:
                r0 = r7
                boolean r0 = r0.hasNext()
                if (r0 == 0) goto L39
                r0 = r7
                java.lang.Object r0 = r0.next()
                org.springframework.core.convert.converter.GenericConverter r0 = (org.springframework.core.convert.converter.GenericConverter) r0
                r8 = r0
                r0 = r8
                boolean r0 = r0 instanceof org.springframework.core.convert.converter.ConditionalGenericConverter
                if (r0 == 0) goto L33
                r0 = r8
                org.springframework.core.convert.converter.ConditionalGenericConverter r0 = (org.springframework.core.convert.converter.ConditionalGenericConverter) r0
                r1 = r5
                r2 = r6
                boolean r0 = r0.matches(r1, r2)
                if (r0 == 0) goto L36
            L33:
                r0 = r8
                return r0
            L36:
                goto L8
            L39:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.convert.support.GenericConversionService.ConvertersForPair.getConverter(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor):org.springframework.core.convert.converter.GenericConverter");
        }

        public String toString() {
            return StringUtils.collectionToCommaDelimitedString(this.converters);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/support/GenericConversionService$NoOpConverter.class */
    private static class NoOpConverter implements GenericConverter {
        private final String name;

        public NoOpConverter(String name) {
            this.name = name;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return null;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return source;
        }

        public String toString() {
            return this.name;
        }
    }
}