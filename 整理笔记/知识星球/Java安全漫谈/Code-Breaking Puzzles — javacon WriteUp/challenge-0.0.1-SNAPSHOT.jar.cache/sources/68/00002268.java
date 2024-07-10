package org.springframework.remoting.jaxws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.SOAPFaultException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/JaxWsPortClientInterceptor.class */
public class JaxWsPortClientInterceptor extends LocalJaxWsServiceFactory implements MethodInterceptor, BeanClassLoaderAware, InitializingBean {
    @Nullable
    private Service jaxWsService;
    @Nullable
    private String portName;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String endpointAddress;
    private boolean maintainSession;
    private boolean useSoapAction;
    @Nullable
    private String soapActionUri;
    @Nullable
    private Map<String, Object> customProperties;
    @Nullable
    private WebServiceFeature[] portFeatures;
    @Nullable
    private Class<?> serviceInterface;
    @Nullable
    private QName portQName;
    @Nullable
    private Object portStub;
    private boolean lookupServiceOnStartup = true;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final Object preparationMonitor = new Object();

    public void setJaxWsService(@Nullable Service jaxWsService) {
        this.jaxWsService = jaxWsService;
    }

    @Nullable
    public Service getJaxWsService() {
        return this.jaxWsService;
    }

    public void setPortName(@Nullable String portName) {
        this.portName = portName;
    }

    @Nullable
    public String getPortName() {
        return this.portName;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    public void setEndpointAddress(@Nullable String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    @Nullable
    public String getEndpointAddress() {
        return this.endpointAddress;
    }

    public void setMaintainSession(boolean maintainSession) {
        this.maintainSession = maintainSession;
    }

    public boolean isMaintainSession() {
        return this.maintainSession;
    }

    public void setUseSoapAction(boolean useSoapAction) {
        this.useSoapAction = useSoapAction;
    }

    public boolean isUseSoapAction() {
        return this.useSoapAction;
    }

    public void setSoapActionUri(@Nullable String soapActionUri) {
        this.soapActionUri = soapActionUri;
    }

    @Nullable
    public String getSoapActionUri() {
        return this.soapActionUri;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    public Map<String, Object> getCustomProperties() {
        if (this.customProperties == null) {
            this.customProperties = new HashMap();
        }
        return this.customProperties;
    }

    public void addCustomProperty(String name, Object value) {
        getCustomProperties().put(name, value);
    }

    public void setPortFeatures(WebServiceFeature... features) {
        this.portFeatures = features;
    }

    public void setServiceInterface(@Nullable Class<?> serviceInterface) {
        if (serviceInterface != null) {
            Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
    }

    @Nullable
    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setLookupServiceOnStartup(boolean lookupServiceOnStartup) {
        this.lookupServiceOnStartup = lookupServiceOnStartup;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(@Nullable ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.lookupServiceOnStartup) {
            prepare();
        }
    }

    public void prepare() {
        Class<?> ifc = getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        WebService ann = (WebService) ifc.getAnnotation(WebService.class);
        if (ann != null) {
            applyDefaultsFromAnnotation(ann);
        }
        Service serviceToUse = getJaxWsService();
        if (serviceToUse == null) {
            serviceToUse = createJaxWsService();
        }
        this.portQName = getQName(getPortName() != null ? getPortName() : ifc.getName());
        Object stub = getPortStub(serviceToUse, getPortName() != null ? this.portQName : null);
        preparePortStub(stub);
        this.portStub = stub;
    }

    protected void applyDefaultsFromAnnotation(WebService ann) {
        if (getWsdlDocumentUrl() == null) {
            String wsdl = ann.wsdlLocation();
            if (StringUtils.hasText(wsdl)) {
                try {
                    setWsdlDocumentUrl(new URL(wsdl));
                } catch (MalformedURLException ex) {
                    throw new IllegalStateException("Encountered invalid @Service wsdlLocation value [" + wsdl + "]", ex);
                }
            }
        }
        if (getNamespaceUri() == null) {
            String ns = ann.targetNamespace();
            if (StringUtils.hasText(ns)) {
                setNamespaceUri(ns);
            }
        }
        if (getServiceName() == null) {
            String sn = ann.serviceName();
            if (StringUtils.hasText(sn)) {
                setServiceName(sn);
            }
        }
        if (getPortName() == null) {
            String pn = ann.portName();
            if (StringUtils.hasText(pn)) {
                setPortName(pn);
            }
        }
    }

    protected boolean isPrepared() {
        boolean z;
        synchronized (this.preparationMonitor) {
            z = this.portStub != null;
        }
        return z;
    }

    @Nullable
    protected final QName getPortQName() {
        return this.portQName;
    }

    protected Object getPortStub(Service service, @Nullable QName portQName) {
        return this.portFeatures != null ? portQName != null ? service.getPort(portQName, getServiceInterface(), this.portFeatures) : service.getPort(getServiceInterface(), this.portFeatures) : portQName != null ? service.getPort(portQName, getServiceInterface()) : service.getPort(getServiceInterface());
    }

    protected void preparePortStub(Object stub) {
        HashMap hashMap = new HashMap();
        String username = getUsername();
        if (username != null) {
            hashMap.put("javax.xml.ws.security.auth.username", username);
        }
        String password = getPassword();
        if (password != null) {
            hashMap.put("javax.xml.ws.security.auth.password", password);
        }
        String endpointAddress = getEndpointAddress();
        if (endpointAddress != null) {
            hashMap.put("javax.xml.ws.service.endpoint.address", endpointAddress);
        }
        if (isMaintainSession()) {
            hashMap.put("javax.xml.ws.session.maintain", Boolean.TRUE);
        }
        if (isUseSoapAction()) {
            hashMap.put("javax.xml.ws.soap.http.soapaction.use", Boolean.TRUE);
        }
        String soapActionUri = getSoapActionUri();
        if (soapActionUri != null) {
            hashMap.put("javax.xml.ws.soap.http.soapaction.uri", soapActionUri);
        }
        hashMap.putAll(getCustomProperties());
        if (!hashMap.isEmpty()) {
            if (!(stub instanceof BindingProvider)) {
                throw new RemoteLookupFailureException("Port stub of class [" + stub.getClass().getName() + "] is not a customizable JAX-WS stub: it does not implement interface [javax.xml.ws.BindingProvider]");
            }
            ((BindingProvider) stub).getRequestContext().putAll(hashMap);
        }
    }

    @Nullable
    protected Object getPortStub() {
        return this.portStub;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (AopUtils.isToStringMethod(invocation.getMethod())) {
            return "JAX-WS proxy for port [" + getPortName() + "] of service [" + getServiceName() + "]";
        }
        synchronized (this.preparationMonitor) {
            if (!isPrepared()) {
                prepare();
            }
        }
        return doInvoke(invocation);
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        try {
            return doInvoke(invocation, getPortStub());
        } catch (WebServiceException ex) {
            throw new RemoteAccessException("Could not access remote service at [" + getEndpointAddress() + "]", ex);
        } catch (SOAPFaultException ex2) {
            throw new JaxWsSoapFaultException(ex2);
        } catch (ProtocolException ex3) {
            throw new RemoteConnectFailureException("Could not connect to remote service [" + getEndpointAddress() + "]", ex3);
        }
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation, @Nullable Object portStub) throws Throwable {
        Method method = invocation.getMethod();
        try {
            return method.invoke(portStub, invocation.getArguments());
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        } catch (Throwable ex2) {
            throw new RemoteProxyFailureException("Invocation of stub method failed: " + method, ex2);
        }
    }
}