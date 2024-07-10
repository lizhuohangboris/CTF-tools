package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ConstantValueInstantiator;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/BasicDeserializerFactory.class */
public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
    private static final Class<?> CLASS_OBJECT = Object.class;
    private static final Class<?> CLASS_STRING = String.class;
    private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
    private static final Class<?> CLASS_ITERABLE = Iterable.class;
    private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
    static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap<>();
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    protected final DeserializerFactoryConfig _factoryConfig;

    protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig deserializerFactoryConfig);

    static {
        _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
        _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
        _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
        _mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
        _mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
        _collectionFallbacks = new HashMap<>();
        _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
        _collectionFallbacks.put(List.class.getName(), ArrayList.class);
        _collectionFallbacks.put(Set.class.getName(), HashSet.class);
        _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
        _collectionFallbacks.put("java.util.Deque", LinkedList.class);
        _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BasicDeserializerFactory(DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }

    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
        return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
        return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
        return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
        JavaType next;
        while (true) {
            next = _mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            Class<?> prevCls = type.getRawClass();
            Class<?> nextCls = next.getRawClass();
            if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
                break;
            }
            type = next;
        }
        throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
    }

    private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
        Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && !concrete.hasRawClass(currClass)) {
                    return concrete;
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        AnnotatedClass ac = beanDesc.getClassInfo();
        Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = _valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = _findStdValueInstantiator(config, beanDesc);
            if (instantiator == null) {
                instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
                }
            }
        }
        if (instantiator.getIncompleteParameter() != null) {
            AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
            AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
            throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
        }
        return instantiator;
    }

    private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> raw = beanDesc.getBeanClass();
        if (raw == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        if (Collection.class.isAssignableFrom(raw)) {
            if (Collections.EMPTY_SET.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_SET);
            }
            if (Collections.EMPTY_LIST.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_LIST);
            }
            return null;
        } else if (Map.class.isAssignableFrom(raw) && Collections.EMPTY_MAP.getClass() == raw) {
            return new ConstantValueInstantiator(Collections.EMPTY_MAP);
        } else {
            return null;
        }
    }

    protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        DeserializationConfig config = ctxt.getConfig();
        VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
        _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        if (beanDesc.getType().isConcrete()) {
            _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        }
        return creators.constructValueInstantiator(ctxt);
    }

    protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
        for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
            while (it.hasNext()) {
                AnnotatedParameter param = it.next();
                AnnotatedWithParams owner = param.getOwner();
                BeanPropertyDefinition[] defs = result.get(owner);
                int index = param.getIndex();
                if (defs == null) {
                    boolean isEmpty = result.isEmpty();
                    result = result;
                    if (isEmpty) {
                        result = new LinkedHashMap<>();
                    }
                    defs = new BeanPropertyDefinition[owner.getParameterCount()];
                    result.put(owner, defs);
                } else if (defs[index] != null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", Integer.valueOf(index), owner, defs[index], propDef);
                }
                defs[index] = propDef;
            }
        }
        return result;
    }

    public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
        ValueInstantiator inst;
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator) instDef;
        }
        if (!(instDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        }
        Class<?> instClass = (Class) instDef;
        if (ClassUtil.isBogusClass(instClass)) {
            return null;
        }
        if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        HandlerInstantiator hi = config.getHandlerInstantiator();
        if (hi != null && (inst = hi.valueInstantiatorInstance(config, annotated, instClass)) != null) {
            return inst;
        }
        return (ValueInstantiator) ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
    }

    protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
        if (isNonStaticInnerClass) {
            return;
        }
        AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || _hasCreatorAnnotation(ctxt, defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        List<CreatorCandidate> nonAnnotated = new LinkedList<>();
        int explCount = 0;
        for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
            if (JsonCreator.Mode.DISABLED != creatorMode) {
                if (creatorMode == null) {
                    if (vchecker.isCreatorVisible(ctor)) {
                        nonAnnotated.add(CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                    }
                } else {
                    switch (creatorMode) {
                        case DELEGATING:
                            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, null));
                            break;
                        case PROPERTIES:
                            _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                            break;
                        default:
                            _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                            break;
                    }
                    explCount++;
                }
            }
        }
        if (explCount > 0) {
            return;
        }
        List<AnnotatedWithParams> implicitCtors = null;
        for (CreatorCandidate candidate : nonAnnotated) {
            int argCount = candidate.paramCount();
            AnnotatedWithParams ctor2 = candidate.creator();
            if (argCount == 1) {
                BeanPropertyDefinition propDef = candidate.propertyDef(0);
                boolean useProps = _checkIfCreatorPropertyBased(intr, ctor2, propDef);
                if (useProps) {
                    creators.addPropertyCreator(ctor2, false, new SettableBeanProperty[]{constructCreatorProperty(ctxt, beanDesc, candidate.paramName(0), 0, candidate.parameter(0), candidate.injection(0))});
                } else {
                    _handleSingleArgumentCreator(creators, ctor2, false, vchecker.isCreatorVisible(ctor2));
                    if (propDef != null) {
                        ((POJOPropertyBuilder) propDef).removeConstructors();
                    }
                }
            } else {
                int nonAnnotatedParamIndex = -1;
                SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                int explicitNameCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; i++) {
                    AnnotatedParameter param = ctor2.getParameter(i);
                    BeanPropertyDefinition propDef2 = candidate.propertyDef(i);
                    JacksonInject.Value injectId = intr.findInjectableValue(param);
                    PropertyName name = propDef2 == null ? null : propDef2.getFullName();
                    if (propDef2 != null && propDef2.isExplicitlyNamed()) {
                        explicitNameCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (injectId != null) {
                        injectCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else {
                        NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        } else if (nonAnnotatedParamIndex < 0) {
                            nonAnnotatedParamIndex = i;
                        }
                    }
                }
                int namedCount = explicitNameCount + 0;
                if (explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(ctor2, false, properties);
                    } else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor2, false, properties, 0);
                    } else {
                        PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
                        if (impl == null || impl.isEmpty()) {
                            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", Integer.valueOf(nonAnnotatedParamIndex), ctor2);
                        }
                    }
                }
                if (!creators.hasDefaultCreator()) {
                    if (implicitCtors == null) {
                        implicitCtors = new LinkedList<>();
                    }
                    implicitCtors.add(ctor2);
                }
            }
        }
        if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
            _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
        }
    }

    protected void _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        int ix = -1;
        int argCount = candidate.paramCount();
        SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; i++) {
            AnnotatedParameter param = candidate.parameter(i);
            JacksonInject.Value injectId = candidate.injection(i);
            if (injectId != null) {
                properties[i] = constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
            } else if (ix < 0) {
                ix = i;
            } else {
                ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", Integer.valueOf(ix), Integer.valueOf(i), candidate);
            }
        }
        if (ix < 0) {
            ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", candidate);
        }
        if (argCount == 1) {
            _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
            BeanPropertyDefinition paramDef = candidate.propertyDef(0);
            if (paramDef != null) {
                ((POJOPropertyBuilder) paramDef).removeConstructors();
                return;
            }
            return;
        }
        creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
    }

    protected void _addExplicitPropertyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        int paramCount = candidate.paramCount();
        SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
        for (int i = 0; i < paramCount; i++) {
            JacksonInject.Value injectId = candidate.injection(i);
            AnnotatedParameter param = candidate.parameter(i);
            PropertyName name = candidate.paramName(i);
            if (name == null) {
                NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
                if (unwrapper != null) {
                    _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                }
                name = candidate.findImplicitParamName(i);
                if (name == null && injectId == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d has no property name, is not Injectable: can not use as Creator %s", Integer.valueOf(i), candidate);
                }
            }
            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
        }
        creators.addPropertyCreator(candidate.creator(), true, properties);
    }

    protected void _addExplicitAnyCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
        if (1 != candidate.paramCount()) {
            int oneNotInjected = candidate.findOnlyParamWithoutInjection();
            if (oneNotInjected >= 0 && candidate.paramName(oneNotInjected) == null) {
                _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
                return;
            } else {
                _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
                return;
            }
        }
        AnnotatedParameter param = candidate.parameter(0);
        JacksonInject.Value injectId = candidate.injection(0);
        PropertyName paramName = candidate.explicitParamName(0);
        BeanPropertyDefinition paramDef = candidate.propertyDef(0);
        boolean useProps = (paramName == null && injectId == null) ? false : true;
        if (!useProps && paramDef != null) {
            paramName = candidate.paramName(0);
            useProps = paramName != null && paramDef.couldSerialize();
        }
        if (useProps) {
            SettableBeanProperty[] properties = {constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId)};
            creators.addPropertyCreator(candidate.creator(), true, properties);
            return;
        }
        _handleSingleArgumentCreator(creators, candidate.creator(), true, true);
        if (paramDef != null) {
            ((POJOPropertyBuilder) paramDef).removeConstructors();
        }
    }

    private boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
        String implName;
        if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValue(creator.getParameter(0)) != null) {
            return true;
        }
        if (propDef != null && (implName = propDef.getName()) != null && !implName.isEmpty() && propDef.couldSerialize()) {
            return true;
        }
        return false;
    }

    private void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
        AnnotatedWithParams found = null;
        SettableBeanProperty[] foundProps = null;
        Iterator i$ = implicitCtors.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            AnnotatedWithParams ctor = i$.next();
            if (vchecker.isCreatorVisible(ctor)) {
                int argCount = ctor.getParameterCount();
                SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                int i = 0;
                while (true) {
                    if (i < argCount) {
                        AnnotatedParameter param = ctor.getParameter(i);
                        PropertyName name = _findParamName(param, intr);
                        if (name != null && !name.isEmpty()) {
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
                            i++;
                        }
                    } else if (found != null) {
                        found = null;
                        break;
                    } else {
                        found = ctor;
                        foundProps = properties;
                    }
                }
            }
        }
        if (found != null) {
            creators.addPropertyCreator(found, false, foundProps);
            BasicBeanDescription bbd = (BasicBeanDescription) beanDesc;
            SettableBeanProperty[] arr$ = foundProps;
            for (SettableBeanProperty prop : arr$) {
                PropertyName pn = prop.getFullName();
                if (!bbd.hasProperty(pn)) {
                    BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
                    bbd.addProperty(newDef);
                }
            }
        }
    }

    protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        List<CreatorCandidate> nonAnnotated = new LinkedList<>();
        int explCount = 0;
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
            int argCount = factory.getParameterCount();
            if (creatorMode == null) {
                if (argCount == 1 && vchecker.isCreatorVisible(factory)) {
                    nonAnnotated.add(CreatorCandidate.construct(intr, factory, null));
                }
            } else if (creatorMode != JsonCreator.Mode.DISABLED) {
                if (argCount == 0) {
                    creators.setDefaultCreator(factory);
                } else {
                    switch (creatorMode) {
                        case DELEGATING:
                            _addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, null));
                            break;
                        case PROPERTIES:
                            _addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                        case DEFAULT:
                        default:
                            _addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                    }
                    explCount++;
                }
            }
        }
        if (explCount > 0) {
            return;
        }
        for (CreatorCandidate candidate : nonAnnotated) {
            int argCount2 = candidate.paramCount();
            AnnotatedWithParams factory2 = candidate.creator();
            BeanPropertyDefinition[] propDefs = creatorParams.get(factory2);
            if (argCount2 == 1) {
                BeanPropertyDefinition argDef = candidate.propertyDef(0);
                boolean useProps = _checkIfCreatorPropertyBased(intr, factory2, argDef);
                if (!useProps) {
                    _handleSingleArgumentCreator(creators, factory2, false, vchecker.isCreatorVisible(factory2));
                    if (argDef != null) {
                        ((POJOPropertyBuilder) argDef).removeConstructors();
                    }
                } else {
                    AnnotatedParameter nonAnnotatedParam = null;
                    SettableBeanProperty[] properties = new SettableBeanProperty[argCount2];
                    int explicitNameCount = 0;
                    int injectCount = 0;
                    for (int i = 0; i < argCount2; i++) {
                        AnnotatedParameter param = factory2.getParameter(i);
                        BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                        JacksonInject.Value injectable = intr.findInjectableValue(param);
                        PropertyName name = propDef == null ? null : propDef.getFullName();
                        if (propDef != null && propDef.isExplicitlyNamed()) {
                            explicitNameCount++;
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                        } else if (injectable != null) {
                            injectCount++;
                            properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                        } else {
                            NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                            if (unwrapper != null) {
                                _reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                            } else if (nonAnnotatedParam == null) {
                                nonAnnotatedParam = param;
                            }
                        }
                    }
                    int namedCount = explicitNameCount + 0;
                    if (explicitNameCount > 0 || injectCount > 0) {
                        if (namedCount + injectCount == argCount2) {
                            creators.addPropertyCreator(factory2, false, properties);
                        } else if (explicitNameCount == 0 && injectCount + 1 == argCount2) {
                            creators.addDelegatingCreator(factory2, false, properties, 0);
                        } else {
                            ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", Integer.valueOf(nonAnnotatedParam.getIndex()), factory2);
                        }
                    }
                }
            }
        }
    }

    protected boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
        Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class || type == CLASS_CHAR_SEQUENCE) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor, isCreator);
                return true;
            }
            return true;
        } else if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor, isCreator);
                return true;
            }
            return true;
        } else if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor, isCreator);
                return true;
            }
            return true;
        } else if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor, isCreator);
                return true;
            }
            return true;
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || isVisible) {
                creators.addBooleanCreator(ctor, isCreator);
                return true;
            }
            return true;
        } else if (isCreator) {
            creators.addDelegatingCreator(ctor, isCreator, null, 0);
            return true;
        } else {
            return false;
        }
    }

    protected void _reportUnwrappedCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedParameter param) throws JsonMappingException {
        ctxt.reportBadDefinition(beanDesc.getType(), String.format("Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", Integer.valueOf(param.getIndex())));
    }

    protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
        PropertyMetadata metadata;
        DeserializationConfig config = ctxt.getConfig();
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        } else {
            Boolean b = intr.hasRequiredMarker(param);
            String desc = intr.findPropertyDescription(param);
            Integer idx = intr.findPropertyIndex(param);
            String def = intr.findPropertyDefaultValue(param);
            metadata = PropertyMetadata.construct(b, desc, idx, def);
        }
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
        BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = findTypeDeserializer(config, type);
        }
        Object injectableValueId = injectable == null ? null : injectable.getId();
        SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        SettableBeanProperty prop2 = prop;
        if (deser != null) {
            prop2 = prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        return prop2;
    }

    private PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        if (param != null && intr != null) {
            PropertyName name = intr.findNameForDeserialization(param);
            if (name != null) {
                return name;
            }
            String str = intr.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                return PropertyName.construct(str);
            }
            return null;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType elemType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = (TypeDeserializer) elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (deser == null) {
            if (contentDeser == null) {
                Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                deser = new EnumSetDeserializer(contentType, null);
            }
        }
        if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
                CollectionType implType = _mapAbstractCollectionType(type, config);
                if (implType == null) {
                    if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
                    }
                    deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                } else {
                    type = implType;
                    beanDesc = config.introspectForCreation(type);
                }
            }
            if (deser == null) {
                ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault()) {
                    if (type.hasRawClass(ArrayBlockingQueue.class)) {
                        return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
                    }
                    JsonDeserializer<?> deser2 = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
                    if (deser2 != null) {
                        return deser2;
                    }
                }
                if (contentType.hasRawClass(String.class)) {
                    deser = new StringCollectionDeserializer(type, contentDeser, inst);
                } else {
                    deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
        Class<?> collectionClass = _collectionFallbacks.get(type.getRawClass().getName());
        if (collectionClass == null) {
            return null;
        }
        return (CollectionType) config.constructSpecializedType(type, collectionClass);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator inst;
        DeserializationConfig config = ctxt.getConfig();
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                if (mapClass == EnumMap.class) {
                    inst = null;
                } else {
                    inst = findValueInstantiator(ctxt, beanDesc);
                }
                Class<?> kt = keyType.getRawClass();
                if (kt == null || !kt.isEnum()) {
                    throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
                }
                deser = new EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
                    if (fallback != null) {
                        type = (MapType) config.constructSpecializedType(type, fallback);
                        beanDesc = config.introspectForCreation(type);
                    } else if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
                    } else {
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                } else {
                    deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (deser == null) {
                    ValueInstantiator inst2 = findValueInstantiator(ctxt, beanDesc);
                    MapDeserializer md = new MapDeserializer(type, inst2, keyDes, contentDeser, contentTypeDeser);
                    JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                    Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
                    md.setIgnorableProperties(ignored);
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        DeserializationConfig config = ctxt.getConfig();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        JsonDeserializer<?> deser2 = deser;
        if (deser == null) {
            ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            Iterator i$ = beanDesc.getFactoryMethods().iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                AnnotatedMethod factory = i$.next();
                if (_hasCreatorAnnotation(ctxt, factory)) {
                    if (factory.getParameterCount() == 0) {
                        deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                        break;
                    }
                    Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                        break;
                    }
                }
            }
            JsonDeserializer<?> jsonDeserializer = deser;
            deser2 = deser;
            if (jsonDeserializer == null) {
                deser2 = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), Boolean.valueOf(config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser2 = mod.modifyEnumDeserializer(config, type, beanDesc, deser2);
            }
        }
        return deser2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> rawClass = nodeType.getRawClass();
        JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(rawClass, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(rawClass);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator inst;
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
            Class<?> rawType = type.getRawClass();
            if (rawType == AtomicReference.class) {
                inst = null;
            } else {
                inst = findValueInstantiator(ctxt, beanDesc);
            }
            return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
        JavaType defaultType;
        BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        AnnotatedClass ac = bean.getClassInfo();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        } else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
        }
        if (b.getDefaultImpl() == null && baseType.isAbstract() && (defaultType = mapAbstractType(config, baseType)) != null && !defaultType.hasRawClass(baseType.getRawClass())) {
            b = b.defaultImpl(defaultType.getRawClass());
        }
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        } catch (IllegalArgumentException e0) {
            InvalidDefinitionException e = InvalidDefinitionException.from((JsonParser) null, ClassUtil.exceptionMessage(e0), baseType);
            e.initCause(e0);
            throw e;
        }
    }

    protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }

    @Override // com.fasterxml.jackson.databind.deser.DeserializerFactory
    public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
            for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (type.isEnumType()) {
                deser = _createEnumKeyDeserializer(ctxt, type);
            } else {
                deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
            }
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }

    private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        BeanDescription beanDesc = config.introspect(type);
        KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return des;
        }
        JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (valueDesForKey != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
        }
        EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (_hasCreatorAnnotation(ctxt, factory)) {
                int argCount = factory.getParameterCount();
                if (argCount == 1) {
                    Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        if (factory.getRawParameterType(0) != String.class) {
                            throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                        }
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                    }
                }
                throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }

    public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return findTypeDeserializer(config, baseType);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
        return b.buildTypeDeserializer(config, baseType, subtypes);
    }

    public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        JavaType contentType = containerType.getContentType();
        if (b == null) {
            return findTypeDeserializer(config, contentType);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
        return b.buildTypeDeserializer(config, contentType, subtypes);
    }

    public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType mt;
        JavaType lt;
        Class<?> rawType = type.getRawClass();
        if (rawType == CLASS_OBJECT) {
            DeserializationConfig config = ctxt.getConfig();
            if (this._factoryConfig.hasAbstractTypeResolvers()) {
                lt = _findRemappedType(config, List.class);
                mt = _findRemappedType(config, Map.class);
            } else {
                mt = null;
                lt = null;
            }
            return new UntypedObjectDeserializer(lt, mt);
        } else if (rawType == CLASS_STRING || rawType == CLASS_CHAR_SEQUENCE) {
            return StringDeserializer.instance;
        } else {
            if (rawType == CLASS_ITERABLE) {
                TypeFactory tf = ctxt.getTypeFactory();
                JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
                JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
                CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
                return createCollectionDeserializer(ctxt, ct, beanDesc);
            } else if (rawType == CLASS_MAP_ENTRY) {
                JavaType kt = type.containedTypeOrUnknown(0);
                JavaType vt = type.containedTypeOrUnknown(1);
                TypeDeserializer vts = (TypeDeserializer) vt.getTypeHandler();
                if (vts == null) {
                    vts = findTypeDeserializer(ctxt.getConfig(), vt);
                }
                JsonDeserializer<Object> valueDeser = (JsonDeserializer) vt.getValueHandler();
                KeyDeserializer keyDes = (KeyDeserializer) kt.getValueHandler();
                return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
            } else {
                String clsName = rawType.getName();
                if (rawType.isPrimitive() || clsName.startsWith("java.")) {
                    JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
                    if (deser == null) {
                        deser = DateDeserializers.find(rawType, clsName);
                    }
                    if (deser != null) {
                        return deser;
                    }
                }
                if (rawType == TokenBuffer.class) {
                    return new TokenBufferDeserializer();
                }
                JsonDeserializer<?> deser2 = findOptionalStdDeserializer(ctxt, type, beanDesc);
                if (deser2 != null) {
                    return deser2;
                }
                return JdkDeserializers.find(rawType, clsName);
            }
        }
    }

    protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
        JavaType type = mapAbstractType(config, config.constructType(rawType));
        if (type == null || type.hasRawClass(rawType)) {
            return null;
        }
        return type;
    }

    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findDeserializer(ann)) != null) {
            return ctxt.deserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findKeyDeserializer(ann)) != null) {
            return ctxt.keyDeserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null && (deserDef = intr.findContentDeserializer(ann)) != null) {
            return ctxt.deserializerInstance(ann, deserDef);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
                Object kdDef = intr.findKeyDeserializer(member);
                KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
                if (kd != null) {
                    type = ((MapLikeType) type).withKeyValueHandler(kd);
                    type.getKeyType();
                }
            }
        }
        if (type.hasContentType()) {
            Object cdDef = intr.findContentDeserializer(member);
            JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        return intr.refineDeserializationType(ctxt.getConfig(), member, type);
    }

    protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMember jsonValueAccessor) {
        if (jsonValueAccessor != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return EnumResolver.constructUnsafeUsingMethod(enumClass, jsonValueAccessor, config.getAnnotationIntrospector());
        }
        return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
    }

    protected boolean _hasCreatorAnnotation(DeserializationContext ctxt, Annotated ann) {
        JsonCreator.Mode mode;
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        return (intr == null || (mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann)) == null || mode == JsonCreator.Mode.DISABLED) ? false : true;
    }

    @Deprecated
    protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }

    @Deprecated
    protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
        return resolveMemberAndTypeAnnotations(ctxt, member, type);
    }

    @Deprecated
    protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        BeanDescription beanDesc = config.introspect(enumType);
        return beanDesc.findJsonValueMethod();
    }
}