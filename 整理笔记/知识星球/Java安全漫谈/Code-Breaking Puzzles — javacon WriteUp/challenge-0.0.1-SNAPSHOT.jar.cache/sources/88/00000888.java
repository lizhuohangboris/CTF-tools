package org.apache.catalina.mbeans;

import java.io.File;
import java.net.InetAddress;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.DataSourceRealm;
import org.apache.catalina.realm.JDBCRealm;
import org.apache.catalina.realm.JNDIRealm;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.realm.UserDatabaseRealm;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.HostConfig;
import org.apache.coyote.http11.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/mbeans/MBeanFactory.class */
public class MBeanFactory {
    private static final Log log = LogFactory.getLog(MBeanFactory.class);
    protected static final StringManager sm = StringManager.getManager(MBeanFactory.class);
    private static final MBeanServer mserver = MBeanUtils.createServer();
    private Object container;

    public void setContainer(Object container) {
        this.container = container;
    }

    private final String getPathStr(String t) {
        if (t == null || t.equals("/")) {
            return "";
        }
        return t;
    }

    private Container getParentContainerFromParent(ObjectName pname) throws Exception {
        String type = pname.getKeyProperty("type");
        String j2eeType = pname.getKeyProperty("j2eeType");
        Service service = getService(pname);
        StandardEngine engine = (StandardEngine) service.getContainer();
        if (j2eeType != null && j2eeType.equals("WebModule")) {
            String name = pname.getKeyProperty("name").substring(2);
            int i = name.indexOf(47);
            String hostName = name.substring(0, i);
            String path = name.substring(i);
            Container host = engine.findChild(hostName);
            String pathStr = getPathStr(path);
            Container context = host.findChild(pathStr);
            return context;
        } else if (type != null) {
            if (type.equals("Engine")) {
                return engine;
            }
            if (type.equals("Host")) {
                String hostName2 = pname.getKeyProperty("host");
                Container host2 = engine.findChild(hostName2);
                return host2;
            }
            return null;
        } else {
            return null;
        }
    }

    private Container getParentContainerFromChild(ObjectName oname) throws Exception {
        String hostName = oname.getKeyProperty("host");
        String path = oname.getKeyProperty("path");
        Service service = getService(oname);
        Container engine = service.getContainer();
        if (hostName == null) {
            return engine;
        }
        if (path == null) {
            Container host = engine.findChild(hostName);
            return host;
        }
        Container host2 = engine.findChild(hostName);
        Container context = host2.findChild(getPathStr(path));
        return context;
    }

    private Service getService(ObjectName oname) throws Exception {
        if (this.container instanceof Service) {
            return (Service) this.container;
        }
        StandardService service = null;
        String domain = oname.getDomain();
        if (this.container instanceof Server) {
            Service[] services = ((Server) this.container).findServices();
            for (Service service2 : services) {
                service = (StandardService) service2;
                if (domain.equals(service.getObjectName().getDomain())) {
                    break;
                }
            }
        }
        if (service == null || !service.getObjectName().getDomain().equals(domain)) {
            throw new Exception("Service with the domain is not found");
        }
        return service;
    }

    public String createAjpConnector(String parent, String address, int port) throws Exception {
        return createConnector(parent, address, port, true, false);
    }

    public String createDataSourceRealm(String parent, String dataSourceName, String roleNameCol, String userCredCol, String userNameCol, String userRoleTable, String userTable) throws Exception {
        DataSourceRealm realm = new DataSourceRealm();
        realm.setDataSourceName(dataSourceName);
        realm.setRoleNameCol(roleNameCol);
        realm.setUserCredCol(userCredCol);
        realm.setUserNameCol(userNameCol);
        realm.setUserRoleTable(userRoleTable);
        realm.setUserTable(userTable);
        return addRealmToParent(parent, realm);
    }

    private String addRealmToParent(String parent, Realm realm) throws Exception {
        ObjectName pname = new ObjectName(parent);
        Container container = getParentContainerFromParent(pname);
        container.setRealm(realm);
        ObjectName oname = null;
        if (realm instanceof JmxEnabled) {
            oname = ((JmxEnabled) realm).getObjectName();
        }
        if (oname != null) {
            return oname.toString();
        }
        return null;
    }

    public String createHttpConnector(String parent, String address, int port) throws Exception {
        return createConnector(parent, address, port, false, false);
    }

    private String createConnector(String parent, String address, int port, boolean isAjp, boolean isSSL) throws Exception {
        String protocol = isAjp ? "AJP/1.3" : Constants.HTTP_11;
        Connector retobj = new Connector(protocol);
        if (address != null && address.length() > 0) {
            retobj.setProperty("address", address);
        }
        retobj.setPort(port);
        retobj.setSecure(isSSL);
        retobj.setScheme(isSSL ? "https" : "http");
        ObjectName pname = new ObjectName(parent);
        Service service = getService(pname);
        service.addConnector(retobj);
        ObjectName coname = retobj.getObjectName();
        return coname.toString();
    }

