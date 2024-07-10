package org.springframework.http.converter.support;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/support/AllEncompassingFormHttpMessageConverter.class */
public class AllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter {
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;

    static {
        ClassLoader classLoader = AllEncompassingFormHttpMessageConverter.class.getClassLoader();
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", classLoader);
    }

    public AllEncompassingFormHttpMessageConverter() {
        try {
            addPartConverter(new SourceHttpMessageConverter());
        } catch (Error e) {
        }
        if (jaxb2Present && !jackson2XmlPresent) {
            addPartConverter(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            addPartConverter(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            addPartConverter(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            addPartConverter(new JsonbHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            addPartConverter(new MappingJackson2XmlHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            addPartConverter(new MappingJackson2SmileHttpMessageConverter());
        }
    }
}