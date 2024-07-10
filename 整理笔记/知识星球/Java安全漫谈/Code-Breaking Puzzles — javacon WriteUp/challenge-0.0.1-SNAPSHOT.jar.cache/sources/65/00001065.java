package org.hibernate.validator.internal.engine;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/MessageInterpolatorContext.class */
public class MessageInterpolatorContext implements HibernateMessageInterpolatorContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ConstraintDescriptor<?> constraintDescriptor;
    private final Object validatedValue;
    private final Class<?> rootBeanType;
    private final Map<String, Object> messageParameters;
    private final Map<String, Object> expressionVariables;

    public MessageInterpolatorContext(ConstraintDescriptor<?> constraintDescriptor, Object validatedValue, Class<?> rootBeanType, Map<String, Object> messageParameters, Map<String, Object> expressionVariables) {
        this.constraintDescriptor = constraintDescriptor;
        this.validatedValue = validatedValue;
        this.rootBeanType = rootBeanType;
        this.messageParameters = CollectionHelper.toImmutableMap(messageParameters);
        this.expressionVariables = CollectionHelper.toImmutableMap(expressionVariables);
    }

    @Override // javax.validation.MessageInterpolator.Context
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    @Override // javax.validation.MessageInterpolator.Context
    public Object getValidatedValue() {
        return this.validatedValue;
    }

    @Override // org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext
    public Class<?> getRootBeanType() {
        return this.rootBeanType;
    }

    @Override // org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext
    public Map<String, Object> getMessageParameters() {
        return this.messageParameters;
    }

    @Override // org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext
    public Map<String, Object> getExpressionVariables() {
        return this.expressionVariables;
    }

    @Override // javax.validation.MessageInterpolator.Context
    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(HibernateMessageInterpolatorContext.class)) {
            return type.cast(this);
        }
        throw LOG.getTypeNotSupportedForUnwrappingException(type);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageInterpolatorContext that = (MessageInterpolatorContext) o;
        if (this.constraintDescriptor != null) {
            if (!this.constraintDescriptor.equals(that.constraintDescriptor)) {
                return false;
            }
        } else if (that.constraintDescriptor != null) {
            return false;
        }
        if (this.rootBeanType != null) {
            if (!this.rootBeanType.equals(that.rootBeanType)) {
                return false;
            }
        } else if (that.rootBeanType != null) {
            return false;
        }
        if (this.validatedValue != null) {
            if (this.validatedValue != that.validatedValue) {
                return false;
            }
            return true;
        } else if (that.validatedValue != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        int result = this.constraintDescriptor != null ? this.constraintDescriptor.hashCode() : 0;
        return (31 * ((31 * result) + System.identityHashCode(this.validatedValue))) + (this.rootBeanType != null ? this.rootBeanType.hashCode() : 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageInterpolatorContext");
        sb.append("{constraintDescriptor=").append(this.constraintDescriptor);
        sb.append(", validatedValue=").append(this.validatedValue);
        sb.append(", messageParameters=").append(this.messageParameters);
        sb.append(", expressionVariables=").append(this.expressionVariables);
        sb.append('}');
        return sb.toString();
    }
}