package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cglib.core.ReflectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/proxy/CallbackHelper.class */
public abstract class CallbackHelper implements CallbackFilter {
    private Map methodMap = new HashMap();
    private List callbacks = new ArrayList();

    protected abstract Object getCallback(Method method);

    public CallbackHelper(Class superclass, Class[] interfaces) {
        List methods = new ArrayList();
        Enhancer.getMethods(superclass, interfaces, methods);
        Map indexes = new HashMap();
        int size = methods.size();
        for (int i = 0; i < size; i++) {
            Method method = (Method) methods.get(i);
            Object callback = getCallback(method);
            if (callback == null) {
                throw new IllegalStateException("getCallback cannot return null");
            }
            boolean isCallback = callback instanceof Callback;
            if (!isCallback && !(callback instanceof Class)) {
                throw new IllegalStateException("getCallback must return a Callback or a Class");
            }
            if (i > 0 && ((this.callbacks.get(i - 1) instanceof Callback) ^ isCallback)) {
                throw new IllegalStateException("getCallback must return a Callback or a Class consistently for every Method");
            }
            Integer index = (Integer) indexes.get(callback);
            if (index == null) {
                index = new Integer(this.callbacks.size());
                indexes.put(callback, index);
            }
            this.methodMap.put(method, index);
            this.callbacks.add(callback);
        }
    }

    public Callback[] getCallbacks() {
        if (this.callbacks.size() == 0) {
            return new Callback[0];
        }
        if (this.callbacks.get(0) instanceof Callback) {
            return (Callback[]) this.callbacks.toArray(new Callback[this.callbacks.size()]);
        }
        throw new IllegalStateException("getCallback returned classes, not callbacks; call getCallbackTypes instead");
    }

    public Class[] getCallbackTypes() {
        if (this.callbacks.size() == 0) {
            return new Class[0];
        }
        if (this.callbacks.get(0) instanceof Callback) {
            return ReflectUtils.getClasses(getCallbacks());
        }
        return (Class[]) this.callbacks.toArray(new Class[this.callbacks.size()]);
    }

    @Override // org.springframework.cglib.proxy.CallbackFilter
    public int accept(Method method) {
        return ((Integer) this.methodMap.get(method)).intValue();
    }

    public int hashCode() {
        return this.methodMap.hashCode();
    }

    @Override // org.springframework.cglib.proxy.CallbackFilter
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CallbackHelper)) {
            return false;
        }
        return this.methodMap.equals(((CallbackHelper) o).methodMap);
    }
}