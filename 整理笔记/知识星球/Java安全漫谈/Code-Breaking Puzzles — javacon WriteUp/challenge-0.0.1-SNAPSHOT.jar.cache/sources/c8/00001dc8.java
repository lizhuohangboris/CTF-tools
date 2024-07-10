package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotatedElementUtils.class */
public abstract class AnnotatedElementUtils {
    @Nullable
    private static final Boolean CONTINUE = null;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotatedElementUtils$Processor.class */
    public interface Processor<T> {
        @Nullable
        T process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int i);

        void postProcess(@Nullable AnnotatedElement annotatedElement, Annotation annotation, T t);

        boolean alwaysProcesses();

        boolean aggregates();

        List<T> getAggregatedResults();
    }

    public static AnnotatedElement forAnnotations(final Annotation... annotations) {
        return new AnnotatedElement() { // from class: org.springframework.core.annotation.AnnotatedElementUtils.1
            @Override // java.lang.reflect.AnnotatedElement
            @Nullable
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                for (Annotation annotation : annotations) {
                    T t = (T) annotation;
                    if (t.annotationType() == annotationClass) {
                        return t;
                    }
                }
                return null;
            }

            @Override // java.lang.reflect.AnnotatedElement
            public Annotation[] getAnnotations() {
                return annotations;
            }

            @Override // java.lang.reflect.AnnotatedElement
            public Annotation[] getDeclaredAnnotations() {
                return annotations;
            }
        };
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getMetaAnnotationTypes(element, element.getAnnotation(annotationType));
    }

    public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        return getMetaAnnotationTypes(element, AnnotationUtils.getAnnotation(element, annotationName));
    }

    private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, @Nullable Annotation composed) {
        if (composed == null) {
            return Collections.emptySet();
        }
        try {
            final Set<String> types = new LinkedHashSet<>();
            searchWithGetSemantics(composed.annotationType(), Collections.emptySet(), null, null, new SimpleAnnotationProcessor<Object>(true) { // from class: org.springframework.core.annotation.AnnotatedElementUtils.2
                @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
                @Nullable
                public Object process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                    types.add(annotation.annotationType().getName());
                    return AnnotatedElementUtils.CONTINUE;
                }
            }, new HashSet(), 1);
            return types;
        } catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return hasMetaAnnotationTypes(element, annotationType, null);
    }

    public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
        return hasMetaAnnotationTypes(element, null, annotationName);
    }

    private static boolean hasMetaAnnotationTypes(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName) {
        return Boolean.TRUE.equals(searchWithGetSemantics(element, annotationType, annotationName, new SimpleAnnotationProcessor<Boolean>() { // from class: org.springframework.core.annotation.AnnotatedElementUtils.3
            @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
            @Nullable
            public Boolean process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                return metaDepth > 0 ? Boolean.TRUE : AnnotatedElementUtils.CONTINUE;
            }
        }));
    }

    public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element.isAnnotationPresent(annotationType)) {
            return true;
        }
        return Boolean.TRUE.equals(searchWithGetSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
    }

    public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
        return Boolean.TRUE.equals(searchWithGetSemantics(element, null, annotationName, alwaysTrueAnnotationProcessor));
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        AnnotationAttributes attributes = (AnnotationAttributes) searchWithGetSemantics(element, annotationType, null, new MergedAnnotationAttributesProcessor());
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
        return attributes;
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return getMergedAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = (AnnotationAttributes) searchWithGetSemantics(element, null, annotationName, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        AnnotationAttributes attributes;
        Annotation declaredAnnotation = element.getDeclaredAnnotation(annotationType);
        if (declaredAnnotation != null) {
            return (A) AnnotationUtils.synthesizeAnnotation(declaredAnnotation, element);
        }
        if (AnnotationUtils.hasPlainJavaAnnotationsOnly(element) || (attributes = getMergedAnnotationAttributes(element, annotationType)) == null) {
            return null;
        }
        return (A) AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
    }

    public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithGetSemantics(element, annotationType, null, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    public static Set<Annotation> getAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithGetSemantics(element, annotationTypes, null, null, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return getMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        if (containerType == null) {
            containerType = resolveContainerType(annotationType);
        } else {
            validateContainerType(annotationType, containerType);
        }
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithGetSemantics(element, Collections.singleton(annotationType), null, containerType, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName) {
        return getAllAnnotationAttributes(element, annotationName, false, false);
    }

    @Nullable
    public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {
        final MultiValueMap<String, Object> attributesMap = new LinkedMultiValueMap<>();
        searchWithGetSemantics(element, null, annotationName, new SimpleAnnotationProcessor<Object>() { // from class: org.springframework.core.annotation.AnnotatedElementUtils.4
            @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
            @Nullable
            public Object process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
                AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation, classValuesAsString, nestedAnnotationsAsMap);
                MultiValueMap multiValueMap = attributesMap;
                multiValueMap.getClass();
                annotationAttributes.forEach((v1, v2) -> {
                    r1.add(v1, v2);
                });
                return AnnotatedElementUtils.CONTINUE;
            }
        });
        if (attributesMap.isEmpty()) {
            return null;
        }
        return attributesMap;
    }

    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element.isAnnotationPresent(annotationType)) {
            return true;
        }
        return Boolean.TRUE.equals(searchWithFindSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = (AnnotationAttributes) searchWithFindSemantics(element, annotationType, null, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = (AnnotationAttributes) searchWithFindSemantics(element, null, annotationName, new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
        AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    @Nullable
    public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        AnnotationAttributes attributes;
        Annotation declaredAnnotation = element.getDeclaredAnnotation(annotationType);
        if (declaredAnnotation != null) {
            return (A) AnnotationUtils.synthesizeAnnotation(declaredAnnotation, element);
        }
        if (AnnotationUtils.hasPlainJavaAnnotationsOnly(element) || (attributes = findMergedAnnotationAttributes(element, (Class<? extends Annotation>) annotationType, false, false)) == null) {
            return null;
        }
        return (A) AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
    }

    public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element, Class<A> annotationType) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithFindSemantics(element, annotationType, null, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    public static Set<Annotation> findAllMergedAnnotations(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes) {
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithFindSemantics(element, annotationTypes, null, null, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        return findMergedRepeatableAnnotations(element, annotationType, null);
    }

    public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType, @Nullable Class<? extends Annotation> containerType) {
        if (containerType == null) {
            containerType = resolveContainerType(annotationType);
        } else {
            validateContainerType(annotationType, containerType);
        }
        MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
        searchWithFindSemantics(element, Collections.singleton(annotationType), null, containerType, processor);
        return postProcessAndSynthesizeAggregatedResults(element, processor.getAggregatedResults());
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, Processor<T> processor) {
        return (T) searchWithGetSemantics(element, annotationType != null ? Collections.singleton(annotationType) : Collections.emptySet(), annotationName, null, processor);
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor) {
        try {
            return (T) searchWithGetSemantics(element, annotationTypes, annotationName, containerType, processor, new HashSet(), 0);
        } catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    @Nullable
    private static <T> T searchWithGetSemantics(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        Class<?> superclass;
        Annotation[] annotations;
        if (visited.add(element)) {
            try {
                List<Annotation> declaredAnnotations = Arrays.asList(AnnotationUtils.getDeclaredAnnotations(element));
                T result = (T) searchWithGetSemanticsInAnnotations(element, declaredAnnotations, annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                if (result != null) {
                    return result;
                }
                if ((element instanceof Class) && (superclass = ((Class) element).getSuperclass()) != null && superclass != Object.class) {
                    List<Annotation> inheritedAnnotations = new LinkedList<>();
                    for (Annotation annotation : element.getAnnotations()) {
                        if (!declaredAnnotations.contains(annotation)) {
                            inheritedAnnotations.add(annotation);
                        }
                    }
                    T result2 = (T) searchWithGetSemanticsInAnnotations(element, inheritedAnnotations, annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                    if (result2 != null) {
                        return result2;
                    }
                    return null;
                }
                return null;
            } catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
                return null;
            }
        }
        return null;
    }

    @Nullable
    private static <T> T searchWithGetSemanticsInAnnotations(@Nullable AnnotatedElement element, List<Annotation> annotations, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        T result;
        Annotation[] rawAnnotationsFromContainer;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
            if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
                if (annotationTypes.contains(currentAnnotationType) || currentAnnotationType.getName().equals(annotationName) || processor.alwaysProcesses()) {
                    T result2 = processor.process(element, annotation, metaDepth);
                    if (result2 == null) {
                        continue;
                    } else if (processor.aggregates() && metaDepth == 0) {
                        processor.getAggregatedResults().add(result2);
                    } else {
                        return result2;
                    }
                } else if (currentAnnotationType == containerType) {
                    for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
                        T result3 = processor.process(element, contained, metaDepth);
                        if (result3 != null) {
                            processor.getAggregatedResults().add(result3);
                        }
                    }
                }
            }
        }
        for (Annotation annotation2 : annotations) {
            Class<? extends Annotation> currentAnnotationType2 = annotation2.annotationType();
            if (!AnnotationUtils.hasPlainJavaAnnotationsOnly(currentAnnotationType2) && (result = (T) searchWithGetSemantics(currentAnnotationType2, annotationTypes, annotationName, containerType, processor, visited, metaDepth + 1)) != null) {
                processor.postProcess(element, annotation2, result);
                if (processor.aggregates() && metaDepth == 0) {
                    processor.getAggregatedResults().add(result);
                } else {
                    return result;
                }
            }
        }
        return null;
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, @Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName, Processor<T> processor) {
        return (T) searchWithFindSemantics(element, annotationType != null ? Collections.singleton(annotationType) : Collections.emptySet(), annotationName, null, processor);
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor) {
        if (containerType != null && !processor.aggregates()) {
            throw new IllegalArgumentException("Searches for repeatable annotations must supply an aggregating Processor");
        }
        try {
            return (T) searchWithFindSemantics(element, annotationTypes, annotationName, containerType, processor, new HashSet(), 0);
        } catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
        }
    }

    @Nullable
    private static <T> T searchWithFindSemantics(AnnotatedElement element, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth) {
        Class<?>[] interfaces;
        T result;
        T result2;
        T result3;
        T result4;
        Annotation[] rawAnnotationsFromContainer;
        if (visited.add(element)) {
            try {
                Annotation[] annotations = AnnotationUtils.getDeclaredAnnotations(element);
                if (annotations.length > 0) {
                    List<T> aggregatedResults = processor.aggregates() ? new ArrayList<>() : null;
                    for (Annotation annotation : annotations) {
                        Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
                        if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
                            if (annotationTypes.contains(currentAnnotationType) || currentAnnotationType.getName().equals(annotationName) || processor.alwaysProcesses()) {
                                T result5 = processor.process(element, annotation, metaDepth);
                                if (result5 != null) {
                                    if (aggregatedResults != null && metaDepth == 0) {
                                        aggregatedResults.add(result5);
                                    } else {
                                        return result5;
                                    }
                                }
                            } else if (currentAnnotationType == containerType) {
                                for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
                                    T result6 = processor.process(element, contained, metaDepth);
                                    if (aggregatedResults != null && result6 != null) {
                                        aggregatedResults.add(result6);
                                    }
                                }
                            }
                        }
                    }
                    for (Annotation annotation2 : annotations) {
                        Class<? extends Annotation> currentAnnotationType2 = annotation2.annotationType();
                        if (!AnnotationUtils.hasPlainJavaAnnotationsOnly(currentAnnotationType2) && (result4 = (T) searchWithFindSemantics(currentAnnotationType2, annotationTypes, annotationName, containerType, processor, visited, metaDepth + 1)) != null) {
                            processor.postProcess(currentAnnotationType2, annotation2, result4);
                            if (aggregatedResults != null && metaDepth == 0) {
                                aggregatedResults.add(result4);
                            } else {
                                return result4;
                            }
                        }
                    }
                    if (!CollectionUtils.isEmpty(aggregatedResults)) {
                        processor.getAggregatedResults().addAll(0, aggregatedResults);
                    }
                }
                if (element instanceof Method) {
                    Method method = (Method) element;
                    Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
                    if (resolvedMethod != method && (result3 = (T) searchWithFindSemantics(resolvedMethod, annotationTypes, annotationName, containerType, processor, visited, metaDepth)) != null) {
                        return result3;
                    }
                    Class<?>[] ifcs = method.getDeclaringClass().getInterfaces();
                    if (ifcs.length > 0 && (result2 = (T) searchOnInterfaces(method, annotationTypes, annotationName, containerType, processor, visited, metaDepth, ifcs)) != null) {
                        return result2;
                    }
                    Class<?> clazz = method.getDeclaringClass();
                    while (true) {
                        clazz = clazz.getSuperclass();
                        if (clazz == null || clazz == Object.class) {
                            break;
                        }
                        Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(clazz);
                        if (!annotatedMethods.isEmpty()) {
                            for (Method annotatedMethod : annotatedMethods) {
                                if (AnnotationUtils.isOverride(method, annotatedMethod)) {
                                    Method resolvedSuperMethod = BridgeMethodResolver.findBridgedMethod(annotatedMethod);
                                    T result7 = (T) searchWithFindSemantics(resolvedSuperMethod, annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                                    if (result7 != null) {
                                        return result7;
                                    }
                                }
                            }
                        }
                        T result8 = (T) searchOnInterfaces(method, annotationTypes, annotationName, containerType, processor, visited, metaDepth, clazz.getInterfaces());
                        if (result8 != null) {
                            return result8;
                        }
                    }
                    return null;
                } else if (element instanceof Class) {
                    Class<?> clazz2 = (Class) element;
                    if (!Annotation.class.isAssignableFrom(clazz2)) {
                        for (Class<?> ifc : clazz2.getInterfaces()) {
                            T result9 = (T) searchWithFindSemantics(ifc, annotationTypes, annotationName, containerType, processor, visited, metaDepth);
                            if (result9 != null) {
                                return result9;
                            }
                        }
                        Class<?> superclass = clazz2.getSuperclass();
                        if (superclass != null && superclass != Object.class && (result = (T) searchWithFindSemantics(superclass, annotationTypes, annotationName, containerType, processor, visited, metaDepth)) != null) {
                            return result;
                        }
                        return null;
                    }
                    return null;
                } else {
                    return null;
                }
            } catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
                return null;
            }
        }
        return null;
    }

    @Nullable
    private static <T> T searchOnInterfaces(Method method, Set<Class<? extends Annotation>> annotationTypes, @Nullable String annotationName, @Nullable Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited, int metaDepth, Class<?>[] ifcs) {
        T result;
        for (Class<?> ifc : ifcs) {
            Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(ifc);
            if (!annotatedMethods.isEmpty()) {
                for (Method annotatedMethod : annotatedMethods) {
                    if (AnnotationUtils.isOverride(method, annotatedMethod) && (result = (T) searchWithFindSemantics(annotatedMethod, annotationTypes, annotationName, containerType, processor, visited, metaDepth)) != null) {
                        return result;
                    }
                }
                continue;
            }
        }
        return null;
    }

    private static <A extends Annotation> A[] getRawAnnotationsFromContainer(@Nullable AnnotatedElement element, Annotation container) {
        try {
            A[] value = (A[]) ((Annotation[]) AnnotationUtils.getValue(container));
            if (value != null) {
                return value;
            }
        } catch (Throwable ex) {
            AnnotationUtils.handleIntrospectionFailure(element, ex);
        }
        return (A[]) EMPTY_ANNOTATION_ARRAY;
    }

    private static Class<? extends Annotation> resolveContainerType(Class<? extends Annotation> annotationType) {
        Class<? extends Annotation> containerType = AnnotationUtils.resolveContainerAnnotationType(annotationType);
        if (containerType == null) {
            throw new IllegalArgumentException("Annotation type must be a repeatable annotation: failed to resolve container type for " + annotationType.getName());
        }
        return containerType;
    }

    private static void validateContainerType(Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType) {
        try {
            Method method = containerType.getDeclaredMethod("value", new Class[0]);
            Class<?> returnType = method.getReturnType();
            if (!returnType.isArray() || returnType.getComponentType() != annotationType) {
                String msg = String.format("Container type [%s] must declare a 'value' attribute for an array of type [%s]", containerType.getName(), annotationType.getName());
                throw new AnnotationConfigurationException(msg);
            }
        } catch (Throwable ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            String msg2 = String.format("Invalid declaration of container type [%s] for repeatable annotation [%s]", containerType.getName(), annotationType.getName());
            throw new AnnotationConfigurationException(msg2, ex);
        }
    }

    private static <A extends Annotation> Set<A> postProcessAndSynthesizeAggregatedResults(AnnotatedElement element, List<AnnotationAttributes> aggregatedResults) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (AnnotationAttributes attributes : aggregatedResults) {
            AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
            Class<? extends Annotation> annType = attributes.annotationType();
            if (annType != null) {
                linkedHashSet.add(AnnotationUtils.synthesizeAnnotation(attributes, annType, element));
            }
        }
        return linkedHashSet;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotatedElementUtils$SimpleAnnotationProcessor.class */
    private static abstract class SimpleAnnotationProcessor<T> implements Processor<T> {
        private final boolean alwaysProcesses;

        public SimpleAnnotationProcessor() {
            this(false);
        }

        public SimpleAnnotationProcessor(boolean alwaysProcesses) {
            this.alwaysProcesses = alwaysProcesses;
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public final boolean alwaysProcesses() {
            return this.alwaysProcesses;
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public final void postProcess(@Nullable AnnotatedElement annotatedElement, Annotation annotation, T result) {
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public final boolean aggregates() {
            return false;
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public final List<T> getAggregatedResults() {
            throw new UnsupportedOperationException("SimpleAnnotationProcessor does not support aggregated results");
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotatedElementUtils$AlwaysTrueBooleanAnnotationProcessor.class */
    static class AlwaysTrueBooleanAnnotationProcessor extends SimpleAnnotationProcessor<Boolean> {
        AlwaysTrueBooleanAnnotationProcessor() {
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public final Boolean process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return Boolean.TRUE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotatedElementUtils$MergedAnnotationAttributesProcessor.class */
    public static class MergedAnnotationAttributesProcessor implements Processor<AnnotationAttributes> {
        private final boolean classValuesAsString;
        private final boolean nestedAnnotationsAsMap;
        private final boolean aggregates;
        private final List<AnnotationAttributes> aggregatedResults;

        MergedAnnotationAttributesProcessor() {
            this(false, false, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
            this(classValuesAsString, nestedAnnotationsAsMap, false);
        }

        MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap, boolean aggregates) {
            this.classValuesAsString = classValuesAsString;
            this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
            this.aggregates = aggregates;
            this.aggregatedResults = aggregates ? new ArrayList<>() : Collections.emptyList();
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public boolean alwaysProcesses() {
            return false;
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public boolean aggregates() {
            return this.aggregates;
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public List<AnnotationAttributes> getAggregatedResults() {
            return this.aggregatedResults;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        @Nullable
        public AnnotationAttributes process(@Nullable AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
            return AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation, this.classValuesAsString, this.nestedAnnotationsAsMap);
        }

        @Override // org.springframework.core.annotation.AnnotatedElementUtils.Processor
        public void postProcess(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes) {
            Annotation annotation2 = AnnotationUtils.synthesizeAnnotation(annotation, element);
            Class<? extends Annotation> targetAnnotationType = attributes.annotationType();
            Set<String> valuesAlreadyReplaced = new HashSet<>();
            for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotation2.annotationType())) {
                String attributeName = attributeMethod.getName();
                String attributeOverrideName = AnnotationUtils.getAttributeOverrideName(attributeMethod, targetAnnotationType);
                if (attributeOverrideName != null) {
                    if (!valuesAlreadyReplaced.contains(attributeOverrideName)) {
                        List<String> targetAttributeNames = new ArrayList<>();
                        targetAttributeNames.add(attributeOverrideName);
                        valuesAlreadyReplaced.add(attributeOverrideName);
                        List<String> aliases = AnnotationUtils.getAttributeAliasMap(targetAnnotationType).get(attributeOverrideName);
                        if (aliases != null) {
                            for (String alias : aliases) {
                                if (!valuesAlreadyReplaced.contains(alias)) {
                                    targetAttributeNames.add(alias);
                                    valuesAlreadyReplaced.add(alias);
                                }
                            }
                        }
                        overrideAttributes(element, annotation2, attributes, attributeName, targetAttributeNames);
                    }
                } else if (!"value".equals(attributeName) && attributes.containsKey(attributeName)) {
                    overrideAttribute(element, annotation2, attributes, attributeName, attributeName);
                }
            }
        }

        private void overrideAttributes(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes, String sourceAttributeName, List<String> targetAttributeNames) {
            Object adaptedValue = getAdaptedValue(element, annotation, sourceAttributeName);
            for (String targetAttributeName : targetAttributeNames) {
                attributes.put(targetAttributeName, adaptedValue);
            }
        }

        private void overrideAttribute(@Nullable AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes, String sourceAttributeName, String targetAttributeName) {
            attributes.put(targetAttributeName, getAdaptedValue(element, annotation, sourceAttributeName));
        }

        @Nullable
        private Object getAdaptedValue(@Nullable AnnotatedElement element, Annotation annotation, String sourceAttributeName) {
            Object value = AnnotationUtils.getValue(annotation, sourceAttributeName);
            return AnnotationUtils.adaptValue(element, value, this.classValuesAsString, this.nestedAnnotationsAsMap);
        }
    }
}