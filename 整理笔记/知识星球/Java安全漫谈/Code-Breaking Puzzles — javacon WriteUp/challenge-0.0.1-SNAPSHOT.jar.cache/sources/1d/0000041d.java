package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/BasicClassIntrospector.class */
public class BasicClassIntrospector extends ClassIntrospector implements Serializable {
    private static final long serialVersionUID = 1;
    protected static final BasicBeanDescription STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(String.class));
    protected static final BasicBeanDescription BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
    protected static final BasicBeanDescription INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
    protected static final BasicBeanDescription LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
    protected final LRUMap<JavaType, BasicBeanDescription> _cachedFCA = new LRUMap<>(16, 64);

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public /* bridge */ /* synthetic */ BeanDescription forDirectClassAnnotations(MapperConfig x0, JavaType x1, ClassIntrospector.MixInResolver x2) {
        return forDirectClassAnnotations((MapperConfig<?>) x0, x1, x2);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public /* bridge */ /* synthetic */ BeanDescription forClassAnnotations(MapperConfig x0, JavaType x1, ClassIntrospector.MixInResolver x2) {
        return forClassAnnotations((MapperConfig<?>) x0, x1, x2);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public ClassIntrospector copy() {
        return new BasicClassIntrospector();
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forSerialization(SerializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forSerialization(collectProperties(cfg, type, r, true, "set"));
            }
            this._cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDeserialization(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(collectProperties(cfg, type, r, false, "set"));
            }
            this._cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = BasicBeanDescription.forDeserialization(collectPropertiesWithBuilder(cfg, type, r, false));
        this._cachedFCA.putIfAbsent(type, desc);
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forCreation(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = _findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(collectProperties(cfg, type, r, false, "set"));
            }
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = this._cachedFCA.get(type);
            if (desc == null) {
                desc = BasicBeanDescription.forOtherUse(config, type, _resolveAnnotatedClass(config, type, r));
                this._cachedFCA.put(type, desc);
            }
        }
        return desc;
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector
    public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, _resolveAnnotatedWithoutSuperTypes(config, type, r));
        }
        return desc;
    }

    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization, String mutatorPrefix) {
        return constructPropertyCollector(config, _resolveAnnotatedClass(config, type, r), type, forSerialization, mutatorPrefix);
    }

    protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
        AnnotatedClass ac = _resolveAnnotatedClass(config, type, r);
        AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
        JsonPOJOBuilder.Value builderConfig = ai == null ? null : ai.findPOJOBuilderConfig(ac);
        String mutatorPrefix = builderConfig == null ? "with" : builderConfig.withPrefix;
        return constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
    }

    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization, String mutatorPrefix) {
        return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
    }

    protected BasicBeanDescription _findStdTypeDesc(JavaType type) {
        Class<?> cls = type.getRawClass();
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                return BOOLEAN_DESC;
            }
            if (cls == Integer.TYPE) {
                return INT_DESC;
            }
            if (cls == Long.TYPE) {
                return LONG_DESC;
            }
            return null;
        } else if (cls == String.class) {
            return STRING_DESC;
        } else {
            return null;
        }
    }

    protected boolean _isStdJDKCollection(JavaType type) {
        Class<?> raw;
        String pkgName;
        if (type.isContainerType() && !type.isArrayType() && (pkgName = ClassUtil.getPackageName((raw = type.getRawClass()))) != null) {
            if (pkgName.startsWith("java.lang") || pkgName.startsWith("java.util")) {
                if (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    protected BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type) {
        if (_isStdJDKCollection(type)) {
            return BasicBeanDescription.forOtherUse(cfg, type, _resolveAnnotatedClass(cfg, type, cfg));
        }
        return null;
    }

    protected AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        return AnnotatedClassResolver.resolve(config, type, r);
    }

    protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
    }
}