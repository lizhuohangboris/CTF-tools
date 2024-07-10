package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationUtils.class */
public abstract class AnnotationUtils {
    public static final String VALUE = "value";
    private static final Map<AnnotationCacheKey, Annotation> findAnnotationCache = new ConcurrentReferenceHashMap(256);
    private static final Map<AnnotationCacheKey, Boolean> metaPresentCache = new ConcurrentReferenceHashMap(256);
    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationsCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<?>, Set<Method>> annotatedBaseTypeCache = new ConcurrentReferenceHashMap(256);
    @Deprecated
    private static final Map<Class<?>, ?> annotatedInterfaceCache = annotatedBaseTypeCache;
    private static final Map<Class<? extends Annotation>, Boolean> synthesizableCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Class<? extends Annotation>, List<Method>> attributeMethodsCache = new ConcurrentReferenceHashMap(256);
    private static final Map<Method, AliasDescriptor> aliasDescriptorCache = new ConcurrentReferenceHashMap(256);
    @Nullable
    private static transient Log logger;

    @Nullable
    public static <A extends Annotation> A getAnnotation(Annotation annotation, Class<A> annotationType) {
        if (annotationType.isInstance(annotation)) {
            return (A) synthesizeAnnotation(annotation);
        }
        Class<? extends Annotation> annotatedElement = annotation.annotationType();
        try {
            Annotation annotation2 = annotatedElement.getAnnotation(annotationType);
            if (annotation2 != null) {
                return (A) synthesizeAnnotation(annotation2, (AnnotatedElement) annotatedElement);
            }
            return null;
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        Annotation[] annotations;
        try {
            Annotation annotation = annotatedElement.getAnnotation(annotationType);
            if (annotation == null) {
                for (Annotation metaAnn : annotatedElement.getAnnotations()) {
                    annotation = metaAnn.annotationType().getAnnotation(annotationType);
                    if (annotation != null) {
                        break;
                    }
                }
            }
            if (annotation != null) {
                return (A) synthesizeAnnotation(annotation, annotatedElement);
            }
            return null;
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        return (A) getAnnotation((AnnotatedElement) resolvedMethod, (Class<Annotation>) annotationType);
    }

    @Nullable
    public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
        try {
            return synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static Annotation[] getAnnotations(Method method) {
        try {
            return synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
        } catch (Throwable ex) {
            handleIntrospectionFailure(method, ex);
            return null;
        }
    }

    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return getRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        Class<?> superclass;
        Set<A> annotations = getDeclaredRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType);
        if (annotations.isEmpty() && (annotatedElement instanceof Class) && (superclass = ((Class) annotatedElement).getSuperclass()) != null && superclass != Object.class) {
            return getRepeatableAnnotations(superclass, annotationType, containerAnnotationType);
        }
        return annotations;
    }

    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
    }

