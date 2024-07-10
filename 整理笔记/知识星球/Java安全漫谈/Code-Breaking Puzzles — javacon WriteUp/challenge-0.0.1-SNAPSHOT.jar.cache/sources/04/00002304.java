package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ui/ExtendedModelMap.class */
public class ExtendedModelMap extends ModelMap implements Model {
    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap mergeAttributes(@Nullable Map map) {
        return mergeAttributes((Map<String, ?>) map);
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap addAllAttributes(@Nullable Map map) {
        return addAllAttributes((Map<String, ?>) map);
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap addAllAttributes(@Nullable Collection collection) {
        return addAllAttributes((Collection<?>) collection);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model mergeAttributes(@Nullable Map map) {
        return mergeAttributes((Map<String, ?>) map);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Map map) {
        return addAllAttributes((Map<String, ?>) map);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Collection collection) {
        return addAllAttributes((Collection<?>) collection);
    }

    @Override // org.springframework.ui.Model
    public ExtendedModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
        return this;
    }

    @Override // org.springframework.ui.Model
    public ExtendedModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        super.addAllAttributes(attributes);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        super.mergeAttributes(attributes);
        return this;
    }

    @Override // org.springframework.ui.Model
    public Map<String, Object> asMap() {
        return this;
    }
}