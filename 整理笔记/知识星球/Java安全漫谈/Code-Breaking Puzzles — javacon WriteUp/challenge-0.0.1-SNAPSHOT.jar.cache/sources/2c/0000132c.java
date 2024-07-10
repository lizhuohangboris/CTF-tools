package org.springframework.aop.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.PatternMatchUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/NameMatchMethodPointcut.class */
public class NameMatchMethodPointcut extends StaticMethodMatcherPointcut implements Serializable {
    private List<String> mappedNames = new ArrayList();

    public void setMappedName(String mappedName) {
        setMappedNames(mappedName);
    }

    public void setMappedNames(String... mappedNames) {
        this.mappedNames = new ArrayList(Arrays.asList(mappedNames));
    }

    public NameMatchMethodPointcut addMethodName(String name) {
        this.mappedNames.add(name);
        return this;
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x0013  */
    @Override // org.springframework.aop.MethodMatcher
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean matches(java.lang.reflect.Method r5, java.lang.Class<?> r6) {
        /*
            r4 = this;
            r0 = r4
            java.util.List<java.lang.String> r0 = r0.mappedNames
            java.util.Iterator r0 = r0.iterator()
            r7 = r0
        La:
            r0 = r7
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L3c
            r0 = r7
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r8 = r0
            r0 = r8
            r1 = r5
            java.lang.String r1 = r1.getName()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L37
            r0 = r4
            r1 = r5
            java.lang.String r1 = r1.getName()
            r2 = r8
            boolean r0 = r0.isMatch(r1, r2)
            if (r0 == 0) goto L39
        L37:
            r0 = 1
            return r0
        L39:
            goto La
        L3c:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.aop.support.NameMatchMethodPointcut.matches(java.lang.reflect.Method, java.lang.Class):boolean");
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof NameMatchMethodPointcut) && this.mappedNames.equals(((NameMatchMethodPointcut) other).mappedNames));
    }

    public int hashCode() {
        return this.mappedNames.hashCode();
    }
}