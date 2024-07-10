package org.hibernate.validator.internal.engine.messageinterpolation.el;

import java.beans.FeatureDescriptor;
import java.util.IllegalFormatException;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import org.hibernate.validator.internal.engine.messageinterpolation.FormatterWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/el/RootResolver.class */
public class RootResolver extends ELResolver {
    public static final String FORMATTER = "formatter";
    private static final String FORMAT = "format";

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        throw new PropertyNotWritableException();
    }

    @Override // javax.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        if (!(base instanceof FormatterWrapper)) {
            return null;
        }
        return evaluateFormatExpression(context, method, params);
    }

    private Object evaluateFormatExpression(ELContext context, Object method, Object[] params) {
        if (!FORMAT.equals(method)) {
            throw new ELException("Wrong method name 'formatter#" + method + "' does not exist. Only formatter#format is supported.");
        }
        if (params.length == 0) {
            throw new ELException("Invalid number of arguments to Formatter#format");
        }
        if (!(params[0] instanceof String)) {
            throw new ELException("The first argument to Formatter#format must be String");
        }
        FormatterWrapper formatterWrapper = (FormatterWrapper) context.getVariableMapper().resolveVariable(FORMATTER).getValue(context);
        Object[] formattingParameters = new Object[params.length - 1];
        System.arraycopy(params, 1, formattingParameters, 0, params.length - 1);
        try {
            Object returnValue = formatterWrapper.format((String) params[0], formattingParameters);
            context.setPropertyResolved(true);
            return returnValue;
        } catch (IllegalFormatException e) {
            throw new ELException("Error in Formatter#format call", e);
        }
    }
}