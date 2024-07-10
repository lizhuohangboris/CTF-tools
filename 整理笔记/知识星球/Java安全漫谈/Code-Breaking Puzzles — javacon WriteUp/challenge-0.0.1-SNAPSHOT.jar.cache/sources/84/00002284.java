package org.springframework.remoting.support;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/support/RemoteExporter.class */
public abstract class RemoteExporter extends RemotingSupport {
    private Object service;
    private Class<?> serviceInterface;
    private Boolean registerTraceInterceptor;
    private Object[] interceptors;

    public void setService(Object service) {
        this.service = service;
    }

    public Object getService() {
        return this.service;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "'serviceInterface' must not be null");
        Assert.isTrue(serviceInterface.isInterface(), "'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setRegisterTraceInterceptor(boolean registerTraceInterceptor) {
        this.registerTraceInterceptor = Boolean.valueOf(registerTraceInterceptor);
    }

    public void setInterceptors(Object[] interceptors) {
        this.interceptors = interceptors;
    }

    public void checkService() throws IllegalArgumentException {
        Assert.notNull(getService(), "Property 'service' is required");
    }

    public void checkServiceInterface() throws IllegalArgumentException {
        Class<?> serviceInterface = getServiceInterface();
        Assert.notNull(serviceInterface, "Property 'serviceInterface' is required");
        Object service = getService();
        if (service instanceof String) {
            throw new IllegalArgumentException("Service [" + service + "] is a String rather than an actual service reference: Have you accidentally specified the service bean name as value instead of as reference?");
        }
        if (!serviceInterface.isInstance(service)) {
            throw new IllegalArgumentException("Service interface [" + serviceInterface.getName() + "] needs to be implemented by service [" + service + "] of class [" + service.getClass().getName() + "]");
        }
    }

    public Object getProxyForService() {
        Object[] objArr;
        checkService();
        checkServiceInterface();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(getServiceInterface());
        if (this.registerTraceInterceptor == null ? this.interceptors == null : this.registerTraceInterceptor.booleanValue()) {
            proxyFactory.addAdvice(new RemoteInvocationTraceInterceptor(getExporterName()));
        }
        if (this.interceptors != null) {
            AdvisorAdapterRegistry adapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
            for (Object interceptor : this.interceptors) {
                proxyFactory.addAdvisor(adapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.setTarget(getService());
        proxyFactory.setOpaque(true);
        return proxyFactory.getProxy(getBeanClassLoader());
    }

    protected String getExporterName() {
        return ClassUtils.getShortName(getClass());
    }
}