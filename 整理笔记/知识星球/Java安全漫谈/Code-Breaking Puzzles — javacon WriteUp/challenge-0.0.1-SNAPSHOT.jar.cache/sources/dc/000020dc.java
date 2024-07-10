package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/AbstractHttpMessageConverter.class */
public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {
    protected final Log logger = HttpLogging.forLogName(getClass());
    private List<MediaType> supportedMediaTypes = Collections.emptyList();
    @Nullable
    private Charset defaultCharset;

    protected abstract boolean supports(Class<?> cls);

    protected abstract T readInternal(Class<? extends T> cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException;

    protected abstract void writeInternal(T t, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException;

    public AbstractHttpMessageConverter() {
    }

    public AbstractHttpMessageConverter(MediaType supportedMediaType) {
        setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    public AbstractHttpMessageConverter(MediaType... supportedMediaTypes) {
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public AbstractHttpMessageConverter(Charset defaultCharset, MediaType... supportedMediaTypes) {
        this.defaultCharset = defaultCharset;
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        Assert.notEmpty(supportedMediaTypes, "MediaType List must not be empty");
        this.supportedMediaTypes = new ArrayList(supportedMediaTypes);
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public void setDefaultCharset(@Nullable Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    @Nullable
    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return supports(clazz) && canRead(mediaType);
    }

    public boolean canRead(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return supports(clazz) && canWrite(mediaType);
    }

    public boolean canWrite(@Nullable MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readInternal(clazz, inputMessage);
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public final void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        addDefaultHeaders(headers, t, contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(outputStream -> {
                writeInternal(t, new HttpOutputMessage() { // from class: org.springframework.http.converter.AbstractHttpMessageConverter.1
                    {
                        AbstractHttpMessageConverter.this = this;
                    }

                    @Override // org.springframework.http.HttpOutputMessage
                    public OutputStream getBody() {
                        return outputStream;
                    }

                    @Override // org.springframework.http.HttpMessage
                    public HttpHeaders getHeaders() {
                        return headers;
                    }
                });
            });
            return;
        }
        writeInternal(t, outputMessage);
        outputMessage.getBody().flush();
    }

    public void addDefaultHeaders(HttpHeaders headers, T t, @Nullable MediaType contentType) throws IOException {
        Long contentLength;
        Charset defaultCharset;
        if (headers.getContentType() == null) {
            MediaType contentTypeToUse = contentType;
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentTypeToUse = getDefaultContentType(t);
            } else if (MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                MediaType mediaType = getDefaultContentType(t);
                contentTypeToUse = mediaType != null ? mediaType : contentTypeToUse;
            }
            if (contentTypeToUse != null) {
                if (contentTypeToUse.getCharset() == null && (defaultCharset = getDefaultCharset()) != null) {
                    contentTypeToUse = new MediaType(contentTypeToUse, defaultCharset);
                }
                headers.setContentType(contentTypeToUse);
            }
        }
        if (headers.getContentLength() < 0 && !headers.containsKey("Transfer-Encoding") && (contentLength = getContentLength(t, headers.getContentType())) != null) {
            headers.setContentLength(contentLength.longValue());
        }
    }

    @Nullable
    public MediaType getDefaultContentType(T t) throws IOException {
        List<MediaType> mediaTypes = getSupportedMediaTypes();
        if (mediaTypes.isEmpty()) {
            return null;
        }
        return mediaTypes.get(0);
    }

    @Nullable
    public Long getContentLength(T t, @Nullable MediaType contentType) throws IOException {
        return null;
    }
}