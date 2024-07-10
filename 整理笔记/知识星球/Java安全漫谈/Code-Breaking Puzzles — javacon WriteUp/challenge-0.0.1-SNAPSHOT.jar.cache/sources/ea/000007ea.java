package org.apache.catalina.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.Parameters;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationHttpRequest.class */
public class ApplicationHttpRequest extends HttpServletRequestWrapper {
    protected static final String[] specials = {"javax.servlet.include.request_uri", "javax.servlet.include.context_path", "javax.servlet.include.servlet_path", "javax.servlet.include.path_info", "javax.servlet.include.query_string", RequestDispatcher.INCLUDE_MAPPING, "javax.servlet.forward.request_uri", "javax.servlet.forward.context_path", "javax.servlet.forward.servlet_path", "javax.servlet.forward.path_info", "javax.servlet.forward.query_string", RequestDispatcher.FORWARD_MAPPING};
    private static final int SPECIALS_FIRST_FORWARD_INDEX = 6;
    protected final Context context;
    protected String contextPath;
    protected final boolean crossContext;
    protected DispatcherType dispatcherType;
    protected Map<String, String[]> parameters;
    private boolean parsedParams;
    protected String pathInfo;
    private String queryParamString;
    protected String queryString;
    protected Object requestDispatcherPath;
    protected String requestURI;
    protected String servletPath;
    private HttpServletMapping mapping;
    protected Session session;
    protected final Object[] specialAttributes;

