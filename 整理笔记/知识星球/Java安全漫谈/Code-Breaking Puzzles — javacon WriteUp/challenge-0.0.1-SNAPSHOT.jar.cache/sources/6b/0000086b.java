package org.apache.catalina.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/manager/StatusManagerServlet.class */
public class StatusManagerServlet extends HttpServlet implements NotificationListener {
    private static final long serialVersionUID = 1;
    protected MBeanServer mBeanServer = null;
    protected final Vector<ObjectName> protocolHandlers = new Vector<>();
    protected final Vector<ObjectName> threadPools = new Vector<>();
    protected final Vector<ObjectName> requestProcessors = new Vector<>();
    protected final Vector<ObjectName> globalRequestProcessors = new Vector<>();
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    @Override // javax.servlet.GenericServlet
    public void init() throws ServletException {
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("*:type=ProtocolHandler,*");
            Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, (QueryExp) null);
            for (ObjectInstance oi : set) {
                this.protocolHandlers.addElement(oi.getObjectName());
            }
            ObjectName objectName2 = new ObjectName("*:type=ThreadPool,*");
            Set<ObjectInstance> set2 = this.mBeanServer.queryMBeans(objectName2, (QueryExp) null);
            for (ObjectInstance oi2 : set2) {
                this.threadPools.addElement(oi2.getObjectName());
            }
            ObjectName objectName3 = new ObjectName("*:type=GlobalRequestProcessor,*");
            Set<ObjectInstance> set3 = this.mBeanServer.queryMBeans(objectName3, (QueryExp) null);
            for (ObjectInstance oi3 : set3) {
                this.globalRequestProcessors.addElement(oi3.getObjectName());
            }
            ObjectName objectName4 = new ObjectName("*:type=RequestProcessor,*");
            Set<ObjectInstance> set4 = this.mBeanServer.queryMBeans(objectName4, (QueryExp) null);
            for (ObjectInstance oi4 : set4) {
                this.requestProcessors.addElement(oi4.getObjectName());
            }
            ObjectName objectName5 = new ObjectName("JMImplementation:type=MBeanServerDelegate");
            this.mBeanServer.addNotificationListener(objectName5, this, (NotificationFilter) null, (Object) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void destroy() {
        try {
            ObjectName objectName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
            this.mBeanServer.removeNotificationListener(objectName, this, (NotificationFilter) null, (Object) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int mode = 0;
        if (request.getParameter("XML") != null && request.getParameter("XML").equals("true")) {
            mode = 1;
        }
        StatusTransformer.setContentType(response, mode);
        PrintWriter writer = response.getWriter();
        boolean completeStatus = false;
        if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
            completeStatus = true;
        }
        StatusTransformer.writeHeader(writer, new Object[]{request.getContextPath()}, mode);
        Object[] args = new Object[2];
        args[0] = request.getContextPath();
        if (completeStatus) {
            args[1] = sm.getString("statusServlet.complete");
        } else {
            args[1] = sm.getString("statusServlet.title");
        }
        StatusTransformer.writeBody(writer, args, mode);
        Object[] args2 = new Object[9];
        args2[0] = sm.getString("htmlManagerServlet.manager");
        args2[1] = response.encodeURL(request.getContextPath() + "/html/list");
        args2[2] = sm.getString("htmlManagerServlet.list");
        args2[3] = response.encodeURL(request.getContextPath() + "/" + sm.getString("htmlManagerServlet.helpHtmlManagerFile"));
        args2[4] = sm.getString("htmlManagerServlet.helpHtmlManager");
        args2[5] = response.encodeURL(request.getContextPath() + "/" + sm.getString("htmlManagerServlet.helpManagerFile"));
        args2[6] = sm.getString("htmlManagerServlet.helpManager");
        if (completeStatus) {
            args2[7] = response.encodeURL(request.getContextPath() + "/status");
            args2[8] = sm.getString("statusServlet.title");
        } else {
            args2[7] = response.encodeURL(request.getContextPath() + "/status/all");
            args2[8] = sm.getString("statusServlet.complete");
        }
        StatusTransformer.writeManager(writer, args2, mode);
        StatusTransformer.writePageHeading(writer, new Object[]{sm.getString("htmlManagerServlet.serverTitle"), sm.getString("htmlManagerServlet.serverVersion"), sm.getString("htmlManagerServlet.serverJVMVersion"), sm.getString("htmlManagerServlet.serverJVMVendor"), sm.getString("htmlManagerServlet.serverOSName"), sm.getString("htmlManagerServlet.serverOSVersion"), sm.getString("htmlManagerServlet.serverOSArch"), sm.getString("htmlManagerServlet.serverHostname"), sm.getString("htmlManagerServlet.serverIPAddress")}, mode);
        Object[] args3 = new Object[8];
        args3[0] = ServerInfo.getServerInfo();
        args3[1] = System.getProperty("java.runtime.version");
        args3[2] = System.getProperty("java.vm.vendor");
        args3[3] = System.getProperty("os.name");
        args3[4] = System.getProperty("os.version");
        args3[5] = System.getProperty("os.arch");
        try {
            InetAddress address = InetAddress.getLocalHost();
            args3[6] = address.getHostName();
            args3[7] = address.getHostAddress();
        } catch (UnknownHostException e) {
            args3[6] = "-";
            args3[7] = "-";
        }
        StatusTransformer.writeServerInfo(writer, args3, mode);
        try {
            StatusTransformer.writeOSState(writer, mode, new Object[]{sm.getString("htmlManagerServlet.osPhysicalMemory"), sm.getString("htmlManagerServlet.osAvailableMemory"), sm.getString("htmlManagerServlet.osTotalPageFile"), sm.getString("htmlManagerServlet.osFreePageFile"), sm.getString("htmlManagerServlet.osMemoryLoad"), sm.getString("htmlManagerServlet.osKernelTime"), sm.getString("htmlManagerServlet.osUserTime")});
            StatusTransformer.writeVMState(writer, mode, new Object[]{sm.getString("htmlManagerServlet.jvmFreeMemory"), sm.getString("htmlManagerServlet.jvmTotalMemory"), sm.getString("htmlManagerServlet.jvmMaxMemory"), sm.getString("htmlManagerServlet.jvmTableTitleMemoryPool"), sm.getString("htmlManagerServlet.jvmTableTitleType"), sm.getString("htmlManagerServlet.jvmTableTitleInitial"), sm.getString("htmlManagerServlet.jvmTableTitleTotal"), sm.getString("htmlManagerServlet.jvmTableTitleMaximum"), sm.getString("htmlManagerServlet.jvmTableTitleUsed")});
            Enumeration<ObjectName> enumeration = this.threadPools.elements();
            while (enumeration.hasMoreElements()) {
                ObjectName objectName = enumeration.nextElement();
                String name = objectName.getKeyProperty("name");
                StatusTransformer.writeConnectorState(writer, objectName, name, this.mBeanServer, this.globalRequestProcessors, this.requestProcessors, mode, new Object[]{sm.getString("htmlManagerServlet.connectorStateMaxThreads"), sm.getString("htmlManagerServlet.connectorStateThreadCount"), sm.getString("htmlManagerServlet.connectorStateThreadBusy"), sm.getString("htmlManagerServlet.connectorStateAliveSocketCount"), sm.getString("htmlManagerServlet.connectorStateMaxProcessingTime"), sm.getString("htmlManagerServlet.connectorStateProcessingTime"), sm.getString("htmlManagerServlet.connectorStateRequestCount"), sm.getString("htmlManagerServlet.connectorStateErrorCount"), sm.getString("htmlManagerServlet.connectorStateBytesRecieved"), sm.getString("htmlManagerServlet.connectorStateBytesSent"), sm.getString("htmlManagerServlet.connectorStateTableTitleStage"), sm.getString("htmlManagerServlet.connectorStateTableTitleTime"), sm.getString("htmlManagerServlet.connectorStateTableTitleBSent"), sm.getString("htmlManagerServlet.connectorStateTableTitleBRecv"), sm.getString("htmlManagerServlet.connectorStateTableTitleClientForw"), sm.getString("htmlManagerServlet.connectorStateTableTitleClientAct"), sm.getString("htmlManagerServlet.connectorStateTableTitleVHost"), sm.getString("htmlManagerServlet.connectorStateTableTitleRequest"), sm.getString("htmlManagerServlet.connectorStateHint")});
            }
            if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
                StatusTransformer.writeDetailedState(writer, this.mBeanServer, mode);
            }
            StatusTransformer.writeFooter(writer, mode);
        } catch (Exception e2) {
            throw new ServletException(e2);
        }
    }

    public void handleNotification(Notification notification, Object handback) {
        String type;
        if (notification instanceof MBeanServerNotification) {
            ObjectName objectName = ((MBeanServerNotification) notification).getMBeanName();
            if (notification.getType().equals("JMX.mbean.registered")) {
                String type2 = objectName.getKeyProperty("type");
                if (type2 != null) {
                    if (type2.equals("ProtocolHandler")) {
                        this.protocolHandlers.addElement(objectName);
                    } else if (type2.equals("ThreadPool")) {
                        this.threadPools.addElement(objectName);
                    } else if (type2.equals("GlobalRequestProcessor")) {
                        this.globalRequestProcessors.addElement(objectName);
                    } else if (type2.equals("RequestProcessor")) {
                        this.requestProcessors.addElement(objectName);
                    }
                }
            } else if (notification.getType().equals("JMX.mbean.unregistered") && (type = objectName.getKeyProperty("type")) != null) {
                if (type.equals("ProtocolHandler")) {
                    this.protocolHandlers.removeElement(objectName);
                } else if (type.equals("ThreadPool")) {
                    this.threadPools.removeElement(objectName);
                } else if (type.equals("GlobalRequestProcessor")) {
                    this.globalRequestProcessors.removeElement(objectName);
                } else if (type.equals("RequestProcessor")) {
                    this.requestProcessors.removeElement(objectName);
                }
            }
        }
    }
}