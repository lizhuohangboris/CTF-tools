package org.thymeleaf.spring5.linkbuilder.webflux;

import java.util.Map;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/linkbuilder/webflux/SpringWebFluxLinkBuilder.class */
public class SpringWebFluxLinkBuilder extends StandardLinkBuilder {
    @Override // org.thymeleaf.linkbuilder.StandardLinkBuilder
    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        if (!(context instanceof ISpringWebFluxContext)) {
            throw new TemplateProcessingException("Link base \"" + base + "\" cannot be context relative (/...) unless the context used for executing the engine implements the " + ISpringWebFluxContext.class.getName() + " interface");
        }
        ServerHttpRequest request = ((ISpringWebFluxContext) context).getRequest();
        return request.getPath().contextPath().value();
    }

    @Override // org.thymeleaf.linkbuilder.StandardLinkBuilder
    protected String processLink(IExpressionContext context, String link) {
        if (!(context instanceof ISpringWebFluxContext)) {
            return link;
        }
        ServerWebExchange exchange = ((ISpringWebFluxContext) context).getExchange();
        return exchange.transformUrl(link);
    }
}