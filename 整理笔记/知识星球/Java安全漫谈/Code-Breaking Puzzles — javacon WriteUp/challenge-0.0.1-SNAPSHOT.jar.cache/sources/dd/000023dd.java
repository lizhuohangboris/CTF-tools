package org.springframework.validation.beanvalidation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter.class */
public class SpringValidatorAdapter implements SmartValidator, Validator {
    private static final Set<String> internalAnnotationAttributes = new HashSet(4);
    @Nullable
    private Validator targetValidator;

    static {
        internalAnnotationAttributes.add(ConstraintHelper.MESSAGE);
        internalAnnotationAttributes.add(ConstraintHelper.GROUPS);
        internalAnnotationAttributes.add(ConstraintHelper.PAYLOAD);
    }

    public SpringValidatorAdapter(Validator targetValidator) {
        Assert.notNull(targetValidator, "Target Validator must not be null");
        this.targetValidator = targetValidator;
    }

    public SpringValidatorAdapter() {
    }

    public void setTargetValidator(Validator targetValidator) {
        this.targetValidator = targetValidator;
    }

    @Override // org.springframework.validation.Validator
    public boolean supports(Class<?> clazz) {
        return this.targetValidator != null;
    }

    @Override // org.springframework.validation.Validator
    public void validate(Object target, Errors errors) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validate(target, new Class[0]), errors);
        }
    }

    @Override // org.springframework.validation.SmartValidator
    public void validate(Object target, Errors errors, Object... validationHints) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validate(target, asValidationGroups(validationHints)), errors);
        }
    }

    @Override // org.springframework.validation.SmartValidator
    public void validateValue(Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object... validationHints) {
        if (this.targetValidator != null) {
            processConstraintViolations(this.targetValidator.validateValue(targetType, fieldName, value, asValidationGroups(validationHints)), errors);
        }
    }

    private Class<?>[] asValidationGroups(Object... validationHints) {
        Set<Class<?>> groups = new LinkedHashSet<>(4);
        for (Object hint : validationHints) {
            if (hint instanceof Class) {
                groups.add((Class) hint);
            }
        }
        return ClassUtils.toClassArray(groups);
    }

    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
                    if (errors instanceof BindingResult) {
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if (nestedField.isEmpty()) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            ObjectError error = new ObjectError(errors.getObjectName(), errorCodes, errorArgs, violation.getMessage());
                            error.wrap(violation);
                            bindingResult.addError(error);
                        } else {
                            Object rejectedValue = getRejectedValue(field, violation, bindingResult);
                            String[] errorCodes2 = bindingResult.resolveMessageCodes(errorCode, field);
                            FieldError error2 = new FieldError(errors.getObjectName(), nestedField, rejectedValue, false, errorCodes2, errorArgs, violation.getMessage());
                            error2.wrap(violation);
                            bindingResult.addError(error2);
                        }
                    } else {
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                } catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field + "' does not have a corresponding accessor for Spring data binding - check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

    protected String determineField(ConstraintViolation<Object> violation) {
        Path path = violation.getPropertyPath();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Path.Node node : path) {
            if (node.isInIterable()) {
                sb.append('[');
                Object index = node.getIndex();
                if (index == null) {
                    index = node.getKey();
                }
                if (index != null) {
                    sb.append(index);
                }
                sb.append(']');
            }
            String name = node.getName();
            if (name != null && node.getKind() == ElementKind.PROPERTY && !name.startsWith("<")) {
                if (!first) {
                    sb.append('.');
                }
                first = false;
                sb.append(name);
            }
        }
        return sb.toString();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.annotation.Annotation] */
    protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
        return descriptor.getAnnotation().annotationType().getSimpleName();
    }

    protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
        List<Object> arguments = new ArrayList<>();
        arguments.add(getResolvableField(objectName, field));
        Map<String, Object> attributesToExpose = new TreeMap<>();
        descriptor.getAttributes().forEach(attributeName, attributeValue -> {
            if (!internalAnnotationAttributes.contains(attributeName)) {
                if (attributeValue instanceof String) {
                    attributeValue = new ResolvableAttribute(attributeValue.toString());
                }
                attributesToExpose.put(attributeName, attributeValue);
            }
        });
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray();
    }

    protected MessageSourceResolvable getResolvableField(String objectName, String field) {
        String[] codes = {objectName + "." + field, field};
        return new DefaultMessageSourceResolvable(codes, field);
    }

    @Nullable
    protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        if (!"".equals(field) && !field.contains(ClassUtils.ARRAY_SUFFIX) && (invalidValue == violation.getLeafBean() || field.contains(PropertyAccessor.PROPERTY_KEY_PREFIX) || field.contains("."))) {
            invalidValue = bindingResult.getRawFieldValue(field);
        }
        return invalidValue;
    }

    @Override // javax.validation.Validator
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validate(object, groups);
    }

    @Override // javax.validation.Validator
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validateProperty(object, propertyName, groups);
    }

    @Override // javax.validation.Validator
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.validateValue(beanType, propertyName, value, groups);
    }

    @Override // javax.validation.Validator
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.getConstraintsForClass(clazz);
    }

    public <T> T unwrap(@Nullable Class<T> type) {
        Assert.state(this.targetValidator != null, "No target Validator set");
        try {
            return type != null ? (T) this.targetValidator.unwrap(type) : (T) this.targetValidator;
        } catch (ValidationException ex) {
            if (Validator.class == type) {
                return (T) this.targetValidator;
            }
            throw ex;
        }
    }

    @Override // javax.validation.Validator
    public ExecutableValidator forExecutables() {
        Assert.state(this.targetValidator != null, "No target Validator set");
        return this.targetValidator.forExecutables();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/SpringValidatorAdapter$ResolvableAttribute.class */
    public static class ResolvableAttribute implements MessageSourceResolvable, Serializable {
        private final String resolvableString;

        public ResolvableAttribute(String resolvableString) {
            this.resolvableString = resolvableString;
        }

        @Override // org.springframework.context.MessageSourceResolvable
        public String[] getCodes() {
            return new String[]{this.resolvableString};
        }

        @Override // org.springframework.context.MessageSourceResolvable
        @Nullable
        public Object[] getArguments() {
            return null;
        }

        @Override // org.springframework.context.MessageSourceResolvable
        public String getDefaultMessage() {
            return this.resolvableString;
        }
    }
}