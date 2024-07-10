package org.springframework.core.env;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/SystemEnvironmentPropertySource.class */
public class SystemEnvironmentPropertySource extends MapPropertySource {
    public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        return getProperty(name) != null;
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        String actualName = resolvePropertyName(name);
        if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
            this.logger.debug("PropertySource '" + getName() + "' does not contain property '" + name + "', but found equivalent '" + actualName + "'");
        }
        return super.getProperty(actualName);
    }

    public final String resolvePropertyName(String name) {
        String resolvedName;
        Assert.notNull(name, "Property name must not be null");
        String resolvedName2 = checkPropertyName(name);
        if (resolvedName2 != null) {
            return resolvedName2;
        }
        String uppercasedName = name.toUpperCase();
        if (!name.equals(uppercasedName) && (resolvedName = checkPropertyName(uppercasedName)) != null) {
            return resolvedName;
        }
        return name;
    }

    @Nullable
    private String checkPropertyName(String name) {
        if (containsKey(name)) {
            return name;
        }
        String noDotName = name.replace('.', '_');
        if (!name.equals(noDotName) && containsKey(noDotName)) {
            return noDotName;
        }
        String noHyphenName = name.replace('-', '_');
        if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
            return noHyphenName;
        }
        String noDotNoHyphenName = noDotName.replace('-', '_');
        if (!noDotName.equals(noDotNoHyphenName) && containsKey(noDotNoHyphenName)) {
            return noDotNoHyphenName;
        }
        return null;
    }

    private boolean containsKey(String name) {
        return isSecurityManagerPresent() ? ((Map) this.source).keySet().contains(name) : ((Map) this.source).containsKey(name);
    }

    protected boolean isSecurityManagerPresent() {
        return System.getSecurityManager() != null;
    }
}