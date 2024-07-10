package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/ExpressionContext.class */
public final class ExpressionContext extends AbstractExpressionContext {
    public ExpressionContext(IEngineConfiguration configuration) {
        super(configuration);
    }

    public ExpressionContext(IEngineConfiguration configuration, Locale locale) {
        super(configuration, locale);
    }

    public ExpressionContext(IEngineConfiguration configuration, Locale locale, Map<String, Object> variables) {
        super(configuration, locale, variables);
    }
}