package org.springframework.http.converter.protobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.google.protobuf.util.JsonFormat;
import com.googlecode.protobuf.format.FormatFactory;
import com.googlecode.protobuf.format.ProtobufFormatter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter.class */
public class ProtobufHttpMessageConverter extends AbstractHttpMessageConverter<Message> {
    public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
    public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";
    final ExtensionRegistry extensionRegistry;
    @Nullable
    private final ProtobufFormatSupport protobufFormatSupport;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);
    private static final Map<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter$ProtobufFormatSupport.class */
    public interface ProtobufFormatSupport {
        MediaType[] supportedMediaTypes();

        boolean supportsWriteOnly(@Nullable MediaType mediaType);

        void merge(InputStream inputStream, Charset charset, MediaType mediaType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException;

        void print(Message message, OutputStream outputStream, MediaType mediaType, Charset charset) throws IOException, HttpMessageConversionException;
    }

    public ProtobufHttpMessageConverter() {
        this(null, null);
    }

    @Deprecated
    public ProtobufHttpMessageConverter(@Nullable ExtensionRegistryInitializer registryInitializer) {
        this(null, null);
        if (registryInitializer != null) {
            registryInitializer.initializeExtensionRegistry(this.extensionRegistry);
        }
    }

    public ProtobufHttpMessageConverter(ExtensionRegistry extensionRegistry) {
        this(null, extensionRegistry);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProtobufHttpMessageConverter(@Nullable ProtobufFormatSupport formatSupport, @Nullable ExtensionRegistry extensionRegistry) {
        if (formatSupport != null) {
            this.protobufFormatSupport = formatSupport;
        } else if (ClassUtils.isPresent("com.googlecode.protobuf.format.FormatFactory", getClass().getClassLoader())) {
            this.protobufFormatSupport = new ProtobufJavaFormatSupport();
        } else if (ClassUtils.isPresent("com.google.protobuf.util.JsonFormat", getClass().getClassLoader())) {
            this.protobufFormatSupport = new ProtobufJavaUtilSupport(null, null);
        } else {
            this.protobufFormatSupport = null;
        }
        setSupportedMediaTypes(Arrays.asList(this.protobufFormatSupport != null ? this.protobufFormatSupport.supportedMediaTypes() : new MediaType[]{PROTOBUF, MediaType.TEXT_PLAIN}));
        this.extensionRegistry = extensionRegistry == null ? ExtensionRegistry.newInstance() : extensionRegistry;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public MediaType getDefaultContentType(Message message) {
        return PROTOBUF;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        Message.Builder builder = getMessageBuilder(clazz);
        if (PROTOBUF.isCompatibleWith(contentType)) {
            builder.mergeFrom(inputMessage.getBody(), this.extensionRegistry);
        } else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            InputStreamReader reader = new InputStreamReader(inputMessage.getBody(), charset);
            TextFormat.merge(reader, this.extensionRegistry, builder);
        } else if (this.protobufFormatSupport != null) {
            this.protobufFormatSupport.merge(inputMessage.getBody(), charset, contentType, this.extensionRegistry, builder);
        }
        return builder.build();
    }

    private Message.Builder getMessageBuilder(Class<? extends Message> clazz) {
        try {
            Method method = methodCache.get(clazz);
            if (method == null) {
                method = clazz.getMethod("newBuilder", new Class[0]);
                methodCache.put(clazz, method);
            }
            return (Message.Builder) method.invoke(clazz, new Object[0]);
        } catch (Exception ex) {
            throw new HttpMessageConversionException("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean canWrite(@Nullable MediaType mediaType) {
        return super.canWrite(mediaType) || (this.protobufFormatSupport != null && this.protobufFormatSupport.supportsWriteOnly(mediaType));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(Message message, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = getDefaultContentType(message);
            Assert.state(contentType != null, "No content type");
        }
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        if (PROTOBUF.isCompatibleWith(contentType)) {
            setProtoHeader(outputMessage, message);
            CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputMessage.getBody());
            message.writeTo(codedOutputStream);
            codedOutputStream.flush();
        } else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
            TextFormat.print(message, outputStreamWriter);
            outputStreamWriter.flush();
            outputMessage.getBody().flush();
        } else if (this.protobufFormatSupport != null) {
            this.protobufFormatSupport.print(message, outputMessage.getBody(), contentType, charset);
            outputMessage.getBody().flush();
        }
    }

    private void setProtoHeader(HttpOutputMessage response, Message message) {
        response.getHeaders().set(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
        response.getHeaders().set(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter$ProtobufJavaFormatSupport.class */
    static class ProtobufJavaFormatSupport implements ProtobufFormatSupport {
        private final ProtobufFormatter jsonFormatter;
        private final ProtobufFormatter xmlFormatter;
        private final ProtobufFormatter htmlFormatter;

        public ProtobufJavaFormatSupport() {
            FormatFactory formatFactory = new FormatFactory();
            this.jsonFormatter = formatFactory.createFormatter(FormatFactory.Formatter.JSON);
            this.xmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.XML);
            this.htmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.HTML);
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public MediaType[] supportedMediaTypes() {
            return new MediaType[]{ProtobufHttpMessageConverter.PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON};
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
            return MediaType.TEXT_HTML.isCompatibleWith(mediaType);
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                this.jsonFormatter.merge(input, charset, extensionRegistry, builder);
            } else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
                this.xmlFormatter.merge(input, charset, extensionRegistry, builder);
            } else {
                throw new HttpMessageConversionException("protobuf-java-format does not support parsing " + contentType);
            }
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                this.jsonFormatter.print(message, output, charset);
            } else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
                this.xmlFormatter.print(message, output, charset);
            } else if (contentType.isCompatibleWith(MediaType.TEXT_HTML)) {
                this.htmlFormatter.print(message, output, charset);
            } else {
                throw new HttpMessageConversionException("protobuf-java-format does not support printing " + contentType);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/protobuf/ProtobufHttpMessageConverter$ProtobufJavaUtilSupport.class */
    static class ProtobufJavaUtilSupport implements ProtobufFormatSupport {
        private final JsonFormat.Parser parser;
        private final JsonFormat.Printer printer;

        public ProtobufJavaUtilSupport(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
            this.parser = parser != null ? parser : JsonFormat.parser();
            this.printer = printer != null ? printer : JsonFormat.printer();
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public MediaType[] supportedMediaTypes() {
            return new MediaType[]{ProtobufHttpMessageConverter.PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON};
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
            return false;
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                InputStreamReader reader = new InputStreamReader(input, charset);
                this.parser.merge(reader, builder);
                return;
            }
            throw new HttpMessageConversionException("protobuf-java-util does not support parsing " + contentType);
        }

        @Override // org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.ProtobufFormatSupport
        public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                OutputStreamWriter writer = new OutputStreamWriter(output, charset);
                this.printer.appendTo(message, writer);
                writer.flush();
                return;
            }
            throw new HttpMessageConversionException("protobuf-java-util does not support printing " + contentType);
        }
    }
}