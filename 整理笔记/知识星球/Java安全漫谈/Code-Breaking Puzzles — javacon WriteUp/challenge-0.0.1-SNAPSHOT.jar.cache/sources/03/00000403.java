package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/AnnotatedClassResolver.class */
public class AnnotatedClassResolver {
    private static final Annotations NO_ANNOTATIONS = AnnotationCollector.emptyAnnotations();
    private final MapperConfig<?> _config;
    private final AnnotationIntrospector _intr;
    private final ClassIntrospector.MixInResolver _mixInResolver;
    private final TypeBindings _bindings;
    private final JavaType _type;
    private final Class<?> _class;
    private final Class<?> _primaryMixin;

    AnnotatedClassResolver(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        this._config = config;
        this._type = type;
        this._class = type.getRawClass();
        this._mixInResolver = r;
        this._bindings = type.getBindings();
        this._intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
        this._primaryMixin = this._config.findMixInClassFor(this._class);
    }

    AnnotatedClassResolver(MapperConfig<?> config, Class<?> cls, ClassIntrospector.MixInResolver r) {
        this._config = config;
        this._type = null;
        this._class = cls;
        this._mixInResolver = r;
        this._bindings = TypeBindings.emptyBindings();
        if (config == null) {
            this._intr = null;
            this._primaryMixin = null;
            return;
        }
        this._intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
        this._primaryMixin = this._config.findMixInClassFor(this._class);
    }

    public static AnnotatedClass resolve(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r) {
        if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
            return createArrayType(config, forType.getRawClass());
        }
        return new AnnotatedClassResolver(config, forType, r).resolveFully();
    }

    public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType) {
        return resolveWithoutSuperTypes(config, forType, config);
    }

    public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, JavaType forType, ClassIntrospector.MixInResolver r) {
        if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
            return createArrayType(config, forType.getRawClass());
        }
        return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
    }

    public static AnnotatedClass resolveWithoutSuperTypes(MapperConfig<?> config, Class<?> forType, ClassIntrospector.MixInResolver r) {
        if (forType.isArray() && skippableArray(config, forType)) {
            return createArrayType(config, forType);
        }
        return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
    }

    private static boolean skippableArray(MapperConfig<?> config, Class<?> type) {
        return config == null || config.findMixInClassFor(type) == null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AnnotatedClass createPrimordial(Class<?> raw) {
        return new AnnotatedClass(raw);
    }

    static AnnotatedClass createArrayType(MapperConfig<?> config, Class<?> raw) {
        return new AnnotatedClass(raw);
    }

    AnnotatedClass resolveFully() {
        List<JavaType> superTypes = ClassUtil.findSuperTypes(this._type, (Class<?>) null, false);
        return new AnnotatedClass(this._type, this._class, superTypes, this._primaryMixin, resolveClassAnnotations(superTypes), this._bindings, this._intr, this._mixInResolver, this._config.getTypeFactory());
    }

    AnnotatedClass resolveWithoutSuperTypes() {
        List<JavaType> superTypes = Collections.emptyList();
        return new AnnotatedClass(null, this._class, superTypes, this._primaryMixin, resolveClassAnnotations(superTypes), this._bindings, this._intr, this._config, this._config.getTypeFactory());
    }

    private Annotations resolveClassAnnotations(List<JavaType> superTypes) {
        if (this._intr == null) {
            return NO_ANNOTATIONS;
        }
        AnnotationCollector resolvedCA = AnnotationCollector.emptyCollector();
        if (this._primaryMixin != null) {
            resolvedCA = _addClassMixIns(resolvedCA, this._class, this._primaryMixin);
        }
        AnnotationCollector resolvedCA2 = _addAnnotationsIfNotPresent(resolvedCA, ClassUtil.findClassAnnotations(this._class));
        for (JavaType type : superTypes) {
            if (this._mixInResolver != null) {
                Class<?> cls = type.getRawClass();
                resolvedCA2 = _addClassMixIns(resolvedCA2, cls, this._mixInResolver.findMixInClassFor(cls));
            }
            resolvedCA2 = _addAnnotationsIfNotPresent(resolvedCA2, ClassUtil.findClassAnnotations(type.getRawClass()));
        }
        if (this._mixInResolver != null) {
            resolvedCA2 = _addClassMixIns(resolvedCA2, Object.class, this._mixInResolver.findMixInClassFor(Object.class));
        }
        return resolvedCA2.asAnnotations();
    }

    private AnnotationCollector _addClassMixIns(AnnotationCollector annotations, Class<?> target, Class<?> mixin) {
        if (mixin != null) {
            annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
            for (Class<?> parent : ClassUtil.findSuperClasses(mixin, target, false)) {
                annotations = _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
            }
        }
        return annotations;
    }

    private AnnotationCollector _addAnnotationsIfNotPresent(AnnotationCollector c, Annotation[] anns) {
        if (anns != null) {
            for (Annotation ann : anns) {
                if (!c.isPresent(ann)) {
                    c = c.addOrOverride(ann);
                    if (this._intr.isAnnotationBundle(ann)) {
                        c = _addFromBundleIfNotPresent(c, ann);
                    }
                }
            }
        }
        return c;
    }

    private AnnotationCollector _addFromBundleIfNotPresent(AnnotationCollector c, Annotation bundle) {
        Annotation[] arr$ = ClassUtil.findClassAnnotations(bundle.annotationType());
        for (Annotation ann : arr$) {
            if (!(ann instanceof Target) && !(ann instanceof Retention) && !c.isPresent(ann)) {
                c = c.addOrOverride(ann);
                if (this._intr.isAnnotationBundle(ann)) {
                    c = _addFromBundleIfNotPresent(c, ann);
                }
            }
        }
        return c;
    }
}