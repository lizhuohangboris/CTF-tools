package org.thymeleaf.spring5.context.webflux;

import java.util.Locale;
import java.util.Map;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxExpressionContext.class */
public class SpringWebFluxExpressionContext extends SpringWebFluxContext implements IExpressionContext {
    private final IEngineConfiguration configuration;
    private IExpressionObjects expressionObjects;

    public SpringWebFluxExpressionContext(IEngineConfiguration configuration, ServerWebExchange exchange) {
        this(configuration, exchange, null, null, null);
    }

    public SpringWebFluxExpressionContext(IEngineConfiguration configuration, ServerWebExchange exchange, Locale locale) {
        this(configuration, exchange, null, locale, null);
    }

    public SpringWebFluxExpressionContext(IEngineConfiguration configuration, ServerWebExchange exchange, Locale locale, Map<String, Object> variables) {
        this(configuration, exchange, null, locale, variables);
    }

    public SpringWebFluxExpressionContext(IEngineConfiguration configuration, ServerWebExchange exchange, ReactiveAdapterRegistry reactiveAdapterRegistry, Locale locale, Map<String, Object> variables) {
        super(exchange, reactiveAdapterRegistry, locale, variables);
        this.expressionObjects = null;
        this.configuration = configuration;
    }

    @Override // org.thymeleaf.context.IExpressionContext
    public IEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.thymeleaf.context.IExpressionContext
    public IExpressionObjects getExpressionObjects() {
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }
}