package javax.el;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ExpressionFactory.class */
public abstract class ExpressionFactory {
    private static final boolean IS_SECURITY_ENABLED;
    private static final String SERVICE_RESOURCE_NAME = "META-INF/services/javax.el.ExpressionFactory";
    private static final String PROPERTY_NAME = "javax.el.ExpressionFactory";
    private static final String PROPERTY_FILE;
    private static final CacheValue nullTcclFactory;
    private static final Map<CacheKey, CacheValue> factoryCache;

    public abstract ValueExpression createValueExpression(ELContext eLContext, String str, Class<?> cls);

    public abstract ValueExpression createValueExpression(Object obj, Class<?> cls);

    public abstract MethodExpression createMethodExpression(ELContext eLContext, String str, Class<?> cls, Class<?>[] clsArr);

    public abstract Object coerceToType(Object obj, Class<?> cls);

    static /* synthetic */ String access$000() {
        return getClassNameJreDir();
    }

    static /* synthetic */ String access$100() {
        return getClassNameSysProp();
    }

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap();
        if (IS_SECURITY_ENABLED) {
            PROPERTY_FILE = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.el.ExpressionFactory.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties";
                }
            });
        } else {
            PROPERTY_FILE = System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties";
        }
    }

    public static ExpressionFactory newInstance() {
        return newInstance(null);
    }

    public static ExpressionFactory newInstance(Properties properties) {
        CacheValue cacheValue;
        ExpressionFactory result;
        ClassLoader tccl = Util.getContextClassLoader();
        if (tccl == null) {
            cacheValue = nullTcclFactory;
        } else {
            CacheKey key = new CacheKey(tccl);
            cacheValue = factoryCache.get(key);
            if (cacheValue == null) {
                CacheValue newCacheValue = new CacheValue();
                cacheValue = factoryCache.putIfAbsent(key, newCacheValue);
                if (cacheValue == null) {
                    cacheValue = newCacheValue;
                }
            }
        }
        Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            Class<?> clazz = cacheValue.getFactoryClass();
            readLock.unlock();
            if (clazz == null) {
                String className = null;
                try {
                    Lock writeLock = cacheValue.getLock().writeLock();
                    writeLock.lock();
                    className = cacheValue.getFactoryClassName();
                    if (className == null) {
                        className = discoverClassName(tccl);
                        cacheValue.setFactoryClassName(className);
                    }
                    if (tccl == null) {
                        clazz = Class.forName(className);
                    } else {
                        clazz = tccl.loadClass(className);
                    }
                    cacheValue.setFactoryClass(clazz);
                    writeLock.unlock();
                } catch (ClassNotFoundException e) {
                    throw new ELException("Unable to find ExpressionFactory of type: " + className, e);
                }
            }
            Constructor<?> constructor = null;
            if (properties != null) {
                try {
                    try {
                        constructor = clazz.getConstructor(Properties.class);
                    } catch (IllegalArgumentException | ReflectiveOperationException e2) {
                        throw new ELException("Unable to create ExpressionFactory of type: " + clazz.getName(), e2);
                    } catch (InvocationTargetException e3) {
                        Throwable cause = e3.getCause();
                        Util.handleThrowable(cause);
                        throw new ELException("Unable to create ExpressionFactory of type: " + clazz.getName(), e3);
                    }
                } catch (NoSuchMethodException e4) {
                } catch (SecurityException se) {
                    throw new ELException(se);
                }
            }
            if (constructor == null) {
                result = (ExpressionFactory) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                result = (ExpressionFactory) constructor.newInstance(properties);
            }
            return result;
        } catch (Throwable th) {
            readLock.unlock();
            throw th;
        }
    }

    public ELResolver getStreamELResolver() {
        return null;
    }

    public Map<String, Method> getInitFunctionMap() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ExpressionFactory$CacheKey.class */
    public static class CacheKey {
        private final int hash;
        private final WeakReference<ClassLoader> ref;

        public CacheKey(ClassLoader cl) {
            this.hash = cl.hashCode();
            this.ref = new WeakReference<>(cl);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            ClassLoader thisCl;
            if (obj == this) {
                return true;
            }
            return (obj instanceof CacheKey) && (thisCl = this.ref.get()) != null && thisCl == ((CacheKey) obj).ref.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ExpressionFactory$CacheValue.class */
    public static class CacheValue {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String className;
        private WeakReference<Class<?>> ref;

        public ReadWriteLock getLock() {
            return this.lock;
        }

        public String getFactoryClassName() {
            return this.className;
        }

        public void setFactoryClassName(String className) {
            this.className = className;
        }

        public Class<?> getFactoryClass() {
            if (this.ref != null) {
                return this.ref.get();
            }
            return null;
        }

        public void setFactoryClass(Class<?> clazz) {
            this.ref = new WeakReference<>(clazz);
        }
    }

    private static String discoverClassName(ClassLoader tccl) {
        String className = getClassNameServices(tccl);
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.el.ExpressionFactory.2
                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.security.PrivilegedAction
                    public String run() {
                        return ExpressionFactory.access$000();
                    }
                });
            } else {
                className = getClassNameJreDir();
            }
        }
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.el.ExpressionFactory.3
                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.security.PrivilegedAction
                    public String run() {
                        return ExpressionFactory.access$100();
                    }
                });
            } else {
                className = getClassNameSysProp();
            }
        }
        if (className == null) {
            className = "org.apache.el.ExpressionFactoryImpl";
        }
        return className;
    }

    private static String getClassNameServices(ClassLoader tccl) {
        InputStream is = tccl == null ? ClassLoader.getSystemResourceAsStream(SERVICE_RESOURCE_NAME) : tccl.getResourceAsStream(SERVICE_RESOURCE_NAME);
        if (is != null) {
            try {
                try {
                    InputStreamReader isr = new InputStreamReader(is, UriEscape.DEFAULT_ENCODING);
                    Throwable th = null;
                    try {
                        BufferedReader br = new BufferedReader(isr);
                        Throwable th2 = null;
                        try {
                            String line = br.readLine();
                            if (line != null && line.trim().length() > 0) {
                                String trim = line.trim();
                                if (br != null) {
                                    if (0 != 0) {
                                        try {
                                            br.close();
                                        } catch (Throwable th3) {
                                            th2.addSuppressed(th3);
                                        }
                                    } else {
                                        br.close();
                                    }
                                }
                                if (isr != null) {
                                    if (0 != 0) {
                                        try {
                                            isr.close();
                                        } catch (Throwable th4) {
                                            th.addSuppressed(th4);
                                        }
                                    } else {
                                        isr.close();
                                    }
                                }
                                return trim;
                            }
                            if (br != null) {
                                if (0 != 0) {
                                    try {
                                        br.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    br.close();
                                }
                            }
                            if (isr != null) {
                                if (0 != 0) {
                                    try {
                                        isr.close();
                                    } catch (Throwable th6) {
                                        th.addSuppressed(th6);
                                    }
                                } else {
                                    isr.close();
                                }
                            }
                            try {
                                is.close();
                                return null;
                            } catch (IOException e) {
                                return null;
                            }
                        } catch (Throwable th7) {
                            try {
                                throw th7;
                            } catch (Throwable th8) {
                                if (br != null) {
                                    if (th7 != null) {
                                        try {
                                            br.close();
                                        } catch (Throwable th9) {
                                            th7.addSuppressed(th9);
                                        }
                                    } else {
                                        br.close();
                                    }
                                }
                                throw th8;
                            }
                        }
                    } catch (Throwable th10) {
                        try {
                            throw th10;
                        } catch (Throwable th11) {
                            if (isr != null) {
                                if (th10 != null) {
                                    try {
                                        isr.close();
                                    } catch (Throwable th12) {
                                        th10.addSuppressed(th12);
                                    }
                                } else {
                                    isr.close();
                                }
                            }
                            throw th11;
                        }
                    }
                } catch (UnsupportedEncodingException e2) {
                    try {
                        is.close();
                        return null;
                    } catch (IOException e3) {
                        return null;
                    }
                } catch (IOException e4) {
                    throw new ELException("Failed to read META-INF/services/javax.el.ExpressionFactory", e4);
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e5) {
                }
            }
        }
        return null;
    }

    private static String getClassNameJreDir() {
        File file = new File(PROPERTY_FILE);
        if (file.canRead()) {
            try {
                InputStream is = new FileInputStream(file);
                Throwable th = null;
                try {
                    Properties props = new Properties();
                    props.load(is);
                    String value = props.getProperty(PROPERTY_NAME);
                    if (value == null || value.trim().length() <= 0) {
                        if (is != null) {
                            if (0 != 0) {
                                try {
                                    is.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                is.close();
                            }
                        }
                        return null;
                    }
                    String trim = value.trim();
                    if (is != null) {
                        if (0 != 0) {
                            try {
                                is.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            is.close();
                        }
                    }
                    return trim;
                } catch (Throwable th4) {
                    try {
                        throw th4;
                    } catch (Throwable th5) {
                        if (is != null) {
                            if (th4 != null) {
                                try {
                                    is.close();
                                } catch (Throwable th6) {
                                    th4.addSuppressed(th6);
                                }
                            } else {
                                is.close();
                            }
                        }
                        throw th5;
                    }
                }
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e2) {
                throw new ELException("Failed to read " + PROPERTY_FILE, e2);
            }
        }
        return null;
    }

    private static final String getClassNameSysProp() {
        String value = System.getProperty(PROPERTY_NAME);
        if (value != null && value.trim().length() > 0) {
            return value.trim();
        }
        return null;
    }
}