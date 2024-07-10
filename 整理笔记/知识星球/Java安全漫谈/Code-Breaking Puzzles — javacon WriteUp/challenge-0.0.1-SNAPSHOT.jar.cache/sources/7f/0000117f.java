package org.hibernate.validator.internal.util.logging;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import javax.validation.ClockProvider;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ElementKind;
import javax.validation.GroupDefinitionException;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;
import javax.validation.spi.ValidationProvider;
import javax.validation.valueextraction.ValueExtractor;
import javax.validation.valueextraction.ValueExtractorDeclarationException;
import javax.validation.valueextraction.ValueExtractorDefinitionException;
import javax.xml.stream.XMLStreamException;
import org.hibernate.validator.internal.engine.messageinterpolation.parser.MessageDescriptorFormatException;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.logging.formatter.ArrayOfClassesObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ClassObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.CollectionOfClassesObjectFormatter;
import org.hibernate.validator.internal.util.logging.formatter.CollectionOfObjectsToStringFormatter;
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ExecutableFormatter;
import org.hibernate.validator.internal.util.logging.formatter.ObjectArrayFormatter;
import org.hibernate.validator.internal.util.logging.formatter.TypeFormatter;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypePath;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorNotFoundException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/Log_$logger.class */
public class Log_$logger extends DelegatingBasicLogger implements Log, BasicLogger, Serializable {
    private static final long serialVersionUID = 1;
    private static final String FQCN = Log_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;
    private static final String version = "HV000001: Hibernate Validator %s";
    private static final String ignoringXmlConfiguration = "HV000002: Ignoring XML configuration.";
    private static final String usingConstraintValidatorFactory = "HV000003: Using %s as constraint validator factory.";
    private static final String usingMessageInterpolator = "HV000004: Using %s as message interpolator.";
    private static final String usingTraversableResolver = "HV000005: Using %s as traversable resolver.";
    private static final String usingValidationProvider = "HV000006: Using %s as validation provider.";
    private static final String parsingXMLFile = "HV000007: %s found. Parsing XML based configuration.";
    private static final String unableToCloseInputStream = "HV000008: Unable to close input stream.";
    private static final String unableToCloseXMLFileInputStream = "HV000010: Unable to close input stream for %s.";
    private static final String unableToCreateSchema = "HV000011: Unable to create schema for %1$s: %2$s";
    private static final String getUnableToCreateAnnotationForConfiguredConstraintException = "HV000012: Unable to create annotation for configured constraint";
    private static final String getUnableToFindPropertyWithAccessException = "HV000013: The class %1$s does not have a property '%2$s' with access %3$s.";
    private static final String getInvalidBigDecimalFormatException = "HV000016: %s does not represent a valid BigDecimal format.";
    private static final String getInvalidLengthForIntegerPartException = "HV000017: The length of the integer part cannot be negative.";
    private static final String getInvalidLengthForFractionPartException = "HV000018: The length of the fraction part cannot be negative.";
    private static final String getMinCannotBeNegativeException = "HV000019: The min parameter cannot be negative.";
    private static final String getMaxCannotBeNegativeException = "HV000020: The max parameter cannot be negative.";
    private static final String getLengthCannotBeNegativeException = "HV000021: The length cannot be negative.";
    private static final String getInvalidRegularExpressionException = "HV000022: Invalid regular expression.";
    private static final String getErrorDuringScriptExecutionException = "HV000023: Error during execution of script \"%s\" occurred.";
    private static final String getScriptMustReturnTrueOrFalseException1 = "HV000024: Script \"%s\" returned null, but must return either true or false.";
    private static final String getScriptMustReturnTrueOrFalseException3 = "HV000025: Script \"%1$s\" returned %2$s (of type %3$s), but must return either true or false.";
    private static final String getInconsistentConfigurationException = "HV000026: Assertion error: inconsistent ConfigurationImpl construction.";
    private static final String getUnableToFindProviderException = "HV000027: Unable to find provider: %s.";
    private static final String getExceptionDuringIsValidCallException = "HV000028: Unexpected exception during isValid call.";
    private static final String getConstraintValidatorFactoryMustNotReturnNullException = "HV000029: Constraint factory returned null when trying to create instance of %s.";
    private static final String getNoValidatorFoundForTypeException = "HV000030: No validator could be found for constraint '%s' validating type '%s'. Check configuration for '%s'";
    private static final String getMoreThanOneValidatorFoundForTypeException = "HV000031: There are multiple validator classes which could validate the type %1$s. The validator classes are: %2$s.";
    private static final String getUnableToInitializeConstraintValidatorException = "HV000032: Unable to initialize %s.";
    private static final String getAtLeastOneCustomMessageMustBeCreatedException = "HV000033: At least one custom message must be created if the default error message gets disabled.";
    private static final String getInvalidJavaIdentifierException = "HV000034: %s is not a valid Java Identifier.";
    private static final String getUnableToParsePropertyPathException = "HV000035: Unable to parse property path %s.";
    private static final String getTypeNotSupportedForUnwrappingException = "HV000036: Type %s not supported for unwrapping.";
    private static final String getInconsistentFailFastConfigurationException = "HV000037: Inconsistent fail fast configuration. Fail fast enabled via programmatic API, but explicitly disabled via properties.";
    private static final String getInvalidPropertyPathException0 = "HV000038: Invalid property path.";
    private static final String getInvalidPropertyPathException2 = "HV000039: Invalid property path. Either there is no property %2$s in entity %1$s or it is not possible to cascade to the property.";
    private static final String getPropertyPathMustProvideIndexOrMapKeyException = "HV000040: Property path must provide index or map key.";
    private static final String getErrorDuringCallOfTraversableResolverIsReachableException = "HV000041: Call to TraversableResolver.isReachable() threw an exception.";
    private static final String getErrorDuringCallOfTraversableResolverIsCascadableException = "HV000042: Call to TraversableResolver.isCascadable() threw an exception.";
    private static final String getUnableToExpandDefaultGroupListException = "HV000043: Unable to expand default group list %1$s into sequence %2$s.";
    private static final String getAtLeastOneGroupHasToBeSpecifiedException = "HV000044: At least one group has to be specified.";
    private static final String getGroupHasToBeAnInterfaceException = "HV000045: A group has to be an interface. %s is not.";
    private static final String getSequenceDefinitionsNotAllowedException = "HV000046: Sequence definitions are not allowed as composing parts of a sequence.";
    private static final String getCyclicDependencyInGroupsDefinitionException = "HV000047: Cyclic dependency in groups definition";
    private static final String getUnableToExpandGroupSequenceException = "HV000048: Unable to expand group sequence.";
    private static final String getInvalidDefaultGroupSequenceDefinitionException = "HV000052: Default group sequence and default group sequence provider cannot be defined at the same time.";
    private static final String getNoDefaultGroupInGroupSequenceException = "HV000053: 'Default.class' cannot appear in default group sequence list.";
    private static final String getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException = "HV000054: %s must be part of the redefined default group sequence.";
    private static final String getWrongDefaultGroupSequenceProviderTypeException = "HV000055: The default group sequence provider defined for %s has the wrong type";
    private static final String getInvalidExecutableParameterIndexException = "HV000056: Method or constructor %1$s doesn't have a parameter with index %2$d.";
    private static final String getUnableToRetrieveAnnotationParameterValueException = "HV000059: Unable to retrieve annotation parameter value.";
    private static final String getInvalidLengthOfParameterMetaDataListException = "HV000062: Method or constructor %1$s has %2$s parameters, but the passed list of parameter meta data has a size of %3$s.";
    private static final String getUnableToInstantiateException1 = "HV000063: Unable to instantiate %s.";
    private static final String getUnableToInstantiateException2 = "HV000064: Unable to instantiate %1$s: %2$s.";
    private static final String getUnableToLoadClassException = "HV000065: Unable to load class: %s from %s.";
    private static final String getStartIndexCannotBeNegativeException = "HV000068: Start index cannot be negative: %d.";
    private static final String getEndIndexCannotBeNegativeException = "HV000069: End index cannot be negative: %d.";
    private static final String getInvalidRangeException = "HV000070: Invalid Range: %1$d > %2$d.";
    private static final String getInvalidCheckDigitException = "HV000071: A explicitly specified check digit must lie outside the interval: [%1$d, %2$d].";
    private static final String getCharacterIsNotADigitException = "HV000072: '%c' is not a digit.";
    private static final String getConstraintParametersCannotStartWithValidException = "HV000073: Parameters starting with 'valid' are not allowed in a constraint.";
    private static final String getConstraintWithoutMandatoryParameterException = "HV000074: %2$s contains Constraint annotation, but does not contain a %1$s parameter.";
    private static final String getWrongDefaultValueForPayloadParameterException = "HV000075: %s contains Constraint annotation, but the payload parameter default value is not the empty array.";
    private static final String getWrongTypeForPayloadParameterException = "HV000076: %s contains Constraint annotation, but the payload parameter is of wrong type.";
    private static final String getWrongDefaultValueForGroupsParameterException = "HV000077: %s contains Constraint annotation, but the groups parameter default value is not the empty array.";
    private static final String getWrongTypeForGroupsParameterException = "HV000078: %s contains Constraint annotation, but the groups parameter is of wrong type.";
    private static final String getWrongTypeForMessageParameterException = "HV000079: %s contains Constraint annotation, but the message parameter is not of type java.lang.String.";
    private static final String getOverriddenConstraintAttributeNotFoundException = "HV000080: Overridden constraint does not define an attribute with name %s.";
    private static final String getWrongAttributeTypeForOverriddenConstraintException = "HV000081: The overriding type of a composite constraint must be identical to the overridden one. Expected %1$s found %2$s.";
    private static final String getWrongAnnotationAttributeTypeException = "HV000082: Wrong type for attribute '%2$s' of annotation %1$s. Expected: %3$s. Actual: %4$s.";
    private static final String getUnableToFindAnnotationAttributeException = "HV000083: The specified annotation %1$s defines no attribute '%2$s'.";
    private static final String getUnableToGetAnnotationAttributeException = "HV000084: Unable to get attribute '%2$s' from annotation %1$s.";
    private static final String getNoValueProvidedForAnnotationAttributeException = "HV000085: No value provided for attribute '%1$s' of annotation @%2$s.";
    private static final String getTryingToInstantiateAnnotationWithUnknownAttributesException = "HV000086: Trying to instantiate annotation %1$s with unknown attribute(s): %2$s.";
    private static final String getPropertyNameCannotBeNullOrEmptyException = "HV000087: Property name cannot be null or empty.";
    private static final String getElementTypeHasToBeFieldOrMethodException = "HV000088: Element type has to be FIELD or METHOD.";
    private static final String getMemberIsNeitherAFieldNorAMethodException = "HV000089: Member %s is neither a field nor a method.";
    private static final String getUnableToAccessMemberException = "HV000090: Unable to access %s.";
    private static final String getHasToBeAPrimitiveTypeException = "HV000091: %s has to be a primitive type.";
    private static final String getNullIsAnInvalidTypeForAConstraintValidatorException = "HV000093: null is an invalid type for a constraint validator.";
    private static final String getMissingActualTypeArgumentForTypeParameterException = "HV000094: Missing actual type argument for type parameter: %s.";
    private static final String getUnableToInstantiateConstraintValidatorFactoryClassException = "HV000095: Unable to instantiate constraint factory class %s.";
    private static final String getUnableToOpenInputStreamForMappingFileException = "HV000096: Unable to open input stream for mapping file %s.";
    private static final String getUnableToInstantiateMessageInterpolatorClassException = "HV000097: Unable to instantiate message interpolator class %s.";
    private static final String getUnableToInstantiateTraversableResolverClassException = "HV000098: Unable to instantiate traversable resolver class %s.";
    private static final String getUnableToInstantiateValidationProviderClassException = "HV000099: Unable to instantiate validation provider class %s.";
    private static final String getUnableToParseValidationXmlFileException = "HV000100: Unable to parse %s.";
    private static final String getIsNotAnAnnotationException = "HV000101: %s is not an annotation.";
    private static final String getIsNotAConstraintValidatorClassException = "HV000102: %s is not a constraint validator class.";
    private static final String getBeanClassHasAlreadyBeenConfiguredInXmlException = "HV000103: %s is configured at least twice in xml.";
    private static final String getIsDefinedTwiceInMappingXmlForBeanException = "HV000104: %1$s is defined twice in mapping xml for bean %2$s.";
    private static final String getBeanDoesNotContainTheFieldException = "HV000105: %1$s does not contain the fieldType %2$s.";
    private static final String getBeanDoesNotContainThePropertyException = "HV000106: %1$s does not contain the property %2$s.";
    private static final String getAnnotationDoesNotContainAParameterException = "HV000107: Annotation of type %1$s does not contain a parameter %2$s.";
    private static final String getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException = "HV000108: Attempt to specify an array where single value is expected.";
    private static final String getUnexpectedParameterValueException = "HV000109: Unexpected parameter value.";
    private static final String getInvalidNumberFormatException = "HV000110: Invalid %s format.";
    private static final String getInvalidCharValueException = "HV000111: Invalid char value: %s.";
    private static final String getInvalidReturnTypeException = "HV000112: Invalid return type: %s. Should be a enumeration type.";
    private static final String getReservedParameterNamesException = "HV000113: %s, %s, %s are reserved parameter names.";
    private static final String getWrongPayloadClassException = "HV000114: Specified payload class %s does not implement javax.validation.Payload";
    private static final String getErrorParsingMappingFileException = "HV000115: Error parsing mapping file.";
    private static final String getIllegalArgumentException = "HV000116: %s";
    private static final String getUnableToNarrowNodeTypeException = "HV000118: Unable to cast %s (with element kind %s) to %s";
    private static final String usingParameterNameProvider = "HV000119: Using %s as parameter name provider.";
    private static final String getUnableToInstantiateParameterNameProviderClassException = "HV000120: Unable to instantiate parameter name provider class %s.";
    private static final String getUnableToDetermineSchemaVersionException = "HV000121: Unable to parse %s.";
    private static final String getUnsupportedSchemaVersionException = "HV000122: Unsupported schema version for %s: %s.";
    private static final String getMultipleGroupConversionsForSameSourceException = "HV000124: Found multiple group conversions for source group %s: %s.";
    private static final String getGroupConversionOnNonCascadingElementException = "HV000125: Found group conversions for non-cascading element at: %s.";
    private static final String getGroupConversionForSequenceException = "HV000127: Found group conversion using a group sequence as source at: %s.";
    private static final String unknownPropertyInExpressionLanguage = "HV000129: EL expression '%s' references an unknown property";
    private static final String errorInExpressionLanguage = "HV000130: Error in EL expression '%s'";
    private static final String getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException = "HV000131: A method return value must not be marked for cascaded validation more than once in a class hierarchy, but the following two methods are marked as such: %s, %s.";
    private static final String getVoidMethodsMustNotBeConstrainedException = "HV000132: Void methods must not be constrained or marked for cascaded validation, but method %s is.";
    private static final String getBeanDoesNotContainConstructorException = "HV000133: %1$s does not contain a constructor with the parameter types %2$s.";
    private static final String getInvalidParameterTypeException = "HV000134: Unable to load parameter of type '%1$s' in %2$s.";
    private static final String getBeanDoesNotContainMethodException = "HV000135: %1$s does not contain a method with the name '%2$s' and parameter types %3$s.";
    private static final String getUnableToLoadConstraintAnnotationClassException = "HV000136: The specified constraint annotation class %1$s cannot be loaded.";
    private static final String getMethodIsDefinedTwiceInMappingXmlForBeanException = "HV000137: The method '%1$s' is defined twice in the mapping xml for bean %2$s.";
    private static final String getConstructorIsDefinedTwiceInMappingXmlForBeanException = "HV000138: The constructor '%1$s' is defined twice in the mapping xml for bean %2$s.";
    private static final String getMultipleCrossParameterValidatorClassesException = "HV000139: The constraint '%1$s' defines multiple cross parameter validators. Only one is allowed.";
    private static final String getImplicitConstraintTargetInAmbiguousConfigurationException = "HV000141: The constraint %1$s used ConstraintTarget#IMPLICIT where the target cannot be inferred.";
    private static final String getCrossParameterConstraintOnMethodWithoutParametersException = "HV000142: Cross parameter constraint %1$s is illegally placed on a parameterless method or constructor '%2$s'.";
    private static final String getCrossParameterConstraintOnClassException = "HV000143: Cross parameter constraint %1$s is illegally placed on class level.";
    private static final String getCrossParameterConstraintOnFieldException = "HV000144: Cross parameter constraint %1$s is illegally placed on field '%2$s'.";
    private static final String getParameterNodeAddedForNonCrossParameterConstraintException = "HV000146: No parameter nodes may be added since path %s doesn't refer to a cross-parameter constraint.";
    private static final String getConstrainedElementConfiguredMultipleTimesException = "HV000147: %1$s is configured multiple times (note, <getter> and <method> nodes for the same method are not allowed)";
    private static final String evaluatingExpressionLanguageExpressionCausedException = "HV000148: An exception occurred during evaluation of EL expression '%s'";
    private static final String getExceptionOccurredDuringMessageInterpolationException = "HV000149: An exception occurred during message interpolation";
    private static final String getMultipleValidatorsForSameTypeException = "HV000150: The constraint %1$s defines multiple validators for the type %2$s: %3$s, %4$s. Only one is allowed.";
    private static final String getParameterConfigurationAlteredInSubTypeException = "HV000151: A method overriding another method must not redefine the parameter constraint configuration, but method %2$s redefines the configuration of %1$s.";
    private static final String getParameterConstraintsDefinedInMethodsFromParallelTypesException = "HV000152: Two methods defined in parallel types must not declare parameter constraints, if they are overridden by the same method, but methods %s and %s both define parameter constraints.";
    private static final String getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException = "HV000153: The constraint %1$s used ConstraintTarget#%2$s but is not specified on a method or constructor.";
    private static final String getCrossParameterConstraintHasNoValidatorException = "HV000154: Cross parameter constraint %1$s has no cross-parameter validator.";
    private static final String getComposedAndComposingConstraintsHaveDifferentTypesException = "HV000155: Composed and composing constraints must have the same constraint type, but composed constraint %1$s has type %3$s, while composing constraint %2$s has type %4$s.";
    private static final String getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException = "HV000156: Constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s doesn't.";
    private static final String getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException = "HV000157: Return type of the attribute validationAppliesTo() of the constraint %s must be javax.validation.ConstraintTarget.";
    private static final String getValidationAppliesToParameterMustHaveDefaultValueImplicitException = "HV000158: Default value of the attribute validationAppliesTo() of the constraint %s must be ConstraintTarget#IMPLICIT.";
    private static final String getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException = "HV000159: Only constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s does.";
    private static final String getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException = "HV000160: Validator for cross-parameter constraint %s does not validate Object nor Object[].";
    private static final String getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException = "HV000161: Two methods defined in parallel types must not define group conversions for a cascaded method return value, if they are overridden by the same method, but methods %s and %s both define parameter constraints.";
    private static final String getMethodOrConstructorNotDefinedByValidatedTypeException = "HV000162: The validated type %1$s does not specify the constructor/method: %2$s";
    private static final String getParameterTypesDoNotMatchException = "HV000163: The actual parameter type '%1$s' is not assignable to the expected one '%2$s' for parameter %3$d of '%4$s'";
    private static final String getHasToBeABoxedTypeException = "HV000164: %s has to be a auto-boxed type.";
    private static final String getMixingImplicitWithOtherExecutableTypesException = "HV000165: Mixing IMPLICIT and other executable types is not allowed.";
    private static final String getValidateOnExecutionOnOverriddenOrInterfaceMethodException = "HV000166: @ValidateOnExecution is not allowed on methods overriding a superclass method or implementing an interface. Check configuration for %1$s";
    private static final String getOverridingConstraintDefinitionsInMultipleMappingFilesException = "HV000167: A given constraint definition can only be overridden in one mapping file. %1$s is overridden in multiple files";
    private static final String getNonTerminatedParameterException = "HV000168: The message descriptor '%1$s' contains an unbalanced meta character '%2$c' parameter.";
    private static final String getNestedParameterException = "HV000169: The message descriptor '%1$s' has nested parameters.";
    private static final String getCreationOfScriptExecutorFailedException = "HV000170: No JSR-223 scripting engine could be bootstrapped for language \"%s\".";
    private static final String getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000171: %s is configured more than once via the programmatic constraint declaration API.";
    private static final String getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000172: Property \"%2$s\" of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getMethodHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000173: Method %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getParameterHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000174: Parameter %3$s of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000175: The return value of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000176: Constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException = "HV000177: Cross-parameter constraints for the method or constructor %2$s of type %1$s are declared more than once via the programmatic constraint declaration API.";
    private static final String getMultiplierCannotBeNegativeException = "HV000178: Multiplier cannot be negative: %d.";
    private static final String getWeightCannotBeNegativeException = "HV000179: Weight cannot be negative: %d.";
    private static final String getTreatCheckAsIsNotADigitNorALetterException = "HV000180: '%c' is not a digit nor a letter.";
    private static final String getInvalidParameterCountForExecutableException = "HV000181: Wrong number of parameters. Method or constructor %1$s expects %2$d parameters, but got %3$d.";
    private static final String getNoUnwrapperFoundForTypeException = "HV000182: No validation value unwrapper is registered for type '%1$s'.";
    private static final String getUnableToInitializeELExpressionFactoryException = "HV000183: Unable to initialize 'javax.el.ExpressionFactory'. Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead";
    private static final String warnElIsUnsupported = "HV000185: Message contains EL expression: %1s, which is not supported by the selected message interpolator";
    private static final String getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException = "HV000189: The configuration of value unwrapping for property '%s' of bean '%s' is inconsistent between the field and its getter.";
    private static final String getUnableToCreateXMLEventReader = "HV000190: Unable to parse %s.";
    private static final String unknownJvmVersion = "HV000192: Couldn't determine Java version from value %1s; Not enabling features requiring Java 8";
    private static final String getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException = "HV000193: %s is configured more than once via the programmatic constraint definition API.";
    private static final String getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection = "HV000194: An empty element is only supported when a CharSequence is expected.";
    private static final String getUnableToReachPropertyToValidateException = "HV000195: Unable to reach the property to validate for the bean %s and the property path %s. A property is null along the way.";
    private static final String getUnableToConvertTypeToClassException = "HV000196: Unable to convert the Type %s to a Class.";
    private static final String getNoValueExtractorFoundForTypeException2 = "HV000197: No value extractor found for type parameter '%2$s' of type %1$s.";
    private static final String getNoValueExtractorFoundForUnwrapException = "HV000198: No suitable value extractor found for type %1$s.";
    private static final String usingClockProvider = "HV000200: Using %s as clock provider.";
    private static final String getUnableToInstantiateClockProviderClassException = "HV000201: Unable to instantiate clock provider class %s.";
    private static final String getUnableToGetCurrentTimeFromClockProvider = "HV000202: Unable to get the current time from the clock provider";
    private static final String getValueExtractorFailsToDeclareExtractedValueException = "HV000203: Value extractor type %1s fails to declare the extracted type parameter using @ExtractedValue.";
    private static final String getValueExtractorDeclaresExtractedValueMultipleTimesException = "HV000204: Only one type parameter must be marked with @ExtractedValue for value extractor type %1s.";
    private static final String getInvalidUnwrappingConfigurationForConstraintException = "HV000205: Invalid unwrapping configuration for constraint %2$s on %1$s. You can only define one of 'Unwrapping.Skip' or 'Unwrapping.Unwrap'.";
    private static final String getUnableToInstantiateValueExtractorClassException = "HV000206: Unable to instantiate value extractor class %s.";
    private static final String addingValueExtractor = "HV000207: Adding value extractor %s.";
    private static final String getValueExtractorForTypeAndTypeUseAlreadyPresentException = "HV000208: Given value extractor %2$s handles the same type and type use as previously given value extractor %1$s.";
    private static final String getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException = "HV000209: A composing constraint (%2$s) must not be given directly on the composed constraint (%1$s) and using the corresponding List annotation at the same time.";
    private static final String getUnableToFindTypeParameterInClass = "HV000210: Unable to find the type parameter %2$s in class %1$s.";
    private static final String getTypeIsNotAParameterizedNorArrayTypeException = "HV000211: Given type is neither a parameterized nor an array type: %s.";
    private static final String getInvalidTypeArgumentIndexException = "HV000212: Given type has no type argument with index %2$s: %1$s.";
    private static final String getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException = "HV000213: Given type has more than one type argument, hence an argument index must be specified: %s.";
    private static final String getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException = "HV000214: The same container element type of type %1$s is configured more than once via the programmatic constraint declaration API.";
    private static final String getParameterIsNotAValidCallException = "HV000215: Calling parameter() is not allowed for the current element.";
    private static final String getReturnValueIsNotAValidCallException = "HV000216: Calling returnValue() is not allowed for the current element.";
    private static final String getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException = "HV000217: The same container element type %2$s is configured more than once for location %1$s via the XML mapping configuration.";
    private static final String getParallelDefinitionsOfValueExtractorsException = "HV000218: Having parallel definitions of value extractors on a given class is not allowed: %s.";
    private static final String getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException = "HV000219: Unable to get the most specific value extractor for type %1$s as several most specific value extractors are declared: %2$s.";
    private static final String getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException = "HV000220: When @ExtractedValue is defined on a type parameter of a container type, the type attribute may not be set: %1$s.";
    private static final String getErrorWhileExtractingValuesInValueExtractorException = "HV000221: An error occurred while extracting values in value extractor %1$s.";
    private static final String getDuplicateDefinitionsOfValueExtractorException = "HV000222: The same value extractor %s is added more than once via the XML configuration.";
    private static final String getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException = "HV000223: Implicit unwrapping is not allowed for type %1$s as several maximally specific value extractors marked with @UnwrapByDefault are declared: %2$s.";
    private static final String getUnwrappingOfConstraintDescriptorNotSupportedYetException = "HV000224: Unwrapping of ConstraintDescriptor is not supported yet.";
    private static final String getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException = "HV000225: Only unbound wildcard type arguments are supported for the container type of the value extractor: %1$s.";
    private static final String getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException = "HV000226: Container element constraints and cascading validation are not supported on arrays: %1$s";
    private static final String getPropertyNotDefinedByValidatedTypeException = "HV000227: The validated type %1$s does not specify the property: %2$s";
    private static final String getNoValueExtractorFoundForTypeException3 = "HV000228: No value extractor found when narrowing down to the runtime type %3$s among the value extractors for type parameter '%2$s' of type %1$s.";
    private static final String getUnableToCastException = "HV000229: Unable to cast %1$s to %2$s.";
    private static final String usingScriptEvaluatorFactory = "HV000230: Using %s as script evaluator factory.";
    private static final String getUnableToInstantiateScriptEvaluatorFactoryClassException = "HV000231: Unable to instantiate script evaluator factory class %s.";
    private static final String getUnableToFindScriptEngineException = "HV000232: No JSR 223 script engine found for language \"%s\".";
    private static final String getErrorExecutingScriptException = "HV000233: An error occurred while executing the script: \"%s\".";
    private static final String logValidatorFactoryScopedConfiguration = "HV000234: Using %1$s as ValidatorFactory-scoped %2$s.";
    private static final String getUnableToCreateAnnotationDescriptor = "HV000235: Unable to create an annotation descriptor for %1$s.";
    private static final String getUnableToFindAnnotationDefDeclaredMethods = "HV000236: Unable to find the method required to create the constraint annotation descriptor.";
    private static final String getUnableToAccessMethodException = "HV000237: Unable to access method %3$s of class %2$s with parameters %4$s using lookup %1$s.";
    private static final String logTemporalValidationTolerance = "HV000238: Temporal validation tolerance set to %1$s.";
    private static final String getUnableToParseTemporalValidationToleranceException = "HV000239: Unable to parse the temporal validation tolerance property %s. It should be a duration represented in milliseconds.";
    private static final String logConstraintValidatorPayload = "HV000240: Constraint validator payload set to %1$s.";
    private static final String logUnknownElementInXmlConfiguration = "HV000241: Encountered unsupported element %1$s while parsing the XML configuration.";
    private static final String logUnableToLoadOrInstantiateJPAAwareResolver = "HV000242: Unable to load or instantiate JPA aware resolver %1$s. All properties will per default be traversable.";
    private static final String getConstraintValidatorDefinitionConstraintMismatchException = "HV000243: Constraint %2$s references constraint validator type %1$s, but this validator is defined for constraint type %3$s.";
    private static final String unableToGetXmlSchema = "HV000248: Unable to get an XML schema named %s.";

