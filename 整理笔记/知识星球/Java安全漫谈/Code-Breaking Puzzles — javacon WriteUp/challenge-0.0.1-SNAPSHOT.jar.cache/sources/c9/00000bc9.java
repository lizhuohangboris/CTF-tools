package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/InstanceManager.class */
public interface InstanceManager {
    Object newInstance(Class<?> cls) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    Object newInstance(String str) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    Object newInstance(String str, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    void newInstance(Object obj) throws IllegalAccessException, InvocationTargetException, NamingException;

    void destroyInstance(Object obj) throws IllegalAccessException, InvocationTargetException;

    default void backgroundProcess() {
    }
}