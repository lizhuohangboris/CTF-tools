package org.springframework.http.converter;

import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/HttpMessageConverter.class */
public interface HttpMessageConverter<T> {
    boolean canRead(Class<?> cls, @Nullable MediaType mediaType);

    boolean canWrite(Class<?> cls, @Nullable MediaType mediaType);

    List<MediaType> getSupportedMediaTypes();

    T read(Class<? extends T> cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException;

    void write(T t, @Nullable MediaType mediaType, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException;
}