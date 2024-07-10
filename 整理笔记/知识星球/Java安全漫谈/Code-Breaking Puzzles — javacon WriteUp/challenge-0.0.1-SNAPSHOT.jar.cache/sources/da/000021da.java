package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/assembler/MethodExclusionMBeanInfoAssembler.class */
public class MethodExclusionMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler {
    @Nullable
    private Set<String> ignoredMethods;
    @Nullable
    private Map<String, Set<String>> ignoredMethodMappings;

    public void setIgnoredMethods(String... ignoredMethodNames) {
        this.ignoredMethods = new HashSet(Arrays.asList(ignoredMethodNames));
    }

    public void setIgnoredMethodMappings(Properties mappings) {
        this.ignoredMethodMappings = new HashMap();
        Enumeration<?> en = mappings.keys();
        while (en.hasMoreElements()) {
            String beanKey = (String) en.nextElement();
            String[] methodNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
            this.ignoredMethodMappings.put(beanKey, new HashSet(Arrays.asList(methodNames)));
        }
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return isNotIgnored(method, beanKey);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return isNotIgnored(method, beanKey);
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeOperation(Method method, String beanKey) {
        return isNotIgnored(method, beanKey);
    }

    protected boolean isNotIgnored(Method method, String beanKey) {
        Set<String> methodNames;
        return (this.ignoredMethodMappings == null || (methodNames = this.ignoredMethodMappings.get(beanKey)) == null) ? this.ignoredMethods == null || !this.ignoredMethods.contains(method.getName()) : !methodNames.contains(method.getName());
    }
}