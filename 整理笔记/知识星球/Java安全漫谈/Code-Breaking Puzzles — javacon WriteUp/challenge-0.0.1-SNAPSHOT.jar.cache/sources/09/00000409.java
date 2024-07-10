package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/AnnotatedFieldCollector.class */
public class AnnotatedFieldCollector extends CollectorBase {
    private final TypeFactory _typeFactory;
    private final ClassIntrospector.MixInResolver _mixInResolver;

    AnnotatedFieldCollector(AnnotationIntrospector intr, TypeFactory types, ClassIntrospector.MixInResolver mixins) {
        super(intr);
        this._typeFactory = types;
        this._mixInResolver = intr == null ? null : mixins;
    }

    public static List<AnnotatedField> collectFields(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type) {
        return new AnnotatedFieldCollector(intr, types, mixins).collect(tc, type);
    }

    List<AnnotatedField> collect(TypeResolutionContext tc, JavaType type) {
        Map<String, FieldBuilder> foundFields = _findFields(tc, type, null);
        if (foundFields == null) {
            return Collections.emptyList();
        }
        List<AnnotatedField> result = new ArrayList<>(foundFields.size());
        for (FieldBuilder b : foundFields.values()) {
            result.add(b.build());
        }
        return result;
    }

    private Map<String, FieldBuilder> _findFields(TypeResolutionContext tc, JavaType type, Map<String, FieldBuilder> fields) {
        Class<?> mixin;
        JavaType parent = type.getSuperClass();
        if (parent == null) {
            return fields;
        }
        Class<?> cls = type.getRawClass();
        Map<String, FieldBuilder> fields2 = _findFields(new TypeResolutionContext.Basic(this._typeFactory, parent.getBindings()), parent, fields);
        Field[] arr$ = ClassUtil.getDeclaredFields(cls);
        for (Field f : arr$) {
            if (_isIncludableField(f)) {
                if (fields2 == null) {
                    fields2 = new LinkedHashMap<>();
                }
                FieldBuilder b = new FieldBuilder(tc, f);
                if (this._intr != null) {
                    b.annotations = collectAnnotations(b.annotations, f.getDeclaredAnnotations());
                }
                fields2.put(f.getName(), b);
            }
        }
        if (this._mixInResolver != null && (mixin = this._mixInResolver.findMixInClassFor(cls)) != null) {
            _addFieldMixIns(mixin, cls, fields2);
        }
        return fields2;
    }

    private void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, FieldBuilder> fields) {
        List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
        for (Class<?> mixin : parents) {
            Field[] arr$ = ClassUtil.getDeclaredFields(mixin);
            for (Field mixinField : arr$) {
                if (_isIncludableField(mixinField)) {
                    String name = mixinField.getName();
                    FieldBuilder b = fields.get(name);
                    if (b != null) {
                        b.annotations = collectAnnotations(b.annotations, mixinField.getDeclaredAnnotations());
                    }
                }
            }
        }
    }

    private boolean _isIncludableField(Field f) {
        if (f.isSynthetic()) {
            return false;
        }
        int mods = f.getModifiers();
        if (Modifier.isStatic(mods)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/AnnotatedFieldCollector$FieldBuilder.class */
    public static final class FieldBuilder {
        public final TypeResolutionContext typeContext;
        public final Field field;
        public AnnotationCollector annotations = AnnotationCollector.emptyCollector();

        public FieldBuilder(TypeResolutionContext tc, Field f) {
            this.typeContext = tc;
            this.field = f;
        }

        public AnnotatedField build() {
            return new AnnotatedField(this.typeContext, this.field, this.annotations.asAnnotationMap());
        }
    }
}