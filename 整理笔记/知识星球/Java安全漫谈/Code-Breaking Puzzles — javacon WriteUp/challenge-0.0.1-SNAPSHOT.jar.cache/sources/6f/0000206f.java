package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/SimpleClientHttpRequestFactory.class */
public class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory, AsyncClientHttpRequestFactory {
    private static final int DEFAULT_CHUNK_SIZE = 4096;
    @Nullable
    private Proxy proxy;
    private boolean bufferRequestBody = true;
    private int chunkSize = 4096;
    private int connectTimeout = -1;
    private int readTimeout = -1;
    private boolean outputStreaming = true;
    @Nullable
    private AsyncListenableTaskExecutor taskExecutor;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void setBufferRequestBody(boolean bufferRequestBody) {
        this.bufferRequestBody = bufferRequestBody;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setOutputStreaming(boolean outputStreaming) {
        this.outputStreaming = outputStreaming;
    }

    public void setTaskExecutor(AsyncListenableTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override // org.springframework.http.client.ClientHttpRequestFactory
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
        prepareConnection(connection, httpMethod.name());
        if (this.bufferRequestBody) {
            return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
        }
        return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
    }

    @Override // org.springframework.http.client.AsyncClientHttpRequestFactory
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
        Assert.state(this.taskExecutor != null, "Asynchronous execution requires TaskExecutor to be set");
        HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
        prepareConnection(connection, httpMethod.name());
        if (this.bufferRequestBody) {
            return new SimpleBufferingAsyncClientHttpRequest(connection, this.outputStreaming, this.taskExecutor);
        }
        return new SimpleStreamingAsyncClientHttpRequest(connection, this.chunkSize, this.outputStreaming, this.taskExecutor);
    }

    protected HttpURLConnection openConnection(URL url, @Nullable Proxy proxy) throws IOException {
        URLConnection urlConnection = proxy != null ? url.openConnection(proxy) : url.openConnection();
        if (!HttpURLConnection.class.isInstance(urlConnection)) {
            throw new IllegalStateException("HttpURLConnection required for [" + url + "] but got: " + urlConnection);
        }
        return (HttpURLConnection) urlConnection;
    }

    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoInput(true);
        if ("GET".equals(httpMethod)) {
            connection.setInstanceFollowRedirects(true);
        } else {
            connection.setInstanceFollowRedirects(false);
        }
        if (WebContentGenerator.METHOD_POST.equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            connection.setDoOutput(true);
        } else {
            connection.setDoOutput(false);
        }
        connection.setRequestMethod(httpMethod);
    }
}