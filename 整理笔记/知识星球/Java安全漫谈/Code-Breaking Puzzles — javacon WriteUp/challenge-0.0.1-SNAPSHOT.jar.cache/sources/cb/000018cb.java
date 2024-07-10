package org.springframework.boot.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/cloud/CloudFoundryVcapEnvironmentPostProcessor.class */
public class CloudFoundryVcapEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    private static final Log logger = LogFactory.getLog(CloudFoundryVcapEnvironmentPostProcessor.class);
    private static final String VCAP_APPLICATION = "VCAP_APPLICATION";
    private static final String VCAP_SERVICES = "VCAP_SERVICES";
    private int order = -2147483639;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (CloudPlatform.CLOUD_FOUNDRY.isActive(environment)) {
            Properties properties = new Properties();
            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            addWithPrefix(properties, getPropertiesFromApplication(environment, jsonParser), "vcap.application.");
            addWithPrefix(properties, getPropertiesFromServices(environment, jsonParser), "vcap.services.");
            MutablePropertySources propertySources = environment.getPropertySources();
            if (propertySources.contains(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME)) {
                propertySources.addAfter(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME, new PropertiesPropertySource("vcap", properties));
            } else {
                propertySources.addFirst(new PropertiesPropertySource("vcap", properties));
            }
        }
    }

    private void addWithPrefix(Properties properties, Properties other, String prefix) {
        for (String key : other.stringPropertyNames()) {
            String prefixed = prefix + key;
            properties.setProperty(prefixed, other.getProperty(key));
        }
    }

    private Properties getPropertiesFromApplication(Environment environment, JsonParser parser) {
        Properties properties = new Properties();
        try {
            String property = environment.getProperty(VCAP_APPLICATION, "{}");
            Map<String, Object> map = parser.parseMap(property);
            extractPropertiesFromApplication(properties, map);
        } catch (Exception ex) {
            logger.error("Could not parse VCAP_APPLICATION", ex);
        }
        return properties;
    }

    private Properties getPropertiesFromServices(Environment environment, JsonParser parser) {
        Properties properties = new Properties();
        try {
            String property = environment.getProperty(VCAP_SERVICES, "{}");
            Map<String, Object> map = parser.parseMap(property);
            extractPropertiesFromServices(properties, map);
        } catch (Exception ex) {
            logger.error("Could not parse VCAP_SERVICES", ex);
        }
        return properties;
    }

    private void extractPropertiesFromApplication(Properties properties, Map<String, Object> map) {
        if (map != null) {
            flatten(properties, map, "");
        }
    }

    private void extractPropertiesFromServices(Properties properties, Map<String, Object> map) {
        if (map != null) {
            for (Object services : map.values()) {
                List<Object> list = (List) services;
                for (Object object : list) {
                    Map<String, Object> service = (Map) object;
                    String key = (String) service.get("name");
                    if (key == null) {
                        key = (String) service.get("label");
                    }
                    flatten(properties, service, key);
                }
            }
        }
    }

    private void flatten(Properties properties, Map<String, Object> input, String path) {
        input.forEach(key, value -> {
            String name = getPropertyName(path, key);
            if (value instanceof Map) {
                flatten(properties, (Map) value, name);
            } else if (value instanceof Collection) {
                Collection<Object> collection = (Collection) value;
                properties.put(name, StringUtils.collectionToCommaDelimitedString(collection));
                int count = 0;
                for (Object item : collection) {
                    int i = count;
                    count++;
                    String itemKey = PropertyAccessor.PROPERTY_KEY_PREFIX + i + "]";
                    flatten(properties, Collections.singletonMap(itemKey, item), name);
                }
            } else if (value instanceof String) {
                properties.put(name, value);
            } else if (value instanceof Number) {
                properties.put(name, value.toString());
            } else if (value instanceof Boolean) {
                properties.put(name, value.toString());
            } else {
                properties.put(name, value != null ? value : "");
            }
        });
    }

    private String getPropertyName(String path, String key) {
        if (!StringUtils.hasText(path)) {
            return key;
        }
        if (key.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
            return path + key;
        }
        return path + "." + key;
    }
}