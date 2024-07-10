package org.springframework.boot.context.properties.source;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.util.CollectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/AliasedIterableConfigurationPropertySource.class */
public class AliasedIterableConfigurationPropertySource extends AliasedConfigurationPropertySource implements IterableConfigurationPropertySource {
    public AliasedIterableConfigurationPropertySource(IterableConfigurationPropertySource source, ConfigurationPropertyNameAliases aliases) {
        super(source, aliases);
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getSource().stream().flatMap(this::addAliases);
    }

    private Stream<ConfigurationPropertyName> addAliases(ConfigurationPropertyName name) {
        Stream<ConfigurationPropertyName> names = Stream.of(name);
        List<ConfigurationPropertyName> aliases = getAliases().getAliases(name);
        if (CollectionUtils.isEmpty(aliases)) {
            return names;
        }
        return Stream.concat(names, aliases.stream());
    }

    @Override // org.springframework.boot.context.properties.source.AliasedConfigurationPropertySource
    public IterableConfigurationPropertySource getSource() {
        return (IterableConfigurationPropertySource) super.getSource();
    }
}