    public ApplicationHttpRequest(HttpServletRequest request, Context context, boolean crossContext) {
        super(request);
        this.contextPath = null;
        this.dispatcherType = null;
        this.parameters = null;
        this.parsedParams = false;
        this.pathInfo = null;
        this.queryParamString = null;
        this.queryString = null;
        this.requestDispatcherPath = null;
        this.requestURI = null;
        this.servletPath = null;
        this.mapping = null;
        this.session = null;
        this.specialAttributes = new Object[specials.length];
        this.context = context;
        this.crossContext = crossContext;
        setRequest(request);
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public ServletContext getServletContext() {
        if (this.context == null) {
            return null;
        }
        return this.context.getServletContext();
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Object getAttribute(String name) {
        if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
            return this.dispatcherType;
        }
        if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
            if (this.requestDispatcherPath != null) {
                return this.requestDispatcherPath.toString();
            }
            return null;
        }
        int pos = getSpecial(name);
        if (pos == -1) {
            return getRequest().getAttribute(name);
        }
        if (this.specialAttributes[pos] == null && this.specialAttributes[6] == null && pos >= 6) {
            return getRequest().getAttribute(name);
        }
        return this.specialAttributes[pos];
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        return new AttributeNamesEnumerator();
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public void removeAttribute(String name) {
        if (!removeSpecial(name)) {
            getRequest().removeAttribute(name);
        }
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public void setAttribute(String name, Object value) {
        if (name.equals(Globals.DISPATCHER_TYPE_ATTR)) {
            this.dispatcherType = (DispatcherType) value;
        } else if (name.equals(Globals.DISPATCHER_REQUEST_PATH_ATTR)) {
            this.requestDispatcherPath = value;
        } else if (!setSpecial(name, value)) {
            getRequest().setAttribute(name, value);
        }
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        String requestPath;
        String relative;
        if (this.context == null || path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            return this.context.getServletContext().getRequestDispatcher(path);
        }
        String servletPath = (String) getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = getServletPath();
        }
        String pathInfo = getPathInfo();
        if (pathInfo == null) {
            requestPath = servletPath;
        } else {
            requestPath = servletPath + pathInfo;
        }
        int pos = requestPath.lastIndexOf(47);
        if (pos >= 0) {
            relative = requestPath.substring(0, pos + 1) + path;
        } else {
            relative = requestPath + path;
        }
        return this.context.getServletContext().getRequestDispatcher(relative);
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public DispatcherType getDispatcherType() {
        return this.dispatcherType;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getContextPath() {
        return this.contextPath;
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public String getParameter(String name) {
        parseParameters();
        String[] value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        return value[0];
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return this.parameters;
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        parseParameters();
        return this.parameters.get(name);
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getPathInfo() {
        return this.pathInfo;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        if (getPathInfo() == null || getServletContext() == null) {
            return null;
        }
        return getServletContext().getRealPath(getPathInfo());
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getQueryString() {
        return this.queryString;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        return RequestUtil.getRequestURL(this);
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public String getServletPath() {
        return this.servletPath;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        return this.mapping;
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        if (this.crossContext) {
            if (this.context == null) {
                return null;
            }
            if (this.session != null && this.session.isValid()) {
                return this.session.getSession();
            }
            HttpSession other = super.getSession(false);
            if (create && other == null) {
                other = super.getSession(true);
            }
            if (other != null) {
                Session localSession = null;
                try {
                    localSession = this.context.getManager().findSession(other.getId());
                    if (localSession != null) {
                        if (!localSession.isValid()) {
                            localSession = null;
                        }
                    }
                } catch (IOException e) {
                }
                if (localSession == null && create) {
                    localSession = this.context.getManager().createSession(other.getId());
                }
                if (localSession != null) {
                    localSession.access();
                    this.session = localSession;
                    return this.session.getSession();
                }
                return null;
            }
            return null;
        }
        return super.getSession(create);
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        Manager manager;
        if (this.crossContext) {
            String requestedSessionId = getRequestedSessionId();
            if (requestedSessionId == null || this.context == null || (manager = this.context.getManager()) == null) {
                return false;
            }
            Session session = null;
            try {
                session = manager.findSession(requestedSessionId);
            } catch (IOException e) {
            }
            if (session != null && session.isValid()) {
                return true;
            }
            return false;
        }
        return super.isRequestedSessionIdValid();
    }

    @Override // javax.servlet.http.HttpServletRequestWrapper, javax.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        ServletRequest current;
        ServletRequest request = getRequest();
        while (true) {
            current = request;
            if (!(current instanceof ServletRequestWrapper)) {
                break;
            }
            request = ((ServletRequestWrapper) current).getRequest();
        }
        if (current instanceof RequestFacade) {
            return ((RequestFacade) current).newPushBuilder(this);
        }
        return null;
    }

    public void recycle() {
        if (this.session != null) {
            this.session.endAccess();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    void setRequest(HttpServletRequest request) {
        super.setRequest((ServletRequest) request);
        this.dispatcherType = (DispatcherType) request.getAttribute(Globals.DISPATCHER_TYPE_ATTR);
        this.requestDispatcherPath = request.getAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR);
        this.contextPath = request.getContextPath();
        this.pathInfo = request.getPathInfo();
        this.queryString = request.getQueryString();
        this.requestURI = request.getRequestURI();
        this.servletPath = request.getServletPath();
        this.mapping = request.getHttpServletMapping();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    void parseParameters() {
        if (this.parsedParams) {
            return;
        }
        this.parameters = new ParameterMap();
        this.parameters.putAll(getRequest().getParameterMap());
        mergeParameters();
        ((ParameterMap) this.parameters).setLocked(true);
        this.parsedParams = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQueryParams(String queryString) {
        this.queryParamString = queryString;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMapping(HttpServletMapping mapping) {
        this.mapping = mapping;
    }

    protected boolean isSpecial(String name) {
        for (int i = 0; i < specials.length; i++) {
            if (specials[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected int getSpecial(String name) {
        for (int i = 0; i < specials.length; i++) {
            if (specials[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean setSpecial(String name, Object value) {
        for (int i = 0; i < specials.length; i++) {
            if (specials[i].equals(name)) {
                this.specialAttributes[i] = value;
                return true;
            }
        }
        return false;
    }

    protected boolean removeSpecial(String name) {
        for (int i = 0; i < specials.length; i++) {
            if (specials[i].equals(name)) {
                this.specialAttributes[i] = null;
                return true;
            }
        }
        return false;
    }

    private String[] mergeValues(String[] values1, String[] values2) {
        List<Object> results = new ArrayList<>();
        if (values1 != null) {
            for (String value : values1) {
                results.add(value);
            }
        }
        if (values2 != null) {
            for (String value2 : values2) {
                results.add(value2);
            }
        }
        String[] values = new String[results.size()];
        return (String[]) results.toArray(values);
    }

    private void mergeParameters() {
        if (this.queryParamString == null || this.queryParamString.length() < 1) {
            return;
        }
        Parameters paramParser = new Parameters();
        MessageBytes queryMB = MessageBytes.newInstance();
        queryMB.setString(this.queryParamString);
        String encoding = getCharacterEncoding();
        Charset charset = null;
        if (encoding != null) {
            try {
                charset = B2CConverter.getCharset(encoding);
                queryMB.setCharset(charset);
            } catch (UnsupportedEncodingException e) {
                charset = StandardCharsets.ISO_8859_1;
            }
        }
        paramParser.setQuery(queryMB);
        paramParser.setQueryStringCharset(charset);
        paramParser.handleQueryParameters();
        Enumeration<String> dispParamNames = paramParser.getParameterNames();
        while (dispParamNames.hasMoreElements()) {
            String dispParamName = dispParamNames.nextElement();
            String[] dispParamValues = paramParser.getParameterValues(dispParamName);
            String[] originalValues = this.parameters.get(dispParamName);
            if (originalValues == null) {
                this.parameters.put(dispParamName, dispParamValues);
            } else {
                this.parameters.put(dispParamName, mergeValues(dispParamValues, originalValues));
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationHttpRequest$AttributeNamesEnumerator.class */
    protected class AttributeNamesEnumerator implements Enumeration<String> {
        protected final int last;
        protected final Enumeration<String> parentEnumeration;
        protected int pos = -1;
        protected String next = null;

        public AttributeNamesEnumerator() {
            int last = -1;
            this.parentEnumeration = ApplicationHttpRequest.this.getRequest().getAttributeNames();
            int i = ApplicationHttpRequest.this.specialAttributes.length - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (ApplicationHttpRequest.this.getAttribute(ApplicationHttpRequest.specials[i]) == null) {
                    i--;
                } else {
                    last = i;
                    break;
                }
            }
            this.last = last;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            if (this.pos == this.last && this.next == null) {
                String findNext = findNext();
                this.next = findNext;
                if (findNext == null) {
                    return false;
                }
            }
            return true;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public String nextElement() {
            if (this.pos != this.last) {
                for (int i = this.pos + 1; i <= this.last; i++) {
                    if (ApplicationHttpRequest.this.getAttribute(ApplicationHttpRequest.specials[i]) != null) {
                        this.pos = i;
                        return ApplicationHttpRequest.specials[i];
                    }
                }
            }
            String result = this.next;
            if (this.next != null) {
                this.next = findNext();
                return result;
            }
            throw new NoSuchElementException();
        }

        protected String findNext() {
            String result = null;
            while (result == null && this.parentEnumeration.hasMoreElements()) {
                String current = this.parentEnumeration.nextElement();
                if (!ApplicationHttpRequest.this.isSpecial(current)) {
                    result = current;
                }
            }
            return result;
        }
    }
}