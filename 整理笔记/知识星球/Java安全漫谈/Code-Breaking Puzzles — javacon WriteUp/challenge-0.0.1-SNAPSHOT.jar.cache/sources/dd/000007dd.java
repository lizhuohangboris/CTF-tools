package org.apache.catalina.core;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationContextFacade.class */
public class ApplicationContextFacade implements ServletContext {
    private final Map<String, Class<?>[]> classCache = new HashMap();
    private final Map<String, Method> objectCache = new ConcurrentHashMap();
    private final ApplicationContext context;

    public ApplicationContextFacade(ApplicationContext context) {
        this.context = context;
        initClassCache();
    }

    private void initClassCache() {
        Class<?>[] clazz = {String.class};
        this.classCache.put("getContext", clazz);
        this.classCache.put("getMimeType", clazz);
        this.classCache.put("getResourcePaths", clazz);
        this.classCache.put("getResource", clazz);
        this.classCache.put("getResourceAsStream", clazz);
        this.classCache.put("getRequestDispatcher", clazz);
        this.classCache.put("getNamedDispatcher", clazz);
        this.classCache.put("getServlet", clazz);
        this.classCache.put("setInitParameter", new Class[]{String.class, String.class});
        this.classCache.put("createServlet", new Class[]{Class.class});
        this.classCache.put("addServlet", new Class[]{String.class, String.class});
        this.classCache.put("createFilter", new Class[]{Class.class});
        this.classCache.put("addFilter", new Class[]{String.class, String.class});
        this.classCache.put("createListener", new Class[]{Class.class});
        this.classCache.put("addListener", clazz);
        this.classCache.put("getFilterRegistration", clazz);
        this.classCache.put("getServletRegistration", clazz);
        this.classCache.put("getInitParameter", clazz);
        this.classCache.put("setAttribute", new Class[]{String.class, Object.class});
        this.classCache.put("removeAttribute", clazz);
        this.classCache.put("getRealPath", clazz);
        this.classCache.put("getAttribute", clazz);
        this.classCache.put("log", clazz);
        this.classCache.put("setSessionTrackingModes", new Class[]{Set.class});
    }

