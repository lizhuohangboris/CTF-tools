package org.springframework.remoting.jaxws;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/AbstractJaxWsServiceExporter.class */
public abstract class AbstractJaxWsServiceExporter implements BeanFactoryAware, InitializingBean, DisposableBean {
    @Nullable
    private Map<String, Object> endpointProperties;
    @Nullable
    private Executor executor;
    @Nullable
    private String bindingType;
    @Nullable
    private WebServiceFeature[] endpointFeatures;
    @Nullable
    private ListableBeanFactory beanFactory;
    private final Set<Endpoint> publishedEndpoints = new LinkedHashSet();

    protected abstract void publishEndpoint(Endpoint endpoint, WebService webService);

    protected abstract void publishEndpoint(Endpoint endpoint, WebServiceProvider webServiceProvider);

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public void setEndpointFeatures(WebServiceFeature... endpointFeatures) {
        this.endpointFeatures = endpointFeatures;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalStateException(getClass().getSimpleName() + " requires a ListableBeanFactory");
        }
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        publishEndpoints();
    }

    public void publishEndpoints() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Set<String> beanNames = new LinkedHashSet<>(this.beanFactory.getBeanDefinitionCount());
        Collections.addAll(beanNames, this.beanFactory.getBeanDefinitionNames());
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            Collections.addAll(beanNames, ((ConfigurableBeanFactory) this.beanFactory).getSingletonNames());
        }
        for (String beanName : beanNames) {
            try {
                Class<?> type = this.beanFactory.getType(beanName);
                if (type != null && !type.isInterface()) {
                    WebService wsAnnotation = (WebService) type.getAnnotation(WebService.class);
                    WebServiceProvider wsProviderAnnotation = (WebServiceProvider) type.getAnnotation(WebServiceProvider.class);
                    if (wsAnnotation != null || wsProviderAnnotation != null) {
                        Endpoint endpoint = createEndpoint(this.beanFactory.getBean(beanName));
                        if (this.endpointProperties != null) {
                            endpoint.setProperties(this.endpointProperties);
                        }
                        if (this.executor != null) {
                            endpoint.setExecutor(this.executor);
                        }
                        if (wsAnnotation != null) {
                            publishEndpoint(endpoint, wsAnnotation);
                        } else {
                            publishEndpoint(endpoint, wsProviderAnnotation);
                        }
                        this.publishedEndpoints.add(endpoint);
                    }
                }
            } catch (CannotLoadBeanClassException e) {
            }
        }
    }

    protected Endpoint createEndpoint(Object bean) {
        if (this.endpointFeatures != null) {
            return Endpoint.create(this.bindingType, bean, this.endpointFeatures);
        }
        return Endpoint.create(this.bindingType, bean);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        for (Endpoint endpoint : this.publishedEndpoints) {
            endpoint.stop();
        }
    }
}