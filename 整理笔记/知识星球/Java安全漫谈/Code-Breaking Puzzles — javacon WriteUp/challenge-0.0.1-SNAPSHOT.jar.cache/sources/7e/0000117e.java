package org.hibernate.validator.internal.util.logging;

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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletResponse;
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
import org.apache.catalina.servlets.WebdavStatus;
import org.apache.coyote.http11.Constants;
import org.apache.el.parser.ELParserConstants;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
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
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.FormatWith;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;

@MessageLogger(projectCode = "HV")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/Log.class */
public interface Log extends BasicLogger {
    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 1, value = "Hibernate Validator %s")
    void version(String str);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 2, value = "Ignoring XML configuration.")
    void ignoringXmlConfiguration();

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 3, value = "Using %s as constraint validator factory.")
    void usingConstraintValidatorFactory(@FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidatorFactory> cls);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 4, value = "Using %s as message interpolator.")
    void usingMessageInterpolator(@FormatWith(ClassObjectFormatter.class) Class<? extends MessageInterpolator> cls);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 5, value = "Using %s as traversable resolver.")
    void usingTraversableResolver(@FormatWith(ClassObjectFormatter.class) Class<? extends TraversableResolver> cls);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 6, value = "Using %s as validation provider.")
    void usingValidationProvider(@FormatWith(ClassObjectFormatter.class) Class<? extends ValidationProvider<?>> cls);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 7, value = "%s found. Parsing XML based configuration.")
    void parsingXMLFile(String str);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 8, value = "Unable to close input stream.")
    void unableToCloseInputStream();

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 10, value = "Unable to close input stream for %s.")
    void unableToCloseXMLFileInputStream(String str);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11, value = "Unable to create schema for %1$s: %2$s")
    void unableToCreateSchema(String str, String str2);

    @Message(id = 12, value = "Unable to create annotation for configured constraint")
    ValidationException getUnableToCreateAnnotationForConfiguredConstraintException(@Cause RuntimeException runtimeException);

    @Message(id = 13, value = "The class %1$s does not have a property '%2$s' with access %3$s.")
    ValidationException getUnableToFindPropertyWithAccessException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str, ElementType elementType);

    @Message(id = 16, value = "%s does not represent a valid BigDecimal format.")
    IllegalArgumentException getInvalidBigDecimalFormatException(String str, @Cause NumberFormatException numberFormatException);

    @Message(id = 17, value = "The length of the integer part cannot be negative.")
    IllegalArgumentException getInvalidLengthForIntegerPartException();

    @Message(id = 18, value = "The length of the fraction part cannot be negative.")
    IllegalArgumentException getInvalidLengthForFractionPartException();

    @Message(id = 19, value = "The min parameter cannot be negative.")
    IllegalArgumentException getMinCannotBeNegativeException();

    @Message(id = 20, value = "The max parameter cannot be negative.")
    IllegalArgumentException getMaxCannotBeNegativeException();

    @Message(id = 21, value = "The length cannot be negative.")
    IllegalArgumentException getLengthCannotBeNegativeException();

    @Message(id = 22, value = "Invalid regular expression.")
    IllegalArgumentException getInvalidRegularExpressionException(@Cause PatternSyntaxException patternSyntaxException);

    @Message(id = 23, value = "Error during execution of script \"%s\" occurred.")
    ConstraintDeclarationException getErrorDuringScriptExecutionException(String str, @Cause Exception exc);

    @Message(id = 24, value = "Script \"%s\" returned null, but must return either true or false.")
    ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String str);

    @Message(id = 25, value = "Script \"%1$s\" returned %2$s (of type %3$s), but must return either true or false.")
    ConstraintDeclarationException getScriptMustReturnTrueOrFalseException(String str, Object obj, String str2);

    @Message(id = 26, value = "Assertion error: inconsistent ConfigurationImpl construction.")
    ValidationException getInconsistentConfigurationException();

    @Message(id = 27, value = "Unable to find provider: %s.")
    ValidationException getUnableToFindProviderException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 28, value = "Unexpected exception during isValid call.")
    ValidationException getExceptionDuringIsValidCallException(@Cause RuntimeException runtimeException);

    @Message(id = 29, value = "Constraint factory returned null when trying to create instance of %s.")
    ValidationException getConstraintValidatorFactoryMustNotReturnNullException(@FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> cls);

    @Message(id = 30, value = "No validator could be found for constraint '%s' validating type '%s'. Check configuration for '%s'")
    UnexpectedTypeException getNoValidatorFoundForTypeException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, String str, String str2);

    @Message(id = 31, value = "There are multiple validator classes which could validate the type %1$s. The validator classes are: %2$s.")
    UnexpectedTypeException getMoreThanOneValidatorFoundForTypeException(Type type, @FormatWith(CollectionOfObjectsToStringFormatter.class) Collection<Type> collection);

    @Message(id = 32, value = "Unable to initialize %s.")
    ValidationException getUnableToInitializeConstraintValidatorException(@FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidator> cls, @Cause RuntimeException runtimeException);

    @Message(id = 33, value = "At least one custom message must be created if the default error message gets disabled.")
    ValidationException getAtLeastOneCustomMessageMustBeCreatedException();

    @Message(id = 34, value = "%s is not a valid Java Identifier.")
    IllegalArgumentException getInvalidJavaIdentifierException(String str);

    @Message(id = 35, value = "Unable to parse property path %s.")
    IllegalArgumentException getUnableToParsePropertyPathException(String str);

    @Message(id = 36, value = "Type %s not supported for unwrapping.")
    ValidationException getTypeNotSupportedForUnwrappingException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 37, value = "Inconsistent fail fast configuration. Fail fast enabled via programmatic API, but explicitly disabled via properties.")
    ValidationException getInconsistentFailFastConfigurationException();

    @Message(id = 38, value = "Invalid property path.")
    IllegalArgumentException getInvalidPropertyPathException();

    @Message(id = 39, value = "Invalid property path. Either there is no property %2$s in entity %1$s or it is not possible to cascade to the property.")
    IllegalArgumentException getInvalidPropertyPathException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = 40, value = "Property path must provide index or map key.")
    IllegalArgumentException getPropertyPathMustProvideIndexOrMapKeyException();

    @Message(id = 41, value = "Call to TraversableResolver.isReachable() threw an exception.")
    ValidationException getErrorDuringCallOfTraversableResolverIsReachableException(@Cause RuntimeException runtimeException);

    @Message(id = 42, value = "Call to TraversableResolver.isCascadable() threw an exception.")
    ValidationException getErrorDuringCallOfTraversableResolverIsCascadableException(@Cause RuntimeException runtimeException);

    @Message(id = ELParserConstants.EMPTY, value = "Unable to expand default group list %1$s into sequence %2$s.")
    GroupDefinitionException getUnableToExpandDefaultGroupListException(@FormatWith(CollectionOfObjectsToStringFormatter.class) List<?> list, @FormatWith(CollectionOfObjectsToStringFormatter.class) List<?> list2);

    @Message(id = 44, value = "At least one group has to be specified.")
    IllegalArgumentException getAtLeastOneGroupHasToBeSpecifiedException();

    @Message(id = 45, value = "A group has to be an interface. %s is not.")
    ValidationException getGroupHasToBeAnInterfaceException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 46, value = "Sequence definitions are not allowed as composing parts of a sequence.")
    GroupDefinitionException getSequenceDefinitionsNotAllowedException();

    @Message(id = 47, value = "Cyclic dependency in groups definition")
    GroupDefinitionException getCyclicDependencyInGroupsDefinitionException();

    @Message(id = 48, value = "Unable to expand group sequence.")
    GroupDefinitionException getUnableToExpandGroupSequenceException();

    @Message(id = 52, value = "Default group sequence and default group sequence provider cannot be defined at the same time.")
    GroupDefinitionException getInvalidDefaultGroupSequenceDefinitionException();

    @Message(id = 53, value = "'Default.class' cannot appear in default group sequence list.")
    GroupDefinitionException getNoDefaultGroupInGroupSequenceException();

    @Message(id = 54, value = "%s must be part of the redefined default group sequence.")
    GroupDefinitionException getBeanClassMustBePartOfRedefinedDefaultGroupSequenceException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 55, value = "The default group sequence provider defined for %s has the wrong type")
    GroupDefinitionException getWrongDefaultGroupSequenceProviderTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 56, value = "Method or constructor %1$s doesn't have a parameter with index %2$d.")
    IllegalArgumentException getInvalidExecutableParameterIndexException(@FormatWith(ExecutableFormatter.class) Executable executable, int i);

    @Message(id = 59, value = "Unable to retrieve annotation parameter value.")
    ValidationException getUnableToRetrieveAnnotationParameterValueException(@Cause Exception exc);

    @Message(id = 62, value = "Method or constructor %1$s has %2$s parameters, but the passed list of parameter meta data has a size of %3$s.")
    IllegalArgumentException getInvalidLengthOfParameterMetaDataListException(@FormatWith(ExecutableFormatter.class) Executable executable, int i, int i2);

    @Message(id = Constants.QUESTION, value = "Unable to instantiate %s.")
    ValidationException getUnableToInstantiateException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @Cause Exception exc);

    @Message(id = 64, value = "Unable to instantiate %1$s: %2$s.")
    ValidationException getUnableToInstantiateException(String str, @FormatWith(ClassObjectFormatter.class) Class<?> cls, @Cause Exception exc);

    @Message(id = 65, value = "Unable to load class: %s from %s.")
    ValidationException getUnableToLoadClassException(String str, ClassLoader classLoader, @Cause Exception exc);

    @Message(id = 68, value = "Start index cannot be negative: %d.")
    IllegalArgumentException getStartIndexCannotBeNegativeException(int i);

    @Message(id = TypeReference.CONSTRUCTOR_REFERENCE, value = "End index cannot be negative: %d.")
    IllegalArgumentException getEndIndexCannotBeNegativeException(int i);

    @Message(id = 70, value = "Invalid Range: %1$d > %2$d.")
    IllegalArgumentException getInvalidRangeException(int i, int i2);

    @Message(id = TypeReference.CAST, value = "A explicitly specified check digit must lie outside the interval: [%1$d, %2$d].")
    IllegalArgumentException getInvalidCheckDigitException(int i, int i2);

    @Message(id = 72, value = "'%c' is not a digit.")
    NumberFormatException getCharacterIsNotADigitException(char c);

    @Message(id = 73, value = "Parameters starting with 'valid' are not allowed in a constraint.")
    ConstraintDefinitionException getConstraintParametersCannotStartWithValidException();

    @Message(id = 74, value = "%2$s contains Constraint annotation, but does not contain a %1$s parameter.")
    ConstraintDefinitionException getConstraintWithoutMandatoryParameterException(String str, String str2);

    @Message(id = TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT, value = "%s contains Constraint annotation, but the payload parameter default value is not the empty array.")
    ConstraintDefinitionException getWrongDefaultValueForPayloadParameterException(String str);

    @Message(id = BaseNCodec.MIME_CHUNK_SIZE, value = "%s contains Constraint annotation, but the payload parameter is of wrong type.")
    ConstraintDefinitionException getWrongTypeForPayloadParameterException(String str, @Cause ClassCastException classCastException);

    @Message(id = 77, value = "%s contains Constraint annotation, but the groups parameter default value is not the empty array.")
    ConstraintDefinitionException getWrongDefaultValueForGroupsParameterException(String str);

    @Message(id = 78, value = "%s contains Constraint annotation, but the groups parameter is of wrong type.")
    ConstraintDefinitionException getWrongTypeForGroupsParameterException(String str, @Cause ClassCastException classCastException);

    @Message(id = Opcodes.IASTORE, value = "%s contains Constraint annotation, but the message parameter is not of type java.lang.String.")
    ConstraintDefinitionException getWrongTypeForMessageParameterException(String str);

    @Message(id = 80, value = "Overridden constraint does not define an attribute with name %s.")
    ConstraintDefinitionException getOverriddenConstraintAttributeNotFoundException(String str);

    @Message(id = Opcodes.FASTORE, value = "The overriding type of a composite constraint must be identical to the overridden one. Expected %1$s found %2$s.")
    ConstraintDefinitionException getWrongAttributeTypeForOverriddenConstraintException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ClassObjectFormatter.class) Class<?> cls2);

    @Message(id = Opcodes.DASTORE, value = "Wrong type for attribute '%2$s' of annotation %1$s. Expected: %3$s. Actual: %4$s.")
    ValidationException getWrongAnnotationAttributeTypeException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, String str, @FormatWith(ClassObjectFormatter.class) Class<?> cls2, @FormatWith(ClassObjectFormatter.class) Class<?> cls3);

    @Message(id = 83, value = "The specified annotation %1$s defines no attribute '%2$s'.")
    ValidationException getUnableToFindAnnotationAttributeException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, String str, @Cause NoSuchMethodException noSuchMethodException);

    @Message(id = Opcodes.BASTORE, value = "Unable to get attribute '%2$s' from annotation %1$s.")
    ValidationException getUnableToGetAnnotationAttributeException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, String str, @Cause Exception exc);

    @Message(id = Opcodes.CASTORE, value = "No value provided for attribute '%1$s' of annotation @%2$s.")
    IllegalArgumentException getNoValueProvidedForAnnotationAttributeException(String str, @FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.SASTORE, value = "Trying to instantiate annotation %1$s with unknown attribute(s): %2$s.")
    RuntimeException getTryingToInstantiateAnnotationWithUnknownAttributesException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, Set<String> set);

    @Message(id = Opcodes.POP, value = "Property name cannot be null or empty.")
    IllegalArgumentException getPropertyNameCannotBeNullOrEmptyException();

    @Message(id = 88, value = "Element type has to be FIELD or METHOD.")
    IllegalArgumentException getElementTypeHasToBeFieldOrMethodException();

    @Message(id = 89, value = "Member %s is neither a field nor a method.")
    IllegalArgumentException getMemberIsNeitherAFieldNorAMethodException(Member member);

    @Message(id = 90, value = "Unable to access %s.")
    ValidationException getUnableToAccessMemberException(String str, @Cause Exception exc);

    @Message(id = 91, value = "%s has to be a primitive type.")
    IllegalArgumentException getHasToBeAPrimitiveTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 93, value = "null is an invalid type for a constraint validator.")
    ValidationException getNullIsAnInvalidTypeForAConstraintValidatorException();

    @Message(id = Opcodes.DUP2_X2, value = "Missing actual type argument for type parameter: %s.")
    IllegalArgumentException getMissingActualTypeArgumentForTypeParameterException(TypeVariable<?> typeVariable);

    @Message(id = Opcodes.SWAP, value = "Unable to instantiate constraint factory class %s.")
    ValidationException getUnableToInstantiateConstraintValidatorFactoryClassException(String str, @Cause ValidationException validationException);

    @Message(id = 96, value = "Unable to open input stream for mapping file %s.")
    ValidationException getUnableToOpenInputStreamForMappingFileException(String str);

    @Message(id = 97, value = "Unable to instantiate message interpolator class %s.")
    ValidationException getUnableToInstantiateMessageInterpolatorClassException(String str, @Cause Exception exc);

    @Message(id = Opcodes.FADD, value = "Unable to instantiate traversable resolver class %s.")
    ValidationException getUnableToInstantiateTraversableResolverClassException(String str, @Cause Exception exc);

    @Message(id = 99, value = "Unable to instantiate validation provider class %s.")
    ValidationException getUnableToInstantiateValidationProviderClassException(String str, @Cause Exception exc);

    @Message(id = 100, value = "Unable to parse %s.")
    ValidationException getUnableToParseValidationXmlFileException(String str, @Cause Exception exc);

    @Message(id = 101, value = "%s is not an annotation.")
    ValidationException getIsNotAnAnnotationException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.FSUB, value = "%s is not a constraint validator class.")
    ValidationException getIsNotAConstraintValidatorClassException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.DSUB, value = "%s is configured at least twice in xml.")
    ValidationException getBeanClassHasAlreadyBeenConfiguredInXmlException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 104, value = "%1$s is defined twice in mapping xml for bean %2$s.")
    ValidationException getIsDefinedTwiceInMappingXmlForBeanException(String str, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.LMUL, value = "%1$s does not contain the fieldType %2$s.")
    ValidationException getBeanDoesNotContainTheFieldException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = Opcodes.FMUL, value = "%1$s does not contain the property %2$s.")
    ValidationException getBeanDoesNotContainThePropertyException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = Opcodes.DMUL, value = "Annotation of type %1$s does not contain a parameter %2$s.")
    ValidationException getAnnotationDoesNotContainAParameterException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, String str);

    @Message(id = 108, value = "Attempt to specify an array where single value is expected.")
    ValidationException getAttemptToSpecifyAnArrayWhereSingleValueIsExpectedException();

    @Message(id = Opcodes.LDIV, value = "Unexpected parameter value.")
    ValidationException getUnexpectedParameterValueException();

    ValidationException getUnexpectedParameterValueException(@Cause ClassCastException classCastException);

    @Message(id = Opcodes.FDIV, value = "Invalid %s format.")
    ValidationException getInvalidNumberFormatException(String str, @Cause NumberFormatException numberFormatException);

    @Message(id = Opcodes.DDIV, value = "Invalid char value: %s.")
    ValidationException getInvalidCharValueException(String str);

    @Message(id = 112, value = "Invalid return type: %s. Should be a enumeration type.")
    ValidationException getInvalidReturnTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @Cause ClassCastException classCastException);

    @Message(id = Opcodes.LREM, value = "%s, %s, %s are reserved parameter names.")
    ValidationException getReservedParameterNamesException(String str, String str2, String str3);

    @Message(id = Opcodes.FREM, value = "Specified payload class %s does not implement javax.validation.Payload")
    ValidationException getWrongPayloadClassException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 115, value = "Error parsing mapping file.")
    ValidationException getErrorParsingMappingFileException(@Cause Exception exc);

    @Message(id = 116, value = "%s")
    IllegalArgumentException getIllegalArgumentException(String str);

    @Message(id = Opcodes.FNEG, value = "Unable to cast %s (with element kind %s) to %s")
    ClassCastException getUnableToNarrowNodeTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, ElementKind elementKind, @FormatWith(ClassObjectFormatter.class) Class<?> cls2);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = Opcodes.DNEG, value = "Using %s as parameter name provider.")
    void usingParameterNameProvider(@FormatWith(ClassObjectFormatter.class) Class<? extends ParameterNameProvider> cls);

    @Message(id = 120, value = "Unable to instantiate parameter name provider class %s.")
    ValidationException getUnableToInstantiateParameterNameProviderClassException(String str, @Cause ValidationException validationException);

    @Message(id = Opcodes.LSHL, value = "Unable to parse %s.")
    ValidationException getUnableToDetermineSchemaVersionException(String str, @Cause XMLStreamException xMLStreamException);

    @Message(id = 122, value = "Unsupported schema version for %s: %s.")
    ValidationException getUnsupportedSchemaVersionException(String str, String str2);

    @Message(id = 124, value = "Found multiple group conversions for source group %s: %s.")
    ConstraintDeclarationException getMultipleGroupConversionsForSameSourceException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(CollectionOfClassesObjectFormatter.class) Collection<Class<?>> collection);

    @Message(id = 125, value = "Found group conversions for non-cascading element at: %s.")
    ConstraintDeclarationException getGroupConversionOnNonCascadingElementException(Object obj);

    @Message(id = 127, value = "Found group conversion using a group sequence as source at: %s.")
    ConstraintDeclarationException getGroupConversionForSequenceException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = Opcodes.LOR, value = "EL expression '%s' references an unknown property")
    void unknownPropertyInExpressionLanguage(String str, @Cause Exception exc);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 130, value = "Error in EL expression '%s'")
    void errorInExpressionLanguage(String str, @Cause Exception exc);

    @Message(id = Opcodes.LXOR, value = "A method return value must not be marked for cascaded validation more than once in a class hierarchy, but the following two methods are marked as such: %s, %s.")
    ConstraintDeclarationException getMethodReturnValueMustNotBeMarkedMoreThanOnceForCascadedValidationException(@FormatWith(ExecutableFormatter.class) Executable executable, @FormatWith(ExecutableFormatter.class) Executable executable2);

    @Message(id = 132, value = "Void methods must not be constrained or marked for cascaded validation, but method %s is.")
    ConstraintDeclarationException getVoidMethodsMustNotBeConstrainedException(@FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = Opcodes.I2L, value = "%1$s does not contain a constructor with the parameter types %2$s.")
    ValidationException getBeanDoesNotContainConstructorException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ArrayOfClassesObjectFormatter.class) Class<?>[] clsArr);

    @Message(id = Opcodes.I2F, value = "Unable to load parameter of type '%1$s' in %2$s.")
    ValidationException getInvalidParameterTypeException(String str, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.I2D, value = "%1$s does not contain a method with the name '%2$s' and parameter types %3$s.")
    ValidationException getBeanDoesNotContainMethodException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str, @FormatWith(ArrayOfClassesObjectFormatter.class) Class<?>[] clsArr);

    @Message(id = 136, value = "The specified constraint annotation class %1$s cannot be loaded.")
    ValidationException getUnableToLoadConstraintAnnotationClassException(String str, @Cause Exception exc);

    @Message(id = Opcodes.L2F, value = "The method '%1$s' is defined twice in the mapping xml for bean %2$s.")
    ValidationException getMethodIsDefinedTwiceInMappingXmlForBeanException(Method method, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.L2D, value = "The constructor '%1$s' is defined twice in the mapping xml for bean %2$s.")
    ValidationException getConstructorIsDefinedTwiceInMappingXmlForBeanException(Constructor<?> constructor, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.F2I, value = "The constraint '%1$s' defines multiple cross parameter validators. Only one is allowed.")
    ConstraintDefinitionException getMultipleCrossParameterValidatorClassesException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.F2D, value = "The constraint %1$s used ConstraintTarget#IMPLICIT where the target cannot be inferred.")
    ConstraintDeclarationException getImplicitConstraintTargetInAmbiguousConfigurationException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.D2I, value = "Cross parameter constraint %1$s is illegally placed on a parameterless method or constructor '%2$s'.")
    ConstraintDeclarationException getCrossParameterConstraintOnMethodWithoutParametersException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, @FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = Opcodes.D2L, value = "Cross parameter constraint %1$s is illegally placed on class level.")
    ConstraintDeclarationException getCrossParameterConstraintOnClassException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = 144, value = "Cross parameter constraint %1$s is illegally placed on field '%2$s'.")
    ConstraintDeclarationException getCrossParameterConstraintOnFieldException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, Member member);

    @Message(id = Opcodes.I2C, value = "No parameter nodes may be added since path %s doesn't refer to a cross-parameter constraint.")
    IllegalStateException getParameterNodeAddedForNonCrossParameterConstraintException(Path path);

    @Message(id = Opcodes.I2S, value = "%1$s is configured multiple times (note, <getter> and <method> nodes for the same method are not allowed)")
    ValidationException getConstrainedElementConfiguredMultipleTimesException(String str);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = Opcodes.LCMP, value = "An exception occurred during evaluation of EL expression '%s'")
    void evaluatingExpressionLanguageExpressionCausedException(String str, @Cause Exception exc);

    @Message(id = Opcodes.FCMPL, value = "An exception occurred during message interpolation")
    ValidationException getExceptionOccurredDuringMessageInterpolationException(@Cause Exception exc);

    @Message(id = 150, value = "The constraint %1$s defines multiple validators for the type %2$s: %3$s, %4$s. Only one is allowed.")
    UnexpectedTypeException getMultipleValidatorsForSameTypeException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, @FormatWith(TypeFormatter.class) Type type, @FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> cls2, @FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> cls3);

    @Message(id = Opcodes.DCMPL, value = "A method overriding another method must not redefine the parameter constraint configuration, but method %2$s redefines the configuration of %1$s.")
    ConstraintDeclarationException getParameterConfigurationAlteredInSubTypeException(@FormatWith(ExecutableFormatter.class) Executable executable, @FormatWith(ExecutableFormatter.class) Executable executable2);

    @Message(id = 152, value = "Two methods defined in parallel types must not declare parameter constraints, if they are overridden by the same method, but methods %s and %s both define parameter constraints.")
    ConstraintDeclarationException getParameterConstraintsDefinedInMethodsFromParallelTypesException(@FormatWith(ExecutableFormatter.class) Executable executable, @FormatWith(ExecutableFormatter.class) Executable executable2);

    @Message(id = 153, value = "The constraint %1$s used ConstraintTarget#%2$s but is not specified on a method or constructor.")
    ConstraintDeclarationException getParametersOrReturnValueConstraintTargetGivenAtNonExecutableException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, ConstraintTarget constraintTarget);

    @Message(id = 154, value = "Cross parameter constraint %1$s has no cross-parameter validator.")
    ConstraintDefinitionException getCrossParameterConstraintHasNoValidatorException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = 155, value = "Composed and composing constraints must have the same constraint type, but composed constraint %1$s has type %3$s, while composing constraint %2$s has type %4$s.")
    ConstraintDefinitionException getComposedAndComposingConstraintsHaveDifferentTypesException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, @FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls2, ConstraintDescriptorImpl.ConstraintType constraintType, ConstraintDescriptorImpl.ConstraintType constraintType2);

    @Message(id = 156, value = "Constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s doesn't.")
    ConstraintDefinitionException getGenericAndCrossParameterConstraintDoesNotDefineValidationAppliesToParameterException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = 157, value = "Return type of the attribute validationAppliesTo() of the constraint %s must be javax.validation.ConstraintTarget.")
    ConstraintDefinitionException getValidationAppliesToParameterMustHaveReturnTypeConstraintTargetException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = 158, value = "Default value of the attribute validationAppliesTo() of the constraint %s must be ConstraintTarget#IMPLICIT.")
    ConstraintDefinitionException getValidationAppliesToParameterMustHaveDefaultValueImplicitException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.IF_ICMPEQ, value = "Only constraints with generic as well as cross-parameter validators must define an attribute validationAppliesTo(), but constraint %s does.")
    ConstraintDefinitionException getValidationAppliesToParameterMustNotBeDefinedForNonGenericAndCrossParameterConstraintException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = 160, value = "Validator for cross-parameter constraint %s does not validate Object nor Object[].")
    ConstraintDefinitionException getValidatorForCrossParameterConstraintMustEitherValidateObjectOrObjectArrayException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.IF_ICMPLT, value = "Two methods defined in parallel types must not define group conversions for a cascaded method return value, if they are overridden by the same method, but methods %s and %s both define parameter constraints.")
    ConstraintDeclarationException getMethodsFromParallelTypesMustNotDefineGroupConversionsForCascadedReturnValueException(@FormatWith(ExecutableFormatter.class) Executable executable, @FormatWith(ExecutableFormatter.class) Executable executable2);

    @Message(id = Opcodes.IF_ICMPGE, value = "The validated type %1$s does not specify the constructor/method: %2$s")
    IllegalArgumentException getMethodOrConstructorNotDefinedByValidatedTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = Opcodes.IF_ICMPGT, value = "The actual parameter type '%1$s' is not assignable to the expected one '%2$s' for parameter %3$d of '%4$s'")
    IllegalArgumentException getParameterTypesDoNotMatchException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, Type type, int i, @FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = Opcodes.IF_ICMPLE, value = "%s has to be a auto-boxed type.")
    IllegalArgumentException getHasToBeABoxedTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.IF_ACMPEQ, value = "Mixing IMPLICIT and other executable types is not allowed.")
    IllegalArgumentException getMixingImplicitWithOtherExecutableTypesException();

    @Message(id = Opcodes.IF_ACMPNE, value = "@ValidateOnExecution is not allowed on methods overriding a superclass method or implementing an interface. Check configuration for %1$s")
    ValidationException getValidateOnExecutionOnOverriddenOrInterfaceMethodException(@FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = 167, value = "A given constraint definition can only be overridden in one mapping file. %1$s is overridden in multiple files")
    ValidationException getOverridingConstraintDefinitionsInMultipleMappingFilesException(String str);

    @Message(id = 168, value = "The message descriptor '%1$s' contains an unbalanced meta character '%2$c' parameter.")
    MessageDescriptorFormatException getNonTerminatedParameterException(String str, char c);

    @Message(id = Opcodes.RET, value = "The message descriptor '%1$s' has nested parameters.")
    MessageDescriptorFormatException getNestedParameterException(String str);

    @Message(id = Opcodes.TABLESWITCH, value = "No JSR-223 scripting engine could be bootstrapped for language \"%s\".")
    ConstraintDeclarationException getCreationOfScriptExecutorFailedException(String str, @Cause Exception exc);

    @Message(id = Opcodes.LOOKUPSWITCH, value = "%s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getBeanClassHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.IRETURN, value = "Property \"%2$s\" of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getPropertyHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = Opcodes.LRETURN, value = "Method %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getMethodHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = Opcodes.FRETURN, value = "Parameter %3$s of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getParameterHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ExecutableFormatter.class) Executable executable, int i);

    @Message(id = Opcodes.DRETURN, value = "The return value of method or constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getReturnValueHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = 176, value = "Constructor %2$s of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getConstructorHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = Opcodes.RETURN, value = "Cross-parameter constraints for the method or constructor %2$s of type %1$s are declared more than once via the programmatic constraint declaration API.")
    ValidationException getCrossParameterElementHasAlreadyBeConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(ExecutableFormatter.class) Executable executable);

    @Message(id = Opcodes.GETSTATIC, value = "Multiplier cannot be negative: %d.")
    IllegalArgumentException getMultiplierCannotBeNegativeException(int i);

    @Message(id = Opcodes.PUTSTATIC, value = "Weight cannot be negative: %d.")
    IllegalArgumentException getWeightCannotBeNegativeException(int i);

    @Message(id = Opcodes.GETFIELD, value = "'%c' is not a digit nor a letter.")
    IllegalArgumentException getTreatCheckAsIsNotADigitNorALetterException(int i);

    @Message(id = Opcodes.PUTFIELD, value = "Wrong number of parameters. Method or constructor %1$s expects %2$d parameters, but got %3$d.")
    IllegalArgumentException getInvalidParameterCountForExecutableException(String str, int i, int i2);

    @Message(id = Opcodes.INVOKEVIRTUAL, value = "No validation value unwrapper is registered for type '%1$s'.")
    ValidationException getNoUnwrapperFoundForTypeException(Type type);

    @Message(id = Opcodes.INVOKESPECIAL, value = "Unable to initialize 'javax.el.ExpressionFactory'. Check that you have the EL dependencies on the classpath, or use ParameterMessageInterpolator instead")
    ValidationException getUnableToInitializeELExpressionFactoryException(@Cause Throwable th);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = Opcodes.INVOKEINTERFACE, value = "Message contains EL expression: %1s, which is not supported by the selected message interpolator")
    void warnElIsUnsupported(String str);

    @Message(id = Opcodes.ANEWARRAY, value = "The configuration of value unwrapping for property '%s' of bean '%s' is inconsistent between the field and its getter.")
    ConstraintDeclarationException getInconsistentValueUnwrappingConfigurationBetweenFieldAndItsGetterException(String str, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = Opcodes.ARRAYLENGTH, value = "Unable to parse %s.")
    ValidationException getUnableToCreateXMLEventReader(String str, @Cause Exception exc);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = Opcodes.CHECKCAST, value = "Couldn't determine Java version from value %1s; Not enabling features requiring Java 8")
    void unknownJvmVersion(String str);

    @Message(id = Opcodes.INSTANCEOF, value = "%s is configured more than once via the programmatic constraint definition API.")
    ValidationException getConstraintHasAlreadyBeenConfiguredViaProgrammaticApiException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = Opcodes.MONITORENTER, value = "An empty element is only supported when a CharSequence is expected.")
    ValidationException getEmptyElementOnlySupportedWhenCharSequenceIsExpectedExpection();

    @Message(id = Opcodes.MONITOREXIT, value = "Unable to reach the property to validate for the bean %s and the property path %s. A property is null along the way.")
    ValidationException getUnableToReachPropertyToValidateException(Object obj, Path path);

    @Message(id = 196, value = "Unable to convert the Type %s to a Class.")
    ValidationException getUnableToConvertTypeToClassException(Type type);

    @Message(id = Opcodes.MULTIANEWARRAY, value = "No value extractor found for type parameter '%2$s' of type %1$s.")
    ConstraintDeclarationException getNoValueExtractorFoundForTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, TypeVariable<?> typeVariable);

    @Message(id = Opcodes.IFNULL, value = "No suitable value extractor found for type %1$s.")
    ConstraintDeclarationException getNoValueExtractorFoundForUnwrapException(Type type);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 200, value = "Using %s as clock provider.")
    void usingClockProvider(@FormatWith(ClassObjectFormatter.class) Class<? extends ClockProvider> cls);

    @Message(id = 201, value = "Unable to instantiate clock provider class %s.")
    ValidationException getUnableToInstantiateClockProviderClassException(String str, @Cause ValidationException validationException);

    @Message(id = 202, value = "Unable to get the current time from the clock provider")
    ValidationException getUnableToGetCurrentTimeFromClockProvider(@Cause Exception exc);

    @Message(id = HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION, value = "Value extractor type %1s fails to declare the extracted type parameter using @ExtractedValue.")
    ValueExtractorDefinitionException getValueExtractorFailsToDeclareExtractedValueException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 204, value = "Only one type parameter must be marked with @ExtractedValue for value extractor type %1s.")
    ValueExtractorDefinitionException getValueExtractorDeclaresExtractedValueMultipleTimesException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = HttpServletResponse.SC_RESET_CONTENT, value = "Invalid unwrapping configuration for constraint %2$s on %1$s. You can only define one of 'Unwrapping.Skip' or 'Unwrapping.Unwrap'.")
    ConstraintDeclarationException getInvalidUnwrappingConfigurationForConstraintException(Member member, @FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls);

    @Message(id = HttpServletResponse.SC_PARTIAL_CONTENT, value = "Unable to instantiate value extractor class %s.")
    ValidationException getUnableToInstantiateValueExtractorClassException(String str, @Cause ValidationException validationException);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = WebdavStatus.SC_MULTI_STATUS, value = "Adding value extractor %s.")
    void addingValueExtractor(@FormatWith(ClassObjectFormatter.class) Class<? extends ValueExtractor<?>> cls);

    @Message(id = 208, value = "Given value extractor %2$s handles the same type and type use as previously given value extractor %1$s.")
    ValueExtractorDeclarationException getValueExtractorForTypeAndTypeUseAlreadyPresentException(ValueExtractor<?> valueExtractor, ValueExtractor<?> valueExtractor2);

    @Message(id = 209, value = "A composing constraint (%2$s) must not be given directly on the composed constraint (%1$s) and using the corresponding List annotation at the same time.")
    ConstraintDeclarationException getCannotMixDirectAnnotationAndListContainerOnComposedConstraintException(@FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls, @FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls2);

    @Message(id = 210, value = "Unable to find the type parameter %2$s in class %1$s.")
    IllegalArgumentException getUnableToFindTypeParameterInClass(@FormatWith(ClassObjectFormatter.class) Class<?> cls, Object obj);

    @Message(id = 211, value = "Given type is neither a parameterized nor an array type: %s.")
    ValidationException getTypeIsNotAParameterizedNorArrayTypeException(@FormatWith(TypeFormatter.class) Type type);

    @Message(id = 212, value = "Given type has no type argument with index %2$s: %1$s.")
    ValidationException getInvalidTypeArgumentIndexException(@FormatWith(TypeFormatter.class) Type type, int i);

    @Message(id = 213, value = "Given type has more than one type argument, hence an argument index must be specified: %s.")
    ValidationException getNoTypeArgumentIndexIsGivenForTypeWithMultipleTypeArgumentsException(@FormatWith(TypeFormatter.class) Type type);

    @Message(id = 214, value = "The same container element type of type %1$s is configured more than once via the programmatic constraint declaration API.")
    ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaProgrammaticApiException(@FormatWith(TypeFormatter.class) Type type);

    @Message(id = 215, value = "Calling parameter() is not allowed for the current element.")
    ValidationException getParameterIsNotAValidCallException();

    @Message(id = 216, value = "Calling returnValue() is not allowed for the current element.")
    ValidationException getReturnValueIsNotAValidCallException();

    @Message(id = 217, value = "The same container element type %2$s is configured more than once for location %1$s via the XML mapping configuration.")
    ValidationException getContainerElementTypeHasAlreadyBeenConfiguredViaXmlMappingConfigurationException(ConstraintLocation constraintLocation, ContainerElementTypePath containerElementTypePath);

    @Message(id = 218, value = "Having parallel definitions of value extractors on a given class is not allowed: %s.")
    ValueExtractorDefinitionException getParallelDefinitionsOfValueExtractorsException(@FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 219, value = "Unable to get the most specific value extractor for type %1$s as several most specific value extractors are declared: %2$s.")
    ConstraintDeclarationException getUnableToGetMostSpecificValueExtractorDueToSeveralMaximallySpecificValueExtractorsDeclaredException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(CollectionOfClassesObjectFormatter.class) Collection<Class<? extends ValueExtractor>> collection);

    @Message(id = 220, value = "When @ExtractedValue is defined on a type parameter of a container type, the type attribute may not be set: %1$s.")
    ValueExtractorDefinitionException getExtractedValueOnTypeParameterOfContainerTypeMayNotDefineTypeAttributeException(@FormatWith(ClassObjectFormatter.class) Class<? extends ValueExtractor> cls);

    @Message(id = 221, value = "An error occurred while extracting values in value extractor %1$s.")
    ValidationException getErrorWhileExtractingValuesInValueExtractorException(@FormatWith(ClassObjectFormatter.class) Class<? extends ValueExtractor> cls, @Cause Exception exc);

    @Message(id = 222, value = "The same value extractor %s is added more than once via the XML configuration.")
    ValueExtractorDeclarationException getDuplicateDefinitionsOfValueExtractorException(String str);

    @Message(id = 223, value = "Implicit unwrapping is not allowed for type %1$s as several maximally specific value extractors marked with @UnwrapByDefault are declared: %2$s.")
    ConstraintDeclarationException getImplicitUnwrappingNotAllowedWhenSeveralMaximallySpecificValueExtractorsMarkedWithUnwrapByDefaultDeclaredException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @FormatWith(CollectionOfClassesObjectFormatter.class) Collection<Class<? extends ValueExtractor>> collection);

    @Message(id = 224, value = "Unwrapping of ConstraintDescriptor is not supported yet.")
    ValidationException getUnwrappingOfConstraintDescriptorNotSupportedYetException();

    @Message(id = 225, value = "Only unbound wildcard type arguments are supported for the container type of the value extractor: %1$s.")
    ValueExtractorDefinitionException getOnlyUnboundWildcardTypeArgumentsSupportedForContainerTypeOfValueExtractorException(@FormatWith(ClassObjectFormatter.class) Class<? extends ValueExtractor> cls);

    @Message(id = 226, value = "Container element constraints and cascading validation are not supported on arrays: %1$s")
    ValidationException getContainerElementConstraintsAndCascadedValidationNotSupportedOnArraysException(@FormatWith(TypeFormatter.class) Type type);

    @Message(id = 227, value = "The validated type %1$s does not specify the property: %2$s")
    IllegalArgumentException getPropertyNotDefinedByValidatedTypeException(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = 228, value = "No value extractor found when narrowing down to the runtime type %3$s among the value extractors for type parameter '%2$s' of type %1$s.")
    ConstraintDeclarationException getNoValueExtractorFoundForTypeException(@FormatWith(TypeFormatter.class) Type type, TypeVariable<?> typeVariable, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @Message(id = 229, value = "Unable to cast %1$s to %2$s.")
    ClassCastException getUnableToCastException(Object obj, @FormatWith(ClassObjectFormatter.class) Class<?> cls);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 230, value = "Using %s as script evaluator factory.")
    void usingScriptEvaluatorFactory(@FormatWith(ClassObjectFormatter.class) Class<? extends ScriptEvaluatorFactory> cls);

    @Message(id = 231, value = "Unable to instantiate script evaluator factory class %s.")
    ValidationException getUnableToInstantiateScriptEvaluatorFactoryClassException(String str, @Cause Exception exc);

    @Message(id = 232, value = "No JSR 223 script engine found for language \"%s\".")
    ScriptEvaluatorNotFoundException getUnableToFindScriptEngineException(String str);

    @Message(id = 233, value = "An error occurred while executing the script: \"%s\".")
    ScriptEvaluationException getErrorExecutingScriptException(String str, @Cause Exception exc);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 234, value = "Using %1$s as ValidatorFactory-scoped %2$s.")
    void logValidatorFactoryScopedConfiguration(@FormatWith(ClassObjectFormatter.class) Class<?> cls, String str);

    @Message(id = 235, value = "Unable to create an annotation descriptor for %1$s.")
    ValidationException getUnableToCreateAnnotationDescriptor(@FormatWith(ClassObjectFormatter.class) Class<?> cls, @Cause Throwable th);

    @Message(id = 236, value = "Unable to find the method required to create the constraint annotation descriptor.")
    ValidationException getUnableToFindAnnotationDefDeclaredMethods(@Cause Exception exc);

    @Message(id = 237, value = "Unable to access method %3$s of class %2$s with parameters %4$s using lookup %1$s.")
    ValidationException getUnableToAccessMethodException(MethodHandles.Lookup lookup, @FormatWith(ClassObjectFormatter.class) Class<?> cls, String str, @FormatWith(ObjectArrayFormatter.class) Object[] objArr, @Cause Throwable th);

    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 238, value = "Temporal validation tolerance set to %1$s.")
    void logTemporalValidationTolerance(@FormatWith(DurationFormatter.class) Duration duration);

    @Message(id = 239, value = "Unable to parse the temporal validation tolerance property %s. It should be a duration represented in milliseconds.")
    ValidationException getUnableToParseTemporalValidationToleranceException(String str, @Cause Exception exc);

    @LogMessage(level = Logger.Level.DEBUG)
    @Message(id = 240, value = "Constraint validator payload set to %1$s.")
    void logConstraintValidatorPayload(Object obj);

    @Message(id = 241, value = "Encountered unsupported element %1$s while parsing the XML configuration.")
    ValidationException logUnknownElementInXmlConfiguration(String str);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 242, value = "Unable to load or instantiate JPA aware resolver %1$s. All properties will per default be traversable.")
    void logUnableToLoadOrInstantiateJPAAwareResolver(String str);

    @Message(id = 243, value = "Constraint %2$s references constraint validator type %1$s, but this validator is defined for constraint type %3$s.")
    ConstraintDefinitionException getConstraintValidatorDefinitionConstraintMismatchException(@FormatWith(ClassObjectFormatter.class) Class<? extends ConstraintValidator<?, ?>> cls, @FormatWith(ClassObjectFormatter.class) Class<? extends Annotation> cls2, @FormatWith(TypeFormatter.class) Type type);

    @Message(id = 248, value = "Unable to get an XML schema named %s.")
    ValidationException unableToGetXmlSchema(String str);
}