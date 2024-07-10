package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectWriter.class */
public class ObjectWriter implements Versioned, Serializable {
    private static final long serialVersionUID = 1;
    protected static final PrettyPrinter NULL_PRETTY_PRINTER = new MinimalPrettyPrinter();
    protected final SerializationConfig _config;
    protected final DefaultSerializerProvider _serializerProvider;
    protected final SerializerFactory _serializerFactory;
    protected final JsonFactory _generatorFactory;
    protected final GeneratorSettings _generatorSettings;
    protected final Prefetch _prefetch;

    public ObjectWriter(ObjectMapper mapper, SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = pp == null ? GeneratorSettings.empty : new GeneratorSettings(pp, null, null, null);
        if (rootType == null || rootType.hasRawClass(Object.class)) {
            this._prefetch = Prefetch.empty;
        } else {
            this._prefetch = Prefetch.empty.forRootType(this, rootType.withStaticTyping());
        }
    }

    public ObjectWriter(ObjectMapper mapper, SerializationConfig config) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = GeneratorSettings.empty;
        this._prefetch = Prefetch.empty;
    }

    public ObjectWriter(ObjectMapper mapper, SerializationConfig config, FormatSchema s) {
        this._config = config;
        this._serializerProvider = mapper._serializerProvider;
        this._serializerFactory = mapper._serializerFactory;
        this._generatorFactory = mapper._jsonFactory;
        this._generatorSettings = s == null ? GeneratorSettings.empty : new GeneratorSettings(null, s, null, null);
        this._prefetch = Prefetch.empty;
    }

    protected ObjectWriter(ObjectWriter base, SerializationConfig config, GeneratorSettings genSettings, Prefetch prefetch) {
        this._config = config;
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._generatorSettings = genSettings;
        this._prefetch = prefetch;
    }

    protected ObjectWriter(ObjectWriter base, SerializationConfig config) {
        this._config = config;
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = base._generatorFactory;
        this._generatorSettings = base._generatorSettings;
        this._prefetch = base._prefetch;
    }

    protected ObjectWriter(ObjectWriter base, JsonFactory f) {
        this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
        this._serializerProvider = base._serializerProvider;
        this._serializerFactory = base._serializerFactory;
        this._generatorFactory = f;
        this._generatorSettings = base._generatorSettings;
        this._prefetch = base._prefetch;
    }

    @Override // com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    protected ObjectWriter _new(ObjectWriter base, JsonFactory f) {
        return new ObjectWriter(base, f);
    }

    protected ObjectWriter _new(ObjectWriter base, SerializationConfig config) {
        if (config == this._config) {
            return this;
        }
        return new ObjectWriter(base, config);
    }

    protected ObjectWriter _new(GeneratorSettings genSettings, Prefetch prefetch) {
        if (this._generatorSettings == genSettings && this._prefetch == prefetch) {
            return this;
        }
        return new ObjectWriter(this, this._config, genSettings, prefetch);
    }

    protected SequenceWriter _newSequenceWriter(boolean wrapInArray, JsonGenerator gen, boolean managedInput) throws IOException {
        _configureGenerator(gen);
        return new SequenceWriter(_serializerProvider(), gen, managedInput, this._prefetch).init(wrapInArray);
    }

    public ObjectWriter with(SerializationFeature feature) {
        return _new(this, this._config.with(feature));
    }

    public ObjectWriter with(SerializationFeature first, SerializationFeature... other) {
        return _new(this, this._config.with(first, other));
    }

    public ObjectWriter withFeatures(SerializationFeature... features) {
        return _new(this, this._config.withFeatures(features));
    }

    public ObjectWriter without(SerializationFeature feature) {
        return _new(this, this._config.without(feature));
    }

    public ObjectWriter without(SerializationFeature first, SerializationFeature... other) {
        return _new(this, this._config.without(first, other));
    }

    public ObjectWriter withoutFeatures(SerializationFeature... features) {
        return _new(this, this._config.withoutFeatures(features));
    }

    public ObjectWriter with(JsonGenerator.Feature feature) {
        return _new(this, this._config.with(feature));
    }

    public ObjectWriter withFeatures(JsonGenerator.Feature... features) {
        return _new(this, this._config.withFeatures(features));
    }

    public ObjectWriter without(JsonGenerator.Feature feature) {
        return _new(this, this._config.without(feature));
    }

    public ObjectWriter withoutFeatures(JsonGenerator.Feature... features) {
        return _new(this, this._config.withoutFeatures(features));
    }

    public ObjectWriter with(FormatFeature feature) {
        return _new(this, this._config.with(feature));
    }

    public ObjectWriter withFeatures(FormatFeature... features) {
        return _new(this, this._config.withFeatures(features));
    }

    public ObjectWriter without(FormatFeature feature) {
        return _new(this, this._config.without(feature));
    }

    public ObjectWriter withoutFeatures(FormatFeature... features) {
        return _new(this, this._config.withoutFeatures(features));
    }

    public ObjectWriter forType(JavaType rootType) {
        return _new(this._generatorSettings, this._prefetch.forRootType(this, rootType));
    }

    public ObjectWriter forType(Class<?> rootType) {
        if (rootType == Object.class) {
            return forType((JavaType) null);
        }
        return forType(this._config.constructType(rootType));
    }

    public ObjectWriter forType(TypeReference<?> rootType) {
        return forType(this._config.getTypeFactory().constructType(rootType.getType()));
    }

    @Deprecated
    public ObjectWriter withType(JavaType rootType) {
        return forType(rootType);
    }

    @Deprecated
    public ObjectWriter withType(Class<?> rootType) {
        return forType(rootType);
    }

    @Deprecated
    public ObjectWriter withType(TypeReference<?> rootType) {
        return forType(rootType);
    }

    public ObjectWriter with(DateFormat df) {
        return _new(this, this._config.with(df));
    }

    public ObjectWriter withDefaultPrettyPrinter() {
        return with(this._config.getDefaultPrettyPrinter());
    }

    public ObjectWriter with(FilterProvider filterProvider) {
        if (filterProvider == this._config.getFilterProvider()) {
            return this;
        }
        return _new(this, this._config.withFilters(filterProvider));
    }

    public ObjectWriter with(PrettyPrinter pp) {
        return _new(this._generatorSettings.with(pp), this._prefetch);
    }

    public ObjectWriter withRootName(String rootName) {
        return _new(this, this._config.withRootName(rootName));
    }

    public ObjectWriter withRootName(PropertyName rootName) {
        return _new(this, this._config.withRootName(rootName));
    }

    public ObjectWriter withoutRootName() {
        return _new(this, this._config.withRootName(PropertyName.NO_NAME));
    }

    public ObjectWriter with(FormatSchema schema) {
        _verifySchemaType(schema);
        return _new(this._generatorSettings.with(schema), this._prefetch);
    }

    @Deprecated
    public ObjectWriter withSchema(FormatSchema schema) {
        return with(schema);
    }

    public ObjectWriter withView(Class<?> view) {
        return _new(this, this._config.withView(view));
    }

    public ObjectWriter with(Locale l) {
        return _new(this, this._config.with(l));
    }

    public ObjectWriter with(TimeZone tz) {
        return _new(this, this._config.with(tz));
    }

    public ObjectWriter with(Base64Variant b64variant) {
        return _new(this, this._config.with(b64variant));
    }

    public ObjectWriter with(CharacterEscapes escapes) {
        return _new(this._generatorSettings.with(escapes), this._prefetch);
    }

    public ObjectWriter with(JsonFactory f) {
        return f == this._generatorFactory ? this : _new(this, f);
    }

    public ObjectWriter with(ContextAttributes attrs) {
        return _new(this, this._config.with(attrs));
    }

    public ObjectWriter withAttributes(Map<?, ?> attrs) {
        return _new(this, this._config.withAttributes(attrs));
    }

    public ObjectWriter withAttribute(Object key, Object value) {
        return _new(this, this._config.withAttribute(key, value));
    }

    public ObjectWriter withoutAttribute(Object key) {
        return _new(this, this._config.withoutAttribute(key));
    }

    public ObjectWriter withRootValueSeparator(String sep) {
        return _new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
    }

    public ObjectWriter withRootValueSeparator(SerializableString sep) {
        return _new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
    }

    public SequenceWriter writeValues(File out) throws IOException {
        return _newSequenceWriter(false, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }

    public SequenceWriter writeValues(JsonGenerator gen) throws IOException {
        _configureGenerator(gen);
        return _newSequenceWriter(false, gen, false);
    }

    public SequenceWriter writeValues(Writer out) throws IOException {
        return _newSequenceWriter(false, this._generatorFactory.createGenerator(out), true);
    }

    public SequenceWriter writeValues(OutputStream out) throws IOException {
        return _newSequenceWriter(false, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }

    public SequenceWriter writeValues(DataOutput out) throws IOException {
        return _newSequenceWriter(false, this._generatorFactory.createGenerator(out), true);
    }

    public SequenceWriter writeValuesAsArray(File out) throws IOException {
        return _newSequenceWriter(true, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }

    public SequenceWriter writeValuesAsArray(JsonGenerator gen) throws IOException {
        return _newSequenceWriter(true, gen, false);
    }

    public SequenceWriter writeValuesAsArray(Writer out) throws IOException {
        return _newSequenceWriter(true, this._generatorFactory.createGenerator(out), true);
    }

    public SequenceWriter writeValuesAsArray(OutputStream out) throws IOException {
        return _newSequenceWriter(true, this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), true);
    }

    public SequenceWriter writeValuesAsArray(DataOutput out) throws IOException {
        return _newSequenceWriter(true, this._generatorFactory.createGenerator(out), true);
    }

    public boolean isEnabled(SerializationFeature f) {
        return this._config.isEnabled(f);
    }

    public boolean isEnabled(MapperFeature f) {
        return this._config.isEnabled(f);
    }

    @Deprecated
    public boolean isEnabled(JsonParser.Feature f) {
        return this._generatorFactory.isEnabled(f);
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return this._generatorFactory.isEnabled(f);
    }

    public SerializationConfig getConfig() {
        return this._config;
    }

    public JsonFactory getFactory() {
        return this._generatorFactory;
    }

    public TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }

    public boolean hasPrefetchedSerializer() {
        return this._prefetch.hasSerializer();
    }

    public ContextAttributes getAttributes() {
        return this._config.getAttributes();
    }

    public void writeValue(JsonGenerator gen, Object value) throws IOException {
        _configureGenerator(gen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && (value instanceof Closeable)) {
            Closeable toClose = (Closeable) value;
            try {
                this._prefetch.serialize(gen, value, _serializerProvider());
                if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                    gen.flush();
                }
                toClose.close();
                return;
            } catch (Exception e) {
                ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
                return;
            }
        }
        this._prefetch.serialize(gen, value, _serializerProvider());
        if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            gen.flush();
        }
    }

    public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._generatorFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }

    public void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._generatorFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }

    public void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        _configAndWriteValue(this._generatorFactory.createGenerator(w), value);
    }

    public void writeValue(DataOutput out, Object value) throws IOException {
        _configAndWriteValue(this._generatorFactory.createGenerator(out), value);
    }

    public String writeValueAsString(Object value) throws JsonProcessingException {
        SegmentedStringWriter sw = new SegmentedStringWriter(this._generatorFactory._getBufferRecycler());
        try {
            _configAndWriteValue(this._generatorFactory.createGenerator(sw), value);
            return sw.getAndClear();
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
        ByteArrayBuilder bb = new ByteArrayBuilder(this._generatorFactory._getBufferRecycler());
        try {
            _configAndWriteValue(this._generatorFactory.createGenerator(bb, JsonEncoding.UTF8), value);
            byte[] result = bb.toByteArray();
            bb.release();
            return result;
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
    }

    public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        _serializerProvider().acceptJsonFormatVisitor(type, visitor);
    }

    public void acceptJsonFormatVisitor(Class<?> rawType, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        acceptJsonFormatVisitor(this._config.constructType(rawType), visitor);
    }

    public boolean canSerialize(Class<?> type) {
        return _serializerProvider().hasSerializerFor(type, null);
    }

    public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
        return _serializerProvider().hasSerializerFor(type, cause);
    }

    protected DefaultSerializerProvider _serializerProvider() {
        return this._serializerProvider.createInstance(this._config, this._serializerFactory);
    }

    protected void _verifySchemaType(FormatSchema schema) {
        if (schema != null && !this._generatorFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._generatorFactory.getFormatName());
        }
    }

    protected final void _configAndWriteValue(JsonGenerator gen, Object value) throws IOException {
        _configureGenerator(gen);
        if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && (value instanceof Closeable)) {
            _writeCloseable(gen, value);
            return;
        }
        try {
            this._prefetch.serialize(gen, value, _serializerProvider());
            gen.close();
        } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, e);
        }
    }

    private final void _writeCloseable(JsonGenerator gen, Object value) throws IOException {
        Closeable toClose = (Closeable) value;
        try {
            this._prefetch.serialize(gen, value, _serializerProvider());
            toClose = null;
            toClose.close();
            gen.close();
        } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, toClose, e);
        }
    }

    protected final void _configureGenerator(JsonGenerator gen) {
        this._config.initialize(gen);
        this._generatorSettings.initialize(gen);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectWriter$GeneratorSettings.class */
    public static final class GeneratorSettings implements Serializable {
        private static final long serialVersionUID = 1;
        public static final GeneratorSettings empty = new GeneratorSettings(null, null, null, null);
        public final PrettyPrinter prettyPrinter;
        public final FormatSchema schema;
        public final CharacterEscapes characterEscapes;
        public final SerializableString rootValueSeparator;

        public GeneratorSettings(PrettyPrinter pp, FormatSchema sch, CharacterEscapes esc, SerializableString rootSep) {
            this.prettyPrinter = pp;
            this.schema = sch;
            this.characterEscapes = esc;
            this.rootValueSeparator = rootSep;
        }

        public GeneratorSettings with(PrettyPrinter pp) {
            if (pp == null) {
                pp = ObjectWriter.NULL_PRETTY_PRINTER;
            }
            return pp == this.prettyPrinter ? this : new GeneratorSettings(pp, this.schema, this.characterEscapes, this.rootValueSeparator);
        }

        public GeneratorSettings with(FormatSchema sch) {
            return this.schema == sch ? this : new GeneratorSettings(this.prettyPrinter, sch, this.characterEscapes, this.rootValueSeparator);
        }

        public GeneratorSettings with(CharacterEscapes esc) {
            return this.characterEscapes == esc ? this : new GeneratorSettings(this.prettyPrinter, this.schema, esc, this.rootValueSeparator);
        }

        public GeneratorSettings withRootValueSeparator(String sep) {
            if (sep == null) {
                if (this.rootValueSeparator == null) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
            } else if (sep.equals(_rootValueSeparatorAsString())) {
                return this;
            } else {
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, new SerializedString(sep));
            }
        }

        public GeneratorSettings withRootValueSeparator(SerializableString sep) {
            if (sep == null) {
                if (this.rootValueSeparator == null) {
                    return this;
                }
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, null);
            } else if (sep.equals(this.rootValueSeparator)) {
                return this;
            } else {
                return new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, sep);
            }
        }

        private final String _rootValueSeparatorAsString() {
            if (this.rootValueSeparator == null) {
                return null;
            }
            return this.rootValueSeparator.getValue();
        }

        public void initialize(JsonGenerator gen) {
            PrettyPrinter pp = this.prettyPrinter;
            if (this.prettyPrinter != null) {
                if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
                    gen.setPrettyPrinter(null);
                } else {
                    if (pp instanceof Instantiatable) {
                        pp = (PrettyPrinter) ((Instantiatable) pp).createInstance();
                    }
                    gen.setPrettyPrinter(pp);
                }
            }
            if (this.characterEscapes != null) {
                gen.setCharacterEscapes(this.characterEscapes);
            }
            if (this.schema != null) {
                gen.setSchema(this.schema);
            }
            if (this.rootValueSeparator != null) {
                gen.setRootValueSeparator(this.rootValueSeparator);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/ObjectWriter$Prefetch.class */
    public static final class Prefetch implements Serializable {
        private static final long serialVersionUID = 1;
        public static final Prefetch empty = new Prefetch(null, null, null);
        private final JavaType rootType;
        private final JsonSerializer<Object> valueSerializer;
        private final TypeSerializer typeSerializer;

        private Prefetch(JavaType rootT, JsonSerializer<Object> ser, TypeSerializer typeSer) {
            this.rootType = rootT;
            this.valueSerializer = ser;
            this.typeSerializer = typeSer;
        }

        public Prefetch forRootType(ObjectWriter parent, JavaType newType) {
            boolean noType = newType == null || newType.isJavaLangObject();
            if (noType) {
                if (this.rootType == null || this.valueSerializer == null) {
                    return this;
                }
                return new Prefetch(null, null, this.typeSerializer);
            } else if (newType.equals(this.rootType)) {
                return this;
            } else {
                if (parent.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
                    DefaultSerializerProvider prov = parent._serializerProvider();
                    try {
                        JsonSerializer<Object> ser = prov.findTypedValueSerializer(newType, true, (BeanProperty) null);
                        if (ser instanceof TypeWrappedSerializer) {
                            return new Prefetch(newType, null, ((TypeWrappedSerializer) ser).typeSerializer());
                        }
                        return new Prefetch(newType, ser, null);
                    } catch (JsonProcessingException e) {
                    }
                }
                return new Prefetch(newType, null, this.typeSerializer);
            }
        }

        public final JsonSerializer<Object> getValueSerializer() {
            return this.valueSerializer;
        }

        public final TypeSerializer getTypeSerializer() {
            return this.typeSerializer;
        }

        public boolean hasSerializer() {
            return (this.valueSerializer == null && this.typeSerializer == null) ? false : true;
        }

        public void serialize(JsonGenerator gen, Object value, DefaultSerializerProvider prov) throws IOException {
            if (this.typeSerializer != null) {
                prov.serializePolymorphic(gen, value, this.rootType, this.valueSerializer, this.typeSerializer);
            } else if (this.valueSerializer != null) {
                prov.serializeValue(gen, value, this.rootType, this.valueSerializer);
            } else if (this.rootType != null) {
                prov.serializeValue(gen, value, this.rootType);
            } else {
                prov.serializeValue(gen, value);
            }
        }
    }
}