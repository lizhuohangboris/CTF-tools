package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.coyote.Constants;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.RequestUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIServletExternalResolver.class */
public class SSIServletExternalResolver implements SSIExternalResolver {
    protected final String[] VARIABLE_NAMES = {"AUTH_TYPE", "CONTENT_LENGTH", "CONTENT_TYPE", "DOCUMENT_NAME", "DOCUMENT_URI", "GATEWAY_INTERFACE", "HTTP_ACCEPT", "HTTP_ACCEPT_ENCODING", "HTTP_ACCEPT_LANGUAGE", "HTTP_CONNECTION", "HTTP_HOST", "HTTP_REFERER", "HTTP_USER_AGENT", "PATH_INFO", "PATH_TRANSLATED", "QUERY_STRING", "QUERY_STRING_UNESCAPED", "REMOTE_ADDR", "REMOTE_HOST", "REMOTE_PORT", "REMOTE_USER", "REQUEST_METHOD", "REQUEST_URI", "SCRIPT_FILENAME", "SCRIPT_NAME", "SERVER_ADDR", "SERVER_NAME", "SERVER_PORT", "SERVER_PROTOCOL", "SERVER_SOFTWARE", "UNIQUE_ID"};
    protected final ServletContext context;
    protected final HttpServletRequest req;
    protected final HttpServletResponse res;
    protected final boolean isVirtualWebappRelative;
    protected final int debug;
    protected final String inputEncoding;

