package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/AbstractEncoder.class */
public abstract class AbstractEncoder<T> implements Encoder<T> {
    protected Log logger = LogFactory.getLog(getClass());
    private final List<MimeType> encodableMimeTypes;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEncoder(MimeType... supportedMimeTypes) {
        this.encodableMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    public void setLogger(Log logger) {
        this.logger = logger;
    }

    public Log getLogger() {
        return this.logger;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return this.encodableMimeTypes;
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        return this.encodableMimeTypes.stream().anyMatch(candidate -> {
            return candidate.isCompatibleWith(mimeType);
        });
    }
}