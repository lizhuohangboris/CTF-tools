package org.springframework.http.codec;

import java.util.List;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/CodecConfigurer.class */
public interface CodecConfigurer {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/CodecConfigurer$CustomCodecs.class */
    public interface CustomCodecs {
        void decoder(Decoder<?> decoder);

        void encoder(Encoder<?> encoder);

        void reader(HttpMessageReader<?> httpMessageReader);

        void writer(HttpMessageWriter<?> httpMessageWriter);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/CodecConfigurer$DefaultCodecs.class */
    public interface DefaultCodecs {
        void jackson2JsonDecoder(Decoder<?> decoder);

        void jackson2JsonEncoder(Encoder<?> encoder);

        void protobufDecoder(Decoder<?> decoder);

        void protobufEncoder(Encoder<?> encoder);

        void enableLoggingRequestDetails(boolean z);
    }

    DefaultCodecs defaultCodecs();

    CustomCodecs customCodecs();

    void registerDefaults(boolean z);

    List<HttpMessageReader<?>> getReaders();

    List<HttpMessageWriter<?>> getWriters();
}