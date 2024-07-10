package org.apache.catalina.mbeans;

import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/RoleMBean.class */
public class RoleMBean extends BaseModelMBean {
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("Role");
}