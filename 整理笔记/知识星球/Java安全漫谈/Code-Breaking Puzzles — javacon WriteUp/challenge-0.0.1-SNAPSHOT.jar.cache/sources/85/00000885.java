package org.apache.catalina.mbeans;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import org.apache.catalina.Group;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/GlobalResourcesLifecycleListener.class */
public class GlobalResourcesLifecycleListener implements LifecycleListener {
    protected Lifecycle component = null;
    private static final Log log = LogFactory.getLog(GlobalResourcesLifecycleListener.class);
    protected static final Registry registry = MBeanUtils.createRegistry();

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            this.component = event.getLifecycle();
            createMBeans();
        } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
            destroyMBeans();
            this.component = null;
        }
    }

    protected void createMBeans() {
        try {
            Context context = (Context) new InitialContext().lookup("java:/");
            try {
                createMBeans("", context);
            } catch (NamingException e) {
                log.error("Exception processing Global JNDI Resources", e);
            }
        } catch (NamingException e2) {
            log.error("No global naming context defined for server");
        }
    }

    protected void createMBeans(String prefix, Context context) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug("Creating MBeans for Global JNDI Resources in Context '" + prefix + "'");
        }
        try {
            NamingEnumeration<Binding> bindings = context.listBindings("");
            while (bindings.hasMore()) {
                Binding binding = (Binding) bindings.next();
                String name = prefix + binding.getName();
                Object value = context.lookup(binding.getName());
                if (log.isDebugEnabled()) {
                    log.debug("Checking resource " + name);
                }
                if (value instanceof Context) {
                    createMBeans(name + "/", (Context) value);
                } else if (value instanceof UserDatabase) {
                    try {
                        createMBeans(name, (UserDatabase) value);
                    } catch (Exception e) {
                        log.error("Exception creating UserDatabase MBeans for " + name, e);
                    }
                }
            }
        } catch (RuntimeException ex) {
            log.error("RuntimeException " + ex);
        } catch (OperationNotSupportedException ex2) {
            log.error("Operation not supported " + ex2);
        }
    }

    protected void createMBeans(String name, UserDatabase database) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Creating UserDatabase MBeans for resource " + name);
            log.debug("Database=" + database);
        }
        try {
            MBeanUtils.createMBean(database);
            Iterator<Role> roles = database.getRoles();
            while (roles.hasNext()) {
                Role role = roles.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating Role MBean for role " + role);
                }
                try {
                    MBeanUtils.createMBean(role);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot create Role MBean for role " + role, e);
                }
            }
            Iterator<Group> groups = database.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating Group MBean for group " + group);
                }
                try {
                    MBeanUtils.createMBean(group);
                } catch (Exception e2) {
                    throw new IllegalArgumentException("Cannot create Group MBean for group " + group, e2);
                }
            }
            Iterator<User> users = database.getUsers();
            while (users.hasNext()) {
                User user = users.next();
                if (log.isDebugEnabled()) {
                    log.debug("  Creating User MBean for user " + user);
                }
                try {
                    MBeanUtils.createMBean(user);
                } catch (Exception e3) {
                    throw new IllegalArgumentException("Cannot create User MBean for user " + user, e3);
                }
            }
        } catch (Exception e4) {
            throw new IllegalArgumentException("Cannot create UserDatabase MBean for resource " + name, e4);
        }
    }

    protected void destroyMBeans() {
        if (log.isDebugEnabled()) {
            log.debug("Destroying MBeans for Global JNDI Resources");
        }
    }
}