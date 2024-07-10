package org.springframework.http.codec;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/ServerCodecConfigurer.class */
public interface ServerCodecConfigurer extends CodecConfigurer {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/ServerCodecConfigurer$ServerDefaultCodecs.class */
    public interface ServerDefaultCodecs extends CodecConfigurer.DefaultCodecs {
        void serverSentEventEncoder(Encoder<?> encoder);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    ServerDefaultCodecs defaultCodecs();

    static ServerCodecConfigurer create() {
        return (ServerCodecConfigurer) CodecConfigurerFactory.create(ServerCodecConfigurer.class);
    }
}