package org.springframework.http.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/FormHttpMessageWriter.class */
public class FormHttpMessageWriter extends LoggingCodecSupport implements HttpMessageWriter<MultiValueMap<String, String>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);
    private static final List<MediaType> MEDIA_TYPES = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
    private static final ResolvableType MULTIVALUE_TYPE = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
    private Charset defaultCharset = DEFAULT_CHARSET;

    public void setDefaultCharset(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        this.defaultCharset = charset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public List<MediaType> getWritableMediaTypes() {
        return MEDIA_TYPES;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        Class<?> rawClass = elementType.getRawClass();
        if (rawClass == null || !MultiValueMap.class.isAssignableFrom(rawClass)) {
            return false;
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return true;
        }
        if (mediaType == null) {
            return MULTIVALUE_TYPE.isAssignableFrom(elementType);
        }
        return false;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends MultiValueMap<String, String>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        MediaType mediaType2 = getMediaType(mediaType);
        message.getHeaders().setContentType(mediaType2);
        Charset charset = mediaType2.getCharset();
        Assert.notNull(charset, "No charset");
        return Mono.from(inputStream).flatMap(form -> {
            logFormData(form, hints);
            String value = serializeForm(form, charset);
            ByteBuffer byteBuffer = charset.encode(value);
            DataBuffer buffer = message.bufferFactory().wrap(byteBuffer);
            message.getHeaders().setContentLength(byteBuffer.remaining());
            return message.writeWith(Mono.just(buffer));
        });
    }

    private MediaType getMediaType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return DEFAULT_FORM_DATA_MEDIA_TYPE;
        }
        if (mediaType.getCharset() == null) {
            return new MediaType(mediaType, getDefaultCharset());
        }
        return mediaType;
    }

    private void logFormData(MultiValueMap<String, String> form, Map<String, Object> hints) {
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String str;
            StringBuilder append = new StringBuilder().append(Hints.getLogPrefix(hints)).append("Writing ");
            if (isEnableLoggingRequestDetails()) {
                str = LogFormatUtils.formatValue(form, !traceOn.booleanValue());
            } else {
                str = "form fields " + form.keySet() + " (content masked)";
            }
            return append.append(str).toString();
        });
    }

    protected String serializeForm(MultiValueMap<String, String> formData, Charset charset) {
        StringBuilder builder = new StringBuilder();
        formData.forEach(name, values -> {
            values.forEach(value -> {
                try {
                    if (builder.length() != 0) {
                        builder.append('&');
                    }
                    builder.append(URLEncoder.encode(name, charset.name()));
                    if (value != null) {
                        builder.append('=');
                        builder.append(URLEncoder.encode(value, charset.name()));
                    }
                } catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
            });
        });
        return builder.toString();
    }
}