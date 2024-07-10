package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.condition.BeanTypeRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

@Order(Integer.MAX_VALUE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnBeanCondition.class */
class OnBeanCondition extends FilteringSpringBootCondition implements ConfigurationCondition {
    public static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

    OnBeanCondition() {
    }

    @Override // org.springframework.context.annotation.ConfigurationCondition
    public ConfigurationCondition.ConfigurationPhase getConfigurationPhase() {
        return ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN;
    }

    @Override // org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition
    protected final ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
        for (int i = 0; i < outcomes.length; i++) {
            String autoConfigurationClass = autoConfigurationClasses[i];
            if (autoConfigurationClass != null) {
                Set<String> onBeanTypes = autoConfigurationMetadata.getSet(autoConfigurationClass, "ConditionalOnBean");
                outcomes[i] = getOutcome(onBeanTypes, ConditionalOnBean.class);
                if (outcomes[i] == null) {
                    Set<String> onSingleCandidateTypes = autoConfigurationMetadata.getSet(autoConfigurationClass, "ConditionalOnSingleCandidate");
                    outcomes[i] = getOutcome(onSingleCandidateTypes, ConditionalOnSingleCandidate.class);
                }
            }
        }
        return outcomes;
    }

    private ConditionOutcome getOutcome(Set<String> requiredBeanTypes, Class<? extends Annotation> annotation) {
        List<String> missing = filter(requiredBeanTypes, FilteringSpringBootCondition.ClassNameFilter.MISSING, getBeanClassLoader());
        if (!missing.isEmpty()) {
            ConditionMessage message = ConditionMessage.forCondition(annotation, new Object[0]).didNotFind("required type", "required types").items(ConditionMessage.Style.QUOTE, missing);
            return ConditionOutcome.noMatch(message);
        }
        return null;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage matchMessage = ConditionMessage.empty();
        if (metadata.isAnnotated(ConditionalOnBean.class.getName())) {
            BeanSearchSpec spec = new BeanSearchSpec(context, metadata, ConditionalOnBean.class);
            MatchResult matchResult = getMatchingBeans(context, spec);
            if (!matchResult.isAllMatched()) {
                String reason = createOnBeanNoMatchReason(matchResult);
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnBean.class, spec).because(reason));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnBean.class, spec).found("bean", DefaultBeanDefinitionDocumentReader.NESTED_BEANS_ELEMENT).items(ConditionMessage.Style.QUOTE, matchResult.getNamesOfAllMatches());
        }
        if (metadata.isAnnotated(ConditionalOnSingleCandidate.class.getName())) {
            BeanSearchSpec spec2 = new SingleCandidateBeanSearchSpec(context, metadata, ConditionalOnSingleCandidate.class);
            MatchResult matchResult2 = getMatchingBeans(context, spec2);
            if (!matchResult2.isAllMatched()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnSingleCandidate.class, spec2).didNotFind("any beans").atAll());
            }
            if (!hasSingleAutowireCandidate(context.getBeanFactory(), matchResult2.getNamesOfAllMatches(), spec2.getStrategy() == SearchStrategy.ALL)) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnSingleCandidate.class, spec2).didNotFind("a primary bean from beans").items(ConditionMessage.Style.QUOTE, matchResult2.getNamesOfAllMatches()));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnSingleCandidate.class, spec2).found("a primary bean from beans").items(ConditionMessage.Style.QUOTE, matchResult2.getNamesOfAllMatches());
        }
        if (metadata.isAnnotated(ConditionalOnMissingBean.class.getName())) {
            BeanSearchSpec spec3 = new BeanSearchSpec(context, metadata, ConditionalOnMissingBean.class);
            MatchResult matchResult3 = getMatchingBeans(context, spec3);
            if (matchResult3.isAnyMatched()) {
                String reason2 = createOnMissingBeanNoMatchReason(matchResult3);
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnMissingBean.class, spec3).because(reason2));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnMissingBean.class, spec3).didNotFind("any beans").atAll();
        }
        return ConditionOutcome.match(matchMessage);
    }

    protected final MatchResult getMatchingBeans(ConditionContext context, BeanSearchSpec beans) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beans.getStrategy() == SearchStrategy.ANCESTORS) {
            BeanFactory parent = beanFactory.getParentBeanFactory();
            Assert.isInstanceOf(ConfigurableListableBeanFactory.class, parent, "Unable to use SearchStrategy.PARENTS");
            beanFactory = (ConfigurableListableBeanFactory) parent;
        }
        MatchResult matchResult = new MatchResult();
        boolean considerHierarchy = beans.getStrategy() != SearchStrategy.CURRENT;
        BeanTypeRegistry.TypeExtractor typeExtractor = beans.getTypeExtractor(context.getClassLoader());
        List<String> beansIgnoredByType = getNamesOfBeansIgnoredByType(beans.getIgnoredTypes(), typeExtractor, beanFactory, context, considerHierarchy);
        for (String type : beans.getTypes()) {
            Collection<String> typeMatches = getBeanNamesForType(beanFactory, type, typeExtractor, context.getClassLoader(), considerHierarchy);
            typeMatches.removeAll(beansIgnoredByType);
            if (typeMatches.isEmpty()) {
                matchResult.recordUnmatchedType(type);
            } else {
                matchResult.recordMatchedType(type, typeMatches);
            }
        }
        for (String annotation : beans.getAnnotations()) {
            List<String> annotationMatches = Arrays.asList(getBeanNamesForAnnotation(beanFactory, annotation, context.getClassLoader(), considerHierarchy));
            annotationMatches.removeAll(beansIgnoredByType);
            if (annotationMatches.isEmpty()) {
                matchResult.recordUnmatchedAnnotation(annotation);
            } else {
                matchResult.recordMatchedAnnotation(annotation, annotationMatches);
            }
        }
        for (String beanName : beans.getNames()) {
            if (beansIgnoredByType.contains(beanName) || !containsBean(beanFactory, beanName, considerHierarchy)) {
                matchResult.recordUnmatchedName(beanName);
            } else {
                matchResult.recordMatchedName(beanName);
            }
        }
        return matchResult;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String[] getBeanNamesForAnnotation(ConfigurableListableBeanFactory beanFactory, String type, ClassLoader classLoader, boolean considerHierarchy) throws LinkageError {
        Set<String> names = new HashSet<>();
        try {
            collectBeanNamesForAnnotation(names, beanFactory, ClassUtils.forName(type, classLoader), considerHierarchy);
        } catch (ClassNotFoundException e) {
        }
        return StringUtils.toStringArray(names);
    }

    private void collectBeanNamesForAnnotation(Set<String> names, ListableBeanFactory beanFactory, Class<? extends Annotation> annotationType, boolean considerHierarchy) {
        BeanTypeRegistry registry = BeanTypeRegistry.get(beanFactory);
        names.addAll(registry.getNamesForAnnotation(annotationType));
        if (considerHierarchy) {
            BeanFactory parent = ((HierarchicalBeanFactory) beanFactory).getParentBeanFactory();
            if (parent instanceof ListableBeanFactory) {
                collectBeanNamesForAnnotation(names, (ListableBeanFactory) parent, annotationType, considerHierarchy);
            }
        }
    }

    private List<String> getNamesOfBeansIgnoredByType(List<String> ignoredTypes, BeanTypeRegistry.TypeExtractor typeExtractor, ListableBeanFactory beanFactory, ConditionContext context, boolean considerHierarchy) {
        List<String> beanNames = new ArrayList<>();
        for (String ignoredType : ignoredTypes) {
            beanNames.addAll(getBeanNamesForType(beanFactory, ignoredType, typeExtractor, context.getClassLoader(), considerHierarchy));
        }
        return beanNames;
    }

    private boolean containsBean(ConfigurableListableBeanFactory beanFactory, String beanName, boolean considerHierarchy) {
        if (considerHierarchy) {
            return beanFactory.containsBean(beanName);
        }
        return beanFactory.containsLocalBean(beanName);
    }

    private Collection<String> getBeanNamesForType(ListableBeanFactory beanFactory, String type, BeanTypeRegistry.TypeExtractor typeExtractor, ClassLoader classLoader, boolean considerHierarchy) throws LinkageError {
        try {
            return getBeanNamesForType(beanFactory, considerHierarchy, ClassUtils.forName(type, classLoader), typeExtractor);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return Collections.emptySet();
        }
    }

    private Collection<String> getBeanNamesForType(ListableBeanFactory beanFactory, boolean considerHierarchy, Class<?> type, BeanTypeRegistry.TypeExtractor typeExtractor) {
        Set<String> result = new LinkedHashSet<>();
        collectBeanNamesForType(result, beanFactory, type, typeExtractor, considerHierarchy);
        return result;
    }

    private void collectBeanNamesForType(Set<String> result, ListableBeanFactory beanFactory, Class<?> type, BeanTypeRegistry.TypeExtractor typeExtractor, boolean considerHierarchy) {
        BeanTypeRegistry registry = BeanTypeRegistry.get(beanFactory);
        result.addAll(registry.getNamesForType(type, typeExtractor));
        if (considerHierarchy && (beanFactory instanceof HierarchicalBeanFactory)) {
            BeanFactory parent = ((HierarchicalBeanFactory) beanFactory).getParentBeanFactory();
            if (parent instanceof ListableBeanFactory) {
                collectBeanNamesForType(result, (ListableBeanFactory) parent, type, typeExtractor, considerHierarchy);
            }
        }
    }

    private String createOnBeanNoMatchReason(MatchResult matchResult) {
        StringBuilder reason = new StringBuilder();
        appendMessageForNoMatches(reason, matchResult.getUnmatchedAnnotations(), "annotated with");
        appendMessageForNoMatches(reason, matchResult.getUnmatchedTypes(), "of type");
        appendMessageForNoMatches(reason, matchResult.getUnmatchedNames(), "named");
        return reason.toString();
    }

    private void appendMessageForNoMatches(StringBuilder reason, Collection<String> unmatched, String description) {
        if (!unmatched.isEmpty()) {
            if (reason.length() > 0) {
                reason.append(" and ");
            }
            reason.append("did not find any beans ");
            reason.append(description);
            reason.append(" ");
            reason.append(StringUtils.collectionToDelimitedString(unmatched, ", "));
        }
    }

    private String createOnMissingBeanNoMatchReason(MatchResult matchResult) {
        StringBuilder reason = new StringBuilder();
        appendMessageForMatches(reason, matchResult.getMatchedAnnotations(), "annotated with");
        appendMessageForMatches(reason, matchResult.getMatchedTypes(), "of type");
        if (!matchResult.getMatchedNames().isEmpty()) {
            if (reason.length() > 0) {
                reason.append(" and ");
            }
            reason.append("found beans named ");
            reason.append(StringUtils.collectionToDelimitedString(matchResult.getMatchedNames(), ", "));
        }
        return reason.toString();
    }

    private void appendMessageForMatches(StringBuilder reason, Map<String, Collection<String>> matches, String description) {
        if (!matches.isEmpty()) {
            matches.forEach(key, value -> {
                if (reason.length() > 0) {
                    reason.append(" and ");
                }
                reason.append("found beans ");
                reason.append(description);
                reason.append(" '");
                reason.append(key);
                reason.append("' ");
                reason.append(StringUtils.collectionToDelimitedString(value, ", "));
            });
        }
    }

    private boolean hasSingleAutowireCandidate(ConfigurableListableBeanFactory beanFactory, Set<String> beanNames, boolean considerHierarchy) {
        return beanNames.size() == 1 || getPrimaryBeans(beanFactory, beanNames, considerHierarchy).size() == 1;
    }

    private List<String> getPrimaryBeans(ConfigurableListableBeanFactory beanFactory, Set<String> beanNames, boolean considerHierarchy) {
        List<String> primaryBeans = new ArrayList<>();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = findBeanDefinition(beanFactory, beanName, considerHierarchy);
            if (beanDefinition != null && beanDefinition.isPrimary()) {
                primaryBeans.add(beanName);
            }
        }
        return primaryBeans;
    }

    private BeanDefinition findBeanDefinition(ConfigurableListableBeanFactory beanFactory, String beanName, boolean considerHierarchy) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            return beanFactory.getBeanDefinition(beanName);
        }
        if (considerHierarchy && (beanFactory.getParentBeanFactory() instanceof ConfigurableListableBeanFactory)) {
            return findBeanDefinition((ConfigurableListableBeanFactory) beanFactory.getParentBeanFactory(), beanName, considerHierarchy);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnBeanCondition$BeanSearchSpec.class */
    public static class BeanSearchSpec {
        private final Class<?> annotationType;
        private final List<String> names;
        private final List<String> types;
        private final List<String> annotations;
        private final List<String> ignoredTypes;
        private final List<String> parameterizedContainers;
        private final SearchStrategy strategy;

        public BeanSearchSpec(ConditionContext context, AnnotatedTypeMetadata metadata, Class<?> annotationType) {
            this(context, metadata, annotationType, null);
        }

        public BeanSearchSpec(ConditionContext context, AnnotatedTypeMetadata metadata, Class<?> annotationType, Class<?> genericContainer) {
            this.names = new ArrayList();
            this.types = new ArrayList();
            this.annotations = new ArrayList();
            this.ignoredTypes = new ArrayList();
            this.parameterizedContainers = new ArrayList();
            this.annotationType = annotationType;
            MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(annotationType.getName(), true);
            collect(attributes, "name", this.names);
            collect(attributes, "value", this.types);
            collect(attributes, "type", this.types);
            collect(attributes, "annotation", this.annotations);
            collect(attributes, "ignored", this.ignoredTypes);
            collect(attributes, "ignoredType", this.ignoredTypes);
            collect(attributes, "parameterizedContainer", this.parameterizedContainers);
            this.strategy = (SearchStrategy) attributes.getFirst(SpringInputGeneralFieldTagProcessor.SEARCH_INPUT_TYPE_ATTR_VALUE);
            BeanTypeDeductionException deductionException = null;
            try {
                if (this.types.isEmpty() && this.names.isEmpty()) {
                    addDeducedBeanType(context, metadata, this.types);
                }
            } catch (BeanTypeDeductionException ex) {
                deductionException = ex;
            }
            validate(deductionException);
        }

        protected void validate(BeanTypeDeductionException ex) {
            if (!hasAtLeastOne(this.types, this.names, this.annotations)) {
                String message = getAnnotationName() + " did not specify a bean using type, name or annotation";
                if (ex == null) {
                    throw new IllegalStateException(message);
                }
                throw new IllegalStateException(message + " and the attempt to deduce the bean's type failed", ex);
            }
        }

        private boolean hasAtLeastOne(List<?>... lists) {
            return Arrays.stream(lists).anyMatch(list -> {
                return !list.isEmpty();
            });
        }

        protected final String getAnnotationName() {
            return "@" + ClassUtils.getShortName(this.annotationType);
        }

        protected void collect(MultiValueMap<String, Object> attributes, String key, List<String> destination) {
            List<?> values = (List) attributes.get(key);
            if (values != null) {
                for (Object value : values) {
                    if (value instanceof String[]) {
                        Collections.addAll(destination, (String[]) value);
                    } else {
                        destination.add((String) value);
                    }
                }
            }
        }

        private void addDeducedBeanType(ConditionContext context, AnnotatedTypeMetadata metadata, final List<String> beanTypes) {
            if ((metadata instanceof MethodMetadata) && metadata.isAnnotated(Bean.class.getName())) {
                addDeducedBeanTypeForBeanMethod(context, (MethodMetadata) metadata, beanTypes);
            }
        }

        private void addDeducedBeanTypeForBeanMethod(ConditionContext context, MethodMetadata metadata, final List<String> beanTypes) {
            try {
                Class<?> returnType = getReturnType(context, metadata);
                beanTypes.add(returnType.getName());
            } catch (Throwable ex) {
                throw new BeanTypeDeductionException(metadata.getDeclaringClassName(), metadata.getMethodName(), ex);
            }
        }

        private Class<?> getReturnType(ConditionContext context, MethodMetadata metadata) throws ClassNotFoundException, LinkageError {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> returnType = ClassUtils.forName(metadata.getReturnTypeName(), classLoader);
            if (isParameterizedContainer(returnType, classLoader)) {
                returnType = getReturnTypeGeneric(metadata, classLoader);
            }
            return returnType;
        }

        private Class<?> getReturnTypeGeneric(MethodMetadata metadata, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
            Class<?> declaringClass = ClassUtils.forName(metadata.getDeclaringClassName(), classLoader);
            Method beanMethod = findBeanMethod(declaringClass, metadata.getMethodName());
            return ResolvableType.forMethodReturnType(beanMethod).resolveGeneric(new int[0]);
        }

        private Method findBeanMethod(Class<?> declaringClass, String methodName) {
            Method method = ReflectionUtils.findMethod(declaringClass, methodName);
            if (isBeanMethod(method)) {
                return method;
            }
            return (Method) Arrays.stream(ReflectionUtils.getAllDeclaredMethods(declaringClass)).filter(candidate -> {
                return candidate.getName().equals(methodName);
            }).filter(this::isBeanMethod).findFirst().orElseThrow(() -> {
                return new IllegalStateException("Unable to find bean method " + methodName);
            });
        }

        private boolean isBeanMethod(Method method) {
            return method != null && AnnotatedElementUtils.hasAnnotation(method, Bean.class);
        }

        public BeanTypeRegistry.TypeExtractor getTypeExtractor(ClassLoader classLoader) {
            if (this.parameterizedContainers.isEmpty()) {
                return (v0) -> {
                    return v0.resolve();
                };
            }
            return type -> {
                Class<?> resolved = type.resolve();
                if (isParameterizedContainer(resolved, classLoader)) {
                    return type.getGeneric(new int[0]).resolve();
                }
                return resolved;
            };
        }

        private boolean isParameterizedContainer(Class<?> type, ClassLoader classLoader) {
            for (String candidate : this.parameterizedContainers) {
                if (ClassUtils.forName(candidate, classLoader).isAssignableFrom(type)) {
                    return true;
                }
            }
            return false;
        }

        public SearchStrategy getStrategy() {
            return this.strategy != null ? this.strategy : SearchStrategy.ALL;
        }

        public List<String> getNames() {
            return this.names;
        }

        public List<String> getTypes() {
            return this.types;
        }

        public List<String> getAnnotations() {
            return this.annotations;
        }

        public List<String> getIgnoredTypes() {
            return this.ignoredTypes;
        }

        public String toString() {
            StringBuilder string = new StringBuilder();
            string.append("(");
            if (!this.names.isEmpty()) {
                string.append("names: ");
                string.append(StringUtils.collectionToCommaDelimitedString(this.names));
                if (!this.types.isEmpty()) {
                    string.append("; ");
                }
            }
            if (!this.types.isEmpty()) {
                string.append("types: ");
                string.append(StringUtils.collectionToCommaDelimitedString(this.types));
            }
            string.append("; SearchStrategy: ");
            string.append(this.strategy.toString().toLowerCase(Locale.ENGLISH));
            string.append(")");
            return string.toString();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnBeanCondition$SingleCandidateBeanSearchSpec.class */
    private static class SingleCandidateBeanSearchSpec extends BeanSearchSpec {
        SingleCandidateBeanSearchSpec(ConditionContext context, AnnotatedTypeMetadata metadata, Class<?> annotationType) {
            super(context, metadata, annotationType);
        }

        @Override // org.springframework.boot.autoconfigure.condition.OnBeanCondition.BeanSearchSpec
        protected void collect(MultiValueMap<String, Object> attributes, String key, List<String> destination) {
            super.collect(attributes, key, destination);
            destination.removeAll(Arrays.asList("", Object.class.getName()));
        }

        @Override // org.springframework.boot.autoconfigure.condition.OnBeanCondition.BeanSearchSpec
        protected void validate(BeanTypeDeductionException ex) {
            Assert.isTrue(getTypes().size() == 1, () -> {
                return getAnnotationName() + " annotations must specify only one type (got " + getTypes() + ")";
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnBeanCondition$MatchResult.class */
    public static final class MatchResult {
        private final Map<String, Collection<String>> matchedAnnotations = new HashMap();
        private final List<String> matchedNames = new ArrayList();
        private final Map<String, Collection<String>> matchedTypes = new HashMap();
        private final List<String> unmatchedAnnotations = new ArrayList();
        private final List<String> unmatchedNames = new ArrayList();
        private final List<String> unmatchedTypes = new ArrayList();
        private final Set<String> namesOfAllMatches = new HashSet();

        protected MatchResult() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordMatchedName(String name) {
            this.matchedNames.add(name);
            this.namesOfAllMatches.add(name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordUnmatchedName(String name) {
            this.unmatchedNames.add(name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordMatchedAnnotation(String annotation, Collection<String> matchingNames) {
            this.matchedAnnotations.put(annotation, matchingNames);
            this.namesOfAllMatches.addAll(matchingNames);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordUnmatchedAnnotation(String annotation) {
            this.unmatchedAnnotations.add(annotation);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordMatchedType(String type, Collection<String> matchingNames) {
            this.matchedTypes.put(type, matchingNames);
            this.namesOfAllMatches.addAll(matchingNames);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void recordUnmatchedType(String type) {
            this.unmatchedTypes.add(type);
        }

        public boolean isAllMatched() {
            return this.unmatchedAnnotations.isEmpty() && this.unmatchedNames.isEmpty() && this.unmatchedTypes.isEmpty();
        }

        public boolean isAnyMatched() {
            return (this.matchedAnnotations.isEmpty() && this.matchedNames.isEmpty() && this.matchedTypes.isEmpty()) ? false : true;
        }

        public Map<String, Collection<String>> getMatchedAnnotations() {
            return this.matchedAnnotations;
        }

        public List<String> getMatchedNames() {
            return this.matchedNames;
        }

        public Map<String, Collection<String>> getMatchedTypes() {
            return this.matchedTypes;
        }

        public List<String> getUnmatchedAnnotations() {
            return this.unmatchedAnnotations;
        }

        public List<String> getUnmatchedNames() {
            return this.unmatchedNames;
        }

        public List<String> getUnmatchedTypes() {
            return this.unmatchedTypes;
        }

        public Set<String> getNamesOfAllMatches() {
            return this.namesOfAllMatches;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnBeanCondition$BeanTypeDeductionException.class */
    public static final class BeanTypeDeductionException extends RuntimeException {
        private BeanTypeDeductionException(String className, String beanMethodName, Throwable cause) {
            super("Failed to deduce bean type for " + className + "." + beanMethodName, cause);
        }
    }
}