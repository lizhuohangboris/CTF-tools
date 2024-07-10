package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LinkedNode;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/DeserializationContext.class */
public abstract class DeserializationContext extends DatabindContext implements Serializable {
    private static final long serialVersionUID = 1;
    protected final DeserializerCache _cache;
    protected final DeserializerFactory _factory;
    protected final DeserializationConfig _config;
    protected final int _featureFlags;
    protected final Class<?> _view;
    protected transient JsonParser _parser;
    protected final InjectableValues _injectableValues;
    protected transient ArrayBuilders _arrayBuilders;
    protected transient ObjectBuffer _objectBuffer;
    protected transient DateFormat _dateFormat;
    protected transient ContextAttributes _attributes;
    protected LinkedNode<JavaType> _currentType;

    public abstract ReadableObjectId findObjectId(Object obj, ObjectIdGenerator<?> objectIdGenerator, ObjectIdResolver objectIdResolver);

    public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;

    public abstract JsonDeserializer<Object> deserializerInstance(Annotated annotated, Object obj) throws JsonMappingException;

    public abstract KeyDeserializer keyDeserializerInstance(Annotated annotated, Object obj) throws JsonMappingException;

    protected DeserializationContext(DeserializerFactory df) {
        this(df, (DeserializerCache) null);
    }

    public DeserializationContext(DeserializerFactory df, DeserializerCache cache) {
        if (df == null) {
            throw new IllegalArgumentException("Cannot pass null DeserializerFactory");
        }
        this._factory = df;
        this._cache = cache == null ? new DeserializerCache() : cache;
        this._featureFlags = 0;
        this._config = null;
        this._injectableValues = null;
        this._view = null;
        this._attributes = null;
    }

    public DeserializationContext(DeserializationContext src, DeserializerFactory factory) {
        this._cache = src._cache;
        this._factory = factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._view = src._view;
        this._parser = src._parser;
        this._injectableValues = src._injectableValues;
        this._attributes = src._attributes;
    }

    public DeserializationContext(DeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues injectableValues) {
        this._cache = src._cache;
        this._factory = src._factory;
        this._config = config;
        this._featureFlags = config.getDeserializationFeatures();
        this._view = config.getActiveView();
        this._parser = p;
        this._injectableValues = injectableValues;
        this._attributes = config.getAttributes();
    }

