package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.filter.FilteringParserDelegate;
import com.fasterxml.jackson.core.filter.JsonPointerBasedFilter;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectReader.class */
public class ObjectReader extends ObjectCodec implements Versioned, Serializable {
    private static final long serialVersionUID = 2;
    private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
    protected final DeserializationConfig _config;
    protected final DefaultDeserializationContext _context;
    protected final JsonFactory _parserFactory;
    protected final boolean _unwrapRoot;
    private final TokenFilter _filter;
    protected final JavaType _valueType;
    protected final JsonDeserializer<Object> _rootDeserializer;
    protected final Object _valueToUpdate;
    protected final FormatSchema _schema;
    protected final InjectableValues _injectableValues;
    protected final DataFormatReaders _dataFormatReaders;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectReader(ObjectMapper mapper, DeserializationConfig config) {
        this(mapper, config, null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues) {
        this._config = config;
        this._context = mapper._deserializationContext;
        this._rootDeserializers = mapper._rootDeserializers;
        this._parserFactory = mapper._jsonFactory;
        this._valueType = valueType;
        this._valueToUpdate = valueToUpdate;
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._rootDeserializer = _prefetchRootDeserializer(valueType);
        this._dataFormatReaders = null;
        this._filter = null;
    }

    protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = valueType;
        this._rootDeserializer = rootDeser;
        this._valueToUpdate = valueToUpdate;
        this._schema = schema;
        this._injectableValues = injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = dataFormatReaders;
        this._filter = base._filter;
    }

    protected ObjectReader(ObjectReader base, DeserializationConfig config) {
        this._config = config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = config.useRootWrapping();
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = base._filter;
    }

    protected ObjectReader(ObjectReader base, JsonFactory f) {
        this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = f;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = base._unwrapRoot;
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = base._filter;
    }

    protected ObjectReader(ObjectReader base, TokenFilter filter) {
        this._config = base._config;
        this._context = base._context;
        this._rootDeserializers = base._rootDeserializers;
        this._parserFactory = base._parserFactory;
        this._valueType = base._valueType;
        this._rootDeserializer = base._rootDeserializer;
        this._valueToUpdate = base._valueToUpdate;
        this._schema = base._schema;
        this._injectableValues = base._injectableValues;
        this._unwrapRoot = base._unwrapRoot;
        this._dataFormatReaders = base._dataFormatReaders;
        this._filter = filter;
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    protected ObjectReader _new(ObjectReader base, JsonFactory f) {
        return new ObjectReader(base, f);
    }

    protected ObjectReader _new(ObjectReader base, DeserializationConfig config) {
        return new ObjectReader(base, config);
    }

    protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders) {
        return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
    }

    protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged) {
        return new MappingIterator<>(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
    }

    protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p) throws IOException {
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        this._config.initialize(p);
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                ctxt.reportInputMismatch(this._valueType, "No content to map due to end-of-input", new Object[0]);
            }
        }
        return t;
    }

    protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p) throws IOException {
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        this._config.initialize(p);
    }

    public ObjectReader with(DeserializationFeature feature) {
        return _with(this._config.with(feature));
    }

    public ObjectReader with(DeserializationFeature first, DeserializationFeature... other) {
        return _with(this._config.with(first, other));
    }

    public ObjectReader withFeatures(DeserializationFeature... features) {
        return _with(this._config.withFeatures(features));
    }

    public ObjectReader without(DeserializationFeature feature) {
        return _with(this._config.without(feature));
    }

    public ObjectReader without(DeserializationFeature first, DeserializationFeature... other) {
        return _with(this._config.without(first, other));
    }

    public ObjectReader withoutFeatures(DeserializationFeature... features) {
        return _with(this._config.withoutFeatures(features));
    }

    public ObjectReader with(JsonParser.Feature feature) {
        return _with(this._config.with(feature));
    }

    public ObjectReader withFeatures(JsonParser.Feature... features) {
        return _with(this._config.withFeatures(features));
    }

    public ObjectReader without(JsonParser.Feature feature) {
        return _with(this._config.without(feature));
    }

    public ObjectReader withoutFeatures(JsonParser.Feature... features) {
        return _with(this._config.withoutFeatures(features));
    }

    public ObjectReader with(FormatFeature feature) {
        return _with(this._config.with(feature));
    }

    public ObjectReader withFeatures(FormatFeature... features) {
        return _with(this._config.withFeatures(features));
    }

    public ObjectReader without(FormatFeature feature) {
        return _with(this._config.without(feature));
    }

    public ObjectReader withoutFeatures(FormatFeature... features) {
        return _with(this._config.withoutFeatures(features));
    }

    public ObjectReader at(String value) {
        return new ObjectReader(this, new JsonPointerBasedFilter(value));
    }

    public ObjectReader at(JsonPointer pointer) {
        return new ObjectReader(this, new JsonPointerBasedFilter(pointer));
    }

    public ObjectReader with(DeserializationConfig config) {
        return _with(config);
    }

    public ObjectReader with(InjectableValues injectableValues) {
        if (this._injectableValues == injectableValues) {
            return this;
        }
        return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
    }

    public ObjectReader with(JsonNodeFactory f) {
        return _with(this._config.with(f));
    }

    public ObjectReader with(JsonFactory f) {
        if (f == this._parserFactory) {
            return this;
        }
        ObjectReader r = _new(this, f);
        if (f.getCodec() == null) {
            f.setCodec(r);
        }
        return r;
    }

    public ObjectReader withRootName(String rootName) {
        return _with(this._config.withRootName(rootName));
    }

    public ObjectReader withRootName(PropertyName rootName) {
        return _with(this._config.withRootName(rootName));
    }

    public ObjectReader withoutRootName() {
        return _with(this._config.withRootName(PropertyName.NO_NAME));
    }

    public ObjectReader with(FormatSchema schema) {
        if (this._schema == schema) {
            return this;
        }
        _verifySchemaType(schema);
        return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
    }

    public ObjectReader forType(JavaType valueType) {
        if (valueType != null && valueType.equals(this._valueType)) {
            return this;
        }
        JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(valueType);
        DataFormatReaders det = this._dataFormatReaders;
        if (det != null) {
            det = det.withType(valueType);
        }
        return _new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
    }

    public ObjectReader forType(Class<?> valueType) {
        return forType(this._config.constructType(valueType));
    }

    public ObjectReader forType(TypeReference<?> valueTypeRef) {
        return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
    }

    @Deprecated
    public ObjectReader withType(JavaType valueType) {
        return forType(valueType);
    }

    @Deprecated
    public ObjectReader withType(Class<?> valueType) {
        return forType(this._config.constructType(valueType));
    }

    @Deprecated
    public ObjectReader withType(Type valueType) {
        return forType(this._config.getTypeFactory().constructType(valueType));
    }

    @Deprecated
    public ObjectReader withType(TypeReference<?> valueTypeRef) {
        return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
    }

    public ObjectReader withValueToUpdate(Object value) {
        JavaType t;
        if (value == this._valueToUpdate) {
            return this;
        }
        if (value == null) {
            return _new(this, this._config, this._valueType, this._rootDeserializer, null, this._schema, this._injectableValues, this._dataFormatReaders);
        }
        if (this._valueType == null) {
            t = this._config.constructType(value.getClass());
        } else {
            t = this._valueType;
        }
        return _new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
    }

    public ObjectReader withView(Class<?> activeView) {
        return _with(this._config.withView(activeView));
    }

    public ObjectReader with(Locale l) {
        return _with(this._config.with(l));
    }

    public ObjectReader with(TimeZone tz) {
        return _with(this._config.with(tz));
    }

    public ObjectReader withHandler(DeserializationProblemHandler h) {
        return _with(this._config.withHandler(h));
    }

    public ObjectReader with(Base64Variant defaultBase64) {
        return _with(this._config.with(defaultBase64));
    }

    public ObjectReader withFormatDetection(ObjectReader... readers) {
        return withFormatDetection(new DataFormatReaders(readers));
    }

    public ObjectReader withFormatDetection(DataFormatReaders readers) {
        return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
    }

    public ObjectReader with(ContextAttributes attrs) {
        return _with(this._config.with(attrs));
    }

    public ObjectReader withAttributes(Map<?, ?> attrs) {
        return _with(this._config.withAttributes(attrs));
    }

    public ObjectReader withAttribute(Object key, Object value) {
        return _with(this._config.withAttribute(key, value));
    }

    public ObjectReader withoutAttribute(Object key) {
        return _with(this._config.withoutAttribute(key));
    }

    protected ObjectReader _with(DeserializationConfig newConfig) {
        if (newConfig == this._config) {
            return this;
        }
        ObjectReader r = _new(this, newConfig);
        if (this._dataFormatReaders != null) {
            r = r.withFormatDetection(this._dataFormatReaders.with(newConfig));
        }
        return r;
    }

    public boolean isEnabled(DeserializationFeature f) {
        return this._config.isEnabled(f);
    }

    public boolean isEnabled(MapperFeature f) {
        return this._config.isEnabled(f);
    }

    public boolean isEnabled(JsonParser.Feature f) {
        return this._parserFactory.isEnabled(f);
    }

    public DeserializationConfig getConfig() {
        return this._config;
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public JsonFactory getFactory() {
        return this._parserFactory;
    }

    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }

    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }

    public InjectableValues getInjectableValues() {
        return this._injectableValues;
    }

    public <T> T readValue(JsonParser p) throws IOException {
        return (T) _bind(p, this._valueToUpdate);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException {
        return (T) forType((Class<?>) valueType).readValue(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef) throws IOException {
        return (T) forType(valueTypeRef).readValue(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException {
        return (T) forType((JavaType) valueType).readValue(p);
    }

    public <T> T readValue(JsonParser p, JavaType valueType) throws IOException {
        return (T) forType(valueType).readValue(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
        return forType((Class<?>) valueType).readValues(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> Iterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef) throws IOException {
        return forType(valueTypeRef).readValues(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
        return readValues(p, (JavaType) valueType);
    }

    public <T> Iterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
        return forType(valueType).readValues(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public JsonNode createArrayNode() {
        return this._config.getNodeFactory().arrayNode();
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public JsonNode createObjectNode() {
        return this._config.getNodeFactory().objectNode();
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public JsonParser treeAsTokens(TreeNode n) {
        ObjectReader codec = withValueToUpdate(null);
        return new TreeTraversingParser((JsonNode) n, codec);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
        return _bindAsTree(p);
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec, com.fasterxml.jackson.core.TreeCodec
    public void writeTree(JsonGenerator g, TreeNode rootNode) {
        throw new UnsupportedOperationException();
    }

    public <T> T readValue(InputStream src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T) _detectBindAndClose(this._dataFormatReaders.findFormat(src), false);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(Reader src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(String src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(byte[] src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T) _detectBindAndClose(src, 0, src.length);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(byte[] src, int offset, int length) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T) _detectBindAndClose(src, offset, length);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src, offset, length), false));
    }

    public <T> T readValue(File src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T) _detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(URL src) throws IOException {
        if (this._dataFormatReaders != null) {
            return (T) _detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> T readValue(JsonNode src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return (T) _bindAndClose(_considerFilter(treeAsTokens(src), false));
    }

    public <T> T readValue(DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return (T) _bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public JsonNode readTree(InputStream in) throws IOException {
        if (this._dataFormatReaders != null) {
            return _detectBindAndCloseAsTree(in);
        }
        return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(in), false));
    }

    public JsonNode readTree(Reader r) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(r);
        }
        return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(r), false));
    }

    public JsonNode readTree(String json) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(json);
        }
        return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(json), false));
    }

    public JsonNode readTree(DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(src), false));
    }

    public <T> MappingIterator<T> readValues(JsonParser p) throws IOException {
        DeserializationContext ctxt = createDeserializationContext(p);
        return _newIterator(p, ctxt, _findRootDeserializer(ctxt), false);
    }

    public <T> MappingIterator<T> readValues(InputStream src) throws IOException {
        if (this._dataFormatReaders != null) {
            return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false);
        }
        return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
    }

    public <T> MappingIterator<T> readValues(Reader src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        JsonParser p = _considerFilter(this._parserFactory.createParser(src), true);
        DeserializationContext ctxt = createDeserializationContext(p);
        _initForMultiRead(ctxt, p);
        p.nextToken();
        return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
    }

    public <T> MappingIterator<T> readValues(String json) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(json);
        }
        JsonParser p = _considerFilter(this._parserFactory.createParser(json), true);
        DeserializationContext ctxt = createDeserializationContext(p);
        _initForMultiRead(ctxt, p);
        p.nextToken();
        return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
    }

    public <T> MappingIterator<T> readValues(byte[] src, int offset, int length) throws IOException {
        if (this._dataFormatReaders != null) {
            return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false);
        }
        return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src, offset, length), true));
    }

    public final <T> MappingIterator<T> readValues(byte[] src) throws IOException {
        return readValues(src, 0, src.length);
    }

    public <T> MappingIterator<T> readValues(File src) throws IOException {
        if (this._dataFormatReaders != null) {
            return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), false);
        }
        return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
    }

    public <T> MappingIterator<T> readValues(URL src) throws IOException {
        if (this._dataFormatReaders != null) {
            return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), true);
        }
        return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
    }

    public <T> MappingIterator<T> readValues(DataInput src) throws IOException {
        if (this._dataFormatReaders != null) {
            _reportUndetectableSource(src);
        }
        return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
        try {
            return (T) readValue(treeAsTokens(n), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    @Override // com.fasterxml.jackson.core.ObjectCodec
    public void writeValue(JsonGenerator gen, Object value) throws IOException {
        throw new UnsupportedOperationException("Not implemented for ObjectReader");
    }

    protected Object _bind(JsonParser p, Object valueToUpdate) throws IOException {
        Object result;
        DeserializationContext ctxt = createDeserializationContext(p);
        JsonToken t = _initForReading(ctxt, p);
        if (t == JsonToken.VALUE_NULL) {
            if (valueToUpdate == null) {
                result = _findRootDeserializer(ctxt).getNullValue(ctxt);
            } else {
                result = valueToUpdate;
            }
        } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = valueToUpdate;
        } else {
            JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
            if (this._unwrapRoot) {
                result = _unwrapAndDeserialize(p, ctxt, this._valueType, deser);
            } else if (valueToUpdate == null) {
                result = deser.deserialize(p, ctxt);
            } else {
                result = deser.deserialize(p, ctxt, valueToUpdate);
            }
        }
        p.clearCurrentToken();
        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            _verifyNoTrailingTokens(p, ctxt, this._valueType);
        }
        return result;
    }

    protected Object _bindAndClose(JsonParser p0) throws IOException {
        Object result;
        Throwable th = null;
        try {
            DeserializationContext ctxt = createDeserializationContext(p0);
            JsonToken t = _initForReading(ctxt, p0);
            if (t == JsonToken.VALUE_NULL) {
                if (this._valueToUpdate == null) {
                    result = _findRootDeserializer(ctxt).getNullValue(ctxt);
                } else {
                    result = this._valueToUpdate;
                }
            } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = this._valueToUpdate;
            } else {
                JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
                if (this._unwrapRoot) {
                    result = _unwrapAndDeserialize(p0, ctxt, this._valueType, deser);
                } else if (this._valueToUpdate == null) {
                    result = deser.deserialize(p0, ctxt);
                } else {
                    deser.deserialize(p0, ctxt, this._valueToUpdate);
                    result = this._valueToUpdate;
                }
            }
            if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                _verifyNoTrailingTokens(p0, ctxt, this._valueType);
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

    protected final JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException {
        Throwable th = null;
        try {
            JsonNode _bindAsTree = _bindAsTree(p0);
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
            return _bindAsTree;
        } finally {
        }
    }

    protected final JsonNode _bindAsTree(JsonParser p) throws IOException {
        Object result;
        this._config.initialize(p);
        if (this._schema != null) {
            p.setSchema(this._schema);
        }
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return null;
            }
        }
        DeserializationContext ctxt = createDeserializationContext(p);
        if (t == JsonToken.VALUE_NULL) {
            return ctxt.getNodeFactory().nullNode();
        }
        JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
        if (this._unwrapRoot) {
            result = _unwrapAndDeserialize(p, ctxt, JSON_NODE_TYPE, deser);
        } else {
            result = deser.deserialize(p, ctxt);
            if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                _verifyNoTrailingTokens(p, ctxt, JSON_NODE_TYPE);
            }
        }
        return (JsonNode) result;
    }

    protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p) throws IOException {
        DeserializationContext ctxt = createDeserializationContext(p);
        _initForMultiRead(ctxt, p);
        p.nextToken();
        return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
    }

    protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser) throws IOException {
        Object result;
        PropertyName expRootName = this._config.findRootName(rootType);
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
        if (this._valueToUpdate == null) {
            result = deser.deserialize(p, ctxt);
        } else {
            deser.deserialize(p, ctxt, this._valueToUpdate);
            result = this._valueToUpdate;
        }
        if (p.nextToken() != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (this._config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            _verifyNoTrailingTokens(p, ctxt, this._valueType);
        }
        return result;
    }

    protected JsonParser _considerFilter(JsonParser p, boolean multiValue) {
        return (this._filter == null || FilteringParserDelegate.class.isInstance(p)) ? p : new FilteringParserDelegate(p, this._filter, false, multiValue);
    }

    protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
        JsonToken t = p.nextToken();
        if (t != null) {
            Class<?> bt = ClassUtil.rawClass(bindType);
            if (bt == null && this._valueToUpdate != null) {
                bt = this._valueToUpdate.getClass();
            }
            ctxt.reportTrailingTokens(bt, p, t);
        }
    }

    protected Object _detectBindAndClose(byte[] src, int offset, int length) throws IOException {
        DataFormatReaders.Match match = this._dataFormatReaders.findFormat(src, offset, length);
        if (!match.hasMatch()) {
            _reportUnkownFormat(this._dataFormatReaders, match);
        }
        JsonParser p = match.createParserWithMatch();
        return match.getReader()._bindAndClose(p);
    }

    protected Object _detectBindAndClose(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
        if (!match.hasMatch()) {
            _reportUnkownFormat(this._dataFormatReaders, match);
        }
        JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndClose(p);
    }

    protected <T> MappingIterator<T> _detectBindAndReadValues(DataFormatReaders.Match match, boolean forceClosing) throws IOException {
        if (!match.hasMatch()) {
            _reportUnkownFormat(this._dataFormatReaders, match);
        }
        JsonParser p = match.createParserWithMatch();
        if (forceClosing) {
            p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        }
        return match.getReader()._bindAndReadValues(p);
    }

    protected JsonNode _detectBindAndCloseAsTree(InputStream in) throws IOException {
        DataFormatReaders.Match match = this._dataFormatReaders.findFormat(in);
        if (!match.hasMatch()) {
            _reportUnkownFormat(this._dataFormatReaders, match);
        }
        JsonParser p = match.createParserWithMatch();
        p.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return match.getReader()._bindAndCloseAsTree(p);
    }

    protected void _reportUnkownFormat(DataFormatReaders detector, DataFormatReaders.Match match) throws JsonProcessingException {
        throw new JsonParseException((JsonParser) null, "Cannot detect format from input, does not look like any of detectable formats " + detector.toString());
    }

    protected void _verifySchemaType(FormatSchema schema) {
        if (schema != null && !this._parserFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName());
        }
    }

    protected DefaultDeserializationContext createDeserializationContext(JsonParser p) {
        return this._context.createInstance(this._config, p, this._injectableValues);
    }

    protected InputStream _inputStream(URL src) throws IOException {
        return src.openStream();
    }

    protected InputStream _inputStream(File f) throws IOException {
        return new FileInputStream(f);
    }

    protected void _reportUndetectableSource(Object src) throws JsonProcessingException {
        throw new JsonParseException((JsonParser) null, "Cannot use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
    }

    protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt) throws JsonMappingException {
        if (this._rootDeserializer != null) {
            return this._rootDeserializer;
        }
        JavaType t = this._valueType;
        if (t == null) {
            ctxt.reportBadDefinition((JavaType) null, "No value type configured for ObjectReader");
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(t);
        if (deser != null) {
            return deser;
        }
        JsonDeserializer<Object> deser2 = ctxt.findRootValueDeserializer(t);
        if (deser2 == null) {
            ctxt.reportBadDefinition(t, "Cannot find a deserializer for type " + t);
        }
        this._rootDeserializers.put(t, deser2);
        return deser2;
    }

    protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._rootDeserializers.get(JSON_NODE_TYPE);
        if (deser == null) {
            deser = ctxt.findRootValueDeserializer(JSON_NODE_TYPE);
            if (deser == null) {
                ctxt.reportBadDefinition(JSON_NODE_TYPE, "Cannot find a deserializer for type " + JSON_NODE_TYPE);
            }
            this._rootDeserializers.put(JSON_NODE_TYPE, deser);
        }
        return deser;
    }

    protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType) {
        if (valueType == null || !this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH)) {
            return null;
        }
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser == null) {
            try {
                DeserializationContext ctxt = createDeserializationContext(null);
                deser = ctxt.findRootValueDeserializer(valueType);
                if (deser != null) {
                    this._rootDeserializers.put(valueType, deser);
                }
                return deser;
            } catch (JsonProcessingException e) {
            }
        }
        return deser;
    }
}