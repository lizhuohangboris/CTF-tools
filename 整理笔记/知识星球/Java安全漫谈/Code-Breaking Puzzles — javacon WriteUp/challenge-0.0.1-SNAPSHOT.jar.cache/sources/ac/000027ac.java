package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/AbstractExpressionContext.class */
public abstract class AbstractExpressionContext extends AbstractContext implements IExpressionContext {
    private final IEngineConfiguration configuration;
    private IExpressionObjects expressionObjects;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractExpressionContext(IEngineConfiguration configuration) {
        this.expressionObjects = null;
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractExpressionContext(IEngineConfiguration configuration, Locale locale) {
        super(locale);
        this.expressionObjects = null;
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractExpressionContext(IEngineConfiguration configuration, Locale locale, Map<String, Object> variables) {
        super(locale, variables);
        this.expressionObjects = null;
        Validate.notNull(configuration, "Configuration cannot be null");
        this.configuration = configuration;
    }

    @Override // org.thymeleaf.context.IExpressionContext
    public final IEngineConfiguration getConfiguration() {
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