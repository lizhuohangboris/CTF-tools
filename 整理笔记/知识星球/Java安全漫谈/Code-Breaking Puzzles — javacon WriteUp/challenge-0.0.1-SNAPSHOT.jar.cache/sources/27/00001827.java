package org.springframework.boot.autoconfigure.thymeleaf;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafTemplateAvailabilityProvider.class */
public class ThymeleafTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
    @Override // org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
    public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent("org.thymeleaf.spring5.SpringTemplateEngine", classLoader)) {
            String prefix = environment.getProperty("spring.thymeleaf.prefix", "classpath:/templates/");
            String suffix = environment.getProperty("spring.thymeleaf.suffix", ThymeleafProperties.DEFAULT_SUFFIX);
            return resourceLoader.getResource(prefix + view + suffix).exists();
        }
        return false;
    }
}