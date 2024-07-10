package org.springframework.remoting.jaxws;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/LocalJaxWsServiceFactory.class */
public class LocalJaxWsServiceFactory {
    @Nullable
    private URL wsdlDocumentUrl;
    @Nullable
    private String namespaceUri;
    @Nullable
    private String serviceName;
    @Nullable
    private WebServiceFeature[] serviceFeatures;
    @Nullable
    private Executor executor;
    @Nullable
    private HandlerResolver handlerResolver;

    public void setWsdlDocumentUrl(@Nullable URL wsdlDocumentUrl) {
        this.wsdlDocumentUrl = wsdlDocumentUrl;
    }

    public void setWsdlDocumentResource(Resource wsdlDocumentResource) throws IOException {
        Assert.notNull(wsdlDocumentResource, "WSDL Resource must not be null");
        this.wsdlDocumentUrl = wsdlDocumentResource.getURL();
    }

    @Nullable
    public URL getWsdlDocumentUrl() {
        return this.wsdlDocumentUrl;
    }

    public void setNamespaceUri(@Nullable String namespaceUri) {
        this.namespaceUri = namespaceUri != null ? namespaceUri.trim() : null;
    }

    @Nullable
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public void setServiceName(@Nullable String serviceName) {
        this.serviceName = serviceName;
    }

    @Nullable
    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceFeatures(WebServiceFeature... serviceFeatures) {
        this.serviceFeatures = serviceFeatures;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setHandlerResolver(HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    public Service createJaxWsService() {
        Service create;
        Service service;
        Service create2;
        Assert.notNull(this.serviceName, "No service name specified");
        if (this.serviceFeatures != null) {
            if (this.wsdlDocumentUrl != null) {
                create2 = Service.create(this.wsdlDocumentUrl, getQName(this.serviceName), this.serviceFeatures);
            } else {
                create2 = Service.create(getQName(this.serviceName), this.serviceFeatures);
            }
            service = create2;
        } else {
            if (this.wsdlDocumentUrl != null) {
                create = Service.create(this.wsdlDocumentUrl, getQName(this.serviceName));
            } else {
                create = Service.create(getQName(this.serviceName));
            }
            service = create;
        }
        if (this.executor != null) {
            service.setExecutor(this.executor);
        }
        if (this.handlerResolver != null) {
            service.setHandlerResolver(this.handlerResolver);
        }
        return service;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public QName getQName(String name) {
        return getNamespaceUri() != null ? new QName(getNamespaceUri(), name) : new QName(name);
    }
}