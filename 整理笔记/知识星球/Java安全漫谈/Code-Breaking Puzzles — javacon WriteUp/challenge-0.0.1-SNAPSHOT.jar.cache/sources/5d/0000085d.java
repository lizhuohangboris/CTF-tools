package org.apache.catalina.manager;

import ch.qos.logback.classic.ClassicConstants;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.manager.util.BaseSessionComparator;
import org.apache.catalina.manager.util.SessionUtils;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.http.HttpHeaders;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/manager/HTMLManagerServlet.class */
public final class HTMLManagerServlet extends ManagerServlet {
    private static final long serialVersionUID = 1;
    static final String APPLICATION_MESSAGE = "message";
    static final String APPLICATION_ERROR = "error";
    static final String sessionsListJspPath = "/WEB-INF/jsp/sessionsList.jsp";
    static final String sessionDetailJspPath = "/WEB-INF/jsp/sessionDetail.jsp";
    static final String connectorCiphersJspPath = "/WEB-INF/jsp/connectorCiphers.jsp";
    static final String connectorCertsJspPath = "/WEB-INF/jsp/connectorCerts.jsp";
    static final String connectorTrustedCertsJspPath = "/WEB-INF/jsp/connectorTrustedCerts.jsp";
    private boolean showProxySessions = false;
    private static final String APPS_HEADER_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"6\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-left\"><small>{1}</small></td>\n <td class=\"header-left\"><small>{2}</small></td>\n <td class=\"header-center\"><small>{3}</small></td>\n <td class=\"header-center\"><small>{4}</small></td>\n <td class=\"header-left\"><small>{5}</small></td>\n <td class=\"header-left\"><small>{6}</small></td>\n</tr>\n";
    private static final String APPS_ROW_DETAILS_SECTION = "<tr>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{0}</small></td>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{1}</small></td>\n <td class=\"row-left\" bgcolor=\"{6}\" rowspan=\"2\"><small>{2}</small></td>\n <td class=\"row-center\" bgcolor=\"{6}\" rowspan=\"2\"><small>{3}</small></td>\n <td class=\"row-center\" bgcolor=\"{6}\" rowspan=\"2\"><small><a href=\"{4}\">{5}</a></small></td>\n";
    private static final String MANAGER_APP_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  <small>\n  &nbsp;{1}&nbsp;\n  &nbsp;{3}&nbsp;\n  &nbsp;{5}&nbsp;\n  &nbsp;{7}&nbsp;\n  </small>\n </td>\n</tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STARTED_DEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  &nbsp;<small>{1}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">  <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{4}\">  <small><input type=\"submit\" value=\"{5}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{6}\">  <small><input type=\"submit\" value=\"{7}\"></small>  </form>\n </td>\n </tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STOPPED_DEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\" rowspan=\"2\">\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">  <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  &nbsp;<small>{3}</small>&nbsp;\n  &nbsp;<small>{5}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{6}\">  <small><input type=\"submit\" value=\"{7}\"></small>  </form>\n </td>\n</tr>\n<tr></tr>\n";
    private static final String STARTED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\">\n  &nbsp;<small>{1}</small>&nbsp;\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">  <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{4}\">  <small><input type=\"submit\" value=\"{5}\"></small>  </form>\n  &nbsp;<small>{7}</small>&nbsp;\n </td>\n </tr><tr>\n <td class=\"row-left\" bgcolor=\"{13}\">\n  <form method=\"POST\" action=\"{8}\">\n  <small>\n  &nbsp;<input type=\"submit\" value=\"{9}\">&nbsp;{10}&nbsp;<input type=\"text\" name=\"idle\" size=\"5\" value=\"{11}\">&nbsp;{12}&nbsp;\n  </small>\n  </form>\n </td>\n</tr>\n";
    private static final String STOPPED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION = " <td class=\"row-left\" bgcolor=\"{13}\" rowspan=\"2\">\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">  <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  &nbsp;<small>{3}</small>&nbsp;\n  &nbsp;<small>{5}</small>&nbsp;\n  &nbsp;<small>{7}</small>&nbsp;\n </td>\n</tr>\n<tr></tr>\n";
    private static final String DEPLOY_SECTION = "</table>\n<br>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployPath\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{4}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployVersion\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{5}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployConfig\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{6}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"deployWar\" size=\"40\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{7}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n";
    private static final String UPLOAD_SECTION = "<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{0}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{1}\" enctype=\"multipart/form-data\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{2}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"file\" name=\"deployWar\" size=\"40\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{3}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>\n\n";
    private static final String CONFIG_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"tlsHostName\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{4}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>";
    private static final String DIAGNOSTICS_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{2}\">\n   <input type=\"submit\" value=\"{4}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{3}</small>\n </td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{5}</small></td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{6}\">\n   <input type=\"submit\" value=\"{7}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{8}</small>\n </td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{9}\">\n   <input type=\"submit\" value=\"{10}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{11}</small>\n </td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form method=\"post\" action=\"{12}\">\n   <input type=\"submit\" value=\"{13}\">\n  </form>\n </td>\n <td class=\"row-left\">\n  <small>{14}</small>\n </td>\n</tr>\n</table>\n<br>";

