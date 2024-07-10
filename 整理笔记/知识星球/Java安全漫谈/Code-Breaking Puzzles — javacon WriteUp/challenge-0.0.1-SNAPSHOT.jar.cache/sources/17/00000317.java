package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectMapper.class */
public class ObjectMapper extends ObjectCodec implements Versioned, Serializable {
    private static final long serialVersionUID = 2;
    private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
    protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
    protected static final BaseSettings DEFAULT_BASE = new BaseSettings(null, DEFAULT_ANNOTATION_INTROSPECTOR, null, TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, Locale.getDefault(), null, Base64Variants.getDefaultVariant());
    protected final JsonFactory _jsonFactory;
    protected TypeFactory _typeFactory;
    protected InjectableValues _injectableValues;
    protected SubtypeResolver _subtypeResolver;
    protected final ConfigOverrides _configOverrides;
    protected SimpleMixInResolver _mixIns;
    protected SerializationConfig _serializationConfig;
    protected DefaultSerializerProvider _serializerProvider;
    protected SerializerFactory _serializerFactory;
    protected DeserializationConfig _deserializationConfig;
    protected DefaultDeserializationContext _deserializationContext;
    protected Set<Object> _registeredModuleTypes;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectMapper$DefaultTyping.class */
    public enum DefaultTyping {
        JAVA_LANG_OBJECT,
        OBJECT_AND_NON_CONCRETE,
        NON_CONCRETE_AND_ARRAYS,
        NON_FINAL
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public /* bridge */ /* synthetic */ Iterator readValues(JsonParser x0, TypeReference x1) throws IOException {
        return readValues(x0, (TypeReference<?>) x1);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectMapper$DefaultTypeResolverBuilder.class */
    public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable {
        private static final long serialVersionUID = 1;
        protected final DefaultTyping _appliesFor;

        public DefaultTypeResolverBuilder(DefaultTyping t) {
            this._appliesFor = t;
        }

        @Override // com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder, com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
        public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
            if (useForType(baseType)) {
                return super.buildTypeDeserializer(config, baseType, subtypes);
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder, com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
        public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
            if (useForType(baseType)) {
                return super.buildTypeSerializer(config, baseType, subtypes);
            }
            return null;
        }

        /* JADX WARN: Removed duplicated region for block: B:58:0x0046 A[LOOP:3: B:56:0x003f->B:58:0x0046, LOOP_END] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public boolean useForType(com.fasterxml.jackson.databind.JavaType r4) {
            /*
                r3 = this;
                r0 = r4
                boolean r0 = r0.isPrimitive()
                if (r0 == 0) goto L9
                r0 = 0
                return r0
            L9:
                int[] r0 = com.fasterxml.jackson.databind.ObjectMapper.AnonymousClass3.$SwitchMap$com$fasterxml$jackson$databind$ObjectMapper$DefaultTyping
                r1 = r3
                com.fasterxml.jackson.databind.ObjectMapper$DefaultTyping r1 = r1._appliesFor
                int r1 = r1.ordinal()
                r0 = r0[r1]
                switch(r0) {
                    case 1: goto L30;
                    case 2: goto L3f;
                    case 3: goto L6f;
                    default: goto La7;
                }
            L30:
                r0 = r4
                boolean r0 = r0.isArrayType()
                if (r0 == 0) goto L3f
                r0 = r4
                com.fasterxml.jackson.databind.JavaType r0 = r0.getContentType()
                r4 = r0
                goto L30
            L3f:
                r0 = r4
                boolean r0 = r0.isReferenceType()
                if (r0 == 0) goto L4e
                r0 = r4
                com.fasterxml.jackson.databind.JavaType r0 = r0.getReferencedType()
                r4 = r0
                goto L3f
            L4e:
                r0 = r4
                boolean r0 = r0.isJavaLangObject()
                if (r0 != 0) goto L69
                r0 = r4
                boolean r0 = r0.isConcrete()
                if (r0 != 0) goto L6d
                java.lang.Class<com.fasterxml.jackson.core.TreeNode> r0 = com.fasterxml.jackson.core.TreeNode.class
                r1 = r4
                java.lang.Class r1 = r1.getRawClass()
                boolean r0 = r0.isAssignableFrom(r1)
                if (r0 != 0) goto L6d
            L69:
                r0 = 1
                goto L6e
            L6d:
                r0 = 0
            L6e:
                return r0
            L6f:
                r0 = r4
                boolean r0 = r0.isArrayType()
                if (r0 == 0) goto L7e
                r0 = r4
                com.fasterxml.jackson.databind.JavaType r0 = r0.getContentType()
                r4 = r0
                goto L6f
            L7e:
                r0 = r4
                boolean r0 = r0.isReferenceType()
                if (r0 == 0) goto L8d
                r0 = r4
                com.fasterxml.jackson.databind.JavaType r0 = r0.getReferencedType()
                r4 = r0
                goto L7e
            L8d:
                r0 = r4
                boolean r0 = r0.isFinal()
                if (r0 != 0) goto La5
                java.lang.Class<com.fasterxml.jackson.core.TreeNode> r0 = com.fasterxml.jackson.core.TreeNode.class
                r1 = r4
                java.lang.Class r1 = r1.getRawClass()
                boolean r0 = r0.isAssignableFrom(r1)
                if (r0 != 0) goto La5
                r0 = 1
                goto La6
            La5:
                r0 = 0
            La6:
                return r0
            La7:
                r0 = r4
                boolean r0 = r0.isJavaLangObject()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder.useForType(com.fasterxml.jackson.databind.JavaType):boolean");
        }
    }

    public ObjectMapper() {
        this(null, null, null);
    }

    public ObjectMapper(JsonFactory jf) {
        this(jf, null, null);
    }

    protected ObjectMapper(ObjectMapper src) {
        this._rootDeserializers = new ConcurrentHashMap<>(64, 0.6f, 2);
        this._jsonFactory = src._jsonFactory.copy();
        this._jsonFactory.setCodec(this);
        this._subtypeResolver = src._subtypeResolver;
        this._typeFactory = src._typeFactory;
        this._injectableValues = src._injectableValues;
        this._configOverrides = src._configOverrides.copy();
        this._mixIns = src._mixIns.copy();
        RootNameLookup rootNames = new RootNameLookup();
        this._serializationConfig = new SerializationConfig(src._serializationConfig, this._mixIns, rootNames, this._configOverrides);
        this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, this._mixIns, rootNames, this._configOverrides);
        this._serializerProvider = src._serializerProvider.copy();
        this._deserializationContext = src._deserializationContext.copy();
        this._serializerFactory = src._serializerFactory;
        Set<Object> reg = src._registeredModuleTypes;
        if (reg == null) {
            this._registeredModuleTypes = null;
        } else {
            this._registeredModuleTypes = new LinkedHashSet(reg);
        }
    }

    public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
        this._rootDeserializers = new ConcurrentHashMap<>(64, 0.6f, 2);
        if (jf == null) {
            this._jsonFactory = new MappingJsonFactory(this);
        } else {
            this._jsonFactory = jf;
            if (jf.getCodec() == null) {
                this._jsonFactory.setCodec(this);
            }
        }
        this._subtypeResolver = new StdSubtypeResolver();
        RootNameLookup rootNames = new RootNameLookup();
        this._typeFactory = TypeFactory.defaultInstance();
        SimpleMixInResolver mixins = new SimpleMixInResolver(null);
        this._mixIns = mixins;
        BaseSettings base = DEFAULT_BASE.withClassIntrospector(defaultClassIntrospector());
        this._configOverrides = new ConfigOverrides();
        this._serializationConfig = new SerializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
        this._deserializationConfig = new DeserializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
        boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
        if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
            configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
        }
        this._serializerProvider = sp == null ? new DefaultSerializerProvider.Impl() : sp;
        this._deserializationContext = dc == null ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc;
        this._serializerFactory = BeanSerializerFactory.instance;
    }

    protected ClassIntrospector defaultClassIntrospector() {
        return new BasicClassIntrospector();
    }

    public ObjectMapper copy() {
        _checkInvalidCopy(ObjectMapper.class);
        return new ObjectMapper(this);
    }

    protected void _checkInvalidCopy(Class<?> exp) {
        if (getClass() != exp) {
            throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to");
        }
    }

    protected ObjectReader _newReader(DeserializationConfig config) {
        return new ObjectReader(this, config);
    }

    protected ObjectReader _newReader(DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
        return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
    }

    protected ObjectWriter _newWriter(SerializationConfig config) {
        return new ObjectWriter(this, config);
    }

    protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema) {
        return new ObjectWriter(this, config, schema);
    }

    protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
        return new ObjectWriter(this, config, rootType, pp);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    public ObjectMapper registerModule(Module module) {
        Object typeId;
        if (isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS) && (typeId = module.getTypeId()) != null) {
            if (this._registeredModuleTypes == null) {
                this._registeredModuleTypes = new LinkedHashSet();
            }
            if (!this._registeredModuleTypes.add(typeId)) {
                return this;
            }
        }
        String name = module.getModuleName();
        if (name == null) {
            throw new IllegalArgumentException("Module without defined name");
        }
        Version version = module.version();
        if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
        }
        module.setupModule(new Module.SetupContext() { // from class: com.fasterxml.jackson.databind.ObjectMapper.1
            {
                ObjectMapper.this = this;
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public Version getMapperVersion() {
                return ObjectMapper.this.version();
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public <C extends ObjectCodec> C getOwner() {
                return ObjectMapper.this;
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public TypeFactory getTypeFactory() {
                return ObjectMapper.this._typeFactory;
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(MapperFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(DeserializationFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(SerializationFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(JsonFactory.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(JsonParser.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public boolean isEnabled(JsonGenerator.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public MutableConfigOverride configOverride(Class<?> type) {
                return ObjectMapper.this.configOverride(type);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addDeserializers(Deserializers d) {
                DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalDeserializers(d);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addKeyDeserializers(KeyDeserializers d) {
                DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalKeyDeserializers(d);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
                DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withDeserializerModifier(modifier);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addSerializers(Serializers s) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalSerializers(s);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addKeySerializers(Serializers s) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalKeySerializers(s);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withSerializerModifier(modifier);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
                DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAbstractTypeResolver(resolver);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addTypeModifier(TypeModifier modifier) {
                TypeFactory f = ObjectMapper.this._typeFactory;
                ObjectMapper.this.setTypeFactory(f.withModifier(modifier));
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addValueInstantiators(ValueInstantiators instantiators) {
                DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withValueInstantiators(instantiators);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void setClassIntrospector(ClassIntrospector ci) {
                ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.with(ci);
                ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.with(ci);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
                ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.withInsertedAnnotationIntrospector(ai);
                ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.withInsertedAnnotationIntrospector(ai);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
                ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.withAppendedAnnotationIntrospector(ai);
                ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.withAppendedAnnotationIntrospector(ai);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void registerSubtypes(Class<?>... subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void registerSubtypes(NamedType... subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void registerSubtypes(Collection<Class<?>> subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
                ObjectMapper.this.addMixIn(target, mixinSource);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void addDeserializationProblemHandler(DeserializationProblemHandler handler) {
                ObjectMapper.this.addHandler(handler);
            }

            @Override // com.fasterxml.jackson.databind.Module.SetupContext
            public void setNamingStrategy(PropertyNamingStrategy naming) {
                ObjectMapper.this.setPropertyNamingStrategy(naming);
            }
        });
        return this;
    }

    public ObjectMapper registerModules(Module... modules) {
        for (Module module : modules) {
            registerModule(module);
        }
        return this;
    }

    public ObjectMapper registerModules(Iterable<? extends Module> modules) {
        for (Module module : modules) {
            registerModule(module);
        }
        return this;
    }

    public Set<Object> getRegisteredModuleIds() {
        return Collections.unmodifiableSet(this._registeredModuleTypes);
    }

    public static List<Module> findModules() {
        return findModules(null);
    }

    public static List<Module> findModules(ClassLoader classLoader) {
        ArrayList<Module> modules = new ArrayList<>();
        ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
        Iterator i$ = loader.iterator();
        while (i$.hasNext()) {
            Module module = i$.next();
            modules.add(module);
        }
        return modules;
    }

    private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
        }
        return (ServiceLoader) AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() { // from class: com.fasterxml.jackson.databind.ObjectMapper.2
            @Override // java.security.PrivilegedAction
            public ServiceLoader<T> run() {
                return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
        });
    }

    public ObjectMapper findAndRegisterModules() {
        return registerModules(findModules());
    }

    public SerializationConfig getSerializationConfig() {
        return this._serializationConfig;
    }

    public DeserializationConfig getDeserializationConfig() {
        return this._deserializationConfig;
    }

    public DeserializationContext getDeserializationContext() {
        return this._deserializationContext;
    }

    public ObjectMapper setSerializerFactory(SerializerFactory f) {
        this._serializerFactory = f;
        return this;
    }

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
        this._serializerProvider = p;
        return this;
    }

    public SerializerProvider getSerializerProvider() {
        return this._serializerProvider;
    }

    public SerializerProvider getSerializerProviderInstance() {
        return _serializerProvider(this._serializationConfig);
    }

    public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins) {
        this._mixIns.setLocalDefinitions(sourceMixins);
        return this;
    }

    public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource) {
        this._mixIns.addLocalDefinition(target, mixinSource);
        return this;
    }

    public ObjectMapper setMixInResolver(ClassIntrospector.MixInResolver resolver) {
        SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
        if (r != this._mixIns) {
            this._mixIns = r;
            this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
            this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
        }
        return this;
    }

    public Class<?> findMixInClassFor(Class<?> cls) {
        return this._mixIns.findMixInClassFor(cls);
    }

    public int mixInCount() {
        return this._mixIns.localSize();
    }

    @Deprecated
    public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins) {
        setMixIns(sourceMixins);
    }

    @Deprecated
    public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource) {
        addMixIn(target, mixinSource);
    }

    public VisibilityChecker<?> getVisibilityChecker() {
        return this._serializationConfig.getDefaultVisibilityChecker();
    }

    public ObjectMapper setVisibility(VisibilityChecker<?> vc) {
        this._configOverrides.setDefaultVisibility(vc);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.fasterxml.jackson.databind.introspect.VisibilityChecker] */
    public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
        VisibilityChecker<?> vc = this._configOverrides.getDefaultVisibility();
        this._configOverrides.setDefaultVisibility(vc.withVisibility(forMethod, visibility));
        return this;
    }

    public SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }

