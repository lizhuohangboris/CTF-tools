package org.springframework.remoting.jaxws;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.List;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.lang.UsesSunHttpServer;
import org.springframework.util.Assert;

@Deprecated
@UsesSunHttpServer
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/jaxws/SimpleHttpServerJaxWsServiceExporter.class */
public class SimpleHttpServerJaxWsServiceExporter extends AbstractJaxWsServiceExporter {
    @Nullable
    private HttpServer server;
    @Nullable
    private String hostname;
    @Nullable
    private List<Filter> filters;
    @Nullable
    private Authenticator authenticator;
    protected final Log logger = LogFactory.getLog(getClass());
    private int port = 8080;
    private int backlog = -1;
    private int shutdownDelay = 0;
    private String basePath = "/";
    private boolean localServer = false;

    public void setServer(HttpServer server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setShutdownDelay(int shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.server == null) {
            InetSocketAddress address = this.hostname != null ? new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port);
            HttpServer server = HttpServer.create(address, this.backlog);
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Starting HttpServer at address " + address);
            }
            server.start();
            this.server = server;
            this.localServer = true;
        }
        super.afterPropertiesSet();
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter
    protected void publishEndpoint(Endpoint endpoint, WebService annotation) {
        endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter
    protected void publishEndpoint(Endpoint endpoint, WebServiceProvider annotation) {
        endpoint.publish(buildHttpContext(endpoint, annotation.serviceName()));
    }

    protected HttpContext buildHttpContext(Endpoint endpoint, String serviceName) {
        Assert.state(this.server != null, "No HttpServer available");
        String fullPath = calculateEndpointPath(endpoint, serviceName);
        HttpContext httpContext = this.server.createContext(fullPath);
        if (this.filters != null) {
            httpContext.getFilters().addAll(this.filters);
        }
        if (this.authenticator != null) {
            httpContext.setAuthenticator(this.authenticator);
        }
        return httpContext;
    }

    protected String calculateEndpointPath(Endpoint endpoint, String serviceName) {
        return this.basePath + serviceName;
    }

    @Override // org.springframework.remoting.jaxws.AbstractJaxWsServiceExporter, org.springframework.beans.factory.DisposableBean
    public void destroy() {
        super.destroy();
        if (this.server != null && this.localServer) {
            this.logger.info("Stopping HttpServer");
            this.server.stop(this.shutdownDelay);
        }
    }
}