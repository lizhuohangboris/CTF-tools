package org.hibernate.validator.internal.util.logging;

import java.io.Serializable;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/Messages_$bundle.class */
public class Messages_$bundle implements Messages, Serializable {
    private static final long serialVersionUID = 1;
    public static final Messages_$bundle INSTANCE = new Messages_$bundle();
    private static final Locale LOCALE = Locale.ROOT;
    private static final String mustNotBeNull0 = "must not be null.";
    private static final String mustNotBeNull1 = "%s must not be null.";
    private static final String parameterMustNotBeNull = "The parameter \"%s\" must not be null.";
    private static final String parameterMustNotBeEmpty = "The parameter \"%s\" must not be empty.";
    private static final String beanTypeCannotBeNull = "The bean type cannot be null.";
    private static final String propertyPathCannotBeNull = "null is not allowed as property path.";
    private static final String propertyNameMustNotBeEmpty = "The property name must not be empty.";
    private static final String groupMustNotBeNull = "null passed as group name.";
    private static final String beanTypeMustNotBeNull = "The bean type must not be null when creating a constraint mapping.";
    private static final String methodNameMustNotBeNull = "The method name must not be null.";
    private static final String validatedObjectMustNotBeNull = "The object to be validated must not be null.";
    private static final String validatedMethodMustNotBeNull = "The method to be validated must not be null.";
    private static final String classCannotBeNull = "The class cannot be null.";
    private static final String classIsNull = "Class is null.";
    private static final String validatedConstructorMustNotBeNull = "The constructor to be validated must not be null.";
    private static final String validatedParameterArrayMustNotBeNull = "The method parameter array cannot not be null.";
    private static final String validatedConstructorCreatedInstanceMustNotBeNull = "The created instance must not be null.";
    private static final String inputStreamCannotBeNull = "The input stream for #addMapping() cannot be null.";
    private static final String constraintOnConstructorOfNonStaticInnerClass = "Constraints on the parameters of constructors of non-static inner classes are not supported if those parameters have a generic type due to JDK bug JDK-5087240.";
    private static final String parameterizedTypesWithMoreThanOneTypeArgument = "Custom parameterized types with more than one type argument are not supported and will not be checked for type use constraints.";
    private static final String unableToUseResourceBundleAggregation = "Hibernate Validator cannot instantiate AggregateResourceBundle.CONTROL. This can happen most notably in a Google App Engine environment or when running Hibernate Validator as Java 9 named module. A PlatformResourceBundleLocator without bundle aggregation was created. This only affects you in case you are using multiple ConstraintDefinitionContributor JARs. ConstraintDefinitionContributors are a Hibernate Validator specific feature. All Bean Validation features work as expected. See also https://hibernate.atlassian.net/browse/HV-1023.";
    private static final String annotationTypeMustNotBeNull = "The annotation type must not be null when creating a constraint definition.";
    private static final String annotationTypeMustBeAnnotatedWithConstraint = "The annotation type must be annotated with @javax.validation.Constraint when creating a constraint definition.";

    protected Messages_$bundle() {
    }

