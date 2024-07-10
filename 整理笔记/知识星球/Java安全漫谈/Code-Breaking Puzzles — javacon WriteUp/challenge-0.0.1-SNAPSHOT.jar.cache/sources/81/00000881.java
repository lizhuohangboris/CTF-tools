package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.NamingResources;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/ContextEnvironmentMBean.class */
public class ContextEnvironmentMBean extends BaseCatalinaMBean<ContextEnvironment> {
    @Override // org.apache.tomcat.util.modeler.BaseModelMBean
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        super.setAttribute(attribute);
        ContextEnvironment ce = doGetManagedResource();
        NamingResources nr = ce.getNamingResources();
        nr.removeEnvironment(ce.getName());
        nr.addEnvironment(ce);
    }
}