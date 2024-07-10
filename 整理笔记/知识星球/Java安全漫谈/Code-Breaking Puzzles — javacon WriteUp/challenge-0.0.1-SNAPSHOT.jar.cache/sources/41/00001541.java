package org.springframework.boot.autoconfigure;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/ImportAutoConfigurationImportSelector.class */
class ImportAutoConfigurationImportSelector extends AutoConfigurationImportSelector implements DeterminableImports {
    private static final Set<String> ANNOTATION_NAMES;

    ImportAutoConfigurationImportSelector() {
    }

    static {
        Set<String> names = new LinkedHashSet<>();
        names.add(ImportAutoConfiguration.class.getName());
        names.add("org.springframework.boot.autoconfigure.test.ImportAutoConfiguration");
        ANNOTATION_NAMES = Collections.unmodifiableSet(names);
    }

    @Override // org.springframework.boot.context.annotation.DeterminableImports
    public Set<Object> determineImports(AnnotationMetadata metadata) {
        Set<String> result = new LinkedHashSet<>();
        result.addAll(getCandidateConfigurations(metadata, null));
        result.removeAll(getExclusions(metadata, null));
        return Collections.unmodifiableSet(result);
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        return null;
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> candidates = new ArrayList<>();
        Map<Class<?>, List<Annotation>> annotations = getAnnotations(metadata);
        annotations.forEach(source, sourceAnnotations -> {
            collectCandidateConfigurations(source, sourceAnnotations, candidates);
        });
        return candidates;
    }

    private void collectCandidateConfigurations(Class<?> source, List<Annotation> annotations, List<String> candidates) {
        for (Annotation annotation : annotations) {
            candidates.addAll(getConfigurationsForAnnotation(source, annotation));
        }
    }

    private Collection<String> getConfigurationsForAnnotation(Class<?> source, Annotation annotation) {
        String[] classes = (String[]) AnnotationUtils.getAnnotationAttributes(annotation, true).get("classes");
        if (classes.length > 0) {
            return Arrays.asList(classes);
        }
        return loadFactoryNames(source);
    }

    protected Collection<String> loadFactoryNames(Class<?> source) {
        return SpringFactoriesLoader.loadFactoryNames(source, getClass().getClassLoader());
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
    protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        Set<String> exclusions = new LinkedHashSet<>();
        Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
        for (String annotationName : ANNOTATION_NAMES) {
            AnnotationAttributes merged = AnnotatedElementUtils.getMergedAnnotationAttributes(source, annotationName);
            Class<?>[] exclude = merged != null ? merged.getClassArray("exclude") : null;
            if (exclude != null) {
                for (Class<?> excludeClass : exclude) {
                    exclusions.add(excludeClass.getName());
                }
            }
        }
        for (List<Annotation> annotations : getAnnotations(metadata).values()) {
            for (Annotation annotation : annotations) {
                String[] exclude2 = (String[]) AnnotationUtils.getAnnotationAttributes(annotation, true).get("exclude");
                if (!ObjectUtils.isEmpty((Object[]) exclude2)) {
                    exclusions.addAll(Arrays.asList(exclude2));
                }
            }
        }
        return exclusions;
    }

    protected final Map<Class<?>, List<Annotation>> getAnnotations(AnnotationMetadata metadata) {
        MultiValueMap<Class<?>, Annotation> annotations = new LinkedMultiValueMap<>();
        Class<?> source = ClassUtils.resolveClassName(metadata.getClassName(), null);
        collectAnnotations(source, annotations, new HashSet<>());
        return Collections.unmodifiableMap(annotations);
    }

    private void collectAnnotations(Class<?> source, MultiValueMap<Class<?>, Annotation> annotations, HashSet<Class<?>> seen) {
        Annotation[] declaredAnnotations;
        if (source != null && seen.add(source)) {
            for (Annotation annotation : source.getDeclaredAnnotations()) {
                if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
                    if (ANNOTATION_NAMES.contains(annotation.annotationType().getName())) {
                        annotations.add(source, annotation);
                    }
                    collectAnnotations(annotation.annotationType(), annotations, seen);
                }
            }
            collectAnnotations(source.getSuperclass(), annotations, seen);
        }
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportSelector, org.springframework.core.Ordered
    public int getOrder() {
        return super.getOrder() - 1;
    }

    @Override // org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
    protected void handleInvalidExcludes(List<String> invalidExcludes) {
    }
}