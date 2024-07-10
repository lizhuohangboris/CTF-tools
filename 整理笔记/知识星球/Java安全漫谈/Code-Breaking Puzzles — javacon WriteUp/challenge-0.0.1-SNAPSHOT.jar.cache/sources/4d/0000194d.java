package org.springframework.boot.context.properties.source;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/IterableConfigurationPropertySource.class */
public interface IterableConfigurationPropertySource extends ConfigurationPropertySource, Iterable<ConfigurationPropertyName> {
    Stream<ConfigurationPropertyName> stream();

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    /* bridge */ /* synthetic */ default ConfigurationPropertySource filter(Predicate filter) {
        return filter((Predicate<ConfigurationPropertyName>) filter);
    }

    @Override // java.lang.Iterable
    default Iterator<ConfigurationPropertyName> iterator() {
        return stream().iterator();
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        name.getClass();
        return ConfigurationPropertyState.search(this, this::isAncestorOf);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default IterableConfigurationPropertySource filter(Predicate<ConfigurationPropertyName> filter) {
        return new FilteredIterableConfigurationPropertiesSource(this, filter);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    default IterableConfigurationPropertySource withAliases(ConfigurationPropertyNameAliases aliases) {
        return new AliasedIterableConfigurationPropertySource(this, aliases);
    }
}