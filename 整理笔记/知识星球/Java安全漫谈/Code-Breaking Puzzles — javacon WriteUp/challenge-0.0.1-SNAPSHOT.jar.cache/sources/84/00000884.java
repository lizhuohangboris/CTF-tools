package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.naming.ResourceRef;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.NamingResources;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/ContextResourceMBean.class */
public class ContextResourceMBean extends BaseCatalinaMBean<ContextResource> {
    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        ContextResource cr = doGetManagedResource();
        if (ResourceRef.AUTH.equals(name)) {
            return cr.getAuth();
        }
        if ("description".equals(name)) {
            return cr.getDescription();
        }
        if ("name".equals(name)) {
            return cr.getName();
        }
        if ("scope".equals(name)) {
            return cr.getScope();
        }
        if ("type".equals(name)) {
            return cr.getType();
        }
        String value = (String) cr.getProperty(name);
        if (value == null) {
            throw new AttributeNotFoundException("Cannot find attribute [" + name + "]");
        }
        return value;
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
        ContextResource cr = doGetManagedResource();
        if (ResourceRef.AUTH.equals(name)) {
            cr.setAuth((String) value);
        } else if ("description".equals(name)) {
            cr.setDescription((String) value);
        } else if ("name".equals(name)) {
            cr.setName((String) value);
        } else if ("scope".equals(name)) {
            cr.setScope((String) value);
        } else if ("type".equals(name)) {
            cr.setType((String) value);
        } else {
            cr.setProperty(name, "" + value);
        }
        NamingResources nr = cr.getNamingResources();
        nr.removeResource(cr.getName());
        nr.addResource(cr);
    }
}