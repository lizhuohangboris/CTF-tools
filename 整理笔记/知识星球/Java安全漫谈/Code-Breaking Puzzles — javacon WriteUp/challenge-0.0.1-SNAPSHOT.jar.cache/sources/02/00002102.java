package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/json/MappingJackson2HttpMessageConverter.class */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    @Nullable
    private String jsonPrefix;

    public MappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }
}