    public SSIServletExternalResolver(ServletContext context, HttpServletRequest req, HttpServletResponse res, boolean isVirtualWebappRelative, int debug, String inputEncoding) {
        this.context = context;
        this.req = req;
        this.res = res;
        this.isVirtualWebappRelative = isVirtualWebappRelative;
        this.debug = debug;
        this.inputEncoding = inputEncoding;
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public void log(String message, Throwable throwable) {
        if (throwable != null) {
            this.context.log(message, throwable);
        } else {
            this.context.log(message);
        }
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public void addVariableNames(Collection<String> variableNames) {
        for (int i = 0; i < this.VARIABLE_NAMES.length; i++) {
            String variableName = this.VARIABLE_NAMES[i];
            String variableValue = getVariableValue(variableName);
            if (variableValue != null) {
                variableNames.add(variableName);
            }
        }
        Enumeration<String> e = this.req.getAttributeNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            if (!isNameReserved(name)) {
                variableNames.add(name);
            }
        }
    }

    protected Object getReqAttributeIgnoreCase(String targetName) {
        Object object = null;
        if (!isNameReserved(targetName)) {
            object = this.req.getAttribute(targetName);
            if (object == null) {
                Enumeration<String> e = this.req.getAttributeNames();
                while (e.hasMoreElements()) {
                    String name = e.nextElement();
                    if (targetName.equalsIgnoreCase(name) && !isNameReserved(name)) {
                        object = this.req.getAttribute(name);
                        if (object != null) {
                            break;
                        }
                    }
                }
            }
        }
        return object;
    }

    protected boolean isNameReserved(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.");
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public void setVariableValue(String name, String value) {
        if (!isNameReserved(name)) {
            this.req.setAttribute(name, value);
        }
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public String getVariableValue(String name) {
        String retVal;
        Object object = getReqAttributeIgnoreCase(name);
        if (object != null) {
            retVal = object.toString();
        } else {
            retVal = getCGIVariable(name);
        }
        return retVal;
    }

    protected String getCGIVariable(String name) {
        Charset queryStringCharset;
        Enumeration<String> acceptHeaders;
        String retVal = null;
        String[] nameParts = name.toUpperCase(Locale.ENGLISH).split("_");
        int requiredParts = 2;
        if (nameParts.length == 1) {
            if (nameParts[0].equals("PATH")) {
                requiredParts = 1;
            }
        } else if (nameParts[0].equals("AUTH")) {
            if (nameParts[1].equals("TYPE")) {
                retVal = this.req.getAuthType();
            }
        } else if (nameParts[0].equals("CONTENT")) {
            if (nameParts[1].equals("LENGTH")) {
                long contentLength = this.req.getContentLengthLong();
                if (contentLength >= 0) {
                    retVal = Long.toString(contentLength);
                }
            } else if (nameParts[1].equals("TYPE")) {
                retVal = this.req.getContentType();
            }
        } else if (nameParts[0].equals("DOCUMENT")) {
            if (nameParts[1].equals("NAME")) {
                String requestURI = this.req.getRequestURI();
                retVal = requestURI.substring(requestURI.lastIndexOf(47) + 1);
            } else if (nameParts[1].equals("URI")) {
                retVal = this.req.getRequestURI();
            }
        } else if (name.equalsIgnoreCase("GATEWAY_INTERFACE")) {
            retVal = "CGI/1.1";
        } else if (nameParts[0].equals("HTTP")) {
            if (nameParts[1].equals("ACCEPT")) {
                String accept = null;
                if (nameParts.length == 2) {
                    accept = HttpHeaders.ACCEPT;
                } else if (nameParts[2].equals("ENCODING")) {
                    requiredParts = 3;
                    accept = HttpHeaders.ACCEPT_ENCODING;
                } else if (nameParts[2].equals("LANGUAGE")) {
                    requiredParts = 3;
                    accept = HttpHeaders.ACCEPT_LANGUAGE;
                }
                if (accept != null && (acceptHeaders = this.req.getHeaders(accept)) != null && acceptHeaders.hasMoreElements()) {
                    StringBuilder rv = new StringBuilder(acceptHeaders.nextElement());
                    while (acceptHeaders.hasMoreElements()) {
                        rv.append(", ");
                        rv.append(acceptHeaders.nextElement());
                    }
                    retVal = rv.toString();
                }
            } else if (nameParts[1].equals("CONNECTION")) {
                retVal = this.req.getHeader("Connection");
            } else if (nameParts[1].equals("HOST")) {
                retVal = this.req.getHeader("Host");
            } else if (nameParts[1].equals("REFERER")) {
                retVal = this.req.getHeader(HttpHeaders.REFERER);
            } else if (nameParts[1].equals("USER") && nameParts.length == 3 && nameParts[2].equals("AGENT")) {
                requiredParts = 3;
                retVal = this.req.getHeader(HttpHeaders.USER_AGENT);
            }
        } else if (nameParts[0].equals("PATH")) {
            if (nameParts[1].equals("INFO")) {
                retVal = this.req.getPathInfo();
            } else if (nameParts[1].equals("TRANSLATED")) {
                retVal = this.req.getPathTranslated();
            }
        } else if (nameParts[0].equals("QUERY")) {
            if (nameParts[1].equals("STRING")) {
                String queryString = this.req.getQueryString();
                if (nameParts.length == 2) {
                    retVal = nullToEmptyString(queryString);
                } else if (nameParts[2].equals("UNESCAPED")) {
                    requiredParts = 3;
                    if (queryString != null) {
                        Charset uriCharset = null;
                        Charset requestCharset = null;
                        boolean useBodyEncodingForURI = false;
                        if (this.req instanceof Request) {
                            try {
                                requestCharset = ((Request) this.req).getCoyoteRequest().getCharset();
                            } catch (UnsupportedEncodingException e) {
                            }
                            Connector connector = ((Request) this.req).getConnector();
                            uriCharset = connector.getURICharset();
                            useBodyEncodingForURI = connector.getUseBodyEncodingForURI();
                        }
                        if (useBodyEncodingForURI && requestCharset != null) {
                            queryStringCharset = requestCharset;
                        } else if (uriCharset != null) {
                            queryStringCharset = uriCharset;
                        } else {
                            queryStringCharset = Constants.DEFAULT_URI_CHARSET;
                        }
                        retVal = UDecoder.URLDecode(queryString, queryStringCharset);
                    }
                }
            }
        } else if (nameParts[0].equals("REMOTE")) {
            if (nameParts[1].equals("ADDR")) {
                retVal = this.req.getRemoteAddr();
            } else if (nameParts[1].equals("HOST")) {
                retVal = this.req.getRemoteHost();
            } else if (!nameParts[1].equals("IDENT")) {
                if (nameParts[1].equals("PORT")) {
                    retVal = Integer.toString(this.req.getRemotePort());
                } else if (nameParts[1].equals("USER")) {
                    retVal = this.req.getRemoteUser();
                }
            }
        } else if (nameParts[0].equals("REQUEST")) {
            if (nameParts[1].equals("METHOD")) {
                retVal = this.req.getMethod();
            } else if (nameParts[1].equals("URI")) {
                retVal = (String) this.req.getAttribute("javax.servlet.forward.request_uri");
                if (retVal == null) {
                    retVal = this.req.getRequestURI();
                }
            }
        } else if (nameParts[0].equals("SCRIPT")) {
            String scriptName = this.req.getServletPath();
            if (nameParts[1].equals("FILENAME")) {
                retVal = this.context.getRealPath(scriptName);
            } else if (nameParts[1].equals("NAME")) {
                retVal = scriptName;
            }
        } else if (nameParts[0].equals("SERVER")) {
            if (nameParts[1].equals("ADDR")) {
                retVal = this.req.getLocalAddr();
            }
            if (nameParts[1].equals("NAME")) {
                retVal = this.req.getServerName();
            } else if (nameParts[1].equals("PORT")) {
                retVal = Integer.toString(this.req.getServerPort());
            } else if (nameParts[1].equals("PROTOCOL")) {
                retVal = this.req.getProtocol();
            } else if (nameParts[1].equals("SOFTWARE")) {
                retVal = this.context.getServerInfo() + " " + System.getProperty("java.vm.name") + "/" + System.getProperty("java.vm.version") + " " + System.getProperty("os.name");
            }
        } else if (name.equalsIgnoreCase("UNIQUE_ID")) {
            retVal = this.req.getRequestedSessionId();
        }
        if (requiredParts != nameParts.length) {
            return null;
        }
        return retVal;
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public Date getCurrentDate() {
        return new Date();
    }

    protected String nullToEmptyString(String string) {
        String retVal = string;
        if (retVal == null) {
            retVal = "";
        }
        return retVal;
    }

    protected String getPathWithoutFileName(String servletPath) {
        String retVal = null;
        int lastSlash = servletPath.lastIndexOf(47);
        if (lastSlash >= 0) {
            retVal = servletPath.substring(0, lastSlash + 1);
        }
        return retVal;
    }

    protected String getPathWithoutContext(String contextPath, String servletPath) {
        if (servletPath.startsWith(contextPath)) {
            return servletPath.substring(contextPath.length());
        }
        return servletPath;
    }

    protected String getAbsolutePath(String path) throws IOException {
        String pathWithoutContext = SSIServletRequestUtil.getRelativePath(this.req);
        String prefix = getPathWithoutFileName(pathWithoutContext);
        if (prefix == null) {
            throw new IOException("Couldn't remove filename from path: " + pathWithoutContext);
        }
        String fullPath = prefix + path;
        String retVal = RequestUtil.normalize(fullPath);
        if (retVal == null) {
            throw new IOException("Normalization yielded null on path: " + fullPath);
        }
        return retVal;
    }

    protected ServletContextAndPath getServletContextAndPathFromNonVirtualPath(String nonVirtualPath) throws IOException {
        if (nonVirtualPath.startsWith("/") || nonVirtualPath.startsWith("\\")) {
            throw new IOException("A non-virtual path can't be absolute: " + nonVirtualPath);
        }
        if (nonVirtualPath.contains("../")) {
            throw new IOException("A non-virtual path can't contain '../' : " + nonVirtualPath);
        }
        String path = getAbsolutePath(nonVirtualPath);
        ServletContextAndPath csAndP = new ServletContextAndPath(this.context, path);
        return csAndP;
    }

    protected ServletContextAndPath getServletContextAndPathFromVirtualPath(String virtualPath) throws IOException {
        if (!virtualPath.startsWith("/") && !virtualPath.startsWith("\\")) {
            return new ServletContextAndPath(this.context, getAbsolutePath(virtualPath));
        }
        String normalized = RequestUtil.normalize(virtualPath);
        if (this.isVirtualWebappRelative) {
            return new ServletContextAndPath(this.context, normalized);
        }
        ServletContext normContext = this.context.getContext(normalized);
        if (normContext == null) {
            throw new IOException("Couldn't get context for path: " + normalized);
        }
        if (!isRootContext(normContext)) {
            String noContext = getPathWithoutContext(normContext.getContextPath(), normalized);
            return new ServletContextAndPath(normContext, noContext);
        }
        return new ServletContextAndPath(normContext, normalized);
    }

    protected boolean isRootContext(ServletContext servletContext) {
        return servletContext == servletContext.getContext("/");
    }

    protected ServletContextAndPath getServletContextAndPath(String originalPath, boolean virtual) throws IOException {
        ServletContextAndPath csAndP;
        if (this.debug > 0) {
            log("SSIServletExternalResolver.getServletContextAndPath( " + originalPath + ", " + virtual + ")", null);
        }
        if (virtual) {
            csAndP = getServletContextAndPathFromVirtualPath(originalPath);
        } else {
            csAndP = getServletContextAndPathFromNonVirtualPath(originalPath);
        }
        return csAndP;
    }

    protected URLConnection getURLConnection(String originalPath, boolean virtual) throws IOException {
        ServletContextAndPath csAndP = getServletContextAndPath(originalPath, virtual);
        ServletContext context = csAndP.getServletContext();
        String path = csAndP.getPath();
        URL url = context.getResource(path);
        if (url == null) {
            throw new IOException("Context did not contain resource: " + path);
        }
        URLConnection urlConnection = url.openConnection();
        return urlConnection;
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public long getFileLastModified(String path, boolean virtual) throws IOException {
        long lastModified = 0;
        try {
            URLConnection urlConnection = getURLConnection(path, virtual);
            lastModified = urlConnection.getLastModified();
        } catch (IOException e) {
        }
        return lastModified;
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public long getFileSize(String path, boolean virtual) throws IOException {
        long fileSize = -1;
        try {
            URLConnection urlConnection = getURLConnection(path, virtual);
            fileSize = urlConnection.getContentLengthLong();
        } catch (IOException e) {
        }
        return fileSize;
    }

    @Override // org.apache.catalina.ssi.SSIExternalResolver
    public String getFileText(String originalPath, boolean virtual) throws IOException {
        String retVal;
        try {
            ServletContextAndPath csAndP = getServletContextAndPath(originalPath, virtual);
            ServletContext context = csAndP.getServletContext();
            String path = csAndP.getPath();
            RequestDispatcher rd = context.getRequestDispatcher(path);
            if (rd == null) {
                throw new IOException("Couldn't get request dispatcher for path: " + path);
            }
            ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
            ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(this.res, basos);
            rd.include(this.req, responseIncludeWrapper);
            responseIncludeWrapper.flushOutputStreamOrWriter();
            byte[] bytes = basos.toByteArray();
            if (this.inputEncoding == null) {
                retVal = new String(bytes);
            } else {
                retVal = new String(bytes, B2CConverter.getCharset(this.inputEncoding));
            }
            if (retVal.equals("") && !this.req.getMethod().equalsIgnoreCase(WebContentGenerator.METHOD_HEAD)) {
                throw new IOException("Couldn't find file: " + path);
            }
            return retVal;
        } catch (ServletException e) {
            throw new IOException("Couldn't include file: " + originalPath + " because of ServletException: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIServletExternalResolver$ServletContextAndPath.class */
    public static class ServletContextAndPath {
        protected final ServletContext servletContext;
        protected final String path;

        public ServletContextAndPath(ServletContext servletContext, String path) {
            this.servletContext = servletContext;
            this.path = path;
        }

        public ServletContext getServletContext() {
            return this.servletContext;
        }

        public String getPath() {
            return this.path;
        }
    }
}