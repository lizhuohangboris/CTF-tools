package org.apache.naming.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.naming.ResourceRef;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/factory/BeanFactory.class */
public class BeanFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {
        String setterName;
        if (obj instanceof ResourceRef) {
            try {
                Reference ref = (Reference) obj;
                String beanClassName = ref.getClassName();
                Class<?> beanClass = null;
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                if (tcl != null) {
                    try {
                        beanClass = tcl.loadClass(beanClassName);
                    } catch (ClassNotFoundException e) {
                    }
                } else {
                    try {
                        beanClass = Class.forName(beanClassName);
                    } catch (ClassNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
                if (beanClass == null) {
                    throw new NamingException("Class not found: " + beanClassName);
                }
                BeanInfo bi = Introspector.getBeanInfo(beanClass);
                PropertyDescriptor[] pda = bi.getPropertyDescriptors();
                Object bean = beanClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                RefAddr ra = ref.get("forceString");
                Map<String, Method> forced = new HashMap<>();
                if (ra != null) {
                    String value = (String) ra.getContent();
                    Class<?>[] paramTypes = {String.class};
                    for (String param : value.split(",")) {
                        String param2 = param.trim();
                        int index = param2.indexOf(61);
                        if (index >= 0) {
                            setterName = param2.substring(index + 1).trim();
                            param2 = param2.substring(0, index).trim();
                        } else {
                            setterName = "set" + param2.substring(0, 1).toUpperCase(Locale.ENGLISH) + param2.substring(1);
                        }
                        try {
                            forced.put(param2, beanClass.getMethod(setterName, paramTypes));
                        } catch (NoSuchMethodException | SecurityException e3) {
                            throw new NamingException("Forced String setter " + setterName + " not found for property " + param2);
                        }
                    }
                }
                Enumeration<RefAddr> e4 = ref.getAll();
                while (e4.hasMoreElements()) {
                    RefAddr ra2 = e4.nextElement();
                    String propName = ra2.getType();
                    if (!propName.equals(Constants.FACTORY) && !propName.equals("scope") && !propName.equals(ResourceRef.AUTH) && !propName.equals("forceString") && !propName.equals("singleton")) {
                        String value2 = (String) ra2.getContent();
                        Object[] valueArray = new Object[1];
                        Method method = forced.get(propName);
                        if (method != null) {
                            valueArray[0] = value2;
                            try {
                                method.invoke(bean, valueArray);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e5) {
                                throw new NamingException("Forced String setter " + method.getName() + " threw exception for property " + propName);
                            }
                        } else {
                            int i = 0;
                            while (true) {
                                if (i >= pda.length) {
                                    break;
                                } else if (!pda[i].getName().equals(propName)) {
                                    i++;
                                } else {
                                    Class<?> propType = pda[i].getPropertyType();
                                    if (propType.equals(String.class)) {
                                        valueArray[0] = value2;
                                    } else if (propType.equals(Character.class) || propType.equals(Character.TYPE)) {
                                        valueArray[0] = Character.valueOf(value2.charAt(0));
                                    } else if (propType.equals(Byte.class) || propType.equals(Byte.TYPE)) {
                                        valueArray[0] = Byte.valueOf(value2);
                                    } else if (propType.equals(Short.class) || propType.equals(Short.TYPE)) {
                                        valueArray[0] = Short.valueOf(value2);
                                    } else if (propType.equals(Integer.class) || propType.equals(Integer.TYPE)) {
                                        valueArray[0] = Integer.valueOf(value2);
                                    } else if (propType.equals(Long.class) || propType.equals(Long.TYPE)) {
                                        valueArray[0] = Long.valueOf(value2);
                                    } else if (propType.equals(Float.class) || propType.equals(Float.TYPE)) {
                                        valueArray[0] = Float.valueOf(value2);
                                    } else if (propType.equals(Double.class) || propType.equals(Double.TYPE)) {
                                        valueArray[0] = Double.valueOf(value2);
                                    } else if (propType.equals(Boolean.class) || propType.equals(Boolean.TYPE)) {
                                        valueArray[0] = Boolean.valueOf(value2);
                                    } else {
                                        throw new NamingException("String conversion for property " + propName + " of type '" + propType.getName() + "' not available");
                                    }
                                    Method setProp = pda[i].getWriteMethod();
                                    if (setProp != null) {
                                        setProp.invoke(bean, valueArray);
                                    } else {
                                        throw new NamingException("Write not allowed for property: " + propName);
                                    }
                                }
                            }
                            if (i == pda.length) {
                                throw new NamingException("No set method found for property: " + propName);
                            }
                        }
                    }
                }
                return bean;
            } catch (IntrospectionException ie) {
                NamingException ne = new NamingException(ie.getMessage());
                ne.setRootCause(ie);
                throw ne;
            } catch (ReflectiveOperationException e6) {
                Throwable cause = e6.getCause();
                if (cause instanceof ThreadDeath) {
                    throw ((ThreadDeath) cause);
                }
                if (cause instanceof VirtualMachineError) {
                    throw ((VirtualMachineError) cause);
                }
                NamingException ne2 = new NamingException(e6.getMessage());
                ne2.setRootCause(e6);
                throw ne2;
            }
        }
        return null;
    }
}