package org.springframework.web.bind.support;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/WebExchangeDataBinder.class */
public class WebExchangeDataBinder extends WebDataBinder {
    public WebExchangeDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebExchangeDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public Mono<Void> bind(ServerWebExchange exchange) {
        return getValuesToBind(exchange).doOnNext(values -> {
            doBind(new MutablePropertyValues(values));
        }).then();
    }

    protected Mono<Map<String, Object>> getValuesToBind(ServerWebExchange exchange) {
        return extractValuesToBind(exchange);
    }

    public static Mono<Map<String, Object>> extractValuesToBind(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Mono<MultiValueMap<String, String>> formData = exchange.getFormData();
        Mono<MultiValueMap<String, Part>> multipartData = exchange.getMultipartData();
        return Mono.zip(Mono.just(queryParams), formData, multipartData).map(tuple -> {
            Map<String, Object> result = new TreeMap<>();
            ((MultiValueMap) tuple.getT1()).forEach(key, values -> {
                addBindValue(result, key, values);
            });
            ((MultiValueMap) tuple.getT2()).forEach(key2, values2 -> {
                addBindValue(result, key2, values2);
            });
            ((MultiValueMap) tuple.getT3()).forEach(key3, values3 -> {
                addBindValue(result, key3, values3);
            });
            return result;
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addBindValue(Map<String, Object> params, String key, List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            List<?> values2 = (List) values.stream().map(value -> {
                return value instanceof FormFieldPart ? ((FormFieldPart) value).value() : value;
            }).collect(Collectors.toList());
            params.put(key, values2.size() == 1 ? values2.get(0) : values2);
        }
    }
}