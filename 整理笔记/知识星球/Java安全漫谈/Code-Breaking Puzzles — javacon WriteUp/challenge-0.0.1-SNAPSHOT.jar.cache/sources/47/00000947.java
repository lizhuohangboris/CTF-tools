package org.apache.catalina.util;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Globals;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/util/LifecycleMBeanBase.class */
public abstract class LifecycleMBeanBase extends LifecycleBase implements JmxEnabled {
    private static final Log log = LogFactory.getLog(LifecycleMBeanBase.class);
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");
    private String domain = null;
    private ObjectName oname = null;
    protected MBeanServer mserver = null;

    protected abstract String getDomainInternal();

    protected abstract String getObjectNameKeyProperties();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void initInternal() throws LifecycleException {
        if (this.oname == null) {
            this.mserver = Registry.getRegistry(null, null).getMBeanServer();
            this.oname = register(this, getObjectNameKeyProperties());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.util.LifecycleBase
    public void destroyInternal() throws LifecycleException {
        unregister(this.oname);
    }

    @Override // org.apache.catalina.JmxEnabled
    public final void setDomain(String domain) {
        this.domain = domain;
    }

    @Override // org.apache.catalina.JmxEnabled
    public final String getDomain() {
        if (this.domain == null) {
            this.domain = getDomainInternal();
        }
        if (this.domain == null) {
            this.domain = Globals.DEFAULT_MBEAN_DOMAIN;
        }
        return this.domain;
    }

    @Override // org.apache.catalina.JmxEnabled
    public final ObjectName getObjectName() {
        return this.oname;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ObjectName register(Object obj, String objectNameKeyProperties) {
        StringBuilder name = new StringBuilder(getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);
        ObjectName on = null;
        try {
            on = new ObjectName(name.toString());
            Registry.getRegistry(null, null).registerComponent(obj, on, (String) null);
        } catch (Exception e) {
            log.warn(sm.getString("lifecycleMBeanBase.registerFail", obj, name), e);
        } catch (MalformedObjectNameException e2) {
            log.warn(sm.getString("lifecycleMBeanBase.registerFail", obj, name), e2);
        }
        return on;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void unregister(ObjectName on) {
        if (on == null) {
            return;
        }
        if (this.mserver == null) {
            log.warn(sm.getString("lifecycleMBeanBase.unregisterNoServer", on));
            return;
        }
        try {
            this.mserver.unregisterMBean(on);
        } catch (InstanceNotFoundException e) {
            log.warn(sm.getString("lifecycleMBeanBase.unregisterFail", on), e);
        } catch (MBeanRegistrationException e2) {
            log.warn(sm.getString("lifecycleMBeanBase.unregisterFail", on), e2);
        }
    }

    public final void postDeregister() {
    }

    public final void postRegister(Boolean registrationDone) {
    }

    public final void preDeregister() throws Exception {
    }

    public final ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.mserver = server;
        this.oname = name;
        this.domain = name.getDomain();
        return this.oname;
    }
}