    @Override // org.apache.catalina.manager.ManagerServlet, javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter(XMLDeclaration.ATTRIBUTE_NAME_VERSION));
        }
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && !command.equals("/") && !command.equals("/list")) {
            if (command.equals("/sessions")) {
                try {
                    doSessions(cn, request, response, smClient);
                    return;
                } catch (Exception e) {
                    log("HTMLManagerServlet.sessions[" + cn + "]", e);
                    message = smClient.getString("managerServlet.exception", e.toString());
                }
            } else if (command.equals("/sslConnectorCiphers")) {
                sslConnectorCiphers(request, response);
            } else if (command.equals("/sslConnectorCerts")) {
                sslConnectorCerts(request, response);
            } else if (command.equals("/sslConnectorTrustedCerts")) {
                sslConnectorTrustedCerts(request, response);
            } else {
                message = (command.equals("/upload") || command.equals("/deploy") || command.equals("/reload") || command.equals("/undeploy") || command.equals("/expire") || command.equals("/start") || command.equals("/stop")) ? smClient.getString("managerServlet.postCommand", command) : smClient.getString("managerServlet.unknownCommand", command);
            }
        }
        list(request, response, message, smClient);
    }

    @Override // javax.servlet.http.HttpServlet
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter(XMLDeclaration.ATTRIBUTE_NAME_VERSION));
        }
        String deployPath = request.getParameter("deployPath");
        String deployWar = request.getParameter("deployWar");
        String deployConfig = request.getParameter("deployConfig");
        ContextName deployCn = null;
        if (deployPath != null && deployPath.length() > 0) {
            deployCn = new ContextName(deployPath, request.getParameter("deployVersion"));
        } else if (deployConfig != null && deployConfig.length() > 0) {
            deployCn = ContextName.extractFromPath(deployConfig);
        } else if (deployWar != null && deployWar.length() > 0) {
            deployCn = ContextName.extractFromPath(deployWar);
        }
        String tlsHostName = request.getParameter("tlsHostName");
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && command.length() != 0) {
            if (command.equals("/upload")) {
                message = upload(request, smClient);
            } else if (command.equals("/deploy")) {
                message = deployInternal(deployConfig, deployCn, deployWar, smClient);
            } else if (command.equals("/reload")) {
                message = reload(cn, smClient);
            } else if (command.equals("/undeploy")) {
                message = undeploy(cn, smClient);
            } else if (command.equals("/expire")) {
                message = expireSessions(cn, request, smClient);
            } else if (command.equals("/start")) {
                message = start(cn, smClient);
            } else if (command.equals("/stop")) {
                message = stop(cn, smClient);
            } else if (command.equals("/findleaks")) {
                message = findleaks(smClient);
            } else if (command.equals("/sslReload")) {
                message = sslReload(tlsHostName, smClient);
            } else {
                doGet(request, response);
                return;
            }
        }
        list(request, response, message, smClient);
    }

    protected String upload(HttpServletRequest request, StringManager smClient) {
        String message = "";
        try {
            Part warPart = request.getPart("deployWar");
            if (warPart == null) {
                message = smClient.getString("htmlManagerServlet.deployUploadNoFile");
            } else {
                String filename = warPart.getSubmittedFileName();
                if (!filename.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    message = smClient.getString("htmlManagerServlet.deployUploadNotWar", filename);
                } else {
                    if (filename.lastIndexOf(92) >= 0) {
                        filename = filename.substring(filename.lastIndexOf(92) + 1);
                    }
                    if (filename.lastIndexOf(47) >= 0) {
                        filename = filename.substring(filename.lastIndexOf(47) + 1);
                    }
                    File file = new File(this.host.getAppBaseFile(), filename);
                    if (file.exists()) {
                        message = smClient.getString("htmlManagerServlet.deployUploadWarExists", filename);
                    } else {
                        ContextName cn = new ContextName(filename, true);
                        String name = cn.getName();
                        if (this.host.findChild(name) != null && !isDeployed(name)) {
                            message = smClient.getString("htmlManagerServlet.deployUploadInServerXml", filename);
                        } else if (isServiced(name)) {
                            message = smClient.getString("managerServlet.inService", name);
                        } else {
                            addServiced(name);
                            warPart.write(file.getAbsolutePath());
                            check(name);
                            removeServiced(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            message = smClient.getString("htmlManagerServlet.deployUploadFail", e.getMessage());
            log(message, e);
        }
        return message;
    }

    protected String deployInternal(String config, ContextName cn, String war, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.deploy(printWriter, config, cn, war, false, smClient);
        return stringWriter.toString();
    }

    protected void list(HttpServletRequest request, HttpServletResponse response, String message, StringManager smClient) throws IOException {
        String highlightColor;
        boolean isDeployed;
        if (this.debug >= 1) {
            log("list: Listing contexts for virtual host '" + this.host.getName() + "'");
        }
        PrintWriter writer = response.getWriter();
        writer.print(Constants.HTML_HEADER_SECTION);
        writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, request.getContextPath(), smClient.getString("htmlManagerServlet.title")));
        Object[] args = new Object[3];
        args[0] = smClient.getString("htmlManagerServlet.messageLabel");
        if (message == null || message.length() == 0) {
            args[1] = "OK";
        } else {
            args[1] = Escape.htmlElementContent(message);
        }
        writer.print(MessageFormat.format(Constants.MESSAGE_SECTION, args));
        writer.print(MessageFormat.format(Constants.MANAGER_SECTION, smClient.getString("htmlManagerServlet.manager"), response.encodeURL(request.getContextPath() + "/html/list"), smClient.getString("htmlManagerServlet.list"), response.encodeURL(request.getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpHtmlManagerFile")), smClient.getString("htmlManagerServlet.helpHtmlManager"), response.encodeURL(request.getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpManagerFile")), smClient.getString("htmlManagerServlet.helpManager"), response.encodeURL(request.getContextPath() + "/status"), smClient.getString("statusServlet.title")));
        writer.print(MessageFormat.format(APPS_HEADER_SECTION, smClient.getString("htmlManagerServlet.appsTitle"), smClient.getString("htmlManagerServlet.appsPath"), smClient.getString("htmlManagerServlet.appsVersion"), smClient.getString("htmlManagerServlet.appsName"), smClient.getString("htmlManagerServlet.appsAvailable"), smClient.getString("htmlManagerServlet.appsSessions"), smClient.getString("htmlManagerServlet.appsTasks")));
        Container[] children = this.host.findChildren();
        String[] contextNames = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            contextNames[i] = children[i].getName();
        }
        Arrays.sort(contextNames);
        String appsStart = smClient.getString("htmlManagerServlet.appsStart");
        String appsStop = smClient.getString("htmlManagerServlet.appsStop");
        String appsReload = smClient.getString("htmlManagerServlet.appsReload");
        String appsUndeploy = smClient.getString("htmlManagerServlet.appsUndeploy");
        String appsExpire = smClient.getString("htmlManagerServlet.appsExpire");
        String noVersion = "<i>" + smClient.getString("htmlManagerServlet.noVersion") + "</i>";
        boolean isHighlighted = true;
        for (String contextName : contextNames) {
            Context ctxt = (Context) this.host.findChild(contextName);
            if (ctxt != null) {
                isHighlighted = !isHighlighted;
                if (isHighlighted) {
                    highlightColor = "#C3F3C3";
                } else {
                    highlightColor = "#FFFFFF";
                }
                String contextPath = ctxt.getPath();
                String displayPath = contextPath;
                if (displayPath.equals("")) {
                    displayPath = "/";
                }
                StringBuilder tmp = new StringBuilder();
                tmp.append("path=");
                tmp.append(URLEncoder.DEFAULT.encode(displayPath, StandardCharsets.UTF_8));
                if (ctxt.getWebappVersion().length() > 0) {
                    tmp.append("&version=");
                    tmp.append(URLEncoder.DEFAULT.encode(ctxt.getWebappVersion(), StandardCharsets.UTF_8));
                }
                String pathVersion = tmp.toString();
                try {
                    isDeployed = isDeployed(contextName);
                } catch (Exception e) {
                    isDeployed = false;
                }
                Object[] args2 = new Object[7];
                args2[0] = "<a href=\"" + URLEncoder.DEFAULT.encode(contextPath + "/", StandardCharsets.UTF_8) + "\">" + Escape.htmlElementContent(displayPath) + "</a>";
                if ("".equals(ctxt.getWebappVersion())) {
                    args2[1] = noVersion;
                } else {
                    args2[1] = Escape.htmlElementContent(ctxt.getWebappVersion());
                }
                if (ctxt.getDisplayName() == null) {
                    args2[2] = "&nbsp;";
                } else {
                    args2[2] = Escape.htmlElementContent(ctxt.getDisplayName());
                }
                args2[3] = Boolean.valueOf(ctxt.getState().isAvailable());
                args2[4] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/sessions?" + pathVersion));
                Manager manager = ctxt.getManager();
                if ((manager instanceof DistributedManager) && this.showProxySessions) {
                    args2[5] = Integer.valueOf(((DistributedManager) manager).getActiveSessionsFull());
                } else if (manager != null) {
                    args2[5] = Integer.valueOf(manager.getActiveSessions());
                } else {
                    args2[5] = 0;
                }
                args2[6] = highlightColor;
                writer.print(MessageFormat.format(APPS_ROW_DETAILS_SECTION, args2));
                Object[] args3 = new Object[14];
                args3[0] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/start?" + pathVersion));
                args3[1] = appsStart;
                args3[2] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/stop?" + pathVersion));
                args3[3] = appsStop;
                args3[4] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/reload?" + pathVersion));
                args3[5] = appsReload;
                args3[6] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/undeploy?" + pathVersion));
                args3[7] = appsUndeploy;
                args3[8] = Escape.htmlElementContent(response.encodeURL(request.getContextPath() + "/html/expire?" + pathVersion));
                args3[9] = appsExpire;
                args3[10] = smClient.getString("htmlManagerServlet.expire.explain");
                if (manager == null) {
                    args3[11] = smClient.getString("htmlManagerServlet.noManager");
                } else {
                    args3[11] = Integer.valueOf(ctxt.getSessionTimeout());
                }
                args3[12] = smClient.getString("htmlManagerServlet.expire.unit");
                args3[13] = highlightColor;
                if (ctxt.getName().equals(this.context.getName())) {
                    writer.print(MessageFormat.format(MANAGER_APP_ROW_BUTTON_SECTION, args3));
                } else if (ctxt.getState().isAvailable() && isDeployed) {
                    writer.print(MessageFormat.format(STARTED_DEPLOYED_APPS_ROW_BUTTON_SECTION, args3));
                } else if (ctxt.getState().isAvailable() && !isDeployed) {
                    writer.print(MessageFormat.format(STARTED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION, args3));
                } else if (!ctxt.getState().isAvailable() && isDeployed) {
                    writer.print(MessageFormat.format(STOPPED_DEPLOYED_APPS_ROW_BUTTON_SECTION, args3));
                } else {
                    writer.print(MessageFormat.format(STOPPED_NONDEPLOYED_APPS_ROW_BUTTON_SECTION, args3));
                }
            }
        }
        writer.print(MessageFormat.format(DEPLOY_SECTION, smClient.getString("htmlManagerServlet.deployTitle"), smClient.getString("htmlManagerServlet.deployServer"), response.encodeURL(request.getContextPath() + "/html/deploy"), smClient.getString("htmlManagerServlet.deployPath"), smClient.getString("htmlManagerServlet.deployVersion"), smClient.getString("htmlManagerServlet.deployConfig"), smClient.getString("htmlManagerServlet.deployWar"), smClient.getString("htmlManagerServlet.deployButton")));
        writer.print(MessageFormat.format(UPLOAD_SECTION, smClient.getString("htmlManagerServlet.deployUpload"), response.encodeURL(request.getContextPath() + "/html/upload"), smClient.getString("htmlManagerServlet.deployUploadFile"), smClient.getString("htmlManagerServlet.deployButton")));
        writer.print(MessageFormat.format(CONFIG_SECTION, smClient.getString("htmlManagerServlet.configTitle"), smClient.getString("htmlManagerServlet.configSslReloadTitle"), response.encodeURL(request.getContextPath() + "/html/sslReload"), smClient.getString("htmlManagerServlet.configSslHostName"), smClient.getString("htmlManagerServlet.configReloadButton")));
        writer.print(MessageFormat.format(DIAGNOSTICS_SECTION, smClient.getString("htmlManagerServlet.diagnosticsTitle"), smClient.getString("htmlManagerServlet.diagnosticsLeak"), response.encodeURL(request.getContextPath() + "/html/findleaks"), smClient.getString("htmlManagerServlet.diagnosticsLeakWarning"), smClient.getString("htmlManagerServlet.diagnosticsLeakButton"), smClient.getString("htmlManagerServlet.diagnosticsSsl"), response.encodeURL(request.getContextPath() + "/html/sslConnectorCiphers"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCipherButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCipherText"), response.encodeURL(request.getContextPath() + "/html/sslConnectorCerts"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCertsButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorCertsText"), response.encodeURL(request.getContextPath() + "/html/sslConnectorTrustedCerts"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorTrustedCertsButton"), smClient.getString("htmlManagerServlet.diagnosticsSslConnectorTrustedCertsText")));
        writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, smClient.getString("htmlManagerServlet.serverTitle"), smClient.getString("htmlManagerServlet.serverVersion"), smClient.getString("htmlManagerServlet.serverJVMVersion"), smClient.getString("htmlManagerServlet.serverJVMVendor"), smClient.getString("htmlManagerServlet.serverOSName"), smClient.getString("htmlManagerServlet.serverOSVersion"), smClient.getString("htmlManagerServlet.serverOSArch"), smClient.getString("htmlManagerServlet.serverHostname"), smClient.getString("htmlManagerServlet.serverIPAddress")));
        Object[] args4 = new Object[8];
        args4[0] = ServerInfo.getServerInfo();
        args4[1] = System.getProperty("java.runtime.version");
        args4[2] = System.getProperty("java.vm.vendor");
        args4[3] = System.getProperty("os.name");
        args4[4] = System.getProperty("os.version");
        args4[5] = System.getProperty("os.arch");
        try {
            InetAddress address = InetAddress.getLocalHost();
            args4[6] = address.getHostName();
            args4[7] = address.getHostAddress();
        } catch (UnknownHostException e2) {
            args4[6] = "-";
            args4[7] = "-";
        }
        writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args4));
        writer.print(Constants.HTML_TAIL_SECTION);
        writer.flush();
        writer.close();
    }

    protected String reload(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.reload(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String undeploy(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.undeploy(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String sessions(ContextName cn, int idle, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.sessions(printWriter, cn, idle, smClient);
        return stringWriter.toString();
    }

    protected String start(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.start(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String stop(ContextName cn, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.stop(printWriter, cn, smClient);
        return stringWriter.toString();
    }

    protected String findleaks(StringManager smClient) {
        StringBuilder msg = new StringBuilder();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.findleaks(false, printWriter, smClient);
        String writerText = stringWriter.toString();
        if (writerText.length() > 0) {
            if (!writerText.startsWith("FAIL -")) {
                msg.append(smClient.getString("htmlManagerServlet.findleaksList"));
            }
            msg.append(writerText);
        } else {
            msg.append(smClient.getString("htmlManagerServlet.findleaksNone"));
        }
        return msg.toString();
    }

    protected String sslReload(String tlsHostName, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.sslReload(printWriter, tlsHostName, smClient);
        return stringWriter.toString();
    }

    protected void sslConnectorCiphers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cipherList", getConnectorCiphers());
        getServletContext().getRequestDispatcher(connectorCiphersJspPath).forward(request, response);
    }

    protected void sslConnectorCerts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("certList", getConnectorCerts());
        getServletContext().getRequestDispatcher(connectorCertsJspPath).forward(request, response);
    }

    protected void sslConnectorTrustedCerts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("trustedCertList", getConnectorTrustedCerts());
        getServletContext().getRequestDispatcher(connectorTrustedCertsJspPath).forward(request, response);
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public String getServletInfo() {
        return "HTMLManagerServlet, Copyright (c) 1999-2018, The Apache Software Foundation";
    }

    @Override // org.apache.catalina.manager.ManagerServlet, javax.servlet.GenericServlet
    public void init() throws ServletException {
        super.init();
        String value = getServletConfig().getInitParameter("showProxySessions");
        this.showProxySessions = Boolean.parseBoolean(value);
    }

    protected String expireSessions(ContextName cn, HttpServletRequest req, StringManager smClient) {
        int idle = -1;
        String idleParam = req.getParameter("idle");
        if (idleParam != null) {
            try {
                idle = Integer.parseInt(idleParam);
            } catch (NumberFormatException e) {
                log("Could not parse idle parameter to an int: " + idleParam);
            }
        }
        return sessions(cn, idle, smClient);
    }

    protected void doSessions(ContextName cn, HttpServletRequest req, HttpServletResponse resp, StringManager smClient) throws ServletException, IOException {
        req.setAttribute("path", cn.getPath());
        req.setAttribute(XMLDeclaration.ATTRIBUTE_NAME_VERSION, cn.getVersion());
        String action = req.getParameter("action");
        if (this.debug >= 1) {
            log("sessions: Session action '" + action + "' for web application '" + cn.getDisplayName() + "'");
        }
        if ("sessionDetail".equals(action)) {
            displaySessionDetailPage(req, resp, cn, req.getParameter("sessionId"), smClient);
            return;
        }
        if ("invalidateSessions".equals(action)) {
            String[] sessionIds = req.getParameterValues("sessionIds");
            int i = invalidateSessions(cn, sessionIds, smClient);
            req.setAttribute("message", "" + i + " sessions invalidated.");
        } else if ("removeSessionAttribute".equals(action)) {
            String sessionId = req.getParameter("sessionId");
            String name = req.getParameter("attributeName");
            boolean removed = removeSessionAttribute(cn, sessionId, name, smClient);
            String outMessage = removed ? "Session attribute '" + name + "' removed." : "Session did not contain any attribute named '" + name + "'";
            req.setAttribute("message", outMessage);
            displaySessionDetailPage(req, resp, cn, sessionId, smClient);
            return;
        }
        displaySessionsListPage(cn, req, resp, smClient);
    }

    protected List<Session> getSessionsForName(ContextName cn, StringManager smClient) {
        if (cn == null || (!cn.getPath().startsWith("/") && !cn.getPath().equals(""))) {
            String path = null;
            if (cn != null) {
                path = cn.getPath();
            }
            throw new IllegalArgumentException(smClient.getString("managerServlet.invalidPath", Escape.htmlElementContent(path)));
        }
        Context ctxt = (Context) this.host.findChild(cn.getName());
        if (null == ctxt) {
            throw new IllegalArgumentException(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(cn.getDisplayName())));
        }
        Manager manager = ctxt.getManager();
        List<Session> sessions = new ArrayList<>();
        sessions.addAll(Arrays.asList(manager.findSessions()));
        if ((manager instanceof DistributedManager) && this.showProxySessions) {
            Set<String> sessionIds = ((DistributedManager) manager).getSessionIdsFull();
            for (Session session : sessions) {
                sessionIds.remove(session.getId());
            }
            for (String sessionId : sessionIds) {
                sessions.add(new DummyProxySession(sessionId));
            }
        }
        return sessions;
    }

    protected Session getSessionForNameAndId(ContextName cn, String id, StringManager smClient) {
        List<Session> sessions = getSessionsForName(cn, smClient);
        if (sessions.isEmpty()) {
            return null;
        }
        for (Session session : sessions) {
            if (session.getId().equals(id)) {
                return session;
            }
        }
        return null;
    }

    protected void displaySessionsListPage(ContextName cn, HttpServletRequest req, HttpServletResponse resp, StringManager smClient) throws ServletException, IOException {
        List<Session> sessions = getSessionsForName(cn, smClient);
        String sortBy = req.getParameter("sort");
        String orderBy = null;
        if (null != sortBy && !"".equals(sortBy.trim())) {
            Comparator<Session> comparator = getComparator(sortBy);
            if (comparator != null) {
                if ("DESC".equalsIgnoreCase(req.getParameter("order"))) {
                    comparator = Collections.reverseOrder(comparator);
                    orderBy = "ASC";
                } else {
                    orderBy = "DESC";
                }
                try {
                    Collections.sort(sessions, comparator);
                } catch (IllegalStateException e) {
                    req.setAttribute(APPLICATION_ERROR, "Can't sort session list: one session is invalidated");
                }
            } else {
                log("WARNING: unknown sort order: " + sortBy);
            }
        }
        req.setAttribute("sort", sortBy);
        req.setAttribute("order", orderBy);
        req.setAttribute("activeSessions", sessions);
        resp.setHeader(HttpHeaders.PRAGMA, "No-cache");
        resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache,no-store,max-age=0");
        resp.setDateHeader(HttpHeaders.EXPIRES, 0L);
        getServletContext().getRequestDispatcher(sessionsListJspPath).include(req, resp);
    }

    protected void displaySessionDetailPage(HttpServletRequest req, HttpServletResponse resp, ContextName cn, String sessionId, StringManager smClient) throws ServletException, IOException {
        Session session = getSessionForNameAndId(cn, sessionId, smClient);
        resp.setHeader(HttpHeaders.PRAGMA, "No-cache");
        resp.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache,no-store,max-age=0");
        resp.setDateHeader(HttpHeaders.EXPIRES, 0L);
        req.setAttribute("currentSession", session);
        getServletContext().getRequestDispatcher(resp.encodeURL(sessionDetailJspPath)).include(req, resp);
    }

    protected int invalidateSessions(ContextName cn, String[] sessionIds, StringManager smClient) {
        if (null == sessionIds) {
            return 0;
        }
        int nbAffectedSessions = 0;
        for (String sessionId : sessionIds) {
            HttpSession session = getSessionForNameAndId(cn, sessionId, smClient).getSession();
            if (null == session) {
                if (this.debug >= 1) {
                    log("WARNING: can't invalidate null session " + sessionId);
                }
            } else {
                try {
                    session.invalidate();
                    nbAffectedSessions++;
                    if (this.debug >= 1) {
                        log("Invalidating session id " + sessionId);
                    }
                } catch (IllegalStateException e) {
                    if (this.debug >= 1) {
                        log("Can't invalidate already invalidated session id " + sessionId);
                    }
                }
            }
        }
        return nbAffectedSessions;
    }

    protected boolean removeSessionAttribute(ContextName cn, String sessionId, String attributeName, StringManager smClient) {
        HttpSession session = getSessionForNameAndId(cn, sessionId, smClient).getSession();
        if (null == session) {
            if (this.debug >= 1) {
                log("WARNING: can't remove attribute '" + attributeName + "' for null session " + sessionId);
                return false;
            }
            return false;
        }
        boolean wasPresent = null != session.getAttribute(attributeName);
        try {
            session.removeAttribute(attributeName);
        } catch (IllegalStateException e) {
            if (this.debug >= 1) {
                log("Can't remote attribute '" + attributeName + "' for invalidated session id " + sessionId);
            }
        }
        return wasPresent;
    }

    protected Comparator<Session> getComparator(String sortBy) {
        Comparator<Session> comparator = null;
        if ("CreationTime".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Date>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.1
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Date> getComparableObject(Session session) {
                    return new Date(session.getCreationTime());
                }
            };
        } else if ("id".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<String>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.2
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<String> getComparableObject(Session session) {
                    return session.getId();
                }
            };
        } else if ("LastAccessedTime".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Date>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.3
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Date> getComparableObject(Session session) {
                    return new Date(session.getLastAccessedTime());
                }
            };
        } else if ("MaxInactiveInterval".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Integer>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.4
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Integer> getComparableObject(Session session) {
                    return Integer.valueOf(session.getMaxInactiveInterval());
                }
            };
        } else if ("new".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Boolean>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.5
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Boolean> getComparableObject(Session session) {
                    return Boolean.valueOf(session.getSession().isNew());
                }
            };
        } else if ("locale".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<String>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.6
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<String> getComparableObject(Session session) {
                    return JspHelper.guessDisplayLocaleFromSession(session);
                }
            };
        } else if (ClassicConstants.USER_MDC_KEY.equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<String>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.7
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<String> getComparableObject(Session session) {
                    return JspHelper.guessDisplayUserFromSession(session);
                }
            };
        } else if ("UsedTime".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Date>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.8
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Date> getComparableObject(Session session) {
                    return new Date(SessionUtils.getUsedTimeForSession(session));
                }
            };
        } else if ("InactiveTime".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Date>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.9
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Date> getComparableObject(Session session) {
                    return new Date(SessionUtils.getInactiveTimeForSession(session));
                }
            };
        } else if ("TTL".equalsIgnoreCase(sortBy)) {
            comparator = new BaseSessionComparator<Date>() { // from class: org.apache.catalina.manager.HTMLManagerServlet.10
                @Override // org.apache.catalina.manager.util.BaseSessionComparator
                public Comparable<Date> getComparableObject(Session session) {
                    return new Date(SessionUtils.getTTLForSession(session));
                }
            };
        }
        return comparator;
    }
}