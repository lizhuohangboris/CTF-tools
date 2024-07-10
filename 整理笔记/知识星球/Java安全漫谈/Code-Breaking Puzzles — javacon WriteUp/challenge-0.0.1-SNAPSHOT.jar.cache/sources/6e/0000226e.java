package org.springframework.remoting.jaxws;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/SimpleJaxWsServiceExporter.class */
public class SimpleJaxWsServiceExporter extends AbstractJaxWsServiceExporter {
    public static final String DEFAULT_BASE_ADDRESS = "http://localhost:8080/";
    private String baseAddress = DEFAULT_BASE_ADDRESS;

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter
    protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
        endpoint.publish(calculateEndpointAddress(endpoint, annotation.serviceName()));
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter
    protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
        endpoint.publish(calculateEndpointAddress(endpoint, annotation.serviceName()));
    }

    protected String calculateEndpointAddress(Endpoint endpoint, String serviceName) {
        String fullAddress = this.baseAddress + serviceName;
        if (endpoint.getClass().getName().startsWith("weblogic.")) {
            fullAddress = fullAddress + "/";
        }
        return fullAddress;
    }
}