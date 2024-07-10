package org.apache.catalina.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.WebResource;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.DOMWriter;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.util.XMLWriter;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.ConcurrentDateFormat;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.tags.BindTag;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/WebdavServlet.class */
public class WebdavServlet extends DefaultServlet {
    private static final long serialVersionUID = 1;
    private static final URLEncoder URL_ENCODER_XML = (URLEncoder) URLEncoder.DEFAULT.clone();
    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_PROPPATCH = "PROPPATCH";
    private static final String METHOD_MKCOL = "MKCOL";
    private static final String METHOD_COPY = "COPY";
    private static final String METHOD_MOVE = "MOVE";
    private static final String METHOD_LOCK = "LOCK";
    private static final String METHOD_UNLOCK = "UNLOCK";
    private static final int FIND_BY_PROPERTY = 0;
    private static final int FIND_ALL_PROP = 1;
    private static final int FIND_PROPERTY_NAMES = 2;
    private static final int LOCK_CREATION = 0;
    private static final int LOCK_REFRESH = 1;
    private static final int DEFAULT_TIMEOUT = 3600;
    private static final int MAX_TIMEOUT = 604800;
    protected static final String DEFAULT_NAMESPACE = "DAV:";
    protected static final ConcurrentDateFormat creationDateFormat;
    private final Hashtable<String, LockInfo> resourceLocks = new Hashtable<>();
    private final Hashtable<String, Vector<String>> lockNullResources = new Hashtable<>();
    private final Vector<LockInfo> collectionLocks = new Vector<>();
    private String secret = "catalina";
    private int maxDepth = 3;
    private boolean allowSpecialPaths = false;

