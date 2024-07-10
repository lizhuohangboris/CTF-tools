package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

@Order(-2147483628)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnResourceCondition.class */
class OnResourceCondition extends SpringBootCondition {
    private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    OnResourceCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnResource.class.getName(), true);
        ResourceLoader loader = context.getResourceLoader() != null ? context.getResourceLoader() : this.defaultResourceLoader;
        List<String> locations = new ArrayList<>();
        collectValues(locations, (List) attributes.get("resources"));
        Assert.isTrue(!locations.isEmpty(), "@ConditionalOnResource annotations must specify at least one resource location");
        List<String> missing = new ArrayList<>();
        for (String location : locations) {
            String resource = context.getEnvironment().resolvePlaceholders(location);
            if (!loader.getResource(resource).exists()) {
                missing.add(location);
            }
        }
        if (!missing.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnResource.class, new Object[0]).didNotFind(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, "resources").items(ConditionMessage.Style.QUOTE, missing));
        }
        return ConditionOutcome.match(ConditionMessage.forCondition(ConditionalOnResource.class, new Object[0]).found("location", "locations").items(locations));
    }

    private void collectValues(List<String> names, List<Object> values) {
        Object[] objArr;
        for (Object value : values) {
            for (Object item : (Object[]) value) {
                names.add((String) item);
            }
        }
    }
}