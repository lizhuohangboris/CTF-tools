package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/AbstractEngineContext.class */
public abstract class AbstractEngineContext implements IEngineContext {
    private final IEngineConfiguration configuration;
    private final Map<String, Object> templateResolutionAttributes;
    private final Locale locale;
    private IExpressionObjects expressionObjects = null;
    private IdentifierSequences identifierSequences;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEngineContext(IEngineConfiguration configuration, Map<String, Object> templateResolutionAttributes, Locale locale) {
        this.identifierSequences = null;
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        this.configuration = configuration;
        this.locale = locale;
        this.templateResolutionAttributes = templateResolutionAttributes;
        this.identifierSequences = null;
    }

    @Override // org.thymeleaf.context.IExpressionContext
    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }

    @Override // org.thymeleaf.context.IContext
    public final Locale getLocale() {
        return this.locale;
    }

    @Override // org.thymeleaf.context.IExpressionContext
    public final IExpressionObjects getExpressionObjects() {
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final TemplateMode getTemplateMode() {
        return getTemplateData().getTemplateMode();
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final IModelFactory getModelFactory() {
        return this.configuration.getModelFactory(getTemplateMode());
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final String getMessage(Class<?> origin, String key, Object[] messageParameters, boolean useAbsentMessageRepresentation) {
        Validate.notNull(key, "Message key cannot be null");
        Set<IMessageResolver> messageResolvers = this.configuration.getMessageResolvers();
        for (IMessageResolver messageResolver : messageResolvers) {
            String resolvedMessage = messageResolver.resolveMessage(this, origin, key, messageParameters);
            if (resolvedMessage != null) {
                return resolvedMessage;
            }
        }
        if (useAbsentMessageRepresentation) {
            for (IMessageResolver messageResolver2 : messageResolvers) {
                String absentMessageRepresentation = messageResolver2.createAbsentMessageRepresentation(this, origin, key, messageParameters);
                if (absentMessageRepresentation != null) {
                    return absentMessageRepresentation;
                }
            }
            return null;
        }
        return null;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final String buildLink(String base, Map<String, Object> parameters) {
        Set<ILinkBuilder> linkBuilders = this.configuration.getLinkBuilders();
        for (ILinkBuilder linkBuilder : linkBuilders) {
            String link = linkBuilder.buildLink(this, base, parameters);
            if (link != null) {
                return link;
            }
        }
        throw new TemplateProcessingException("No configured link builder instance was able to build link with base \"" + base + "\" and parameters " + parameters);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public final IdentifierSequences getIdentifierSequences() {
        if (this.identifierSequences == null) {
            this.identifierSequences = new IdentifierSequences();
        }
        return this.identifierSequences;
    }
}