    public Log_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void version(String version2) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, version$str(), version2);
    }

    protected String version$str() {
        return version;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void ignoringXmlConfiguration() {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, ignoringXmlConfiguration$str(), new Object[0]);
    }

    protected String ignoringXmlConfiguration$str() {
        return ignoringXmlConfiguration;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingConstraintValidatorFactory(Class<? extends ConstraintValidatorFactory> constraintValidatorFactoryClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingConstraintValidatorFactory$str(), new ClassObjectFormatter(constraintValidatorFactoryClass));
    }

    protected String usingConstraintValidatorFactory$str() {
        return usingConstraintValidatorFactory;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingMessageInterpolator(Class<? extends MessageInterpolator> messageInterpolatorClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingMessageInterpolator$str(), new ClassObjectFormatter(messageInterpolatorClass));
    }

    protected String usingMessageInterpolator$str() {
        return usingMessageInterpolator;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingTraversableResolver(Class<? extends TraversableResolver> traversableResolverClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingTraversableResolver$str(), new ClassObjectFormatter(traversableResolverClass));
    }

    protected String usingTraversableResolver$str() {
        return usingTraversableResolver;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingValidationProvider(Class<? extends ValidationProvider<?>> validationProviderClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingValidationProvider$str(), new ClassObjectFormatter(validationProviderClass));
    }

    protected String usingValidationProvider$str() {
        return usingValidationProvider;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void parsingXMLFile(String fileName) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, parsingXMLFile$str(), fileName);
    }

    protected String parsingXMLFile$str() {
        return parsingXMLFile;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void unableToCloseInputStream() {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, unableToCloseInputStream$str(), new Object[0]);
    }

    protected String unableToCloseInputStream$str() {
        return unableToCloseInputStream;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void unableToCloseXMLFileInputStream(String fileName) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, unableToCloseXMLFileInputStream$str(), fileName);
    }

    protected String unableToCloseXMLFileInputStream$str() {
        return unableToCloseXMLFileInputStream;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void unableToCreateSchema(String fileName, String message) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, unableToCreateSchema$str(), fileName, message);
    }

    protected String unableToCreateSchema$str() {
        return unableToCreateSchema;
    }

    protected String getUnableToCreateAnnotationForConfiguredConstraintException$str() {
        return getUnableToCreateAnnotationForConfiguredConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToCreateAnnotationForConfiguredConstraintException(RuntimeException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToCreateAnnotationForConfiguredConstraintException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindPropertyWithAccessException$str() {
        return getUnableToFindPropertyWithAccessException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToFindPropertyWithAccessException(Class<?> beanClass, String property, ElementType elementType) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToFindPropertyWithAccessException$str(), new ClassObjectFormatter(beanClass), property, elementType));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidBigDecimalFormatException$str() {
        return getInvalidBigDecimalFormatException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidBigDecimalFormatException(String value, NumberFormatException e) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidBigDecimalFormatException$str(), value), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidLengthForIntegerPartException$str() {
        return getInvalidLengthForIntegerPartException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidLengthForIntegerPartException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidLengthForIntegerPartException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidLengthForFractionPartException$str() {
        return getInvalidLengthForFractionPartException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidLengthForFractionPartException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidLengthForFractionPartException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMinCannotBeNegativeException$str() {
        return getMinCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMinCannotBeNegativeException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMinCannotBeNegativeException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMaxCannotBeNegativeException$str() {
        return getMaxCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMaxCannotBeNegativeException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMaxCannotBeNegativeException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getLengthCannotBeNegativeException$str() {
        return getLengthCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getLengthCannotBeNegativeException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getLengthCannotBeNegativeException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidRegularExpressionException$str() {
        return getInvalidRegularExpressionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidRegularExpressionException(PatternSyntaxException e) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidRegularExpressionException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorDuringScriptExecutionException$str() {
        return getErrorDuringScriptExecutionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getErrorDuringScriptExecutionException(String script, Exception e) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getErrorDuringScriptExecutionException$str(), script), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getScriptMustReturnTrueOrFalseException1$str() {
        return getScriptMustReturnTrueOrFalseException1;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String script) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getScriptMustReturnTrueOrFalseException1$str(), script));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getScriptMustReturnTrueOrFalseException3$str() {
        return getScriptMustReturnTrueOrFalseException3;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String script, Object executionResult, String type) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getScriptMustReturnTrueOrFalseException3$str(), script, executionResult, type));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInconsistentConfigurationException$str() {
        return getInconsistentConfigurationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInconsistentConfigurationException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInconsistentConfigurationException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindProviderException$str() {
        return getUnableToFindProviderException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToFindProviderException(Class<?> providerClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToFindProviderException$str(), new ClassObjectFormatter(providerClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getExceptionDuringIsValidCallException$str() {
        return getExceptionDuringIsValidCallException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getExceptionDuringIsValidCallException(RuntimeException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getExceptionDuringIsValidCallException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstraintValidatorFactoryMustNotReturnNullException$str() {
        return getConstraintValidatorFactoryMustNotReturnNullException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getConstraintValidatorFactoryMustNotReturnNullException(Class<? extends ConstraintValidator<?, ?>> validatorClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getConstraintValidatorFactoryMustNotReturnNullException$str(), new ClassObjectFormatter(validatorClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoValidatorFoundForTypeException$str() {
        return getNoValidatorFoundForTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final UnexpectedTypeException getNoValidatorFoundForTypeException(Class<? extends Annotation> constraintType, String validatedValueType, String path) {
        UnexpectedTypeException result = new UnexpectedTypeException(String.format(getLoggingLocale(), getNoValidatorFoundForTypeException$str(), new ClassObjectFormatter(constraintType), validatedValueType, path));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMoreThanOneValidatorFoundForTypeException$str() {
        return getMoreThanOneValidatorFoundForTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final UnexpectedTypeException getMoreThanOneValidatorFoundForTypeException(Type type, Collection<Type> validatorClasses) {
        UnexpectedTypeException result = new UnexpectedTypeException(String.format(getLoggingLocale(), getMoreThanOneValidatorFoundForTypeException$str(), type, new CollectionOfObjectsToStringFormatter(validatorClasses)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInitializeConstraintValidatorException$str() {
        return getUnableToInitializeConstraintValidatorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInitializeConstraintValidatorException(Class<? extends ConstraintValidator> validatorClass, RuntimeException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInitializeConstraintValidatorException$str(), new ClassObjectFormatter(validatorClass)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getAtLeastOneCustomMessageMustBeCreatedException$str() {
        return getAtLeastOneCustomMessageMustBeCreatedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getAtLeastOneCustomMessageMustBeCreatedException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getAtLeastOneCustomMessageMustBeCreatedException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidJavaIdentifierException$str() {
        return getInvalidJavaIdentifierException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidJavaIdentifierException(String identifier) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidJavaIdentifierException$str(), identifier));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToParsePropertyPathException$str() {
        return getUnableToParsePropertyPathException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getUnableToParsePropertyPathException(String propertyPath) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getUnableToParsePropertyPathException$str(), propertyPath));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getTypeNotSupportedForUnwrappingException$str() {
        return getTypeNotSupportedForUnwrappingException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getTypeNotSupportedForUnwrappingException(Class<?> type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getTypeNotSupportedForUnwrappingException$str(), new ClassObjectFormatter(type)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInconsistentFailFastConfigurationException$str() {
        return getInconsistentFailFastConfigurationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInconsistentFailFastConfigurationException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInconsistentFailFastConfigurationException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidPropertyPathException0$str() {
        return getInvalidPropertyPathException0;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidPropertyPathException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidPropertyPathException0$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidPropertyPathException2$str() {
        return getInvalidPropertyPathException2;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidPropertyPathException(Class<?> beanClass, String propertyName) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidPropertyPathException2$str(), new ClassObjectFormatter(beanClass), propertyName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getPropertyPathMustProvideIndexOrMapKeyException$str() {
        return getPropertyPathMustProvideIndexOrMapKeyException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getPropertyPathMustProvideIndexOrMapKeyException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getPropertyPathMustProvideIndexOrMapKeyException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorDuringCallOfTraversableResolverIsReachableException$str() {
        return getErrorDuringCallOfTraversableResolverIsReachableException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getErrorDuringCallOfTraversableResolverIsReachableException(RuntimeException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getErrorDuringCallOfTraversableResolverIsReachableException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorDuringCallOfTraversableResolverIsCascadableException$str() {
        return getErrorDuringCallOfTraversableResolverIsCascadableException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getErrorDuringCallOfTraversableResolverIsCascadableException(RuntimeException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getErrorDuringCallOfTraversableResolverIsCascadableException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToExpandDefaultGroupListException$str() {
        return getUnableToExpandDefaultGroupListException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getUnableToExpandDefaultGroupListException(List<?> defaultGroupList, List<?> groupList) {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getUnableToExpandDefaultGroupListException$str(), new CollectionOfObjectsToStringFormatter(defaultGroupList), new CollectionOfObjectsToStringFormatter(groupList)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getAtLeastOneGroupHasToBeSpecifiedException$str() {
        return getAtLeastOneGroupHasToBeSpecifiedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getAtLeastOneGroupHasToBeSpecifiedException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getAtLeastOneGroupHasToBeSpecifiedException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getGroupHasToBeAnInterfaceException$str() {
        return getGroupHasToBeAnInterfaceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getGroupHasToBeAnInterfaceException(Class<?> clazz) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getGroupHasToBeAnInterfaceException$str(), new ClassObjectFormatter(clazz)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getSequenceDefinitionsNotAllowedException$str() {
        return getSequenceDefinitionsNotAllowedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getSequenceDefinitionsNotAllowedException() {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getSequenceDefinitionsNotAllowedException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCyclicDependencyInGroupsDefinitionException$str() {
        return getCyclicDependencyInGroupsDefinitionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getCyclicDependencyInGroupsDefinitionException() {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getCyclicDependencyInGroupsDefinitionException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToExpandGroupSequenceException$str() {
        return getUnableToExpandGroupSequenceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getUnableToExpandGroupSequenceException() {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getUnableToExpandGroupSequenceException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidDefaultGroupSequenceDefinitionException$str() {
        return getInvalidDefaultGroupSequenceDefinitionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getInvalidDefaultGroupSequenceDefinitionException() {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getInvalidDefaultGroupSequenceDefinitionException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoDefaultGroupInGroupSequenceException$str() {
        return getNoDefaultGroupInGroupSequenceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getNoDefaultGroupInGroupSequenceException() {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getNoDefaultGroupInGroupSequenceException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException$str() {
        return getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException(Class<?> beanClass) {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException$str(), new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongDefaultGroupSequenceProviderTypeException$str() {
        return getWrongDefaultGroupSequenceProviderTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final GroupDefinitionException getWrongDefaultGroupSequenceProviderTypeException(Class<?> beanClass) {
        GroupDefinitionException result = new GroupDefinitionException(String.format(getLoggingLocale(), getWrongDefaultGroupSequenceProviderTypeException$str(), new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidExecutableParameterIndexException$str() {
        return getInvalidExecutableParameterIndexException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidExecutableParameterIndexException(Executable executable, int index) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidExecutableParameterIndexException$str(), new ExecutableFormatter(executable), Integer.valueOf(index)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToRetrieveAnnotationParameterValueException$str() {
        return getUnableToRetrieveAnnotationParameterValueException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToRetrieveAnnotationParameterValueException(Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToRetrieveAnnotationParameterValueException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidLengthOfParameterMetaDataListException$str() {
        return getInvalidLengthOfParameterMetaDataListException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidLengthOfParameterMetaDataListException(Executable executable, int nbParameters, int listSize) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidLengthOfParameterMetaDataListException$str(), new ExecutableFormatter(executable), Integer.valueOf(nbParameters), Integer.valueOf(listSize)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateException1$str() {
        return getUnableToInstantiateException1;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateException(Class<?> clazz, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateException1$str(), new ClassObjectFormatter(clazz)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateException2$str() {
        return getUnableToInstantiateException2;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateException(String message, Class<?> clazz, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateException2$str(), message, new ClassObjectFormatter(clazz)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToLoadClassException$str() {
        return getUnableToLoadClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToLoadClassException(String className, ClassLoader loader, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToLoadClassException$str(), className, loader), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getStartIndexCannotBeNegativeException$str() {
        return getStartIndexCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getStartIndexCannotBeNegativeException(int startIndex) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getStartIndexCannotBeNegativeException$str(), Integer.valueOf(startIndex)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getEndIndexCannotBeNegativeException$str() {
        return getEndIndexCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getEndIndexCannotBeNegativeException(int endIndex) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getEndIndexCannotBeNegativeException$str(), Integer.valueOf(endIndex)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidRangeException$str() {
        return getInvalidRangeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidRangeException(int startIndex, int endIndex) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidRangeException$str(), Integer.valueOf(startIndex), Integer.valueOf(endIndex)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidCheckDigitException$str() {
        return getInvalidCheckDigitException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidCheckDigitException(int startIndex, int endIndex) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidCheckDigitException$str(), Integer.valueOf(startIndex), Integer.valueOf(endIndex)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCharacterIsNotADigitException$str() {
        return getCharacterIsNotADigitException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final NumberFormatException getCharacterIsNotADigitException(char c) {
        NumberFormatException result = new NumberFormatException(String.format(getLoggingLocale(), getCharacterIsNotADigitException$str(), Character.valueOf(c)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstraintParametersCannotStartWithValidException$str() {
        return getConstraintParametersCannotStartWithValidException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getConstraintParametersCannotStartWithValidException() {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getConstraintParametersCannotStartWithValidException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstraintWithoutMandatoryParameterException$str() {
        return getConstraintWithoutMandatoryParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getConstraintWithoutMandatoryParameterException(String parameterName, String constraintName) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getConstraintWithoutMandatoryParameterException$str(), parameterName, constraintName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongDefaultValueForPayloadParameterException$str() {
        return getWrongDefaultValueForPayloadParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongDefaultValueForPayloadParameterException(String constraintName) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongDefaultValueForPayloadParameterException$str(), constraintName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongTypeForPayloadParameterException$str() {
        return getWrongTypeForPayloadParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongTypeForPayloadParameterException(String constraintName, ClassCastException e) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongTypeForPayloadParameterException$str(), constraintName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongDefaultValueForGroupsParameterException$str() {
        return getWrongDefaultValueForGroupsParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongDefaultValueForGroupsParameterException(String constraintName) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongDefaultValueForGroupsParameterException$str(), constraintName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongTypeForGroupsParameterException$str() {
        return getWrongTypeForGroupsParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongTypeForGroupsParameterException(String constraintName, ClassCastException e) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongTypeForGroupsParameterException$str(), constraintName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongTypeForMessageParameterException$str() {
        return getWrongTypeForMessageParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongTypeForMessageParameterException(String constraintName) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongTypeForMessageParameterException$str(), constraintName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getOverriddenConstraintAttributeNotFoundException$str() {
        return getOverriddenConstraintAttributeNotFoundException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getOverriddenConstraintAttributeNotFoundException(String attributeName) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getOverriddenConstraintAttributeNotFoundException$str(), attributeName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongAttributeTypeForOverriddenConstraintException$str() {
        return getWrongAttributeTypeForOverriddenConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getWrongAttributeTypeForOverriddenConstraintException(Class<?> expectedReturnType, Class<?> currentReturnType) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getWrongAttributeTypeForOverriddenConstraintException$str(), new ClassObjectFormatter(expectedReturnType), new ClassObjectFormatter(currentReturnType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongAnnotationAttributeTypeException$str() {
        return getWrongAnnotationAttributeTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getWrongAnnotationAttributeTypeException(Class<? extends Annotation> annotationClass, String attributeName, Class<?> expectedType, Class<?> currentType) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getWrongAnnotationAttributeTypeException$str(), new ClassObjectFormatter(annotationClass), attributeName, new ClassObjectFormatter(expectedType), new ClassObjectFormatter(currentType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindAnnotationAttributeException$str() {
        return getUnableToFindAnnotationAttributeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToFindAnnotationAttributeException(Class<? extends Annotation> annotationClass, String parameterName, NoSuchMethodException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToFindAnnotationAttributeException$str(), new ClassObjectFormatter(annotationClass), parameterName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToGetAnnotationAttributeException$str() {
        return getUnableToGetAnnotationAttributeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToGetAnnotationAttributeException(Class<? extends Annotation> annotationClass, String parameterName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToGetAnnotationAttributeException$str(), new ClassObjectFormatter(annotationClass), parameterName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoValueProvidedForAnnotationAttributeException$str() {
        return getNoValueProvidedForAnnotationAttributeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getNoValueProvidedForAnnotationAttributeException(String parameterName, Class<? extends Annotation> annotation) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getNoValueProvidedForAnnotationAttributeException$str(), parameterName, new ClassObjectFormatter(annotation)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getTryingToInstantiateAnnotationWithUnknownAttributesException$str() {
        return getTryingToInstantiateAnnotationWithUnknownAttributesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final RuntimeException getTryingToInstantiateAnnotationWithUnknownAttributesException(Class<? extends Annotation> annotationType, Set<String> unknownParameters) {
        RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), getTryingToInstantiateAnnotationWithUnknownAttributesException$str(), new ClassObjectFormatter(annotationType), unknownParameters));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getPropertyNameCannotBeNullOrEmptyException$str() {
        return getPropertyNameCannotBeNullOrEmptyException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getPropertyNameCannotBeNullOrEmptyException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getPropertyNameCannotBeNullOrEmptyException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getElementTypeHasToBeFieldOrMethodException$str() {
        return getElementTypeHasToBeFieldOrMethodException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getElementTypeHasToBeFieldOrMethodException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getElementTypeHasToBeFieldOrMethodException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMemberIsNeitherAFieldNorAMethodException$str() {
        return getMemberIsNeitherAFieldNorAMethodException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMemberIsNeitherAFieldNorAMethodException(Member member) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMemberIsNeitherAFieldNorAMethodException$str(), member));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToAccessMemberException$str() {
        return getUnableToAccessMemberException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToAccessMemberException(String memberName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToAccessMemberException$str(), memberName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getHasToBeAPrimitiveTypeException$str() {
        return getHasToBeAPrimitiveTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getHasToBeAPrimitiveTypeException(Class<?> clazz) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getHasToBeAPrimitiveTypeException$str(), new ClassObjectFormatter(clazz)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNullIsAnInvalidTypeForAConstraintValidatorException$str() {
        return getNullIsAnInvalidTypeForAConstraintValidatorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getNullIsAnInvalidTypeForAConstraintValidatorException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getNullIsAnInvalidTypeForAConstraintValidatorException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMissingActualTypeArgumentForTypeParameterException$str() {
        return getMissingActualTypeArgumentForTypeParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMissingActualTypeArgumentForTypeParameterException(TypeVariable<?> typeParameter) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMissingActualTypeArgumentForTypeParameterException$str(), typeParameter));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateConstraintValidatorFactoryClassException$str() {
        return getUnableToInstantiateConstraintValidatorFactoryClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateConstraintValidatorFactoryClassException(String constraintValidatorFactoryClassName, ValidationException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateConstraintValidatorFactoryClassException$str(), constraintValidatorFactoryClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToOpenInputStreamForMappingFileException$str() {
        return getUnableToOpenInputStreamForMappingFileException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToOpenInputStreamForMappingFileException(String mappingFileName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToOpenInputStreamForMappingFileException$str(), mappingFileName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateMessageInterpolatorClassException$str() {
        return getUnableToInstantiateMessageInterpolatorClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateMessageInterpolatorClassException(String messageInterpolatorClassName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateMessageInterpolatorClassException$str(), messageInterpolatorClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateTraversableResolverClassException$str() {
        return getUnableToInstantiateTraversableResolverClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateTraversableResolverClassException(String traversableResolverClassName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateTraversableResolverClassException$str(), traversableResolverClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateValidationProviderClassException$str() {
        return getUnableToInstantiateValidationProviderClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateValidationProviderClassException(String providerClassName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateValidationProviderClassException$str(), providerClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToParseValidationXmlFileException$str() {
        return getUnableToParseValidationXmlFileException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToParseValidationXmlFileException(String file, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToParseValidationXmlFileException$str(), file), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getIsNotAnAnnotationException$str() {
        return getIsNotAnAnnotationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getIsNotAnAnnotationException(Class<?> annotationClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getIsNotAnAnnotationException$str(), new ClassObjectFormatter(annotationClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getIsNotAConstraintValidatorClassException$str() {
        return getIsNotAConstraintValidatorClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getIsNotAConstraintValidatorClassException(Class<?> validatorClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getIsNotAConstraintValidatorClassException$str(), new ClassObjectFormatter(validatorClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanClassHasAlreadyBeenConfiguredInXmlException$str() {
        return getBeanClassHasAlreadyBeenConfiguredInXmlException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanClassHasAlreadyBeenConfiguredInXmlException(Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanClassHasAlreadyBeenConfiguredInXmlException$str(), new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getIsDefinedTwiceInMappingXmlForBeanException$str() {
        return getIsDefinedTwiceInMappingXmlForBeanException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getIsDefinedTwiceInMappingXmlForBeanException(String name, Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getIsDefinedTwiceInMappingXmlForBeanException$str(), name, new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanDoesNotContainTheFieldException$str() {
        return getBeanDoesNotContainTheFieldException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanDoesNotContainTheFieldException(Class<?> beanClass, String fieldName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanDoesNotContainTheFieldException$str(), new ClassObjectFormatter(beanClass), fieldName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanDoesNotContainThePropertyException$str() {
        return getBeanDoesNotContainThePropertyException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanDoesNotContainThePropertyException(Class<?> beanClass, String getterName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanDoesNotContainThePropertyException$str(), new ClassObjectFormatter(beanClass), getterName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getAnnotationDoesNotContainAParameterException$str() {
        return getAnnotationDoesNotContainAParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getAnnotationDoesNotContainAParameterException(Class<? extends Annotation> annotationClass, String parameterName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getAnnotationDoesNotContainAParameterException$str(), new ClassObjectFormatter(annotationClass), parameterName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException$str() {
        return getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnexpectedParameterValueException$str() {
        return getUnexpectedParameterValueException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnexpectedParameterValueException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnexpectedParameterValueException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnexpectedParameterValueException(ClassCastException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnexpectedParameterValueException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidNumberFormatException$str() {
        return getInvalidNumberFormatException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInvalidNumberFormatException(String formatName, NumberFormatException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInvalidNumberFormatException$str(), formatName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidCharValueException$str() {
        return getInvalidCharValueException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInvalidCharValueException(String value) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInvalidCharValueException$str(), value));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidReturnTypeException$str() {
        return getInvalidReturnTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInvalidReturnTypeException(Class<?> returnType, ClassCastException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInvalidReturnTypeException$str(), new ClassObjectFormatter(returnType)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getReservedParameterNamesException$str() {
        return getReservedParameterNamesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getReservedParameterNamesException(String messageParameterName, String groupsParameterName, String payloadParameterName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getReservedParameterNamesException$str(), messageParameterName, groupsParameterName, payloadParameterName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWrongPayloadClassException$str() {
        return getWrongPayloadClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getWrongPayloadClassException(Class<?> payloadClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getWrongPayloadClassException$str(), new ClassObjectFormatter(payloadClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorParsingMappingFileException$str() {
        return getErrorParsingMappingFileException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getErrorParsingMappingFileException(Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getErrorParsingMappingFileException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getIllegalArgumentException$str() {
        return getIllegalArgumentException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getIllegalArgumentException(String message) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getIllegalArgumentException$str(), message));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToNarrowNodeTypeException$str() {
        return getUnableToNarrowNodeTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ClassCastException getUnableToNarrowNodeTypeException(Class<?> actualDescriptorType, ElementKind kind, Class<?> expectedDescriptorType) {
        ClassCastException result = new ClassCastException(String.format(getLoggingLocale(), getUnableToNarrowNodeTypeException$str(), new ClassObjectFormatter(actualDescriptorType), kind, new ClassObjectFormatter(expectedDescriptorType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingParameterNameProvider(Class<? extends ParameterNameProvider> parameterNameProviderClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingParameterNameProvider$str(), new ClassObjectFormatter(parameterNameProviderClass));
    }

    protected String usingParameterNameProvider$str() {
        return usingParameterNameProvider;
    }

    protected String getUnableToInstantiateParameterNameProviderClassException$str() {
        return getUnableToInstantiateParameterNameProviderClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateParameterNameProviderClassException(String parameterNameProviderClassName, ValidationException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateParameterNameProviderClassException$str(), parameterNameProviderClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToDetermineSchemaVersionException$str() {
        return getUnableToDetermineSchemaVersionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToDetermineSchemaVersionException(String file, XMLStreamException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToDetermineSchemaVersionException$str(), file), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnsupportedSchemaVersionException$str() {
        return getUnsupportedSchemaVersionException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnsupportedSchemaVersionException(String file, String version2) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnsupportedSchemaVersionException$str(), file, version2));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMultipleGroupConversionsForSameSourceException$str() {
        return getMultipleGroupConversionsForSameSourceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getMultipleGroupConversionsForSameSourceException(Class<?> from, Collection<Class<?>> tos) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getMultipleGroupConversionsForSameSourceException$str(), new ClassObjectFormatter(from), new CollectionOfClassesObjectFormatter(tos)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getGroupConversionOnNonCascadingElementException$str() {
        return getGroupConversionOnNonCascadingElementException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getGroupConversionOnNonCascadingElementException(Object context) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getGroupConversionOnNonCascadingElementException$str(), context));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getGroupConversionForSequenceException$str() {
        return getGroupConversionForSequenceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getGroupConversionForSequenceException(Class<?> from) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getGroupConversionForSequenceException$str(), new ClassObjectFormatter(from)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void unknownPropertyInExpressionLanguage(String expression, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, e, unknownPropertyInExpressionLanguage$str(), expression);
    }

    protected String unknownPropertyInExpressionLanguage$str() {
        return unknownPropertyInExpressionLanguage;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void errorInExpressionLanguage(String expression, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, e, errorInExpressionLanguage$str(), expression);
    }

    protected String errorInExpressionLanguage$str() {
        return errorInExpressionLanguage;
    }

    protected String getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException$str() {
        return getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException(Executable executable1, Executable executable2) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException$str(), new ExecutableFormatter(executable1), new ExecutableFormatter(executable2)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getVoidMethodsMustNotBeConstrainedException$str() {
        return getVoidMethodsMustNotBeConstrainedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getVoidMethodsMustNotBeConstrainedException(Executable executable) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getVoidMethodsMustNotBeConstrainedException$str(), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanDoesNotContainConstructorException$str() {
        return getBeanDoesNotContainConstructorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanDoesNotContainConstructorException(Class<?> beanClass, Class<?>[] parameterTypes) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanDoesNotContainConstructorException$str(), new ClassObjectFormatter(beanClass), new ArrayOfClassesObjectFormatter(parameterTypes)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidParameterTypeException$str() {
        return getInvalidParameterTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInvalidParameterTypeException(String type, Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInvalidParameterTypeException$str(), type, new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanDoesNotContainMethodException$str() {
        return getBeanDoesNotContainMethodException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanDoesNotContainMethodException(Class<?> beanClass, String methodName, Class<?>[] parameterTypes) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanDoesNotContainMethodException$str(), new ClassObjectFormatter(beanClass), methodName, new ArrayOfClassesObjectFormatter(parameterTypes)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToLoadConstraintAnnotationClassException$str() {
        return getUnableToLoadConstraintAnnotationClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToLoadConstraintAnnotationClassException(String constraintAnnotationClassName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToLoadConstraintAnnotationClassException$str(), constraintAnnotationClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMethodIsDefinedTwiceInMappingXmlForBeanException$str() {
        return getMethodIsDefinedTwiceInMappingXmlForBeanException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getMethodIsDefinedTwiceInMappingXmlForBeanException(Method name, Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getMethodIsDefinedTwiceInMappingXmlForBeanException$str(), name, new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstructorIsDefinedTwiceInMappingXmlForBeanException$str() {
        return getConstructorIsDefinedTwiceInMappingXmlForBeanException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getConstructorIsDefinedTwiceInMappingXmlForBeanException(Constructor<?> name, Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getConstructorIsDefinedTwiceInMappingXmlForBeanException$str(), name, new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMultipleCrossParameterValidatorClassesException$str() {
        return getMultipleCrossParameterValidatorClassesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getMultipleCrossParameterValidatorClassesException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getMultipleCrossParameterValidatorClassesException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getImplicitConstraintTargetInAmbiguousConfigurationException$str() {
        return getImplicitConstraintTargetInAmbiguousConfigurationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getImplicitConstraintTargetInAmbiguousConfigurationException(Class<? extends Annotation> constraint) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getImplicitConstraintTargetInAmbiguousConfigurationException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCrossParameterConstraintOnMethodWithoutParametersException$str() {
        return getCrossParameterConstraintOnMethodWithoutParametersException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getCrossParameterConstraintOnMethodWithoutParametersException(Class<? extends Annotation> constraint, Executable executable) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getCrossParameterConstraintOnMethodWithoutParametersException$str(), new ClassObjectFormatter(constraint), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCrossParameterConstraintOnClassException$str() {
        return getCrossParameterConstraintOnClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getCrossParameterConstraintOnClassException(Class<? extends Annotation> constraint) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getCrossParameterConstraintOnClassException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCrossParameterConstraintOnFieldException$str() {
        return getCrossParameterConstraintOnFieldException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getCrossParameterConstraintOnFieldException(Class<? extends Annotation> constraint, Member field) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getCrossParameterConstraintOnFieldException$str(), new ClassObjectFormatter(constraint), field));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterNodeAddedForNonCrossParameterConstraintException$str() {
        return getParameterNodeAddedForNonCrossParameterConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalStateException getParameterNodeAddedForNonCrossParameterConstraintException(Path path) {
        IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), getParameterNodeAddedForNonCrossParameterConstraintException$str(), path));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstrainedElementConfiguredMultipleTimesException$str() {
        return getConstrainedElementConfiguredMultipleTimesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getConstrainedElementConfiguredMultipleTimesException(String location) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getConstrainedElementConfiguredMultipleTimesException$str(), location));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void evaluatingExpressionLanguageExpressionCausedException(String expression, Exception e) {
        this.log.logf(FQCN, Logger.Level.WARN, e, evaluatingExpressionLanguageExpressionCausedException$str(), expression);
    }

    protected String evaluatingExpressionLanguageExpressionCausedException$str() {
        return evaluatingExpressionLanguageExpressionCausedException;
    }

    protected String getExceptionOccurredDuringMessageInterpolationException$str() {
        return getExceptionOccurredDuringMessageInterpolationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getExceptionOccurredDuringMessageInterpolationException(Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getExceptionOccurredDuringMessageInterpolationException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMultipleValidatorsForSameTypeException$str() {
        return getMultipleValidatorsForSameTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final UnexpectedTypeException getMultipleValidatorsForSameTypeException(Class<? extends Annotation> constraint, Type type, Class<? extends ConstraintValidator<?, ?>> validatorClass1, Class<? extends ConstraintValidator<?, ?>> validatorClass2) {
        UnexpectedTypeException result = new UnexpectedTypeException(String.format(getLoggingLocale(), getMultipleValidatorsForSameTypeException$str(), new ClassObjectFormatter(constraint), new TypeFormatter(type), new ClassObjectFormatter(validatorClass1), new ClassObjectFormatter(validatorClass2)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterConfigurationAlteredInSubTypeException$str() {
        return getParameterConfigurationAlteredInSubTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getParameterConfigurationAlteredInSubTypeException(Executable superMethod, Executable subMethod) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getParameterConfigurationAlteredInSubTypeException$str(), new ExecutableFormatter(superMethod), new ExecutableFormatter(subMethod)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterConstraintsDefinedInMethodsFromParallelTypesException$str() {
        return getParameterConstraintsDefinedInMethodsFromParallelTypesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getParameterConstraintsDefinedInMethodsFromParallelTypesException(Executable method1, Executable method2) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getParameterConstraintsDefinedInMethodsFromParallelTypesException$str(), new ExecutableFormatter(method1), new ExecutableFormatter(method2)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException$str() {
        return getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(Class<? extends Annotation> constraint, ConstraintTarget target) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException$str(), new ClassObjectFormatter(constraint), target));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCrossParameterConstraintHasNoValidatorException$str() {
        return getCrossParameterConstraintHasNoValidatorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getCrossParameterConstraintHasNoValidatorException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getCrossParameterConstraintHasNoValidatorException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getComposedAndComposingConstraintsHaveDifferentTypesException$str() {
        return getComposedAndComposingConstraintsHaveDifferentTypesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getComposedAndComposingConstraintsHaveDifferentTypesException(Class<? extends Annotation> composedConstraintClass, Class<? extends Annotation> composingConstraintClass, ConstraintDescriptorImpl.ConstraintType composedConstraintType, ConstraintDescriptorImpl.ConstraintType composingConstraintType) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getComposedAndComposingConstraintsHaveDifferentTypesException$str(), new ClassObjectFormatter(composedConstraintClass), new ClassObjectFormatter(composingConstraintClass), composedConstraintType, composingConstraintType));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException$str() {
        return getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException$str() {
        return getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValidationAppliesToParameterMustHaveDefaultValueImplicitException$str() {
        return getValidationAppliesToParameterMustHaveDefaultValueImplicitException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getValidationAppliesToParameterMustHaveDefaultValueImplicitException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getValidationAppliesToParameterMustHaveDefaultValueImplicitException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException$str() {
        return getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException$str() {
        return getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException(Class<? extends Annotation> constraint) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException$str(), new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException$str() {
        return getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException(Executable method1, Executable method2) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException$str(), new ExecutableFormatter(method1), new ExecutableFormatter(method2)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMethodOrConstructorNotDefinedByValidatedTypeException$str() {
        return getMethodOrConstructorNotDefinedByValidatedTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMethodOrConstructorNotDefinedByValidatedTypeException(Class<?> validatedType, Executable executable) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMethodOrConstructorNotDefinedByValidatedTypeException$str(), new ClassObjectFormatter(validatedType), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterTypesDoNotMatchException$str() {
        return getParameterTypesDoNotMatchException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getParameterTypesDoNotMatchException(Class<?> actualType, Type expectedType, int index, Executable executable) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getParameterTypesDoNotMatchException$str(), new ClassObjectFormatter(actualType), expectedType, Integer.valueOf(index), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getHasToBeABoxedTypeException$str() {
        return getHasToBeABoxedTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getHasToBeABoxedTypeException(Class<?> clazz) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getHasToBeABoxedTypeException$str(), new ClassObjectFormatter(clazz)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMixingImplicitWithOtherExecutableTypesException$str() {
        return getMixingImplicitWithOtherExecutableTypesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMixingImplicitWithOtherExecutableTypesException() {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMixingImplicitWithOtherExecutableTypesException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValidateOnExecutionOnOverriddenOrInterfaceMethodException$str() {
        return getValidateOnExecutionOnOverriddenOrInterfaceMethodException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getValidateOnExecutionOnOverriddenOrInterfaceMethodException(Executable executable) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getValidateOnExecutionOnOverriddenOrInterfaceMethodException$str(), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getOverridingConstraintDefinitionsInMultipleMappingFilesException$str() {
        return getOverridingConstraintDefinitionsInMultipleMappingFilesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getOverridingConstraintDefinitionsInMultipleMappingFilesException(String constraintClassName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getOverridingConstraintDefinitionsInMultipleMappingFilesException$str(), constraintClassName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNonTerminatedParameterException$str() {
        return getNonTerminatedParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final MessageDescriptorFormatException getNonTerminatedParameterException(String messageDescriptor, char character) {
        MessageDescriptorFormatException result = new MessageDescriptorFormatException(String.format(getLoggingLocale(), getNonTerminatedParameterException$str(), messageDescriptor, Character.valueOf(character)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNestedParameterException$str() {
        return getNestedParameterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final MessageDescriptorFormatException getNestedParameterException(String messageDescriptor) {
        MessageDescriptorFormatException result = new MessageDescriptorFormatException(String.format(getLoggingLocale(), getNestedParameterException$str(), messageDescriptor));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCreationOfScriptExecutorFailedException$str() {
        return getCreationOfScriptExecutorFailedException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getCreationOfScriptExecutorFailedException(String languageName, Exception e) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getCreationOfScriptExecutorFailedException$str(), languageName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, String propertyName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), propertyName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMethodHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getMethodHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getMethodHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, String method) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getMethodHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), method));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getParameterHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getParameterHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, Executable executable, int parameterIndex) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getParameterHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), new ExecutableFormatter(executable), Integer.valueOf(parameterIndex)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, Executable executable) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, String constructor) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), constructor));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException$str() {
        return getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException(Class<?> beanClass, Executable executable) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(beanClass), new ExecutableFormatter(executable)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getMultiplierCannotBeNegativeException$str() {
        return getMultiplierCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getMultiplierCannotBeNegativeException(int multiplier) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getMultiplierCannotBeNegativeException$str(), Integer.valueOf(multiplier)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getWeightCannotBeNegativeException$str() {
        return getWeightCannotBeNegativeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getWeightCannotBeNegativeException(int weight) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getWeightCannotBeNegativeException$str(), Integer.valueOf(weight)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getTreatCheckAsIsNotADigitNorALetterException$str() {
        return getTreatCheckAsIsNotADigitNorALetterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getTreatCheckAsIsNotADigitNorALetterException(int weight) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getTreatCheckAsIsNotADigitNorALetterException$str(), Integer.valueOf(weight)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidParameterCountForExecutableException$str() {
        return getInvalidParameterCountForExecutableException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getInvalidParameterCountForExecutableException(String executable, int expectedParameterCount, int actualParameterCount) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getInvalidParameterCountForExecutableException$str(), executable, Integer.valueOf(expectedParameterCount), Integer.valueOf(actualParameterCount)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoUnwrapperFoundForTypeException$str() {
        return getNoUnwrapperFoundForTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getNoUnwrapperFoundForTypeException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getNoUnwrapperFoundForTypeException$str(), type));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInitializeELExpressionFactoryException$str() {
        return getUnableToInitializeELExpressionFactoryException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInitializeELExpressionFactoryException(Throwable e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInitializeELExpressionFactoryException$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void warnElIsUnsupported(String expression) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, warnElIsUnsupported$str(), expression);
    }

    protected String warnElIsUnsupported$str() {
        return warnElIsUnsupported;
    }

    protected String getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException$str() {
        return getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException(String property, Class<?> beanClass) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException$str(), property, new ClassObjectFormatter(beanClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToCreateXMLEventReader$str() {
        return getUnableToCreateXMLEventReader;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToCreateXMLEventReader(String file, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToCreateXMLEventReader$str(), file), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void unknownJvmVersion(String vmVersionStr) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, unknownJvmVersion$str(), vmVersionStr);
    }

    protected String unknownJvmVersion$str() {
        return unknownJvmVersion;
    }

    protected String getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException$str() {
        return getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(Class<? extends Annotation> annotationClass) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException$str(), new ClassObjectFormatter(annotationClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection$str() {
        return getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToReachPropertyToValidateException$str() {
        return getUnableToReachPropertyToValidateException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToReachPropertyToValidateException(Object bean, Path path) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToReachPropertyToValidateException$str(), bean, path));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToConvertTypeToClassException$str() {
        return getUnableToConvertTypeToClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToConvertTypeToClassException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToConvertTypeToClassException$str(), type));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoValueExtractorFoundForTypeException2$str() {
        return getNoValueExtractorFoundForTypeException2;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getNoValueExtractorFoundForTypeException(Class<?> type, TypeVariable<?> typeParameter) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getNoValueExtractorFoundForTypeException2$str(), new ClassObjectFormatter(type), typeParameter));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoValueExtractorFoundForUnwrapException$str() {
        return getNoValueExtractorFoundForUnwrapException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getNoValueExtractorFoundForUnwrapException(Type type) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getNoValueExtractorFoundForUnwrapException$str(), type));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingClockProvider(Class<? extends ClockProvider> clockProviderClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingClockProvider$str(), new ClassObjectFormatter(clockProviderClass));
    }

    protected String usingClockProvider$str() {
        return usingClockProvider;
    }

    protected String getUnableToInstantiateClockProviderClassException$str() {
        return getUnableToInstantiateClockProviderClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateClockProviderClassException(String clockProviderClassName, ValidationException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateClockProviderClassException$str(), clockProviderClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToGetCurrentTimeFromClockProvider$str() {
        return getUnableToGetCurrentTimeFromClockProvider;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToGetCurrentTimeFromClockProvider(Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToGetCurrentTimeFromClockProvider$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValueExtractorFailsToDeclareExtractedValueException$str() {
        return getValueExtractorFailsToDeclareExtractedValueException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDefinitionException getValueExtractorFailsToDeclareExtractedValueException(Class<?> extractorType) {
        ValueExtractorDefinitionException result = new ValueExtractorDefinitionException(String.format(getLoggingLocale(), getValueExtractorFailsToDeclareExtractedValueException$str(), new ClassObjectFormatter(extractorType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getValueExtractorDeclaresExtractedValueMultipleTimesException$str() {
        return getValueExtractorDeclaresExtractedValueMultipleTimesException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDefinitionException getValueExtractorDeclaresExtractedValueMultipleTimesException(Class<?> extractorType) {
        ValueExtractorDefinitionException result = new ValueExtractorDefinitionException(String.format(getLoggingLocale(), getValueExtractorDeclaresExtractedValueMultipleTimesException$str(), new ClassObjectFormatter(extractorType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidUnwrappingConfigurationForConstraintException$str() {
        return getInvalidUnwrappingConfigurationForConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getInvalidUnwrappingConfigurationForConstraintException(Member member, Class<? extends Annotation> constraint) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getInvalidUnwrappingConfigurationForConstraintException$str(), member, new ClassObjectFormatter(constraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToInstantiateValueExtractorClassException$str() {
        return getUnableToInstantiateValueExtractorClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateValueExtractorClassException(String valueExtractorClassName, ValidationException e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateValueExtractorClassException$str(), valueExtractorClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void addingValueExtractor(Class<? extends ValueExtractor<?>> valueExtractorClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, addingValueExtractor$str(), new ClassObjectFormatter(valueExtractorClass));
    }

    protected String addingValueExtractor$str() {
        return addingValueExtractor;
    }

    protected String getValueExtractorForTypeAndTypeUseAlreadyPresentException$str() {
        return getValueExtractorForTypeAndTypeUseAlreadyPresentException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDeclarationException getValueExtractorForTypeAndTypeUseAlreadyPresentException(ValueExtractor<?> first, ValueExtractor<?> second) {
        ValueExtractorDeclarationException result = new ValueExtractorDeclarationException(String.format(getLoggingLocale(), getValueExtractorForTypeAndTypeUseAlreadyPresentException$str(), first, second));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException$str() {
        return getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(Class<? extends Annotation> composedConstraint, Class<? extends Annotation> composingConstraint) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException$str(), new ClassObjectFormatter(composedConstraint), new ClassObjectFormatter(composingConstraint)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindTypeParameterInClass$str() {
        return getUnableToFindTypeParameterInClass;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getUnableToFindTypeParameterInClass(Class<?> clazz, Object typeParameterReference) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getUnableToFindTypeParameterInClass$str(), new ClassObjectFormatter(clazz), typeParameterReference));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getTypeIsNotAParameterizedNorArrayTypeException$str() {
        return getTypeIsNotAParameterizedNorArrayTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getTypeIsNotAParameterizedNorArrayTypeException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getTypeIsNotAParameterizedNorArrayTypeException$str(), new TypeFormatter(type)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getInvalidTypeArgumentIndexException$str() {
        return getInvalidTypeArgumentIndexException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getInvalidTypeArgumentIndexException(Type type, int index) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getInvalidTypeArgumentIndexException$str(), new TypeFormatter(type), Integer.valueOf(index)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException$str() {
        return getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException$str(), new TypeFormatter(type)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException$str() {
        return getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException$str(), new TypeFormatter(type)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParameterIsNotAValidCallException$str() {
        return getParameterIsNotAValidCallException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getParameterIsNotAValidCallException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getParameterIsNotAValidCallException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getReturnValueIsNotAValidCallException$str() {
        return getReturnValueIsNotAValidCallException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getReturnValueIsNotAValidCallException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getReturnValueIsNotAValidCallException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException$str() {
        return getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException(ConstraintLocation rootConstraintLocation, ContainerElementTypePath path) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException$str(), rootConstraintLocation, path));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getParallelDefinitionsOfValueExtractorsException$str() {
        return getParallelDefinitionsOfValueExtractorsException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDefinitionException getParallelDefinitionsOfValueExtractorsException(Class<?> extractorImplementationType) {
        ValueExtractorDefinitionException result = new ValueExtractorDefinitionException(String.format(getLoggingLocale(), getParallelDefinitionsOfValueExtractorsException$str(), new ClassObjectFormatter(extractorImplementationType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException$str() {
        return getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(Class<?> valueType, Collection<Class<? extends ValueExtractor>> valueExtractors) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException$str(), new ClassObjectFormatter(valueType), new CollectionOfClassesObjectFormatter(valueExtractors)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException$str() {
        return getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDefinitionException getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException(Class<? extends ValueExtractor> extractorImplementationType) {
        ValueExtractorDefinitionException result = new ValueExtractorDefinitionException(String.format(getLoggingLocale(), getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException$str(), new ClassObjectFormatter(extractorImplementationType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorWhileExtractingValuesInValueExtractorException$str() {
        return getErrorWhileExtractingValuesInValueExtractorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getErrorWhileExtractingValuesInValueExtractorException(Class<? extends ValueExtractor> extractorImplementationType, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getErrorWhileExtractingValuesInValueExtractorException$str(), new ClassObjectFormatter(extractorImplementationType)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getDuplicateDefinitionsOfValueExtractorException$str() {
        return getDuplicateDefinitionsOfValueExtractorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDeclarationException getDuplicateDefinitionsOfValueExtractorException(String className) {
        ValueExtractorDeclarationException result = new ValueExtractorDeclarationException(String.format(getLoggingLocale(), getDuplicateDefinitionsOfValueExtractorException$str(), className));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException$str() {
        return getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException(Class<?> valueType, Collection<Class<? extends ValueExtractor>> valueExtractors) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException$str(), new ClassObjectFormatter(valueType), new CollectionOfClassesObjectFormatter(valueExtractors)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnwrappingOfConstraintDescriptorNotSupportedYetException$str() {
        return getUnwrappingOfConstraintDescriptorNotSupportedYetException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnwrappingOfConstraintDescriptorNotSupportedYetException() {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnwrappingOfConstraintDescriptorNotSupportedYetException$str(), new Object[0]));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException$str() {
        return getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValueExtractorDefinitionException getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException(Class<? extends ValueExtractor> valueExtractorClass) {
        ValueExtractorDefinitionException result = new ValueExtractorDefinitionException(String.format(getLoggingLocale(), getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException$str(), new ClassObjectFormatter(valueExtractorClass)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException$str() {
        return getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(Type type) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException$str(), new TypeFormatter(type)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getPropertyNotDefinedByValidatedTypeException$str() {
        return getPropertyNotDefinedByValidatedTypeException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final IllegalArgumentException getPropertyNotDefinedByValidatedTypeException(Class<?> validatedType, String propertyName) {
        IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), getPropertyNotDefinedByValidatedTypeException$str(), new ClassObjectFormatter(validatedType), propertyName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getNoValueExtractorFoundForTypeException3$str() {
        return getNoValueExtractorFoundForTypeException3;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDeclarationException getNoValueExtractorFoundForTypeException(Type declaredType, TypeVariable<?> declaredTypeParameter, Class<?> valueType) {
        ConstraintDeclarationException result = new ConstraintDeclarationException(String.format(getLoggingLocale(), getNoValueExtractorFoundForTypeException3$str(), new TypeFormatter(declaredType), declaredTypeParameter, new ClassObjectFormatter(valueType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToCastException$str() {
        return getUnableToCastException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ClassCastException getUnableToCastException(Object object, Class<?> clazz) {
        ClassCastException result = new ClassCastException(String.format(getLoggingLocale(), getUnableToCastException$str(), object, new ClassObjectFormatter(clazz)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void usingScriptEvaluatorFactory(Class<? extends ScriptEvaluatorFactory> scriptEvaluatorFactoryClass) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, usingScriptEvaluatorFactory$str(), new ClassObjectFormatter(scriptEvaluatorFactoryClass));
    }

    protected String usingScriptEvaluatorFactory$str() {
        return usingScriptEvaluatorFactory;
    }

    protected String getUnableToInstantiateScriptEvaluatorFactoryClassException$str() {
        return getUnableToInstantiateScriptEvaluatorFactoryClassException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToInstantiateScriptEvaluatorFactoryClassException(String scriptEvaluatorFactoryClassName, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToInstantiateScriptEvaluatorFactoryClassException$str(), scriptEvaluatorFactoryClassName), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindScriptEngineException$str() {
        return getUnableToFindScriptEngineException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ScriptEvaluatorNotFoundException getUnableToFindScriptEngineException(String languageName) {
        ScriptEvaluatorNotFoundException result = new ScriptEvaluatorNotFoundException(String.format(getLoggingLocale(), getUnableToFindScriptEngineException$str(), languageName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getErrorExecutingScriptException$str() {
        return getErrorExecutingScriptException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ScriptEvaluationException getErrorExecutingScriptException(String script, Exception e) {
        ScriptEvaluationException result = new ScriptEvaluationException(String.format(getLoggingLocale(), getErrorExecutingScriptException$str(), script), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void logValidatorFactoryScopedConfiguration(Class<?> configuredClass, String configuredElement) {
        this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable) null, logValidatorFactoryScopedConfiguration$str(), new ClassObjectFormatter(configuredClass), configuredElement);
    }

    protected String logValidatorFactoryScopedConfiguration$str() {
        return logValidatorFactoryScopedConfiguration;
    }

    protected String getUnableToCreateAnnotationDescriptor$str() {
        return getUnableToCreateAnnotationDescriptor;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToCreateAnnotationDescriptor(Class<?> configuredClass, Throwable e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToCreateAnnotationDescriptor$str(), new ClassObjectFormatter(configuredClass)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToFindAnnotationDefDeclaredMethods$str() {
        return getUnableToFindAnnotationDefDeclaredMethods;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToFindAnnotationDefDeclaredMethods(Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToFindAnnotationDefDeclaredMethods$str(), new Object[0]), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String getUnableToAccessMethodException$str() {
        return getUnableToAccessMethodException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToAccessMethodException(MethodHandles.Lookup lookup, Class<?> clazz, String methodName, Object[] parameterTypes, Throwable e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToAccessMethodException$str(), lookup, new ClassObjectFormatter(clazz), methodName, new ObjectArrayFormatter(parameterTypes)), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void logTemporalValidationTolerance(Duration tolerance) {
        this.log.logf(FQCN, Logger.Level.INFO, (Throwable) null, logTemporalValidationTolerance$str(), new DurationFormatter(tolerance));
    }

    protected String logTemporalValidationTolerance$str() {
        return logTemporalValidationTolerance;
    }

    protected String getUnableToParseTemporalValidationToleranceException$str() {
        return getUnableToParseTemporalValidationToleranceException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException getUnableToParseTemporalValidationToleranceException(String toleranceProperty, Exception e) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), getUnableToParseTemporalValidationToleranceException$str(), toleranceProperty), e);
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void logConstraintValidatorPayload(Object payload) {
        this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable) null, logConstraintValidatorPayload$str(), payload);
    }

    protected String logConstraintValidatorPayload$str() {
        return logConstraintValidatorPayload;
    }

    protected String logUnknownElementInXmlConfiguration$str() {
        return logUnknownElementInXmlConfiguration;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException logUnknownElementInXmlConfiguration(String tag) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), logUnknownElementInXmlConfiguration$str(), tag));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final void logUnableToLoadOrInstantiateJPAAwareResolver(String traversableResolverClassName) {
        this.log.logf(FQCN, Logger.Level.WARN, (Throwable) null, logUnableToLoadOrInstantiateJPAAwareResolver$str(), traversableResolverClassName);
    }

    protected String logUnableToLoadOrInstantiateJPAAwareResolver$str() {
        return logUnableToLoadOrInstantiateJPAAwareResolver;
    }

    protected String getConstraintValidatorDefinitionConstraintMismatchException$str() {
        return getConstraintValidatorDefinitionConstraintMismatchException;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ConstraintDefinitionException getConstraintValidatorDefinitionConstraintMismatchException(Class<? extends ConstraintValidator<?, ?>> constraintValidatorImplementationType, Class<? extends Annotation> registeredConstraintAnnotationType, Type declaredConstraintAnnotationType) {
        ConstraintDefinitionException result = new ConstraintDefinitionException(String.format(getLoggingLocale(), getConstraintValidatorDefinitionConstraintMismatchException$str(), new ClassObjectFormatter(constraintValidatorImplementationType), new ClassObjectFormatter(registeredConstraintAnnotationType), new TypeFormatter(declaredConstraintAnnotationType)));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    protected String unableToGetXmlSchema$str() {
        return unableToGetXmlSchema;
    }

    @Override // org.hibernate.validator.internal.util.logging.Log
    public final ValidationException unableToGetXmlSchema(String schemaResourceName) {
        ValidationException result = new ValidationException(String.format(getLoggingLocale(), unableToGetXmlSchema$str(), schemaResourceName));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(st, 1, st.length));
        return result;
    }
}