package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/AbstractGenericHttpMessageConverter.class */
public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> implements GenericHttpMessageConverter<T> {
    protected abstract void writeInternal(T t, @Nullable Type type, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter(MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return type instanceof Class ? canRead((Class) type, mediaType) : canRead(mediaType);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(clazz, mediaType);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public final void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        addDefaultHeaders(headers, t, contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(outputStream -> {
                writeInternal(t, type, new HttpOutputMessage() { // from class: org.springframework.http.converter.AbstractGenericHttpMessageConverter.1
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
        writeInternal(t, type, outputMessage);
        outputMessage.getBody().flush();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(t, null, outputMessage);
    }
}