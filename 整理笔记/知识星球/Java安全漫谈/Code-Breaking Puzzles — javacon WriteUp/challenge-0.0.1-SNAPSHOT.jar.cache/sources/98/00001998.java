package org.springframework.boot.env;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.PropertySourceOrigin;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/SpringApplicationJsonEnvironmentPostProcessor.class */
public class SpringApplicationJsonEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final String SPRING_APPLICATION_JSON_PROPERTY = "spring.application.json";
    public static final String SPRING_APPLICATION_JSON_ENVIRONMENT_VARIABLE = "SPRING_APPLICATION_JSON";
    private static final String SERVLET_ENVIRONMENT_CLASS = "org.springframework.web.context.support.StandardServletEnvironment";
    public static final int DEFAULT_ORDER = -2147483643;
    private int order = DEFAULT_ORDER;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.stream().map(JsonPropertyValue::get).filter((v0) -> {
            return Objects.nonNull(v0);
        }).findFirst().ifPresent(v -> {
            processJson(environment, v);
        });
    }

    private void processJson(ConfigurableEnvironment environment, JsonPropertyValue propertyValue) {
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = parser.parseMap(propertyValue.getJson());
        if (!map.isEmpty()) {
            addJsonPropertySource(environment, new JsonPropertySource(propertyValue, flatten(map)));
        }
    }

    private Map<String, Object> flatten(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        flatten(null, result, map);
        return result;
    }

    private void flatten(String prefix, Map<String, Object> result, Map<String, Object> map) {
        String namePrefix = prefix != null ? prefix + "." : "";
        map.forEach(key, value -> {
            extract(namePrefix + key, result, value);
        });
    }

    private void extract(String name, Map<String, Object> result, Object value) {
        if (value instanceof Map) {
            flatten(name, result, (Map) value);
        } else if (value instanceof Collection) {
            int index = 0;
            for (Object object : (Collection) value) {
                extract(name + PropertyAccessor.PROPERTY_KEY_PREFIX + index + "]", result, object);
                index++;
            }
        } else {
            result.put(name, value);
        }
    }

    private void addJsonPropertySource(ConfigurableEnvironment environment, PropertySource<?> source) {
        MutablePropertySources sources = environment.getPropertySources();
        String name = findPropertySource(sources);
        if (sources.contains(name)) {
            sources.addBefore(name, source);
        } else {
            sources.addFirst(source);
        }
    }

    private String findPropertySource(MutablePropertySources sources) {
        if (ClassUtils.isPresent(SERVLET_ENVIRONMENT_CLASS, null) && sources.contains(StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME)) {
            return StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME;
        }
        return "systemProperties";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/SpringApplicationJsonEnvironmentPostProcessor$JsonPropertySource.class */
    public static class JsonPropertySource extends MapPropertySource implements OriginLookup<String> {
        private final JsonPropertyValue propertyValue;

        JsonPropertySource(JsonPropertyValue propertyValue, Map<String, Object> source) {
            super(SpringApplicationJsonEnvironmentPostProcessor.SPRING_APPLICATION_JSON_PROPERTY, source);
            this.propertyValue = propertyValue;
        }

        @Override // org.springframework.boot.origin.OriginLookup
        public Origin getOrigin(String key) {
            return this.propertyValue.getOrigin();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/SpringApplicationJsonEnvironmentPostProcessor$JsonPropertyValue.class */
    public static class JsonPropertyValue {
        private static final String[] CANDIDATES = {SpringApplicationJsonEnvironmentPostProcessor.SPRING_APPLICATION_JSON_PROPERTY, SpringApplicationJsonEnvironmentPostProcessor.SPRING_APPLICATION_JSON_ENVIRONMENT_VARIABLE};
        private final PropertySource<?> propertySource;
        private final String propertyName;
        private final String json;

        JsonPropertyValue(PropertySource<?> propertySource, String propertyName, String json) {
            this.propertySource = propertySource;
            this.propertyName = propertyName;
            this.json = json;
        }

        public String getJson() {
            return this.json;
        }

        public Origin getOrigin() {
            return PropertySourceOrigin.get(this.propertySource, this.propertyName);
        }

        public static JsonPropertyValue get(PropertySource<?> propertySource) {
            String[] strArr;
            for (String candidate : CANDIDATES) {
                Object value = propertySource.getProperty(candidate);
                if (value != null && (value instanceof String) && StringUtils.hasLength((String) value)) {
                    return new JsonPropertyValue(propertySource, candidate, (String) value);
                }
            }
            return null;
        }
    }
}