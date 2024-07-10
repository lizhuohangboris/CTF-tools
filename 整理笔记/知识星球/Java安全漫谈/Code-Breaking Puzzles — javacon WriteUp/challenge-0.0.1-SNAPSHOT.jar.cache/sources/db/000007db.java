package org.apache.catalina.core;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Service;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.util.Introspection;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationContext.class */
public class ApplicationContext implements ServletContext {
    protected static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    protected static final boolean GET_RESOURCE_REQUIRE_SLASH;
    private final StandardContext context;
    private final Service service;
    private static final List<String> emptyString;
    private static final List<Servlet> emptyServlet;
    private static final StringManager sm;
    private SessionCookieConfig sessionCookieConfig;
    protected Map<String, Object> attributes = new ConcurrentHashMap();
    private final Map<String, String> readOnlyAttributes = new ConcurrentHashMap();
    private final ServletContext facade = new ApplicationContextFacade(this);
    private final Map<String, String> parameters = new ConcurrentHashMap();
    private final ThreadLocal<DispatchData> dispatchData = new ThreadLocal<>();
    private Set<SessionTrackingMode> sessionTrackingModes = null;
    private Set<SessionTrackingMode> defaultSessionTrackingModes = null;
    private Set<SessionTrackingMode> supportedSessionTrackingModes = null;
    private boolean newServletContextListenerAllowed = true;

    static {
        String requireSlash = System.getProperty("org.apache.catalina.core.ApplicationContext.GET_RESOURCE_REQUIRE_SLASH");
        if (requireSlash == null) {
            GET_RESOURCE_REQUIRE_SLASH = STRICT_SERVLET_COMPLIANCE;
        } else {
            GET_RESOURCE_REQUIRE_SLASH = Boolean.parseBoolean(requireSlash);
        }
        emptyString = Collections.emptyList();
        emptyServlet = Collections.emptyList();
        sm = StringManager.getManager(Constants.Package);
    }

    public ApplicationContext(StandardContext context) {
        this.context = context;
        this.service = ((Engine) context.getParent().getParent()).getService();
        this.sessionCookieConfig = new ApplicationSessionCookieConfig(context);
        populateSessionTrackingModes();
    }

