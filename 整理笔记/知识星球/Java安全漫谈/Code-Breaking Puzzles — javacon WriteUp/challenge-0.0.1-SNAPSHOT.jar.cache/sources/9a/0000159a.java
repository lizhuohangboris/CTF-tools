package org.springframework.boot.autoconfigure.condition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/AbstractNestedCondition.class */
public abstract class AbstractNestedCondition extends SpringBootCondition implements ConfigurationCondition {
    private final ConfigurationCondition.ConfigurationPhase configurationPhase;

    protected abstract ConditionOutcome getFinalMatchOutcome(MemberMatchOutcomes memberOutcomes);

    public AbstractNestedCondition(ConfigurationCondition.ConfigurationPhase configurationPhase) {
        Assert.notNull(configurationPhase, "ConfigurationPhase must not be null");
        this.configurationPhase = configurationPhase;
    }

    @Override // org.springframework.context.annotation.ConfigurationCondition
    public ConfigurationCondition.ConfigurationPhase getConfigurationPhase() {
        return this.configurationPhase;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String className = getClass().getName();
        MemberConditions memberConditions = new MemberConditions(context, className);
        MemberMatchOutcomes memberOutcomes = new MemberMatchOutcomes(memberConditions);
        return getFinalMatchOutcome(memberOutcomes);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/AbstractNestedCondition$MemberMatchOutcomes.class */
    protected static class MemberMatchOutcomes {
        private final List<ConditionOutcome> all;
        private final List<ConditionOutcome> matches;
        private final List<ConditionOutcome> nonMatches;

        public MemberMatchOutcomes(MemberConditions memberConditions) {
            this.all = Collections.unmodifiableList(memberConditions.getMatchOutcomes());
            List<ConditionOutcome> matches = new ArrayList<>();
            List<ConditionOutcome> nonMatches = new ArrayList<>();
            for (ConditionOutcome outcome : this.all) {
                (outcome.isMatch() ? matches : nonMatches).add(outcome);
            }
            this.matches = Collections.unmodifiableList(matches);
            this.nonMatches = Collections.unmodifiableList(nonMatches);
        }

        public List<ConditionOutcome> getAll() {
            return this.all;
        }

        public List<ConditionOutcome> getMatches() {
            return this.matches;
        }

        public List<ConditionOutcome> getNonMatches() {
            return this.nonMatches;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/AbstractNestedCondition$MemberConditions.class */
    public static class MemberConditions {
        private final ConditionContext context;
        private final MetadataReaderFactory readerFactory;
        private final Map<AnnotationMetadata, List<Condition>> memberConditions;

        MemberConditions(ConditionContext context, String className) {
            this.context = context;
            this.readerFactory = new SimpleMetadataReaderFactory(context.getResourceLoader());
            String[] members = getMetadata(className).getMemberClassNames();
            this.memberConditions = getMemberConditions(members);
        }

        private Map<AnnotationMetadata, List<Condition>> getMemberConditions(String[] members) {
            MultiValueMap<AnnotationMetadata, Condition> memberConditions = new LinkedMultiValueMap<>();
            for (String member : members) {
                AnnotationMetadata metadata = getMetadata(member);
                for (String[] conditionClasses : getConditionClasses(metadata)) {
                    for (String conditionClass : conditionClasses) {
                        Condition condition = getCondition(conditionClass);
                        memberConditions.add(metadata, condition);
                    }
                }
            }
            return Collections.unmodifiableMap(memberConditions);
        }

        private AnnotationMetadata getMetadata(String className) {
            try {
                return this.readerFactory.getMetadataReader(className).getAnnotationMetadata();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
            MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
            Object values = attributes != null ? attributes.get("value") : null;
            return values != null ? values : Collections.emptyList();
        }

        private Condition getCondition(String conditionClassName) {
            Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, this.context.getClassLoader());
            return (Condition) BeanUtils.instantiateClass(conditionClass);
        }

        public List<ConditionOutcome> getMatchOutcomes() {
            List<ConditionOutcome> outcomes = new ArrayList<>();
            this.memberConditions.forEach(metadata, conditions -> {
                outcomes.add(new MemberOutcomes(this.context, metadata, conditions).getUltimateOutcome());
            });
            return Collections.unmodifiableList(outcomes);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/AbstractNestedCondition$MemberOutcomes.class */
    public static class MemberOutcomes {
        private final ConditionContext context;
        private final AnnotationMetadata metadata;
        private final List<ConditionOutcome> outcomes;

        MemberOutcomes(ConditionContext context, AnnotationMetadata metadata, List<Condition> conditions) {
            this.context = context;
            this.metadata = metadata;
            this.outcomes = new ArrayList(conditions.size());
            for (Condition condition : conditions) {
                this.outcomes.add(getConditionOutcome(metadata, condition));
            }
        }

        private ConditionOutcome getConditionOutcome(AnnotationMetadata metadata, Condition condition) {
            if (condition instanceof SpringBootCondition) {
                return ((SpringBootCondition) condition).getMatchOutcome(this.context, metadata);
            }
            return new ConditionOutcome(condition.matches(this.context, metadata), ConditionMessage.empty());
        }

        public ConditionOutcome getUltimateOutcome() {
            ConditionMessage.Builder message = ConditionMessage.forCondition("NestedCondition on " + ClassUtils.getShortName(this.metadata.getClassName()), new Object[0]);
            if (this.outcomes.size() == 1) {
                ConditionOutcome outcome = this.outcomes.get(0);
                return new ConditionOutcome(outcome.isMatch(), message.because(outcome.getMessage()));
            }
            List<ConditionOutcome> match = new ArrayList<>();
            List<ConditionOutcome> nonMatch = new ArrayList<>();
            for (ConditionOutcome outcome2 : this.outcomes) {
                (outcome2.isMatch() ? match : nonMatch).add(outcome2);
            }
            if (nonMatch.isEmpty()) {
                return ConditionOutcome.match(message.found("matching nested conditions").items(match));
            }
            return ConditionOutcome.noMatch(message.found("non-matching nested conditions").items(nonMatch));
        }
    }
}