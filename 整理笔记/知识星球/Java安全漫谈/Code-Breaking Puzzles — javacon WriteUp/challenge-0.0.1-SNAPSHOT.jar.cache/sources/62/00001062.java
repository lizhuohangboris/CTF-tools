package org.hibernate.validator.internal.engine;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.engine.HibernateConstraintViolation;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/ConstraintViolationImpl.class */
public class ConstraintViolationImpl<T> implements HibernateConstraintViolation<T>, Serializable {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final long serialVersionUID = -4970067626703103139L;
    private final String interpolatedMessage;
    private final T rootBean;
    private final Object value;
    private final Path propertyPath;
    private final Object leafBeanInstance;
    private final ConstraintDescriptor<?> constraintDescriptor;
    private final String messageTemplate;
    private final Map<String, Object> messageParameters;
    private final Map<String, Object> expressionVariables;
    private final Class<T> rootBeanClass;
    private final ElementType elementType;
    private final Object[] executableParameters;
    private final Object executableReturnValue;
    private final Object dynamicPayload;
    private final int hashCode = createHashCode();

    public static <T> ConstraintViolation<T> forBeanValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object dynamicPayload) {
        return new ConstraintViolationImpl(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, null, null, dynamicPayload);
    }

    public static <T> ConstraintViolation<T> forParameterValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object[] executableParameters, Object dynamicPayload) {
        return new ConstraintViolationImpl(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, executableParameters, null, dynamicPayload);
    }

    public static <T> ConstraintViolation<T> forReturnValueValidation(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object executableReturnValue, Object dynamicPayload) {
        return new ConstraintViolationImpl(messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean, leafBeanInstance, value, propertyPath, constraintDescriptor, elementType, null, executableReturnValue, dynamicPayload);
    }

    private ConstraintViolationImpl(String messageTemplate, Map<String, Object> messageParameters, Map<String, Object> expressionVariables, String interpolatedMessage, Class<T> rootBeanClass, T rootBean, Object leafBeanInstance, Object value, Path propertyPath, ConstraintDescriptor<?> constraintDescriptor, ElementType elementType, Object[] executableParameters, Object executableReturnValue, Object dynamicPayload) {
        this.messageTemplate = messageTemplate;
        this.messageParameters = messageParameters;
        this.expressionVariables = expressionVariables;
        this.interpolatedMessage = interpolatedMessage;
        this.rootBean = rootBean;
        this.value = value;
        this.propertyPath = propertyPath;
        this.leafBeanInstance = leafBeanInstance;
        this.constraintDescriptor = constraintDescriptor;
        this.rootBeanClass = rootBeanClass;
        this.elementType = elementType;
        this.executableParameters = executableParameters;
        this.executableReturnValue = executableReturnValue;
        this.dynamicPayload = dynamicPayload;
    }

    @Override // javax.validation.ConstraintViolation
    public final String getMessage() {
        return this.interpolatedMessage;
    }

    @Override // javax.validation.ConstraintViolation
    public final String getMessageTemplate() {
        return this.messageTemplate;
    }

    public Map<String, Object> getMessageParameters() {
        return this.messageParameters;
    }

    public Map<String, Object> getExpressionVariables() {
        return this.expressionVariables;
    }

    @Override // javax.validation.ConstraintViolation
    public final T getRootBean() {
        return this.rootBean;
    }

    @Override // javax.validation.ConstraintViolation
    public final Class<T> getRootBeanClass() {
        return this.rootBeanClass;
    }

    @Override // javax.validation.ConstraintViolation
    public final Object getLeafBean() {
        return this.leafBeanInstance;
    }

    @Override // javax.validation.ConstraintViolation
    public final Object getInvalidValue() {
        return this.value;
    }

    @Override // javax.validation.ConstraintViolation
    public final Path getPropertyPath() {
        return this.propertyPath;
    }

    @Override // javax.validation.ConstraintViolation
    public final ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    @Override // javax.validation.ConstraintViolation
    public <C> C unwrap(Class<C> type) {
        if (type.isAssignableFrom(ConstraintViolation.class)) {
            return type.cast(this);
        }
        if (type.isAssignableFrom(HibernateConstraintViolation.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    @Override // javax.validation.ConstraintViolation
    public Object[] getExecutableParameters() {
        return this.executableParameters;
    }

    @Override // javax.validation.ConstraintViolation
    public Object getExecutableReturnValue() {
        return this.executableReturnValue;
    }

    @Override // org.hibernate.validator.engine.HibernateConstraintViolation
    public <C> C getDynamicPayload(Class<C> type) {
        if (this.dynamicPayload != null && type.isAssignableFrom(this.dynamicPayload.getClass())) {
            return type.cast(this.dynamicPayload);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConstraintViolationImpl<?> that = (ConstraintViolationImpl) o;
        if (this.interpolatedMessage != null) {
            if (!this.interpolatedMessage.equals(that.interpolatedMessage)) {
                return false;
            }
        } else if (that.interpolatedMessage != null) {
            return false;
        }
        if (this.messageTemplate != null) {
            if (!this.messageTemplate.equals(that.messageTemplate)) {
                return false;
            }
        } else if (that.messageTemplate != null) {
            return false;
        }
        if (this.propertyPath != null) {
            if (!this.propertyPath.equals(that.propertyPath)) {
                return false;
            }
        } else if (that.propertyPath != null) {
            return false;
        }
        if (this.rootBean != null) {
            if (this.rootBean != that.rootBean) {
                return false;
            }
        } else if (that.rootBean != null) {
            return false;
        }
        if (this.leafBeanInstance != null) {
            if (this.leafBeanInstance != that.leafBeanInstance) {
                return false;
            }
        } else if (that.leafBeanInstance != null) {
            return false;
        }
        if (this.value != null) {
            if (this.value != that.value) {
                return false;
            }
        } else if (that.value != null) {
            return false;
        }
        if (this.constraintDescriptor != null) {
            if (!this.constraintDescriptor.equals(that.constraintDescriptor)) {
                return false;
            }
        } else if (that.constraintDescriptor != null) {
            return false;
        }
        if (this.elementType != null) {
            if (!this.elementType.equals(that.elementType)) {
                return false;
            }
            return true;
        } else if (that.elementType != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintViolationImpl");
        sb.append("{interpolatedMessage='").append(this.interpolatedMessage).append('\'');
        sb.append(", propertyPath=").append(this.propertyPath);
        sb.append(", rootBeanClass=").append(this.rootBeanClass);
        sb.append(", messageTemplate='").append(this.messageTemplate).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private int createHashCode() {
        int result = this.interpolatedMessage != null ? this.interpolatedMessage.hashCode() : 0;
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + (this.propertyPath != null ? this.propertyPath.hashCode() : 0))) + System.identityHashCode(this.rootBean))) + System.identityHashCode(this.leafBeanInstance))) + System.identityHashCode(this.value))) + (this.constraintDescriptor != null ? this.constraintDescriptor.hashCode() : 0))) + (this.messageTemplate != null ? this.messageTemplate.hashCode() : 0))) + (this.elementType != null ? this.elementType.hashCode() : 0);
    }
}