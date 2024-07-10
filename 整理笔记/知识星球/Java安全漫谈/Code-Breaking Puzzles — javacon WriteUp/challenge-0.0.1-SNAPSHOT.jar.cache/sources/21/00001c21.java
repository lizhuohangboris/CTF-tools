package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.RejectModifierPredicate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/proxy/MixinEverythingEmitter.class */
class MixinEverythingEmitter extends MixinEmitter {
    public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes) {
        super(v, className, classes, null);
    }

    @Override // org.springframework.cglib.proxy.MixinEmitter
    protected Class[] getInterfaces(Class[] classes) {
        List list = new ArrayList();
        for (Class cls : classes) {
            ReflectUtils.addAllInterfaces(cls, list);
        }
        return (Class[]) list.toArray(new Class[list.size()]);
    }

    @Override // org.springframework.cglib.proxy.MixinEmitter
    protected Method[] getMethods(Class type) {
        List methods = new ArrayList(Arrays.asList(type.getMethods()));
        CollectionUtils.filter(methods, new RejectModifierPredicate(24));
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }
}