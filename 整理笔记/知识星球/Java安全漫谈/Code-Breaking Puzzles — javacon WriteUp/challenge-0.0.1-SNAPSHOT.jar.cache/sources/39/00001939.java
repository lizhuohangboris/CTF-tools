package org.springframework.boot.context.properties.source;

import java.util.Iterator;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/AliasedConfigurationPropertySource.class */
public class AliasedConfigurationPropertySource implements ConfigurationPropertySource {
    private final ConfigurationPropertySource source;
    private final ConfigurationPropertyNameAliases aliases;

    public AliasedConfigurationPropertySource(ConfigurationPropertySource source, ConfigurationPropertyNameAliases aliases) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(aliases, "Aliases must not be null");
        this.source = source;
        this.aliases = aliases;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        ConfigurationProperty result = getSource().getConfigurationProperty(name);
        if (result == null) {
            ConfigurationPropertyName aliasedName = getAliases().getNameForAlias(name);
            result = getSource().getConfigurationProperty(aliasedName);
        }
        return result;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        ConfigurationPropertyState result = this.source.containsDescendantOf(name);
        if (result != ConfigurationPropertyState.ABSENT) {
            return result;
        }
        for (ConfigurationPropertyName alias : getAliases().getAliases(name)) {
            ConfigurationPropertyState aliasResult = this.source.containsDescendantOf(alias);
            if (aliasResult != ConfigurationPropertyState.ABSENT) {
                return aliasResult;
            }
        }
        Iterator<ConfigurationPropertyName> it = getAliases().iterator();
        while (it.hasNext()) {
            ConfigurationPropertyName from = it.next();
            for (ConfigurationPropertyName alias2 : getAliases().getAliases(from)) {
                if (name.isAncestorOf(alias2) && this.source.getConfigurationProperty(from) != null) {
                    return ConfigurationPropertyState.PRESENT;
                }
            }
        }
        return ConfigurationPropertyState.ABSENT;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.source.getUnderlyingSource();
    }

    public ConfigurationPropertySource getSource() {
        return this.source;
    }

    public ConfigurationPropertyNameAliases getAliases() {
        return this.aliases;
    }
}