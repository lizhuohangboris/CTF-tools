package org.springframework.http.codec.protobuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/protobuf/ProtobufCodecSupport.class */
public abstract class ProtobufCodecSupport {
    static final List<MimeType> MIME_TYPES = Collections.unmodifiableList(Arrays.asList(new MimeType("application", "x-protobuf"), new MimeType("application", "octet-stream")));
    static final String DELIMITED_KEY = "delimited";
    static final String DELIMITED_VALUE = "true";

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean supportsMimeType(@Nullable MimeType mimeType) {
        return mimeType == null || MIME_TYPES.stream().anyMatch(m -> {
            return m.isCompatibleWith(mimeType);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MimeType> getMimeTypes() {
        return MIME_TYPES;
    }
}