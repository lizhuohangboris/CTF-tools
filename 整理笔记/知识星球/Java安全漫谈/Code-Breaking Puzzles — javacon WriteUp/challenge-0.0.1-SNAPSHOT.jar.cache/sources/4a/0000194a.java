package org.springframework.boot.context.properties.source;

import java.util.function.Predicate;
import java.util.stream.Stream;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/FilteredIterableConfigurationPropertiesSource.class */
public class FilteredIterableConfigurationPropertiesSource extends FilteredConfigurationPropertiesSource implements IterableConfigurationPropertySource {
    public FilteredIterableConfigurationPropertiesSource(IterableConfigurationPropertySource source, Predicate<ConfigurationPropertyName> filter) {
        super(source, filter);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getSource().stream().filter(getFilter());
    }

    @Override // org.springframework.boot.context.properties.source.FilteredConfigurationPropertiesSource
    public IterableConfigurationPropertySource getSource() {
        return (IterableConfigurationPropertySource) super.getSource();
    }

    @Override // org.springframework.boot.context.properties.source.FilteredConfigurationPropertiesSource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        name.getClass();
        return ConfigurationPropertyState.search(this, this::isAncestorOf);
    }
}