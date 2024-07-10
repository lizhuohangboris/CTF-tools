package org.apache.catalina.mbeans;

import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Group;
import org.apache.catalina.Loader;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.core.Constants;
import org.apache.catalina.util.ContextName;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/MBeanUtils.class */
public class MBeanUtils {
    private static final String[][] exceptions = {new String[]{"org.apache.catalina.users.MemoryGroup", "Group"}, new String[]{"org.apache.catalina.users.MemoryRole", "Role"}, new String[]{"org.apache.catalina.users.MemoryUser", "User"}};
    private static Registry registry = createRegistry();
    private static MBeanServer mserver = createServer();

    static String createManagedName(Object component) {
        String className = component.getClass().getName();
        for (int i = 0; i < exceptions.length; i++) {
            if (className.equals(exceptions[i][0])) {
                return exceptions[i][1];
            }
        }
        int period = className.lastIndexOf(46);
        if (period >= 0) {
            className = className.substring(period + 1);
        }
        return className;
    }

    public static DynamicMBean createMBean(ContextEnvironment environment) throws Exception {
        String mname = createManagedName(environment);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(environment);
        ObjectName oname = createObjectName(domain, environment);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static DynamicMBean createMBean(ContextResource resource) throws Exception {
        String mname = createManagedName(resource);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(resource);
        ObjectName oname = createObjectName(domain, resource);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static DynamicMBean createMBean(ContextResourceLink resourceLink) throws Exception {
        String mname = createManagedName(resourceLink);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(resourceLink);
        ObjectName oname = createObjectName(domain, resourceLink);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DynamicMBean createMBean(Group group) throws Exception {
        String mname = createManagedName(group);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(group);
        ObjectName oname = createObjectName(domain, group);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DynamicMBean createMBean(Role role) throws Exception {
        String mname = createManagedName(role);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(role);
        ObjectName oname = createObjectName(domain, role);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DynamicMBean createMBean(User user) throws Exception {
        String mname = createManagedName(user);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(user);
        ObjectName oname = createObjectName(domain, user);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DynamicMBean createMBean(UserDatabase userDatabase) throws Exception {
        String mname = createManagedName(userDatabase);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            Exception e = new Exception("ManagedBean is not found with " + mname);
            throw new MBeanException(e);
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        DynamicMBean mbean = managed.createMBean(userDatabase);
        ObjectName oname = createObjectName(domain, userDatabase);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
        mserver.registerMBean(mbean, oname);
        return mbean;
    }

    public static ObjectName createObjectName(String domain, ContextEnvironment environment) throws MalformedObjectNameException {
        ObjectName name = null;
        Object container = environment.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=Environment,resourcetype=Global,name=" + environment.getName());
        } else if (container instanceof Context) {
            Context context = (Context) container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=Environment,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",name=" + environment.getName());
        }
        return name;
    }

    public static ObjectName createObjectName(String domain, ContextResource resource) throws MalformedObjectNameException {
        ObjectName name = null;
        String quotedResourceName = ObjectName.quote(resource.getName());
        Object container = resource.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=Resource,resourcetype=Global,class=" + resource.getType() + ",name=" + quotedResourceName);
        } else if (container instanceof Context) {
            Context context = (Context) container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=Resource,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",class=" + resource.getType() + ",name=" + quotedResourceName);
        }
        return name;
    }

    public static ObjectName createObjectName(String domain, ContextResourceLink resourceLink) throws MalformedObjectNameException {
        ObjectName name = null;
        String quotedResourceLinkName = ObjectName.quote(resourceLink.getName());
        Object container = resourceLink.getNamingResources().getContainer();
        if (container instanceof Server) {
            name = new ObjectName(domain + ":type=ResourceLink,resourcetype=Global,name=" + quotedResourceLinkName);
        } else if (container instanceof Context) {
            Context context = (Context) container;
            ContextName cn = new ContextName(context.getName(), false);
            Container host = context.getParent();
            name = new ObjectName(domain + ":type=ResourceLink,resourcetype=Context,host=" + host.getName() + ",context=" + cn.getDisplayName() + ",name=" + quotedResourceLinkName);
        }
        return name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ObjectName createObjectName(String domain, Group group) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=Group,groupname=" + ObjectName.quote(group.getGroupname()) + ",database=" + group.getUserDatabase().getId());
        return name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ObjectName createObjectName(String domain, Loader loader) throws MalformedObjectNameException {
        Context context = loader.getContext();
        ContextName cn = new ContextName(context.getName(), false);
        Container host = context.getParent();
        ObjectName name = new ObjectName(domain + ":type=Loader,host=" + host.getName() + ",context=" + cn.getDisplayName());
        return name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ObjectName createObjectName(String domain, Role role) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=Role,rolename=" + ObjectName.quote(role.getRolename()) + ",database=" + role.getUserDatabase().getId());
        return name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ObjectName createObjectName(String domain, User user) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=User,username=" + ObjectName.quote(user.getUsername()) + ",database=" + user.getUserDatabase().getId());
        return name;
    }

    static ObjectName createObjectName(String domain, UserDatabase userDatabase) throws MalformedObjectNameException {
        ObjectName name = new ObjectName(domain + ":type=UserDatabase,database=" + userDatabase.getId());
        return name;
    }

    public static synchronized Registry createRegistry() {
        if (registry == null) {
            registry = Registry.getRegistry(null, null);
            ClassLoader cl = MBeanUtils.class.getClassLoader();
            registry.loadDescriptors("org.apache.catalina.mbeans", cl);
            registry.loadDescriptors("org.apache.catalina.authenticator", cl);
            registry.loadDescriptors(Constants.Package, cl);
            registry.loadDescriptors("org.apache.catalina", cl);
            registry.loadDescriptors("org.apache.catalina.deploy", cl);
            registry.loadDescriptors(org.apache.catalina.loader.Constants.Package, cl);
            registry.loadDescriptors("org.apache.catalina.realm", cl);
            registry.loadDescriptors("org.apache.catalina.session", cl);
            registry.loadDescriptors(org.apache.catalina.startup.Constants.Package, cl);
            registry.loadDescriptors(org.apache.catalina.users.Constants.Package, cl);
            registry.loadDescriptors("org.apache.catalina.ha", cl);
            registry.loadDescriptors("org.apache.catalina.connector", cl);
            registry.loadDescriptors(org.apache.catalina.valves.Constants.Package, cl);
            registry.loadDescriptors("org.apache.catalina.storeconfig", cl);
            registry.loadDescriptors("org.apache.tomcat.util.descriptor.web", cl);
        }
        return registry;
    }

    public static synchronized MBeanServer createServer() {
        if (mserver == null) {
            mserver = Registry.getRegistry(null, null).getMBeanServer();
        }
        return mserver;
    }

    public static void destroyMBean(ContextEnvironment environment) throws Exception {
        String mname = createManagedName(environment);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, environment);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    public static void destroyMBean(ContextResource resource) throws Exception {
        if ("org.apache.catalina.UserDatabase".equals(resource.getType())) {
            destroyMBeanUserDatabase(resource.getName());
        }
        String mname = createManagedName(resource);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, resource);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    public static void destroyMBean(ContextResourceLink resourceLink) throws Exception {
        String mname = createManagedName(resourceLink);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, resourceLink);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void destroyMBean(Group group) throws Exception {
        String mname = createManagedName(group);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, group);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void destroyMBean(Role role) throws Exception {
        String mname = createManagedName(role);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, role);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void destroyMBean(User user) throws Exception {
        String mname = createManagedName(user);
        ManagedBean managed = registry.findManagedBean(mname);
        if (managed == null) {
            return;
        }
        String domain = managed.getDomain();
        if (domain == null) {
            domain = mserver.getDefaultDomain();
        }
        ObjectName oname = createObjectName(domain, user);
        if (mserver.isRegistered(oname)) {
            mserver.unregisterMBean(oname);
        }
    }

    static void destroyMBeanUserDatabase(String userDatabase) throws Exception {
        ObjectName query = new ObjectName("Users:type=Group,database=" + userDatabase + ",*");
        Set<ObjectName> results = mserver.queryNames(query, (QueryExp) null);
        for (ObjectName result : results) {
            mserver.unregisterMBean(result);
        }
        ObjectName query2 = new ObjectName("Users:type=Role,database=" + userDatabase + ",*");
        Set<ObjectName> results2 = mserver.queryNames(query2, (QueryExp) null);
        for (ObjectName result2 : results2) {
            mserver.unregisterMBean(result2);
        }
        ObjectName query3 = new ObjectName("Users:type=User,database=" + userDatabase + ",*");
        Set<ObjectName> results3 = mserver.queryNames(query3, (QueryExp) null);
        for (ObjectName result3 : results3) {
            mserver.unregisterMBean(result3);
        }
        ObjectName db = new ObjectName("Users:type=UserDatabase,database=" + userDatabase);
        if (mserver.isRegistered(db)) {
            mserver.unregisterMBean(db);
        }
    }
}