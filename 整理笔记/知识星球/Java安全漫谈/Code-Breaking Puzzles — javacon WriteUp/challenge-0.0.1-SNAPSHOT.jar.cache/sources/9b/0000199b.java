package org.springframework.boot.env;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/SystemEnvironmentPropertySourceEnvironmentPostProcessor.class */
public class SystemEnvironmentPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int DEFAULT_ORDER = -2147483644;
    private int order = DEFAULT_ORDER;

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PropertySource<?> propertySource = environment.getPropertySources().get("systemEnvironment");
        if (propertySource != null) {
            replacePropertySource(environment, "systemEnvironment", propertySource);
        }
    }

    private void replacePropertySource(ConfigurableEnvironment environment, String sourceName, PropertySource<?> propertySource) {
        Map<String, Object> originalSource = (Map) propertySource.getSource();
        SystemEnvironmentPropertySource source = new OriginAwareSystemEnvironmentPropertySource(sourceName, originalSource);
        environment.getPropertySources().replace(sourceName, source);
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/env/SystemEnvironmentPropertySourceEnvironmentPostProcessor$OriginAwareSystemEnvironmentPropertySource.class */
    public static class OriginAwareSystemEnvironmentPropertySource extends SystemEnvironmentPropertySource implements OriginLookup<String> {
        OriginAwareSystemEnvironmentPropertySource(String name, Map<String, Object> source) {
            super(name, source);
        }

        @Override // org.springframework.boot.origin.OriginLookup
        public Origin getOrigin(String key) {
            String property = resolvePropertyName(key);
            if (super.containsProperty(property)) {
                return new SystemEnvironmentOrigin(property);
            }
            return null;
        }
    }
}