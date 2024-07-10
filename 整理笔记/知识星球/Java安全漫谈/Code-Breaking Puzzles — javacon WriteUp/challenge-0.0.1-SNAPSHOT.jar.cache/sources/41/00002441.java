package org.springframework.web.client;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/DefaultResponseErrorHandler.class */
public class DefaultResponseErrorHandler implements ResponseErrorHandler {
    @Override // org.springframework.web.client.ResponseErrorHandler
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        return statusCode != null && hasError(statusCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean hasError(HttpStatus statusCode) {
        return statusCode.series() == HttpStatus.Series.CLIENT_ERROR || statusCode.series() == HttpStatus.Series.SERVER_ERROR;
    }

    @Override // org.springframework.web.client.ResponseErrorHandler
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        handleError(response, statusCode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        String statusText = response.getStatusText();
        HttpHeaders headers = response.getHeaders();
        byte[] body = getResponseBody(response);
        Charset charset = getCharset(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw HttpClientErrorException.create(statusCode, statusText, headers, body, charset);
            case SERVER_ERROR:
                throw HttpServerErrorException.create(statusCode, statusText, headers, body, charset);
            default:
                throw new UnknownHttpStatusCodeException(statusCode.value(), statusText, headers, body, charset);
        }
    }

    @Deprecated
    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            return contentType.getCharset();
        }
        return null;
    }
}