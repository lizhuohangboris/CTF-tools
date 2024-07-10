package org.springframework.http.converter.json;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/json/MappingJacksonValue.class */
public class MappingJacksonValue {
    private Object value;
    @Nullable
    private Class<?> serializationView;
    @Nullable
    private FilterProvider filters;

    public MappingJacksonValue(Object value) {
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public void setSerializationView(@Nullable Class<?> serializationView) {
        this.serializationView = serializationView;
    }

    @Nullable
    public Class<?> getSerializationView() {
        return this.serializationView;
    }

    public void setFilters(@Nullable FilterProvider filters) {
        this.filters = filters;
    }

    @Nullable
    public FilterProvider getFilters() {
        return this.filters;
    }
}