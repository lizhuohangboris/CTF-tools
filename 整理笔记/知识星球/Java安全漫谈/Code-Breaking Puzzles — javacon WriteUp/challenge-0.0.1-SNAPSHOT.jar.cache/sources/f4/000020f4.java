package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.TypeUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/json/AbstractJackson2HttpMessageConverter.class */
public abstract class AbstractJackson2HttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected ObjectMapper objectMapper;
    @Nullable
    private Boolean prettyPrint;
    @Nullable
    private PrettyPrinter ssePrettyPrinter;

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setDefaultCharset(DEFAULT_CHARSET);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    public AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType supportedMediaType) {
        this(objectMapper);
        setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    public AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType... supportedMediaTypes) {
        this(objectMapper);
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        configurePrettyPrint();
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = Boolean.valueOf(prettyPrint);
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint.booleanValue());
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!canRead(mediaType)) {
            return false;
        }
        JavaType javaType = getJavaType(type, contextClass);
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }
        logWarningIfNecessary(javaType, causeRef.get());
        return false;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!canWrite(mediaType)) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        logWarningIfNecessary(clazz, causeRef.get());
        return false;
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause == null) {
            return;
        }
        boolean debugLevel = (cause instanceof JsonMappingException) && cause.getMessage().startsWith("Cannot find");
        if (debugLevel) {
            if (!this.logger.isDebugEnabled()) {
                return;
            }
        } else if (!this.logger.isWarnEnabled()) {
            return;
        }
        String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
        if (debugLevel) {
            this.logger.debug(msg, cause);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.warn(msg, cause);
        } else {
            this.logger.warn(msg + ": " + cause);
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        Class<?> deserializationView;
        try {
            if ((inputMessage instanceof MappingJacksonInputMessage) && (deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView()) != null) {
                return this.objectMapper.readerWithView(deserializationView).forType(javaType).readValue(inputMessage.getBody());
            }
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex2) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex2.getOriginalMessage(), ex2, inputMessage);
        }
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try {
            writePrefix(generator, object);
            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue) object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = getJavaType(type, null);
            }
            ObjectWriter objectWriter = serializationView != null ? this.objectMapper.writerWithView(serializationView) : this.objectMapper.writer();
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }
            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }
            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, value);
            writeSuffix(generator, object);
            generator.flush();
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex2) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex2.getOriginalMessage(), ex2);
        }
    }

    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        JsonEncoding[] values;
        if (contentType != null && contentType.getCharset() != null) {
            Charset charset = contentType.getCharset();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    @Nullable
    public MediaType getDefaultContentType(Object object) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }
        return super.getDefaultContentType(object);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(Object object, @Nullable MediaType contentType) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }
        return super.getContentLength(object, contentType);
    }
}