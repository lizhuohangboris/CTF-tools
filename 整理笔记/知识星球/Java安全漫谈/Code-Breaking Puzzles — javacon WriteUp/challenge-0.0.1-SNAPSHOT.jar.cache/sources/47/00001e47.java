package org.springframework.core.env;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/AbstractEnvironment.class */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {
    public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";
    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";
    public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";
    protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";
    protected final Log logger = LogFactory.getLog(getClass());
    private final Set<String> activeProfiles = new LinkedHashSet();
    private final Set<String> defaultProfiles = new LinkedHashSet(getReservedDefaultProfiles());
    private final MutablePropertySources propertySources = new MutablePropertySources();
    private final ConfigurablePropertyResolver propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);

    public AbstractEnvironment() {
        customizePropertySources(this.propertySources);
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    protected Set<String> getReservedDefaultProfiles() {
        return Collections.singleton("default");
    }

    @Override // org.springframework.core.env.Environment
    public String[] getActiveProfiles() {
        return StringUtils.toStringArray(doGetActiveProfiles());
    }

    protected Set<String> doGetActiveProfiles() {
        Set<String> set;
        synchronized (this.activeProfiles) {
            if (this.activeProfiles.isEmpty()) {
                String profiles = getProperty("spring.profiles.active");
                if (StringUtils.hasText(profiles)) {
                    setActiveProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }
            set = this.activeProfiles;
        }
        return set;
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void setActiveProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Activating profiles " + Arrays.asList(profiles));
        }
        synchronized (this.activeProfiles) {
            this.activeProfiles.clear();
            for (String profile : profiles) {
                validateProfile(profile);
                this.activeProfiles.add(profile);
            }
        }
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void addActiveProfile(String profile) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Activating profile '" + profile + "'");
        }
        validateProfile(profile);
        doGetActiveProfiles();
        synchronized (this.activeProfiles) {
            this.activeProfiles.add(profile);
        }
    }

    @Override // org.springframework.core.env.Environment
    public String[] getDefaultProfiles() {
        return StringUtils.toStringArray(doGetDefaultProfiles());
    }

    protected Set<String> doGetDefaultProfiles() {
        Set<String> set;
        synchronized (this.defaultProfiles) {
            if (this.defaultProfiles.equals(getReservedDefaultProfiles())) {
                String profiles = getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
                if (StringUtils.hasText(profiles)) {
                    setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }
            set = this.defaultProfiles;
        }
        return set;
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void setDefaultProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        synchronized (this.defaultProfiles) {
            this.defaultProfiles.clear();
            for (String profile : profiles) {
                validateProfile(profile);
                this.defaultProfiles.add(profile);
            }
        }
    }

    @Override // org.springframework.core.env.Environment
    @Deprecated
    public boolean acceptsProfiles(String... profiles) {
        Assert.notEmpty(profiles, "Must specify at least one profile");
        for (String profile : profiles) {
            if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
                if (!isProfileActive(profile.substring(1))) {
                    return true;
                }
            } else if (isProfileActive(profile)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.env.Environment
    public boolean acceptsProfiles(Profiles profiles) {
        Assert.notNull(profiles, "Profiles must not be null");
        return profiles.matches(this::isProfileActive);
    }

    protected boolean isProfileActive(String profile) {
        validateProfile(profile);
        Set<String> currentActiveProfiles = doGetActiveProfiles();
        return currentActiveProfiles.contains(profile) || (currentActiveProfiles.isEmpty() && doGetDefaultProfiles().contains(profile));
    }

    protected void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
        }
        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
        }
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public Map<String, Object> getSystemProperties() {
        try {
            return System.getProperties();
        } catch (AccessControlException e) {
            return new ReadOnlySystemAttributesMap() { // from class: org.springframework.core.env.AbstractEnvironment.1
                {
                    AbstractEnvironment.this = this;
                }

                @Override // org.springframework.core.env.ReadOnlySystemAttributesMap
                @Nullable
                protected String getSystemAttribute(String attributeName) {
                    try {
                        return System.getProperty(attributeName);
                    } catch (AccessControlException ex) {
                        if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                            AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system property '" + attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
                            return null;
                        }
                        return null;
                    }
                }
            };
        }
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public Map<String, Object> getSystemEnvironment() {
        if (suppressGetenvAccess()) {
            return Collections.emptyMap();
        }
        try {
            return System.getenv();
        } catch (AccessControlException e) {
            return new ReadOnlySystemAttributesMap() { // from class: org.springframework.core.env.AbstractEnvironment.2
                {
                    AbstractEnvironment.this = this;
                }

                @Override // org.springframework.core.env.ReadOnlySystemAttributesMap
                @Nullable
                protected String getSystemAttribute(String attributeName) {
                    try {
                        return System.getenv(attributeName);
                    } catch (AccessControlException ex) {
                        if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                            AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system environment variable '" + attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
                            return null;
                        }
                        return null;
                    }
                }
            };
        }
    }

    protected boolean suppressGetenvAccess() {
        return SpringProperties.getFlag(IGNORE_GETENV_PROPERTY_NAME);
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void merge(ConfigurableEnvironment parent) {
        Iterator<PropertySource<?>> it = parent.getPropertySources().iterator();
        while (it.hasNext()) {
            PropertySource<?> ps = it.next();
            if (!this.propertySources.contains(ps.getName())) {
                this.propertySources.addLast(ps);
            }
        }
        String[] parentActiveProfiles = parent.getActiveProfiles();
        if (!ObjectUtils.isEmpty((Object[]) parentActiveProfiles)) {
            synchronized (this.activeProfiles) {
                for (String profile : parentActiveProfiles) {
                    this.activeProfiles.add(profile);
                }
            }
        }
        String[] parentDefaultProfiles = parent.getDefaultProfiles();
        if (!ObjectUtils.isEmpty((Object[]) parentDefaultProfiles)) {
            synchronized (this.defaultProfiles) {
                this.defaultProfiles.remove("default");
                for (String profile2 : parentDefaultProfiles) {
                    this.defaultProfiles.add(profile2);
                }
            }
        }
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public ConfigurableConversionService getConversionService() {
        return this.propertyResolver.getConversionService();
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setConversionService(ConfigurableConversionService conversionService) {
        this.propertyResolver.setConversionService(conversionService);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setValueSeparator(@Nullable String valueSeparator) {
        this.propertyResolver.setValueSeparator(valueSeparator);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setRequiredProperties(String... requiredProperties) {
        this.propertyResolver.setRequiredProperties(requiredProperties);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
        this.propertyResolver.validateRequiredProperties();
    }

    @Override // org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        return this.propertyResolver.containsProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public <T> T getProperty(String key, Class<T> targetType) {
        return (T) this.propertyResolver.getProperty(key, targetType);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return (T) this.propertyResolver.getProperty(key, targetType, defaultValue);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getRequiredProperty(String key) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return (T) this.propertyResolver.getRequiredProperty(key, targetType);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolvePlaceholders(String text) {
        return this.propertyResolver.resolvePlaceholders(text);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }

    public String toString() {
        return getClass().getSimpleName() + " {activeProfiles=" + this.activeProfiles + ", defaultProfiles=" + this.defaultProfiles + ", propertySources=" + this.propertySources + "}";
    }
}