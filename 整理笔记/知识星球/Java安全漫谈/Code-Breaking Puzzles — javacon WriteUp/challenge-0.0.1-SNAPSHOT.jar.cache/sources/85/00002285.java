package org.springframework.remoting.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteInvocation.class */
public class RemoteInvocation implements Serializable {
    private static final long serialVersionUID = 6876024250231820554L;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private Map<String, Serializable> attributes;

    public RemoteInvocation(MethodInvocation methodInvocation) {
        this.methodName = methodInvocation.getMethod().getName();
        this.parameterTypes = methodInvocation.getMethod().getParameterTypes();
        this.arguments = methodInvocation.getArguments();
    }

    public RemoteInvocation(String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }

    public RemoteInvocation() {
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public void addAttribute(String key, Serializable value) throws IllegalStateException {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        if (this.attributes.containsKey(key)) {
            throw new IllegalStateException("There is already an attribute with key '" + key + "' bound");
        }
        this.attributes.put(key, value);
    }

    @Nullable
    public Serializable getAttribute(String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }

    public void setAttributes(@Nullable Map<String, Serializable> attributes) {
        this.attributes = attributes;
    }

    @Nullable
    public Map<String, Serializable> getAttributes() {
        return this.attributes;
    }

    public Object invoke(Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = targetObject.getClass().getMethod(this.methodName, this.parameterTypes);
        return method.invoke(targetObject, this.arguments);
    }

    public String toString() {
        return "RemoteInvocation: method name '" + this.methodName + "'; parameter types " + ClassUtils.classNamesToString(this.parameterTypes);
    }
}