    @Override // javax.servlet.ServletContext
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override // javax.servlet.ServletContext
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>();
        names.addAll(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override // javax.servlet.ServletContext
    public ServletContext getContext(String uri) {
        if (uri == null || !uri.startsWith("/")) {
            return null;
        }
        try {
            Container host = this.context.getParent();
            Context child = (Context) host.findChild(uri);
            if (child != null && !child.getState().isAvailable()) {
                child = null;
            }
            if (child == null) {
                int i = uri.indexOf("##");
                if (i > -1) {
                    uri = uri.substring(0, i);
                }
                MessageBytes hostMB = MessageBytes.newInstance();
                hostMB.setString(host.getName());
                MessageBytes pathMB = MessageBytes.newInstance();
                pathMB.setString(uri);
                MappingData mappingData = new MappingData();
                ((Engine) host.getParent()).getService().getMapper().map(hostMB, pathMB, null, mappingData);
                child = mappingData.context;
            }
            if (child == null) {
                return null;
            }
            if (this.context.getCrossContext()) {
                return child.getServletContext();
            }
            if (child == this.context) {
                return this.context.getServletContext();
            }
            return null;
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            return null;
        }
    }

    @Override // javax.servlet.ServletContext
    public String getContextPath() {
        return this.context.getPath();
    }

    @Override // javax.servlet.ServletContext
    public String getInitParameter(String name) {
        if (Globals.JASPER_XML_VALIDATION_TLD_INIT_PARAM.equals(name) && this.context.getTldValidation()) {
            return "true";
        }
        if (Globals.JASPER_XML_BLOCK_EXTERNAL_INIT_PARAM.equals(name) && !this.context.getXmlBlockExternal()) {
            return "false";
        }
        return this.parameters.get(name);
    }

    @Override // javax.servlet.ServletContext
    public Enumeration<String> getInitParameterNames() {
        Set<String> names = new HashSet<>();
        names.addAll(this.parameters.keySet());
        if (this.context.getTldValidation()) {
            names.add(Globals.JASPER_XML_VALIDATION_TLD_INIT_PARAM);
        }
        if (!this.context.getXmlBlockExternal()) {
            names.add(Globals.JASPER_XML_BLOCK_EXTERNAL_INIT_PARAM);
        }
        return Collections.enumeration(names);
    }

    @Override // javax.servlet.ServletContext
    public int getMajorVersion() {
        return 4;
    }

    @Override // javax.servlet.ServletContext
    public int getMinorVersion() {
        return 0;
    }

    @Override // javax.servlet.ServletContext
    public String getMimeType(String file) {
        int period;
        if (file == null || (period = file.lastIndexOf(46)) < 0) {
            return null;
        }
        String extension = file.substring(period + 1);
        if (extension.length() < 1) {
            return null;
        }
        return this.context.findMimeMapping(extension);
    }

    @Override // javax.servlet.ServletContext
    public RequestDispatcher getNamedDispatcher(String name) {
        Wrapper wrapper;
        if (name == null || (wrapper = (Wrapper) this.context.findChild(name)) == null) {
            return null;
        }
        return new ApplicationDispatcher(wrapper, null, null, null, null, null, name);
    }

    @Override // javax.servlet.ServletContext
    public String getRealPath(String path) {
        String validatedPath = validateResourcePath(path, true);
        return this.context.getRealPath(validatedPath);
    }

    @Override // javax.servlet.ServletContext
    public RequestDispatcher getRequestDispatcher(String path) {
        String uri;
        String queryString;
        String uri2;
        if (path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            int pos = path.indexOf(63);
            if (pos >= 0) {
                uri = path.substring(0, pos);
                queryString = path.substring(pos + 1);
            } else {
                uri = path;
                queryString = null;
            }
            String uriNoParams = stripPathParams(uri);
            String normalizedUri = RequestUtil.normalize(uriNoParams);
            if (normalizedUri == null) {
                return null;
            }
            if (getContext().getDispatchersUseEncodedPaths()) {
                String decodedUri = UDecoder.URLDecode(normalizedUri);
                normalizedUri = RequestUtil.normalize(decodedUri);
                if (!decodedUri.equals(normalizedUri)) {
                    getContext().getLogger().warn(sm.getString("applicationContext.illegalDispatchPath", path), new IllegalArgumentException());
                    return null;
                }
                uri2 = URLEncoder.DEFAULT.encode(getContextPath(), StandardCharsets.UTF_8) + uri;
            } else {
                uri2 = URLEncoder.DEFAULT.encode(getContextPath() + uri, StandardCharsets.UTF_8);
            }
            normalizedUri.length();
            DispatchData dd = this.dispatchData.get();
            if (dd == null) {
                dd = new DispatchData();
                this.dispatchData.set(dd);
            }
            MessageBytes uriMB = dd.uriMB;
            uriMB.recycle();
            MappingData mappingData = dd.mappingData;
            try {
                CharChunk uriCC = uriMB.getCharChunk();
                try {
                    uriCC.append(this.context.getPath());
                    uriCC.append(normalizedUri);
                    this.service.getMapper().map(this.context, uriMB, mappingData);
                    if (mappingData.wrapper == null) {
                        mappingData.recycle();
                        return null;
                    }
                    Wrapper wrapper = mappingData.wrapper;
                    String wrapperPath = mappingData.wrapperPath.toString();
                    String pathInfo = mappingData.pathInfo.toString();
                    HttpServletMapping mapping = new ApplicationMapping(mappingData).getHttpServletMapping();
                    ApplicationDispatcher applicationDispatcher = new ApplicationDispatcher(wrapper, uri2, wrapperPath, pathInfo, queryString, mapping, null);
                    mappingData.recycle();
                    return applicationDispatcher;
                } catch (Exception e) {
                    log(sm.getString("applicationContext.mapping.error"), e);
                    mappingData.recycle();
                    return null;
                }
            } catch (Throwable th) {
                mappingData.recycle();
                throw th;
            }
        }
        throw new IllegalArgumentException(sm.getString("applicationContext.requestDispatcher.iae", path));
    }

    static String stripPathParams(String input) {
        if (input.indexOf(59) < 0) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length());
        int pos = 0;
        int limit = input.length();
        while (pos < limit) {
            int nextSemiColon = input.indexOf(59, pos);
            if (nextSemiColon < 0) {
                nextSemiColon = limit;
            }
            sb.append(input.substring(pos, nextSemiColon));
            int followingSlash = input.indexOf(47, nextSemiColon);
            if (followingSlash < 0) {
                pos = limit;
            } else {
                pos = followingSlash;
            }
        }
        return sb.toString();
    }

