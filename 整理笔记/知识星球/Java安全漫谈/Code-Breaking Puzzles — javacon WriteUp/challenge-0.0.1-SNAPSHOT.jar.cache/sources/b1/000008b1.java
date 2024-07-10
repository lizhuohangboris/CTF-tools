package org.apache.catalina.servlets;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.util.IOTools;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.slf4j.Marker;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.servlet.tags.BindTag;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/CGIServlet.class */
public final class CGIServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    private String cgiPathPrefix = null;
    private String cgiExecutable = "perl";
    private List<String> cgiExecutableArgs = null;
    private String parameterEncoding = System.getProperty("file.encoding", UriEscape.DEFAULT_ENCODING);
    private long stderrTimeout = ExponentialBackOff.DEFAULT_INITIAL_INTERVAL;
    private Pattern envHttpHeadersPattern = Pattern.compile("ACCEPT[-0-9A-Z]*|CACHE-CONTROL|COOKIE|HOST|IF-[-0-9A-Z]*|REFERER|USER-AGENT");
    private final Hashtable<String, String> shellEnv = new Hashtable<>();
    private boolean enableCmdLineArguments = false;
    private static final Log log = LogFactory.getLog(CGIServlet.class);
    private static final StringManager sm = StringManager.getManager(CGIServlet.class);
    private static final Object expandFileLock = new Object();

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.cgiPathPrefix = getServletConfig().getInitParameter("cgiPathPrefix");
        boolean passShellEnvironment = Boolean.parseBoolean(getServletConfig().getInitParameter("passShellEnvironment"));
        if (passShellEnvironment) {
            this.shellEnv.putAll(System.getenv());
        }
        Enumeration<String> e = config.getInitParameterNames();
        while (e.hasMoreElements()) {
            String initParamName = e.nextElement();
            if (initParamName.startsWith("environment-variable-")) {
                if (initParamName.length() == 21) {
                    throw new ServletException(sm.getString("cgiServlet.emptyEnvVarName"));
                }
                this.shellEnv.put(initParamName.substring(21), config.getInitParameter(initParamName));
            }
        }
        if (getServletConfig().getInitParameter("executable") != null) {
            this.cgiExecutable = getServletConfig().getInitParameter("executable");
        }
        if (getServletConfig().getInitParameter("executable-arg-1") != null) {
            List<String> args = new ArrayList<>();
            int i = 1;
            while (true) {
                String arg = getServletConfig().getInitParameter("executable-arg-" + i);
                if (arg == null) {
                    break;
                }
                args.add(arg);
                i++;
            }
            this.cgiExecutableArgs = args;
        }
        if (getServletConfig().getInitParameter("parameterEncoding") != null) {
            this.parameterEncoding = getServletConfig().getInitParameter("parameterEncoding");
        }
        if (getServletConfig().getInitParameter("stderrTimeout") != null) {
            this.stderrTimeout = Long.parseLong(getServletConfig().getInitParameter("stderrTimeout"));
        }
        if (getServletConfig().getInitParameter("envHttpHeaders") != null) {
            this.envHttpHeadersPattern = Pattern.compile(getServletConfig().getInitParameter("envHttpHeaders"));
        }
        if (getServletConfig().getInitParameter("enableCmdLineArguments") != null) {
            this.enableCmdLineArguments = Boolean.parseBoolean(config.getInitParameter("enableCmdLineArguments"));
        }
    }

    private void printServletEnvironment(HttpServletRequest req) throws IOException {
        String[] parameterValues;
        log.trace("ServletRequest Properties");
        Enumeration<String> attrs = req.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String attr = attrs.nextElement();
            log.trace("Request Attribute: " + attr + ": [ " + req.getAttribute(attr) + "]");
        }
        log.trace("Character Encoding: [" + req.getCharacterEncoding() + "]");
        log.trace("Content Length: [" + req.getContentLengthLong() + "]");
        log.trace("Content Type: [" + req.getContentType() + "]");
        Enumeration<Locale> locales = req.getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            log.trace("Locale: [" + locale + "]");
        }
        Enumeration<String> params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            for (String value : req.getParameterValues(param)) {
                log.trace("Request Parameter: " + param + ":  [" + value + "]");
            }
        }
        log.trace("Protocol: [" + req.getProtocol() + "]");
        log.trace("Remote Address: [" + req.getRemoteAddr() + "]");
        log.trace("Remote Host: [" + req.getRemoteHost() + "]");
        log.trace("Scheme: [" + req.getScheme() + "]");
        log.trace("Secure: [" + req.isSecure() + "]");
        log.trace("Server Name: [" + req.getServerName() + "]");
        log.trace("Server Port: [" + req.getServerPort() + "]");
        log.trace("HttpServletRequest Properties");
        log.trace("Auth Type: [" + req.getAuthType() + "]");
        log.trace("Context Path: [" + req.getContextPath() + "]");
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.trace("Cookie: " + cookie.getName() + ": [" + cookie.getValue() + "]");
            }
        }
        Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            log.trace("HTTP Header: " + header + ": [" + req.getHeader(header) + "]");
        }
        log.trace("Method: [" + req.getMethod() + "]");
        log.trace("Path Info: [" + req.getPathInfo() + "]");
        log.trace("Path Translated: [" + req.getPathTranslated() + "]");
        log.trace("Query String: [" + req.getQueryString() + "]");
        log.trace("Remote User: [" + req.getRemoteUser() + "]");
        log.trace("Requested Session ID: [" + req.getRequestedSessionId() + "]");
        log.trace("Requested Session ID From Cookie: [" + req.isRequestedSessionIdFromCookie() + "]");
        log.trace("Requested Session ID From URL: [" + req.isRequestedSessionIdFromURL() + "]");
        log.trace("Requested Session ID Valid: [" + req.isRequestedSessionIdValid() + "]");
        log.trace("Request URI: [" + req.getRequestURI() + "]");
        log.trace("Servlet Path: [" + req.getServletPath() + "]");
        log.trace("User Principal: [" + req.getUserPrincipal() + "]");
        HttpSession session = req.getSession(false);
        if (session != null) {
            log.trace("HttpSession Properties");
            log.trace("ID: [" + session.getId() + "]");
            log.trace("Creation Time: [" + new Date(session.getCreationTime()) + "]");
            log.trace("Last Accessed Time: [" + new Date(session.getLastAccessedTime()) + "]");
            log.trace("Max Inactive Interval: [" + session.getMaxInactiveInterval() + "]");
            Enumeration<String> attrs2 = session.getAttributeNames();
            while (attrs2.hasMoreElements()) {
                String attr2 = attrs2.nextElement();
                log.trace("Session Attribute: " + attr2 + ": [" + session.getAttribute(attr2) + "]");
            }
        }
        log.trace("ServletConfig Properties");
        log.trace("Servlet Name: [" + getServletConfig().getServletName() + "]");
        Enumeration<String> params2 = getServletConfig().getInitParameterNames();
        while (params2.hasMoreElements()) {
            String param2 = params2.nextElement();
            String value2 = getServletConfig().getInitParameter(param2);
            log.trace("Servlet Init Param: " + param2 + ": [" + value2 + "]");
        }
        log.trace("ServletContext Properties");
        log.trace("Major Version: [" + getServletContext().getMajorVersion() + "]");
        log.trace("Minor Version: [" + getServletContext().getMinorVersion() + "]");
        log.trace("Real Path for '/': [" + getServletContext().getRealPath("/") + "]");
        log.trace("Server Info: [" + getServletContext().getServerInfo() + "]");
        log.trace("ServletContext Initialization Parameters");
        Enumeration<String> params3 = getServletContext().getInitParameterNames();
        while (params3.hasMoreElements()) {
            String param3 = params3.nextElement();
            String value3 = getServletContext().getInitParameter(param3);
            log.trace("Servlet Context Init Param: " + param3 + ": [" + value3 + "]");
        }
        log.trace("ServletContext Attributes");
        Enumeration<String> attrs3 = getServletContext().getAttributeNames();
        while (attrs3.hasMoreElements()) {
            String attr3 = attrs3.nextElement();
            log.trace("Servlet Context Attribute: " + attr3 + ": [" + getServletContext().getAttribute(attr3) + "]");
        }
    }

    @Override // javax.servlet.http.HttpServlet
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }

    @Override // javax.servlet.http.HttpServlet
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        CGIEnvironment cgiEnv = new CGIEnvironment(req, getServletContext());
        if (cgiEnv.isValid()) {
            CGIRunner cgi = new CGIRunner(cgiEnv.getCommand(), cgiEnv.getEnvironment(), cgiEnv.getWorkingDirectory(), cgiEnv.getParameters());
            if (WebContentGenerator.METHOD_POST.equals(req.getMethod())) {
                cgi.setInput(req.getInputStream());
            }
            cgi.setResponse(res);
            cgi.run();
        } else {
            res.sendError(404);
        }
        if (log.isTraceEnabled()) {
            String[] cgiEnvLines = cgiEnv.toString().split(System.lineSeparator());
            for (String cgiEnvLine : cgiEnvLines) {
                log.trace(cgiEnvLine);
            }
            printServletEnvironment(req);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setStatus(HttpServletResponse response, int status) throws IOException {
        if (status >= 400) {
            response.sendError(status);
            return true;
        }
        response.setStatus(status);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/CGIServlet$CGIEnvironment.class */
    public class CGIEnvironment {
        private final File workingDirectory;
        private final boolean valid;
        private ServletContext context = null;
        private String contextPath = null;
        private String servletPath = null;
        private String pathInfo = null;
        private String webAppRootDir = null;
        private File tmpDir = null;
        private Hashtable<String, String> env = null;
        private String command = null;
        private final ArrayList<String> cmdLineParameters = new ArrayList<>();

        protected CGIEnvironment(HttpServletRequest req, ServletContext context) throws IOException {
            setupFromContext(context);
            setupFromRequest(req);
            this.valid = setCGIEnvironment(req);
            if (this.valid) {
                this.workingDirectory = new File(this.command.substring(0, this.command.lastIndexOf(File.separator)));
            } else {
                this.workingDirectory = null;
            }
        }

        protected void setupFromContext(ServletContext context) {
            this.context = context;
            this.webAppRootDir = context.getRealPath("/");
            this.tmpDir = (File) context.getAttribute("javax.servlet.context.tempdir");
        }

        protected void setupFromRequest(HttpServletRequest req) throws UnsupportedEncodingException {
            String qs;
            boolean isIncluded = false;
            if (req.getAttribute("javax.servlet.include.request_uri") != null) {
                isIncluded = true;
            }
            if (isIncluded) {
                this.contextPath = (String) req.getAttribute("javax.servlet.include.context_path");
                this.servletPath = (String) req.getAttribute("javax.servlet.include.servlet_path");
                this.pathInfo = (String) req.getAttribute("javax.servlet.include.path_info");
            } else {
                this.contextPath = req.getContextPath();
                this.servletPath = req.getServletPath();
                this.pathInfo = req.getPathInfo();
            }
            if (this.pathInfo == null) {
                this.pathInfo = this.servletPath;
            }
            if (CGIServlet.this.enableCmdLineArguments) {
                if (req.getMethod().equals("GET") || req.getMethod().equals(WebContentGenerator.METHOD_POST) || req.getMethod().equals(WebContentGenerator.METHOD_HEAD)) {
                    if (isIncluded) {
                        qs = (String) req.getAttribute("javax.servlet.include.query_string");
                    } else {
                        qs = req.getQueryString();
                    }
                    if (qs != null && qs.indexOf(61) == -1) {
                        StringTokenizer qsTokens = new StringTokenizer(qs, Marker.ANY_NON_NULL_MARKER);
                        while (qsTokens.hasMoreTokens()) {
                            this.cmdLineParameters.add(URLDecoder.decode(qsTokens.nextToken(), CGIServlet.this.parameterEncoding));
                        }
                    }
                }
            }
        }

        protected String[] findCGI(String pathInfo, String webAppRootDir, String contextPath, String servletPath, String cgiPathPrefix) {
            String scriptname;
            if (webAppRootDir != null && webAppRootDir.lastIndexOf(File.separator) == webAppRootDir.length() - 1) {
                webAppRootDir = webAppRootDir.substring(0, webAppRootDir.length() - 1);
            }
            if (cgiPathPrefix != null) {
                webAppRootDir = webAppRootDir + File.separator + cgiPathPrefix;
            }
            if (CGIServlet.log.isDebugEnabled()) {
                CGIServlet.log.debug(CGIServlet.sm.getString("cgiServlet.find.path", pathInfo, webAppRootDir));
            }
            File currentLocation = new File(webAppRootDir);
            StringTokenizer dirWalker = new StringTokenizer(pathInfo, "/");
            if (CGIServlet.log.isDebugEnabled()) {
                CGIServlet.log.debug(CGIServlet.sm.getString("cgiServlet.find.location", currentLocation.getAbsolutePath()));
            }
            StringBuilder cginameBuilder = new StringBuilder();
            while (!currentLocation.isFile() && dirWalker.hasMoreElements()) {
                String nextElement = (String) dirWalker.nextElement();
                currentLocation = new File(currentLocation, nextElement);
                cginameBuilder.append('/').append(nextElement);
                if (CGIServlet.log.isDebugEnabled()) {
                    CGIServlet.log.debug(CGIServlet.sm.getString("cgiServlet.find.location", currentLocation.getAbsolutePath()));
                }
            }
            String cginame = cginameBuilder.toString();
            if (currentLocation.isFile()) {
                String path = currentLocation.getAbsolutePath();
                String name = currentLocation.getName();
                if (servletPath.startsWith(cginame)) {
                    scriptname = contextPath + cginame;
                } else {
                    scriptname = contextPath + servletPath + cginame;
                }
                if (CGIServlet.log.isDebugEnabled()) {
                    CGIServlet.log.debug(CGIServlet.sm.getString("cgiServlet.find.found", name, path, scriptname, cginame));
                }
                return new String[]{path, scriptname, cginame, name};
            }
            return new String[]{null, null, null, null};
        }

        protected boolean setCGIEnvironment(HttpServletRequest req) throws IOException {
            String sPathInfoCGI;
            Hashtable<String, String> envp = new Hashtable<>();
            envp.putAll(CGIServlet.this.shellEnv);
            String sPathTranslatedCGI = null;
            String sPathInfoOrig = this.pathInfo;
            String sPathInfoOrig2 = sPathInfoOrig == null ? "" : sPathInfoOrig;
            if (this.webAppRootDir == null) {
                this.webAppRootDir = this.tmpDir.toString();
                expandCGIScript();
            }
            String[] sCGINames = findCGI(sPathInfoOrig2, this.webAppRootDir, this.contextPath, this.servletPath, CGIServlet.this.cgiPathPrefix);
            String sCGIFullPath = sCGINames[0];
            String sCGIScriptName = sCGINames[1];
            String sCGIFullName = sCGINames[2];
            String sCGIName = sCGINames[3];
            if (sCGIFullPath == null || sCGIScriptName == null || sCGIFullName == null || sCGIName == null) {
                return false;
            }
            envp.put("SERVER_SOFTWARE", "TOMCAT");
            envp.put("SERVER_NAME", nullsToBlanks(req.getServerName()));
            envp.put("GATEWAY_INTERFACE", "CGI/1.1");
            envp.put("SERVER_PROTOCOL", nullsToBlanks(req.getProtocol()));
            int port = req.getServerPort();
            Integer iPort = port == 0 ? -1 : Integer.valueOf(port);
            envp.put("SERVER_PORT", iPort.toString());
            envp.put("REQUEST_METHOD", nullsToBlanks(req.getMethod()));
            envp.put("REQUEST_URI", nullsToBlanks(req.getRequestURI()));
            if (this.pathInfo == null || this.pathInfo.substring(sCGIFullName.length()).length() <= 0) {
                sPathInfoCGI = "";
            } else {
                sPathInfoCGI = this.pathInfo.substring(sCGIFullName.length());
            }
            envp.put("PATH_INFO", sPathInfoCGI);
            if (!"".equals(sPathInfoCGI)) {
                sPathTranslatedCGI = this.context.getRealPath(sPathInfoCGI);
            }
            if (sPathTranslatedCGI != null && !"".equals(sPathTranslatedCGI)) {
                envp.put("PATH_TRANSLATED", nullsToBlanks(sPathTranslatedCGI));
            }
            envp.put("SCRIPT_NAME", nullsToBlanks(sCGIScriptName));
            envp.put("QUERY_STRING", nullsToBlanks(req.getQueryString()));
            envp.put("REMOTE_HOST", nullsToBlanks(req.getRemoteHost()));
            envp.put("REMOTE_ADDR", nullsToBlanks(req.getRemoteAddr()));
            envp.put("AUTH_TYPE", nullsToBlanks(req.getAuthType()));
            envp.put("REMOTE_USER", nullsToBlanks(req.getRemoteUser()));
            envp.put("REMOTE_IDENT", "");
            envp.put("CONTENT_TYPE", nullsToBlanks(req.getContentType()));
            long contentLength = req.getContentLengthLong();
            String sContentLength = contentLength <= 0 ? "" : Long.toString(contentLength);
            envp.put("CONTENT_LENGTH", sContentLength);
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = headers.nextElement().toUpperCase(Locale.ENGLISH);
                if (CGIServlet.this.envHttpHeadersPattern.matcher(header).matches()) {
                    envp.put("HTTP_" + header.replace('-', '_'), req.getHeader(header));
                }
            }
            File fCGIFullPath = new File(sCGIFullPath);
            this.command = fCGIFullPath.getCanonicalPath();
            envp.put("X_TOMCAT_SCRIPT_PATH", this.command);
            envp.put("SCRIPT_FILENAME", this.command);
            this.env = envp;
            return true;
        }

        protected void expandCGIScript() {
            StringBuilder srcPath = new StringBuilder();
            StringBuilder destPath = new StringBuilder();
            InputStream is = null;
            if (CGIServlet.this.cgiPathPrefix == null) {
                srcPath.append(this.pathInfo);
                is = this.context.getResourceAsStream(srcPath.toString());
                destPath.append(this.tmpDir);
                destPath.append(this.pathInfo);
            } else {
                srcPath.append(CGIServlet.this.cgiPathPrefix);
                StringTokenizer pathWalker = new StringTokenizer(this.pathInfo, "/");
                while (pathWalker.hasMoreElements() && is == null) {
                    srcPath.append("/");
                    srcPath.append(pathWalker.nextElement());
                    is = this.context.getResourceAsStream(srcPath.toString());
                }
                destPath.append(this.tmpDir);
                destPath.append("/");
                destPath.append((CharSequence) srcPath);
            }
            if (is == null) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.expandNotFound", srcPath));
                return;
            }
            File f = new File(destPath.toString());
            if (f.exists()) {
                try {
                    is.close();
                    return;
                } catch (IOException e) {
                    CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.expandCloseFail", srcPath), e);
                    return;
                }
            }
            File dir = f.getParentFile();
            if (dir.mkdirs() || dir.isDirectory()) {
                try {
                    synchronized (CGIServlet.expandFileLock) {
                        if (f.exists()) {
                            return;
                        }
                        if (f.createNewFile()) {
                            try {
                                Files.copy(is, f.toPath(), new CopyOption[0]);
                                is.close();
                                if (CGIServlet.log.isDebugEnabled()) {
                                    CGIServlet.log.debug(CGIServlet.sm.getString("cgiServlet.expandOk", srcPath, destPath));
                                }
                                return;
                            } catch (Throwable th) {
                                is.close();
                                throw th;
                            }
                        }
                        return;
                    }
                } catch (IOException ioe) {
                    CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.expandFail", srcPath, destPath), ioe);
                    if (f.exists() && !f.delete()) {
                        CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.expandDeleteFail", f.getAbsolutePath()));
                        return;
                    }
                    return;
                }
            }
            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.expandCreateDirFail", dir.getAbsolutePath()));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CGIEnvironment Info:");
            sb.append(System.lineSeparator());
            if (isValid()) {
                sb.append("Validity: [true]");
                sb.append(System.lineSeparator());
                sb.append("Environment values:");
                sb.append(System.lineSeparator());
                for (Map.Entry<String, String> entry : this.env.entrySet()) {
                    sb.append("  ");
                    sb.append(entry.getKey());
                    sb.append(": [");
                    sb.append(blanksToString(entry.getValue(), "will be set to blank"));
                    sb.append("]");
                    sb.append(System.lineSeparator());
                }
                sb.append("Derived Command :[");
                sb.append(nullsToBlanks(this.command));
                sb.append("]");
                sb.append(System.lineSeparator());
                sb.append("Working Directory: [");
                if (this.workingDirectory != null) {
                    sb.append(this.workingDirectory.toString());
                }
                sb.append("]");
                sb.append(System.lineSeparator());
                sb.append("Command Line Params:");
                sb.append(System.lineSeparator());
                Iterator<String> it = this.cmdLineParameters.iterator();
                while (it.hasNext()) {
                    String param = it.next();
                    sb.append("  [");
                    sb.append(param);
                    sb.append("]");
                    sb.append(System.lineSeparator());
                }
            } else {
                sb.append("Validity: [false]");
                sb.append(System.lineSeparator());
                sb.append("CGI script not found or not specified.");
                sb.append(System.lineSeparator());
                sb.append("Check the HttpServletRequest pathInfo property to see if it is what ");
                sb.append(System.lineSeparator());
                sb.append("you meant it to be. You must specify an existent and executable file ");
                sb.append(System.lineSeparator());
                sb.append("as part of the path-info.");
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }

        protected String getCommand() {
            return this.command;
        }

        protected File getWorkingDirectory() {
            return this.workingDirectory;
        }

        protected Hashtable<String, String> getEnvironment() {
            return this.env;
        }

        protected ArrayList<String> getParameters() {
            return this.cmdLineParameters;
        }

        protected boolean isValid() {
            return this.valid;
        }

        protected String nullsToBlanks(String s) {
            return nullsToString(s, "");
        }

        protected String nullsToString(String couldBeNull, String subForNulls) {
            return couldBeNull == null ? subForNulls : couldBeNull;
        }

        protected String blanksToString(String couldBeBlank, String subForBlanks) {
            return ("".equals(couldBeBlank) || couldBeBlank == null) ? subForBlanks : couldBeBlank;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/CGIServlet$CGIRunner.class */
    public class CGIRunner {
        private final String command;
        private final Hashtable<String, String> env;
        private final File wd;
        private final ArrayList<String> params;
        private InputStream stdin = null;
        private HttpServletResponse response = null;
        private boolean readyToRun = false;

        protected CGIRunner(String command, Hashtable<String, String> env, File wd, ArrayList<String> params) {
            this.command = command;
            this.env = env;
            this.wd = wd;
            this.params = params;
            updateReadyStatus();
        }

        protected void updateReadyStatus() {
            if (this.command != null && this.env != null && this.wd != null && this.params != null && this.response != null) {
                this.readyToRun = true;
            } else {
                this.readyToRun = false;
            }
        }

        protected boolean isReady() {
            return this.readyToRun;
        }

        protected void setResponse(HttpServletResponse response) {
            this.response = response;
            updateReadyStatus();
        }

        protected void setInput(InputStream stdin) {
            this.stdin = stdin;
            updateReadyStatus();
        }

        protected String[] hashToStringArray(Hashtable<String, ?> h) throws NullPointerException {
            Vector<String> v = new Vector<>();
            Enumeration<String> e = h.keys();
            while (e.hasMoreElements()) {
                String k = e.nextElement();
                v.add(k + "=" + h.get(k).toString());
            }
            String[] strArr = new String[v.size()];
            v.copyInto(strArr);
            return strArr;
        }

        protected void run() throws IOException {
            if (!isReady()) {
                throw new IOException(getClass().getName() + ": not ready to run.");
            }
            if (CGIServlet.log.isDebugEnabled()) {
                CGIServlet.log.debug("envp: [" + this.env + "], command: [" + this.command + "]");
            }
            if (this.command.contains(File.separator + "." + File.separator) || this.command.contains(File.separator + CallerDataConverter.DEFAULT_RANGE_DELIMITER) || this.command.contains(CallerDataConverter.DEFAULT_RANGE_DELIMITER + File.separator)) {
                throw new IOException(getClass().getName() + "Illegal Character in CGI command path ('.' or '..') detected.  Not running CGI [" + this.command + "].");
            }
            BufferedReader cgiHeaderReader = null;
            InputStream cgiOutput = null;
            Thread errReaderThread = null;
            Process proc = null;
            int bufRead = -1;
            List<String> cmdAndArgs = new ArrayList<>();
            if (CGIServlet.this.cgiExecutable.length() != 0) {
                cmdAndArgs.add(CGIServlet.this.cgiExecutable);
            }
            if (CGIServlet.this.cgiExecutableArgs != null) {
                cmdAndArgs.addAll(CGIServlet.this.cgiExecutableArgs);
            }
            cmdAndArgs.add(this.command);
            cmdAndArgs.addAll(this.params);
            try {
                try {
                    Runtime rt = Runtime.getRuntime();
                    proc = rt.exec((String[]) cmdAndArgs.toArray(new String[cmdAndArgs.size()]), hashToStringArray(this.env), this.wd);
                    String sContentLength = this.env.get("CONTENT_LENGTH");
                    if (!"".equals(sContentLength)) {
                        BufferedOutputStream commandsStdIn = new BufferedOutputStream(proc.getOutputStream());
                        IOTools.flow(this.stdin, commandsStdIn);
                        commandsStdIn.flush();
                        commandsStdIn.close();
                    }
                    boolean isRunning = true;
                    final BufferedReader commandsStdErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                    errReaderThread = new Thread() { // from class: org.apache.catalina.servlets.CGIServlet.CGIRunner.1
                        @Override // java.lang.Thread, java.lang.Runnable
                        public void run() {
                            CGIRunner.this.sendToLog(commandsStdErr);
                        }
                    };
                    errReaderThread.start();
                    InputStream cgiHeaderStream = new HTTPHeaderInputStream(proc.getInputStream());
                    cgiHeaderReader = new BufferedReader(new InputStreamReader(cgiHeaderStream));
                    boolean skipBody = false;
                    while (isRunning) {
                        while (true) {
                            try {
                                String line = cgiHeaderReader.readLine();
                                if (line == null || "".equals(line)) {
                                    break;
                                }
                                if (CGIServlet.log.isTraceEnabled()) {
                                    CGIServlet.log.trace("addHeader(\"" + line + "\")");
                                }
                                if (line.startsWith("HTTP")) {
                                    skipBody = CGIServlet.this.setStatus(this.response, getSCFromHttpStatusLine(line));
                                } else if (line.indexOf(58) >= 0) {
                                    String header = line.substring(0, line.indexOf(58)).trim();
                                    String value = line.substring(line.indexOf(58) + 1).trim();
                                    if (header.equalsIgnoreCase(BindTag.STATUS_VARIABLE_NAME)) {
                                        skipBody = CGIServlet.this.setStatus(this.response, getSCFromCGIStatusHeader(value));
                                    } else {
                                        this.response.addHeader(header, value);
                                    }
                                } else {
                                    CGIServlet.log.info(CGIServlet.sm.getString("cgiServlet.runBadHeader", line));
                                }
                            } catch (IllegalThreadStateException e) {
                                try {
                                    Thread.sleep(500L);
                                } catch (InterruptedException e2) {
                                }
                            }
                        }
                        byte[] bBuf = new byte[2048];
                        OutputStream out = this.response.getOutputStream();
                        cgiOutput = proc.getInputStream();
                        while (!skipBody) {
                            try {
                                int read = cgiOutput.read(bBuf);
                                bufRead = read;
                                if (read == -1) {
                                    break;
                                }
                                if (CGIServlet.log.isTraceEnabled()) {
                                    CGIServlet.log.trace("output " + bufRead + " bytes of data");
                                }
                                out.write(bBuf, 0, bufRead);
                            } catch (Throwable th) {
                                if (bufRead != -1) {
                                    while (true) {
                                        int read2 = cgiOutput.read(bBuf);
                                        bufRead = read2;
                                        if (read2 == -1) {
                                            break;
                                        }
                                    }
                                }
                                throw th;
                                break;
                            }
                        }
                        if (bufRead != -1) {
                            while (true) {
                                int read3 = cgiOutput.read(bBuf);
                                bufRead = read3;
                                if (read3 == -1) {
                                    break;
                                }
                            }
                        }
                        proc.exitValue();
                        isRunning = false;
                    }
                    if (cgiHeaderReader != null) {
                        try {
                            cgiHeaderReader.close();
                        } catch (IOException ioe) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runHeaderReaderFail"), ioe);
                        }
                    }
                    if (cgiOutput != null) {
                        try {
                            cgiOutput.close();
                        } catch (IOException ioe2) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runOutputStreamFail"), ioe2);
                        }
                    }
                    if (errReaderThread != null) {
                        try {
                            errReaderThread.join(CGIServlet.this.stderrTimeout);
                        } catch (InterruptedException e3) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runReaderInterrupt"));
                        }
                    }
                    if (proc != null) {
                        proc.destroy();
                    }
                } catch (Throwable th2) {
                    if (cgiHeaderReader != null) {
                        try {
                            cgiHeaderReader.close();
                        } catch (IOException ioe3) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runHeaderReaderFail"), ioe3);
                        }
                    }
                    if (cgiOutput != null) {
                        try {
                            cgiOutput.close();
                        } catch (IOException ioe4) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runOutputStreamFail"), ioe4);
                        }
                    }
                    if (errReaderThread != null) {
                        try {
                            errReaderThread.join(CGIServlet.this.stderrTimeout);
                        } catch (InterruptedException e4) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runReaderInterrupt"));
                        }
                    }
                    if (proc != null) {
                        proc.destroy();
                    }
                    throw th2;
                }
            } catch (IOException e5) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runFail"), e5);
                throw e5;
            }
        }

        private int getSCFromHttpStatusLine(String line) {
            int statusStart = line.indexOf(32) + 1;
            if (statusStart < 1 || line.length() < statusStart + 3) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runInvalidStatus", line));
                return 500;
            }
            String status = line.substring(statusStart, statusStart + 3);
            try {
                int statusCode = Integer.parseInt(status);
                return statusCode;
            } catch (NumberFormatException e) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runInvalidStatus", status));
                return 500;
            }
        }

        private int getSCFromCGIStatusHeader(String value) {
            if (value.length() < 3) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runInvalidStatus", value));
                return 500;
            }
            String status = value.substring(0, 3);
            try {
                int statusCode = Integer.parseInt(status);
                return statusCode;
            } catch (NumberFormatException e) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runInvalidStatus", status));
                return 500;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendToLog(BufferedReader rdr) {
            int lineCount = 0;
            while (true) {
                try {
                    try {
                        String line = rdr.readLine();
                        if (line == null) {
                            break;
                        }
                        CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErr", line));
                        lineCount++;
                    } catch (Throwable th) {
                        try {
                            rdr.close();
                        } catch (IOException e) {
                            CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErrFail"), e);
                        }
                        throw th;
                    }
                } catch (IOException e2) {
                    CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErrFail"), e2);
                    try {
                        rdr.close();
                    } catch (IOException e3) {
                        CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErrFail"), e3);
                    }
                }
            }
            try {
                rdr.close();
            } catch (IOException e4) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErrFail"), e4);
            }
            if (lineCount > 0) {
                CGIServlet.log.warn(CGIServlet.sm.getString("cgiServlet.runStdErrCount", Integer.valueOf(lineCount)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/CGIServlet$HTTPHeaderInputStream.class */
    public static class HTTPHeaderInputStream extends InputStream {
        private static final int STATE_CHARACTER = 0;
        private static final int STATE_FIRST_CR = 1;
        private static final int STATE_FIRST_LF = 2;
        private static final int STATE_SECOND_CR = 3;
        private static final int STATE_HEADER_END = 4;
        private final InputStream input;
        private int state = 0;

        HTTPHeaderInputStream(InputStream theInput) {
            this.input = theInput;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.state == 4) {
                return -1;
            }
            int i = this.input.read();
            if (i == 10) {
                switch (this.state) {
                    case 0:
                        this.state = 2;
                        break;
                    case 1:
                        this.state = 2;
                        break;
                    case 2:
                    case 3:
                        this.state = 4;
                        break;
                }
            } else if (i == 13) {
                switch (this.state) {
                    case 0:
                        this.state = 1;
                        break;
                    case 1:
                        this.state = 4;
                        break;
                    case 2:
                        this.state = 3;
                        break;
                }
            } else {
                this.state = 0;
            }
            return i;
        }
    }
}