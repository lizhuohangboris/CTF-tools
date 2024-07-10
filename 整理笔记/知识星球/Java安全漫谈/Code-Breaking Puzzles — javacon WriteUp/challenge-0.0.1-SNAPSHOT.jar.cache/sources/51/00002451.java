package org.springframework.web.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/client/HttpMessageConverterExtractor.class */
public class HttpMessageConverterExtractor<T> implements ResponseExtractor<T> {
    private final Type responseType;
    @Nullable
    private final Class<T> responseClass;
    private final List<HttpMessageConverter<?>> messageConverters;
    private final Log logger;

    public HttpMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
        this((Type) responseType, messageConverters);
    }

    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
        this(responseType, messageConverters, LogFactory.getLog(HttpMessageConverterExtractor.class));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters, Log logger) {
        Assert.notNull(responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.responseType = responseType;
        this.responseClass = responseType instanceof Class ? (Class) responseType : null;
        this.messageConverters = messageConverters;
        this.logger = logger;
    }

    @Override // org.springframework.web.client.ResponseExtractor
    public T extractData(ClientHttpResponse response) throws IOException {
        MessageBodyClientHttpResponseWrapper responseWrapper = new MessageBodyClientHttpResponseWrapper(response);
        if (!responseWrapper.hasMessageBody() || responseWrapper.hasEmptyMessageBody()) {
            return null;
        }
        MediaType contentType = getContentType(responseWrapper);
        try {
            for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
                if (messageConverter instanceof GenericHttpMessageConverter) {
                    GenericHttpMessageConverter<?> genericMessageConverter = (GenericHttpMessageConverter) messageConverter;
                    if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
                        if (this.logger.isDebugEnabled()) {
                            ResolvableType resolvableType = ResolvableType.forType(this.responseType);
                            this.logger.debug("Reading to [" + resolvableType + "]");
                        }
                        return (T) genericMessageConverter.read(this.responseType, null, responseWrapper);
                    }
                }
                if (this.responseClass != null && messageConverter.canRead(this.responseClass, contentType)) {
                    if (this.logger.isDebugEnabled()) {
                        String className = this.responseClass.getName();
                        this.logger.debug("Reading to [" + className + "] as \"" + contentType + "\"");
                    }
                    return (T) messageConverter.read((Class<T>) this.responseClass, responseWrapper);
                }
            }
            throw new RestClientException("Could not extract response: no suitable HttpMessageConverter found for response type [" + this.responseType + "] and content type [" + contentType + "]");
        } catch (IOException | HttpMessageNotReadableException ex) {
            throw new RestClientException("Error while extracting response for type [" + this.responseType + "] and content type [" + contentType + "]", ex);
        }
    }

    @Nullable
    protected MediaType getContentType(ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No content-type, using 'application/octet-stream'");
            }
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return contentType;
    }
}