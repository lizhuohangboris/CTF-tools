package com.fasterxml.classmate;

import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.members.HierarchicType;
import com.fasterxml.classmate.members.RawConstructor;
import com.fasterxml.classmate.members.RawField;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedConstructor;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.util.MethodKey;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/ResolvedTypeWithMembers.class */
public class ResolvedTypeWithMembers {
    private static final ResolvedType[] NO_RESOLVED_TYPES = new ResolvedType[0];
    private static final ResolvedMethod[] NO_RESOLVED_METHODS = new ResolvedMethod[0];
    private static final ResolvedField[] NO_RESOLVED_FIELDS = new ResolvedField[0];
    private static final ResolvedConstructor[] NO_RESOLVED_CONSTRUCTORS = new ResolvedConstructor[0];
    protected static final AnnotationConfiguration DEFAULT_ANNOTATION_CONFIG = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.DONT_INCLUDE);
    protected final TypeResolver _typeResolver;
    protected final AnnotationHandler _annotationHandler;
    protected final HierarchicType _mainType;
    protected final HierarchicType[] _types;
    protected Filter<RawField> _fieldFilter;
    protected Filter<RawConstructor> _constructorFilter;
    protected Filter<RawMethod> _methodFilter;
    protected ResolvedMethod[] _staticMethods = null;
    protected ResolvedField[] _staticFields = null;
    protected ResolvedMethod[] _memberMethods = null;
    protected ResolvedField[] _memberFields = null;
    protected ResolvedConstructor[] _constructors = null;

    public ResolvedTypeWithMembers(TypeResolver typeResolver, AnnotationConfiguration annotationConfig, HierarchicType mainType, HierarchicType[] types, Filter<RawConstructor> constructorFilter, Filter<RawField> fieldFilter, Filter<RawMethod> methodFilter) {
        this._typeResolver = typeResolver;
        this._mainType = mainType;
        this._types = types;
        this._annotationHandler = new AnnotationHandler(annotationConfig == null ? DEFAULT_ANNOTATION_CONFIG : annotationConfig);
        this._constructorFilter = constructorFilter;
        this._fieldFilter = fieldFilter;
        this._methodFilter = methodFilter;
    }

    public int size() {
        return this._types.length;
    }

    public List<HierarchicType> allTypesAndOverrides() {
        return Arrays.asList(this._types);
    }

    public List<HierarchicType> mainTypeAndOverrides() {
        List<HierarchicType> l = Arrays.asList(this._types);
        int end = this._mainType.getPriority() + 1;
        if (end < l.size()) {
            l = l.subList(0, end);
        }
        return l;
    }

    public List<HierarchicType> overridesOnly() {
        int index = this._mainType.getPriority();
        if (index == 0) {
            return Collections.emptyList();
        }
        List<HierarchicType> l = Arrays.asList(this._types);
        return l.subList(0, index);
    }

    public ResolvedField[] getStaticFields() {
        if (this._staticFields == null) {
            this._staticFields = resolveStaticFields();
        }
        return this._staticFields;
    }

    public ResolvedMethod[] getStaticMethods() {
        if (this._staticMethods == null) {
            this._staticMethods = resolveStaticMethods();
        }
        return this._staticMethods;
    }

    public ResolvedField[] getMemberFields() {
        if (this._memberFields == null) {
            this._memberFields = resolveMemberFields();
        }
        return this._memberFields;
    }

    public ResolvedMethod[] getMemberMethods() {
        if (this._memberMethods == null) {
            this._memberMethods = resolveMemberMethods();
        }
        return this._memberMethods;
    }

    public ResolvedConstructor[] getConstructors() {
        if (this._constructors == null) {
            this._constructors = resolveConstructors();
        }
        return this._constructors;
    }

    protected ResolvedConstructor[] resolveConstructors() {
        Annotation[] annotations;
        Annotation[] annotationArr;
        LinkedHashMap<MethodKey, ResolvedConstructor> constructors = new LinkedHashMap<>();
        for (RawConstructor constructor : this._mainType.getType().getConstructors()) {
            if (this._constructorFilter == null || this._constructorFilter.include(constructor)) {
                constructors.put(constructor.createKey(), resolveConstructor(constructor));
            }
        }
        for (HierarchicType type : overridesOnly()) {
            for (RawConstructor raw : type.getType().getConstructors()) {
                ResolvedConstructor constructor2 = constructors.get(raw.createKey());
                if (constructor2 != null) {
                    for (Annotation ann : raw.getAnnotations()) {
                        if (this._annotationHandler.includeMethodAnnotation(ann)) {
                            constructor2.applyOverride(ann);
                        }
                    }
                    Annotation[][] params = raw.getRawMember().getParameterAnnotations();
                    for (int i = 0; i < params.length; i++) {
                        for (Annotation annotation : params[i]) {
                            if (this._annotationHandler.includeParameterAnnotation(annotation)) {
                                constructor2.applyParamOverride(i, annotation);
                            }
                        }
                    }
                }
            }
        }
        if (constructors.size() == 0) {
            return NO_RESOLVED_CONSTRUCTORS;
        }
        return (ResolvedConstructor[]) constructors.values().toArray(new ResolvedConstructor[constructors.size()]);
    }

    protected ResolvedField[] resolveMemberFields() {
        Annotation[] annotations;
        LinkedHashMap<String, ResolvedField> fields = new LinkedHashMap<>();
        int typeIndex = this._types.length;
        while (true) {
            typeIndex--;
            if (typeIndex < 0) {
                break;
            }
            HierarchicType thisType = this._types[typeIndex];
            if (thisType.isMixin()) {
                for (RawField raw : thisType.getType().getMemberFields()) {
                    if (this._fieldFilter == null || this._fieldFilter.include(raw)) {
                        ResolvedField field = fields.get(raw.getName());
                        if (field != null) {
                            for (Annotation ann : raw.getAnnotations()) {
                                if (this._annotationHandler.includeMethodAnnotation(ann)) {
                                    field.applyOverride(ann);
                                }
                            }
                        }
                    }
                }
            } else {
                for (RawField field2 : thisType.getType().getMemberFields()) {
                    if (this._fieldFilter == null || this._fieldFilter.include(field2)) {
                        fields.put(field2.getName(), resolveField(field2));
                    }
                }
            }
        }
        if (fields.size() == 0) {
            return NO_RESOLVED_FIELDS;
        }
        return (ResolvedField[]) fields.values().toArray(new ResolvedField[fields.size()]);
    }

    protected ResolvedMethod[] resolveMemberMethods() {
        Annotation[] annotations;
        Annotation[] annotationArr;
        Annotation[] annotationArr2;
        Annotation[] annotationArr3;
        Annotation[] annotations2;
        Annotation[] annotationArr4;
        LinkedHashMap<MethodKey, ResolvedMethod> methods = new LinkedHashMap<>();
        LinkedHashMap<MethodKey, Annotations> overrides = new LinkedHashMap<>();
        LinkedHashMap<MethodKey, Annotations[]> paramOverrides = new LinkedHashMap<>();
        for (HierarchicType type : allTypesAndOverrides()) {
            for (RawMethod method : type.getType().getMemberMethods()) {
                if (this._methodFilter == null || this._methodFilter.include(method)) {
                    MethodKey key = method.createKey();
                    ResolvedMethod old = methods.get(key);
                    if (type.isMixin()) {
                        for (Annotation ann : method.getAnnotations()) {
                            if (old != null) {
                                if (methodCanInherit(ann)) {
                                    old.applyDefault(ann);
                                }
                            } else {
                                Annotations oldAnn = overrides.get(key);
                                if (oldAnn == null) {
                                    Annotations oldAnn2 = new Annotations();
                                    oldAnn2.add(ann);
                                    overrides.put(key, oldAnn2);
                                } else {
                                    oldAnn.addAsDefault(ann);
                                }
                            }
                        }
                        Annotation[][] argAnnotations = method.getRawMember().getParameterAnnotations();
                        if (old == null) {
                            Annotations[] oldParamAnns = paramOverrides.get(key);
                            if (oldParamAnns == null) {
                                Annotations[] oldParamAnns2 = new Annotations[argAnnotations.length];
                                for (int i = 0; i < argAnnotations.length; i++) {
                                    oldParamAnns2[i] = new Annotations();
                                    for (Annotation annotation : argAnnotations[i]) {
                                        if (parameterCanInherit(annotation)) {
                                            oldParamAnns2[i].add(annotation);
                                        }
                                    }
                                }
                                paramOverrides.put(key, oldParamAnns2);
                            } else {
                                for (int i2 = 0; i2 < argAnnotations.length; i2++) {
                                    for (Annotation annotation2 : argAnnotations[i2]) {
                                        if (parameterCanInherit(annotation2)) {
                                            oldParamAnns[i2].addAsDefault(annotation2);
                                        }
                                    }
                                }
                            }
                        } else {
                            for (int i3 = 0; i3 < argAnnotations.length; i3++) {
                                for (Annotation annotation3 : argAnnotations[i3]) {
                                    if (parameterCanInherit(annotation3)) {
                                        old.applyParamDefault(i3, annotation3);
                                    }
                                }
                            }
                        }
                    } else if (old == null) {
                        ResolvedMethod newMethod = resolveMethod(method);
                        methods.put(key, newMethod);
                        Annotations overrideAnn = overrides.get(key);
                        if (overrideAnn != null) {
                            newMethod.applyOverrides(overrideAnn);
                        }
                        Annotations[] annotations3 = paramOverrides.get(key);
                        if (annotations3 != null) {
                            for (int i4 = 0; i4 < annotations3.length; i4++) {
                                newMethod.applyParamOverrides(i4, annotations3[i4]);
                            }
                        }
                    } else {
                        for (Annotation ann2 : method.getAnnotations()) {
                            if (methodCanInherit(ann2)) {
                                old.applyDefault(ann2);
                            }
                        }
                        Annotation[][] parameterAnnotations = method.getRawMember().getParameterAnnotations();
                        for (int i5 = 0; i5 < parameterAnnotations.length; i5++) {
                            for (Annotation annotation4 : parameterAnnotations[i5]) {
                                if (parameterCanInherit(annotation4)) {
                                    old.applyParamDefault(i5, annotation4);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (methods.size() == 0) {
            return NO_RESOLVED_METHODS;
        }
        return (ResolvedMethod[]) methods.values().toArray(new ResolvedMethod[methods.size()]);
    }

    protected ResolvedField[] resolveStaticFields() {
        Annotation[] annotations;
        LinkedHashMap<String, ResolvedField> fields = new LinkedHashMap<>();
        for (RawField field : this._mainType.getType().getStaticFields()) {
            if (this._fieldFilter == null || this._fieldFilter.include(field)) {
                fields.put(field.getName(), resolveField(field));
            }
        }
        for (HierarchicType type : overridesOnly()) {
            for (RawField raw : type.getType().getStaticFields()) {
                ResolvedField field2 = fields.get(raw.getName());
                if (field2 != null) {
                    for (Annotation ann : raw.getAnnotations()) {
                        if (this._annotationHandler.includeFieldAnnotation(ann)) {
                            field2.applyOverride(ann);
                        }
                    }
                }
            }
        }
        if (fields.isEmpty()) {
            return NO_RESOLVED_FIELDS;
        }
        return (ResolvedField[]) fields.values().toArray(new ResolvedField[fields.size()]);
    }

    protected ResolvedMethod[] resolveStaticMethods() {
        Annotation[] annotations;
        LinkedHashMap<MethodKey, ResolvedMethod> methods = new LinkedHashMap<>();
        for (RawMethod method : this._mainType.getType().getStaticMethods()) {
            if (this._methodFilter == null || this._methodFilter.include(method)) {
                methods.put(method.createKey(), resolveMethod(method));
            }
        }
        for (HierarchicType type : overridesOnly()) {
            for (RawMethod raw : type.getType().getStaticMethods()) {
                ResolvedMethod method2 = methods.get(raw.createKey());
                if (method2 != null) {
                    for (Annotation ann : raw.getAnnotations()) {
                        if (this._annotationHandler.includeMethodAnnotation(ann)) {
                            method2.applyOverride(ann);
                        }
                    }
                }
            }
        }
        if (methods.size() == 0) {
            return NO_RESOLVED_METHODS;
        }
        return (ResolvedMethod[]) methods.values().toArray(new ResolvedMethod[methods.size()]);
    }

    protected ResolvedConstructor resolveConstructor(RawConstructor raw) {
        ResolvedType[] argTypes;
        Annotation[] annotations;
        ResolvedType context = raw.getDeclaringType();
        TypeBindings bindings = context.getTypeBindings();
        Constructor<?> ctor = raw.getRawMember();
        Type[] rawTypes = ctor.getGenericParameterTypes();
        if (rawTypes == null || rawTypes.length == 0) {
            argTypes = NO_RESOLVED_TYPES;
        } else {
            argTypes = new ResolvedType[rawTypes.length];
            int len = rawTypes.length;
            for (int i = 0; i < len; i++) {
                argTypes[i] = this._typeResolver.resolve(bindings, rawTypes[i]);
            }
        }
        Annotations anns = new Annotations();
        for (Annotation ann : ctor.getAnnotations()) {
            if (this._annotationHandler.includeConstructorAnnotation(ann)) {
                anns.add(ann);
            }
        }
        ResolvedConstructor constructor = new ResolvedConstructor(context, anns, ctor, argTypes);
        Annotation[][] annotations2 = ctor.getParameterAnnotations();
        for (int i2 = 0; i2 < argTypes.length; i2++) {
            for (Annotation ann2 : annotations2[i2]) {
                constructor.applyParamOverride(i2, ann2);
            }
        }
        return constructor;
    }

    protected ResolvedField resolveField(RawField raw) {
        Annotation[] annotations;
        ResolvedType context = raw.getDeclaringType();
        Field field = raw.getRawMember();
        ResolvedType type = this._typeResolver.resolve(context.getTypeBindings(), field.getGenericType());
        Annotations anns = new Annotations();
        for (Annotation ann : field.getAnnotations()) {
            if (this._annotationHandler.includeFieldAnnotation(ann)) {
                anns.add(ann);
            }
        }
        return new ResolvedField(context, anns, field, type);
    }

    protected ResolvedMethod resolveMethod(RawMethod raw) {
        ResolvedType[] argTypes;
        Annotation[] annotations;
        ResolvedType context = raw.getDeclaringType();
        TypeBindings bindings = context.getTypeBindings();
        Method m = raw.getRawMember();
        Type rawType = m.getGenericReturnType();
        ResolvedType rt = rawType == Void.TYPE ? null : this._typeResolver.resolve(bindings, rawType);
        Type[] rawTypes = m.getGenericParameterTypes();
        if (rawTypes == null || rawTypes.length == 0) {
            argTypes = NO_RESOLVED_TYPES;
        } else {
            argTypes = new ResolvedType[rawTypes.length];
            int len = rawTypes.length;
            for (int i = 0; i < len; i++) {
                argTypes[i] = this._typeResolver.resolve(bindings, rawTypes[i]);
            }
        }
        Annotations anns = new Annotations();
        for (Annotation ann : m.getAnnotations()) {
            if (this._annotationHandler.includeMethodAnnotation(ann)) {
                anns.add(ann);
            }
        }
        ResolvedMethod method = new ResolvedMethod(context, anns, m, rt, argTypes);
        Annotation[][] annotations2 = m.getParameterAnnotations();
        for (int i2 = 0; i2 < argTypes.length; i2++) {
            for (Annotation ann2 : annotations2[i2]) {
                method.applyParamOverride(i2, ann2);
            }
        }
        return method;
    }

    protected boolean methodCanInherit(Annotation annotation) {
        AnnotationInclusion annotationInclusion = this._annotationHandler.methodInclusion(annotation);
        if (annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED) {
            return annotation.annotationType().isAnnotationPresent(Inherited.class);
        }
        return annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT;
    }

    protected boolean parameterCanInherit(Annotation annotation) {
        AnnotationInclusion annotationInclusion = this._annotationHandler.parameterInclusion(annotation);
        if (annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT_IF_INHERITED) {
            return annotation.annotationType().isAnnotationPresent(Inherited.class);
        }
        return annotationInclusion == AnnotationInclusion.INCLUDE_AND_INHERIT;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/classmate-1.4.0.jar:com/fasterxml/classmate/ResolvedTypeWithMembers$AnnotationHandler.class */
    public static final class AnnotationHandler {
        private final AnnotationConfiguration _annotationConfig;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _fieldInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _constructorInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _methodInclusions;
        private HashMap<Class<? extends Annotation>, AnnotationInclusion> _parameterInclusions;

        public AnnotationHandler(AnnotationConfiguration annotationConfig) {
            this._annotationConfig = annotationConfig;
        }

        public boolean includeConstructorAnnotation(Annotation ann) {
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._constructorInclusions == null) {
                this._constructorInclusions = new HashMap<>();
            } else {
                AnnotationInclusion incl = this._constructorInclusions.get(annType);
                if (incl != null) {
                    return incl != AnnotationInclusion.DONT_INCLUDE;
                }
            }
            AnnotationInclusion incl2 = this._annotationConfig.getInclusionForConstructor(annType);
            this._constructorInclusions.put(annType, incl2);
            return incl2 != AnnotationInclusion.DONT_INCLUDE;
        }

        public boolean includeFieldAnnotation(Annotation ann) {
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._fieldInclusions == null) {
                this._fieldInclusions = new HashMap<>();
            } else {
                AnnotationInclusion incl = this._fieldInclusions.get(annType);
                if (incl != null) {
                    return incl != AnnotationInclusion.DONT_INCLUDE;
                }
            }
            AnnotationInclusion incl2 = this._annotationConfig.getInclusionForField(annType);
            this._fieldInclusions.put(annType, incl2);
            return incl2 != AnnotationInclusion.DONT_INCLUDE;
        }

        public boolean includeMethodAnnotation(Annotation ann) {
            return methodInclusion(ann) != AnnotationInclusion.DONT_INCLUDE;
        }

        public AnnotationInclusion methodInclusion(Annotation ann) {
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._methodInclusions == null) {
                this._methodInclusions = new HashMap<>();
            } else {
                AnnotationInclusion incl = this._methodInclusions.get(annType);
                if (incl != null) {
                    return incl;
                }
            }
            AnnotationInclusion incl2 = this._annotationConfig.getInclusionForMethod(annType);
            this._methodInclusions.put(annType, incl2);
            return incl2;
        }

        public boolean includeParameterAnnotation(Annotation ann) {
            return parameterInclusion(ann) != AnnotationInclusion.DONT_INCLUDE;
        }

        public AnnotationInclusion parameterInclusion(Annotation ann) {
            Class<? extends Annotation> annType = ann.annotationType();
            if (this._parameterInclusions == null) {
                this._parameterInclusions = new HashMap<>();
            } else {
                AnnotationInclusion incl = this._parameterInclusions.get(annType);
                if (incl != null) {
                    return incl;
                }
            }
            AnnotationInclusion incl2 = this._annotationConfig.getInclusionForParameter(annType);
            this._parameterInclusions.put(annType, incl2);
            return incl2;
        }
    }
}