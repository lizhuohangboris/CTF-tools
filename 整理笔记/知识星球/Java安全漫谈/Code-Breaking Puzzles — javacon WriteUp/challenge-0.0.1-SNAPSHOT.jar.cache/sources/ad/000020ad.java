package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.nio.charset.StandardCharsets;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/json/Jackson2SmileDecoder.class */
public class Jackson2SmileDecoder extends AbstractJackson2Decoder {
    private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = {new MimeType("application", "x-jackson-smile", StandardCharsets.UTF_8), new MimeType("application", "*+x-jackson-smile", StandardCharsets.UTF_8)};

    public Jackson2SmileDecoder() {
        this(Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
    }

    public Jackson2SmileDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
    }
}