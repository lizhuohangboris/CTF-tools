package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Objects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/BeanNameELResolver.class */
public class BeanNameELResolver extends ELResolver {
    private final BeanNameResolver beanNameResolver;

    public BeanNameELResolver(BeanNameResolver beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String) property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            try {
                Object result = this.beanNameResolver.getBean(beanName);
                context.setPropertyResolved(base, property);
                return result;
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return;
        }
        String beanName = (String) property;
        boolean isResolved = context.isPropertyResolved();
        try {
            boolean isReadOnly = isReadOnly(context, base, property);
            context.setPropertyResolved(isResolved);
            if (isReadOnly) {
                throw new PropertyNotWritableException(Util.message(context, "beanNameELResolver.beanReadOnly", beanName));
            }
            if (this.beanNameResolver.isNameResolved(beanName) || this.beanNameResolver.canCreateBean(beanName)) {
                try {
                    this.beanNameResolver.setBeanValue(beanName, value);
                    context.setPropertyResolved(base, property);
                } finally {
                    Util.handleThrowable(t);
                    ELException eLException = new ELException(t);
                }
            }
        } catch (Throwable t) {
            try {
                throw new ELException(t);
            } catch (Throwable th) {
                context.setPropertyResolved(isResolved);
                throw th;
            }
        }
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return null;
        }
        String beanName = (String) property;
        try {
            if (this.beanNameResolver.isNameResolved(beanName)) {
                Class<?> result = this.beanNameResolver.getBean(beanName).getClass();
                context.setPropertyResolved(base, property);
                return result;
            }
            return null;
        } catch (Throwable t) {
            Util.handleThrowable(t);
            throw new ELException(t);
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base != null || !(property instanceof String)) {
            return false;
        }
        String beanName = (String) property;
        if (this.beanNameResolver.isNameResolved(beanName)) {
            try {
                boolean result = this.beanNameResolver.isReadOnly(beanName);
                context.setPropertyResolved(base, property);
                return result;
            } catch (Throwable t) {
                Util.handleThrowable(t);
                throw new ELException(t);
            }
        }
        return false;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return String.class;
    }
}