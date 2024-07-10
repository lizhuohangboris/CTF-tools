package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/BaseCodecConfigurer.class */
class BaseCodecConfigurer implements CodecConfigurer {
    private final BaseDefaultCodecs defaultCodecs;
    private final DefaultCustomCodecs customCodecs = new DefaultCustomCodecs();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseCodecConfigurer(BaseDefaultCodecs defaultCodecs) {
        Assert.notNull(defaultCodecs, "'defaultCodecs' is required");
        this.defaultCodecs = defaultCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public CodecConfigurer.DefaultCodecs defaultCodecs() {
        return this.defaultCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public void registerDefaults(boolean shouldRegister) {
        this.defaultCodecs.registerDefaults(shouldRegister);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public CodecConfigurer.CustomCodecs customCodecs() {
        return this.customCodecs;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public List<HttpMessageReader<?>> getReaders() {
        List<HttpMessageReader<?>> result = new ArrayList<>();
        result.addAll(this.defaultCodecs.getTypedReaders());
        result.addAll(this.customCodecs.getTypedReaders());
        result.addAll(this.defaultCodecs.getObjectReaders());
        result.addAll(this.customCodecs.getObjectReaders());
        result.addAll(this.defaultCodecs.getCatchAllReaders());
        return result;
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    public List<HttpMessageWriter<?>> getWriters() {
        return getWritersInternal(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<HttpMessageWriter<?>> getWritersInternal(boolean forMultipart) {
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.addAll(this.defaultCodecs.getTypedWriters(forMultipart));
        result.addAll(this.customCodecs.getTypedWriters());
        result.addAll(this.defaultCodecs.getObjectWriters(forMultipart));
        result.addAll(this.customCodecs.getObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/support/BaseCodecConfigurer$DefaultCustomCodecs.class */
    public static final class DefaultCustomCodecs implements CodecConfigurer.CustomCodecs {
        private final List<HttpMessageReader<?>> typedReaders;
        private final List<HttpMessageWriter<?>> typedWriters;
        private final List<HttpMessageReader<?>> objectReaders;
        private final List<HttpMessageWriter<?>> objectWriters;

        private DefaultCustomCodecs() {
            this.typedReaders = new ArrayList();
            this.typedWriters = new ArrayList();
            this.objectReaders = new ArrayList();
            this.objectWriters = new ArrayList();
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void decoder(Decoder<?> decoder) {
            reader(new DecoderHttpMessageReader(decoder));
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void encoder(Encoder<?> encoder) {
            writer(new EncoderHttpMessageWriter(encoder));
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void reader(HttpMessageReader<?> reader) {
            boolean canReadToObject = reader.canRead(ResolvableType.forClass(Object.class), null);
            (canReadToObject ? this.objectReaders : this.typedReaders).add(reader);
        }

        @Override // org.springframework.http.codec.CodecConfigurer.CustomCodecs
        public void writer(HttpMessageWriter<?> writer) {
            boolean canWriteObject = writer.canWrite(ResolvableType.forClass(Object.class), null);
            (canWriteObject ? this.objectWriters : this.typedWriters).add(writer);
        }

        List<HttpMessageReader<?>> getTypedReaders() {
            return this.typedReaders;
        }

        List<HttpMessageWriter<?>> getTypedWriters() {
            return this.typedWriters;
        }

        List<HttpMessageReader<?>> getObjectReaders() {
            return this.objectReaders;
        }

        List<HttpMessageWriter<?>> getObjectWriters() {
            return this.objectWriters;
        }
    }
}