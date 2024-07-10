package org.apache.tomcat.util.modeler;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import org.apache.tomcat.util.buf.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/modeler/ManagedBean.class */
public class ManagedBean implements Serializable {
    private static final long serialVersionUID = 1;
    private static final String BASE_MBEAN = "org.apache.tomcat.util.modeler.BaseModelMBean";
    static final Class<?>[] NO_ARGS_PARAM_SIG = new Class[0];
    private final ReadWriteLock mBeanInfoLock = new ReentrantReadWriteLock();
    private volatile transient MBeanInfo info = null;
    private Map<String, AttributeInfo> attributes = new HashMap();
    private Map<String, OperationInfo> operations = new HashMap();
    protected String className = BASE_MBEAN;
    protected String description = null;
    protected String domain = null;
    protected String group = null;
    protected String name = null;
    private NotificationInfo[] notifications = new NotificationInfo[0];
    protected String type = null;

    public ManagedBean() {
        AttributeInfo ai = new AttributeInfo();
        ai.setName("modelerType");
        ai.setDescription("Type of the modeled resource. Can be set only once");
        ai.setType("java.lang.String");
        ai.setWriteable(false);
        addAttribute(ai);
    }

    public AttributeInfo[] getAttributes() {
        AttributeInfo[] result = new AttributeInfo[this.attributes.size()];
        this.attributes.values().toArray(result);
        return result;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.className = className;
            this.info = null;
        } finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.description = description;
            this.info = null;
        } finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.name = name;
            this.info = null;
        } finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public NotificationInfo[] getNotifications() {
        return this.notifications;
    }

    public OperationInfo[] getOperations() {
        OperationInfo[] result = new OperationInfo[this.operations.size()];
        this.operations.values().toArray(result);
        return result;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.type = type;
            this.info = null;
        } finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public void addAttribute(AttributeInfo attribute) {
        this.attributes.put(attribute.getName(), attribute);
    }

    public void addNotification(NotificationInfo notification) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            NotificationInfo[] results = new NotificationInfo[this.notifications.length + 1];
            System.arraycopy(this.notifications, 0, results, 0, this.notifications.length);
            results[this.notifications.length] = notification;
            this.notifications = results;
            this.info = null;
        } finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public void addOperation(OperationInfo operation) {
        this.operations.put(createOperationKey(operation), operation);
    }

    public DynamicMBean createMBean(Object instance) throws InstanceNotFoundException, MBeanException, RuntimeOperationsException {
        BaseModelMBean mbean;
        if (getClassName().equals(BASE_MBEAN)) {
            mbean = new BaseModelMBean();
        } else {
            Class<?> clazz = null;
            Exception ex = null;
            try {
                clazz = Class.forName(getClassName());
            } catch (Exception e) {
            }
            if (clazz == null) {
                try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) {
                        clazz = cl.loadClass(getClassName());
                    }
                } catch (Exception e2) {
                    ex = e2;
                }
            }
            if (clazz == null) {
                throw new MBeanException(ex, "Cannot load ModelMBean class " + getClassName());
            }
            try {
                mbean = (BaseModelMBean) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e3) {
                throw new MBeanException(e3, "Cannot instantiate ModelMBean of class " + getClassName());
            } catch (RuntimeOperationsException e4) {
                throw e4;
            }
        }
        mbean.setManagedBean(this);
        if (instance != null) {
            try {
                mbean.setManagedResource(instance, "ObjectReference");
            } catch (InstanceNotFoundException e5) {
                throw e5;
            }
        }
        return mbean;
    }

    public MBeanInfo getMBeanInfo() {
        this.mBeanInfoLock.readLock().lock();
        try {
            if (this.info != null) {
                return this.info;
            }
            this.mBeanInfoLock.writeLock().lock();
            try {
                if (this.info == null) {
                    AttributeInfo[] attrs = getAttributes();
                    MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[attrs.length];
                    for (int i = 0; i < attrs.length; i++) {
                        attributes[i] = attrs[i].createAttributeInfo();
                    }
                    OperationInfo[] opers = getOperations();
                    MBeanOperationInfo[] operations = new MBeanOperationInfo[opers.length];
                    for (int i2 = 0; i2 < opers.length; i2++) {
                        operations[i2] = opers[i2].createOperationInfo();
                    }
                    NotificationInfo[] notifs = getNotifications();
                    MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[notifs.length];
                    for (int i3 = 0; i3 < notifs.length; i3++) {
                        notifications[i3] = notifs[i3].createNotificationInfo();
                    }
                    this.info = new MBeanInfo(getClassName(), getDescription(), attributes, new MBeanConstructorInfo[0], operations, notifications);
                }
                MBeanInfo mBeanInfo = this.info;
                this.mBeanInfoLock.writeLock().unlock();
                return mBeanInfo;
            } catch (Throwable th) {
                this.mBeanInfoLock.writeLock().unlock();
                throw th;
            }
        } finally {
            this.mBeanInfoLock.readLock().unlock();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ManagedBean[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", className=");
        sb.append(this.className);
        sb.append(", description=");
        sb.append(this.description);
        if (this.group != null) {
            sb.append(", group=");
            sb.append(this.group);
        }
        sb.append(", type=");
        sb.append(this.type);
        sb.append("]");
        return sb.toString();
    }

    public Method getGetter(String aname, BaseModelMBean mbean, Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(" Cannot find attribute " + aname + " for " + resource);
        }
        String getMethod = attrInfo.getGetMethod();
        NoSuchMethodException exception = null;
        try {
            m = mbean.getClass().getMethod(getMethod, NO_ARGS_PARAM_SIG);
        } catch (NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                m = resource.getClass().getMethod(getMethod, NO_ARGS_PARAM_SIG);
                exception = null;
            } catch (NoSuchMethodException e2) {
                exception = e2;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, "Cannot find getter method " + getMethod);
        }
        return m;
    }

    public Method getSetter(String aname, BaseModelMBean bean, Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(" Cannot find attribute " + aname);
        }
        String setMethod = attrInfo.getSetMethod();
        String argType = attrInfo.getType();
        Class<?>[] signature = {BaseModelMBean.getAttributeClass(argType)};
        NoSuchMethodException exception = null;
        try {
            m = bean.getClass().getMethod(setMethod, signature);
        } catch (NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                m = resource.getClass().getMethod(setMethod, signature);
                exception = null;
            } catch (NoSuchMethodException e2) {
                exception = e2;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, "Cannot find setter method " + setMethod + " " + resource);
        }
        return m;
    }

    public Method getInvoke(String aname, Object[] params, String[] signature, BaseModelMBean bean, Object resource) throws MBeanException, ReflectionException {
        Method method = null;
        if (params == null) {
            params = new Object[0];
        }
        if (signature == null) {
            signature = new String[0];
        }
        if (params.length != signature.length) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Inconsistent arguments and signature"), "Inconsistent arguments and signature");
        }
        OperationInfo opInfo = this.operations.get(createOperationKey(aname, signature));
        if (opInfo == null) {
            throw new MBeanException(new ServiceNotFoundException("Cannot find operation " + aname), "Cannot find operation " + aname);
        }
        Class<?>[] types = new Class[signature.length];
        for (int i = 0; i < signature.length; i++) {
            types[i] = BaseModelMBean.getAttributeClass(signature[i]);
        }
        Exception exception = null;
        try {
            method = bean.getClass().getMethod(aname, types);
        } catch (NoSuchMethodException e) {
            exception = e;
        }
        if (method == null && resource != null) {
            try {
                method = resource.getClass().getMethod(aname, types);
            } catch (NoSuchMethodException e2) {
                exception = e2;
            }
        }
        if (method == null) {
            throw new ReflectionException(exception, "Cannot find method " + aname + " with this signature");
        }
        return method;
    }

    private String createOperationKey(OperationInfo operation) {
        StringBuilder key = new StringBuilder(operation.getName());
        key.append('(');
        StringUtils.join((Object[]) operation.getSignature(), ',', x -> {
            return x.getType();
        }, key);
        key.append(')');
        return key.toString();
    }

    private String createOperationKey(String methodName, String[] parameterTypes) {
        StringBuilder key = new StringBuilder(methodName);
        key.append('(');
        StringUtils.join(parameterTypes, ',', key);
        key.append(')');
        return key.toString();
    }
}