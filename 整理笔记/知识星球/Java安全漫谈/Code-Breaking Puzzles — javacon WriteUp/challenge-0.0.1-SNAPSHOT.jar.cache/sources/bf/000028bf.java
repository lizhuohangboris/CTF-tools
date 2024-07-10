package org.thymeleaf.spring5.context.webflux;

import java.util.Map;
import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxThymeleafRequestDataValueProcessor.class */
class SpringWebFluxThymeleafRequestDataValueProcessor implements IThymeleafRequestDataValueProcessor {
    private final RequestDataValueProcessor requestDataValueProcessor;
    private final ServerWebExchange exchange;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringWebFluxThymeleafRequestDataValueProcessor(RequestDataValueProcessor requestDataValueProcessor, ServerWebExchange exchange) {
        this.requestDataValueProcessor = requestDataValueProcessor;
        this.exchange = exchange;
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processAction(String action, String httpMethod) {
        if (this.requestDataValueProcessor == null) {
            return action;
        }
        return this.requestDataValueProcessor.processAction(this.exchange, action, httpMethod);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processFormFieldValue(String name, String value, String type) {
        if (this.requestDataValueProcessor == null) {
            return value;
        }
        return this.requestDataValueProcessor.processFormFieldValue(this.exchange, name, value, type);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public Map<String, String> getExtraHiddenFields() {
        if (this.requestDataValueProcessor == null) {
            return null;
        }
        return this.requestDataValueProcessor.getExtraHiddenFields(this.exchange);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor
    public String processUrl(String url) {
        if (this.requestDataValueProcessor == null) {
            return url;
        }
        return this.requestDataValueProcessor.processUrl(this.exchange, url);
    }
}