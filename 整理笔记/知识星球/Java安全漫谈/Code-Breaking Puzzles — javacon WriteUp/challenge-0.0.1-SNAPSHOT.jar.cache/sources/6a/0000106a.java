package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.time.Duration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.ExecutableParameterNameProvider;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext.class */
public class ValidationContext<T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ValidationOperation validationOperation;
    private final ConstraintValidatorManager constraintValidatorManager;
    private final T rootBean;
    private final Class<T> rootBeanClass;
    private final BeanMetaData<T> rootBeanMetaData;
    private final Executable executable;
    private final Object[] executableParameters;
    private final Object executableReturnValue;
    private final Optional<ExecutableMetaData> executableMetaData;
    private final Set<BeanPathMetaConstraintProcessedUnit> processedPathUnits;
    private final Set<BeanGroupProcessedUnit> processedGroupUnits;
    private final Map<Object, Set<PathImpl>> processedPathsPerBean;
    private final Set<ConstraintViolation<T>> failingConstraintViolations;
    private final ConstraintValidatorFactory constraintValidatorFactory;
    private final ValidatorScopedContext validatorScopedContext;
    private final TraversableResolver traversableResolver;
    private final HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
    private final boolean disableAlreadyValidatedBeanTracking;
    private String validatedProperty;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext$ValidationOperation.class */
    public enum ValidationOperation {
        BEAN_VALIDATION,
        PROPERTY_VALIDATION,
        VALUE_VALIDATION,
        PARAMETER_VALIDATION,
        RETURN_VALUE_VALIDATION
    }

    private ValidationContext(ValidationOperation validationOperation, ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext, T rootBean, Class<T> rootBeanClass, BeanMetaData<T> rootBeanMetaData, Executable executable, Object[] executableParameters, Object executableReturnValue, Optional<ExecutableMetaData> executableMetaData) {
        this.validationOperation = validationOperation;
        this.constraintValidatorManager = constraintValidatorManager;
        this.validatorScopedContext = validatorScopedContext;
        this.constraintValidatorFactory = constraintValidatorFactory;
        this.traversableResolver = traversableResolver;
        this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
        this.rootBean = rootBean;
        this.rootBeanClass = rootBeanClass;
        this.rootBeanMetaData = rootBeanMetaData;
        this.executable = executable;
        this.executableParameters = executableParameters;
        this.executableReturnValue = executableReturnValue;
        this.processedGroupUnits = new HashSet();
        this.processedPathUnits = new HashSet();
        this.processedPathsPerBean = new IdentityHashMap();
        this.failingConstraintViolations = CollectionHelper.newHashSet();
        this.executableMetaData = executableMetaData;
        this.disableAlreadyValidatedBeanTracking = buildDisableAlreadyValidatedBeanTracking(validationOperation, rootBeanMetaData, executableMetaData);
    }

    public static ValidationContextBuilder getValidationContextBuilder(BeanMetaDataManager beanMetaDataManager, ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
        return new ValidationContextBuilder(beanMetaDataManager, constraintValidatorManager, constraintValidatorFactory, validatorScopedContext, traversableResolver, constraintValidatorInitializationContext);
    }

    public T getRootBean() {
        return this.rootBean;
    }

    public Class<T> getRootBeanClass() {
        return this.rootBeanClass;
    }

    public BeanMetaData<T> getRootBeanMetaData() {
        return this.rootBeanMetaData;
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Optional<ExecutableMetaData> getExecutableMetaData() {
        return this.executableMetaData;
    }

    public TraversableResolver getTraversableResolver() {
        return this.traversableResolver;
    }

    public boolean isFailFastModeEnabled() {
        return this.validatorScopedContext.isFailFast();
    }

    public ConstraintValidatorManager getConstraintValidatorManager() {
        return this.constraintValidatorManager;
    }

    public List<String> getParameterNames() {
        if (!ValidationOperation.PARAMETER_VALIDATION.equals(this.validationOperation)) {
            return null;
        }
        return this.validatorScopedContext.getParameterNameProvider().getParameterNames(this.executable);
    }

    public ClockProvider getClockProvider() {
        return this.validatorScopedContext.getClockProvider();
    }

    public Object getConstraintValidatorPayload() {
        return this.validatorScopedContext.getConstraintValidatorPayload();
    }

    public HibernateConstraintValidatorInitializationContext getConstraintValidatorInitializationContext() {
        return this.constraintValidatorInitializationContext;
    }

    public Set<ConstraintViolation<T>> createConstraintViolations(ValueContext<?, ?> localContext, ConstraintValidatorContextImpl constraintValidatorContext) {
        return (Set) constraintValidatorContext.getConstraintViolationCreationContexts().stream().map(c -> {
            return createConstraintViolation(localContext, c, constraintValidatorContext.getConstraintDescriptor());
        }).collect(Collectors.toSet());
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorFactory;
    }

    public boolean isBeanAlreadyValidated(Object value, Class<?> group, PathImpl path) {
        if (this.disableAlreadyValidatedBeanTracking) {
            return false;
        }
        boolean alreadyValidated = isAlreadyValidatedForCurrentGroup(value, group);
        if (alreadyValidated) {
            alreadyValidated = isAlreadyValidatedForPath(value, path);
        }
        return alreadyValidated;
    }

    public void markCurrentBeanAsProcessed(ValueContext<?, ?> valueContext) {
        if (this.disableAlreadyValidatedBeanTracking) {
            return;
        }
        markCurrentBeanAsProcessedForCurrentGroup(valueContext.getCurrentBean(), valueContext.getCurrentGroup());
        markCurrentBeanAsProcessedForCurrentPath(valueContext.getCurrentBean(), valueContext.getPropertyPath());
    }

    public void addConstraintFailures(Set<ConstraintViolation<T>> failingConstraintViolations) {
        this.failingConstraintViolations.addAll(failingConstraintViolations);
    }

    public Set<ConstraintViolation<T>> getFailingConstraints() {
        return this.failingConstraintViolations;
    }

    public ConstraintViolation<T> createConstraintViolation(ValueContext<?, ?> localContext, ConstraintViolationCreationContext constraintViolationCreationContext, ConstraintDescriptor<?> descriptor) {
        String messageTemplate = constraintViolationCreationContext.getMessage();
        String interpolatedMessage = interpolate(messageTemplate, localContext.getCurrentValidatedValue(), descriptor, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables());
        Path path = PathImpl.createCopy(constraintViolationCreationContext.getPath());
        Object dynamicPayload = constraintViolationCreationContext.getDynamicPayload();
        switch (this.validationOperation) {
            case PARAMETER_VALIDATION:
                return ConstraintViolationImpl.forParameterValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, getRootBeanClass(), getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), this.executableParameters, dynamicPayload);
            case RETURN_VALUE_VALIDATION:
                return ConstraintViolationImpl.forReturnValueValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, getRootBeanClass(), getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), this.executableReturnValue, dynamicPayload);
            default:
                return ConstraintViolationImpl.forBeanValidation(messageTemplate, constraintViolationCreationContext.getMessageParameters(), constraintViolationCreationContext.getExpressionVariables(), interpolatedMessage, getRootBeanClass(), getRootBean(), localContext.getCurrentBean(), localContext.getCurrentValidatedValue(), path, descriptor, localContext.getElementType(), dynamicPayload);
        }
    }

    public boolean hasMetaConstraintBeenProcessed(Object bean, Path path, MetaConstraint<?> metaConstraint) {
        if (metaConstraint.isDefinedForOneGroupOnly()) {
            return false;
        }
        return this.processedPathUnits.contains(new BeanPathMetaConstraintProcessedUnit(bean, path, metaConstraint));
    }

    public void markConstraintProcessed(Object bean, Path path, MetaConstraint<?> metaConstraint) {
        if (metaConstraint.isDefinedForOneGroupOnly()) {
            return;
        }
        this.processedPathUnits.add(new BeanPathMetaConstraintProcessedUnit(bean, path, metaConstraint));
    }

    public String getValidatedProperty() {
        return this.validatedProperty;
    }

    public void setValidatedProperty(String validatedProperty) {
        this.validatedProperty = validatedProperty;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationContext");
        sb.append("{rootBean=").append(this.rootBean);
        sb.append('}');
        return sb.toString();
    }

    private static boolean buildDisableAlreadyValidatedBeanTracking(ValidationOperation validationOperation, BeanMetaData<?> rootBeanMetaData, Optional<ExecutableMetaData> executableMetaData) {
        Validatable validatable;
        switch (validationOperation) {
            case PARAMETER_VALIDATION:
                if (!executableMetaData.isPresent()) {
                    return false;
                }
                validatable = executableMetaData.get().getValidatableParametersMetaData();
                break;
            case RETURN_VALUE_VALIDATION:
                if (!executableMetaData.isPresent()) {
                    return false;
                }
                validatable = executableMetaData.get().getReturnValueMetaData();
                break;
            case BEAN_VALIDATION:
            case PROPERTY_VALIDATION:
            case VALUE_VALIDATION:
                validatable = rootBeanMetaData;
                break;
            default:
                return false;
        }
        return !validatable.hasCascadables();
    }

    private String interpolate(String messageTemplate, Object validatedValue, ConstraintDescriptor<?> descriptor, Map<String, Object> messageParameters, Map<String, Object> expressionVariables) {
        MessageInterpolatorContext context = new MessageInterpolatorContext(descriptor, validatedValue, getRootBeanClass(), messageParameters, expressionVariables);
        try {
            return this.validatorScopedContext.getMessageInterpolator().interpolate(messageTemplate, context);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw LOG.getExceptionOccurredDuringMessageInterpolationException(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0026  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean isAlreadyValidatedForPath(java.lang.Object r5, org.hibernate.validator.internal.engine.path.PathImpl r6) {
        /*
            r4 = this;
            r0 = r4
            java.util.Map<java.lang.Object, java.util.Set<org.hibernate.validator.internal.engine.path.PathImpl>> r0 = r0.processedPathsPerBean
            r1 = r5
            java.lang.Object r0 = r0.get(r1)
            java.util.Set r0 = (java.util.Set) r0
            r7 = r0
            r0 = r7
            if (r0 != 0) goto L14
            r0 = 0
            return r0
        L14:
            r0 = r7
            java.util.Iterator r0 = r0.iterator()
            r8 = r0
        L1c:
            r0 = r8
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L5a
            r0 = r8
            java.lang.Object r0 = r0.next()
            org.hibernate.validator.internal.engine.path.PathImpl r0 = (org.hibernate.validator.internal.engine.path.PathImpl) r0
            r9 = r0
            r0 = r6
            boolean r0 = r0.isRootPath()
            if (r0 != 0) goto L55
            r0 = r9
            boolean r0 = r0.isRootPath()
            if (r0 != 0) goto L55
            r0 = r4
            r1 = r6
            r2 = r9
            boolean r0 = r0.isSubPathOf(r1, r2)
            if (r0 != 0) goto L55
            r0 = r4
            r1 = r9
            r2 = r6
            boolean r0 = r0.isSubPathOf(r1, r2)
            if (r0 == 0) goto L57
        L55:
            r0 = 1
            return r0
        L57:
            goto L1c
        L5a:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.hibernate.validator.internal.engine.ValidationContext.isAlreadyValidatedForPath(java.lang.Object, org.hibernate.validator.internal.engine.path.PathImpl):boolean");
    }

    private boolean isSubPathOf(Path p1, Path p2) {
        Iterator<Path.Node> p2Iter = p2.iterator();
        for (Path.Node p1Node : p1) {
            if (!p2Iter.hasNext()) {
                return false;
            }
            Path.Node p2Node = p2Iter.next();
            if (!p1Node.equals(p2Node)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlreadyValidatedForCurrentGroup(Object value, Class<?> group) {
        return this.processedGroupUnits.contains(new BeanGroupProcessedUnit(value, group));
    }

    private void markCurrentBeanAsProcessedForCurrentPath(Object bean, PathImpl path) {
        this.processedPathsPerBean.computeIfAbsent(bean, b -> {
            return new HashSet();
        }).add(PathImpl.createCopy(path));
    }

    private void markCurrentBeanAsProcessedForCurrentGroup(Object bean, Class<?> group) {
        this.processedGroupUnits.add(new BeanGroupProcessedUnit(bean, group));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext$ValidationContextBuilder.class */
    public static class ValidationContextBuilder {
        private final BeanMetaDataManager beanMetaDataManager;
        private final ConstraintValidatorManager constraintValidatorManager;
        private final ConstraintValidatorFactory constraintValidatorFactory;
        private final TraversableResolver traversableResolver;
        private final HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext;
        private final ValidatorScopedContext validatorScopedContext;

        private ValidationContextBuilder(BeanMetaDataManager beanMetaDataManager, ConstraintValidatorManager constraintValidatorManager, ConstraintValidatorFactory constraintValidatorFactory, ValidatorScopedContext validatorScopedContext, TraversableResolver traversableResolver, HibernateConstraintValidatorInitializationContext constraintValidatorInitializationContext) {
            this.beanMetaDataManager = beanMetaDataManager;
            this.constraintValidatorManager = constraintValidatorManager;
            this.constraintValidatorFactory = constraintValidatorFactory;
            this.traversableResolver = traversableResolver;
            this.constraintValidatorInitializationContext = constraintValidatorInitializationContext;
            this.validatorScopedContext = validatorScopedContext;
        }

        public <T> ValidationContext<T> forValidate(T rootBean) {
            Class<?> cls = rootBean.getClass();
            return new ValidationContext<>(ValidationOperation.BEAN_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, cls, this.beanMetaDataManager.getBeanMetaData(cls), null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateProperty(T rootBean) {
            Class<?> cls = rootBean.getClass();
            return new ValidationContext<>(ValidationOperation.PROPERTY_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, cls, this.beanMetaDataManager.getBeanMetaData(cls), null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateValue(Class<T> rootBeanClass) {
            return new ValidationContext<>(ValidationOperation.VALUE_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, null, rootBeanClass, this.beanMetaDataManager.getBeanMetaData(rootBeanClass), null, null, null, null);
        }

        public <T> ValidationContext<T> forValidateParameters(ExecutableParameterNameProvider parameterNameProvider, T rootBean, Executable executable, Object[] executableParameters) {
            Class<?> cls = rootBean != null ? rootBean.getClass() : executable.getDeclaringClass();
            BeanMetaData<T> rootBeanMetaData = this.beanMetaDataManager.getBeanMetaData(cls);
            return new ValidationContext<>(ValidationOperation.PARAMETER_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, cls, rootBeanMetaData, executable, executableParameters, null, rootBeanMetaData.getMetaDataFor(executable));
        }

        public <T> ValidationContext<T> forValidateReturnValue(T rootBean, Executable executable, Object executableReturnValue) {
            Class<?> cls = rootBean != null ? rootBean.getClass() : executable.getDeclaringClass();
            BeanMetaData<T> rootBeanMetaData = this.beanMetaDataManager.getBeanMetaData(cls);
            return new ValidationContext<>(ValidationOperation.RETURN_VALUE_VALIDATION, this.constraintValidatorManager, this.constraintValidatorFactory, this.validatorScopedContext, this.traversableResolver, this.constraintValidatorInitializationContext, rootBean, cls, this.beanMetaDataManager.getBeanMetaData(cls), executable, null, executableReturnValue, rootBeanMetaData.getMetaDataFor(executable));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext$BeanGroupProcessedUnit.class */
    public static final class BeanGroupProcessedUnit {
        private Object bean;
        private Class<?> group;
        private int hashCode;

        private BeanGroupProcessedUnit(Object bean, Class<?> group) {
            this.bean = bean;
            this.group = group;
            this.hashCode = createHashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            BeanGroupProcessedUnit that = (BeanGroupProcessedUnit) o;
            if (this.bean != that.bean || !this.group.equals(that.group)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = System.identityHashCode(this.bean);
            return (31 * result) + this.group.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext$BeanPathMetaConstraintProcessedUnit.class */
    public static final class BeanPathMetaConstraintProcessedUnit {
        private Object bean;
        private Path path;
        private MetaConstraint<?> metaConstraint;
        private int hashCode;

        private BeanPathMetaConstraintProcessedUnit(Object bean, Path path, MetaConstraint<?> metaConstraint) {
            this.bean = bean;
            this.path = path;
            this.metaConstraint = metaConstraint;
            this.hashCode = createHashCode();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            BeanPathMetaConstraintProcessedUnit that = (BeanPathMetaConstraintProcessedUnit) o;
            if (this.bean != that.bean || this.metaConstraint != that.metaConstraint || !this.path.equals(that.path)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.hashCode;
        }

        private int createHashCode() {
            int result = System.identityHashCode(this.bean);
            return (31 * ((31 * result) + this.path.hashCode())) + System.identityHashCode(this.metaConstraint);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ValidationContext$ValidatorScopedContext.class */
    public static class ValidatorScopedContext {
        private final MessageInterpolator messageInterpolator;
        private final ExecutableParameterNameProvider parameterNameProvider;
        private final ClockProvider clockProvider;
        private final Duration temporalValidationTolerance;
        private final ScriptEvaluatorFactory scriptEvaluatorFactory;
        private final boolean failFast;
        private final boolean traversableResolverResultCacheEnabled;
        private final Object constraintValidatorPayload;

        public ValidatorScopedContext(ValidatorFactoryImpl.ValidatorFactoryScopedContext validatorFactoryScopedContext) {
            this.messageInterpolator = validatorFactoryScopedContext.getMessageInterpolator();
            this.parameterNameProvider = validatorFactoryScopedContext.getParameterNameProvider();
            this.clockProvider = validatorFactoryScopedContext.getClockProvider();
            this.temporalValidationTolerance = validatorFactoryScopedContext.getTemporalValidationTolerance();
            this.scriptEvaluatorFactory = validatorFactoryScopedContext.getScriptEvaluatorFactory();
            this.failFast = validatorFactoryScopedContext.isFailFast();
            this.traversableResolverResultCacheEnabled = validatorFactoryScopedContext.isTraversableResolverResultCacheEnabled();
            this.constraintValidatorPayload = validatorFactoryScopedContext.getConstraintValidatorPayload();
        }

        public MessageInterpolator getMessageInterpolator() {
            return this.messageInterpolator;
        }

        public ExecutableParameterNameProvider getParameterNameProvider() {
            return this.parameterNameProvider;
        }

        public ClockProvider getClockProvider() {
            return this.clockProvider;
        }

        public Duration getTemporalValidationTolerance() {
            return this.temporalValidationTolerance;
        }

        public ScriptEvaluatorFactory getScriptEvaluatorFactory() {
            return this.scriptEvaluatorFactory;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public boolean isTraversableResolverResultCacheEnabled() {
            return this.traversableResolverResultCacheEnabled;
        }

        public Object getConstraintValidatorPayload() {
            return this.constraintValidatorPayload;
        }
    }
}