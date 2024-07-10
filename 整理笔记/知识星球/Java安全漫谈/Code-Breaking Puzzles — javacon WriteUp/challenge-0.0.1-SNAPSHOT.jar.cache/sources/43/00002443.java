package org.springframework.web.client;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/ExtractingResponseErrorHandler.class */
public class ExtractingResponseErrorHandler extends DefaultResponseErrorHandler {
    private List<HttpMessageConverter<?>> messageConverters;
    private final Map<HttpStatus, Class<? extends RestClientException>> statusMapping;
    private final Map<HttpStatus.Series, Class<? extends RestClientException>> seriesMapping;

    public ExtractingResponseErrorHandler() {
        this.messageConverters = Collections.emptyList();
        this.statusMapping = new LinkedHashMap();
        this.seriesMapping = new LinkedHashMap();
    }

    public ExtractingResponseErrorHandler(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = Collections.emptyList();
        this.statusMapping = new LinkedHashMap();
        this.seriesMapping = new LinkedHashMap();
        this.messageConverters = messageConverters;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public void setStatusMapping(Map<HttpStatus, Class<? extends RestClientException>> statusMapping) {
        if (!CollectionUtils.isEmpty(statusMapping)) {
            this.statusMapping.putAll(statusMapping);
        }
    }

    public void setSeriesMapping(Map<HttpStatus.Series, Class<? extends RestClientException>> seriesMapping) {
        if (!CollectionUtils.isEmpty(seriesMapping)) {
            this.seriesMapping.putAll(seriesMapping);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.client.DefaultResponseErrorHandler
    public boolean hasError(HttpStatus statusCode) {
        if (this.statusMapping.containsKey(statusCode)) {
            return this.statusMapping.get(statusCode) != null;
        } else if (this.seriesMapping.containsKey(statusCode.series())) {
            return this.seriesMapping.get(statusCode.series()) != null;
        } else {
            return super.hasError(statusCode);
        }
    }

    @Override // org.springframework.web.client.DefaultResponseErrorHandler
    public void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        if (this.statusMapping.containsKey(statusCode)) {
            extract(this.statusMapping.get(statusCode), response);
        } else if (this.seriesMapping.containsKey(statusCode.series())) {
            extract(this.seriesMapping.get(statusCode.series()), response);
        } else {
            super.handleError(response, statusCode);
        }
    }

    private void extract(@Nullable Class<? extends RestClientException> exceptionClass, ClientHttpResponse response) throws IOException {
        if (exceptionClass == null) {
            return;
        }
        HttpMessageConverterExtractor<? extends RestClientException> extractor = new HttpMessageConverterExtractor<>(exceptionClass, this.messageConverters);
        RestClientException exception = (RestClientException) extractor.extractData(response);
        if (exception != null) {
            throw exception;
        }
    }
}