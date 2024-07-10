package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/support/HttpAccessor.class */
public abstract class HttpAccessor {
    protected final Log logger = HttpLogging.forLogName(getClass());
    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = getRequestFactory().createRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("HTTP " + method.name() + " " + url);
        }
        return request;
    }
}