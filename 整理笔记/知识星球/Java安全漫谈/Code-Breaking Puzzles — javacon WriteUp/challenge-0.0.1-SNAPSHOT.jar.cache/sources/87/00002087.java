package org.springframework.http.client.support;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/client/support/BasicAuthenticationInterceptor.class */
public class BasicAuthenticationInterceptor implements ClientHttpRequestInterceptor {
    private final String username;
    private final String password;
    @Nullable
    private final Charset charset;

    public BasicAuthenticationInterceptor(String username, String password) {
        this(username, password, null);
    }

    public BasicAuthenticationInterceptor(String username, String password, @Nullable Charset charset) {
        Assert.doesNotContain(username, ":", "Username must not contain a colon");
        this.username = username;
        this.password = password;
        this.charset = charset;
    }

    @Override // org.springframework.http.client.ClientHttpRequestInterceptor
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey("Authorization")) {
            headers.setBasicAuth(this.username, this.password, this.charset);
        }
        return execution.execute(request, body);
    }
}