package org.springframework.boot.autoconfigure.freemarker;

import java.util.Properties;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/freemarker/AbstractFreeMarkerConfiguration.class */
abstract class AbstractFreeMarkerConfiguration {
    private final FreeMarkerProperties properties;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFreeMarkerConfiguration(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FreeMarkerProperties getProperties() {
        return this.properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyProperties(FreeMarkerConfigurationFactory factory) {
        factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
        factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        factory.setDefaultEncoding(this.properties.getCharsetName());
        Properties settings = new Properties();
        settings.putAll(this.properties.getSettings());
        factory.setFreemarkerSettings(settings);
    }
}