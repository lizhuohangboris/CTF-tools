package org.springframework.boot.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages.class */
public abstract class AutoConfigurationPackages {
    private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);
    private static final String BEAN = AutoConfigurationPackages.class.getName();

    public static boolean has(BeanFactory beanFactory) {
        return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
    }

    public static List<String> get(BeanFactory beanFactory) {
        try {
            return ((BasePackages) beanFactory.getBean(BEAN, BasePackages.class)).get();
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("Unable to retrieve @EnableAutoConfiguration base packages");
        }
    }

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        if (registry.containsBeanDefinition(BEAN)) {
            ConstructorArgumentValues constructorArguments = registry.getBeanDefinition(BEAN).getConstructorArgumentValues();
            constructorArguments.addIndexedArgumentValue(0, addBasePackages(constructorArguments, packageNames));
            return;
        }
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(BasePackages.class);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, packageNames);
        beanDefinition.setRole(2);
        registry.registerBeanDefinition(BEAN, beanDefinition);
    }

    private static String[] addBasePackages(ConstructorArgumentValues constructorArguments, String[] packageNames) {
        String[] existing = (String[]) constructorArguments.getIndexedArgumentValue(0, String[].class).getValue();
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(Arrays.asList(existing));
        merged.addAll(Arrays.asList(packageNames));
        return StringUtils.toStringArray(merged);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$Registrar.class */
    static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
        Registrar() {
        }

        @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            AutoConfigurationPackages.register(registry, new PackageImport(metadata).getPackageName());
        }

        @Override // org.springframework.boot.context.annotation.DeterminableImports
        public Set<Object> determineImports(AnnotationMetadata metadata) {
            return Collections.singleton(new PackageImport(metadata));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$PackageImport.class */
    private static final class PackageImport {
        private final String packageName;

        PackageImport(AnnotationMetadata metadata) {
            this.packageName = ClassUtils.getPackageName(metadata.getClassName());
        }

        public String getPackageName() {
            return this.packageName;
        }

        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.packageName.equals(((PackageImport) obj).packageName);
        }

        public int hashCode() {
            return this.packageName.hashCode();
        }

        public String toString() {
            return "Package Import " + this.packageName;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/AutoConfigurationPackages$BasePackages.class */
    public static final class BasePackages {
        private final List<String> packages;
        private boolean loggedBasePackageInfo;

        BasePackages(String... names) {
            List<String> packages = new ArrayList<>();
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    packages.add(name);
                }
            }
            this.packages = packages;
        }

        public List<String> get() {
            if (!this.loggedBasePackageInfo) {
                if (this.packages.isEmpty()) {
                    if (AutoConfigurationPackages.logger.isWarnEnabled()) {
                        AutoConfigurationPackages.logger.warn("@EnableAutoConfiguration was declared on a class in the default package. Automatic @Repository and @Entity scanning is not enabled.");
                    }
                } else if (AutoConfigurationPackages.logger.isDebugEnabled()) {
                    String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
                    AutoConfigurationPackages.logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames + "'. Automatic @Repository and @Entity scanning is enabled.");
                }
                this.loggedBasePackageInfo = true;
            }
            return this.packages;
        }
    }
}