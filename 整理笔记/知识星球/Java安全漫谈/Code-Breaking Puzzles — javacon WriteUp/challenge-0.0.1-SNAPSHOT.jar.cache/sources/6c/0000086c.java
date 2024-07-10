package org.apache.catalina.manager;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.core.util.FileSize;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/manager/StatusTransformer.class */
public class StatusTransformer {
    public static void setContentType(HttpServletResponse response, int mode) {
        if (mode == 0) {
            response.setContentType("text/html;charset=utf-8");
        } else if (mode == 1) {
            response.setContentType("text/xml;charset=utf-8");
        }
    }

    public static void writeHeader(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(Constants.HTML_HEADER_SECTION);
        } else if (mode == 1) {
            writer.write(Constants.XML_DECLARATION);
            writer.print(MessageFormat.format(Constants.XML_STYLE, args));
            writer.write("<status>");
        }
    }

    public static void writeBody(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        }
    }

    public static void writeManager(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        }
    }

    public static void writePageHeading(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, args));
        }
    }

    public static void writeServerInfo(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        }
    }

    public static void writeFooter(PrintWriter writer, int mode) {
        if (mode == 0) {
            writer.print(Constants.HTML_TAIL_SECTION);
        } else if (mode == 1) {
            writer.write("</status>");
        }
    }

    public static void writeOSState(PrintWriter writer, int mode, Object[] args) {
        long[] result = new long[16];
        boolean ok = false;
        try {
            Class<?>[] paramTypes = {result.getClass()};
            Object[] paramValues = {result};
            Method method = Class.forName("org.apache.tomcat.jni.OS").getMethod("info", paramTypes);
            method.invoke(null, paramValues);
            ok = true;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(ExceptionUtils.unwrapInvocationTargetException(t));
        }
        if (ok) {
            if (mode == 0) {
                writer.print("<h1>OS</h1>");
                writer.print("<p>");
                writer.print(args[0]);
                writer.print(formatSize(Long.valueOf(result[0]), true));
                writer.print(" " + args[1]);
                writer.print(formatSize(Long.valueOf(result[1]), true));
                writer.print(" " + args[2]);
                writer.print(formatSize(Long.valueOf(result[2]), true));
                writer.print(" " + args[3]);
                writer.print(formatSize(Long.valueOf(result[3]), true));
                writer.print(" " + args[4]);
                writer.print(Long.valueOf(result[6]));
                writer.print("<br>");
                writer.print(" " + args[5]);
                writer.print(formatTime(Long.valueOf(result[11] / 1000), true));
                writer.print(" " + args[6]);
                writer.print(formatTime(Long.valueOf(result[12] / 1000), true));
                writer.print("</p>");
                return;
            }
            if (mode == 1) {
            }
        }
    }

    public static void writeVMState(PrintWriter writer, int mode, Object[] args) throws Exception {
        SortedMap<String, MemoryPoolMXBean> memoryPoolMBeans = new TreeMap<>();
        for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            String sortKey = mbean.getType() + ":" + mbean.getName();
            memoryPoolMBeans.put(sortKey, mbean);
        }
        if (mode == 0) {
            writer.print("<h1>JVM</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().freeMemory()), true));
            writer.print(" " + args[1]);
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().totalMemory()), true));
            writer.print(" " + args[2]);
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().maxMemory()), true));
            writer.print("</p>");
            writer.write("<table border=\"0\"><thead><tr><th>" + args[3] + "</th><th>" + args[4] + "</th><th>" + args[5] + "</th><th>" + args[6] + "</th><th>" + args[7] + "</th><th>" + args[8] + "</th></tr></thead><tbody>");
            for (MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                MemoryUsage usage = memoryPoolMBean.getUsage();
                writer.write("<tr><td>");
                writer.print(memoryPoolMBean.getName());
                writer.write("</td><td>");
                writer.print(memoryPoolMBean.getType());
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getInit()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getCommitted()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getMax()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getUsed()), true));
                if (usage.getMax() > 0) {
                    writer.write(" (" + ((usage.getUsed() * 100) / usage.getMax()) + "%)");
                }
                writer.write("</td></tr>");
            }
            writer.write("</tbody></table>");
        } else if (mode == 1) {
            writer.write("<jvm>");
            writer.write("<memory");
            writer.write(" free='" + Runtime.getRuntime().freeMemory() + "'");
            writer.write(" total='" + Runtime.getRuntime().totalMemory() + "'");
            writer.write(" max='" + Runtime.getRuntime().maxMemory() + "'/>");
            for (MemoryPoolMXBean memoryPoolMBean2 : memoryPoolMBeans.values()) {
                MemoryUsage usage2 = memoryPoolMBean2.getUsage();
                writer.write("<memorypool");
                writer.write(" name='" + Escape.xml("", memoryPoolMBean2.getName()) + "'");
                writer.write(" type='" + memoryPoolMBean2.getType() + "'");
                writer.write(" usageInit='" + usage2.getInit() + "'");
                writer.write(" usageCommitted='" + usage2.getCommitted() + "'");
                writer.write(" usageMax='" + usage2.getMax() + "'");
                writer.write(" usageUsed='" + usage2.getUsed() + "'/>");
            }
            writer.write("</jvm>");
        }
    }

    public static void writeConnectorState(PrintWriter writer, ObjectName tpName, String name, MBeanServer mBeanServer, Vector<ObjectName> globalRequestProcessors, Vector<ObjectName> requestProcessors, int mode, Object[] args) throws Exception {
        if (mode != 0) {
            if (mode == 1) {
                writer.write("<connector name='" + name + "'>");
                writer.write("<threadInfo ");
                writer.write(" maxThreads=\"" + mBeanServer.getAttribute(tpName, "maxThreads") + "\"");
                writer.write(" currentThreadCount=\"" + mBeanServer.getAttribute(tpName, "currentThreadCount") + "\"");
                writer.write(" currentThreadsBusy=\"" + mBeanServer.getAttribute(tpName, "currentThreadsBusy") + "\"");
                writer.write(" />");
                ObjectName grpName = null;
                Enumeration<ObjectName> enumeration = globalRequestProcessors.elements();
                while (enumeration.hasMoreElements()) {
                    ObjectName objectName = enumeration.nextElement();
                    if (name.equals(objectName.getKeyProperty("name"))) {
                        grpName = objectName;
                    }
                }
                if (grpName != null) {
                    writer.write("<requestInfo ");
                    writer.write(" maxTime=\"" + mBeanServer.getAttribute(grpName, "maxTime") + "\"");
                    writer.write(" processingTime=\"" + mBeanServer.getAttribute(grpName, "processingTime") + "\"");
                    writer.write(" requestCount=\"" + mBeanServer.getAttribute(grpName, "requestCount") + "\"");
                    writer.write(" errorCount=\"" + mBeanServer.getAttribute(grpName, "errorCount") + "\"");
                    writer.write(" bytesReceived=\"" + mBeanServer.getAttribute(grpName, "bytesReceived") + "\"");
                    writer.write(" bytesSent=\"" + mBeanServer.getAttribute(grpName, "bytesSent") + "\"");
                    writer.write(" />");
                    writer.write("<workers>");
                    Enumeration<ObjectName> enumeration2 = requestProcessors.elements();
                    while (enumeration2.hasMoreElements()) {
                        ObjectName objectName2 = enumeration2.nextElement();
                        if (name.equals(objectName2.getKeyProperty("worker"))) {
                            writeProcessorState(writer, objectName2, mBeanServer, mode);
                        }
                    }
                    writer.write("</workers>");
                }
                writer.write("</connector>");
                return;
            }
            return;
        }
        writer.print("<h1>");
        writer.print(name);
        writer.print("</h1>");
        writer.print("<p>");
        writer.print(args[0]);
        writer.print(mBeanServer.getAttribute(tpName, "maxThreads"));
        writer.print(" " + args[1]);
        writer.print(mBeanServer.getAttribute(tpName, "currentThreadCount"));
        writer.print(" " + args[2]);
        writer.print(mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
        try {
            Object value = mBeanServer.getAttribute(tpName, "keepAliveCount");
            writer.print(" " + args[3]);
            writer.print(value);
        } catch (Exception e) {
        }
        writer.print("<br>");
        ObjectName grpName2 = null;
        Enumeration<ObjectName> enumeration3 = globalRequestProcessors.elements();
        while (enumeration3.hasMoreElements()) {
            ObjectName objectName3 = enumeration3.nextElement();
            if (name.equals(objectName3.getKeyProperty("name"))) {
                grpName2 = objectName3;
            }
        }
        if (grpName2 == null) {
            return;
        }
        writer.print(args[4]);
        writer.print(formatTime(mBeanServer.getAttribute(grpName2, "maxTime"), false));
        writer.print(" " + args[5]);
        writer.print(formatTime(mBeanServer.getAttribute(grpName2, "processingTime"), true));
        writer.print(" " + args[6]);
        writer.print(mBeanServer.getAttribute(grpName2, "requestCount"));
        writer.print(" " + args[7]);
        writer.print(mBeanServer.getAttribute(grpName2, "errorCount"));
        writer.print(" " + args[8]);
        writer.print(formatSize(mBeanServer.getAttribute(grpName2, "bytesReceived"), true));
        writer.print(" " + args[9]);
        writer.print(formatSize(mBeanServer.getAttribute(grpName2, "bytesSent"), true));
        writer.print("</p>");
        writer.print("<table border=\"0\"><tr><th>" + args[10] + "</th><th>" + args[11] + "</th><th>" + args[12] + "</th><th>" + args[13] + "</th><th>" + args[14] + "</th><th>" + args[15] + "</th><th>" + args[16] + "</th><th>" + args[17] + "</th></tr>");
        Enumeration<ObjectName> enumeration4 = requestProcessors.elements();
        while (enumeration4.hasMoreElements()) {
            ObjectName objectName4 = enumeration4.nextElement();
            if (name.equals(objectName4.getKeyProperty("worker"))) {
                writer.print("<tr>");
                writeProcessorState(writer, objectName4, mBeanServer, mode);
                writer.print("</tr>");
            }
        }
        writer.print("</table>");
        writer.print("<p>");
        writer.print(args[18]);
        writer.print("</p>");
    }

    protected static void writeProcessorState(PrintWriter writer, ObjectName pName, MBeanServer mBeanServer, int mode) throws Exception {
        String stageStr;
        Integer stageValue = (Integer) mBeanServer.getAttribute(pName, "stage");
        int stage = stageValue.intValue();
        boolean fullStatus = true;
        boolean showRequest = true;
        switch (stage) {
            case 0:
                stageStr = "R";
                fullStatus = false;
                break;
            case 1:
                stageStr = "P";
                fullStatus = false;
                break;
            case 2:
                stageStr = "P";
                fullStatus = false;
                break;
            case 3:
                stageStr = "S";
                break;
            case 4:
                stageStr = "F";
                break;
            case 5:
                stageStr = "F";
                break;
            case 6:
                stageStr = "K";
                fullStatus = true;
                showRequest = false;
                break;
            case 7:
                stageStr = "R";
                fullStatus = false;
                break;
            default:
                stageStr = CallerData.NA;
                fullStatus = false;
                break;
        }
        if (mode == 0) {
            writer.write("<td><strong>");
            writer.write(stageStr);
            writer.write("</strong></td>");
            if (fullStatus) {
                writer.write("<td>");
                writer.print(formatTime(mBeanServer.getAttribute(pName, "requestProcessingTime"), false));
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesSent"), false));
                } else {
                    writer.write(CallerData.NA);
                }
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesReceived"), false));
                } else {
                    writer.write(CallerData.NA);
                }
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddrForwarded")));
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddr")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "virtualHost")));
                writer.write("</td>");
                writer.write("<td nowrap class=\"row-left\">");
                if (showRequest) {
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "method")));
                    writer.write(" ");
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "currentUri")));
                    String queryString = (String) mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write(CallerData.NA);
                        writer.print(Escape.htmlElementContent(queryString));
                    }
                    writer.write(" ");
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "protocol")));
                } else {
                    writer.write(CallerData.NA);
                }
                writer.write("</td>");
                return;
            }
            writer.write("<td>?</td><td>?</td><td>?</td><td>?</td><td>?</td><td>?</td>");
        } else if (mode == 1) {
            writer.write("<worker ");
            writer.write(" stage=\"" + stageStr + "\"");
            if (fullStatus) {
                writer.write(" requestProcessingTime=\"" + mBeanServer.getAttribute(pName, "requestProcessingTime") + "\"");
                writer.write(" requestBytesSent=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesSent"));
                } else {
                    writer.write(CustomBooleanEditor.VALUE_0);
                }
                writer.write("\"");
                writer.write(" requestBytesReceived=\"");
                if (showRequest) {
                    writer.write("" + mBeanServer.getAttribute(pName, "requestBytesReceived"));
                } else {
                    writer.write(CustomBooleanEditor.VALUE_0);
                }
                writer.write("\"");
                writer.write(" remoteAddr=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddr")) + "\"");
                writer.write(" virtualHost=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "virtualHost")) + "\"");
                if (showRequest) {
                    writer.write(" method=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "method")) + "\"");
                    writer.write(" currentUri=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "currentUri")) + "\"");
                    String queryString2 = (String) mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString2 != null && !queryString2.equals("")) {
                        writer.write(" currentQueryString=\"" + Escape.htmlElementContent(queryString2) + "\"");
                    } else {
                        writer.write(" currentQueryString=\"&#63;\"");
                    }
                    writer.write(" protocol=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "protocol")) + "\"");
                } else {
                    writer.write(" method=\"&#63;\"");
                    writer.write(" currentUri=\"&#63;\"");
                    writer.write(" currentQueryString=\"&#63;\"");
                    writer.write(" protocol=\"&#63;\"");
                }
            } else {
                writer.write(" requestProcessingTime=\"0\"");
                writer.write(" requestBytesSent=\"0\"");
                writer.write(" requestBytesReceived=\"0\"");
                writer.write(" remoteAddr=\"&#63;\"");
                writer.write(" virtualHost=\"&#63;\"");
                writer.write(" method=\"&#63;\"");
                writer.write(" currentUri=\"&#63;\"");
                writer.write(" currentQueryString=\"&#63;\"");
                writer.write(" protocol=\"&#63;\"");
            }
            writer.write(" />");
        }
    }

    public static void writeDetailedState(PrintWriter writer, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
            Set<ObjectName> hostsON = mBeanServer.queryNames(queryHosts, (QueryExp) null);
            writer.print("<h1>");
            writer.print("Application list");
            writer.print("</h1>");
            writer.print("<p>");
            int count = 0;
            Iterator<ObjectName> iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                ObjectName contextON = iterator.next();
                String webModuleName = contextON.getKeyProperty("name");
                if (webModuleName.startsWith("//")) {
                    webModuleName = webModuleName.substring(2);
                }
                int slash = webModuleName.indexOf(47);
                if (slash == -1) {
                    count++;
                } else {
                    int i = count;
                    count++;
                    writer.print("<a href=\"#" + i + ".0\">");
                    writer.print(Escape.htmlElementContent(webModuleName));
                    writer.print("</a>");
                    if (iterator.hasNext()) {
                        writer.print("<br>");
                    }
                }
            }
            writer.print("</p>");
            int count2 = 0;
            for (ObjectName contextON2 : hostsON) {
                int i2 = count2;
                count2++;
                writer.print("<a class=\"A.name\" name=\"" + i2 + ".0\">");
                writeContext(writer, contextON2, mBeanServer, mode);
            }
            return;
        }
        if (mode == 1) {
        }
    }

    protected static void writeContext(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            String webModuleName = objectName.getKeyProperty("name");
            String name = webModuleName;
            if (name == null) {
                return;
            }
            if (name.startsWith("//")) {
                name = name.substring(2);
            }
            int slash = name.indexOf(47);
            if (slash != -1) {
                String hostName = name.substring(0, slash);
                String contextName = name.substring(slash);
                ObjectName queryManager = new ObjectName(objectName.getDomain() + ":type=Manager,context=" + contextName + ",host=" + hostName + ",*");
                Set<ObjectName> managersON = mBeanServer.queryNames(queryManager, (QueryExp) null);
                ObjectName managerON = null;
                for (ObjectName aManagersON : managersON) {
                    managerON = aManagersON;
                }
                ObjectName queryJspMonitor = new ObjectName(objectName.getDomain() + ":type=JspMonitor,WebModule=" + webModuleName + ",*");
                Set<ObjectName> jspMonitorONs = mBeanServer.queryNames(queryJspMonitor, (QueryExp) null);
                if (contextName.equals("/")) {
                }
                writer.print("<h1>");
                writer.print(Escape.htmlElementContent(name));
                writer.print("</h1>");
                writer.print("</a>");
                writer.print("<p>");
                Object startTime = mBeanServer.getAttribute(objectName, "startTime");
                writer.print(" Start time: " + new Date(((Long) startTime).longValue()));
                writer.print(" Startup time: ");
                writer.print(formatTime(mBeanServer.getAttribute(objectName, "startupTime"), false));
                writer.print(" TLD scan time: ");
                writer.print(formatTime(mBeanServer.getAttribute(objectName, "tldScanTime"), false));
                if (managerON != null) {
                    writeManager(writer, managerON, mBeanServer, mode);
                }
                if (jspMonitorONs != null) {
                    writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
                }
                writer.print("</p>");
                String onStr = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
                ObjectName servletObjectName = new ObjectName(onStr);
                Set<ObjectInstance> set = mBeanServer.queryMBeans(servletObjectName, (QueryExp) null);
                for (ObjectInstance oi : set) {
                    writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
                }
                return;
            }
            return;
        }
        if (mode == 1) {
        }
    }

    public static void writeManager(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" Active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "activeSessions"));
            writer.print(" Session count: ");
            writer.print(mBeanServer.getAttribute(objectName, "sessionCounter"));
            writer.print(" Max active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "maxActive"));
            writer.print(" Rejected session creations: ");
            writer.print(mBeanServer.getAttribute(objectName, "rejectedSessions"));
            writer.print(" Expired sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "expiredSessions"));
            writer.print(" Longest session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionMaxAliveTime")));
            writer.print(" Average session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionAverageAliveTime")));
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), false));
            return;
        }
        if (mode == 1) {
        }
    }

    public static void writeJspMonitor(PrintWriter writer, Set<ObjectName> jspMonitorONs, MBeanServer mBeanServer, int mode) throws Exception {
        int jspCount = 0;
        int jspReloadCount = 0;
        for (ObjectName jspMonitorON : jspMonitorONs) {
            Object obj = mBeanServer.getAttribute(jspMonitorON, "jspCount");
            jspCount += ((Integer) obj).intValue();
            Object obj2 = mBeanServer.getAttribute(jspMonitorON, "jspReloadCount");
            jspReloadCount += ((Integer) obj2).intValue();
        }
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" JSPs loaded: ");
            writer.print(jspCount);
            writer.print(" JSPs reloaded: ");
            writer.print(jspReloadCount);
            return;
        }
        if (mode == 1) {
        }
    }

    public static void writeWrapper(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            String servletName = objectName.getKeyProperty("name");
            String[] mappings = (String[]) mBeanServer.invoke(objectName, "findMappings", (Object[]) null, (String[]) null);
            writer.print("<h2>");
            writer.print(Escape.htmlElementContent(servletName));
            if (mappings != null && mappings.length > 0) {
                writer.print(" [ ");
                for (int i = 0; i < mappings.length; i++) {
                    writer.print(Escape.htmlElementContent(mappings[i]));
                    if (i < mappings.length - 1) {
                        writer.print(" , ");
                    }
                }
                writer.print(" ] ");
            }
            writer.print("</h2>");
            writer.print("<p>");
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), true));
            writer.print(" Max time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "maxTime"), false));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(objectName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(objectName, "errorCount"));
            writer.print(" Load time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "loadTime"), false));
            writer.print(" Classloading time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "classLoadTime"), false));
            writer.print("</p>");
            return;
        }
        if (mode == 1) {
        }
    }

    public static String formatSize(Object obj, boolean mb) {
        long bytes = -1;
        if (obj instanceof Long) {
            bytes = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            bytes = ((Integer) obj).intValue();
        }
        if (mb) {
            StringBuilder buff = new StringBuilder();
            if (bytes < 0) {
                buff.append('-');
                bytes = -bytes;
            }
            long mbytes = bytes / FileSize.MB_COEFFICIENT;
            long rest = ((bytes - (mbytes * FileSize.MB_COEFFICIENT)) * 100) / FileSize.MB_COEFFICIENT;
            buff.append(mbytes).append('.');
            if (rest < 10) {
                buff.append('0');
            }
            buff.append(rest).append(" MB");
            return buff.toString();
        }
        return (bytes / FileSize.KB_COEFFICIENT) + " KB";
    }

    public static String formatTime(Object obj, boolean seconds) {
        long time = -1;
        if (obj instanceof Long) {
            time = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            time = ((Integer) obj).intValue();
        }
        if (seconds) {
            return (((float) time) / 1000.0f) + " s";
        }
        return time + " ms";
    }

    public static String formatSeconds(Object obj) {
        long time = -1;
        if (obj instanceof Long) {
            time = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            time = ((Integer) obj).intValue();
        }
        return time + " s";
    }
}