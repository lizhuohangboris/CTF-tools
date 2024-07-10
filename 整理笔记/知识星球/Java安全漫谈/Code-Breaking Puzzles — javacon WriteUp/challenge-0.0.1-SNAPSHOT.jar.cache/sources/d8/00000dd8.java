package org.apache.tomcat.websocket;

import javax.websocket.Decoder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/DecoderEntry.class */
public class DecoderEntry {
    private final Class<?> clazz;
    private final Class<? extends Decoder> decoderClazz;

    public DecoderEntry(Class<?> clazz, Class<? extends Decoder> decoderClazz) {
        this.clazz = clazz;
        this.decoderClazz = decoderClazz;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Class<? extends Decoder> getDecoderClazz() {
        return this.decoderClazz;
    }
}