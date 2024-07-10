package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.Executor;
import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/concurrent/DefaultManagedTaskExecutor.class */
public class DefaultManagedTaskExecutor extends ConcurrentTaskExecutor implements InitializingBean {
    private JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();
    @Nullable
    private String jndiName = "java:comp/DefaultManagedExecutorService";

    public void setJndiTemplate(JndiTemplate jndiTemplate) {
        this.jndiLocator.setJndiTemplate(jndiTemplate);
    }

    public void setJndiEnvironment(Properties jndiEnvironment) {
        this.jndiLocator.setJndiEnvironment(jndiEnvironment);
    }

    public void setResourceRef(boolean resourceRef) {
        this.jndiLocator.setResourceRef(resourceRef);
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        if (this.jndiName != null) {
            setConcurrentExecutor((Executor) this.jndiLocator.lookup(this.jndiName, Executor.class));
        }
    }
}