    protected Object readResolve() {
        return INSTANCE;
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    protected String mustNotBeNull0$str() {
        return mustNotBeNull0;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String mustNotBeNull() {
        return mustNotBeNull0$str();
    }

    protected String mustNotBeNull1$str() {
        return mustNotBeNull1;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String mustNotBeNull(String parameterName) {
        return String.format(getLoggingLocale(), mustNotBeNull1$str(), parameterName);
    }

    protected String parameterMustNotBeNull$str() {
        return parameterMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String parameterMustNotBeNull(String parameterName) {
        return String.format(getLoggingLocale(), parameterMustNotBeNull$str(), parameterName);
    }

    protected String parameterMustNotBeEmpty$str() {
        return parameterMustNotBeEmpty;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String parameterMustNotBeEmpty(String parameterName) {
        return String.format(getLoggingLocale(), parameterMustNotBeEmpty$str(), parameterName);
    }

    protected String beanTypeCannotBeNull$str() {
        return beanTypeCannotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String beanTypeCannotBeNull() {
        return beanTypeCannotBeNull$str();
    }

    protected String propertyPathCannotBeNull$str() {
        return propertyPathCannotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String propertyPathCannotBeNull() {
        return propertyPathCannotBeNull$str();
    }

    protected String propertyNameMustNotBeEmpty$str() {
        return propertyNameMustNotBeEmpty;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String propertyNameMustNotBeEmpty() {
        return propertyNameMustNotBeEmpty$str();
    }

    protected String groupMustNotBeNull$str() {
        return groupMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String groupMustNotBeNull() {
        return groupMustNotBeNull$str();
    }

    protected String beanTypeMustNotBeNull$str() {
        return beanTypeMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String beanTypeMustNotBeNull() {
        return beanTypeMustNotBeNull$str();
    }

    protected String methodNameMustNotBeNull$str() {
        return methodNameMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String methodNameMustNotBeNull() {
        return methodNameMustNotBeNull$str();
    }

    protected String validatedObjectMustNotBeNull$str() {
        return validatedObjectMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String validatedObjectMustNotBeNull() {
        return validatedObjectMustNotBeNull$str();
    }

    protected String validatedMethodMustNotBeNull$str() {
        return validatedMethodMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String validatedMethodMustNotBeNull() {
        return validatedMethodMustNotBeNull$str();
    }

    protected String classCannotBeNull$str() {
        return classCannotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String classCannotBeNull() {
        return classCannotBeNull$str();
    }

    protected String classIsNull$str() {
        return classIsNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String classIsNull() {
        return classIsNull$str();
    }

    protected String validatedConstructorMustNotBeNull$str() {
        return validatedConstructorMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String validatedConstructorMustNotBeNull() {
        return validatedConstructorMustNotBeNull$str();
    }

    protected String validatedParameterArrayMustNotBeNull$str() {
        return validatedParameterArrayMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String validatedParameterArrayMustNotBeNull() {
        return validatedParameterArrayMustNotBeNull$str();
    }

    protected String validatedConstructorCreatedInstanceMustNotBeNull$str() {
        return validatedConstructorCreatedInstanceMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String validatedConstructorCreatedInstanceMustNotBeNull() {
        return validatedConstructorCreatedInstanceMustNotBeNull$str();
    }

    protected String inputStreamCannotBeNull$str() {
        return inputStreamCannotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String inputStreamCannotBeNull() {
        return String.format(getLoggingLocale(), inputStreamCannotBeNull$str(), new Object[0]);
    }

    protected String constraintOnConstructorOfNonStaticInnerClass$str() {
        return constraintOnConstructorOfNonStaticInnerClass;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String constraintOnConstructorOfNonStaticInnerClass() {
        return String.format(getLoggingLocale(), constraintOnConstructorOfNonStaticInnerClass$str(), new Object[0]);
    }

    protected String parameterizedTypesWithMoreThanOneTypeArgument$str() {
        return parameterizedTypesWithMoreThanOneTypeArgument;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String parameterizedTypesWithMoreThanOneTypeArgument() {
        return String.format(getLoggingLocale(), parameterizedTypesWithMoreThanOneTypeArgument$str(), new Object[0]);
    }

    protected String unableToUseResourceBundleAggregation$str() {
        return unableToUseResourceBundleAggregation;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String unableToUseResourceBundleAggregation() {
        return String.format(getLoggingLocale(), unableToUseResourceBundleAggregation$str(), new Object[0]);
    }

    protected String annotationTypeMustNotBeNull$str() {
        return annotationTypeMustNotBeNull;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String annotationTypeMustNotBeNull() {
        return annotationTypeMustNotBeNull$str();
    }

    protected String annotationTypeMustBeAnnotatedWithConstraint$str() {
        return annotationTypeMustBeAnnotatedWithConstraint;
    }

    @Override // org.hibernate.validator.internal.util.logging.Messages
    public final String annotationTypeMustBeAnnotatedWithConstraint() {
        return annotationTypeMustBeAnnotatedWithConstraint$str();
    }
}