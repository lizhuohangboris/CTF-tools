package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/BeanWrapperImpl.class */
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {
    @Nullable
    private CachedIntrospectionResults cachedIntrospectionResults;
    @Nullable
    private AccessControlContext acc;

    public BeanWrapperImpl() {
        this(true);
    }

    public BeanWrapperImpl(boolean registerDefaultEditors) {
        super(registerDefaultEditors);
    }

    public BeanWrapperImpl(Object object) {
        super(object);
    }

    public BeanWrapperImpl(Class<?> clazz) {
        super(clazz);
    }

    public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
        super(object, nestedPath, rootObject);
    }

    private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
        super(object, nestedPath, (AbstractNestablePropertyAccessor) parent);
        setSecurityContext(parent.acc);
    }

    public void setBeanInstance(Object object) {
        this.wrappedObject = object;
        this.rootObject = object;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
        setIntrospectionClass(object.getClass());
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        super.setWrappedInstance(object, nestedPath, rootObject);
        setIntrospectionClass(getWrappedClass());
    }

    protected void setIntrospectionClass(Class<?> clazz) {
        if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
            this.cachedIntrospectionResults = null;
        }
    }

    private CachedIntrospectionResults getCachedIntrospectionResults() {
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }

    public void setSecurityContext(@Nullable AccessControlContext acc) {
        this.acc = acc;
    }

    @Nullable
    public AccessControlContext getSecurityContext() {
        return this.acc;
    }

    @Nullable
    public Object convertForProperty(@Nullable Object value, String propertyName) throws TypeMismatchException {
        CachedIntrospectionResults cachedIntrospectionResults = getCachedIntrospectionResults();
        PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
        if (td == null) {
            td = cachedIntrospectionResults.addTypeDescriptor(pd, new TypeDescriptor(property(pd)));
        }
        return convertForProperty(propertyName, null, value, td);
    }

    public Property property(PropertyDescriptor pd) {
        GenericTypeAwarePropertyDescriptor gpd = (GenericTypeAwarePropertyDescriptor) pd;
        return new Property(gpd.getBeanClass(), gpd.getReadMethod(), gpd.getWriteMethod(), gpd.getName());
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    @Nullable
    public BeanPropertyHandler getLocalPropertyHandler(String propertyName) {
        PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        if (pd != null) {
            return new BeanPropertyHandler(pd);
        }
        return null;
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    public BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }

    @Override // org.springframework.beans.AbstractNestablePropertyAccessor
    protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
        PropertyMatches matches = PropertyMatches.forProperty(propertyName, getRootClass());
        throw new NotWritablePropertyException(getRootClass(), getNestedPath() + propertyName, matches.buildErrorMessage(), matches.getPossibleMatches());
    }

    @Override // org.springframework.beans.BeanWrapper
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getCachedIntrospectionResults().getPropertyDescriptors();
    }

    @Override // org.springframework.beans.BeanWrapper
    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
        BeanWrapperImpl nestedBw = (BeanWrapperImpl) getPropertyAccessorForPropertyPath(propertyName);
        String finalPath = getFinalPath(nestedBw, propertyName);
        PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(finalPath);
        if (pd == null) {
            throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName, "No property '" + propertyName + "' found");
        }
        return pd;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/BeanWrapperImpl$BeanPropertyHandler.class */
    public class BeanPropertyHandler extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final PropertyDescriptor pd;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BeanPropertyHandler(PropertyDescriptor pd) {
            super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
            BeanWrapperImpl.this = r6;
            this.pd = pd;
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public ResolvableType getResolvableType() {
            return ResolvableType.forMethodReturnType(this.pd.getReadMethod());
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public TypeDescriptor toTypeDescriptor() {
            return new TypeDescriptor(BeanWrapperImpl.this.property(this.pd));
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public TypeDescriptor nested(int level) {
            return TypeDescriptor.nested(BeanWrapperImpl.this.property(this.pd), level);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        @Nullable
        public Object getValue() throws Exception {
            Method readMethod = this.pd.getReadMethod();
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(readMethod);
                    return null;
                });
                try {
                    return AccessController.doPrivileged(() -> {
                        return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), null);
                    }, BeanWrapperImpl.this.acc);
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            ReflectionUtils.makeAccessible(readMethod);
            return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), null);
        }

        @Override // org.springframework.beans.AbstractNestablePropertyAccessor.PropertyHandler
        public void setValue(@Nullable Object value) throws Exception {
            Method writeMethod;
            if (this.pd instanceof GenericTypeAwarePropertyDescriptor) {
                writeMethod = ((GenericTypeAwarePropertyDescriptor) this.pd).getWriteMethodForActualAccess();
            } else {
                writeMethod = this.pd.getWriteMethod();
            }
            Method writeMethod2 = writeMethod;
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(writeMethod2);
                    return null;
                });
                try {
                    AccessController.doPrivileged(() -> {
                        return writeMethod2.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
                    }, BeanWrapperImpl.this.acc);
                    return;
                } catch (PrivilegedActionException ex) {
                    throw ex.getException();
                }
            }
            ReflectionUtils.makeAccessible(writeMethod2);
            writeMethod2.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
        }
    }
}