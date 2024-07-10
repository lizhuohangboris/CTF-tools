package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.ConfigFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/cfg/MapperConfigBase.class */
public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable {
    protected static final ConfigOverride EMPTY_OVERRIDE = ConfigOverride.empty();
    private static final int DEFAULT_MAPPER_FEATURES = collectFeatureDefaults(MapperFeature.class);
    private static final int AUTO_DETECT_MASK = (((MapperFeature.AUTO_DETECT_FIELDS.getMask() | MapperFeature.AUTO_DETECT_GETTERS.getMask()) | MapperFeature.AUTO_DETECT_IS_GETTERS.getMask()) | MapperFeature.AUTO_DETECT_SETTERS.getMask()) | MapperFeature.AUTO_DETECT_CREATORS.getMask();
    protected final SimpleMixInResolver _mixIns;
    protected final SubtypeResolver _subtypeResolver;
    protected final PropertyName _rootName;
    protected final Class<?> _view;
    protected final ContextAttributes _attributes;
    protected final RootNameLookup _rootNames;
    protected final ConfigOverrides _configOverrides;

    protected abstract T _withBase(BaseSettings baseSettings);

    protected abstract T _withMapperFeatures(int i);

    public abstract T with(ContextAttributes contextAttributes);

    public abstract T withRootName(PropertyName propertyName);

    public abstract T with(SubtypeResolver subtypeResolver);

    public abstract T withView(Class<?> cls);

    public MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(base, DEFAULT_MAPPER_FEATURES);
        this._mixIns = mixins;
        this._subtypeResolver = str;
        this._rootNames = rootNames;
        this._rootName = null;
        this._view = null;
        this._attributes = ContextAttributes.getEmpty();
        this._configOverrides = configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(src, src._base.copy());
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base) {
        super(src, base);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, int mapperFeatures) {
        super(src, mapperFeatures);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = str;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins) {
        super(src);
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    public MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = attr;
        this._configOverrides = src._configOverrides;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final T with(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (MapperFeature f : features) {
            newMapperFlags |= f.getMask();
        }
        if (newMapperFlags == this._mapperFeatures) {
            return this;
        }
        return _withMapperFeatures(newMapperFlags);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final T without(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (MapperFeature f : features) {
            newMapperFlags &= f.getMask() ^ (-1);
        }
        if (newMapperFlags == this._mapperFeatures) {
            return this;
        }
        return _withMapperFeatures(newMapperFlags);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final T with(MapperFeature feature, boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = this._mapperFeatures | feature.getMask();
        } else {
            newMapperFlags = this._mapperFeatures & (feature.getMask() ^ (-1));
        }
        if (newMapperFlags == this._mapperFeatures) {
            return this;
        }
        return _withMapperFeatures(newMapperFlags);
    }

    public final T with(AnnotationIntrospector ai) {
        return _withBase(this._base.withAnnotationIntrospector(ai));
    }

    public final T withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }

    public final T withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }

    public final T with(ClassIntrospector ci) {
        return _withBase(this._base.withClassIntrospector(ci));
    }

    public T withAttributes(Map<?, ?> attributes) {
        return with(getAttributes().withSharedAttributes(attributes));
    }

    public T withAttribute(Object key, Object value) {
        return with(getAttributes().withSharedAttribute(key, value));
    }

    public T withoutAttribute(Object key) {
        return with(getAttributes().withoutSharedAttribute(key));
    }

    public final T with(TypeFactory tf) {
        return _withBase(this._base.withTypeFactory(tf));
    }

    public final T with(TypeResolverBuilder<?> trb) {
        return _withBase(this._base.withTypeResolverBuilder(trb));
    }

    public final T with(PropertyNamingStrategy pns) {
        return _withBase(this._base.withPropertyNamingStrategy(pns));
    }

    public final T with(HandlerInstantiator hi) {
        return _withBase(this._base.withHandlerInstantiator(hi));
    }

    public final T with(Base64Variant base64) {
        return _withBase(this._base.with(base64));
    }

    public T with(DateFormat df) {
        return _withBase(this._base.withDateFormat(df));
    }

    public final T with(Locale l) {
        return _withBase(this._base.with(l));
    }

    public final T with(TimeZone tz) {
        return _withBase(this._base.with(tz));
    }

    public T withRootName(String rootName) {
        if (rootName == null) {
            return withRootName((PropertyName) null);
        }
        return withRootName(PropertyName.construct(rootName));
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }

    @Deprecated
    public final String getRootName() {
        if (this._rootName == null) {
            return null;
        }
        return this._rootName.getSimpleName();
    }

    public final PropertyName getFullRootName() {
        return this._rootName;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final Class<?> getActiveView() {
        return this._view;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final ContextAttributes getAttributes() {
        return this._attributes;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final ConfigOverride getConfigOverride(Class<?> type) {
        ConfigOverride override = this._configOverrides.findOverride(type);
        return override == null ? EMPTY_OVERRIDE : override;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final ConfigOverride findConfigOverride(Class<?> type) {
        return this._configOverrides.findOverride(type);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonInclude.Value getDefaultPropertyInclusion() {
        return this._configOverrides.getDefaultInclusion();
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
        JsonInclude.Value v = getConfigOverride(baseType).getInclude();
        JsonInclude.Value def = getDefaultPropertyInclusion();
        if (def == null) {
            return v;
        }
        return def.withOverrides(v);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonInclude.Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType) {
        JsonInclude.Value v = getConfigOverride(propertyType).getIncludeAsProperty();
        JsonInclude.Value def = getDefaultPropertyInclusion(baseType);
        if (def == null) {
            return v;
        }
        return def.withOverrides(v);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonFormat.Value getDefaultPropertyFormat(Class<?> type) {
        JsonFormat.Value v;
        ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null && (v = overrides.getFormat()) != null) {
            return v;
        }
        return EMPTY_FORMAT;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type) {
        JsonIgnoreProperties.Value v;
        ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null && (v = overrides.getIgnorals()) != null) {
            return v;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass) {
        AnnotationIntrospector intr = getAnnotationIntrospector();
        JsonIgnoreProperties.Value base = intr == null ? null : intr.findPropertyIgnorals(actualClass);
        JsonIgnoreProperties.Value overrides = getDefaultPropertyIgnorals(baseType);
        return JsonIgnoreProperties.Value.merge(base, overrides);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = this._configOverrides.getDefaultVisibility();
        if ((this._mapperFeatures & AUTO_DETECT_MASK) != AUTO_DETECT_MASK) {
            if (!isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
                vchecker = vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
                vchecker = vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
                vchecker = vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
                vchecker = vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
                vchecker = vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
            }
        }
        return vchecker;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> baseType, AnnotatedClass actualClass) {
        VisibilityChecker<?> vc = getDefaultVisibilityChecker();
        AnnotationIntrospector intr = getAnnotationIntrospector();
        if (intr != null) {
            vc = intr.findAutoDetectVisibility(actualClass, vc);
        }
        ConfigOverride overrides = this._configOverrides.findOverride(baseType);
        if (overrides != null) {
            vc = vc.withOverrides(overrides.getVisibility());
        }
        return vc;
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public final JsonSetter.Value getDefaultSetterInfo() {
        return this._configOverrides.getDefaultSetterInfo();
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public Boolean getDefaultMergeable() {
        return this._configOverrides.getDefaultMergeable();
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public Boolean getDefaultMergeable(Class<?> baseType) {
        Boolean b;
        ConfigOverride cfg = this._configOverrides.findOverride(baseType);
        if (cfg != null && (b = cfg.getMergeable()) != null) {
            return b;
        }
        return this._configOverrides.getDefaultMergeable();
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public PropertyName findRootName(JavaType rootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName(rootType, this);
    }

    @Override // com.fasterxml.jackson.databind.cfg.MapperConfig
    public PropertyName findRootName(Class<?> rawRootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName(rawRootType, this);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver
    public final Class<?> findMixInClassFor(Class<?> cls) {
        return this._mixIns.findMixInClassFor(cls);
    }

    @Override // com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver
    public ClassIntrospector.MixInResolver copy() {
        throw new UnsupportedOperationException();
    }

    public final int mixInCount() {
        return this._mixIns.localSize();
    }
}