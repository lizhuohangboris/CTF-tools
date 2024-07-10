package org.springframework.http.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/ByteArrayHttpMessageConverter.class */
public class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {
    public ByteArrayHttpMessageConverter() {
        super(new MediaType("application", "octet-stream"), MediaType.ALL);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supports(Class<?> clazz) {
        return byte[].class == clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
        long contentLength = inputMessage.getHeaders().getContentLength();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : 4096);
        StreamUtils.copy(inputMessage.getBody(), bos);
        return bos.toByteArray();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(byte[] bytes, @Nullable MediaType contentType) {
        return Long.valueOf(bytes.length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
        StreamUtils.copy(bytes, outputMessage.getBody());
    }
}