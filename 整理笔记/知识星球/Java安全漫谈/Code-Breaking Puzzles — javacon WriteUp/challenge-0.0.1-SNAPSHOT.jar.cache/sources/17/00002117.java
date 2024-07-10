package org.springframework.http.converter.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/xml/MappingJackson2XmlHttpMessageConverter.class */
public class MappingJackson2XmlHttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    public MappingJackson2XmlHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", XMLDeclaration.DEFAULT_KEYWORD), new MediaType("text", XMLDeclaration.DEFAULT_KEYWORD), new MediaType("application", "*+xml"));
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
        super.setObjectMapper(objectMapper);
    }
}