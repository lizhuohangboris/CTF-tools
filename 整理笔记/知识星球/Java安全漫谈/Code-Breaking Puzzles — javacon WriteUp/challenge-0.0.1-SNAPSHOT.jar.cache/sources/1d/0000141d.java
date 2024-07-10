package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlMapFactoryBean.class */
public class YamlMapFactoryBean extends YamlProcessor implements FactoryBean<Map<String, Object>>, InitializingBean {
    private boolean singleton = true;
    @Nullable
    private Map<String, Object> map;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (isSingleton()) {
            this.map = createMap();
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Map<String, Object> getObject() {
        return this.map != null ? this.map : createMap();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return Map.class;
    }

    protected Map<String, Object> createMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        process(properties, map -> {
            merge(result, map);
        });
        return result;
    }

    private void merge(Map<String, Object> output, Map<String, Object> map) {
        map.forEach(key, value -> {
            Object existing = output.get(key);
            if ((value instanceof Map) && (existing instanceof Map)) {
                LinkedHashMap linkedHashMap = new LinkedHashMap((Map) existing);
                merge(linkedHashMap, (Map) value);
                output.put(key, linkedHashMap);
                return;
            }
            output.put(key, value);
        });
    }
}