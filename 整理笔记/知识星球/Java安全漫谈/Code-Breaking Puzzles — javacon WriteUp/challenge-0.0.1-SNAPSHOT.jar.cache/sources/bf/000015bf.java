package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/FilteringSpringBootCondition.class */
abstract class FilteringSpringBootCondition extends SpringBootCondition implements AutoConfigurationImportFilter, BeanFactoryAware, BeanClassLoaderAware {
    private BeanFactory beanFactory;
    private ClassLoader beanClassLoader;

    protected abstract ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata);

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportFilter
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        ConditionEvaluationReport report = ConditionEvaluationReport.find(this.beanFactory);
        ConditionOutcome[] outcomes = getOutcomes(autoConfigurationClasses, autoConfigurationMetadata);
        boolean[] match = new boolean[outcomes.length];
        for (int i = 0; i < outcomes.length; i++) {
            match[i] = outcomes[i] == null || outcomes[i].isMatch();
            if (!match[i] && outcomes[i] != null) {
                logOutcome(autoConfigurationClasses[i], outcomes[i]);
                if (report != null) {
                    report.recordConditionEvaluation(autoConfigurationClasses[i], this, outcomes[i]);
                }
            }
        }
        return match;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<String> filter(Collection<String> classNames, ClassNameFilter classNameFilter, ClassLoader classLoader) {
        if (CollectionUtils.isEmpty(classNames)) {
            return Collections.emptyList();
        }
        List<String> matches = new ArrayList<>(classNames.size());
        for (String candidate : classNames) {
            if (classNameFilter.matches(candidate, classLoader)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/FilteringSpringBootCondition$ClassNameFilter.class */
    protected enum ClassNameFilter {
        PRESENT { // from class: org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition.ClassNameFilter.1
            @Override // org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition.ClassNameFilter
            public boolean matches(String className, ClassLoader classLoader) {
                return isPresent(className, classLoader);
            }
        },
        MISSING { // from class: org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition.ClassNameFilter.2
            @Override // org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition.ClassNameFilter
            public boolean matches(String className, ClassLoader classLoader) {
                return !isPresent(className, classLoader);
            }
        };

        public abstract boolean matches(String className, ClassLoader classLoader);

        public static boolean isPresent(String className, ClassLoader classLoader) {
            if (classLoader == null) {
                classLoader = ClassUtils.getDefaultClassLoader();
            }
            try {
                forName(className, classLoader);
                return true;
            } catch (Throwable th) {
                return false;
            }
        }

        private static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
            if (classLoader != null) {
                return classLoader.loadClass(className);
            }
            return Class.forName(className);
        }
    }
}