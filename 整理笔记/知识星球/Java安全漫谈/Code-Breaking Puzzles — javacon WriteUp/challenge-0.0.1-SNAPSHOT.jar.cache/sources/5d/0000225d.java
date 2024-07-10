package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/HttpComponentsHttpInvokerRequestExecutor.class */
public class HttpComponentsHttpInvokerRequestExecutor extends AbstractHttpInvokerRequestExecutor {
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 60000;
    private HttpClient httpClient;
    @Nullable
    private RequestConfig requestConfig;

    public HttpComponentsHttpInvokerRequestExecutor() {
        this(createDefaultHttpClient(), RequestConfig.custom().setSocketTimeout(60000).build());
    }

    public HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient) {
        this(httpClient, null);
    }

    private HttpComponentsHttpInvokerRequestExecutor(HttpClient httpClient, @Nullable RequestConfig requestConfig) {
        this.httpClient = httpClient;
        this.requestConfig = requestConfig;
    }

    private static HttpClient createDefaultHttpClient() {
        Registry<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(5);
        return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setConnectTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = cloneRequestConfig().setConnectTimeout(timeout).build();
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.requestConfig = cloneRequestConfig().setConnectionRequestTimeout(connectionRequestTimeout).build();
    }

    public void setReadTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout must be a non-negative value");
        this.requestConfig = cloneRequestConfig().setSocketTimeout(timeout).build();
    }

    private RequestConfig.Builder cloneRequestConfig() {
        return this.requestConfig != null ? RequestConfig.copy(this.requestConfig) : RequestConfig.custom();
    }

    @Override // org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor
    protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        HttpPost postMethod = createHttpPost(config);
        setRequestBody(config, postMethod, baos);
        try {
            HttpResponse response = executeHttpPost(config, getHttpClient(), postMethod);
            validateResponse(config, response);
            InputStream responseBody = getResponseBody(config, response);
            RemoteInvocationResult readRemoteInvocationResult = readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
            postMethod.releaseConnection();
            return readRemoteInvocationResult;
        } catch (Throwable th) {
            postMethod.releaseConnection();
            throw th;
        }
    }

    protected HttpPost createHttpPost(HttpInvokerClientConfiguration config) throws IOException {
        Locale locale;
        HttpPost httpPost = new HttpPost(config.getServiceUrl());
        RequestConfig requestConfig = createRequestConfig(config);
        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null && (locale = localeContext.getLocale()) != null) {
            httpPost.addHeader(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag());
        }
        if (isAcceptGzipEncoding()) {
            httpPost.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        }
        return httpPost;
    }

    @Nullable
    protected RequestConfig createRequestConfig(HttpInvokerClientConfiguration config) {
        Configurable httpClient = getHttpClient();
        if (httpClient instanceof Configurable) {
            RequestConfig clientRequestConfig = httpClient.getConfig();
            return mergeRequestConfig(clientRequestConfig);
        }
        return this.requestConfig;
    }

    private RequestConfig mergeRequestConfig(RequestConfig defaultRequestConfig) {
        if (this.requestConfig == null) {
            return defaultRequestConfig;
        }
        RequestConfig.Builder builder = RequestConfig.copy(defaultRequestConfig);
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

    protected void setRequestBody(HttpInvokerClientConfiguration config, HttpPost httpPost, ByteArrayOutputStream baos) throws IOException {
        ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
        entity.setContentType(getContentType());
        httpPost.setEntity(entity);
    }

    protected HttpResponse executeHttpPost(HttpInvokerClientConfiguration config, HttpClient httpClient, HttpPost httpPost) throws IOException {
        return httpClient.execute(httpPost);
    }

    protected void validateResponse(HttpInvokerClientConfiguration config, HttpResponse response) throws IOException {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + status.getStatusCode() + ", status message = [" + status.getReasonPhrase() + "]");
        }
    }

    protected InputStream getResponseBody(HttpInvokerClientConfiguration config, HttpResponse httpResponse) throws IOException {
        if (isGzipResponse(httpResponse)) {
            return new GZIPInputStream(httpResponse.getEntity().getContent());
        }
        return httpResponse.getEntity().getContent();
    }

    protected boolean isGzipResponse(HttpResponse httpResponse) {
        Header encodingHeader = httpResponse.getFirstHeader(HttpHeaders.CONTENT_ENCODING);
        return (encodingHeader == null || encodingHeader.getValue() == null || !encodingHeader.getValue().toLowerCase().contains("gzip")) ? false : true;
    }
}