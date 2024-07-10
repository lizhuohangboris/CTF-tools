package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/DefaultClientCodecConfigurer.class */
public class DefaultClientCodecConfigurer extends BaseCodecConfigurer implements ClientCodecConfigurer {
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

    public DefaultClientCodecConfigurer() {
        super(new ClientDefaultCodecsImpl());
        ((ClientDefaultCodecsImpl) defaultCodecs()).setPartWritersSupplier(() -> {
            return getWritersInternal(true);
        });
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
        return (ClientCodecConfigurer.ClientDefaultCodecs) super.defaultCodecs();
    }
}