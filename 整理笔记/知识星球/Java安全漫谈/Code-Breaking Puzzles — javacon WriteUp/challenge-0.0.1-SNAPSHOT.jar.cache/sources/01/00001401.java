package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/MapFactoryBean.class */
public class MapFactoryBean extends AbstractFactoryBean<Map<Object, Object>> {
    @Nullable
    private Map<?, ?> sourceMap;
    @Nullable
    private Class<? extends Map> targetMapClass;

    public void setSourceMap(Map<?, ?> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public void setTargetMapClass(@Nullable Class<? extends Map> targetMapClass) {
        if (targetMapClass == null) {
            throw new IllegalArgumentException("'targetMapClass' must not be null");
        }
        if (!Map.class.isAssignableFrom(targetMapClass)) {
            throw new IllegalArgumentException("'targetMapClass' must implement [java.util.Map]");
        }
        this.targetMapClass = targetMapClass;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<Map> getObjectType() {
        return Map.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v42, types: [java.util.Map] */
    @Override // org.springframework.beans.factory.config.AbstractFactoryBean
    public Map<Object, Object> createInstance() {
        LinkedHashMap linkedHashMap;
        if (this.sourceMap == null) {
            throw new IllegalArgumentException("'sourceMap' is required");
        }
        if (this.targetMapClass != null) {
            linkedHashMap = (Map) BeanUtils.instantiateClass(this.targetMapClass);
        } else {
            linkedHashMap = new LinkedHashMap(this.sourceMap.size());
        }
        Class<?> keyType = null;
        Class<?> valueType = null;
        if (this.targetMapClass != null) {
            ResolvableType mapType = ResolvableType.forClass(this.targetMapClass).asMap();
            keyType = mapType.resolveGeneric(0);
            valueType = mapType.resolveGeneric(1);
        }
        if (keyType != null || valueType != null) {
            TypeConverter converter = getBeanTypeConverter();
            for (Map.Entry<?, ?> entry : this.sourceMap.entrySet()) {
                Object convertedKey = converter.convertIfNecessary(entry.getKey(), keyType);
                Object convertedValue = converter.convertIfNecessary(entry.getValue(), valueType);
                linkedHashMap.put(convertedKey, convertedValue);
            }
        } else {
            linkedHashMap.putAll(this.sourceMap);
        }
        return linkedHashMap;
    }
}