    public String createHttpsConnector(String parent, String address, int port) throws Exception {
        return createConnector(parent, address, port, false, true);
    }

    public String createJDBCRealm(String parent, String driverName, String connectionName, String connectionPassword, String connectionURL) throws Exception {
        JDBCRealm realm = new JDBCRealm();
        realm.setDriverName(driverName);
        realm.setConnectionName(connectionName);
        realm.setConnectionPassword(connectionPassword);
        realm.setConnectionURL(connectionURL);
        return addRealmToParent(parent, realm);
    }

    public String createJNDIRealm(String parent) throws Exception {
        JNDIRealm realm = new JNDIRealm();
        return addRealmToParent(parent, realm);
    }

    public String createMemoryRealm(String parent) throws Exception {
        MemoryRealm realm = new MemoryRealm();
        return addRealmToParent(parent, realm);
    }

    public String createStandardContext(String parent, String path, String docBase) throws Exception {
        return createStandardContext(parent, path, docBase, false, false);
    }

    public String createStandardContext(String parent, String path, String docBase, boolean xmlValidation, boolean xmlNamespaceAware) throws Exception {
        StandardContext context = new StandardContext();
        context.setPath(getPathStr(path));
        context.setDocBase(docBase);
        context.setXmlValidation(xmlValidation);
        context.setXmlNamespaceAware(xmlNamespaceAware);
        ContextConfig contextConfig = new ContextConfig();
        context.addLifecycleListener(contextConfig);
        ObjectName pname = new ObjectName(parent);
        ObjectName deployer = new ObjectName(pname.getDomain() + ":type=Deployer,host=" + pname.getKeyProperty("host"));
        if (mserver.isRegistered(deployer)) {
            String contextName = context.getName();
            mserver.invoke(deployer, "addServiced", new Object[]{contextName}, new String[]{"java.lang.String"});
            String configPath = (String) mserver.getAttribute(deployer, "configBaseName");
            String baseName = context.getBaseName();
            File configFile = new File(new File(configPath), baseName + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);
            if (configFile.isFile()) {
                context.setConfigFile(configFile.toURI().toURL());
            }
            mserver.invoke(deployer, "manageApp", new Object[]{context}, new String[]{"org.apache.catalina.Context"});
            mserver.invoke(deployer, "removeServiced", new Object[]{contextName}, new String[]{"java.lang.String"});
        } else {
            log.warn("Deployer not found for " + pname.getKeyProperty("host"));
            Service service = getService(pname);
            Engine engine = service.getContainer();
            Host host = (Host) engine.findChild(pname.getKeyProperty("host"));
            host.addChild(context);
        }
        return context.getObjectName().toString();
    }

    public String createStandardHost(String parent, String name, String appBase, boolean autoDeploy, boolean deployOnStartup, boolean deployXML, boolean unpackWARs) throws Exception {
        StandardHost host = new StandardHost();
        host.setName(name);
        host.setAppBase(appBase);
        host.setAutoDeploy(autoDeploy);
        host.setDeployOnStartup(deployOnStartup);
        host.setDeployXML(deployXML);
        host.setUnpackWARs(unpackWARs);
        HostConfig hostConfig = new HostConfig();
        host.addLifecycleListener(hostConfig);
        ObjectName pname = new ObjectName(parent);
        Service service = getService(pname);
        Engine engine = service.getContainer();
        engine.addChild(host);
        return host.getObjectName().toString();
    }

    public String createStandardServiceEngine(String domain, String defaultHost, String baseDir) throws Exception {
        if (!(this.container instanceof Server)) {
            throw new Exception("Container not Server");
        }
        StandardEngine engine = new StandardEngine();
        engine.setDomain(domain);
        engine.setName(domain);
        engine.setDefaultHost(defaultHost);
        Service service = new StandardService();
        service.setContainer(engine);
        service.setName(domain);
        ((Server) this.container).addService(service);
        return engine.getObjectName().toString();
    }

    public String createStandardManager(String parent) throws Exception {
        StandardManager manager = new StandardManager();
        ObjectName pname = new ObjectName(parent);
        Container container = getParentContainerFromParent(pname);
        if (container instanceof Context) {
            ((Context) container).setManager(manager);
            ObjectName oname = manager.getObjectName();
            if (oname != null) {
                return oname.toString();
            }
            return null;
        }
        throw new Exception(sm.getString("mBeanFactory.managerContext"));
    }

