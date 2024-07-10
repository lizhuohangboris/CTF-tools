package org.hibernate.validator;

import java.time.Duration;
import java.util.Set;
import javax.validation.Configuration;
import javax.validation.valueextraction.ValueExtractor;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/HibernateValidatorConfiguration.class */
public interface HibernateValidatorConfiguration extends Configuration<HibernateValidatorConfiguration> {
    public static final String FAIL_FAST = "hibernate.validator.fail_fast";
    public static final String ALLOW_PARAMETER_CONSTRAINT_OVERRIDE = "hibernate.validator.allow_parameter_constraint_override";
    public static final String ALLOW_MULTIPLE_CASCADED_VALIDATION_ON_RESULT = "hibernate.validator.allow_multiple_cascaded_validation_on_result";
    public static final String ALLOW_PARALLEL_METHODS_DEFINE_PARAMETER_CONSTRAINTS = "hibernate.validator.allow_parallel_method_parameter_constraint";
    @Deprecated
    public static final String CONSTRAINT_MAPPING_CONTRIBUTOR = "hibernate.validator.constraint_mapping_contributor";
    public static final String CONSTRAINT_MAPPING_CONTRIBUTORS = "hibernate.validator.constraint_mapping_contributors";
    public static final String ENABLE_TRAVERSABLE_RESOLVER_RESULT_CACHE = "hibernate.validator.enable_traversable_resolver_result_cache";
    @Incubating
    public static final String SCRIPT_EVALUATOR_FACTORY_CLASSNAME = "hibernate.validator.script_evaluator_factory";
    @Incubating
    public static final String TEMPORAL_VALIDATION_TOLERANCE = "hibernate.validator.temporal_validation_tolerance";

    ResourceBundleLocator getDefaultResourceBundleLocator();

    ConstraintMapping createConstraintMapping();

    @Incubating
    Set<ValueExtractor<?>> getDefaultValueExtractors();

    HibernateValidatorConfiguration addMapping(ConstraintMapping constraintMapping);

    HibernateValidatorConfiguration failFast(boolean z);

    HibernateValidatorConfiguration externalClassLoader(ClassLoader classLoader);

    HibernateValidatorConfiguration allowOverridingMethodAlterParameterConstraint(boolean z);

    HibernateValidatorConfiguration allowMultipleCascadedValidationOnReturnValues(boolean z);

    HibernateValidatorConfiguration allowParallelMethodsDefineParameterConstraints(boolean z);

    HibernateValidatorConfiguration enableTraversableResolverResultCache(boolean z);

    @Incubating
    HibernateValidatorConfiguration scriptEvaluatorFactory(ScriptEvaluatorFactory scriptEvaluatorFactory);

    @Incubating
    HibernateValidatorConfiguration temporalValidationTolerance(Duration duration);

    @Incubating
    HibernateValidatorConfiguration constraintValidatorPayload(Object obj);
}