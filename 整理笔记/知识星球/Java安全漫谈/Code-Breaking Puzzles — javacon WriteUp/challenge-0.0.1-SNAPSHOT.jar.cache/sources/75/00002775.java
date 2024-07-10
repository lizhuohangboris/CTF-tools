package org.springframework.web.util;

import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/UrlPathHelper.class */
public class UrlPathHelper {
    private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";
    private static final Log logger = LogFactory.getLog(UrlPathHelper.class);
    @Nullable
    static volatile Boolean websphereComplianceFlag;
    private boolean alwaysUseFullPath = false;
    private boolean urlDecode = true;
    private boolean removeSemicolonContent = true;
    private String defaultEncoding = "ISO-8859-1";

    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.alwaysUseFullPath = alwaysUseFullPath;
    }

    public void setUrlDecode(boolean urlDecode) {
        this.urlDecode = urlDecode;
    }

    public boolean isUrlDecode() {
        return this.urlDecode;
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.removeSemicolonContent = removeSemicolonContent;
    }

    public boolean shouldRemoveSemicolonContent() {
        return this.removeSemicolonContent;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    protected String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public String getLookupPathForRequest(HttpServletRequest request) {
        if (this.alwaysUseFullPath) {
            return getPathWithinApplication(request);
        }
        String rest = getPathWithinServletMapping(request);
        if (!"".equals(rest)) {
            return rest;
        }
        return getPathWithinApplication(request);
    }

    public String getPathWithinServletMapping(HttpServletRequest request) {
        String path;
        String pathWithinApp = getPathWithinApplication(request);
        String servletPath = getServletPath(request);
        String sanitizedPathWithinApp = getSanitizedPath(pathWithinApp);
        if (servletPath.contains(sanitizedPathWithinApp)) {
            path = getRemainingPath(sanitizedPathWithinApp, servletPath, false);
        } else {
            path = getRemainingPath(pathWithinApp, servletPath, false);
        }
        if (path != null) {
            return path;
        }
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            return pathInfo;
        }
        if (!this.urlDecode) {
            String path2 = getRemainingPath(decodeInternal(request, pathWithinApp), servletPath, false);
            if (path2 != null) {
                return pathWithinApp;
            }
        }
        return servletPath;
    }

    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        String path = getRemainingPath(requestUri, contextPath, true);
        if (path != null) {
            return StringUtils.hasText(path) ? path : "/";
        }
        return requestUri;
    }

    @Nullable
    private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
        int index1 = 0;
        int index2 = 0;
        while (index1 < requestUri.length() && index2 < mapping.length()) {
            char c1 = requestUri.charAt(index1);
            char c2 = mapping.charAt(index2);
            if (c1 == ';') {
                index1 = requestUri.indexOf(47, index1);
                if (index1 == -1) {
                    return null;
                }
                c1 = requestUri.charAt(index1);
            }
            if (c1 == c2 || (ignoreCase && Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
                index1++;
                index2++;
            } else {
                return null;
            }
        }
        if (index2 != mapping.length()) {
            return null;
        }
        if (index1 == requestUri.length()) {
            return "";
        }
        if (requestUri.charAt(index1) == ';') {
            index1 = requestUri.indexOf(47, index1);
        }
        return index1 != -1 ? requestUri.substring(index1) : "";
    }

    private String getSanitizedPath(String path) {
        String str = path;
        while (true) {
            String sanitized = str;
            int index = sanitized.indexOf("//");
            if (index >= 0) {
                str = sanitized.substring(0, index) + sanitized.substring(index + 1);
            } else {
                return sanitized;
            }
        }
    }

    public String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return decodeAndCleanUriString(request, uri);
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute("javax.servlet.include.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        return decodeRequestString(request, contextPath);
    }

    public String getServletPath(HttpServletRequest request) {
        String servletPath = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        if (servletPath.length() > 1 && servletPath.endsWith("/") && shouldRemoveTrailingServletPathSlash(request)) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        return servletPath;
    }

    public String getOriginatingRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WEBSPHERE_URI_ATTRIBUTE);
        if (uri == null) {
            uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
            if (uri == null) {
                uri = request.getRequestURI();
            }
        }
        return decodeAndCleanUriString(request, uri);
    }

    public String getOriginatingContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute("javax.servlet.forward.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        return decodeRequestString(request, contextPath);
    }

    public String getOriginatingServletPath(HttpServletRequest request) {
        String servletPath = (String) request.getAttribute("javax.servlet.forward.servlet_path");
        if (servletPath == null) {
            servletPath = request.getServletPath();
        }
        return servletPath;
    }

    public String getOriginatingQueryString(HttpServletRequest request) {
        if (request.getAttribute("javax.servlet.forward.request_uri") != null || request.getAttribute("javax.servlet.error.request_uri") != null) {
            return (String) request.getAttribute("javax.servlet.forward.query_string");
        }
        return request.getQueryString();
    }

    private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        return getSanitizedPath(decodeRequestString(request, removeSemicolonContent(uri)));
    }

    public String decodeRequestString(HttpServletRequest request, String source) {
        if (this.urlDecode) {
            return decodeInternal(request, source);
        }
        return source;
    }

    private String decodeInternal(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return UriUtils.decode(source, enc);
        } catch (UnsupportedCharsetException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not decode request string [" + source + "] with encoding '" + enc + "': falling back to platform default encoding; exception message: " + ex.getMessage());
            }
            return URLDecoder.decode(source);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = getDefaultEncoding();
        }
        return enc;
    }

    public String removeSemicolonContent(String requestUri) {
        return this.removeSemicolonContent ? removeSemicolonContentInternal(requestUri) : removeJsessionid(requestUri);
    }

    private String removeSemicolonContentInternal(String requestUri) {
        int indexOf = requestUri.indexOf(59);
        while (true) {
            int semicolonIndex = indexOf;
            if (semicolonIndex != -1) {
                int slashIndex = requestUri.indexOf(47, semicolonIndex);
                String start = requestUri.substring(0, semicolonIndex);
                requestUri = slashIndex != -1 ? start + requestUri.substring(slashIndex) : start;
                indexOf = requestUri.indexOf(59, semicolonIndex);
            } else {
                return requestUri;
            }
        }
    }

    private String removeJsessionid(String requestUri) {
        int startIndex = requestUri.toLowerCase().indexOf(";jsessionid=");
        if (startIndex != -1) {
            int endIndex = requestUri.indexOf(59, startIndex + 12);
            String start = requestUri.substring(0, startIndex);
            requestUri = endIndex != -1 ? start + requestUri.substring(endIndex) : start;
        }
        return requestUri;
    }

    public Map<String, String> decodePathVariables(HttpServletRequest request, Map<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        Map<String, String> decodedVars = new LinkedHashMap<>(vars.size());
        vars.forEach(key, value -> {
            String str = (String) decodedVars.put(key, decodeInternal(request, value));
        });
        return decodedVars;
    }

    public MultiValueMap<String, String> decodeMatrixVariables(HttpServletRequest request, MultiValueMap<String, String> vars) {
        if (this.urlDecode) {
            return vars;
        }
        MultiValueMap<String, String> decodedVars = new LinkedMultiValueMap<>(vars.size());
        vars.forEach(key, values -> {
            Iterator it = values.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                decodedVars.add(key, decodeInternal(request, value));
            }
        });
        return decodedVars;
    }

    private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
        if (request.getAttribute(WEBSPHERE_URI_ATTRIBUTE) == null) {
            return false;
        }
        Boolean flagToUse = websphereComplianceFlag;
        if (flagToUse == null) {
            ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
            boolean flag = false;
            try {
                Class<?> cl = classLoader.loadClass("com.ibm.ws.webcontainer.WebContainer");
                Properties prop = (Properties) cl.getMethod("getWebContainerProperties", new Class[0]).invoke(null, new Object[0]);
                flag = Boolean.parseBoolean(prop.getProperty("com.ibm.ws.webcontainer.removetrailingservletpathslash"));
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not introspect WebSphere web container properties: " + ex);
                }
            }
            flagToUse = Boolean.valueOf(flag);
            websphereComplianceFlag = Boolean.valueOf(flag);
        }
        return !flagToUse.booleanValue();
    }
}