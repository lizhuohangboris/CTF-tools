package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/InjectionMetadata.class */
public class InjectionMetadata {
    private static final Log logger = LogFactory.getLog(InjectionMetadata.class);
    private final Class<?> targetClass;
    private final Collection<InjectedElement> injectedElements;
    @Nullable
    private volatile Set<InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        Set<InjectedElement> checkedElements = new LinkedHashSet<>(this.injectedElements.size());
        for (InjectedElement element : this.injectedElements) {
            Member member = element.getMember();
            if (!beanDefinition.isExternallyManagedConfigMember(member)) {
                beanDefinition.registerExternallyManagedConfigMember(member);
                checkedElements.add(element);
                if (logger.isTraceEnabled()) {
                    logger.trace("Registered injected element on class [" + this.targetClass.getName() + "]: " + element);
                }
            }
        }
        this.checkedElements = checkedElements;
    }

    public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Processing injected element of bean '" + beanName + "': " + element);
                }
                element.inject(target, beanName, pvs);
            }
        }
    }

    public void clear(@Nullable PropertyValues pvs) {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.clearPropertySkipping(pvs);
            }
        }
    }

    public static boolean needsRefresh(@Nullable InjectionMetadata metadata, Class<?> clazz) {
        return metadata == null || metadata.targetClass != clazz;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/InjectionMetadata$InjectedElement.class */
    public static abstract class InjectedElement {
        protected final Member member;
        protected final boolean isField;
        @Nullable
        protected final PropertyDescriptor pd;
        @Nullable
        protected volatile Boolean skip;

        /* JADX INFO: Access modifiers changed from: protected */
        public InjectedElement(Member member, @Nullable PropertyDescriptor pd) {
            this.member = member;
            this.isField = member instanceof Field;
            this.pd = pd;
        }

        public final Member getMember() {
            return this.member;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final Class<?> getResourceType() {
            if (this.isField) {
                return ((Field) this.member).getType();
            }
            if (this.pd != null) {
                return this.pd.getPropertyType();
            }
            return ((Method) this.member).getParameterTypes()[0];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final void checkResourceType(Class<?> resourceType) {
            if (this.isField) {
                Class<?> fieldType = ((Field) this.member).getType();
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
                return;
            }
            Class<?> paramType = this.pd != null ? this.pd.getPropertyType() : ((Method) this.member).getParameterTypes()[0];
            if (!resourceType.isAssignableFrom(paramType) && !paramType.isAssignableFrom(resourceType)) {
                throw new IllegalStateException("Specified parameter type [" + paramType + "] is incompatible with resource type [" + resourceType.getName() + "]");
            }
        }

        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) throws Throwable {
            if (this.isField) {
                Field field = (Field) this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, getResourceToInject(target, requestingBeanName));
            } else if (checkPropertySkipping(pvs)) {
            } else {
                try {
                    Method method = (Method) this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, getResourceToInject(target, requestingBeanName));
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean checkPropertySkipping(@Nullable PropertyValues pvs) {
            Boolean skip = this.skip;
            if (skip != null) {
                return skip.booleanValue();
            }
            if (pvs == null) {
                this.skip = false;
                return false;
            }
            synchronized (pvs) {
                Boolean skip2 = this.skip;
                if (skip2 != null) {
                    return skip2.booleanValue();
                }
                if (this.pd != null) {
                    if (pvs.contains(this.pd.getName())) {
                        this.skip = true;
                        return true;
                    } else if (pvs instanceof MutablePropertyValues) {
                        ((MutablePropertyValues) pvs).registerProcessedProperty(this.pd.getName());
                    }
                }
                this.skip = false;
                return false;
            }
        }

        protected void clearPropertySkipping(@Nullable PropertyValues pvs) {
            if (pvs == null) {
                return;
            }
            synchronized (pvs) {
                if (Boolean.FALSE.equals(this.skip) && this.pd != null && (pvs instanceof MutablePropertyValues)) {
                    ((MutablePropertyValues) pvs).clearProcessedProperty(this.pd.getName());
                }
            }
        }

        @Nullable
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return null;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InjectedElement)) {
                return false;
            }
            InjectedElement otherElement = (InjectedElement) other;
            return this.member.equals(otherElement.member);
        }

        public int hashCode() {
            return (this.member.getClass().hashCode() * 29) + this.member.getName().hashCode();
        }

        public String toString() {
            return getClass().getSimpleName() + " for " + this.member;
        }
    }
}