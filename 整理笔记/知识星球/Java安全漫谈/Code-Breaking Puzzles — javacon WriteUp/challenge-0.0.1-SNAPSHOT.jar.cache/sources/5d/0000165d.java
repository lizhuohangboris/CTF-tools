package org.springframework.boot.autoconfigure.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/domain/EntityScanPackages.class */
public class EntityScanPackages {
    private static final String BEAN = EntityScanPackages.class.getName();
    private static final EntityScanPackages NONE = new EntityScanPackages(new String[0]);
    private final List<String> packageNames;

    EntityScanPackages(String... packageNames) {
        List<String> packages = new ArrayList<>();
        for (String name : packageNames) {
            if (StringUtils.hasText(name)) {
                packages.add(name);
            }
        }
        this.packageNames = Collections.unmodifiableList(packages);
    }

    public List<String> getPackageNames() {
        return this.packageNames;
    }

    public static EntityScanPackages get(BeanFactory beanFactory) {
        try {
            return (EntityScanPackages) beanFactory.getBean(BEAN, EntityScanPackages.class);
        } catch (NoSuchBeanDefinitionException e) {
            return NONE;
        }
    }

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        register(registry, Arrays.asList(packageNames));
    }

    public static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
        Assert.notNull(registry, "Registry must not be null");
        Assert.notNull(packageNames, "PackageNames must not be null");
        if (registry.containsBeanDefinition(BEAN)) {
            ConstructorArgumentValues constructorArguments = registry.getBeanDefinition(BEAN).getConstructorArgumentValues();
            constructorArguments.addIndexedArgumentValue(0, addPackageNames(constructorArguments, packageNames));
            return;
        }
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(EntityScanPackages.class);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, StringUtils.toStringArray(packageNames));
        beanDefinition.setRole(2);
        registry.registerBeanDefinition(BEAN, beanDefinition);
    }

    private static String[] addPackageNames(ConstructorArgumentValues constructorArguments, Collection<String> packageNames) {
        String[] existing = (String[]) constructorArguments.getIndexedArgumentValue(0, String[].class).getValue();
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(Arrays.asList(existing));
        merged.addAll(packageNames);
        return StringUtils.toStringArray(merged);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/domain/EntityScanPackages$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar {
        Registrar() {
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            EntityScanPackages.register(registry, getPackagesToScan(metadata));
        }

        private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EntityScan.class.getName()));
            String[] basePackages = attributes.getStringArray("basePackages");
            Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
            Set<String> packagesToScan = new LinkedHashSet<>();
            packagesToScan.addAll(Arrays.asList(basePackages));
            for (Class<?> basePackageClass : basePackageClasses) {
                packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
            }
            if (packagesToScan.isEmpty()) {
                String packageName = ClassUtils.getPackageName(metadata.getClassName());
                Assert.state(!StringUtils.isEmpty(packageName), "@EntityScan cannot be used with the default package");
                return Collections.singleton(packageName);
            }
            return packagesToScan;
        }
    }
}