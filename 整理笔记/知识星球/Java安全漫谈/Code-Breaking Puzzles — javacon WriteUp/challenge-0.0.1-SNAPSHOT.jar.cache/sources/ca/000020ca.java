package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/BaseDefaultCodecs.class */
public class BaseDefaultCodecs implements CodecConfigurer.DefaultCodecs {
    static final boolean jackson2Present;
    private static final boolean jackson2SmilePresent;
    private static final boolean jaxb2Present;
    private static final boolean protobufPresent;
    @Nullable
    private Decoder<?> jackson2JsonDecoder;
    @Nullable
    private Encoder<?> jackson2JsonEncoder;
    @Nullable
    private Decoder<?> protobufDecoder;
    @Nullable
    private Encoder<?> protobufEncoder;
    private boolean enableLoggingRequestDetails = false;
    private boolean registerDefaults = true;

    static {
        ClassLoader classLoader = BaseCodecConfigurer.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        protobufPresent = ClassUtils.isPresent("com.google.protobuf.Message", classLoader);
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonDecoder(Decoder<?> decoder) {
        this.jackson2JsonDecoder = decoder;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonEncoder(Encoder<?> encoder) {
        this.jackson2JsonEncoder = encoder;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufDecoder(Decoder<?> decoder) {
        this.protobufDecoder = decoder;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufEncoder(Encoder<?> encoder) {
        this.protobufEncoder = encoder;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void enableLoggingRequestDetails(boolean enable) {
        this.enableLoggingRequestDetails = enable;
    }

    public boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    public void registerDefaults(boolean registerDefaults) {
        this.registerDefaults = registerDefaults;
    }

    public final List<HttpMessageReader<?>> getTypedReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageReader<?>> readers = new ArrayList<>();
        readers.add(new DecoderHttpMessageReader<>(new ByteArrayDecoder()));
        readers.add(new DecoderHttpMessageReader<>(new ByteBufferDecoder()));
        readers.add(new DecoderHttpMessageReader<>(new DataBufferDecoder()));
        readers.add(new DecoderHttpMessageReader<>(new ResourceDecoder()));
        readers.add(new DecoderHttpMessageReader<>(StringDecoder.textPlainOnly()));
        if (protobufPresent) {
            readers.add(new DecoderHttpMessageReader<>(getProtobufDecoder()));
        }
        FormHttpMessageReader formReader = new FormHttpMessageReader();
        formReader.setEnableLoggingRequestDetails(this.enableLoggingRequestDetails);
        readers.add(formReader);
        extendTypedReaders(readers);
        return readers;
    }

    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
    }

    public final List<HttpMessageReader<?>> getObjectReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageReader<?>> readers = new ArrayList<>();
        if (jackson2Present) {
            readers.add(new DecoderHttpMessageReader<>(getJackson2JsonDecoder()));
        }
        if (jackson2SmilePresent) {
            readers.add(new DecoderHttpMessageReader<>(new Jackson2SmileDecoder()));
        }
        if (jaxb2Present) {
            readers.add(new DecoderHttpMessageReader<>(new Jaxb2XmlDecoder()));
        }
        extendObjectReaders(readers);
        return readers;
    }

    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
    }

    public final List<HttpMessageReader<?>> getCatchAllReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageReader<?>> result = new ArrayList<>();
        result.add(new DecoderHttpMessageReader<>(StringDecoder.allMimeTypes()));
        return result;
    }

    public final List<HttpMessageWriter<?>> getTypedWriters(boolean forMultipart) {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageWriter<?>> writers = new ArrayList<>();
        writers.add(new EncoderHttpMessageWriter<>(new ByteArrayEncoder()));
        writers.add(new EncoderHttpMessageWriter<>(new ByteBufferEncoder()));
        writers.add(new EncoderHttpMessageWriter<>(new DataBufferEncoder()));
        writers.add(new ResourceHttpMessageWriter());
        writers.add(new EncoderHttpMessageWriter<>(CharSequenceEncoder.textPlainOnly()));
        if (!forMultipart) {
            extendTypedWriters(writers);
        }
        if (protobufPresent) {
            writers.add(new ProtobufHttpMessageWriter(getProtobufEncoder()));
        }
        return writers;
    }

    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
    }

    public final List<HttpMessageWriter<?>> getObjectWriters(boolean forMultipart) {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageWriter<?>> writers = new ArrayList<>();
        if (jackson2Present) {
            writers.add(new EncoderHttpMessageWriter<>(getJackson2JsonEncoder()));
        }
        if (jackson2SmilePresent) {
            writers.add(new EncoderHttpMessageWriter<>(new Jackson2SmileEncoder()));
        }
        if (jaxb2Present) {
            writers.add(new EncoderHttpMessageWriter<>(new Jaxb2XmlEncoder()));
        }
        if (!forMultipart) {
            extendObjectWriters(writers);
        }
        return writers;
    }

    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
    }

    public List<HttpMessageWriter<?>> getCatchAllWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.add(new EncoderHttpMessageWriter<>(CharSequenceEncoder.allMimeTypes()));
        return result;
    }

    public Decoder<?> getJackson2JsonDecoder() {
        return this.jackson2JsonDecoder != null ? this.jackson2JsonDecoder : new Jackson2JsonDecoder();
    }

    public Encoder<?> getJackson2JsonEncoder() {
        return this.jackson2JsonEncoder != null ? this.jackson2JsonEncoder : new Jackson2JsonEncoder();
    }

    protected Decoder<?> getProtobufDecoder() {
        return this.protobufDecoder != null ? this.protobufDecoder : new ProtobufDecoder();
    }

    protected Encoder<?> getProtobufEncoder() {
        return this.protobufEncoder != null ? this.protobufEncoder : new ProtobufEncoder();
    }
}