    public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement, Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
        try {
            if (annotatedElement instanceof Method) {
                annotatedElement = BridgeMethodResolver.findBridgedMethod((Method) annotatedElement);
            }
            return new AnnotationCollector(annotationType, containerAnnotationType).getResult(annotatedElement);
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return Collections.emptySet();
        }
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        Annotation findAnnotation = findAnnotation(annotatedElement, annotationType, new HashSet());
        if (findAnnotation != null) {
            return (A) synthesizeAnnotation(findAnnotation, annotatedElement);
        }
        return null;
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType, Set<Annotation> visited) {
        Annotation[] declaredAnnotations;
        A annotation;
        try {
            A annotation2 = (A) annotatedElement.getDeclaredAnnotation(annotationType);
            if (annotation2 != null) {
                return annotation2;
            }
            for (Annotation declaredAnn : getDeclaredAnnotations(annotatedElement)) {
                Class<? extends Annotation> declaredType = declaredAnn.annotationType();
                if (!isInJavaLangAnnotationPackage(declaredType) && visited.add(declaredAnn) && (annotation = (A) findAnnotation((AnnotatedElement) declaredType, (Class<Annotation>) annotationType, visited)) != null) {
                    return annotation;
                }
            }
            return null;
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotatedElement, ex);
            return null;
        }
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> annotationType) {
        Assert.notNull(method, "Method must not be null");
        if (annotationType == null) {
            return null;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(method, annotationType);
        Annotation annotation = findAnnotationCache.get(cacheKey);
        if (annotation == null) {
            Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
            annotation = findAnnotation((AnnotatedElement) resolvedMethod, (Class<Annotation>) annotationType);
            if (annotation == null) {
                annotation = searchOnInterfaces(method, annotationType, method.getDeclaringClass().getInterfaces());
            }
            Class<?> clazz = method.getDeclaringClass();
            while (annotation == null) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz == Object.class) {
                    break;
                }
                Set<Method> annotatedMethods = getAnnotatedMethodsInBaseType(clazz);
                if (!annotatedMethods.isEmpty()) {
                    for (Method annotatedMethod : annotatedMethods) {
                        if (isOverride(method, annotatedMethod)) {
                            Method resolvedSuperMethod = BridgeMethodResolver.findBridgedMethod(annotatedMethod);
                            annotation = findAnnotation((AnnotatedElement) resolvedSuperMethod, (Class<Annotation>) annotationType);
                            if (annotation != null) {
                                break;
                            }
                        }
                    }
                }
                if (annotation == null) {
                    annotation = searchOnInterfaces(method, annotationType, clazz.getInterfaces());
                }
            }
            if (annotation != null) {
                annotation = synthesizeAnnotation(annotation, (AnnotatedElement) method);
                findAnnotationCache.put(cacheKey, annotation);
            }
        }
        return (A) annotation;
    }

    @Nullable
    private static <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType, Class<?>... ifcs) {
        A annotation;
        for (Class<?> ifc : ifcs) {
            Set<Method> annotatedMethods = getAnnotatedMethodsInBaseType(ifc);
            if (!annotatedMethods.isEmpty()) {
                for (Method annotatedMethod : annotatedMethods) {
                    if (isOverride(method, annotatedMethod) && (annotation = (A) getAnnotation(annotatedMethod, (Class<Annotation>) annotationType)) != null) {
                        return annotation;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public static boolean isOverride(Method method, Method candidate) {
        if (!candidate.getName().equals(method.getName()) || candidate.getParameterCount() != method.getParameterCount()) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        if (Arrays.equals(candidate.getParameterTypes(), paramTypes)) {
            return true;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] != ResolvableType.forMethodParameter(candidate, i, method.getDeclaringClass()).resolve()) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:67:0x005c, code lost:
        if (java.lang.reflect.Modifier.isPrivate(r0.getModifiers()) == false) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Set<java.lang.reflect.Method> getAnnotatedMethodsInBaseType(java.lang.Class<?> r4) {
        /*
            r0 = r4
            boolean r0 = r0.isInterface()
            r5 = r0
            r0 = r5
            if (r0 == 0) goto L14
            r0 = r4
            boolean r0 = org.springframework.util.ClassUtils.isJavaLanguageInterface(r0)
            if (r0 == 0) goto L14
            java.util.Set r0 = java.util.Collections.emptySet()
            return r0
        L14:
            java.util.Map<java.lang.Class<?>, java.util.Set<java.lang.reflect.Method>> r0 = org.springframework.core.annotation.AnnotationUtils.annotatedBaseTypeCache
            r1 = r4
            java.lang.Object r0 = r0.get(r1)
            java.util.Set r0 = (java.util.Set) r0
            r6 = r0
            r0 = r6
            if (r0 == 0) goto L27
            r0 = r6
            return r0
        L27:
            r0 = r5
            if (r0 == 0) goto L32
            r0 = r4
            java.lang.reflect.Method[] r0 = r0.getMethods()
            goto L36
        L32:
            r0 = r4
            java.lang.reflect.Method[] r0 = r0.getDeclaredMethods()
        L36:
            r7 = r0
            r0 = r7
            r8 = r0
            r0 = r8
            int r0 = r0.length
            r9 = r0
            r0 = 0
            r10 = r0
        L42:
            r0 = r10
            r1 = r9
            if (r0 >= r1) goto L8e
            r0 = r8
            r1 = r10
            r0 = r0[r1]
            r11 = r0
            r0 = r5
            if (r0 != 0) goto L5f
            r0 = r11
            int r0 = r0.getModifiers()     // Catch: java.lang.Throwable -> L7f
            boolean r0 = java.lang.reflect.Modifier.isPrivate(r0)     // Catch: java.lang.Throwable -> L7f
            if (r0 != 0) goto L7c
        L5f:
            r0 = r11
            boolean r0 = hasSearchableAnnotations(r0)     // Catch: java.lang.Throwable -> L7f
            if (r0 == 0) goto L7c
            r0 = r6
            if (r0 != 0) goto L73
            java.util.HashSet r0 = new java.util.HashSet     // Catch: java.lang.Throwable -> L7f
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L7f
            r6 = r0
        L73:
            r0 = r6
            r1 = r11
            boolean r0 = r0.add(r1)     // Catch: java.lang.Throwable -> L7f
        L7c:
            goto L88
        L7f:
            r12 = move-exception
            r0 = r11
            r1 = r12
            handleIntrospectionFailure(r0, r1)
        L88:
            int r10 = r10 + 1
            goto L42
        L8e:
            r0 = r6
            if (r0 != 0) goto L96
            java.util.Set r0 = java.util.Collections.emptySet()
            r6 = r0
        L96:
            java.util.Map<java.lang.Class<?>, java.util.Set<java.lang.reflect.Method>> r0 = org.springframework.core.annotation.AnnotationUtils.annotatedBaseTypeCache
            r1 = r4
            r2 = r6
            java.lang.Object r0 = r0.put(r1, r2)
            r0 = r6
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.annotation.AnnotationUtils.getAnnotatedMethodsInBaseType(java.lang.Class):java.util.Set");
    }

    private static boolean hasSearchableAnnotations(Method ifcMethod) {
        Annotation[] anns = getDeclaredAnnotations(ifcMethod);
        if (anns.length == 0) {
            return false;
        }
        for (Annotation ann : anns) {
            String name = ann.annotationType().getName();
            if (!name.startsWith("java.lang.") && !name.startsWith("org.springframework.lang.")) {
                return true;
            }
        }
        return false;
    }

    public static Annotation[] getDeclaredAnnotations(AnnotatedElement element) {
        if ((element instanceof Class) || (element instanceof Member)) {
            return declaredAnnotationsCache.computeIfAbsent(element, (v0) -> {
                return v0.getDeclaredAnnotations();
            });
        }
        return element.getDeclaredAnnotations();
    }

    @Nullable
    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return (A) findAnnotation(clazz, (Class<Annotation>) annotationType, true);
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(Class<?> clazz, @Nullable Class<A> annotationType, boolean synthesize) {
        Assert.notNull(clazz, "Class must not be null");
        if (annotationType == null) {
            return null;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(clazz, annotationType);
        Annotation annotation = findAnnotationCache.get(cacheKey);
        if (annotation == null) {
            annotation = findAnnotation(clazz, (Class<Annotation>) annotationType, (Set<Annotation>) new HashSet());
            if (annotation != null && synthesize) {
                annotation = synthesizeAnnotation(annotation, (AnnotatedElement) clazz);
                findAnnotationCache.put(cacheKey, annotation);
            }
        }
        return (A) annotation;
    }

    @Nullable
    private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
        Annotation[] declaredAnnotations;
        Class<?>[] interfaces;
        A annotation;
        try {
            A annotation2 = (A) clazz.getDeclaredAnnotation(annotationType);
            if (annotation2 != null) {
                return annotation2;
            }
            for (Annotation declaredAnn : getDeclaredAnnotations(clazz)) {
                Class<? extends Annotation> declaredType = declaredAnn.annotationType();
                if (!isInJavaLangAnnotationPackage(declaredType) && visited.add(declaredAnn) && (annotation = (A) findAnnotation((Class<?>) declaredType, (Class<Annotation>) annotationType, visited)) != null) {
                    return annotation;
                }
            }
            for (Class<?> ifc : clazz.getInterfaces()) {
                A annotation3 = (A) findAnnotation(ifc, (Class<Annotation>) annotationType, visited);
                if (annotation3 != null) {
                    return annotation3;
                }
            }
            Class<?> superclass = clazz.getSuperclass();
            if (superclass == null || superclass == Object.class) {
                return null;
            }
            return (A) findAnnotation(superclass, (Class<Annotation>) annotationType, visited);
        } catch (Throwable ex) {
            handleIntrospectionFailure(clazz, ex);
            return null;
        }
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, @Nullable Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }
        if (isAnnotationDeclaredLocally(annotationType, clazz)) {
            return clazz;
        }
        return findAnnotationDeclaringClass(annotationType, clazz.getSuperclass());
    }

    @Nullable
    public static Class<?> findAnnotationDeclaringClassForTypes(List<Class<? extends Annotation>> annotationTypes, @Nullable Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (isAnnotationDeclaredLocally(annotationType, clazz)) {
                return clazz;
            }
        }
        return findAnnotationDeclaringClassForTypes(annotationTypes, clazz.getSuperclass());
    }

    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
        try {
            return clazz.getDeclaredAnnotation(annotationType) != null;
        } catch (Throwable ex) {
            handleIntrospectionFailure(clazz, ex);
            return false;
        }
    }

    public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
        return clazz.isAnnotationPresent(annotationType) && !isAnnotationDeclaredLocally(annotationType, clazz);
    }

    public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType, @Nullable Class<? extends Annotation> metaAnnotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        if (metaAnnotationType == null) {
            return false;
        }
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(annotationType, metaAnnotationType);
        Boolean metaPresent = metaPresentCache.get(cacheKey);
        if (metaPresent != null) {
            return metaPresent.booleanValue();
        }
        Boolean metaPresent2 = Boolean.FALSE;
        if (findAnnotation((Class<?>) annotationType, (Class<Annotation>) metaAnnotationType, false) != null) {
            metaPresent2 = Boolean.TRUE;
        }
        metaPresentCache.put(cacheKey, metaPresent2);
        return metaPresent2.booleanValue();
    }

    public static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
        Class<?> clazz;
        if (annotatedElement instanceof Class) {
            clazz = (Class) annotatedElement;
        } else if (annotatedElement instanceof Member) {
            clazz = ((Member) annotatedElement).getDeclaringClass();
        } else {
            return false;
        }
        String name = clazz.getName();
        return name.startsWith("java") || name.startsWith("org.springframework.lang.");
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable Annotation annotation) {
        return annotation != null && isInJavaLangAnnotationPackage(annotation.annotationType());
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable Class<? extends Annotation> annotationType) {
        return annotationType != null && isInJavaLangAnnotationPackage(annotationType.getName());
    }

    public static boolean isInJavaLangAnnotationPackage(@Nullable String annotationType) {
        return annotationType != null && annotationType.startsWith("java.lang.annotation");
    }

    public static void validateAnnotation(Annotation annotation) {
        for (Method method : getAttributeMethods(annotation.annotationType())) {
            Class<?> returnType = method.getReturnType();
            if (returnType == Class.class || returnType == Class[].class) {
                try {
                    method.invoke(annotation, new Object[0]);
                } catch (Throwable ex) {
                    throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
                }
            }
        }
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        return getAnnotationAttributes((AnnotatedElement) null, annotation);
    }

    public static Map<String, Object> getAnnotationAttributes(Annotation annotation, boolean classValuesAsString) {
        return getAnnotationAttributes(annotation, classValuesAsString, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        return getAnnotationAttributes((AnnotatedElement) null, annotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation) {
        return getAnnotationAttributes(annotatedElement, annotation, false, false);
    }

    public static AnnotationAttributes getAnnotationAttributes(@Nullable AnnotatedElement annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        return getAnnotationAttributes((Object) annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
    }

    private static AnnotationAttributes getAnnotationAttributes(@Nullable Object annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        AnnotationAttributes attributes = retrieveAnnotationAttributes(annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
        postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, nestedAnnotationsAsMap);
        return attributes;
    }

    public static AnnotationAttributes retrieveAnnotationAttributes(@Nullable Object annotatedElement, Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        AnnotationAttributes attributes = new AnnotationAttributes(annotationType);
        for (Method method : getAttributeMethods(annotationType)) {
            try {
                Object attributeValue = method.invoke(annotation, new Object[0]);
                Object defaultValue = method.getDefaultValue();
                if (defaultValue != null && ObjectUtils.nullSafeEquals(attributeValue, defaultValue)) {
                    attributeValue = new DefaultValueHolder(defaultValue);
                }
                attributes.put(method.getName(), adaptValue(annotatedElement, attributeValue, classValuesAsString, nestedAnnotationsAsMap));
            } catch (Throwable ex) {
                if (ex instanceof InvocationTargetException) {
                    Throwable targetException = ((InvocationTargetException) ex).getTargetException();
                    rethrowAnnotationConfigurationException(targetException);
                }
                throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
            }
        }
        return attributes;
    }

    @Nullable
    public static Object adaptValue(@Nullable Object annotatedElement, @Nullable Object value, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (classValuesAsString) {
            if (value instanceof Class) {
                return ((Class) value).getName();
            }
            if (value instanceof Class[]) {
                Class<?>[] clazzArray = (Class[]) value;
                String[] classNames = new String[clazzArray.length];
                for (int i = 0; i < clazzArray.length; i++) {
                    classNames[i] = clazzArray[i].getName();
                }
                return classNames;
            }
        }
        if (value instanceof Annotation) {
            Annotation annotation = (Annotation) value;
            if (nestedAnnotationsAsMap) {
                return getAnnotationAttributes(annotatedElement, annotation, classValuesAsString, true);
            }
            return synthesizeAnnotation(annotation, annotatedElement);
        } else if (value instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[]) value;
            if (nestedAnnotationsAsMap) {
                AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[annotations.length];
                for (int i2 = 0; i2 < annotations.length; i2++) {
                    mappedAnnotations[i2] = getAnnotationAttributes(annotatedElement, annotations[i2], classValuesAsString, true);
                }
                return mappedAnnotations;
            }
            return synthesizeAnnotationArray(annotations, annotatedElement);
        } else {
            return value;
        }
    }

    public static void registerDefaultValues(AnnotationAttributes attributes) {
        Class<? extends Annotation> annotationType = attributes.annotationType();
        if (annotationType != null && Modifier.isPublic(annotationType.getModifiers())) {
            for (Method annotationAttribute : getAttributeMethods(annotationType)) {
                String attributeName = annotationAttribute.getName();
                Object defaultValue = annotationAttribute.getDefaultValue();
                if (defaultValue != null && !attributes.containsKey(attributeName)) {
                    if (defaultValue instanceof Annotation) {
                        defaultValue = getAnnotationAttributes((Annotation) defaultValue, false, true);
                    } else if (defaultValue instanceof Annotation[]) {
                        Annotation[] realAnnotations = (Annotation[]) defaultValue;
                        AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
                        for (int i = 0; i < realAnnotations.length; i++) {
                            mappedAnnotations[i] = getAnnotationAttributes(realAnnotations[i], false, true);
                        }
                        defaultValue = mappedAnnotations;
                    }
                    attributes.put(attributeName, new DefaultValueHolder(defaultValue));
                }
            }
        }
    }

    public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, AnnotationAttributes attributes, boolean classValuesAsString) {
        postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, false);
    }

    public static void postProcessAnnotationAttributes(@Nullable Object annotatedElement, @Nullable AnnotationAttributes attributes, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
        if (attributes == null) {
            return;
        }
        Class<? extends Annotation> annotationType = attributes.annotationType();
        Set<String> valuesAlreadyReplaced = new HashSet<>();
        if (!attributes.validated) {
            Map<String, List<String>> aliasMap = getAttributeAliasMap(annotationType);
            aliasMap.forEach(attributeName, aliasedAttributeNames -> {
                if (valuesAlreadyReplaced.contains(attributeName)) {
                    return;
                }
                Object value = attributes.get(attributeName);
                boolean valuePresent = (value == null || (value instanceof DefaultValueHolder)) ? false : true;
                Iterator it = aliasedAttributeNames.iterator();
                while (it.hasNext()) {
                    String aliasedAttributeName = (String) it.next();
                    if (!valuesAlreadyReplaced.contains(aliasedAttributeName)) {
                        Object aliasedValue = attributes.get(aliasedAttributeName);
                        boolean aliasPresent = (aliasedValue == null || (aliasedValue instanceof DefaultValueHolder)) ? false : true;
                        if (valuePresent || aliasPresent) {
                            if (valuePresent && aliasPresent) {
                                if (!ObjectUtils.nullSafeEquals(value, aliasedValue)) {
                                    String elementAsString = annotatedElement != null ? annotatedElement.toString() : "unknown element";
                                    throw new AnnotationConfigurationException(String.format("In AnnotationAttributes for annotation [%s] declared on %s, attribute '%s' and its alias '%s' are declared with values of [%s] and [%s], but only one is permitted.", attributes.displayName, elementAsString, attributeName, aliasedAttributeName, ObjectUtils.nullSafeToString(value), ObjectUtils.nullSafeToString(aliasedValue)));
                                }
                            } else if (aliasPresent) {
                                attributes.put(attributeName, adaptValue(annotatedElement, aliasedValue, classValuesAsString, nestedAnnotationsAsMap));
                                valuesAlreadyReplaced.add(attributeName);
                            } else {
                                attributes.put(aliasedAttributeName, adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
                                valuesAlreadyReplaced.add(aliasedAttributeName);
                            }
                        }
                    }
                }
            });
            attributes.validated = true;
        }
        for (Map.Entry<String, Object> attributeEntry : attributes.entrySet()) {
            String attributeName2 = attributeEntry.getKey();
            if (!valuesAlreadyReplaced.contains(attributeName2)) {
                Object value = attributeEntry.getValue();
                if (value instanceof DefaultValueHolder) {
                    attributes.put(attributeName2, adaptValue(annotatedElement, ((DefaultValueHolder) value).defaultValue, classValuesAsString, nestedAnnotationsAsMap));
                }
            }
        }
    }

    @Nullable
    public static Object getValue(Annotation annotation) {
        return getValue(annotation, "value");
    }

    @Nullable
    public static Object getValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName, new Class[0]);
            ReflectionUtils.makeAccessible(method);
            return method.invoke(annotation, new Object[0]);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException ex) {
            rethrowAnnotationConfigurationException(ex.getTargetException());
            throw new IllegalStateException("Could not obtain value for annotation attribute '" + attributeName + "' in " + annotation, ex);
        } catch (Throwable ex2) {
            handleIntrospectionFailure(annotation.getClass(), ex2);
            return null;
        }
    }

    @Nullable
    public static Object getDefaultValue(Annotation annotation) {
        return getDefaultValue(annotation, "value");
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Annotation annotation, @Nullable String attributeName) {
        if (annotation == null) {
            return null;
        }
        return getDefaultValue(annotation.annotationType(), attributeName);
    }

    @Nullable
    public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
        return getDefaultValue(annotationType, "value");
    }

    @Nullable
    public static Object getDefaultValue(@Nullable Class<? extends Annotation> annotationType, @Nullable String attributeName) {
        if (annotationType == null || !StringUtils.hasText(attributeName)) {
            return null;
        }
        try {
            return annotationType.getDeclaredMethod(attributeName, new Class[0]).getDefaultValue();
        } catch (Throwable ex) {
            handleIntrospectionFailure(annotationType, ex);
            return null;
        }
    }

    static <A extends Annotation> A synthesizeAnnotation(A annotation) {
        return (A) synthesizeAnnotation((Annotation) annotation, (AnnotatedElement) null);
    }

    public static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable AnnotatedElement annotatedElement) {
        return (A) synthesizeAnnotation((Annotation) annotation, (Object) annotatedElement);
    }

    public static <A extends Annotation> A synthesizeAnnotation(A annotation, @Nullable Object annotatedElement) {
        if ((annotation instanceof SynthesizedAnnotation) || hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotation;
        }
        Class<?> annotationType = annotation.annotationType();
        if (!isSynthesizable(annotationType)) {
            return annotation;
        }
        DefaultAnnotationAttributeExtractor attributeExtractor = new DefaultAnnotationAttributeExtractor(annotation, annotatedElement);
        InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);
        Class<?>[] exposedInterfaces = {annotationType, SynthesizedAnnotation.class};
        return (A) Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, handler);
    }

    public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> attributes, Class<A> annotationType, @Nullable AnnotatedElement annotatedElement) {
        MapAnnotationAttributeExtractor attributeExtractor = new MapAnnotationAttributeExtractor(attributes, annotationType, annotatedElement);
        InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);
        return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), canExposeSynthesizedMarker(annotationType) ? new Class[]{annotationType, SynthesizedAnnotation.class} : new Class[]{annotationType}, handler);
    }

    public static <A extends Annotation> A synthesizeAnnotation(Class<A> annotationType) {
        return (A) synthesizeAnnotation(Collections.emptyMap(), annotationType, null);
    }

    public static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, @Nullable Object annotatedElement) {
        if (hasPlainJavaAnnotationsOnly(annotatedElement)) {
            return annotations;
        }
        Annotation[] synthesized = (Annotation[]) Array.newInstance(annotations.getClass().getComponentType(), annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
        }
        return synthesized;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public static <A extends Annotation> A[] synthesizeAnnotationArray(@Nullable Map<String, Object>[] maps, Class<A> annotationType) {
        if (maps == null) {
            return null;
        }
        A[] synthesized = (A[]) ((Annotation[]) Array.newInstance((Class<?>) annotationType, maps.length));
        for (int i = 0; i < maps.length; i++) {
            synthesized[i] = synthesizeAnnotation(maps[i], annotationType, null);
        }
        return synthesized;
    }

    public static Map<String, List<String>> getAttributeAliasMap(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
        if (map != null) {
            return map;
        }
        Map<String, List<String>> map2 = new LinkedHashMap<>();
        for (Method attribute : getAttributeMethods(annotationType)) {
            List<String> aliasNames = getAttributeAliasNames(attribute);
            if (!aliasNames.isEmpty()) {
                map2.put(attribute.getName(), aliasNames);
            }
        }
        attributeAliasesCache.put(annotationType, map2);
        return map2;
    }

    private static boolean canExposeSynthesizedMarker(Class<? extends Annotation> annotationType) {
        try {
            return Class.forName(SynthesizedAnnotation.class.getName(), false, annotationType.getClassLoader()) == SynthesizedAnnotation.class;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isSynthesizable(Class<? extends Annotation> annotationType) {
        if (hasPlainJavaAnnotationsOnly(annotationType)) {
            return false;
        }
        Boolean synthesizable = synthesizableCache.get(annotationType);
        if (synthesizable != null) {
            return synthesizable.booleanValue();
        }
        Boolean synthesizable2 = Boolean.FALSE;
        Iterator<Method> it = getAttributeMethods(annotationType).iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Method attribute = it.next();
            if (!getAttributeAliasNames(attribute).isEmpty()) {
                synthesizable2 = Boolean.TRUE;
                break;
            }
            Class<?> returnType = attribute.getReturnType();
            if (Annotation[].class.isAssignableFrom(returnType)) {
                if (isSynthesizable(returnType.getComponentType())) {
                    synthesizable2 = Boolean.TRUE;
                    break;
                }
            } else if (Annotation.class.isAssignableFrom(returnType) && isSynthesizable(returnType)) {
                synthesizable2 = Boolean.TRUE;
                break;
            }
        }
        synthesizableCache.put(annotationType, synthesizable2);
        return synthesizable2.booleanValue();
    }

    static List<String> getAttributeAliasNames(Method attribute) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        return descriptor != null ? descriptor.getAttributeAliasNames() : Collections.emptyList();
    }

    @Nullable
    public static String getAttributeOverrideName(Method attribute, @Nullable Class<? extends Annotation> metaAnnotationType) {
        AliasDescriptor descriptor = AliasDescriptor.from(attribute);
        if (descriptor == null || metaAnnotationType == null) {
            return null;
        }
        return descriptor.getAttributeOverrideName(metaAnnotationType);
    }

    public static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
        Method[] declaredMethods;
        List<Method> methods = attributeMethodsCache.get(annotationType);
        if (methods != null) {
            return methods;
        }
        List<Method> methods2 = new ArrayList<>();
        for (Method method : annotationType.getDeclaredMethods()) {
            if (isAttributeMethod(method)) {
                ReflectionUtils.makeAccessible(method);
                methods2.add(method);
            }
        }
        attributeMethodsCache.put(annotationType, methods2);
        return methods2;
    }

    @Nullable
    public static Annotation getAnnotation(AnnotatedElement element, String annotationName) {
        Annotation[] annotations;
        for (Annotation annotation : element.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationName)) {
                return annotation;
            }
        }
        return null;
    }

    public static boolean isAttributeMethod(@Nullable Method method) {
        return (method == null || method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE) ? false : true;
    }

    public static boolean isAnnotationTypeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("annotationType") && method.getParameterCount() == 0;
    }

    @Nullable
    public static Class<? extends Annotation> resolveContainerAnnotationType(Class<? extends Annotation> annotationType) {
        Repeatable repeatable = (Repeatable) getAnnotation(annotationType, Repeatable.class);
        if (repeatable != null) {
            return repeatable.value();
        }
        return null;
    }

    public static void rethrowAnnotationConfigurationException(Throwable ex) {
        if (ex instanceof AnnotationConfigurationException) {
            throw ((AnnotationConfigurationException) ex);
        }
    }

    public static void handleIntrospectionFailure(@Nullable AnnotatedElement element, Throwable ex) {
        rethrowAnnotationConfigurationException(ex);
        Log loggerToUse = logger;
        if (loggerToUse == null) {
            loggerToUse = LogFactory.getLog(AnnotationUtils.class);
            logger = loggerToUse;
        }
        if ((element instanceof Class) && Annotation.class.isAssignableFrom((Class) element)) {
            if (loggerToUse.isDebugEnabled()) {
                loggerToUse.debug("Failed to meta-introspect annotation " + element + ": " + ex);
            }
        } else if (loggerToUse.isInfoEnabled()) {
            loggerToUse.info("Failed to introspect annotations on " + element + ": " + ex);
        }
    }

    public static void clearCache() {
        findAnnotationCache.clear();
        metaPresentCache.clear();
        declaredAnnotationsCache.clear();
        annotatedBaseTypeCache.clear();
        synthesizableCache.clear();
        attributeAliasesCache.clear();
        attributeMethodsCache.clear();
        aliasDescriptorCache.clear();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationUtils$AnnotationCacheKey.class */
    public static final class AnnotationCacheKey implements Comparable<AnnotationCacheKey> {
        private final AnnotatedElement element;
        private final Class<? extends Annotation> annotationType;

        public AnnotationCacheKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {
            this.element = element;
            this.annotationType = annotationType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationCacheKey)) {
                return false;
            }
            AnnotationCacheKey otherKey = (AnnotationCacheKey) other;
            return this.element.equals(otherKey.element) && this.annotationType.equals(otherKey.annotationType);
        }

        public int hashCode() {
            return (this.element.hashCode() * 29) + this.annotationType.hashCode();
        }

        public String toString() {
            return "@" + this.annotationType + " on " + this.element;
        }

        @Override // java.lang.Comparable
        public int compareTo(AnnotationCacheKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.annotationType.getName().compareTo(other.annotationType.getName());
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationUtils$AnnotationCollector.class */
    public static class AnnotationCollector<A extends Annotation> {
        private final Class<A> annotationType;
        @Nullable
        private final Class<? extends Annotation> containerAnnotationType;
        private final Set<AnnotatedElement> visited = new HashSet();
        private final Set<A> result = new LinkedHashSet();

        AnnotationCollector(Class<A> annotationType, @Nullable Class<? extends Annotation> containerAnnotationType) {
            this.annotationType = annotationType;
            this.containerAnnotationType = containerAnnotationType != null ? containerAnnotationType : AnnotationUtils.resolveContainerAnnotationType(annotationType);
        }

        Set<A> getResult(AnnotatedElement element) {
            process(element);
            return Collections.unmodifiableSet(this.result);
        }

        /* JADX WARN: Multi-variable type inference failed */
        private void process(AnnotatedElement element) {
            if (this.visited.add(element)) {
                try {
                    Annotation[] annotations = AnnotationUtils.getDeclaredAnnotations(element);
                    for (Annotation ann : annotations) {
                        Class<? extends Annotation> currentAnnotationType = ann.annotationType();
                        if (ObjectUtils.nullSafeEquals(this.annotationType, currentAnnotationType)) {
                            this.result.add(AnnotationUtils.synthesizeAnnotation(ann, element));
                        } else if (ObjectUtils.nullSafeEquals(this.containerAnnotationType, currentAnnotationType)) {
                            this.result.addAll(getValue(element, ann));
                        } else if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
                            process(currentAnnotationType);
                        }
                    }
                } catch (Throwable ex) {
                    AnnotationUtils.handleIntrospectionFailure(element, ex);
                }
            }
        }

        private List<A> getValue(AnnotatedElement element, Annotation annotation) {
            try {
                ArrayList arrayList = new ArrayList();
                Annotation[] annotationArr = (Annotation[]) AnnotationUtils.getValue(annotation);
                if (annotationArr != null) {
                    for (Annotation annotation2 : annotationArr) {
                        arrayList.add(AnnotationUtils.synthesizeAnnotation(annotation2, element));
                    }
                }
                return arrayList;
            } catch (Throwable ex) {
                AnnotationUtils.handleIntrospectionFailure(element, ex);
                return Collections.emptyList();
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationUtils$AliasDescriptor.class */
    public static final class AliasDescriptor {
        private final Method sourceAttribute;
        private final Class<? extends Annotation> sourceAnnotationType;
        private final String sourceAttributeName;
        private final Method aliasedAttribute;
        private final Class<? extends Annotation> aliasedAnnotationType;
        private final String aliasedAttributeName;
        private final boolean isAliasPair;

        @Nullable
        public static AliasDescriptor from(Method attribute) {
            AliasDescriptor descriptor = (AliasDescriptor) AnnotationUtils.aliasDescriptorCache.get(attribute);
            if (descriptor != null) {
                return descriptor;
            }
            AliasFor aliasFor = (AliasFor) attribute.getAnnotation(AliasFor.class);
            if (aliasFor == null) {
                return null;
            }
            AliasDescriptor descriptor2 = new AliasDescriptor(attribute, aliasFor);
            descriptor2.validate();
            AnnotationUtils.aliasDescriptorCache.put(attribute, descriptor2);
            return descriptor2;
        }

        private AliasDescriptor(Method sourceAttribute, AliasFor aliasFor) {
            Class declaringClass = sourceAttribute.getDeclaringClass();
            this.sourceAttribute = sourceAttribute;
            this.sourceAnnotationType = declaringClass;
            this.sourceAttributeName = sourceAttribute.getName();
            this.aliasedAnnotationType = Annotation.class == aliasFor.annotation() ? this.sourceAnnotationType : aliasFor.annotation();
            this.aliasedAttributeName = getAliasedAttributeName(aliasFor, sourceAttribute);
            if (this.aliasedAnnotationType == this.sourceAnnotationType && this.aliasedAttributeName.equals(this.sourceAttributeName)) {
                String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] points to itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.", sourceAttribute.getName(), declaringClass.getName());
                throw new AnnotationConfigurationException(msg);
            }
            try {
                this.aliasedAttribute = this.aliasedAnnotationType.getDeclaredMethod(this.aliasedAttributeName, new Class[0]);
                this.isAliasPair = this.sourceAnnotationType == this.aliasedAnnotationType;
            } catch (NoSuchMethodException ex) {
                String msg2 = String.format("Attribute '%s' in annotation [%s] is declared as an @AliasFor nonexistent attribute '%s' in annotation [%s].", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg2, ex);
            }
        }

        private void validate() {
            if (!this.isAliasPair && !AnnotationUtils.isAnnotationMetaPresent(this.sourceAnnotationType, this.aliasedAnnotationType)) {
                String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] declares an alias for attribute '%s' in meta-annotation [%s] which is not meta-present.", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg);
            }
            if (this.isAliasPair) {
                AliasFor mirrorAliasFor = (AliasFor) this.aliasedAttribute.getAnnotation(AliasFor.class);
                if (mirrorAliasFor == null) {
                    String msg2 = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s].", this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName);
                    throw new AnnotationConfigurationException(msg2);
                }
                String mirrorAliasedAttributeName = getAliasedAttributeName(mirrorAliasFor, this.aliasedAttribute);
                if (!this.sourceAttributeName.equals(mirrorAliasedAttributeName)) {
                    String msg3 = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s], not [%s].", this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName, mirrorAliasedAttributeName);
                    throw new AnnotationConfigurationException(msg3);
                }
            }
            Class<?> returnType = this.sourceAttribute.getReturnType();
            Class<?> aliasedReturnType = this.aliasedAttribute.getReturnType();
            if (returnType != aliasedReturnType && (!aliasedReturnType.isArray() || returnType != aliasedReturnType.getComponentType())) {
                String msg4 = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare the same return type.", this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName, this.aliasedAnnotationType.getName());
                throw new AnnotationConfigurationException(msg4);
            } else if (this.isAliasPair) {
                validateDefaultValueConfiguration(this.aliasedAttribute);
            }
        }

        private void validateDefaultValueConfiguration(Method aliasedAttribute) {
            Object defaultValue = this.sourceAttribute.getDefaultValue();
            Object aliasedDefaultValue = aliasedAttribute.getDefaultValue();
            if (defaultValue == null || aliasedDefaultValue == null) {
                String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare default values.", this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(), aliasedAttribute.getDeclaringClass().getName());
                throw new AnnotationConfigurationException(msg);
            } else if (!ObjectUtils.nullSafeEquals(defaultValue, aliasedDefaultValue)) {
                String msg2 = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] and attribute '%s' in annotation [%s] must declare the same default value.", this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(), aliasedAttribute.getDeclaringClass().getName());
                throw new AnnotationConfigurationException(msg2);
            }
        }

        private void validateAgainst(AliasDescriptor otherDescriptor) {
            validateDefaultValueConfiguration(otherDescriptor.sourceAttribute);
        }

        private boolean isOverrideFor(Class<? extends Annotation> metaAnnotationType) {
            return this.aliasedAnnotationType == metaAnnotationType;
        }

        private boolean isAliasFor(AliasDescriptor otherDescriptor) {
            AliasDescriptor aliasDescriptor = this;
            while (true) {
                AliasDescriptor lhs = aliasDescriptor;
                if (lhs != null) {
                    AliasDescriptor aliasDescriptor2 = otherDescriptor;
                    while (true) {
                        AliasDescriptor rhs = aliasDescriptor2;
                        if (rhs != null) {
                            if (!lhs.aliasedAttribute.equals(rhs.aliasedAttribute)) {
                                aliasDescriptor2 = rhs.getAttributeOverrideDescriptor();
                            } else {
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
                aliasDescriptor = lhs.getAttributeOverrideDescriptor();
            }
        }

        public List<String> getAttributeAliasNames() {
            if (this.isAliasPair) {
                return Collections.singletonList(this.aliasedAttributeName);
            }
            List<String> aliases = new ArrayList<>();
            for (AliasDescriptor otherDescriptor : getOtherDescriptors()) {
                if (isAliasFor(otherDescriptor)) {
                    validateAgainst(otherDescriptor);
                    aliases.add(otherDescriptor.sourceAttributeName);
                }
            }
            return aliases;
        }

        private List<AliasDescriptor> getOtherDescriptors() {
            AliasDescriptor otherDescriptor;
            List<AliasDescriptor> otherDescriptors = new ArrayList<>();
            for (Method currentAttribute : AnnotationUtils.getAttributeMethods(this.sourceAnnotationType)) {
                if (!this.sourceAttribute.equals(currentAttribute) && (otherDescriptor = from(currentAttribute)) != null) {
                    otherDescriptors.add(otherDescriptor);
                }
            }
            return otherDescriptors;
        }

        @Nullable
        public String getAttributeOverrideName(Class<? extends Annotation> metaAnnotationType) {
            AliasDescriptor aliasDescriptor = this;
            while (true) {
                AliasDescriptor desc = aliasDescriptor;
                if (desc != null) {
                    if (!desc.isOverrideFor(metaAnnotationType)) {
                        aliasDescriptor = desc.getAttributeOverrideDescriptor();
                    } else {
                        return desc.aliasedAttributeName;
                    }
                } else {
                    return null;
                }
            }
        }

        @Nullable
        private AliasDescriptor getAttributeOverrideDescriptor() {
            if (this.isAliasPair) {
                return null;
            }
            return from(this.aliasedAttribute);
        }

        private String getAliasedAttributeName(AliasFor aliasFor, Method attribute) {
            String attributeName = aliasFor.attribute();
            String value = aliasFor.value();
            boolean attributeDeclared = StringUtils.hasText(attributeName);
            boolean valueDeclared = StringUtils.hasText(value);
            if (attributeDeclared && valueDeclared) {
                String msg = String.format("In @AliasFor declared on attribute '%s' in annotation [%s], attribute 'attribute' and its alias 'value' are present with values of [%s] and [%s], but only one is permitted.", attribute.getName(), attribute.getDeclaringClass().getName(), attributeName, value);
                throw new AnnotationConfigurationException(msg);
            }
            String attributeName2 = attributeDeclared ? attributeName : value;
            return StringUtils.hasText(attributeName2) ? attributeName2.trim() : attribute.getName();
        }

        public String toString() {
            return String.format("%s: @%s(%s) is an alias for @%s(%s)", getClass().getSimpleName(), this.sourceAnnotationType.getSimpleName(), this.sourceAttributeName, this.aliasedAnnotationType.getSimpleName(), this.aliasedAttributeName);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationUtils$DefaultValueHolder.class */
    public static class DefaultValueHolder {
        final Object defaultValue;

        public DefaultValueHolder(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}