    static {
        URL_ENCODER_XML.removeSafeCharacter('&');
        creationDateFormat = new ConcurrentDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US, TimeZone.getTimeZone("GMT"));
    }

    @Override // org.apache.catalina.servlets.DefaultServlet, javax.servlet.GenericServlet
    public void init() throws ServletException {
        super.init();
        if (getServletConfig().getInitParameter("secret") != null) {
            this.secret = getServletConfig().getInitParameter("secret");
        }
        if (getServletConfig().getInitParameter("maxDepth") != null) {
            this.maxDepth = Integer.parseInt(getServletConfig().getInitParameter("maxDepth"));
        }
        if (getServletConfig().getInitParameter("allowSpecialPaths") != null) {
            this.allowSpecialPaths = Boolean.parseBoolean(getServletConfig().getInitParameter("allowSpecialPaths"));
        }
    }

    protected DocumentBuilder getDocumentBuilder() throws ServletException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new WebdavResolver(getServletContext()));
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new ServletException(sm.getString("webdavservlet.jaxpfailed"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, javax.servlet.http.HttpServlet
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRelativePath(req);
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            doGet(req, resp);
        } else if (isSpecialPath(path)) {
            resp.sendError(404);
        } else {
            String method = req.getMethod();
            if (this.debug > 0) {
                log(PropertyAccessor.PROPERTY_KEY_PREFIX + method + "] " + path);
            }
            if (method.equals(METHOD_PROPFIND)) {
                doPropfind(req, resp);
            } else if (method.equals(METHOD_PROPPATCH)) {
                doProppatch(req, resp);
            } else if (method.equals(METHOD_MKCOL)) {
                doMkcol(req, resp);
            } else if (method.equals(METHOD_COPY)) {
                doCopy(req, resp);
            } else if (method.equals(METHOD_MOVE)) {
                doMove(req, resp);
            } else if (method.equals(METHOD_LOCK)) {
                doLock(req, resp);
            } else if (method.equals(METHOD_UNLOCK)) {
                doUnlock(req, resp);
            } else {
                super.service(req, resp);
            }
        }
    }

    private final boolean isSpecialPath(String path) {
        return !this.allowSpecialPaths && (path.toUpperCase(Locale.ENGLISH).startsWith("/WEB-INF") || path.toUpperCase(Locale.ENGLISH).startsWith("/META-INF"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet
    public boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        if (!super.checkIfHeaders(request, response, resource)) {
            return false;
        }
        return true;
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String rewriteUrl(String path) {
        return URL_ENCODER_XML.encode(path, StandardCharsets.UTF_8);
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getRelativePath(HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        String pathInfo;
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            pathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
        } else {
            pathInfo = request.getPathInfo();
        }
        StringBuilder result = new StringBuilder();
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0) {
            result.append('/');
        }
        return result.toString();
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String getPathPrefix(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (request.getServletPath() != null) {
            contextPath = contextPath + request.getServletPath();
        }
        return contextPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, javax.servlet.http.HttpServlet
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("DAV", "1,2");
        resp.addHeader(HttpHeaders.ALLOW, determineMethodsAllowed(req));
        resp.addHeader("MS-Author-Via", "DAV");
    }

    protected void doPropfind(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int slash;
        String propertyName;
        if (!this.listings) {
            sendNotAllowed(req, resp);
            return;
        }
        String path = getRelativePath(req);
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        Vector<String> properties = null;
        int depth = this.maxDepth;
        int type = 1;
        String depthStr = req.getHeader("Depth");
        if (depthStr == null) {
            depth = this.maxDepth;
        } else if (depthStr.equals(CustomBooleanEditor.VALUE_0)) {
            depth = 0;
        } else if (depthStr.equals(CustomBooleanEditor.VALUE_1)) {
            depth = 1;
        } else if (depthStr.equals("infinity")) {
            depth = this.maxDepth;
        }
        Node propNode = null;
        if (req.getContentLengthLong() > 0) {
            DocumentBuilder documentBuilder = getDocumentBuilder();
            try {
                Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
                Element rootElement = document.getDocumentElement();
                NodeList childList = rootElement.getChildNodes();
                for (int i = 0; i < childList.getLength(); i++) {
                    Node currentNode = childList.item(i);
                    switch (currentNode.getNodeType()) {
                        case 1:
                            if (currentNode.getNodeName().endsWith(BeanDefinitionParserDelegate.PROP_ELEMENT)) {
                                type = 0;
                                propNode = currentNode;
                            }
                            if (currentNode.getNodeName().endsWith("propname")) {
                                type = 2;
                            }
                            if (currentNode.getNodeName().endsWith("allprop")) {
                                type = 1;
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            break;
                    }
                }
            } catch (IOException e) {
                resp.sendError(400);
                return;
            } catch (SAXException e2) {
                resp.sendError(400);
                return;
            }
        }
        if (type == 0) {
            properties = new Vector<>();
            NodeList childList2 = propNode.getChildNodes();
            for (int i2 = 0; i2 < childList2.getLength(); i2++) {
                Node currentNode2 = childList2.item(i2);
                switch (currentNode2.getNodeType()) {
                    case 1:
                        String nodeName = currentNode2.getNodeName();
                        if (nodeName.indexOf(58) != -1) {
                            propertyName = nodeName.substring(nodeName.indexOf(58) + 1);
                        } else {
                            propertyName = nodeName;
                        }
                        properties.addElement(propertyName);
                        break;
                }
            }
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists() && (slash = path.lastIndexOf(47)) != -1) {
            String parentPath = path.substring(0, slash);
            Vector<String> currentLockNullResources = this.lockNullResources.get(parentPath);
            if (currentLockNullResources != null) {
                Enumeration<String> lockNullResourcesList = currentLockNullResources.elements();
                while (lockNullResourcesList.hasMoreElements()) {
                    String lockNullPath = lockNullResourcesList.nextElement();
                    if (lockNullPath.equals(path)) {
                        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
                        resp.setContentType("text/xml; charset=UTF-8");
                        XMLWriter generatedXML = new XMLWriter(resp.getWriter());
                        generatedXML.writeXMLHeader();
                        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                        parseLockNullProperties(req, generatedXML, lockNullPath, type, properties);
                        generatedXML.writeElement("D", "multistatus", 1);
                        generatedXML.sendData();
                        return;
                    }
                }
            }
        }
        if (!resource.exists()) {
            resp.sendError(404, path);
            return;
        }
        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
        resp.setContentType("text/xml; charset=UTF-8");
        XMLWriter generatedXML2 = new XMLWriter(resp.getWriter());
        generatedXML2.writeXMLHeader();
        generatedXML2.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        if (depth == 0) {
            parseProperties(req, generatedXML2, path, type, properties);
        } else {
            Stack<String> stack = new Stack<>();
            stack.push(path);
            Stack<String> stackBelow = new Stack<>();
            while (!stack.isEmpty() && depth >= 0) {
                String currentPath = stack.pop();
                parseProperties(req, generatedXML2, currentPath, type, properties);
                if (this.resources.getResource(currentPath).isDirectory() && depth > 0) {
                    String[] entries = this.resources.list(currentPath);
                    for (String entry : entries) {
                        String newPath = currentPath;
                        if (!newPath.endsWith("/")) {
                            newPath = newPath + "/";
                        }
                        stackBelow.push(newPath + entry);
                    }
                    String lockPath = currentPath;
                    if (lockPath.endsWith("/")) {
                        lockPath = lockPath.substring(0, lockPath.length() - 1);
                    }
                    Vector<String> currentLockNullResources2 = this.lockNullResources.get(lockPath);
                    if (currentLockNullResources2 != null) {
                        Enumeration<String> lockNullResourcesList2 = currentLockNullResources2.elements();
                        while (lockNullResourcesList2.hasMoreElements()) {
                            parseLockNullProperties(req, generatedXML2, lockNullResourcesList2.nextElement(), type, properties);
                        }
                    }
                }
                if (stack.isEmpty()) {
                    depth--;
                    stack = stackBelow;
                    stackBelow = new Stack<>();
                }
                generatedXML2.sendData();
            }
        }
        generatedXML2.writeElement("D", "multistatus", 1);
        generatedXML2.sendData();
    }

    protected void doProppatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            resp.sendError(501);
        }
    }

    protected void doMkcol(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            sendNotAllowed(req, resp);
        } else if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else if (req.getContentLengthLong() > 0) {
            DocumentBuilder documentBuilder = getDocumentBuilder();
            try {
                documentBuilder.parse(new InputSource(req.getInputStream()));
                resp.sendError(501);
            } catch (SAXException e) {
                resp.sendError(415);
            }
        } else if (this.resources.mkdir(path)) {
            resp.setStatus(201);
            this.lockNullResources.remove(path);
        } else {
            resp.sendError(409, WebdavStatus.getStatusText(409));
        }
    }

    @Override // org.apache.catalina.servlets.DefaultServlet, javax.servlet.http.HttpServlet
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            sendNotAllowed(req, resp);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            deleteResource(req, resp);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.servlets.DefaultServlet, javax.servlet.http.HttpServlet
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return;
        }
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.isDirectory()) {
            sendNotAllowed(req, resp);
            return;
        }
        super.doPut(req, resp);
        this.lockNullResources.remove(path);
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else {
            copyResource(req, resp);
        }
    }

    protected void doMove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            String path = getRelativePath(req);
            if (copyResource(req, resp)) {
                deleteResource(path, req, resp, false);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v132, types: [byte[], byte[][]] */
    protected void doLock(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int lockDuration;
        if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            LockInfo lock = new LockInfo(this.maxDepth);
            String depthStr = req.getHeader("Depth");
            if (depthStr == null) {
                lock.depth = this.maxDepth;
            } else if (depthStr.equals(CustomBooleanEditor.VALUE_0)) {
                lock.depth = 0;
            } else {
                lock.depth = this.maxDepth;
            }
            String lockDurationStr = req.getHeader("Timeout");
            if (lockDurationStr == null) {
                lockDuration = DEFAULT_TIMEOUT;
            } else {
                int commaPos = lockDurationStr.indexOf(44);
                if (commaPos != -1) {
                    lockDurationStr = lockDurationStr.substring(0, commaPos);
                }
                if (lockDurationStr.startsWith("Second-")) {
                    lockDuration = Integer.parseInt(lockDurationStr.substring(7));
                } else if (lockDurationStr.equalsIgnoreCase("infinity")) {
                    lockDuration = MAX_TIMEOUT;
                } else {
                    try {
                        lockDuration = Integer.parseInt(lockDurationStr);
                    } catch (NumberFormatException e) {
                        lockDuration = MAX_TIMEOUT;
                    }
                }
                if (lockDuration == 0) {
                    lockDuration = DEFAULT_TIMEOUT;
                }
                if (lockDuration > MAX_TIMEOUT) {
                    lockDuration = MAX_TIMEOUT;
                }
            }
            lock.expiresAt = System.currentTimeMillis() + (lockDuration * 1000);
            int lockRequestType = 0;
            Node lockInfoNode = null;
            DocumentBuilder documentBuilder = getDocumentBuilder();
            try {
                Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
                Node rootElement = document.getDocumentElement();
                lockInfoNode = rootElement;
            } catch (IOException e2) {
                lockRequestType = 1;
            } catch (SAXException e3) {
                lockRequestType = 1;
            }
            if (lockInfoNode != null) {
                NodeList childList = lockInfoNode.getChildNodes();
                Node lockScopeNode = null;
                Node lockTypeNode = null;
                Node lockOwnerNode = null;
                for (int i = 0; i < childList.getLength(); i++) {
                    Node currentNode = childList.item(i);
                    switch (currentNode.getNodeType()) {
                        case 1:
                            String nodeName = currentNode.getNodeName();
                            if (nodeName.endsWith("lockscope")) {
                                lockScopeNode = currentNode;
                            }
                            if (nodeName.endsWith("locktype")) {
                                lockTypeNode = currentNode;
                            }
                            if (nodeName.endsWith("owner")) {
                                lockOwnerNode = currentNode;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                if (lockScopeNode != null) {
                    NodeList childList2 = lockScopeNode.getChildNodes();
                    for (int i2 = 0; i2 < childList2.getLength(); i2++) {
                        Node currentNode2 = childList2.item(i2);
                        switch (currentNode2.getNodeType()) {
                            case 1:
                                String tempScope = currentNode2.getNodeName();
                                if (tempScope.indexOf(58) != -1) {
                                    lock.scope = tempScope.substring(tempScope.indexOf(58) + 1);
                                    break;
                                } else {
                                    lock.scope = tempScope;
                                    break;
                                }
                        }
                    }
                    if (lock.scope == null) {
                        resp.setStatus(400);
                    }
                } else {
                    resp.setStatus(400);
                }
                if (lockTypeNode != null) {
                    NodeList childList3 = lockTypeNode.getChildNodes();
                    for (int i3 = 0; i3 < childList3.getLength(); i3++) {
                        Node currentNode3 = childList3.item(i3);
                        switch (currentNode3.getNodeType()) {
                            case 1:
                                String tempType = currentNode3.getNodeName();
                                if (tempType.indexOf(58) != -1) {
                                    lock.type = tempType.substring(tempType.indexOf(58) + 1);
                                    break;
                                } else {
                                    lock.type = tempType;
                                    break;
                                }
                        }
                    }
                    if (lock.type == null) {
                        resp.setStatus(400);
                    }
                } else {
                    resp.setStatus(400);
                }
                if (lockOwnerNode != null) {
                    NodeList childList4 = lockOwnerNode.getChildNodes();
                    for (int i4 = 0; i4 < childList4.getLength(); i4++) {
                        Node currentNode4 = childList4.item(i4);
                        switch (currentNode4.getNodeType()) {
                            case 1:
                                StringWriter strWriter = new StringWriter();
                                DOMWriter domWriter = new DOMWriter(strWriter);
                                domWriter.print(currentNode4);
                                lock.owner += strWriter.toString();
                                break;
                            case 3:
                                lock.owner += currentNode4.getNodeValue();
                                break;
                        }
                    }
                    if (lock.owner == null) {
                        resp.setStatus(400);
                    }
                } else {
                    lock.owner = "";
                }
            }
            String path = getRelativePath(req);
            lock.path = path;
            WebResource resource = this.resources.getResource(path);
            if (lockRequestType == 0) {
                String lockTokenStr = req.getServletPath() + "-" + lock.type + "-" + lock.scope + "-" + req.getUserPrincipal() + "-" + lock.depth + "-" + lock.owner + "-" + lock.tokens + "-" + lock.expiresAt + "-" + System.currentTimeMillis() + "-" + this.secret;
                String lockToken = MD5Encoder.encode(ConcurrentMessageDigest.digestMD5(new byte[]{lockTokenStr.getBytes(StandardCharsets.ISO_8859_1)}));
                if (resource.isDirectory() && lock.depth == this.maxDepth) {
                    Vector<String> lockPaths = new Vector<>();
                    Enumeration<LockInfo> locksList = this.collectionLocks.elements();
                    while (locksList.hasMoreElements()) {
                        LockInfo currentLock = locksList.nextElement();
                        if (currentLock.hasExpired()) {
                            this.resourceLocks.remove(currentLock.path);
                        } else if (currentLock.path.startsWith(lock.path) && (currentLock.isExclusive() || lock.isExclusive())) {
                            lockPaths.addElement(currentLock.path);
                        }
                    }
                    Enumeration<LockInfo> locksList2 = this.resourceLocks.elements();
                    while (locksList2.hasMoreElements()) {
                        LockInfo currentLock2 = locksList2.nextElement();
                        if (currentLock2.hasExpired()) {
                            this.resourceLocks.remove(currentLock2.path);
                        } else if (currentLock2.path.startsWith(lock.path) && (currentLock2.isExclusive() || lock.isExclusive())) {
                            lockPaths.addElement(currentLock2.path);
                        }
                    }
                    if (!lockPaths.isEmpty()) {
                        Enumeration<String> lockPathsList = lockPaths.elements();
                        resp.setStatus(409);
                        XMLWriter generatedXML = new XMLWriter();
                        generatedXML.writeXMLHeader();
                        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
                        while (lockPathsList.hasMoreElements()) {
                            generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 0);
                            generatedXML.writeElement("D", "href", 0);
                            generatedXML.writeText(lockPathsList.nextElement());
                            generatedXML.writeElement("D", "href", 1);
                            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                            generatedXML.writeText("HTTP/1.1 423 " + WebdavStatus.getStatusText(WebdavStatus.SC_LOCKED));
                            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                            generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 1);
                        }
                        generatedXML.writeElement("D", "multistatus", 1);
                        Writer writer = resp.getWriter();
                        writer.write(generatedXML.toString());
                        writer.close();
                        return;
                    }
                    boolean addLock = true;
                    Enumeration<LockInfo> locksList3 = this.collectionLocks.elements();
                    while (locksList3.hasMoreElements()) {
                        LockInfo currentLock3 = locksList3.nextElement();
                        if (currentLock3.path.equals(lock.path)) {
                            if (currentLock3.isExclusive()) {
                                resp.sendError(WebdavStatus.SC_LOCKED);
                                return;
                            } else if (lock.isExclusive()) {
                                resp.sendError(WebdavStatus.SC_LOCKED);
                                return;
                            } else {
                                currentLock3.tokens.addElement(lockToken);
                                lock = currentLock3;
                                addLock = false;
                            }
                        }
                    }
                    if (addLock) {
                        lock.tokens.addElement(lockToken);
                        this.collectionLocks.addElement(lock);
                    }
                } else {
                    LockInfo presentLock = this.resourceLocks.get(lock.path);
                    if (presentLock != null) {
                        if (presentLock.isExclusive() || lock.isExclusive()) {
                            resp.sendError(412);
                            return;
                        } else {
                            presentLock.tokens.addElement(lockToken);
                            lock = presentLock;
                        }
                    } else {
                        lock.tokens.addElement(lockToken);
                        this.resourceLocks.put(lock.path, lock);
                        if (!resource.exists()) {
                            int slash = lock.path.lastIndexOf(47);
                            String parentPath = lock.path.substring(0, slash);
                            Vector<String> lockNulls = this.lockNullResources.get(parentPath);
                            if (lockNulls == null) {
                                lockNulls = new Vector<>();
                                this.lockNullResources.put(parentPath, lockNulls);
                            }
                            lockNulls.addElement(lock.path);
                        }
                        resp.addHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");
                    }
                }
            }
            if (lockRequestType == 1) {
                String ifHeader = req.getHeader("If");
                if (ifHeader == null) {
                    ifHeader = "";
                }
                LockInfo toRenew = this.resourceLocks.get(path);
                if (toRenew != null) {
                    Enumeration<String> tokenList = toRenew.tokens.elements();
                    while (tokenList.hasMoreElements()) {
                        String token = tokenList.nextElement();
                        if (ifHeader.contains(token)) {
                            toRenew.expiresAt = lock.expiresAt;
                            lock = toRenew;
                        }
                    }
                }
                Enumeration<LockInfo> collectionLocksList = this.collectionLocks.elements();
                while (collectionLocksList.hasMoreElements()) {
                    LockInfo toRenew2 = collectionLocksList.nextElement();
                    if (path.equals(toRenew2.path)) {
                        Enumeration<String> tokenList2 = toRenew2.tokens.elements();
                        while (tokenList2.hasMoreElements()) {
                            String token2 = tokenList2.nextElement();
                            if (ifHeader.contains(token2)) {
                                toRenew2.expiresAt = lock.expiresAt;
                                lock = toRenew2;
                            }
                        }
                    }
                }
            }
            XMLWriter generatedXML2 = new XMLWriter();
            generatedXML2.writeXMLHeader();
            generatedXML2.writeElement("D", DEFAULT_NAMESPACE, BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
            generatedXML2.writeElement("D", "lockdiscovery", 0);
            lock.toXML(generatedXML2);
            generatedXML2.writeElement("D", "lockdiscovery", 1);
            generatedXML2.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
            resp.setStatus(200);
            resp.setContentType("text/xml; charset=UTF-8");
            Writer writer2 = resp.getWriter();
            writer2.write(generatedXML2.toString());
            writer2.close();
        }
    }

    protected void doUnlock(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (this.readOnly) {
            resp.sendError(403);
        } else if (isLocked(req)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
        } else {
            String path = getRelativePath(req);
            String lockTokenHeader = req.getHeader("Lock-Token");
            if (lockTokenHeader == null) {
                lockTokenHeader = "";
            }
            LockInfo lock = this.resourceLocks.get(path);
            if (lock != null) {
                Enumeration<String> tokenList = lock.tokens.elements();
                while (tokenList.hasMoreElements()) {
                    String token = tokenList.nextElement();
                    if (lockTokenHeader.contains(token)) {
                        lock.tokens.removeElement(token);
                    }
                }
                if (lock.tokens.isEmpty()) {
                    this.resourceLocks.remove(path);
                    this.lockNullResources.remove(path);
                }
            }
            Enumeration<LockInfo> collectionLocksList = this.collectionLocks.elements();
            while (collectionLocksList.hasMoreElements()) {
                LockInfo lock2 = collectionLocksList.nextElement();
                if (path.equals(lock2.path)) {
                    Enumeration<String> tokenList2 = lock2.tokens.elements();
                    while (true) {
                        if (!tokenList2.hasMoreElements()) {
                            break;
                        }
                        String token2 = tokenList2.nextElement();
                        if (lockTokenHeader.contains(token2)) {
                            lock2.tokens.removeElement(token2);
                            break;
                        }
                    }
                    if (lock2.tokens.isEmpty()) {
                        this.collectionLocks.removeElement(lock2);
                        this.lockNullResources.remove(path);
                    }
                }
            }
            resp.setStatus(204);
        }
    }

    private boolean isLocked(HttpServletRequest req) {
        String path = getRelativePath(req);
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        return isLocked(path, ifHeader + lockTokenHeader);
    }

    private boolean isLocked(String path, String ifHeader) {
        LockInfo lock = this.resourceLocks.get(path);
        if (lock != null && lock.hasExpired()) {
            this.resourceLocks.remove(path);
        } else if (lock != null) {
            Enumeration<String> tokenList = lock.tokens.elements();
            boolean tokenMatch = false;
            while (true) {
                if (!tokenList.hasMoreElements()) {
                    break;
                }
                String token = tokenList.nextElement();
                if (ifHeader.contains(token)) {
                    tokenMatch = true;
                    break;
                }
            }
            if (!tokenMatch) {
                return true;
            }
        }
        Enumeration<LockInfo> collectionLocksList = this.collectionLocks.elements();
        while (collectionLocksList.hasMoreElements()) {
            LockInfo lock2 = collectionLocksList.nextElement();
            if (lock2.hasExpired()) {
                this.collectionLocks.removeElement(lock2);
            } else if (path.startsWith(lock2.path)) {
                Enumeration<String> tokenList2 = lock2.tokens.elements();
                boolean tokenMatch2 = false;
                while (true) {
                    if (!tokenList2.hasMoreElements()) {
                        break;
                    }
                    String token2 = tokenList2.nextElement();
                    if (ifHeader.contains(token2)) {
                        tokenMatch2 = true;
                        break;
                    }
                }
                if (!tokenMatch2) {
                    return true;
                }
            } else {
                continue;
            }
        }
        return false;
    }

    private boolean copyResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String servletPath;
        String destinationPath = req.getHeader("Destination");
        if (destinationPath == null) {
            resp.sendError(400);
            return false;
        }
        String destinationPath2 = UDecoder.URLDecode(destinationPath, StandardCharsets.UTF_8);
        int protocolIndex = destinationPath2.indexOf("://");
        if (protocolIndex >= 0) {
            int firstSeparator = destinationPath2.indexOf(47, protocolIndex + 4);
            if (firstSeparator < 0) {
                destinationPath2 = "/";
            } else {
                destinationPath2 = destinationPath2.substring(firstSeparator);
            }
        } else {
            String hostName = req.getServerName();
            if (hostName != null && destinationPath2.startsWith(hostName)) {
                destinationPath2 = destinationPath2.substring(hostName.length());
            }
            int portIndex = destinationPath2.indexOf(58);
            if (portIndex >= 0) {
                destinationPath2 = destinationPath2.substring(portIndex);
            }
            if (destinationPath2.startsWith(":")) {
                int firstSeparator2 = destinationPath2.indexOf(47);
                if (firstSeparator2 < 0) {
                    destinationPath2 = "/";
                } else {
                    destinationPath2 = destinationPath2.substring(firstSeparator2);
                }
            }
        }
        String destinationPath3 = RequestUtil.normalize(destinationPath2);
        String contextPath = req.getContextPath();
        if (contextPath != null && destinationPath3.startsWith(contextPath)) {
            destinationPath3 = destinationPath3.substring(contextPath.length());
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && (servletPath = req.getServletPath()) != null && destinationPath3.startsWith(servletPath)) {
            destinationPath3 = destinationPath3.substring(servletPath.length());
        }
        if (this.debug > 0) {
            log("Dest path :" + destinationPath3);
        }
        if (isSpecialPath(destinationPath3)) {
            resp.sendError(403);
            return false;
        }
        String path = getRelativePath(req);
        if (destinationPath3.equals(path)) {
            resp.sendError(403);
            return false;
        }
        boolean overwrite = true;
        String overwriteHeader = req.getHeader("Overwrite");
        if (overwriteHeader != null) {
            if (overwriteHeader.equalsIgnoreCase("T")) {
                overwrite = true;
            } else {
                overwrite = false;
            }
        }
        WebResource destination = this.resources.getResource(destinationPath3);
        if (overwrite) {
            if (destination.exists()) {
                if (!deleteResource(destinationPath3, req, resp, true)) {
                    return false;
                }
            } else {
                resp.setStatus(201);
            }
        } else if (destination.exists()) {
            resp.sendError(412);
            return false;
        }
        Hashtable<String, Integer> errorList = new Hashtable<>();
        boolean result = copyResource(errorList, path, destinationPath3);
        if (!result || !errorList.isEmpty()) {
            if (errorList.size() == 1) {
                resp.sendError(errorList.elements().nextElement().intValue());
                return false;
            }
            sendReport(req, resp, errorList);
            return false;
        }
        if (destination.exists()) {
            resp.setStatus(204);
        } else {
            resp.setStatus(201);
        }
        this.lockNullResources.remove(destinationPath3);
        return true;
    }

    private boolean copyResource(Hashtable<String, Integer> errorList, String source, String dest) {
        int lastSlash;
        if (this.debug > 1) {
            log("Copy: " + source + " To: " + dest);
        }
        WebResource sourceResource = this.resources.getResource(source);
        if (sourceResource.isDirectory()) {
            if (!this.resources.mkdir(dest) && !this.resources.getResource(dest).isDirectory()) {
                errorList.put(dest, 409);
                return false;
            }
            String[] entries = this.resources.list(source);
            for (String entry : entries) {
                String childDest = dest;
                if (!childDest.equals("/")) {
                    childDest = childDest + "/";
                }
                String childDest2 = childDest + entry;
                String childSrc = source;
                if (!childSrc.equals("/")) {
                    childSrc = childSrc + "/";
                }
                copyResource(errorList, childSrc + entry, childDest2);
            }
            return true;
        } else if (!sourceResource.isFile()) {
            errorList.put(source, 500);
            return false;
        } else {
            WebResource destResource = this.resources.getResource(dest);
            if (!destResource.exists() && !destResource.getWebappPath().endsWith("/") && (lastSlash = destResource.getWebappPath().lastIndexOf(47)) > 0) {
                String parent = destResource.getWebappPath().substring(0, lastSlash);
                WebResource parentResource = this.resources.getResource(parent);
                if (!parentResource.isDirectory()) {
                    errorList.put(source, 409);
                    return false;
                }
            }
            if (!destResource.exists() && dest.endsWith("/") && dest.length() > 1) {
                dest = dest.substring(0, dest.length() - 1);
            }
            try {
                InputStream is = sourceResource.getInputStream();
                if (this.resources.write(dest, is, false)) {
                    if (is != null) {
                        if (0 != 0) {
                            is.close();
                        } else {
                            is.close();
                        }
                    }
                    return true;
                }
                errorList.put(source, 500);
                if (is != null) {
                    if (0 != 0) {
                        is.close();
                    } else {
                        is.close();
                    }
                }
                return false;
            } catch (IOException e) {
                log(sm.getString("webdavservlet.inputstreamclosefail", source), e);
                return true;
            }
        }
    }

    private boolean deleteResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = getRelativePath(req);
        return deleteResource(path, req, resp, true);
    }

    private boolean deleteResource(String path, HttpServletRequest req, HttpServletResponse resp, boolean setStatus) throws IOException {
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        if (isLocked(path, ifHeader + lockTokenHeader)) {
            resp.sendError(WebdavStatus.SC_LOCKED);
            return false;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            resp.sendError(404);
            return false;
        }
        if (!resource.isDirectory()) {
            if (!resource.delete()) {
                resp.sendError(500);
                return false;
            }
        } else {
            Hashtable<String, Integer> errorList = new Hashtable<>();
            deleteCollection(req, path, errorList);
            if (!resource.delete()) {
                errorList.put(path, 500);
            }
            if (!errorList.isEmpty()) {
                sendReport(req, resp, errorList);
                return false;
            }
        }
        if (setStatus) {
            resp.setStatus(204);
            return true;
        }
        return true;
    }

    private void deleteCollection(HttpServletRequest req, String path, Hashtable<String, Integer> errorList) {
        if (this.debug > 1) {
            log("Delete:" + path);
        }
        if (isSpecialPath(path)) {
            errorList.put(path, 403);
            return;
        }
        String ifHeader = req.getHeader("If");
        if (ifHeader == null) {
            ifHeader = "";
        }
        String lockTokenHeader = req.getHeader("Lock-Token");
        if (lockTokenHeader == null) {
            lockTokenHeader = "";
        }
        String[] entries = this.resources.list(path);
        for (String entry : entries) {
            String childName = path;
            if (!childName.equals("/")) {
                childName = childName + "/";
            }
            String childName2 = childName + entry;
            if (isLocked(childName2, ifHeader + lockTokenHeader)) {
                errorList.put(childName2, Integer.valueOf((int) WebdavStatus.SC_LOCKED));
            } else {
                WebResource childResource = this.resources.getResource(childName2);
                if (childResource.isDirectory()) {
                    deleteCollection(req, childName2, errorList);
                }
                if (!childResource.delete() && !childResource.isDirectory()) {
                    errorList.put(childName2, 500);
                }
            }
        }
    }

    private void sendReport(HttpServletRequest req, HttpServletResponse resp, Hashtable<String, Integer> errorList) throws IOException {
        resp.setStatus(WebdavStatus.SC_MULTI_STATUS);
        String absoluteUri = req.getRequestURI();
        String relativePath = getRelativePath(req);
        XMLWriter generatedXML = new XMLWriter();
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("D", DEFAULT_NAMESPACE, "multistatus", 0);
        Enumeration<String> pathList = errorList.keys();
        while (pathList.hasMoreElements()) {
            String errorPath = pathList.nextElement();
            int errorCode = errorList.get(errorPath).intValue();
            generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 0);
            generatedXML.writeElement("D", "href", 0);
            String toAppend = errorPath.substring(relativePath.length());
            if (!toAppend.startsWith("/")) {
                toAppend = "/" + toAppend;
            }
            generatedXML.writeText(absoluteUri + toAppend);
            generatedXML.writeElement("D", "href", 1);
            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
            generatedXML.writeText("HTTP/1.1 " + errorCode + " " + WebdavStatus.getStatusText(errorCode));
            generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
            generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 1);
        }
        generatedXML.writeElement("D", "multistatus", 1);
        Writer writer = resp.getWriter();
        writer.write(generatedXML.toString());
        writer.close();
    }

    private void parseProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, Vector<String> propertiesVector) {
        String href;
        if (isSpecialPath(path)) {
            return;
        }
        WebResource resource = this.resources.getResource(path);
        if (!resource.exists()) {
            return;
        }
        String href2 = req.getContextPath() + req.getServletPath();
        if (href2.endsWith("/") && path.startsWith("/")) {
            href = href2 + path.substring(1);
        } else {
            href = href2 + path;
        }
        if (resource.isDirectory() && !href.endsWith("/")) {
            href = href + "/";
        }
        String rewrittenUrl = rewriteUrl(href);
        generatePropFindResponse(generatedXML, rewrittenUrl, path, type, propertiesVector, resource.isFile(), false, resource.getCreation(), resource.getLastModified(), resource.getContentLength(), getServletContext().getMimeType(resource.getName()), resource.getETag());
    }

    private void parseLockNullProperties(HttpServletRequest req, XMLWriter generatedXML, String path, int type, Vector<String> propertiesVector) {
        LockInfo lock;
        if (isSpecialPath(path) || (lock = this.resourceLocks.get(path)) == null) {
            return;
        }
        String absoluteUri = req.getRequestURI();
        String relativePath = getRelativePath(req);
        String toAppend = path.substring(relativePath.length());
        if (!toAppend.startsWith("/")) {
            toAppend = "/" + toAppend;
        }
        String rewrittenUrl = rewriteUrl(RequestUtil.normalize(absoluteUri + toAppend));
        generatePropFindResponse(generatedXML, rewrittenUrl, path, type, propertiesVector, true, true, lock.creationDate.getTime(), lock.creationDate.getTime(), 0L, "", "");
    }

    private void generatePropFindResponse(XMLWriter generatedXML, String rewrittenUrl, String path, int propFindType, Vector<String> propertiesVector, boolean isFile, boolean isLockNull, long created, long lastModified, long contentLength, String contentType, String eTag) {
        generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 0);
        String status = "HTTP/1.1 200 " + WebdavStatus.getStatusText(200);
        generatedXML.writeElement("D", "href", 0);
        generatedXML.writeText(rewrittenUrl);
        generatedXML.writeElement("D", "href", 1);
        String resourceName = path;
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash != -1) {
            resourceName = resourceName.substring(lastSlash + 1);
        }
        switch (propFindType) {
            case 0:
                Vector<String> propertiesNotFound = new Vector<>();
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                Enumeration<String> properties = propertiesVector.elements();
                while (properties.hasMoreElements()) {
                    String property = properties.nextElement();
                    if (property.equals("creationdate")) {
                        generatedXML.writeProperty("D", "creationdate", getISOCreationDate(created));
                    } else if (property.equals("displayname")) {
                        generatedXML.writeElement("D", "displayname", 0);
                        generatedXML.writeData(resourceName);
                        generatedXML.writeElement("D", "displayname", 1);
                    } else if (property.equals("getcontentlanguage")) {
                        if (isFile) {
                            generatedXML.writeElement("D", "getcontentlanguage", 2);
                        } else {
                            propertiesNotFound.addElement(property);
                        }
                    } else if (property.equals("getcontentlength")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                        } else {
                            propertiesNotFound.addElement(property);
                        }
                    } else if (property.equals("getcontenttype")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getcontenttype", contentType);
                        } else {
                            propertiesNotFound.addElement(property);
                        }
                    } else if (property.equals("getetag")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getetag", eTag);
                        } else {
                            propertiesNotFound.addElement(property);
                        }
                    } else if (property.equals("getlastmodified")) {
                        if (isFile) {
                            generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate(lastModified));
                        } else {
                            propertiesNotFound.addElement(property);
                        }
                    } else if (property.equals("resourcetype")) {
                        if (isFile) {
                            if (isLockNull) {
                                generatedXML.writeElement("D", "resourcetype", 0);
                                generatedXML.writeElement("D", "lock-null", 2);
                                generatedXML.writeElement("D", "resourcetype", 1);
                            } else {
                                generatedXML.writeElement("D", "resourcetype", 2);
                            }
                        } else {
                            generatedXML.writeElement("D", "resourcetype", 0);
                            generatedXML.writeElement("D", "collection", 2);
                            generatedXML.writeElement("D", "resourcetype", 1);
                        }
                    } else if (property.equals("source")) {
                        generatedXML.writeProperty("D", "source", "");
                    } else if (property.equals("supportedlock")) {
                        generatedXML.writeElement("D", "supportedlock", 0);
                        generatedXML.writeText("<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>");
                        generatedXML.writeElement("D", "supportedlock", 1);
                    } else if (property.equals("lockdiscovery")) {
                        if (!generateLockDiscovery(path, generatedXML)) {
                            propertiesNotFound.addElement(property);
                        }
                    } else {
                        propertiesNotFound.addElement(property);
                    }
                }
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                Enumeration<String> propertiesNotFoundList = propertiesNotFound.elements();
                if (propertiesNotFoundList.hasMoreElements()) {
                    String status2 = "HTTP/1.1 404 " + WebdavStatus.getStatusText(404);
                    generatedXML.writeElement("D", "propstat", 0);
                    generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                    while (propertiesNotFoundList.hasMoreElements()) {
                        generatedXML.writeElement("D", propertiesNotFoundList.nextElement(), 2);
                    }
                    generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                    generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                    generatedXML.writeText(status2);
                    generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                    generatedXML.writeElement("D", "propstat", 1);
                    break;
                }
                break;
            case 1:
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                generatedXML.writeProperty("D", "creationdate", getISOCreationDate(created));
                generatedXML.writeElement("D", "displayname", 0);
                generatedXML.writeData(resourceName);
                generatedXML.writeElement("D", "displayname", 1);
                if (isFile) {
                    generatedXML.writeProperty("D", "getlastmodified", FastHttpDateFormat.formatDate(lastModified));
                    generatedXML.writeProperty("D", "getcontentlength", Long.toString(contentLength));
                    if (contentType != null) {
                        generatedXML.writeProperty("D", "getcontenttype", contentType);
                    }
                    generatedXML.writeProperty("D", "getetag", eTag);
                    if (isLockNull) {
                        generatedXML.writeElement("D", "resourcetype", 0);
                        generatedXML.writeElement("D", "lock-null", 2);
                        generatedXML.writeElement("D", "resourcetype", 1);
                    } else {
                        generatedXML.writeElement("D", "resourcetype", 2);
                    }
                } else {
                    generatedXML.writeElement("D", "resourcetype", 0);
                    generatedXML.writeElement("D", "collection", 2);
                    generatedXML.writeElement("D", "resourcetype", 1);
                }
                generatedXML.writeProperty("D", "source", "");
                generatedXML.writeElement("D", "supportedlock", 0);
                generatedXML.writeText("<D:lockentry><D:lockscope><D:exclusive/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry><D:lockentry><D:lockscope><D:shared/></D:lockscope><D:locktype><D:write/></D:locktype></D:lockentry>");
                generatedXML.writeElement("D", "supportedlock", 1);
                generateLockDiscovery(path, generatedXML);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
            case 2:
                generatedXML.writeElement("D", "propstat", 0);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 0);
                generatedXML.writeElement("D", "creationdate", 2);
                generatedXML.writeElement("D", "displayname", 2);
                if (isFile) {
                    generatedXML.writeElement("D", "getcontentlanguage", 2);
                    generatedXML.writeElement("D", "getcontentlength", 2);
                    generatedXML.writeElement("D", "getcontenttype", 2);
                    generatedXML.writeElement("D", "getetag", 2);
                    generatedXML.writeElement("D", "getlastmodified", 2);
                }
                generatedXML.writeElement("D", "resourcetype", 2);
                generatedXML.writeElement("D", "source", 2);
                generatedXML.writeElement("D", "lockdiscovery", 2);
                generatedXML.writeElement("D", BeanDefinitionParserDelegate.PROP_ELEMENT, 1);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 0);
                generatedXML.writeText(status);
                generatedXML.writeElement("D", BindTag.STATUS_VARIABLE_NAME, 1);
                generatedXML.writeElement("D", "propstat", 1);
                break;
        }
        generatedXML.writeElement("D", StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME, 1);
    }

    private boolean generateLockDiscovery(String path, XMLWriter generatedXML) {
        LockInfo resourceLock = this.resourceLocks.get(path);
        Enumeration<LockInfo> collectionLocksList = this.collectionLocks.elements();
        boolean wroteStart = false;
        if (resourceLock != null) {
            wroteStart = true;
            generatedXML.writeElement("D", "lockdiscovery", 0);
            resourceLock.toXML(generatedXML);
        }
        while (collectionLocksList.hasMoreElements()) {
            LockInfo currentLock = collectionLocksList.nextElement();
            if (path.startsWith(currentLock.path)) {
                if (!wroteStart) {
                    wroteStart = true;
                    generatedXML.writeElement("D", "lockdiscovery", 0);
                }
                currentLock.toXML(generatedXML);
            }
        }
        if (wroteStart) {
            generatedXML.writeElement("D", "lockdiscovery", 1);
            return true;
        }
        return false;
    }

    private String getISOCreationDate(long creationDate) {
        return creationDateFormat.format(new Date(creationDate));
    }

    @Override // org.apache.catalina.servlets.DefaultServlet
    protected String determineMethodsAllowed(HttpServletRequest req) {
        WebResource resource = this.resources.getResource(getRelativePath(req));
        StringBuilder methodsAllowed = new StringBuilder("OPTIONS, GET, POST, HEAD");
        if (!this.readOnly) {
            methodsAllowed.append(", DELETE");
            if (!resource.isDirectory()) {
                methodsAllowed.append(", PUT");
            }
        }
        if ((req instanceof RequestFacade) && ((RequestFacade) req).getAllowTrace()) {
            methodsAllowed.append(", TRACE");
        }
        methodsAllowed.append(", LOCK, UNLOCK, PROPPATCH, COPY, MOVE");
        if (this.listings) {
            methodsAllowed.append(", PROPFIND");
        }
        if (!resource.exists()) {
            methodsAllowed.append(", MKCOL");
        }
        return methodsAllowed.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/WebdavServlet$LockInfo.class */
    public static class LockInfo implements Serializable {
        private static final long serialVersionUID = 1;
        private final int maxDepth;
        String path = "/";
        String type = "write";
        String scope = "exclusive";
        int depth = 0;
        String owner = "";
        Vector<String> tokens = new Vector<>();
        long expiresAt = 0;
        Date creationDate = new Date();

        public LockInfo(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("Type:");
            result.append(this.type);
            result.append("\nScope:");
            result.append(this.scope);
            result.append("\nDepth:");
            result.append(this.depth);
            result.append("\nOwner:");
            result.append(this.owner);
            result.append("\nExpiration:");
            result.append(FastHttpDateFormat.formatDate(this.expiresAt));
            Enumeration<String> tokensList = this.tokens.elements();
            while (tokensList.hasMoreElements()) {
                result.append("\nToken:");
                result.append(tokensList.nextElement());
            }
            result.append("\n");
            return result.toString();
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() > this.expiresAt;
        }

        public boolean isExclusive() {
            return this.scope.equals("exclusive");
        }

        public void toXML(XMLWriter generatedXML) {
            generatedXML.writeElement("D", "activelock", 0);
            generatedXML.writeElement("D", "locktype", 0);
            generatedXML.writeElement("D", this.type, 2);
            generatedXML.writeElement("D", "locktype", 1);
            generatedXML.writeElement("D", "lockscope", 0);
            generatedXML.writeElement("D", this.scope, 2);
            generatedXML.writeElement("D", "lockscope", 1);
            generatedXML.writeElement("D", "depth", 0);
            if (this.depth == this.maxDepth) {
                generatedXML.writeText("Infinity");
            } else {
                generatedXML.writeText(CustomBooleanEditor.VALUE_0);
            }
            generatedXML.writeElement("D", "depth", 1);
            generatedXML.writeElement("D", "owner", 0);
            generatedXML.writeText(this.owner);
            generatedXML.writeElement("D", "owner", 1);
            generatedXML.writeElement("D", "timeout", 0);
            long timeout = (this.expiresAt - System.currentTimeMillis()) / 1000;
            generatedXML.writeText("Second-" + timeout);
            generatedXML.writeElement("D", "timeout", 1);
            generatedXML.writeElement("D", "locktoken", 0);
            Enumeration<String> tokensList = this.tokens.elements();
            while (tokensList.hasMoreElements()) {
                generatedXML.writeElement("D", "href", 0);
                generatedXML.writeText("opaquelocktoken:" + tokensList.nextElement());
                generatedXML.writeElement("D", "href", 1);
            }
            generatedXML.writeElement("D", "locktoken", 1);
            generatedXML.writeElement("D", "activelock", 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/WebdavServlet$WebdavResolver.class */
    public static class WebdavResolver implements EntityResolver {
        private ServletContext context;

        public WebdavResolver(ServletContext theContext) {
            this.context = theContext;
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) {
            this.context.log(DefaultServlet.sm.getString("webdavservlet.enternalEntityIgnored", publicId, systemId));
            return new InputSource(new StringReader("Ignored external entity"));
        }
    }
}