    @Override // javax.servlet.ServletContext
    public ServletContext getContext(String uripath) {
        ServletContext theContext;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            theContext = (ServletContext) doPrivileged("getContext", new Object[]{uripath});
        } else {
            theContext = this.context.getContext(uripath);
        }
        if (theContext != null && (theContext instanceof ApplicationContext)) {
            theContext = ((ApplicationContext) theContext).getFacade();
        }
        return theContext;
    }

    @Override // javax.servlet.ServletContext
    public int getMajorVersion() {
        return this.context.getMajorVersion();
    }

    @Override // javax.servlet.ServletContext
    public int getMinorVersion() {
        return this.context.getMinorVersion();
    }

    @Override // javax.servlet.ServletContext
    public String getMimeType(String file) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getMimeType", new Object[]{file});
        }
        return this.context.getMimeType(file);
    }

    @Override // javax.servlet.ServletContext
    public Set<String> getResourcePaths(String path) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Set) doPrivileged("getResourcePaths", new Object[]{path});
        }
        return this.context.getResourcePaths(path);
    }

    @Override // javax.servlet.ServletContext
    public URL getResource(String path) throws MalformedURLException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                return (URL) invokeMethod(this.context, "getResource", new Object[]{path});
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (t instanceof MalformedURLException) {
                    throw ((MalformedURLException) t);
                }
                return null;
            }
        }
        return this.context.getResource(path);
    }

    @Override // javax.servlet.ServletContext
    public InputStream getResourceAsStream(String path) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (InputStream) doPrivileged("getResourceAsStream", new Object[]{path});
        }
        return this.context.getResourceAsStream(path);
    }

    @Override // javax.servlet.ServletContext
    public RequestDispatcher getRequestDispatcher(String path) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (RequestDispatcher) doPrivileged("getRequestDispatcher", new Object[]{path});
        }
        return this.context.getRequestDispatcher(path);
    }

    @Override // javax.servlet.ServletContext
    public RequestDispatcher getNamedDispatcher(String name) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (RequestDispatcher) doPrivileged("getNamedDispatcher", new Object[]{name});
        }
        return this.context.getNamedDispatcher(name);
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return (Servlet) invokeMethod(this.context, "getServlet", new Object[]{name});
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                }
                return null;
            }
        }
        return this.context.getServlet(name);
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Enumeration) doPrivileged("getServlets", null);
        }
        return this.context.getServlets();
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public Enumeration<String> getServletNames() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Enumeration) doPrivileged("getServletNames", null);
        }
        return this.context.getServletNames();
    }

    @Override // javax.servlet.ServletContext
    public void log(String msg) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("log", new Object[]{msg});
        } else {
            this.context.log(msg);
        }
    }

    @Override // javax.servlet.ServletContext
    @Deprecated
    public void log(Exception exception, String msg) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("log", new Class[]{Exception.class, String.class}, new Object[]{exception, msg});
        } else {
            this.context.log(exception, msg);
        }
    }

    @Override // javax.servlet.ServletContext
    public void log(String message, Throwable throwable) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("log", new Class[]{String.class, Throwable.class}, new Object[]{message, throwable});
        } else {
            this.context.log(message, throwable);
        }
    }

    @Override // javax.servlet.ServletContext
    public String getRealPath(String path) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getRealPath", new Object[]{path});
        }
        return this.context.getRealPath(path);
    }

    @Override // javax.servlet.ServletContext
    public String getServerInfo() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getServerInfo", null);
        }
        return this.context.getServerInfo();
    }

    @Override // javax.servlet.ServletContext
    public String getInitParameter(String name) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getInitParameter", new Object[]{name});
        }
        return this.context.getInitParameter(name);
    }

    @Override // javax.servlet.ServletContext
    public Enumeration<String> getInitParameterNames() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Enumeration) doPrivileged("getInitParameterNames", null);
        }
        return this.context.getInitParameterNames();
    }

    @Override // javax.servlet.ServletContext
    public Object getAttribute(String name) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return doPrivileged("getAttribute", new Object[]{name});
        }
        return this.context.getAttribute(name);
    }

    @Override // javax.servlet.ServletContext
    public Enumeration<String> getAttributeNames() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Enumeration) doPrivileged("getAttributeNames", null);
        }
        return this.context.getAttributeNames();
    }

    @Override // javax.servlet.ServletContext
    public void setAttribute(String name, Object object) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("setAttribute", new Object[]{name, object});
        } else {
            this.context.setAttribute(name, object);
        }
    }

    @Override // javax.servlet.ServletContext
    public void removeAttribute(String name) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("removeAttribute", new Object[]{name});
        } else {
            this.context.removeAttribute(name);
        }
    }

    @Override // javax.servlet.ServletContext
    public String getServletContextName() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getServletContextName", null);
        }
        return this.context.getServletContextName();
    }

    @Override // javax.servlet.ServletContext
    public String getContextPath() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getContextPath", null);
        }
        return this.context.getContextPath();
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (FilterRegistration.Dynamic) doPrivileged("addFilter", new Object[]{filterName, className});
        }
        return this.context.addFilter(filterName, className);
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (FilterRegistration.Dynamic) doPrivileged("addFilter", new Class[]{String.class, Filter.class}, new Object[]{filterName, filter});
        }
        return this.context.addFilter(filterName, filter);
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (FilterRegistration.Dynamic) doPrivileged("addFilter", new Class[]{String.class, Class.class}, new Object[]{filterName, filterClass});
        }
        return this.context.addFilter(filterName, filterClass);
    }

    @Override // javax.servlet.ServletContext
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return (T) invokeMethod(this.context, "createFilter", new Object[]{c});
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                }
                return null;
            }
        }
        return (T) this.context.createFilter(c);
    }

    @Override // javax.servlet.ServletContext
    public FilterRegistration getFilterRegistration(String filterName) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (FilterRegistration) doPrivileged("getFilterRegistration", new Object[]{filterName});
        }
        return this.context.getFilterRegistration(filterName);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ServletRegistration.Dynamic) doPrivileged("addServlet", new Object[]{servletName, className});
        }
        return this.context.addServlet(servletName, className);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ServletRegistration.Dynamic) doPrivileged("addServlet", new Class[]{String.class, Servlet.class}, new Object[]{servletName, servlet});
        }
        return this.context.addServlet(servletName, servlet);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ServletRegistration.Dynamic) doPrivileged("addServlet", new Class[]{String.class, Class.class}, new Object[]{servletName, servletClass});
        }
        return this.context.addServlet(servletName, servletClass);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ServletRegistration.Dynamic) doPrivileged("addJspFile", new Object[]{jspName, jspFile});
        }
        return this.context.addJspFile(jspName, jspFile);
    }

    @Override // javax.servlet.ServletContext
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return (T) invokeMethod(this.context, "createServlet", new Object[]{c});
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                }
                return null;
            }
        }
        return (T) this.context.createServlet(c);
    }

    @Override // javax.servlet.ServletContext
    public ServletRegistration getServletRegistration(String servletName) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ServletRegistration) doPrivileged("getServletRegistration", new Object[]{servletName});
        }
        return this.context.getServletRegistration(servletName);
    }

    @Override // javax.servlet.ServletContext
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Set) doPrivileged("getDefaultSessionTrackingModes", null);
        }
        return this.context.getDefaultSessionTrackingModes();
    }

    @Override // javax.servlet.ServletContext
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Set) doPrivileged("getEffectiveSessionTrackingModes", null);
        }
        return this.context.getEffectiveSessionTrackingModes();
    }

    @Override // javax.servlet.ServletContext
    public SessionCookieConfig getSessionCookieConfig() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (SessionCookieConfig) doPrivileged("getSessionCookieConfig", null);
        }
        return this.context.getSessionCookieConfig();
    }

    @Override // javax.servlet.ServletContext
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("setSessionTrackingModes", new Object[]{sessionTrackingModes});
        } else {
            this.context.setSessionTrackingModes(sessionTrackingModes);
        }
    }

    @Override // javax.servlet.ServletContext
    public boolean setInitParameter(String name, String value) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return ((Boolean) doPrivileged("setInitParameter", new Object[]{name, value})).booleanValue();
        }
        return this.context.setInitParameter(name, value);
    }

    @Override // javax.servlet.ServletContext
    public void addListener(Class<? extends EventListener> listenerClass) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("addListener", new Class[]{Class.class}, new Object[]{listenerClass});
        } else {
            this.context.addListener(listenerClass);
        }
    }

    @Override // javax.servlet.ServletContext
    public void addListener(String className) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("addListener", new Object[]{className});
        } else {
            this.context.addListener(className);
        }
    }

    @Override // javax.servlet.ServletContext
    public <T extends EventListener> void addListener(T t) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("addListener", new Class[]{EventListener.class}, new Object[]{t});
        } else {
            this.context.addListener((ApplicationContext) t);
        }
    }

    @Override // javax.servlet.ServletContext
    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                return (T) invokeMethod(this.context, "createListener", new Object[]{c});
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                }
                return null;
            }
        }
        return (T) this.context.createListener(c);
    }

    @Override // javax.servlet.ServletContext
    public void declareRoles(String... roleNames) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("declareRoles", new Object[]{roleNames});
        } else {
            this.context.declareRoles(roleNames);
        }
    }

    @Override // javax.servlet.ServletContext
    public ClassLoader getClassLoader() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (ClassLoader) doPrivileged("getClassLoader", null);
        }
        return this.context.getClassLoader();
    }

    @Override // javax.servlet.ServletContext
    public int getEffectiveMajorVersion() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return ((Integer) doPrivileged("getEffectiveMajorVersion", null)).intValue();
        }
        return this.context.getEffectiveMajorVersion();
    }

    @Override // javax.servlet.ServletContext
    public int getEffectiveMinorVersion() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return ((Integer) doPrivileged("getEffectiveMinorVersion", null)).intValue();
        }
        return this.context.getEffectiveMinorVersion();
    }

    @Override // javax.servlet.ServletContext
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Map) doPrivileged("getFilterRegistrations", null);
        }
        return this.context.getFilterRegistrations();
    }

    @Override // javax.servlet.ServletContext
    public JspConfigDescriptor getJspConfigDescriptor() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (JspConfigDescriptor) doPrivileged("getJspConfigDescriptor", null);
        }
        return this.context.getJspConfigDescriptor();
    }

    @Override // javax.servlet.ServletContext
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (Map) doPrivileged("getServletRegistrations", null);
        }
        return this.context.getServletRegistrations();
    }

    @Override // javax.servlet.ServletContext
    public String getVirtualServerName() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getVirtualServerName", null);
        }
        return this.context.getVirtualServerName();
    }

    @Override // javax.servlet.ServletContext
    public int getSessionTimeout() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return ((Integer) doPrivileged("getSessionTimeout", null)).intValue();
        }
        return this.context.getSessionTimeout();
    }

    @Override // javax.servlet.ServletContext
    public void setSessionTimeout(int sessionTimeout) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("setSessionTimeout", new Object[]{Integer.valueOf(sessionTimeout)});
        } else {
            this.context.setSessionTimeout(sessionTimeout);
        }
    }

    @Override // javax.servlet.ServletContext
    public String getRequestCharacterEncoding() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getRequestCharacterEncoding", null);
        }
        return this.context.getRequestCharacterEncoding();
    }

    @Override // javax.servlet.ServletContext
    public void setRequestCharacterEncoding(String encoding) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("setRequestCharacterEncoding", new Object[]{encoding});
        } else {
            this.context.setRequestCharacterEncoding(encoding);
        }
    }

    @Override // javax.servlet.ServletContext
    public String getResponseCharacterEncoding() {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return (String) doPrivileged("getResponseCharacterEncoding", null);
        }
        return this.context.getResponseCharacterEncoding();
    }

    @Override // javax.servlet.ServletContext
    public void setResponseCharacterEncoding(String encoding) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            doPrivileged("setResponseCharacterEncoding", new Object[]{encoding});
        } else {
            this.context.setResponseCharacterEncoding(encoding);
        }
    }

    private Object doPrivileged(String methodName, Object[] params) {
        try {
            return invokeMethod(this.context, methodName, params);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    private Object invokeMethod(ApplicationContext appContext, String methodName, Object[] params) throws Throwable {
        try {
            try {
                Method method = this.objectCache.get(methodName);
                if (method == null) {
                    method = appContext.getClass().getMethod(methodName, this.classCache.get(methodName));
                    this.objectCache.put(methodName, method);
                }
                return executeMethod(method, appContext, params);
            } catch (Exception ex) {
                handleException(ex);
                return null;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private Object doPrivileged(String methodName, Class<?>[] clazz, Object[] params) {
        try {
            try {
                Method method = this.context.getClass().getMethod(methodName, clazz);
                return executeMethod(method, this.context, params);
            } catch (Exception ex) {
                handleException(ex);
                return null;
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private Object executeMethod(Method method, ApplicationContext context, Object[] params) throws PrivilegedActionException, IllegalAccessException, InvocationTargetException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new PrivilegedExecuteMethod(method, context, params));
        }
        return method.invoke(context, params);
    }

    private void handleException(Exception ex) throws Throwable {
        Throwable realException;
        if (ex instanceof PrivilegedActionException) {
            ex = ((PrivilegedActionException) ex).getException();
        }
        if (ex instanceof InvocationTargetException) {
            realException = ex.getCause();
            if (realException == null) {
                realException = ex;
            }
        } else {
            realException = ex;
        }
        throw realException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationContextFacade$PrivilegedExecuteMethod.class */
    public static class PrivilegedExecuteMethod implements PrivilegedExceptionAction<Object> {
        private final Method method;
        private final ApplicationContext context;
        private final Object[] params;

        public PrivilegedExecuteMethod(Method method, ApplicationContext context, Object[] params) {
            this.method = method;
            this.context = context;
            this.params = params;
        }

        @Override // java.security.PrivilegedExceptionAction
        public Object run() throws Exception {
            return this.method.invoke(this.context, this.params);
        }
    }
}