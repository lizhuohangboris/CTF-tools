package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/AnnotatedCreatorCollector.class */
public final class AnnotatedCreatorCollector extends CollectorBase {
    private final TypeResolutionContext _typeContext;
    private AnnotatedConstructor _defaultConstructor;

    AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc) {
        super(intr);
        this._typeContext = tc;
    }

    public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn) {
        return new AnnotatedCreatorCollector(intr, tc).collect(type, primaryMixIn);
    }

    AnnotatedClass.Creators collect(JavaType type, Class<?> primaryMixIn) {
        List<AnnotatedConstructor> constructors = _findPotentialConstructors(type, primaryMixIn);
        List<AnnotatedMethod> factories = _findPotentialFactories(type, primaryMixIn);
        if (this._intr != null) {
            if (this._defaultConstructor != null && this._intr.hasIgnoreMarker(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            int i = constructors.size();
            while (true) {
                i--;
                if (i < 0) {
                    break;
                } else if (this._intr.hasIgnoreMarker(constructors.get(i))) {
                    constructors.remove(i);
                }
            }
            int i2 = factories.size();
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                } else if (this._intr.hasIgnoreMarker(factories.get(i2))) {
                    factories.remove(i2);
                }
            }
        }
        return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn) {
        int ctorCount;
        List<AnnotatedConstructor> result;
        ClassUtil.Ctor defaultCtor = null;
        List<ClassUtil.Ctor> ctors = null;
        if (!type.isEnumType()) {
            ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());
            for (ClassUtil.Ctor ctor : declaredCtors) {
                if (isIncludableConstructor(ctor.getConstructor())) {
                    if (ctor.getParamCount() == 0) {
                        defaultCtor = ctor;
                    } else {
                        if (ctors == null) {
                            ctors = new ArrayList<>();
                        }
                        ctors.add(ctor);
                    }
                }
            }
        }
        if (ctors == null) {
            result = Collections.emptyList();
            if (defaultCtor == null) {
                return result;
            }
            ctorCount = 0;
        } else {
            ctorCount = ctors.size();
            result = new ArrayList<>(ctorCount);
            for (int i = 0; i < ctorCount; i++) {
                result.add(null);
            }
        }
        if (primaryMixIn != null) {
            MemberKey[] ctorKeys = null;
            ClassUtil.Ctor[] arr$ = ClassUtil.getConstructors(primaryMixIn);
            for (ClassUtil.Ctor mixinCtor : arr$) {
                if (mixinCtor.getParamCount() == 0) {
                    if (defaultCtor != null) {
                        this._defaultConstructor = constructDefaultConstructor(defaultCtor, mixinCtor);
                        defaultCtor = null;
                    }
                } else if (ctors != null) {
                    if (ctorKeys == null) {
                        ctorKeys = new MemberKey[ctorCount];
                        for (int i2 = 0; i2 < ctorCount; i2++) {
                            ctorKeys[i2] = new MemberKey(ctors.get(i2).getConstructor());
                        }
                    }
                    MemberKey key = new MemberKey(mixinCtor.getConstructor());
                    int i3 = 0;
                    while (true) {
                        if (i3 < ctorCount) {
                            if (!key.equals(ctorKeys[i3])) {
                                i3++;
                            } else {
                                result.set(i3, constructNonDefaultConstructor(ctors.get(i3), mixinCtor));
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (defaultCtor != null) {
            this._defaultConstructor = constructDefaultConstructor(defaultCtor, null);
        }
        for (int i4 = 0; i4 < ctorCount; i4++) {
            if (result.get(i4) == null) {
                result.set(i4, constructNonDefaultConstructor(ctors.get(i4), null));
            }
        }
        return result;
    }

    private List<AnnotatedMethod> _findPotentialFactories(JavaType type, Class<?> primaryMixIn) {
        List<Method> candidates = null;
        Method[] arr$ = ClassUtil.getClassMethods(type.getRawClass());
        for (Method m : arr$) {
            if (Modifier.isStatic(m.getModifiers())) {
                if (candidates == null) {
                    candidates = new ArrayList<>();
                }
                candidates.add(m);
            }
        }
        if (candidates == null) {
            return Collections.emptyList();
        }
        int factoryCount = candidates.size();
        List<AnnotatedMethod> result = new ArrayList<>(factoryCount);
        for (int i = 0; i < factoryCount; i++) {
            result.add(null);
        }
        if (primaryMixIn != null) {
            MemberKey[] methodKeys = null;
            Method[] arr$2 = ClassUtil.getDeclaredMethods(primaryMixIn);
            for (Method mixinFactory : arr$2) {
                if (Modifier.isStatic(mixinFactory.getModifiers())) {
                    if (methodKeys == null) {
                        methodKeys = new MemberKey[factoryCount];
                        for (int i2 = 0; i2 < factoryCount; i2++) {
                            methodKeys[i2] = new MemberKey(candidates.get(i2));
                        }
                    }
                    MemberKey key = new MemberKey(mixinFactory);
                    int i3 = 0;
                    while (true) {
                        if (i3 < factoryCount) {
                            if (!key.equals(methodKeys[i3])) {
                                i3++;
                            } else {
                                result.set(i3, constructFactoryCreator(candidates.get(i3), mixinFactory));
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        for (int i4 = 0; i4 < factoryCount; i4++) {
            AnnotatedMethod factory = result.get(i4);
            if (factory == null) {
                result.set(i4, constructFactoryCreator(candidates.get(i4), null));
            }
        }
        return result;
    }

    protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
        if (this._intr == null) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), NO_ANNOTATION_MAPS);
        }
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), collectAnnotations(ctor.getConstructor().getParameterAnnotations(), mixin == null ? null : mixin.getConstructor().getParameterAnnotations()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.annotation.Annotation[]] */
    /* JADX WARN: Type inference failed for: r0v35, types: [java.lang.annotation.Annotation[]] */
    protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
        AnnotationMap[] resolvedAnnotations;
        int paramCount = ctor.getParamCount();
        if (this._intr == null) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
        }
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            if (dc.isEnum() && paramCount == paramAnns.length + 2) {
                paramAnns = new Annotation[paramAnns.length + 2];
                System.arraycopy(paramAnns, 0, paramAnns, 2, paramAnns.length);
                resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][]) null);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                paramAnns = new Annotation[paramAnns.length + 1];
                System.arraycopy(paramAnns, 0, paramAnns, 1, paramAnns.length);
                paramAnns[0] = NO_ANNOTATIONS;
                resolvedAnnotations = collectAnnotations(paramAnns, (Annotation[][]) null);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", ctor.getDeclaringClass().getName(), Integer.valueOf(paramCount), Integer.valueOf(paramAnns.length)));
            }
        } else {
            resolvedAnnotations = collectAnnotations(paramAnns, mixin == null ? null : mixin.getParameterAnnotations());
        }
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), collectAnnotations(ctor, mixin), resolvedAnnotations);
    }

    protected AnnotatedMethod constructFactoryCreator(Method m, Method mixin) {
        int paramCount = m.getParameterTypes().length;
        if (this._intr == null) {
            return new AnnotatedMethod(this._typeContext, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedMethod(this._typeContext, m, collectAnnotations(m, mixin), NO_ANNOTATION_MAPS);
        }
        return new AnnotatedMethod(this._typeContext, m, collectAnnotations(m, mixin), collectAnnotations(m.getParameterAnnotations(), mixin == null ? null : mixin.getParameterAnnotations()));
    }

    private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
        int count = mainAnns.length;
        AnnotationMap[] result = new AnnotationMap[count];
        for (int i = 0; i < count; i++) {
            AnnotationCollector c = collectAnnotations(AnnotationCollector.emptyCollector(), mainAnns[i]);
            if (mixinAnns != null) {
                c = collectAnnotations(c, mixinAnns[i]);
            }
            result[i] = c.asAnnotationMap();
        }
        return result;
    }

    private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin) {
        AnnotationCollector c = collectAnnotations(main.getConstructor().getDeclaredAnnotations());
        if (mixin != null) {
            c = collectAnnotations(c, mixin.getConstructor().getDeclaredAnnotations());
        }
        return c.asAnnotationMap();
    }

    private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
        AnnotationCollector c = collectAnnotations(main.getDeclaredAnnotations());
        if (mixin != null) {
            c = collectAnnotations(c, mixin.getDeclaredAnnotations());
        }
        return c.asAnnotationMap();
    }

    private static boolean isIncludableConstructor(Constructor<?> c) {
        return !c.isSynthetic();
    }
}