package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsClientHttpRequestFactory.class */
public class HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean {
    private HttpClient httpClient;
    @Nullable
    private RequestConfig requestConfig;
    private boolean bufferRequestBody;

    public HttpComponentsClientHttpRequestFactory() {
        this.bufferRequestBody = true;
        this.httpClient = HttpClients.createSystem();
    }

    public HttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        this.bufferRequestBody = true;
        this.httpClient = httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = requestConfigBuilder().setConnectTimeout(timeout).build();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.requestConfig = requestConfigBuilder().setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    public void setReadTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = requestConfigBuilder().setSocketTimeout(timeout).build();
    }

    public void setBufferRequestBody(boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpClient client = getHttpClient();
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
        if (this.bufferRequestBody) {
            return new HttpComponentsClientHttpRequest(client, httpRequest, context);
        }
        return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
    }

    private RequestConfig.Builder requestConfigBuilder() {
        return this.requestConfig != null ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public RequestConfig createRequestConfig(Object client) {
        if (client instanceof Configurable) {
            RequestConfig clientRequestConfig = ((Configurable) client).getConfig();
            return mergeRequestConfig(clientRequestConfig);
        }
        return this.requestConfig;
    }

    protected RequestConfig mergeRequestConfig(RequestConfig clientConfig) {
        if (this.requestConfig == null) {
            return clientConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy(clientConfig);
        int connectTimeout = this.requestConfig.getConnectTimeout();
        if (connectTimeout >= 0) {
            builder.setConnectTimeout(connectTimeout);
        }
        int connectionRequestTimeout = this.requestConfig.getConnectionRequestTimeout();
        if (connectionRequestTimeout >= 0) {
            builder.setConnectionRequestTimeout(connectionRequestTimeout);
        }
        int socketTimeout = this.requestConfig.getSocketTimeout();
        if (socketTimeout >= 0) {
            builder.setSocketTimeout(socketTimeout);
        }
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        switch (httpMethod) {
            case GET:
                return new HttpGet(uri);
            case HEAD:
                return new HttpHead(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case PATCH:
                return new HttpPatch(uri);
            case DELETE:
                return new HttpDelete(uri);
            case OPTIONS:
                return new HttpOptions(uri);
            case TRACE:
                return new HttpTrace(uri);
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void postProcessHttpRequest(HttpUriRequest request) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return null;
    }

    public void destroy() throws Exception {
        Closeable httpClient = getHttpClient();
        if (httpClient instanceof Closeable) {
            httpClient.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/HttpComponentsClientHttpRequestFactory$HttpDelete.class */
    public static class HttpDelete extends HttpEntityEnclosingRequestBase {
        public HttpDelete(URI uri) {
            setURI(uri);
        }

        public String getMethod() {
            return "DELETE";
        }
    }
}