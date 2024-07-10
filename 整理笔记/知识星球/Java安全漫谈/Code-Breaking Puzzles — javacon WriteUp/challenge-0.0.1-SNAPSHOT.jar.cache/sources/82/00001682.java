package org.springframework.boot.autoconfigure.groovy.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAvailabilityProvider.class */
public class GroovyTemplateAvailabilityProvider extends PathBasedTemplateAvailabilityProvider {
    public GroovyTemplateAvailabilityProvider() {
        super("groovy.text.TemplateEngine", GroovyTemplateAvailabilityProperties.class, "spring.groovy.template");
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAvailabilityProvider$GroovyTemplateAvailabilityProperties.class */
    static final class GroovyTemplateAvailabilityProperties extends PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties {
        private List<String> resourceLoaderPath;

        GroovyTemplateAvailabilityProperties() {
            super("", GroovyTemplateProperties.DEFAULT_SUFFIX);
            this.resourceLoaderPath = new ArrayList(Arrays.asList("classpath:/templates/"));
        }

        @Override // org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties
        protected List<String> getLoaderPath() {
            return this.resourceLoaderPath;
        }

        public List<String> getResourceLoaderPath() {
            return this.resourceLoaderPath;
        }

        public void setResourceLoaderPath(List<String> resourceLoaderPath) {
            this.resourceLoaderPath = resourceLoaderPath;
        }
    }
}