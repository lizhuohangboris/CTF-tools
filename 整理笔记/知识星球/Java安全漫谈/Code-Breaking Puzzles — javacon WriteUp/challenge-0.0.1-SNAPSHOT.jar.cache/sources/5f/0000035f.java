package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/DefaultDeserializationContext.class */
public abstract class DefaultDeserializationContext extends DeserializationContext implements Serializable {
    private static final long serialVersionUID = 1;
    protected transient LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId> _objectIds;
    private List<ObjectIdResolver> _objectIdResolvers;

    public abstract DefaultDeserializationContext with(DeserializerFactory deserializerFactory);

    public abstract DefaultDeserializationContext createInstance(DeserializationConfig deserializationConfig, JsonParser jsonParser, InjectableValues injectableValues);

    protected DefaultDeserializationContext(DeserializerFactory df, DeserializerCache cache) {
        super(df, cache);
    }

    protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializationConfig config, JsonParser jp, InjectableValues values) {
        super(src, config, jp, values);
    }

    protected DefaultDeserializationContext(DefaultDeserializationContext src, DeserializerFactory factory) {
        super(src, factory);
    }

    protected DefaultDeserializationContext(DefaultDeserializationContext src) {
        super(src);
    }

    public DefaultDeserializationContext copy() {
        throw new IllegalStateException("DefaultDeserializationContext sub-class not overriding copy()");
    }

    @Override // com.fasterxml.jackson.databind.DeserializationContext
    public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> gen, ObjectIdResolver resolverType) {
        if (id == null) {
            return null;
        }
        ObjectIdGenerator.IdKey key = gen.key(id);
        if (this._objectIds == null) {
            this._objectIds = new LinkedHashMap<>();
        } else {
            ReadableObjectId entry = this._objectIds.get(key);
            if (entry != null) {
                return entry;
            }
        }
        ObjectIdResolver resolver = null;
        if (this._objectIdResolvers == null) {
            this._objectIdResolvers = new ArrayList(8);
        } else {
            Iterator i$ = this._objectIdResolvers.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                ObjectIdResolver res = i$.next();
                if (res.canUseFor(resolverType)) {
                    resolver = res;
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = resolverType.newForDeserialization(this);
            this._objectIdResolvers.add(resolver);
        }
        ReadableObjectId entry2 = createReadableObjectId(key);
        entry2.setResolver(resolver);
        this._objectIds.put(key, entry2);
        return entry2;
    }

    protected ReadableObjectId createReadableObjectId(ObjectIdGenerator.IdKey key) {
        return new ReadableObjectId(key);
    }

    @Override // com.fasterxml.jackson.databind.DeserializationContext
    public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
        if (this._objectIds == null || !isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS)) {
            return;
        }
        UnresolvedForwardReference exception = null;
        for (Map.Entry<ObjectIdGenerator.IdKey, ReadableObjectId> entry : this._objectIds.entrySet()) {
            ReadableObjectId roid = entry.getValue();
            if (roid.hasReferringProperties() && !tryToResolveUnresolvedObjectId(roid)) {
                if (exception == null) {
                    exception = new UnresolvedForwardReference(getParser(), "Unresolved forward references for: ");
                }
                Object key = roid.getKey().key;
                Iterator<ReadableObjectId.Referring> iterator = roid.referringProperties();
                while (iterator.hasNext()) {
                    ReadableObjectId.Referring referring = iterator.next();
                    exception.addUnresolvedId(key, referring.getBeanType(), referring.getLocation());
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    protected boolean tryToResolveUnresolvedObjectId(ReadableObjectId roid) {
        return roid.tryToResolveUnresolved(this);
    }

    @Override // com.fasterxml.jackson.databind.DeserializationContext
    public JsonDeserializer<Object> deserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
        JsonDeserializer<?> deser;
        if (deserDef == null) {
            return null;
        }
        if (deserDef instanceof JsonDeserializer) {
            deser = (JsonDeserializer) deserDef;
        } else if (!(deserDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
        } else {
            Class<?> deserClass = (Class) deserDef;
            if (deserClass == JsonDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
            }
            HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = hi == null ? null : hi.deserializerInstance(this._config, ann, deserClass);
            if (deser == null) {
                deser = (JsonDeserializer) ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer) deser).resolve(this);
        }
        return deser;
    }

    @Override // com.fasterxml.jackson.databind.DeserializationContext
    public final KeyDeserializer keyDeserializerInstance(Annotated ann, Object deserDef) throws JsonMappingException {
        KeyDeserializer deser;
        if (deserDef == null) {
            return null;
        }
        if (deserDef instanceof KeyDeserializer) {
            deser = (KeyDeserializer) deserDef;
        } else if (!(deserDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + deserDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        } else {
            Class<?> deserClass = (Class) deserDef;
            if (deserClass == KeyDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!KeyDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<KeyDeserializer>");
            }
            HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = hi == null ? null : hi.keyDeserializerInstance(this._config, ann, deserClass);
            if (deser == null) {
                deser = (KeyDeserializer) ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer) deser).resolve(this);
        }
        return deser;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/DefaultDeserializationContext$Impl.class */
    public static final class Impl extends DefaultDeserializationContext {
        private static final long serialVersionUID = 1;

        public Impl(DeserializerFactory df) {
            super(df, (DeserializerCache) null);
        }

        protected Impl(Impl src, DeserializationConfig config, JsonParser jp, InjectableValues values) {
            super(src, config, jp, values);
        }

        protected Impl(Impl src) {
            super(src);
        }

        protected Impl(Impl src, DeserializerFactory factory) {
            super(src, factory);
        }

        @Override // com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
        public DefaultDeserializationContext copy() {
            ClassUtil.verifyMustOverride(Impl.class, this, "copy");
            return new Impl(this);
        }

        @Override // com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
        public DefaultDeserializationContext createInstance(DeserializationConfig config, JsonParser p, InjectableValues values) {
            return new Impl(this, config, p, values);
        }

        @Override // com.fasterxml.jackson.databind.deser.DefaultDeserializationContext
        public DefaultDeserializationContext with(DeserializerFactory factory) {
            return new Impl(this, factory);
        }
    }
}