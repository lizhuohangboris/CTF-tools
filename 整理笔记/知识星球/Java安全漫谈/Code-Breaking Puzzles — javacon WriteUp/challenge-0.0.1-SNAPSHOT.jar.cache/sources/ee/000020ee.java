package org.springframework.http.converter.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/cbor/MappingJackson2CborHttpMessageConverter.class */
public class MappingJackson2CborHttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    public MappingJackson2CborHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.cbor().build());
    }

    public MappingJackson2CborHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "cbor"));
        Assert.isInstanceOf(CBORFactory.class, objectMapper.getFactory(), "CBORFactory required");
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(CBORFactory.class, objectMapper.getFactory(), "CBORFactory required");
        super.setObjectMapper(objectMapper);
    }
}