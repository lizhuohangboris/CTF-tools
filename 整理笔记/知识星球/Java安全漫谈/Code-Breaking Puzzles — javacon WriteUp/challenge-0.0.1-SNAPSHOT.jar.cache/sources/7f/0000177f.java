package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.DefaultCollector;
import com.samskivert.mustache.Mustache;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheEnvironmentCollector.class */
public class MustacheEnvironmentCollector extends DefaultCollector implements EnvironmentAware {
    private ConfigurableEnvironment environment;
    private final Mustache.VariableFetcher propertyFetcher = new PropertyVariableFetcher();

    @Override // org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    public Mustache.VariableFetcher createFetcher(Object ctx, String name) {
        Mustache.VariableFetcher fetcher = super.createFetcher(ctx, name);
        if (fetcher != null) {
            return fetcher;
        }
        if (this.environment.containsProperty(name)) {
            return this.propertyFetcher;
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mustache/MustacheEnvironmentCollector$PropertyVariableFetcher.class */
    private class PropertyVariableFetcher implements Mustache.VariableFetcher {
        private PropertyVariableFetcher() {
        }

        public Object get(Object ctx, String name) throws Exception {
            return MustacheEnvironmentCollector.this.environment.getProperty(name);
        }
    }
}