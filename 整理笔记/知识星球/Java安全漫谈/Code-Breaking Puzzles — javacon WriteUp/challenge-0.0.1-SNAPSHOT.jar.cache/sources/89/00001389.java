package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/GenericTypeAwarePropertyDescriptor.class */
public final class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor {
    private final Class<?> beanClass;
    @Nullable
    private final Method readMethod;
    @Nullable
    private final Method writeMethod;
    @Nullable
    private volatile Set<Method> ambiguousWriteMethods;
    @Nullable
    private MethodParameter writeMethodParameter;
    @Nullable
    private Class<?> propertyType;
    private final Class<?> propertyEditorClass;

    public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, Class<?> propertyEditorClass) throws IntrospectionException {
        super(propertyName, (Method) null, (Method) null);
        Method[] methods;
        Method candidate;
        this.beanClass = beanClass;
        Method readMethodToUse = readMethod != null ? BridgeMethodResolver.findBridgedMethod(readMethod) : null;
        Method writeMethodToUse = writeMethod != null ? BridgeMethodResolver.findBridgedMethod(writeMethod) : null;
        if (writeMethodToUse == null && readMethodToUse != null && (candidate = ClassUtils.getMethodIfAvailable(this.beanClass, "set" + StringUtils.capitalize(getName()), null)) != null && candidate.getParameterCount() == 1) {
            writeMethodToUse = candidate;
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;
        if (this.writeMethod != null) {
            if (this.readMethod == null) {
                Set<Method> ambiguousCandidates = new HashSet<>();
                for (Method method : beanClass.getMethods()) {
                    if (method.getName().equals(writeMethodToUse.getName()) && !method.equals(writeMethodToUse) && !method.isBridge() && method.getParameterCount() == writeMethodToUse.getParameterCount()) {
                        ambiguousCandidates.add(method);
                    }
                }
                if (!ambiguousCandidates.isEmpty()) {
                    this.ambiguousWriteMethods = ambiguousCandidates;
                }
            }
            this.writeMethodParameter = new MethodParameter(this.writeMethod, 0);
            GenericTypeResolver.resolveParameterType(this.writeMethodParameter, this.beanClass);
        }
        if (this.readMethod != null) {
            this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
        } else if (this.writeMethodParameter != null) {
            this.propertyType = this.writeMethodParameter.getParameterType();
        }
        this.propertyEditorClass = propertyEditorClass;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Nullable
    public Method getReadMethod() {
        return this.readMethod;
    }

    @Nullable
    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Method getWriteMethodForActualAccess() {
        Assert.state(this.writeMethod != null, "No write method available");
        Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
        if (ambiguousCandidates != null) {
            this.ambiguousWriteMethods = null;
            LogFactory.getLog(GenericTypeAwarePropertyDescriptor.class).warn("Invalid JavaBean property '" + getName() + "' being accessed! Ambiguous write methods found next to actually used [" + this.writeMethod + "]: " + ambiguousCandidates);
        }
        return this.writeMethod;
    }

    public MethodParameter getWriteMethodParameter() {
        Assert.state(this.writeMethodParameter != null, "No write method available");
        return this.writeMethodParameter;
    }

    @Nullable
    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericTypeAwarePropertyDescriptor)) {
            return false;
        }
        GenericTypeAwarePropertyDescriptor otherPd = (GenericTypeAwarePropertyDescriptor) other;
        return getBeanClass().equals(otherPd.getBeanClass()) && PropertyDescriptorUtils.equals(this, otherPd);
    }

    public int hashCode() {
        int hashCode = getBeanClass().hashCode();
        return (29 * ((29 * hashCode) + ObjectUtils.nullSafeHashCode(getReadMethod()))) + ObjectUtils.nullSafeHashCode(getWriteMethod());
    }
}