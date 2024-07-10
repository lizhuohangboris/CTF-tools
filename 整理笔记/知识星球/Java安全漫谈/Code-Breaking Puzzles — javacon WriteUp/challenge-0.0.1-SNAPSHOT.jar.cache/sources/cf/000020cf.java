package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.ServerCodecConfigurer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/DefaultServerCodecConfigurer.class */
public class DefaultServerCodecConfigurer extends BaseCodecConfigurer implements ServerCodecConfigurer {
    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getWriters() {
        return super.getWriters();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getReaders() {
        return super.getReaders();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ CodecConfigurer.CustomCodecs customCodecs() {
        return super.customCodecs();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ void registerDefaults(boolean z) {
        super.registerDefaults(z);
    }

    public DefaultServerCodecConfigurer() {
        super(new ServerDefaultCodecsImpl());
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public ServerCodecConfigurer.ServerDefaultCodecs defaultCodecs() {
        return (ServerCodecConfigurer.ServerDefaultCodecs) super.defaultCodecs();
    }
}