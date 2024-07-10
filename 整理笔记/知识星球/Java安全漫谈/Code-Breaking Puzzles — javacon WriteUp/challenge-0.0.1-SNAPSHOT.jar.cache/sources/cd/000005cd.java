package javax.el;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/BeanELResolver.class */
public class BeanELResolver extends ELResolver {
    private static final int CACHE_SIZE;
    private static final String CACHE_SIZE_PROP = "org.apache.el.BeanELResolver.CACHE_SIZE";
    private final boolean readOnly;
    private final ConcurrentCache<String, BeanProperties> cache;

    static {
        String cacheSizeStr;
        if (System.getSecurityManager() == null) {
            cacheSizeStr = System.getProperty(CACHE_SIZE_PROP, "1000");
        } else {
            cacheSizeStr = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.el.BeanELResolver.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty(BeanELResolver.CACHE_SIZE_PROP, "1000");
                }
            });
        }
        CACHE_SIZE = Integer.parseInt(cacheSizeStr);
    }

    public BeanELResolver() {
        this.cache = new ConcurrentCache<>(CACHE_SIZE);
        this.readOnly = false;
    }

    public BeanELResolver(boolean readOnly) {
        this.cache = new ConcurrentCache<>(CACHE_SIZE);
        this.readOnly = readOnly;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return null;
        }
        context.setPropertyResolved(base, property);
        return property(context, base, property).getPropertyType();
    }

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return null;
        }
        context.setPropertyResolved(base, property);
        Method m = property(context, base, property).read(context);
        try {
            return m.invoke(base, null);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(Util.message(context, "propertyReadError", base.getClass().getName(), property.toString()), cause);
        } catch (Exception e2) {
            throw new ELException(e2);
        }
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return;
        }
        context.setPropertyResolved(base, property);
        if (this.readOnly) {
            throw new PropertyNotWritableException(Util.message(context, "resolverNotWriteable", base.getClass().getName()));
        }
        Method m = property(context, base, property).write(context);
        try {
            m.invoke(base, value);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(Util.message(context, "propertyWriteError", base.getClass().getName(), property.toString()), cause);
        } catch (Exception e2) {
            throw new ELException(e2);
        }
    }

    @Override // javax.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Objects.requireNonNull(context);
        if (base == null || method == null) {
            return null;
        }
        ExpressionFactory factory = ELManager.getExpressionFactory();
        String methodName = (String) factory.coerceToType(method, String.class);
        Method matchingMethod = Util.findMethod(base.getClass(), methodName, paramTypes, params);
        Object[] parameters = Util.buildParameters(matchingMethod.getParameterTypes(), matchingMethod.isVarArgs(), params);
        try {
            Object result = matchingMethod.invoke(base, parameters);
            context.setPropertyResolved(base, method);
            return result;
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ELException(e);
        } catch (InvocationTargetException e2) {
            Throwable cause = e2.getCause();
            Util.handleThrowable(cause);
            throw new ELException(cause);
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null || property == null) {
            return false;
        }
        context.setPropertyResolved(base, property);
        return this.readOnly || property(context, base, property).isReadOnly();
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base == null) {
            return null;
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(base.getClass());
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (int i = 0; i < pds.length; i++) {
                pds[i].setValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME, Boolean.TRUE);
                pds[i].setValue("type", pds[i].getPropertyType());
            }
            return Arrays.asList((FeatureDescriptor[]) pds).iterator();
        } catch (IntrospectionException e) {
            return null;
        }
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base != null) {
            return Object.class;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/BeanELResolver$BeanProperties.class */
    public static final class BeanProperties {
        private final Map<String, BeanProperty> properties = new HashMap();
        private final Class<?> type;

        public BeanProperties(Class<?> type) throws ELException {
            this.type = type;
            try {
                BeanInfo info = Introspector.getBeanInfo(this.type);
                PropertyDescriptor[] pds = info.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    this.properties.put(pd.getName(), new BeanProperty(type, pd));
                }
                if (System.getSecurityManager() != null) {
                    populateFromInterfaces(type);
                }
            } catch (IntrospectionException ie) {
                throw new ELException((Throwable) ie);
            }
        }

        private void populateFromInterfaces(Class<?> aClass) throws IntrospectionException {
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces.length > 0) {
                for (Class<?> ifs : interfaces) {
                    BeanInfo info = Introspector.getBeanInfo(ifs);
                    PropertyDescriptor[] pds = info.getPropertyDescriptors();
                    for (PropertyDescriptor pd : pds) {
                        if (!this.properties.containsKey(pd.getName())) {
                            this.properties.put(pd.getName(), new BeanProperty(this.type, pd));
                        }
                    }
                    populateFromInterfaces(ifs);
                }
            }
            Class<?> superclass = aClass.getSuperclass();
            if (superclass != null) {
                populateFromInterfaces(superclass);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public BeanProperty get(ELContext ctx, String name) {
            BeanProperty property = this.properties.get(name);
            if (property == null) {
                throw new PropertyNotFoundException(Util.message(ctx, "propertyNotFound", this.type.getName(), name));
            }
            return property;
        }

        public BeanProperty getBeanProperty(String name) {
            return get(null, name);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Class<?> getType() {
            return this.type;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/BeanELResolver$BeanProperty.class */
    public static final class BeanProperty {
        private final Class<?> type;
        private final Class<?> owner;
        private final PropertyDescriptor descriptor;
        private Method read;
        private Method write;

        public BeanProperty(Class<?> owner, PropertyDescriptor descriptor) {
            this.owner = owner;
            this.descriptor = descriptor;
            this.type = descriptor.getPropertyType();
        }

        public Class getPropertyType() {
            return this.type;
        }

        public boolean isReadOnly() {
            if (this.write == null) {
                Method method = Util.getMethod(this.owner, this.descriptor.getWriteMethod());
                this.write = method;
                if (null == method) {
                    return true;
                }
            }
            return false;
        }

        public Method getWriteMethod() {
            return write(null);
        }

        public Method getReadMethod() {
            return read(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Method write(ELContext ctx) {
            if (this.write == null) {
                this.write = Util.getMethod(this.owner, this.descriptor.getWriteMethod());
                if (this.write == null) {
                    throw new PropertyNotWritableException(Util.message(ctx, "propertyNotWritable", this.owner.getName(), this.descriptor.getName()));
                }
            }
            return this.write;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Method read(ELContext ctx) {
            if (this.read == null) {
                this.read = Util.getMethod(this.owner, this.descriptor.getReadMethod());
                if (this.read == null) {
                    throw new PropertyNotFoundException(Util.message(ctx, "propertyNotReadable", this.owner.getName(), this.descriptor.getName()));
                }
            }
            return this.read;
        }
    }

    private final BeanProperty property(ELContext ctx, Object base, Object property) {
        Class<?> type = base.getClass();
        String prop = property.toString();
        BeanProperties props = this.cache.get(type.getName());
        if (props == null || type != props.getType()) {
            props = new BeanProperties(type);
            this.cache.put(type.getName(), props);
        }
        return props.get(ctx, prop);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/BeanELResolver$ConcurrentCache.class */
    public static final class ConcurrentCache<K, V> {
        private final int size;
        private final Map<K, V> eden;
        private final Map<K, V> longterm;

        public ConcurrentCache(int size) {
            this.size = size;
            this.eden = new ConcurrentHashMap(size);
            this.longterm = new WeakHashMap(size);
        }

        public V get(K key) {
            V value = this.eden.get(key);
            if (value == null) {
                synchronized (this.longterm) {
                    value = this.longterm.get(key);
                }
                if (value != null) {
                    this.eden.put(key, value);
                }
            }
            return value;
        }

        public void put(K key, V value) {
            if (this.eden.size() >= this.size) {
                synchronized (this.longterm) {
                    this.longterm.putAll(this.eden);
                }
                this.eden.clear();
            }
            this.eden.put(key, value);
        }
    }
}