    @Override // javax.servlet.ServletContext
    public URL getResource(String path) throws MalformedURLException {
        String validatedPath = validateResourcePath(path, false);
        if (validatedPath == null) {
            throw new MalformedURLException(sm.getString("applicationContext.requestDispatcher.iae", path));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.getResource(validatedPath).getURL();
        }
        return null;
    }

    @Override // javax.servlet.ServletContext
    public InputStream getResourceAsStream(String path) {
        WebResourceRoot resources;
        String validatedPath = validateResourcePath(path, false);
        if (validatedPath != null && (resources = this.context.getResources()) != null) {
            return resources.getResource(validatedPath).getInputStream();
        }
        return null;
    }

    private String validateResourcePath(String path, boolean allowEmptyPath) {
        if (path == null) {
            return null;
        }
        if (path.length() == 0 && allowEmptyPath) {
            return path;
        }
        if (!path.startsWith("/")) {
            if (GET_RESOURCE_REQUIRE_SLASH) {
                return null;
            }
            return "/" + path;
        }
        return path;
    }

    @Override // javax.servlet.ServletContext
    public Set<String> getResourcePaths(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.resourcePaths.iae", path));
        }
        WebResourceRoot resources = this.context.getResources();
        if (resources != null) {
            return resources.listWebAppPaths(path);
        }
        return null;
    }

    @Override // javax.servlet.ServletContext
    public String getServerInfo() {
        return ServerInfo.getServerInfo();
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Servlet getServlet(String name) {
        return null;
    }

    @Override // javax.servlet.ServletContext
    public String getServletContextName() {
        return this.context.getDisplayName();
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(emptyString);
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return Collections.enumeration(emptyServlet);
    }

    @Override // javax.servlet.ServletContext
    public void log(String message) {
        this.context.getLogger().info(message);
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public void log(Exception exception, String message) {
        this.context.getLogger().error(message, exception);
    }

    @Override // javax.servlet.ServletContext
    public void log(String message, Throwable throwable) {
        this.context.getLogger().error(message, throwable);
    }

    @Override // javax.servlet.ServletContext
    public void removeAttribute(String name) {
        Object value;
        Object[] listeners;
        if (this.readOnlyAttributes.containsKey(name) || (value = this.attributes.remove(name)) == null || (listeners = this.context.getApplicationEventListeners()) == null || listeners.length == 0) {
            return;
        }
        ServletContextAttributeEvent event = new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof ServletContextAttributeListener) {
                ServletContextAttributeListener listener = (ServletContextAttributeListener) listeners[i];
                try {
                    this.context.fireContainerEvent("beforeContextAttributeRemoved", listener);
                    listener.attributeRemoved(event);
                    this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.context.fireContainerEvent("afterContextAttributeRemoved", listener);
                    log(sm.getString("applicationContext.attributeEvent"), t);
                }
            }
        }
    }

    @Override // javax.servlet.ServletContext
    public void setAttribute(String name, Object value) {
        ServletContextAttributeEvent event;
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        if (value == null) {
            removeAttribute(name);
        } else if (!this.readOnlyAttributes.containsKey(name)) {
            Object oldValue = this.attributes.put(name, value);
            boolean replaced = oldValue != null;
            Object[] listeners = this.context.getApplicationEventListeners();
            if (listeners == null || listeners.length == 0) {
                return;
            }
            if (replaced) {
                event = new ServletContextAttributeEvent(this.context.getServletContext(), name, oldValue);
            } else {
                event = new ServletContextAttributeEvent(this.context.getServletContext(), name, value);
            }
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] instanceof ServletContextAttributeListener) {
                    ServletContextAttributeListener listener = (ServletContextAttributeListener) listeners[i];
                    if (replaced) {
                        try {
                            this.context.fireContainerEvent("beforeContextAttributeReplaced", listener);
                            listener.attributeReplaced(event);
                            this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            if (replaced) {
                                this.context.fireContainerEvent("afterContextAttributeReplaced", listener);
                            } else {
                                this.context.fireContainerEvent("afterContextAttributeAdded", listener);
                            }
                            log(sm.getString("applicationContext.attributeEvent"), t);
                        }
                    } else {
                        this.context.fireContainerEvent("beforeContextAttributeAdded", listener);
                        listener.attributeAdded(event);
                        this.context.fireContainerEvent("afterContextAttributeAdded", listener);
                    }
                }
            }
        }
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return addFilter(filterName, className, null);
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return addFilter(filterName, null, filter);
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return addFilter(filterName, filterClass.getName(), null);
    }

    private FilterRegistration.Dynamic addFilter(String filterName, String filterClass, Filter filter) throws IllegalStateException {
        if (filterName == null || filterName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidFilterName", filterName));
        }
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.addFilter.ise", getContextPath()));
        }
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            this.context.addFilterDef(filterDef);
        } else if (filterDef.getFilterName() != null && filterDef.getFilterClass() != null) {
            return null;
        }
        if (filter == null) {
            filterDef.setFilterClass(filterClass);
        } else {
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    @Override // javax.servlet.ServletContext
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        try {
            T filter = (T) this.context.getInstanceManager().newInstance(c.getName());
            return filter;
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new ServletException(e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new ServletException(e2);
        }
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration getFilterRegistration(String filterName) {
        FilterDef filterDef = this.context.findFilterDef(filterName);
        if (filterDef == null) {
            return null;
        }
        return new ApplicationFilterRegistration(filterDef, this.context);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return addServlet(servletName, className, null, null);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return addServlet(servletName, null, servlet, null);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return addServlet(servletName, servletClass.getName(), null, null);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        String jspServletClassName;
        if (jspFile == null || !jspFile.startsWith("/")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addJspFile.iae", jspFile));
        }
        Map<String, String> jspFileInitParams = new HashMap<>();
        Wrapper jspServlet = (Wrapper) this.context.findChild("jsp");
        if (jspServlet == null) {
            jspServletClassName = Constants.JSP_SERVLET_CLASS;
        } else {
            jspServletClassName = jspServlet.getServletClass();
            String[] params = jspServlet.findInitParameters();
            for (String param : params) {
                jspFileInitParams.put(param, jspServlet.findInitParameter(param));
            }
        }
        jspFileInitParams.put("jspFile", jspFile);
        return addServlet(jspName, jspServletClassName, null, jspFileInitParams);
    }

    private ServletRegistration.Dynamic addServlet(String servletName, String servletClass, Servlet servlet, Map<String, String> initParams) throws IllegalStateException {
        if (servletName == null || servletName.equals("")) {
            throw new IllegalArgumentException(sm.getString("applicationContext.invalidServletName", servletName));
        }
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.addServlet.ise", getContextPath()));
        }
        Wrapper wrapper = (Wrapper) this.context.findChild(servletName);
        if (wrapper == null) {
            wrapper = this.context.createWrapper();
            wrapper.setName(servletName);
            this.context.addChild(wrapper);
        } else if (wrapper.getName() != null && wrapper.getServletClass() != null) {
            if (wrapper.isOverridable()) {
                wrapper.setOverridable(false);
            } else {
                return null;
            }
        }
        ServletSecurity annotation = null;
        if (servlet == null) {
            wrapper.setServletClass(servletClass);
            Class<?> clazz = Introspection.loadClass(this.context, servletClass);
            if (clazz != null) {
                annotation = (ServletSecurity) clazz.getAnnotation(ServletSecurity.class);
            }
        } else {
            wrapper.setServletClass(servlet.getClass().getName());
            wrapper.setServlet(servlet);
            if (this.context.wasCreatedDynamicServlet(servlet)) {
                annotation = (ServletSecurity) servlet.getClass().getAnnotation(ServletSecurity.class);
            }
        }
        if (initParams != null) {
            for (Map.Entry<String, String> initParam : initParams.entrySet()) {
                wrapper.addInitParameter(initParam.getKey(), initParam.getValue());
            }
        }
        ServletRegistration.Dynamic registration = new ApplicationServletRegistration(wrapper, this.context);
        if (annotation != null) {
            registration.setServletSecurity(new ServletSecurityElement(annotation));
        }
        return registration;
    }

    @Override // javax.servlet.ServletContext
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        try {
            T servlet = (T) this.context.getInstanceManager().newInstance(c.getName());
            this.context.dynamicServletCreated(servlet);
            return servlet;
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new ServletException(e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new ServletException(e2);
        }
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration getServletRegistration(String servletName) {
        Wrapper wrapper = (Wrapper) this.context.findChild(servletName);
        if (wrapper == null) {
            return null;
        }
        return new ApplicationServletRegistration(wrapper, this.context);
    }

    @Override // javax.servlet.ServletContext
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.defaultSessionTrackingModes;
    }

    private void populateSessionTrackingModes() {
        this.defaultSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        this.supportedSessionTrackingModes = EnumSet.of(SessionTrackingMode.URL);
        if (this.context.getCookies()) {
            this.defaultSessionTrackingModes.add(SessionTrackingMode.COOKIE);
            this.supportedSessionTrackingModes.add(SessionTrackingMode.COOKIE);
        }
        Service s = ((Engine) this.context.getParent().getParent()).getService();
        Connector[] connectors = s.findConnectors();
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getAttribute("SSLEnabled"))) {
                this.supportedSessionTrackingModes.add(SessionTrackingMode.SSL);
                return;
            }
        }
    }

    @Override // javax.servlet.ServletContext
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if (this.sessionTrackingModes != null) {
            return this.sessionTrackingModes;
        }
        return this.defaultSessionTrackingModes;
    }

    @Override // javax.servlet.ServletContext
    public SessionCookieConfig getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }

    @Override // javax.servlet.ServletContext
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.setSessionTracking.ise", getContextPath()));
        }
        for (SessionTrackingMode sessionTrackingMode : sessionTrackingModes) {
            if (!this.supportedSessionTrackingModes.contains(sessionTrackingMode)) {
                throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.invalid", sessionTrackingMode.toString(), getContextPath()));
            }
        }
        if (sessionTrackingModes.contains(SessionTrackingMode.SSL) && sessionTrackingModes.size() > 1) {
            throw new IllegalArgumentException(sm.getString("applicationContext.setSessionTracking.iae.ssl", getContextPath()));
        }
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override // javax.servlet.ServletContext
    public boolean setInitParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException(sm.getString("applicationContext.setAttribute.namenull"));
        }
        if (this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            return this.parameters.putIfAbsent(name, value) == null;
        }
        throw new IllegalStateException(sm.getString("applicationContext.setInitParam.ise", getContextPath()));
    }

    @Override // javax.servlet.ServletContext
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            EventListener listener = createListener(listenerClass);
            addListener((ApplicationContext) listener);
        } catch (ServletException e) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.init", listenerClass.getName()), e);
        }
    }

    @Override // javax.servlet.ServletContext
    public void addListener(String className) {
        try {
            if (this.context.getInstanceManager() != null) {
                Object obj = this.context.getInstanceManager().newInstance(className);
                if (!(obj instanceof EventListener)) {
                    throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", className));
                }
                EventListener listener = (EventListener) obj;
                addListener((ApplicationContext) listener);
            }
        } catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", className), e);
        } catch (ReflectiveOperationException | NamingException e2) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.cnfe", className), e2);
        }
    }

    @Override // javax.servlet.ServletContext
    public <T extends EventListener> void addListener(T t) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.addListener.ise", getContextPath()));
        }
        boolean match = false;
        if ((t instanceof ServletContextAttributeListener) || (t instanceof ServletRequestListener) || (t instanceof ServletRequestAttributeListener) || (t instanceof HttpSessionIdListener) || (t instanceof HttpSessionAttributeListener)) {
            this.context.addApplicationEventListener(t);
            match = true;
        }
        if ((t instanceof HttpSessionListener) || ((t instanceof ServletContextListener) && this.newServletContextListenerAllowed)) {
            this.context.addApplicationLifecycleListener(t);
            match = true;
        }
        if (match) {
            return;
        }
        if (t instanceof ServletContextListener) {
            throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.sclNotAllowed", t.getClass().getName()));
        }
        throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", t.getClass().getName()));
    }

    @Override // javax.servlet.ServletContext
    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        try {
            try {
                T listener = (T) this.context.getInstanceManager().newInstance((Class<?>) c);
                if ((listener instanceof ServletContextListener) || (listener instanceof ServletContextAttributeListener) || (listener instanceof ServletRequestListener) || (listener instanceof ServletRequestAttributeListener) || (listener instanceof HttpSessionListener) || (listener instanceof HttpSessionIdListener) || (listener instanceof HttpSessionAttributeListener)) {
                    return listener;
                }
                throw new IllegalArgumentException(sm.getString("applicationContext.addListener.iae.wrongType", listener.getClass().getName()));
            } catch (ReflectiveOperationException | NamingException e) {
                throw new ServletException(e);
            }
        } catch (InvocationTargetException e2) {
            ExceptionUtils.handleThrowable(e2.getCause());
            throw new ServletException(e2);
        }
    }

    @Override // javax.servlet.ServletContext
    public void declareRoles(String... roleNames) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.addRole.ise", getContextPath()));
        }
        if (roleNames == null) {
            throw new IllegalArgumentException(sm.getString("applicationContext.roles.iae", getContextPath()));
        }
        for (String role : roleNames) {
            if (role == null || "".equals(role)) {
                throw new IllegalArgumentException(sm.getString("applicationContext.role.iae", getContextPath()));
            }
            this.context.addSecurityRole(role);
        }
    }

    @Override // javax.servlet.ServletContext
    public ClassLoader getClassLoader() {
        ClassLoader parent;
        ClassLoader result = this.context.getLoader().getClassLoader();
        if (Globals.IS_SECURITY_ENABLED) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            ClassLoader classLoader = result;
            while (true) {
                parent = classLoader;
                if (parent == null || parent == tccl) {
                    break;
                }
                classLoader = parent.getParent();
            }
            if (parent == null) {
                System.getSecurityManager().checkPermission(new RuntimePermission("getClassLoader"));
            }
        }
        return result;
    }

    @Override // javax.servlet.ServletContext
    public int getEffectiveMajorVersion() {
        return this.context.getEffectiveMajorVersion();
    }

    @Override // javax.servlet.ServletContext
    public int getEffectiveMinorVersion() {
        return this.context.getEffectiveMinorVersion();
    }

    @Override // javax.servlet.ServletContext
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        Map<String, ApplicationFilterRegistration> result = new HashMap<>();
        FilterDef[] filterDefs = this.context.findFilterDefs();
        for (FilterDef filterDef : filterDefs) {
            result.put(filterDef.getFilterName(), new ApplicationFilterRegistration(filterDef, this.context));
        }
        return result;
    }

    @Override // javax.servlet.ServletContext
    public JspConfigDescriptor getJspConfigDescriptor() {
        return this.context.getJspConfigDescriptor();
    }

    @Override // javax.servlet.ServletContext
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        Map<String, ApplicationServletRegistration> result = new HashMap<>();
        Container[] wrappers = this.context.findChildren();
        for (Container wrapper : wrappers) {
            result.put(wrapper.getName(), new ApplicationServletRegistration((Wrapper) wrapper, this.context));
        }
        return result;
    }

    @Override // javax.servlet.ServletContext
    public String getVirtualServerName() {
        Container host = this.context.getParent();
        Container engine = host.getParent();
        return engine.getName() + "/" + host.getName();
    }

    @Override // javax.servlet.ServletContext
    public int getSessionTimeout() {
        return this.context.getSessionTimeout();
    }

    @Override // javax.servlet.ServletContext
    public void setSessionTimeout(int sessionTimeout) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.setSessionTimeout.ise", getContextPath()));
        }
        this.context.setSessionTimeout(sessionTimeout);
    }

    @Override // javax.servlet.ServletContext
    public String getRequestCharacterEncoding() {
        return this.context.getRequestCharacterEncoding();
    }

    @Override // javax.servlet.ServletContext
    public void setRequestCharacterEncoding(String encoding) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.setRequestEncoding.ise", getContextPath()));
        }
        this.context.setRequestCharacterEncoding(encoding);
    }

    @Override // javax.servlet.ServletContext
    public String getResponseCharacterEncoding() {
        return this.context.getResponseCharacterEncoding();
    }

    @Override // javax.servlet.ServletContext
    public void setResponseCharacterEncoding(String encoding) {
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationContext.setResponseEncoding.ise", getContextPath()));
        }
        this.context.setResponseCharacterEncoding(encoding);
    }

    protected StandardContext getContext() {
        return this.context;
    }

    public void clearAttributes() {
        List<String> list = new ArrayList<>();
        for (String s : this.attributes.keySet()) {
            list.add(s);
        }
        for (String key : list) {
            removeAttribute(key);
        }
    }

    public ServletContext getFacade() {
        return this.facade;
    }

    public void setAttributeReadOnly(String name) {
        if (this.attributes.containsKey(name)) {
            this.readOnlyAttributes.put(name, name);
        }
    }

    public void setNewServletContextListenerAllowed(boolean allowed) {
        this.newServletContextListenerAllowed = allowed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationContext$DispatchData.class */
    public static final class DispatchData {
        public MessageBytes uriMB = MessageBytes.newInstance();
        public MappingData mappingData;

        public DispatchData() {
            CharChunk uriCC = this.uriMB.getCharChunk();
            uriCC.setLimit(-1);
            this.mappingData = new MappingData();
        }
    }
}