    public String createUserDatabaseRealm(String parent, String resourceName) throws Exception {
        UserDatabaseRealm realm = new UserDatabaseRealm();
        realm.setResourceName(resourceName);
        return addRealmToParent(parent, realm);
    }

    public String createValve(String className, String parent) throws Exception {
        ObjectName parentName = new ObjectName(parent);
        Container container = getParentContainerFromParent(parentName);
        if (container == null) {
            throw new IllegalArgumentException();
        }
        Valve valve = (Valve) Class.forName(className).getConstructor(new Class[0]).newInstance(new Object[0]);
        container.getPipeline().addValve(valve);
        if (valve instanceof JmxEnabled) {
            return ((JmxEnabled) valve).getObjectName().toString();
        }
        return null;
    }

    public String createWebappLoader(String parent) throws Exception {
        WebappLoader loader = new WebappLoader();
        ObjectName pname = new ObjectName(parent);
        Container container = getParentContainerFromParent(pname);
        if (container instanceof Context) {
            ((Context) container).setLoader(loader);
        }
        ObjectName oname = MBeanUtils.createObjectName(pname.getDomain(), loader);
        return oname.toString();
    }

    public void removeConnector(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        Service service = getService(oname);
        String port = oname.getKeyProperty("port");
        String address = oname.getKeyProperty("address");
        if (address != null) {
            address = ObjectName.unquote(address);
        }
        Connector[] conns = service.findConnectors();
        for (int i = 0; i < conns.length; i++) {
            String connAddress = null;
            Object objConnAddress = conns[i].getProperty("address");
            if (objConnAddress != null) {
                connAddress = ((InetAddress) objConnAddress).getHostAddress();
            }
            String connPort = "" + conns[i].getPort();
            if (address == null) {
                if (connAddress == null && port.equals(connPort)) {
                    service.removeConnector(conns[i]);
                    conns[i].destroy();
                    return;
                }
            } else if (address.equals(connAddress) && port.equals(connPort)) {
                service.removeConnector(conns[i]);
                conns[i].destroy();
                return;
            }
        }
    }

    public void removeContext(String contextName) throws Exception {
        ObjectName oname = new ObjectName(contextName);
        String domain = oname.getDomain();
        StandardService service = (StandardService) getService(oname);
        Engine engine = service.getContainer();
        String name = oname.getKeyProperty("name").substring(2);
        int i = name.indexOf(47);
        String hostName = name.substring(0, i);
        String path = name.substring(i);
        ObjectName deployer = new ObjectName(domain + ":type=Deployer,host=" + hostName);
        String pathStr = getPathStr(path);
        if (mserver.isRegistered(deployer)) {
            mserver.invoke(deployer, "addServiced", new Object[]{pathStr}, new String[]{"java.lang.String"});
            mserver.invoke(deployer, "unmanageApp", new Object[]{pathStr}, new String[]{"java.lang.String"});
            mserver.invoke(deployer, "removeServiced", new Object[]{pathStr}, new String[]{"java.lang.String"});
            return;
        }
        log.warn("Deployer not found for " + hostName);
        Host host = (Host) engine.findChild(hostName);
        Context context = (Context) host.findChild(pathStr);
        host.removeChild(context);
        if (context instanceof StandardContext) {
            try {
                context.destroy();
            } catch (Exception e) {
                log.warn("Error during context [" + context.getName() + "] destroy ", e);
            }
        }
    }

    public void removeHost(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        String hostName = oname.getKeyProperty("host");
        Service service = getService(oname);
        Engine engine = service.getContainer();
        Host host = (Host) engine.findChild(hostName);
        if (host != null) {
            engine.removeChild(host);
        }
    }

    public void removeLoader(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        Container container = getParentContainerFromChild(oname);
        if (container instanceof Context) {
            ((Context) container).setLoader(null);
        }
    }

    public void removeManager(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        Container container = getParentContainerFromChild(oname);
        if (container instanceof Context) {
            ((Context) container).setManager(null);
        }
    }

    public void removeRealm(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        Container container = getParentContainerFromChild(oname);
        container.setRealm(null);
    }

    public void removeService(String name) throws Exception {
        if (!(this.container instanceof Server)) {
            throw new Exception();
        }
        ObjectName oname = new ObjectName(name);
        Service service = getService(oname);
        ((Server) this.container).removeService(service);
    }

    public void removeValve(String name) throws Exception {
        ObjectName oname = new ObjectName(name);
        Container container = getParentContainerFromChild(oname);
        Valve[] valves = container.getPipeline().getValves();
        for (int i = 0; i < valves.length; i++) {
            ObjectName voname = ((JmxEnabled) valves[i]).getObjectName();
            if (voname.equals(oname)) {
                container.getPipeline().removeValve(valves[i]);
            }
        }
    }
}