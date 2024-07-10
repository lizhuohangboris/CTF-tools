package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/ServerDefaultCodecsImpl.class */
class ServerDefaultCodecsImpl extends BaseDefaultCodecs implements ServerCodecConfigurer.ServerDefaultCodecs {
    private static final boolean synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", DefaultServerCodecConfigurer.class.getClassLoader());
    @Nullable
    private Encoder<?> sseEncoder;

    @Override // org.springframework.http.codec.ServerCodecConfigurer.ServerDefaultCodecs
    public void serverSentEventEncoder(Encoder<?> encoder) {
        this.sseEncoder = encoder;
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
        if (synchronossMultipartPresent) {
            boolean enable = isEnableLoggingRequestDetails();
            SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
            partReader.setEnableLoggingRequestDetails(enable);
            typedReaders.add(partReader);
            MultipartHttpMessageReader reader = new MultipartHttpMessageReader(partReader);
            reader.setEnableLoggingRequestDetails(enable);
            typedReaders.add(reader);
        }
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
        objectWriters.add(new ServerSentEventHttpMessageWriter(getSseEncoder()));
    }

    @Nullable
    private Encoder<?> getSseEncoder() {
        if (this.sseEncoder != null) {
            return this.sseEncoder;
        }
        if (jackson2Present) {
            return getJackson2JsonEncoder();
        }
        return null;
    }
}