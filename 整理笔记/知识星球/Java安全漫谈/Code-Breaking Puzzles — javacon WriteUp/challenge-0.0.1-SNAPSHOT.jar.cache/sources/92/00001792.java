package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties("spring.jpa.hibernate")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateProperties.class */
public class HibernateProperties {
    private final Naming naming = new Naming();
    private String ddlAuto;
    private Boolean useNewIdGeneratorMappings;

    public String getDdlAuto() {
        return this.ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public Boolean isUseNewIdGeneratorMappings() {
        return this.useNewIdGeneratorMappings;
    }

    public void setUseNewIdGeneratorMappings(Boolean useNewIdGeneratorMappings) {
        this.useNewIdGeneratorMappings = useNewIdGeneratorMappings;
    }

    public Naming getNaming() {
        return this.naming;
    }

    public Map<String, Object> determineHibernateProperties(Map<String, String> jpaProperties, HibernateSettings settings) {
        Assert.notNull(jpaProperties, "JpaProperties must not be null");
        Assert.notNull(settings, "Settings must not be null");
        return getAdditionalProperties(jpaProperties, settings);
    }

    private Map<String, Object> getAdditionalProperties(Map<String, String> existing, HibernateSettings settings) {
        Map<String, Object> result = new HashMap<>(existing);
        applyNewIdGeneratorMappings(result);
        getNaming().applyNamingStrategies(result);
        settings.getClass();
        String ddlAuto = determineDdlAuto(existing, this::getDdlAuto);
        if (StringUtils.hasText(ddlAuto) && !"none".equals(ddlAuto)) {
            result.put("hibernate.hbm2ddl.auto", ddlAuto);
        } else {
            result.remove("hibernate.hbm2ddl.auto");
        }
        Collection<HibernatePropertiesCustomizer> customizers = settings.getHibernatePropertiesCustomizers();
        if (!ObjectUtils.isEmpty(customizers)) {
            customizers.forEach(customizer -> {
                customizer.customize(result);
            });
        }
        return result;
    }

    private void applyNewIdGeneratorMappings(Map<String, Object> result) {
        if (this.useNewIdGeneratorMappings != null) {
            result.put("hibernate.id.new_generator_mappings", this.useNewIdGeneratorMappings.toString());
        } else if (!result.containsKey("hibernate.id.new_generator_mappings")) {
            result.put("hibernate.id.new_generator_mappings", "true");
        }
    }

    private String determineDdlAuto(Map<String, String> existing, Supplier<String> defaultDdlAuto) {
        String ddlAuto = existing.get("hibernate.hbm2ddl.auto");
        if (ddlAuto != null) {
            return ddlAuto;
        }
        return this.ddlAuto != null ? this.ddlAuto : defaultDdlAuto.get();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateProperties$Naming.class */
    public static class Naming {
        private String implicitStrategy;
        private String physicalStrategy;

        public String getImplicitStrategy() {
            return this.implicitStrategy;
        }

        public void setImplicitStrategy(String implicitStrategy) {
            this.implicitStrategy = implicitStrategy;
        }

        public String getPhysicalStrategy() {
            return this.physicalStrategy;
        }

        public void setPhysicalStrategy(String physicalStrategy) {
            this.physicalStrategy = physicalStrategy;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void applyNamingStrategies(Map<String, Object> properties) {
            applyNamingStrategy(properties, "hibernate.implicit_naming_strategy", this.implicitStrategy, SpringImplicitNamingStrategy.class.getName());
            applyNamingStrategy(properties, "hibernate.physical_naming_strategy", this.physicalStrategy, SpringPhysicalNamingStrategy.class.getName());
        }

        private void applyNamingStrategy(Map<String, Object> properties, String key, Object strategy, Object defaultStrategy) {
            if (strategy != null) {
                properties.put(key, strategy);
            } else if (defaultStrategy != null && !properties.containsKey(key)) {
                properties.put(key, defaultStrategy);
            }
        }
    }
}