    public DeserializationContext(DeserializationContext src) {
        this._cache = new DeserializerCache();
        this._factory = src._factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._view = src._view;
        this._injectableValues = null;
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public DeserializationConfig getConfig() {
        return this._config;
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final Class<?> getActiveView() {
        return this._view;
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final boolean canOverrideAccessModifiers() {
        return this._config.canOverrideAccessModifiers();
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final boolean isEnabled(MapperFeature feature) {
        return this._config.isEnabled(feature);
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
        return this._config.getDefaultPropertyFormat(baseType);
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public Locale getLocale() {
        return this._config.getLocale();
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public Object getAttribute(Object key) {
        return this._attributes.getAttribute(key);
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public DeserializationContext setAttribute(Object key, Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }

    public JavaType getContextualType() {
        if (this._currentType == null) {
            return null;
        }
        return this._currentType.value();
    }

    public DeserializerFactory getFactory() {
        return this._factory;
    }

    public final boolean isEnabled(DeserializationFeature feat) {
        return (this._featureFlags & feat.getMask()) != 0;
    }

    public final int getDeserializationFeatures() {
        return this._featureFlags;
    }

    public final boolean hasDeserializationFeatures(int featureMask) {
        return (this._featureFlags & featureMask) == featureMask;
    }

    public final boolean hasSomeOfFeatures(int featureMask) {
        return (this._featureFlags & featureMask) != 0;
    }

    public final JsonParser getParser() {
        return this._parser;
    }

    public final Object findInjectableValue(Object valueId, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
        if (this._injectableValues == null) {
            reportBadDefinition(ClassUtil.classOf(valueId), String.format("No 'injectableValues' configured, cannot inject value with id [%s]", valueId));
        }
        return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
    }

    public final Base64Variant getBase64Variant() {
        return this._config.getBase64Variant();
    }

    public final JsonNodeFactory getNodeFactory() {
        return this._config.getNodeFactory();
    }

    public boolean hasValueDeserializerFor(JavaType type, AtomicReference<Throwable> cause) {
        try {
            return this._cache.hasValueDeserializerFor(this, this._factory, type);
        } catch (JsonMappingException e) {
            if (cause != null) {
                cause.set(e);
                return false;
            }
            return false;
        } catch (RuntimeException e2) {
            if (cause == null) {
                throw e2;
            }
            cause.set(e2);
            return false;
        }
    }

    public final JsonDeserializer<Object> findContextualValueDeserializer(JavaType type, BeanProperty prop) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser != null) {
            deser = handleSecondaryContextualization(deser, prop, type);
        }
        return deser;
    }

    public final JsonDeserializer<Object> findNonContextualValueDeserializer(JavaType type) throws JsonMappingException {
        return this._cache.findValueDeserializer(this, this._factory, type);
    }

    public final JsonDeserializer<Object> findRootValueDeserializer(JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser == null) {
            return null;
        }
        JsonDeserializer<Object> deser2 = handleSecondaryContextualization(deser, null, type);
        TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
        if (typeDeser != null) {
            return new TypeWrappedDeserializer(typeDeser.forProperty(null), deser2);
        }
        return deser2;
    }

    public final KeyDeserializer findKeyDeserializer(JavaType keyType, BeanProperty prop) throws JsonMappingException {
        KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
        if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer) kd).createContextual(this, prop);
        }
        return kd;
    }

    public final JavaType constructType(Class<?> cls) {
        if (cls == null) {
            return null;
        }
        return this._config.constructType(cls);
    }

    public Class<?> findClass(String className) throws ClassNotFoundException {
        return getTypeFactory().findClass(className);
    }

    public final ObjectBuffer leaseObjectBuffer() {
        ObjectBuffer buf = this._objectBuffer;
        if (buf == null) {
            buf = new ObjectBuffer();
        } else {
            this._objectBuffer = null;
        }
        return buf;
    }

    public final void returnObjectBuffer(ObjectBuffer buf) {
        if (this._objectBuffer == null || buf.initialCapacity() >= this._objectBuffer.initialCapacity()) {
            this._objectBuffer = buf;
        }
    }

    public final ArrayBuilders getArrayBuilders() {
        if (this._arrayBuilders == null) {
            this._arrayBuilders = new ArrayBuilders();
        }
        return this._arrayBuilders;
    }

    public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode<>(type, this._currentType);
            try {
                deser = ((ContextualDeserializer) deser).createContextual(this, prop);
                this._currentType = this._currentType.next();
            } catch (Throwable th) {
                this._currentType = this._currentType.next();
                throw th;
            }
        }
        return deser;
    }

    public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode<>(type, this._currentType);
            try {
                deser = ((ContextualDeserializer) deser).createContextual(this, prop);
                this._currentType = this._currentType.next();
            } catch (Throwable th) {
                this._currentType = this._currentType.next();
                throw th;
            }
        }
        return deser;
    }

    public Date parseDate(String dateStr) throws IllegalArgumentException {
        try {
            DateFormat df = getDateFormat();
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", dateStr, ClassUtil.exceptionMessage(e)));
        }
    }

    public Calendar constructCalendar(Date d) {
        Calendar c = Calendar.getInstance(getTimeZone());
        c.setTime(d);
        return c;
    }

    public <T> T readValue(JsonParser p, Class<T> type) throws IOException {
        return (T) readValue(p, getTypeFactory().constructType(type));
    }

    public <T> T readValue(JsonParser p, JavaType type) throws IOException {
        JsonDeserializer<Object> deser = findRootValueDeserializer(type);
        if (deser == null) {
            reportBadDefinition(type, "Could not find JsonDeserializer for type " + type);
        }
        return (T) deser.deserialize(p, this);
    }

    public <T> T readPropertyValue(JsonParser p, BeanProperty prop, Class<T> type) throws IOException {
        return (T) readPropertyValue(p, prop, getTypeFactory().constructType(type));
    }

    public <T> T readPropertyValue(JsonParser p, BeanProperty prop, JavaType type) throws IOException {
        JsonDeserializer<Object> deser = findContextualValueDeserializer(type, prop);
        if (deser == null) {
            return (T) reportBadDefinition(type, String.format("Could not find JsonDeserializer for type %s (via property %s)", type, ClassUtil.nameOf(prop)));
        }
        return (T) deser.deserialize(p, this);
    }

    public boolean handleUnknownProperty(JsonParser p, JsonDeserializer<?> deser, Object instanceOrClass, String propName) throws IOException {
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                if (h.value().handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
                    return true;
                }
                problemHandlers = h.next();
            } else if (!isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                p.skipChildren();
                return true;
            } else {
                Collection<Object> propIds = deser == null ? null : deser.getKnownPropertyNames();
                throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, propIds);
            }
        }
    }

    public Object handleWeirdKey(Class<?> keyClass, String keyValue, String msg, Object... msgArgs) throws IOException {
        String msg2 = _format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object key = h.value().handleWeirdKey(this, keyClass, keyValue, msg2);
                if (key != DeserializationProblemHandler.NOT_HANDLED) {
                    if (key == null || keyClass.isInstance(key)) {
                        return key;
                    }
                    throw weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", keyClass, key.getClass()));
                }
                problemHandlers = h.next();
            } else {
                throw weirdKeyException(keyClass, keyValue, msg2);
            }
        }
    }

    public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs) throws IOException {
        String msg2 = _format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object instance = h.value().handleWeirdStringValue(this, targetClass, value, msg2);
                if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                    if (_isCompatible(targetClass, instance)) {
                        return instance;
                    }
                    throw weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", targetClass, instance.getClass()));
                }
                problemHandlers = h.next();
            } else {
                throw weirdStringException(value, targetClass, msg2);
            }
        }
    }

    public Object handleWeirdNumberValue(Class<?> targetClass, Number value, String msg, Object... msgArgs) throws IOException {
        String msg2 = _format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object key = h.value().handleWeirdNumberValue(this, targetClass, value, msg2);
                if (key != DeserializationProblemHandler.NOT_HANDLED) {
                    if (_isCompatible(targetClass, key)) {
                        return key;
                    }
                    throw weirdNumberException(value, targetClass, _format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", targetClass, key.getClass()));
                }
                problemHandlers = h.next();
            } else {
                throw weirdNumberException(value, targetClass, msg2);
            }
        }
    }

    public Object handleWeirdNativeValue(JavaType targetType, Object badValue, JsonParser p) throws IOException {
        Class<?> raw = targetType.getRawClass();
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            Object goodValue = h.value().handleWeirdNativeValue(this, targetType, badValue, p);
            if (goodValue != DeserializationProblemHandler.NOT_HANDLED) {
                if (goodValue == null || raw.isInstance(goodValue)) {
                    return goodValue;
                } else {
                    throw JsonMappingException.from(p, _format("DeserializationProblemHandler.handleWeirdNativeValue() for type %s returned value of type %s", targetType, goodValue.getClass()));
                }
            }
        }
        throw weirdNativeValueException(badValue, raw);
    }

    public Object handleMissingInstantiator(Class<?> instClass, ValueInstantiator valueInst, JsonParser p, String msg, Object... msgArgs) throws IOException {
        if (p == null) {
            p = getParser();
        }
        String msg2 = _format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object instance = h.value().handleMissingInstantiator(this, instClass, valueInst, p, msg2);
                if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                    if (_isCompatible(instClass, instance)) {
                        return instance;
                    }
                    reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", instClass, ClassUtil.classNameOf(instance)));
                }
                problemHandlers = h.next();
            } else if (valueInst != null && !valueInst.canInstantiate()) {
                return reportBadDefinition(constructType(instClass), String.format("Cannot construct instance of %s (no Creators, like default construct, exist): %s", ClassUtil.nameOf(instClass), msg2));
            } else {
                return reportInputMismatch(instClass, String.format("Cannot construct instance of %s (although at least one Creator exists): %s", ClassUtil.nameOf(instClass), msg2), new Object[0]);
            }
        }
    }

    public Object handleInstantiationProblem(Class<?> instClass, Object argument, Throwable t) throws IOException {
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object instance = h.value().handleInstantiationProblem(this, instClass, argument, t);
                if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                    if (_isCompatible(instClass, instance)) {
                        return instance;
                    }
                    reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", instClass, ClassUtil.classNameOf(instance)));
                }
                problemHandlers = h.next();
            } else {
                ClassUtil.throwIfIOE(t);
                throw instantiationException(instClass, t);
            }
        }
    }

    public Object handleUnexpectedToken(Class<?> instClass, JsonParser p) throws IOException {
        return handleUnexpectedToken(instClass, p.getCurrentToken(), p, null, new Object[0]);
    }

    public Object handleUnexpectedToken(Class<?> instClass, JsonToken t, JsonParser p, String msg, Object... msgArgs) throws IOException {
        String msg2 = _format(msg, msgArgs);
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                Object instance = h.value().handleUnexpectedToken(this, instClass, t, p, msg2);
                if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                    if (_isCompatible(instClass, instance)) {
                        return instance;
                    }
                    reportBadDefinition(constructType(instClass), String.format("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", ClassUtil.nameOf(instClass), ClassUtil.classNameOf(instance)));
                }
                problemHandlers = h.next();
            } else {
                if (msg2 == null) {
                    if (t == null) {
                        msg2 = String.format("Unexpected end-of-input when binding data into %s", ClassUtil.nameOf(instClass));
                    } else {
                        msg2 = String.format("Cannot deserialize instance of %s out of %s token", ClassUtil.nameOf(instClass), t);
                    }
                }
                reportInputMismatch(instClass, msg2, new Object[0]);
                return null;
            }
        }
    }

    public JavaType handleUnknownTypeId(JavaType baseType, String id, TypeIdResolver idResolver, String extraDesc) throws IOException {
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                JavaType type = h.value().handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
                if (type != null) {
                    if (type.hasRawClass(Void.class)) {
                        return null;
                    }
                    if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
                        return type;
                    }
                    throw invalidTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + type);
                }
                problemHandlers = h.next();
            } else if (!isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
                return null;
            } else {
                throw invalidTypeIdException(baseType, id, extraDesc);
            }
        }
    }

    public JavaType handleMissingTypeId(JavaType baseType, TypeIdResolver idResolver, String extraDesc) throws IOException {
        LinkedNode<DeserializationProblemHandler> problemHandlers = this._config.getProblemHandlers();
        while (true) {
            LinkedNode<DeserializationProblemHandler> h = problemHandlers;
            if (h != null) {
                JavaType type = h.value().handleMissingTypeId(this, baseType, idResolver, extraDesc);
                if (type != null) {
                    if (type.hasRawClass(Void.class)) {
                        return null;
                    }
                    if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
                        return type;
                    }
                    throw invalidTypeIdException(baseType, null, "problem handler tried to resolve into non-subtype: " + type);
                }
                problemHandlers = h.next();
            } else {
                throw missingTypeIdException(baseType, extraDesc);
            }
        }
    }

    protected boolean _isCompatible(Class<?> target, Object value) {
        if (value == null || target.isInstance(value)) {
            return true;
        }
        return target.isPrimitive() && ClassUtil.wrapperType(target).isInstance(value);
    }

    public void reportWrongTokenException(JsonDeserializer<?> deser, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
        throw wrongTokenException(getParser(), deser.handledType(), expToken, _format(msg, msgArgs));
    }

    public void reportWrongTokenException(JavaType targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
        throw wrongTokenException(getParser(), targetType, expToken, _format(msg, msgArgs));
    }

    public void reportWrongTokenException(Class<?> targetType, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
        throw wrongTokenException(getParser(), targetType, expToken, _format(msg, msgArgs));
    }

    public <T> T reportUnresolvedObjectId(ObjectIdReader oidReader, Object bean) throws JsonMappingException {
        String msg = String.format("No Object Id found for an instance of %s, to assign to property '%s'", ClassUtil.classNameOf(bean), oidReader.propertyName);
        return (T) reportInputMismatch(oidReader.idProperty, msg, new Object[0]);
    }

    public <T> T reportInputMismatch(BeanProperty prop, String msg, Object... msgArgs) throws JsonMappingException {
        String msg2 = _format(msg, msgArgs);
        JavaType type = prop == null ? null : prop.getType();
        throw MismatchedInputException.from(getParser(), type, msg2);
    }

    public <T> T reportInputMismatch(JsonDeserializer<?> src, String msg, Object... msgArgs) throws JsonMappingException {
        throw MismatchedInputException.from(getParser(), src.handledType(), _format(msg, msgArgs));
    }

    public <T> T reportInputMismatch(Class<?> targetType, String msg, Object... msgArgs) throws JsonMappingException {
        throw MismatchedInputException.from(getParser(), targetType, _format(msg, msgArgs));
    }

    public <T> T reportInputMismatch(JavaType targetType, String msg, Object... msgArgs) throws JsonMappingException {
        throw MismatchedInputException.from(getParser(), targetType, _format(msg, msgArgs));
    }

    public <T> T reportTrailingTokens(Class<?> targetType, JsonParser p, JsonToken trailingToken) throws JsonMappingException {
        throw MismatchedInputException.from(p, targetType, String.format("Trailing token (of type %s) found after value (bound as %s): not allowed as per `DeserializationFeature.FAIL_ON_TRAILING_TOKENS`", trailingToken, ClassUtil.nameOf(targetType)));
    }

    @Deprecated
    public void reportWrongTokenException(JsonParser p, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
        throw wrongTokenException(p, expToken, _format(msg, msgArgs));
    }

    @Deprecated
    public void reportUnknownProperty(Object instanceOrClass, String fieldName, JsonDeserializer<?> deser) throws JsonMappingException {
        if (isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            Collection<Object> propIds = deser == null ? null : deser.getKnownPropertyNames();
            throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
        }
    }

    @Deprecated
    public void reportMissingContent(String msg, Object... msgArgs) throws JsonMappingException {
        throw MismatchedInputException.from(getParser(), (JavaType) null, "No content to map due to end-of-input");
    }

    public <T> T reportBadTypeDefinition(BeanDescription bean, String msg, Object... msgArgs) throws JsonMappingException {
        String msg2 = _format(msg, msgArgs);
        String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        throw InvalidDefinitionException.from(this._parser, String.format("Invalid type definition for type %s: %s", beanDesc, msg2), bean, (BeanPropertyDefinition) null);
    }

    public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String msg, Object... msgArgs) throws JsonMappingException {
        String msg2 = _format(msg, msgArgs);
        String propName = ClassUtil.nameOf(prop);
        String beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        throw InvalidDefinitionException.from(this._parser, String.format("Invalid definition for property %s (of type %s): %s", propName, beanDesc, msg2), bean, prop);
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public <T> T reportBadDefinition(JavaType type, String msg) throws JsonMappingException {
        throw InvalidDefinitionException.from(this._parser, msg, type);
    }

    public <T> T reportBadMerge(JsonDeserializer<?> deser) throws JsonMappingException {
        if (isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
            return null;
        }
        JavaType type = constructType(deser.handledType());
        String msg = String.format("Invalid configuration: values of type %s cannot be merged", type);
        throw InvalidDefinitionException.from(getParser(), msg, type);
    }

    public JsonMappingException wrongTokenException(JsonParser p, JavaType targetType, JsonToken expToken, String extra) {
        String msg = String.format("Unexpected token (%s), expected %s", p.getCurrentToken(), expToken);
        return MismatchedInputException.from(p, targetType, _colonConcat(msg, extra));
    }

    public JsonMappingException wrongTokenException(JsonParser p, Class<?> targetType, JsonToken expToken, String extra) {
        String msg = String.format("Unexpected token (%s), expected %s", p.getCurrentToken(), expToken);
        return MismatchedInputException.from(p, targetType, _colonConcat(msg, extra));
    }

    @Deprecated
    public JsonMappingException wrongTokenException(JsonParser p, JsonToken expToken, String msg) {
        return wrongTokenException(p, (JavaType) null, expToken, msg);
    }

    public JsonMappingException weirdKeyException(Class<?> keyClass, String keyValue, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize Map key of type %s from String %s: %s", ClassUtil.nameOf(keyClass), _quotedString(keyValue), msg), keyValue, keyClass);
    }

    public JsonMappingException weirdStringException(String value, Class<?> instClass, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from String %s: %s", ClassUtil.nameOf(instClass), _quotedString(value), msg), value, instClass);
    }

    public JsonMappingException weirdNumberException(Number value, Class<?> instClass, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from number %s: %s", ClassUtil.nameOf(instClass), String.valueOf(value), msg), value, instClass);
    }

    public JsonMappingException weirdNativeValueException(Object value, Class<?> instClass) {
        return InvalidFormatException.from(this._parser, String.format("Cannot deserialize value of type %s from native value (`JsonToken.VALUE_EMBEDDED_OBJECT`) of type %s: incompatible types", ClassUtil.nameOf(instClass), ClassUtil.classNameOf(value)), value, instClass);
    }

    public JsonMappingException instantiationException(Class<?> instClass, Throwable cause) {
        String excMsg;
        JavaType type = constructType(instClass);
        if (cause == null) {
            excMsg = "N/A";
        } else {
            String exceptionMessage = ClassUtil.exceptionMessage(cause);
            excMsg = exceptionMessage;
            if (exceptionMessage == null) {
                excMsg = ClassUtil.nameOf(cause.getClass());
            }
        }
        String msg = String.format("Cannot construct instance of %s, problem: %s", ClassUtil.nameOf(instClass), excMsg);
        InvalidDefinitionException e = InvalidDefinitionException.from(this._parser, msg, type);
        e.initCause(cause);
        return e;
    }

    public JsonMappingException instantiationException(Class<?> instClass, String msg0) {
        JavaType type = constructType(instClass);
        String msg = String.format("Cannot construct instance of %s: %s", ClassUtil.nameOf(instClass), msg0);
        return InvalidDefinitionException.from(this._parser, msg, type);
    }

    @Override // com.fasterxml.jackson.databind.DatabindContext
    public JsonMappingException invalidTypeIdException(JavaType baseType, String typeId, String extraDesc) {
        String msg = String.format("Could not resolve type id '%s' as a subtype of %s", typeId, baseType);
        return InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, typeId);
    }

    public JsonMappingException missingTypeIdException(JavaType baseType, String extraDesc) {
        String msg = String.format("Missing type id when trying to resolve subtype of %s", baseType);
        return InvalidTypeIdException.from(this._parser, _colonConcat(msg, extraDesc), baseType, null);
    }

    @Deprecated
    public JsonMappingException unknownTypeException(JavaType type, String id, String extraDesc) {
        String msg = String.format("Could not resolve type id '%s' into a subtype of %s", id, type);
        return MismatchedInputException.from(this._parser, type, _colonConcat(msg, extraDesc));
    }

    @Deprecated
    public JsonMappingException endOfInputException(Class<?> instClass) {
        return MismatchedInputException.from(this._parser, instClass, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
    }

    @Deprecated
    public void reportMappingException(String msg, Object... msgArgs) throws JsonMappingException {
        throw JsonMappingException.from(getParser(), _format(msg, msgArgs));
    }

    @Deprecated
    public JsonMappingException mappingException(String message) {
        return JsonMappingException.from(getParser(), message);
    }

    @Deprecated
    public JsonMappingException mappingException(String msg, Object... msgArgs) {
        return JsonMappingException.from(getParser(), _format(msg, msgArgs));
    }

    @Deprecated
    public JsonMappingException mappingException(Class<?> targetClass) {
        return mappingException(targetClass, this._parser.getCurrentToken());
    }

    @Deprecated
    public JsonMappingException mappingException(Class<?> targetClass, JsonToken token) {
        return JsonMappingException.from(this._parser, String.format("Cannot deserialize instance of %s out of %s token", ClassUtil.nameOf(targetClass), token));
    }

    protected DateFormat getDateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = (DateFormat) this._config.getDateFormat().clone();
        this._dateFormat = df;
        return df;
    }
}