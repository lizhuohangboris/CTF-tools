package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/StringHttpMessageConverter.class */
public class StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    @Nullable
    private volatile List<Charset> availableCharsets;
    private boolean writeAcceptCharset;

    public StringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public StringHttpMessageConverter(Charset defaultCharset) {
        super(defaultCharset, MediaType.TEXT_PLAIN, MediaType.ALL);
        this.writeAcceptCharset = true;
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supports(Class<?> clazz) {
        return String.class == clazz;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
        Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return StreamUtils.copyToString(inputMessage.getBody(), charset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(String str, @Nullable MediaType contentType) {
        Charset charset = getContentTypeCharset(contentType);
        return Long.valueOf(str.getBytes(charset).length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
        if (this.writeAcceptCharset) {
            outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());
        }
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        StreamUtils.copy(str, charset, outputMessage.getBody());
    }

    protected List<Charset> getAcceptedCharsets() {
        List<Charset> charsets = this.availableCharsets;
        if (charsets == null) {
            charsets = new ArrayList<>(Charset.availableCharsets().values());
            this.availableCharsets = charsets;
        }
        return charsets;
    }

    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        Charset charset = getDefaultCharset();
        Assert.state(charset != null, "No default charset");
        return charset;
    }
}