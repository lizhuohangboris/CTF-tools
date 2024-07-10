package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/ClientDefaultCodecsImpl.class */
class ClientDefaultCodecsImpl extends BaseDefaultCodecs implements ClientCodecConfigurer.ClientDefaultCodecs {
    @Nullable
    private DefaultMultipartCodecs multipartCodecs;
    @Nullable
    private Decoder<?> sseDecoder;
    @Nullable
    private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
        this.partWritersSupplier = supplier;
    }

    @Override // org.springframework.http.codec.ClientCodecConfigurer.ClientDefaultCodecs
    public ClientCodecConfigurer.MultipartCodecs multipartCodecs() {
        if (this.multipartCodecs == null) {
            this.multipartCodecs = new DefaultMultipartCodecs();
        }
        return this.multipartCodecs;
    }

    @Override // org.springframework.http.codec.ClientCodecConfigurer.ClientDefaultCodecs
    public void serverSentEventDecoder(Decoder<?> decoder) {
        this.sseDecoder = decoder;
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
        objectReaders.add(new ServerSentEventHttpMessageReader(getSseDecoder()));
    }

    @Nullable
    private Decoder<?> getSseDecoder() {
        if (this.sseDecoder != null) {
            return this.sseDecoder;
        }
        if (jackson2Present) {
            return getJackson2JsonDecoder();
        }
        return null;
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        FormHttpMessageWriter formWriter = new FormHttpMessageWriter();
        formWriter.setEnableLoggingRequestDetails(isEnableLoggingRequestDetails());
        MultipartHttpMessageWriter multipartWriter = new MultipartHttpMessageWriter(getPartWriters(), formWriter);
        multipartWriter.setEnableLoggingRequestDetails(isEnableLoggingRequestDetails());
        typedWriters.add(multipartWriter);
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        if (this.multipartCodecs != null) {
            return this.multipartCodecs.getWriters();
        }
        if (this.partWritersSupplier != null) {
            return this.partWritersSupplier.get();
        }
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/ClientDefaultCodecsImpl$DefaultMultipartCodecs.class */
    public static class DefaultMultipartCodecs implements ClientCodecConfigurer.MultipartCodecs {
        private final List<HttpMessageWriter<?>> writers;

        private DefaultMultipartCodecs() {
            this.writers = new ArrayList();
        }

        @Override // org.springframework.http.codec.ClientCodecConfigurer.MultipartCodecs
        public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
            writer(new EncoderHttpMessageWriter(encoder));
            return this;
        }

        @Override // org.springframework.http.codec.ClientCodecConfigurer.MultipartCodecs
        public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
            this.writers.add(writer);
            return this;
        }

        List<HttpMessageWriter<?>> getWriters() {
            return this.writers;
        }
    }
}