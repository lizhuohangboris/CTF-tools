package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/json/Jackson2JsonDecoder.class */
public class Jackson2JsonDecoder extends AbstractJackson2Decoder {
    public Jackson2JsonDecoder() {
        super(Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
    }

    public Jackson2JsonDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
    }
}