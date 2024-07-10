package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.IntrospectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/ConnectorMBean.class */
public class ConnectorMBean extends ClassNameMBean<Connector> {
    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        Connector connector = doGetManagedResource();
        return IntrospectionUtils.getProperty(connector, name);
    }

    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute is null"), "Attribute is null");
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        Connector connector = doGetManagedResource();
        IntrospectionUtils.setProperty(connector, name, String.valueOf(value));
    }
}