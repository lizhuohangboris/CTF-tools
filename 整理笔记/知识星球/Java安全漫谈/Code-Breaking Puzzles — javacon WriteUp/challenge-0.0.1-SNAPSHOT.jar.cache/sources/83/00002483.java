package org.springframework.web.context.request;

import java.lang.reflect.Method;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/context/request/FacesRequestAttributes.class */
public class FacesRequestAttributes implements RequestAttributes {
    private static final Log logger = LogFactory.getLog(FacesRequestAttributes.class);
    private final FacesContext facesContext;

    public FacesRequestAttributes(FacesContext facesContext) {
        Assert.notNull(facesContext, "FacesContext must not be null");
        this.facesContext = facesContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FacesContext getFacesContext() {
        return this.facesContext;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ExternalContext getExternalContext() {
        return getFacesContext().getExternalContext();
    }

    protected Map<String, Object> getAttributeMap(int scope) {
        if (scope == 0) {
            return getExternalContext().getRequestMap();
        }
        return getExternalContext().getSessionMap();
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getAttribute(String name, int scope) {
        return getAttributeMap(scope).get(name);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void setAttribute(String name, Object value, int scope) {
        getAttributeMap(scope).put(name, value);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void removeAttribute(String name, int scope) {
        getAttributeMap(scope).remove(name);
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String[] getAttributeNames(int scope) {
        return StringUtils.toStringArray(getAttributeMap(scope).keySet());
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        if (logger.isWarnEnabled()) {
            logger.warn("Could not register destruction callback [" + callback + "] for attribute '" + name + "' because FacesRequestAttributes does not support such callbacks");
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object resolveReference(String key) {
        if ("request".equals(key)) {
            return getExternalContext().getRequest();
        }
        if ("session".equals(key)) {
            return getExternalContext().getSession(true);
        }
        if ("application".equals(key)) {
            return getExternalContext().getContext();
        }
        if ("requestScope".equals(key)) {
            return getExternalContext().getRequestMap();
        }
        if ("sessionScope".equals(key)) {
            return getExternalContext().getSessionMap();
        }
        if ("applicationScope".equals(key)) {
            return getExternalContext().getApplicationMap();
        }
        if ("facesContext".equals(key)) {
            return getFacesContext();
        }
        if ("cookie".equals(key)) {
            return getExternalContext().getRequestCookieMap();
        }
        if ("header".equals(key)) {
            return getExternalContext().getRequestHeaderMap();
        }
        if ("headerValues".equals(key)) {
            return getExternalContext().getRequestHeaderValuesMap();
        }
        if ("param".equals(key)) {
            return getExternalContext().getRequestParameterMap();
        }
        if ("paramValues".equals(key)) {
            return getExternalContext().getRequestParameterValuesMap();
        }
        if ("initParam".equals(key)) {
            return getExternalContext().getInitParameterMap();
        }
        if ("view".equals(key)) {
            return getFacesContext().getViewRoot();
        }
        if ("viewScope".equals(key)) {
            return getFacesContext().getViewRoot().getViewMap();
        }
        if ("flash".equals(key)) {
            return getExternalContext().getFlash();
        }
        if (DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE.equals(key)) {
            return getFacesContext().getApplication().getResourceHandler();
        }
        return null;
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public String getSessionId() {
        Object session = getExternalContext().getSession(true);
        try {
            Method getIdMethod = session.getClass().getMethod("getId", new Class[0]);
            return String.valueOf(ReflectionUtils.invokeMethod(getIdMethod, session));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Session object [" + session + "] does not have a getId() method");
        }
    }

    @Override // org.springframework.web.context.request.RequestAttributes
    public Object getSessionMutex() {
        ExternalContext externalContext = getExternalContext();
        Object session = externalContext.getSession(true);
        Object mutex = externalContext.getSessionMap().get(WebUtils.SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session != null ? session : externalContext;
        }
        return mutex;
    }
}