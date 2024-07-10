package org.thymeleaf.spring5.messageresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/messageresolver/SpringMessageResolver.class */
public class SpringMessageResolver extends AbstractMessageResolver implements MessageSourceAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringMessageResolver.class);
    private final StandardMessageResolver standardMessageResolver = new StandardMessageResolver();
    private MessageSource messageSource;

    private void checkMessageSourceInitialized() {
        if (this.messageSource == null) {
            throw new ConfigurationException("Cannot initialize " + SpringMessageResolver.class.getSimpleName() + ": MessageSource has not been set. Either define this object as a Spring bean (which will automatically set the MessageSource) or, if you instance it directly, set the MessageSource manually using its corresponding setter method.");
        }
    }

    public final MessageSource getMessageSource() {
        return this.messageSource;
    }

    @Override // org.springframework.context.MessageSourceAware
    public final void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public final String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        String message;
        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        if (context != null) {
            checkMessageSourceInitialized();
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Resolving message with key \"{}\" for template \"{}\" and locale \"{}\". Messages will be retrieved from Spring's MessageSource infrastructure.", TemplateEngine.threadIndex(), key, context.getTemplateData().getTemplate(), context.getLocale());
            }
            try {
                return this.messageSource.getMessage(key, messageParameters, context.getLocale());
            } catch (NoSuchMessageException e) {
            }
        }
        if (origin != null && (message = this.standardMessageResolver.resolveMessage(context, origin, key, messageParameters, false, true, true)) != null) {
            return message;
        }
        return null;
    }

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return this.standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }
}