    public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
        this._subtypeResolver = str;
        this._deserializationConfig = this._deserializationConfig.with(str);
        this._serializationConfig = this._serializationConfig.with(str);
        return this;
    }

    public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
        this._serializationConfig = this._serializationConfig.with(ai);
        this._deserializationConfig = this._deserializationConfig.with(ai);
        return this;
    }

    public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI) {
        this._serializationConfig = this._serializationConfig.with(serializerAI);
        this._deserializationConfig = this._deserializationConfig.with(deserializerAI);
        return this;
    }

    public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
        this._serializationConfig = this._serializationConfig.with(s);
        this._deserializationConfig = this._deserializationConfig.with(s);
        return this;
    }

    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._serializationConfig.getPropertyNamingStrategy();
    }

    public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp) {
        this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
        return this;
    }

    @Deprecated
    public void setVisibilityChecker(VisibilityChecker<?> vc) {
        setVisibility(vc);
    }

    public ObjectMapper setSerializationInclusion(JsonInclude.Include incl) {
        setPropertyInclusion(JsonInclude.Value.construct(incl, incl));
        return this;
    }

    @Deprecated
    public ObjectMapper setPropertyInclusion(JsonInclude.Value incl) {
        return setDefaultPropertyInclusion(incl);
    }

    public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Value incl) {
        this._configOverrides.setDefaultInclusion(incl);
        return this;
    }

    public ObjectMapper setDefaultPropertyInclusion(JsonInclude.Include incl) {
        this._configOverrides.setDefaultInclusion(JsonInclude.Value.construct(incl, incl));
        return this;
    }

    public ObjectMapper setDefaultSetterInfo(JsonSetter.Value v) {
        this._configOverrides.setDefaultSetterInfo(v);
        return this;
    }

    public ObjectMapper setDefaultVisibility(JsonAutoDetect.Value vis) {
        this._configOverrides.setDefaultVisibility(VisibilityChecker.Std.construct(vis));
        return this;
    }

    public ObjectMapper setDefaultMergeable(Boolean b) {
        this._configOverrides.setDefaultMergeable(b);
        return this;
    }

    public ObjectMapper enableDefaultTyping() {
        return enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }

    public ObjectMapper enableDefaultTyping(DefaultTyping dti) {
        return enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
    }

    public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs) {
        if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
            throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
        }
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        return setDefaultTyping(typer.init(JsonTypeInfo.Id.CLASS, null).inclusion(includeAs));
    }

    public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName) {
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        return setDefaultTyping(typer.init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY).typeProperty(propertyName));
    }

    public ObjectMapper disableDefaultTyping() {
        return setDefaultTyping(null);
    }

    public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
        this._deserializationConfig = this._deserializationConfig.with(typer);
        this._serializationConfig = this._serializationConfig.with(typer);
        return this;
    }

    public void registerSubtypes(Class<?>... classes) {
        getSubtypeResolver().registerSubtypes(classes);
    }

    public void registerSubtypes(NamedType... types) {
        getSubtypeResolver().registerSubtypes(types);
    }

    public void registerSubtypes(Collection<Class<?>> subtypes) {
        getSubtypeResolver().registerSubtypes(subtypes);
    }

    public MutableConfigOverride configOverride(Class<?> type) {
        return this._configOverrides.findOrCreateOverride(type);
    }

    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }

    public ObjectMapper setTypeFactory(TypeFactory f) {
        this._typeFactory = f;
        this._deserializationConfig = this._deserializationConfig.with(f);
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }

    public JavaType constructType(Type t) {
        return this._typeFactory.constructType(t);
    }

    public JsonNodeFactory getNodeFactory() {
        return this._deserializationConfig.getNodeFactory();
    }

    public ObjectMapper setNodeFactory(JsonNodeFactory f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        return this;
    }

    public ObjectMapper addHandler(DeserializationProblemHandler h) {
        this._deserializationConfig = this._deserializationConfig.withHandler(h);
        return this;
    }

    public ObjectMapper clearProblemHandlers() {
        this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
        return this;
    }

    public ObjectMapper setConfig(DeserializationConfig config) {
        this._deserializationConfig = config;
        return this;
    }

    @Deprecated
    public void setFilters(FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
    }

    public ObjectMapper setFilterProvider(FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
        return this;
    }

    public ObjectMapper setBase64Variant(Base64Variant v) {
        this._serializationConfig = this._serializationConfig.with(v);
        this._deserializationConfig = this._deserializationConfig.with(v);
        return this;
    }

    public ObjectMapper setConfig(SerializationConfig config) {
        this._serializationConfig = config;
        return this;
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public JsonFactory getFactory() {
        return this._jsonFactory;
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    @Deprecated
    public JsonFactory getJsonFactory() {
        return getFactory();
    }

    public ObjectMapper setDateFormat(DateFormat dateFormat) {
        this._deserializationConfig = this._deserializationConfig.with(dateFormat);
        this._serializationConfig = this._serializationConfig.with(dateFormat);
        return this;
    }

    public DateFormat getDateFormat() {
        return this._serializationConfig.getDateFormat();
    }

    public Object setHandlerInstantiator(HandlerInstantiator hi) {
        this._deserializationConfig = this._deserializationConfig.with(hi);
        this._serializationConfig = this._serializationConfig.with(hi);
        return this;
    }

    public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
        this._injectableValues = injectableValues;
        return this;
    }

    public InjectableValues getInjectableValues() {
        return this._injectableValues;
    }

    public ObjectMapper setLocale(Locale l) {
        this._deserializationConfig = this._deserializationConfig.with(l);
        this._serializationConfig = this._serializationConfig.with(l);
        return this;
    }

    public ObjectMapper setTimeZone(TimeZone tz) {
        this._deserializationConfig = this._deserializationConfig.with(tz);
        this._serializationConfig = this._serializationConfig.with(tz);
        return this;
    }

    public boolean isEnabled(MapperFeature f) {
        return this._serializationConfig.isEnabled(f);
    }

    public ObjectMapper configure(MapperFeature f, boolean state) {
        this._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
        this._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
        return this;
    }

    public ObjectMapper enable(MapperFeature... f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }

    public ObjectMapper disable(MapperFeature... f) {
        this._deserializationConfig = this._deserializationConfig.without(f);
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }

    public boolean isEnabled(SerializationFeature f) {
        return this._serializationConfig.isEnabled(f);
    }

    public ObjectMapper configure(SerializationFeature f, boolean state) {
        this._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
        return this;
    }

    public ObjectMapper enable(SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }

    public ObjectMapper enable(SerializationFeature first, SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.with(first, f);
        return this;
    }

    public ObjectMapper disable(SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }

    public ObjectMapper disable(SerializationFeature first, SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.without(first, f);
        return this;
    }

    public boolean isEnabled(DeserializationFeature f) {
        return this._deserializationConfig.isEnabled(f);
    }

    public ObjectMapper configure(DeserializationFeature f, boolean state) {
        this._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
        return this;
    }

    public ObjectMapper enable(DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.with(feature);
        return this;
    }

    public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.with(first, f);
        return this;
    }

    public ObjectMapper disable(DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.without(feature);
        return this;
    }

    public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.without(first, f);
        return this;
    }

    public boolean isEnabled(JsonParser.Feature f) {
        return this._deserializationConfig.isEnabled(f, this._jsonFactory);
    }

    public ObjectMapper configure(JsonParser.Feature f, boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }

    public ObjectMapper enable(JsonParser.Feature... features) {
        for (JsonParser.Feature f : features) {
            this._jsonFactory.enable(f);
        }
        return this;
    }

    public ObjectMapper disable(JsonParser.Feature... features) {
        for (JsonParser.Feature f : features) {
            this._jsonFactory.disable(f);
        }
        return this;
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return this._serializationConfig.isEnabled(f, this._jsonFactory);
    }

    public ObjectMapper configure(JsonGenerator.Feature f, boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }

    public ObjectMapper enable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature f : features) {
            this._jsonFactory.enable(f);
        }
        return this;
    }

    public ObjectMapper disable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature f : features) {
            this._jsonFactory.disable(f);
        }
        return this;
    }

    public boolean isEnabled(JsonFactory.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueType));
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public final <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readValue(getDeserializationConfig(), p, (JavaType) valueType);
    }

    public <T> T readValue(JsonParser p, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readValue(getDeserializationConfig(), p, valueType);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public <T extends TreeNode> T readTree(JsonParser p) throws IOException, JsonProcessingException {
        DeserializationConfig cfg = getDeserializationConfig();
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            JsonToken t2 = p.nextToken();
            if (t2 == null) {
                return null;
            }
        }
        JsonNode n = (JsonNode) _readValue(cfg, p, JSON_NODE_TYPE);
        if (n == null) {
            n = getNodeFactory().nullNode();
        }
        JsonNode result = n;
        return result;
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException, JsonProcessingException {
        return readValues(p, (JavaType) valueType);
    }

    public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType) throws IOException, JsonProcessingException {
        DeserializationConfig config = getDeserializationConfig();
        DeserializationContext ctxt = createDeserializationContext(p, config);
        JsonDeserializer<?> deser = _findRootDeserializer(ctxt, valueType);
        return new MappingIterator<>(valueType, p, ctxt, deser, false, null);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException, JsonProcessingException {
        return readValues(p, this._typeFactory.constructType(valueType));
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return readValues(p, this._typeFactory.constructType(valueTypeRef));
    }

    public JsonNode readTree(InputStream in) throws IOException {
        return _readTreeAndClose(this._jsonFactory.createParser(in));
    }

    public JsonNode readTree(Reader r) throws IOException {
        return _readTreeAndClose(this._jsonFactory.createParser(r));
    }

    public JsonNode readTree(String content) throws IOException {
        return _readTreeAndClose(this._jsonFactory.createParser(content));
    }

    public JsonNode readTree(byte[] content) throws IOException {
        return _readTreeAndClose(this._jsonFactory.createParser(content));
    }

    public JsonNode readTree(File file) throws IOException, JsonProcessingException {
        return _readTreeAndClose(this._jsonFactory.createParser(file));
    }

    public JsonNode readTree(URL source) throws IOException {
        return _readTreeAndClose(this._jsonFactory.createParser(source));
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public void writeValue(JsonGenerator g, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        SerializationConfig config = getSerializationConfig();
        if (config.isEnabled(SerializationFeature.INDENT_OUTPUT) && g.getPrettyPrinter() == null) {
            g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
        }
        if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && (value instanceof Closeable)) {
            _writeCloseableValue(g, value, config);
            return;
        }
        _serializerProvider(config).serializeValue(g, value);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
        }
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public void writeTree(JsonGenerator jgen, TreeNode rootNode) throws IOException, JsonProcessingException {
        SerializationConfig config = getSerializationConfig();
        _serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }

    public void writeTree(JsonGenerator jgen, JsonNode rootNode) throws IOException, JsonProcessingException {
        SerializationConfig config = getSerializationConfig();
        _serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public ObjectNode createObjectNode() {
        return this._deserializationConfig.getNodeFactory().objectNode();
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public ArrayNode createArrayNode() {
        return this._deserializationConfig.getNodeFactory().arrayNode();
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public JsonParser treeAsTokens(TreeNode n) {
        return new TreeTraversingParser((JsonNode) n, this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
        T t;
        if (valueType != Object.class) {
            try {
                if (valueType.isAssignableFrom(n.getClass())) {
                    return n;
                }
            } catch (JsonProcessingException e) {
                throw e;
            } catch (IOException e2) {
                throw new IllegalArgumentException(e2.getMessage(), e2);
            }
        }
        if (n.asToken() == JsonToken.VALUE_EMBEDDED_OBJECT && (n instanceof POJONode) && ((t = (T) ((POJONode) n).getPojo()) == null || valueType.isInstance(t))) {
            return t;
        }
        return (T) readValue(treeAsTokens(n), valueType);
    }

    public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        TokenBuffer buf = new TokenBuffer((ObjectCodec) this, false);
        if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
        try {
            writeValue(buf, fromValue);
            JsonParser p = buf.asParser();
            T t = (T) readTree(p);
            p.close();
            return t;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public boolean canSerialize(Class<?> type) {
        return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, null);
    }

    public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
        return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, cause);
    }

    public boolean canDeserialize(JavaType type) {
        return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, null);
    }

    public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause) {
        return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, cause);
    }

    public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(File src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(File src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(URL src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(URL src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(String content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(String content, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(content), valueType);
    }

    public <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(Reader src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(Reader src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(InputStream src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(InputStream src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(byte[] src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(byte[] src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
    }

    public <T> T readValue(DataInput src, Class<T> valueType) throws IOException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(DataInput src, JavaType valueType) throws IOException {
        return (T) _readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }

    public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }

    public void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }

    public void writeValue(DataOutput out, Object value) throws IOException {
        _configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }

    public void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._jsonFactory.createGenerator(w), value);
    }

    public String writeValueAsString(Object value) throws JsonProcessingException {
        SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
        try {
            _configAndWriteValue(this._jsonFactory.createGenerator(sw), value);
            return sw.getAndClear();
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
        ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
        try {
            _configAndWriteValue(this._jsonFactory.createGenerator(bb, JsonEncoding.UTF8), value);
            byte[] result = bb.toByteArray();
            bb.release();
            return result;
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    public ObjectWriter writer() {
        return _newWriter(getSerializationConfig());
    }

    public ObjectWriter writer(SerializationFeature feature) {
        return _newWriter(getSerializationConfig().with(feature));
    }

    public ObjectWriter writer(SerializationFeature first, SerializationFeature... other) {
        return _newWriter(getSerializationConfig().with(first, other));
    }

    public ObjectWriter writer(DateFormat df) {
        return _newWriter(getSerializationConfig().with(df));
    }

    public ObjectWriter writerWithView(Class<?> serializationView) {
        return _newWriter(getSerializationConfig().withView(serializationView));
    }

    public ObjectWriter writerFor(Class<?> rootType) {
        return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
    }

    public ObjectWriter writerFor(TypeReference<?> rootType) {
        return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
    }

    public ObjectWriter writerFor(JavaType rootType) {
        return _newWriter(getSerializationConfig(), rootType, null);
    }

    public ObjectWriter writer(PrettyPrinter pp) {
        if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
        }
        return _newWriter(getSerializationConfig(), null, pp);
    }

    public ObjectWriter writerWithDefaultPrettyPrinter() {
        SerializationConfig config = getSerializationConfig();
        return _newWriter(config, null, config.getDefaultPrettyPrinter());
    }

    public ObjectWriter writer(FilterProvider filterProvider) {
        return _newWriter(getSerializationConfig().withFilters(filterProvider));
    }

    public ObjectWriter writer(FormatSchema schema) {
        _verifySchemaType(schema);
        return _newWriter(getSerializationConfig(), schema);
    }

    public ObjectWriter writer(Base64Variant defaultBase64) {
        return _newWriter(getSerializationConfig().with(defaultBase64));
    }

    public ObjectWriter writer(CharacterEscapes escapes) {
        return _newWriter(getSerializationConfig()).with(escapes);
    }

    public ObjectWriter writer(ContextAttributes attrs) {
        return _newWriter(getSerializationConfig().with(attrs));
    }

    @Deprecated
    public ObjectWriter writerWithType(Class<?> rootType) {
        return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
    }

    @Deprecated
    public ObjectWriter writerWithType(TypeReference<?> rootType) {
        return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
    }

    @Deprecated
    public ObjectWriter writerWithType(JavaType rootType) {
        return _newWriter(getSerializationConfig(), rootType, null);
    }

    public ObjectReader reader() {
        return _newReader(getDeserializationConfig()).with(this._injectableValues);
    }

    public ObjectReader reader(DeserializationFeature feature) {
        return _newReader(getDeserializationConfig().with(feature));
    }

    public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other) {
        return _newReader(getDeserializationConfig().with(first, other));
    }

    public ObjectReader readerForUpdating(Object valueToUpdate) {
        JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
        return _newReader(getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
    }

    public ObjectReader readerFor(JavaType type) {
        return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
    }

    public ObjectReader readerFor(Class<?> type) {
        return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }

    public ObjectReader readerFor(TypeReference<?> type) {
        return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }

    public ObjectReader reader(JsonNodeFactory f) {
        return _newReader(getDeserializationConfig()).with(f);
    }

    public ObjectReader reader(FormatSchema schema) {
        _verifySchemaType(schema);
        return _newReader(getDeserializationConfig(), null, null, schema, this._injectableValues);
    }

    public ObjectReader reader(InjectableValues injectableValues) {
        return _newReader(getDeserializationConfig(), null, null, null, injectableValues);
    }

    public ObjectReader readerWithView(Class<?> view) {
        return _newReader(getDeserializationConfig().withView(view));
    }

    public ObjectReader reader(Base64Variant defaultBase64) {
        return _newReader(getDeserializationConfig().with(defaultBase64));
    }

    public ObjectReader reader(ContextAttributes attrs) {
        return _newReader(getDeserializationConfig().with(attrs));
    }

    @Deprecated
    public ObjectReader reader(JavaType type) {
        return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
    }

    @Deprecated
    public ObjectReader reader(Class<?> type) {
        return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }

    @Deprecated
    public ObjectReader reader(TypeReference<?> type) {
        return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return (T) _convert(fromValue, this._typeFactory.constructType(toValueType));
    }

    public <T> T convertValue(Object fromValue, TypeReference<?> toValueTypeRef) throws IllegalArgumentException {
        return (T) _convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
    }

    public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
        return (T) _convert(fromValue, toValueType);
    }

    protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
        Object result;
        Class<?> targetType;
        if (fromValue != null && (targetType = toValueType.getRawClass()) != Object.class && !toValueType.hasGenericTypes() && targetType.isAssignableFrom(fromValue.getClass())) {
            return fromValue;
        }
        TokenBuffer buf = new TokenBuffer((ObjectCodec) this, false);
        if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
        try {
            SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
            _serializerProvider(config).serializeValue(buf, fromValue);
            JsonParser p = buf.asParser();
            DeserializationConfig deserConfig = getDeserializationConfig();
            JsonToken t = _initForReading(p, toValueType);
            if (t == JsonToken.VALUE_NULL) {
                DeserializationContext ctxt = createDeserializationContext(p, deserConfig);
                result = _findRootDeserializer(ctxt, toValueType).getNullValue(ctxt);
            } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            } else {
                DeserializationContext ctxt2 = createDeserializationContext(p, deserConfig);
                JsonDeserializer<Object> deser = _findRootDeserializer(ctxt2, toValueType);
                result = deser.deserialize(p, ctxt2);
            }
            p.close();
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T updateValue(T valueToUpdate, Object overrides) throws JsonMappingException {
        T result = valueToUpdate;
        if (valueToUpdate != null && overrides != null) {
            TokenBuffer buf = new TokenBuffer((ObjectCodec) this, false);
            if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                buf = buf.forceUseOfBigDecimal(true);
            }
            try {
                SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
                _serializerProvider(config).serializeValue(buf, overrides);
                JsonParser p = buf.asParser();
                result = readerForUpdating(valueToUpdate).readValue(p);
                p.close();
            } catch (IOException e) {
                if (e instanceof JsonMappingException) {
                    throw ((JsonMappingException) e);
                }
                throw JsonMappingException.fromUnexpectedIOE(e);
            }
        }
        return result;
    }

    @Deprecated
    public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
        return _serializerProvider(getSerializationConfig()).generateJsonSchema(t);
    }

    public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
    }

    public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        _serializerProvider(getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
    }

    protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
        return this._serializerProvider.createInstance(config, this._serializerFactory);
    }

    protected final void _configAndWriteValue(JsonGenerator g, Object value) throws IOException {
        SerializationConfig cfg = getSerializationConfig();
        cfg.initialize(g);
        if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && (value instanceof Closeable)) {
            _configAndWriteCloseable(g, value, cfg);
            return;
        }
        try {
            _serializerProvider(cfg).serializeValue(g, value);
            g.close();
        } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, e);
        }
    }

    private final void _configAndWriteCloseable(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
        Closeable toClose = (Closeable) value;
        try {
            _serializerProvider(cfg).serializeValue(g, value);
            toClose = null;
            toClose.close();
            g.close();
        } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, e);
        }
    }

    private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
        Closeable toClose = (Closeable) value;
        try {
            _serializerProvider(cfg).serializeValue(g, value);
            if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                g.flush();
            }
            toClose.close();
        } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
        }
    }

    protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType) throws IOException {
        Object result;
        JsonToken t = _initForReading(p, valueType);
        DeserializationContext ctxt = createDeserializationContext(p, cfg);
        if (t == JsonToken.VALUE_NULL) {
            result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
        } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = null;
        } else {
            JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
            if (cfg.useRootWrapping()) {
                result = _unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
            } else {
                result = deser.deserialize(p, ctxt);
            }
        }
        p.clearCurrentToken();
        if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            _verifyNoTrailingTokens(p, ctxt, valueType);
        }
        return result;
    }

    protected Object _readMapAndClose(JsonParser p0, JavaType valueType) throws IOException {
        Object result;
        Throwable th = null;
        try {
            JsonToken t = _initForReading(p0, valueType);
            DeserializationConfig cfg = getDeserializationConfig();
            DeserializationContext ctxt = createDeserializationContext(p0, cfg);
            if (t == JsonToken.VALUE_NULL) {
                result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
            } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            } else {
                JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
                if (cfg.useRootWrapping()) {
                    result = _unwrapAndDeserialize(p0, ctxt, cfg, valueType, deser);
                } else {
                    result = deser.deserialize(p0, ctxt);
                }
                ctxt.checkUnresolvedObjectId();
            }
            if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                _verifyNoTrailingTokens(p0, ctxt, valueType);
            }
            Object obj = result;
            if (p0 != null) {
                if (0 != 0) {
                    try {
                        p0.close();
                    } catch (Throwable x2) {
                        th.addSuppressed(x2);
                    }
                } else {
                    p0.close();
                }
            }
            return obj;
        } finally {
        }
    }

    protected JsonNode _readTreeAndClose(JsonParser p0) throws IOException {
        Object result;
        Throwable th = null;
        try {
            JavaType valueType = JSON_NODE_TYPE;
            DeserializationConfig cfg = getDeserializationConfig();
            cfg.initialize(p0);
            JsonToken t = p0.getCurrentToken();
            if (t == null) {
                t = p0.nextToken();
                if (t == null) {
                    if (p0 != null) {
                        if (0 != 0) {
                            try {
                                p0.close();
                            } catch (Throwable x2) {
                                th.addSuppressed(x2);
                            }
                        } else {
                            p0.close();
                        }
                    }
                    return null;
                }
            }
            if (t == JsonToken.VALUE_NULL) {
                NullNode nullNode = cfg.getNodeFactory().nullNode();
                if (p0 != null) {
                    if (0 != 0) {
                        try {
                            p0.close();
                        } catch (Throwable x22) {
                            th.addSuppressed(x22);
                        }
                    } else {
                        p0.close();
                    }
                }
                return nullNode;
            }
            DeserializationContext ctxt = createDeserializationContext(p0, cfg);
            JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
            if (cfg.useRootWrapping()) {
                result = _unwrapAndDeserialize(p0, ctxt, cfg, valueType, deser);
            } else {
                result = deser.deserialize(p0, ctxt);
                if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                    _verifyNoTrailingTokens(p0, ctxt, valueType);
                }
            }
            JsonNode jsonNode = (JsonNode) result;
            if (p0 != null) {
                if (0 != 0) {
                    try {
                        p0.close();
                    } catch (Throwable x23) {
                        th.addSuppressed(x23);
                    }
                } else {
                    p0.close();
                }
            }
            return jsonNode;
        } finally {
        }
    }

    protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, DeserializationConfig config, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
        PropertyName expRootName = config.findRootName(rootType);
        String expSimpleName = expRootName.getSimpleName();
        if (p.getCurrentToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (p.nextToken() != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        String actualName = p.getCurrentName();
        if (!expSimpleName.equals(actualName)) {
            ctxt.reportInputMismatch(rootType, "Root name '%s' does not match expected ('%s') for type %s", actualName, expSimpleName, rootType);
        }
        p.nextToken();
        Object result = deser.deserialize(p, ctxt);
        if (p.nextToken() != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            _verifyNoTrailingTokens(p, ctxt, rootType);
        }
        return result;
    }

    protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg) {
        return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
    }

    protected JsonToken _initForReading(JsonParser p, JavaType targetType) throws IOException {
        this._deserializationConfig.initialize(p);
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
            }
        }
        return t;
    }

    @Deprecated
    protected JsonToken _initForReading(JsonParser p) throws IOException {
        return _initForReading(p, null);
    }

    protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
        JsonToken t = p.nextToken();
        if (t != null) {
            Class<?> bt = ClassUtil.rawClass(bindType);
            ctxt.reportTrailingTokens(bt, p, t);
        }
    }

    protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser != null) {
            return deser;
        }
        JsonDeserializer<Object> deser2 = ctxt.findRootValueDeserializer(valueType);
        if (deser2 == null) {
            return (JsonDeserializer) ctxt.reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
        }
        this._rootDeserializers.put(valueType, deser2);
        return deser2;
    }

    protected void _verifySchemaType(FormatSchema schema) {
        if (schema != null && !this._jsonFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName());
        }
    }
}