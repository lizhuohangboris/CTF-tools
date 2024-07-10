package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

@Deprecated
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsAsyncClientHttpRequestFactory.class */
public class HttpComponentsAsyncClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory implements AsyncClientHttpRequestFactory, InitializingBean {
    private HttpAsyncClient asyncClient;

    public HttpComponentsAsyncClientHttpRequestFactory() {
        this.asyncClient = HttpAsyncClients.createSystem();
    }

    public HttpComponentsAsyncClientHttpRequestFactory(HttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(HttpClient httpClient, HttpAsyncClient asyncClient) {
        super(httpClient);
        this.asyncClient = asyncClient;
    }

    public HttpComponentsAsyncClientHttpRequestFactory(CloseableHttpClient httpClient, CloseableHttpAsyncClient asyncClient) {
        super(httpClient);
        this.asyncClient = asyncClient;
    }

    public void setAsyncClient(HttpAsyncClient asyncClient) {
        Assert.notNull(asyncClient, "HttpAsyncClient must not be null");
        this.asyncClient = asyncClient;
    }

    public HttpAsyncClient getAsyncClient() {
        return this.asyncClient;
    }

    @Deprecated
    public void setHttpAsyncClient(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    @Deprecated
    public CloseableHttpAsyncClient getHttpAsyncClient() {
        Assert.state(this.asyncClient instanceof CloseableHttpAsyncClient, "No CloseableHttpAsyncClient - use getAsyncClient() instead");
        return this.asyncClient;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        startAsyncClient();
    }

    private HttpAsyncClient startAsyncClient() {
        CloseableHttpAsyncClient asyncClient = getAsyncClient();
        if (asyncClient instanceof CloseableHttpAsyncClient) {
            CloseableHttpAsyncClient closeableAsyncClient = asyncClient;
            if (!closeableAsyncClient.isRunning()) {
                closeableAsyncClient.start();
            }
        }
        return asyncClient;
    }

    @Override // org.springframework.http.client.AsyncClientHttpRequestFactory
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpAsyncClient client = startAsyncClient();
        HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        postProcessHttpRequest(httpRequest);
        HttpContext context = createHttpContext(httpMethod, uri);
        if (context == null) {
            context = HttpClientContext.create();
        }
        if (context.getAttribute("http.request-config") == null) {
            RequestConfig config = null;
            if (httpRequest instanceof Configurable) {
                config = ((Configurable) httpRequest).getConfig();
            }
            if (config == null) {
                config = createRequestConfig(client);
            }
            if (config != null) {
                context.setAttribute("http.request-config", config);
            }
        }
        return new HttpComponentsAsyncClientHttpRequest(client, httpRequest, context);
    }

    @Override // org.springframework.http.client.HttpComponentsClientHttpRequestFactory, org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        try {
            super.destroy();
        } finally {
            Closeable asyncClient = getAsyncClient();
            if (asyncClient instanceof Closeable) {
                asyncClient.close();
            }
        }
    }
}