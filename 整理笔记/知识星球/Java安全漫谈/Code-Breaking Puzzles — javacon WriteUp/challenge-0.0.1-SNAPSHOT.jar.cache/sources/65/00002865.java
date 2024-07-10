package org.thymeleaf.messageresolver;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/messageresolver/StandardMessageResolver.class */
public class StandardMessageResolver extends AbstractMessageResolver {
    private final ConcurrentHashMap<String, ConcurrentHashMap<Locale, Map<String, String>>> messagesByLocaleByTemplate = new ConcurrentHashMap<>(20, 0.9f, 2);
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Locale, Map<String, String>>> messagesByLocaleByOrigin = new ConcurrentHashMap<>(20, 0.9f, 2);
    private final Properties defaultMessages = new Properties();

    public final Properties getDefaultMessages() {
        return this.defaultMessages;
    }

    public final void setDefaultMessages(Properties defaultMessages) {
        if (defaultMessages != null) {
            this.defaultMessages.putAll(defaultMessages);
        }
    }

    public final void addDefaultMessage(String key, String value) {
        Validate.notNull(key, "Key for default message cannot be null");
        Validate.notNull(value, "Value for default message cannot be null");
        this.defaultMessages.put(key, value);
    }

    public final void clearDefaultMessages() {
        this.defaultMessages.clear();
    }

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public final String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        return resolveMessage(context, origin, key, messageParameters, true, true, true);
    }

    public final String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters, boolean performTemplateBasedResolution, boolean performOriginBasedResolution, boolean performDefaultBasedResolution) {
        String message;
        Map<String, String> messagesForLocaleForTemplate;
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        Locale locale = context.getLocale();
        if (performTemplateBasedResolution) {
            for (TemplateData templateData : context.getTemplateStack()) {
                String template = templateData.getTemplate();
                ITemplateResource templateResource = templateData.getTemplateResource();
                boolean templateCacheable = templateData.getValidity().isCacheable();
                if (templateCacheable) {
                    ConcurrentHashMap<Locale, Map<String, String>> messagesByLocaleForTemplate = this.messagesByLocaleByTemplate.get(template);
                    if (messagesByLocaleForTemplate == null) {
                        this.messagesByLocaleByTemplate.putIfAbsent(template, new ConcurrentHashMap<>(4));
                        messagesByLocaleForTemplate = this.messagesByLocaleByTemplate.get(template);
                    }
                    messagesForLocaleForTemplate = messagesByLocaleForTemplate.get(locale);
                    if (messagesForLocaleForTemplate == null) {
                        Map<String, String> messagesForLocaleForTemplate2 = resolveMessagesForTemplate(template, templateResource, locale);
                        if (messagesForLocaleForTemplate2 == null) {
                            messagesForLocaleForTemplate2 = Collections.emptyMap();
                        }
                        messagesByLocaleForTemplate.putIfAbsent(locale, messagesForLocaleForTemplate2);
                        messagesForLocaleForTemplate = messagesByLocaleForTemplate.get(locale);
                    }
                } else {
                    messagesForLocaleForTemplate = resolveMessagesForTemplate(template, templateResource, locale);
                    if (messagesForLocaleForTemplate == null) {
                        messagesForLocaleForTemplate = Collections.emptyMap();
                    }
                }
                String message2 = messagesForLocaleForTemplate.get(key);
                if (message2 != null) {
                    return formatMessage(locale, message2, messageParameters);
                }
            }
        }
        if (performOriginBasedResolution && origin != null) {
            ConcurrentHashMap<Locale, Map<String, String>> messagesByLocaleForOrigin = this.messagesByLocaleByOrigin.get(origin);
            if (messagesByLocaleForOrigin == null) {
                this.messagesByLocaleByOrigin.putIfAbsent(origin, new ConcurrentHashMap<>(4));
                messagesByLocaleForOrigin = this.messagesByLocaleByOrigin.get(origin);
            }
            Map<String, String> messagesForLocaleForOrigin = messagesByLocaleForOrigin.get(locale);
            if (messagesForLocaleForOrigin == null) {
                Map<String, String> messagesForLocaleForOrigin2 = resolveMessagesForOrigin(origin, locale);
                if (messagesForLocaleForOrigin2 == null) {
                    messagesForLocaleForOrigin2 = Collections.emptyMap();
                }
                messagesByLocaleForOrigin.putIfAbsent(locale, messagesForLocaleForOrigin2);
                messagesForLocaleForOrigin = messagesByLocaleForOrigin.get(locale);
            }
            String message3 = messagesForLocaleForOrigin.get(key);
            if (message3 != null) {
                return formatMessage(locale, message3, messageParameters);
            }
        }
        if (performDefaultBasedResolution && this.defaultMessages != null && (message = this.defaultMessages.getProperty(key)) != null) {
            return formatMessage(locale, message, messageParameters);
        }
        return null;
    }

    protected Map<String, String> resolveMessagesForTemplate(String template, ITemplateResource templateResource, Locale locale) {
        return StandardMessageResolutionUtils.resolveMessagesForTemplate(templateResource, locale);
    }

    protected Map<String, String> resolveMessagesForOrigin(Class<?> origin, Locale locale) {
        return StandardMessageResolutionUtils.resolveMessagesForOrigin(origin, locale);
    }

    protected String formatMessage(Locale locale, String message, Object[] messageParameters) {
        return StandardMessageResolutionUtils.formatMessage(locale, message, messageParameters);
    }

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
        Validate.notNull(key, "Message key cannot be null");
        if (context.getLocale() != null) {
            return "??" + key + "_" + context.getLocale().toString() + "??";
        }
        return "??" + key + "_??";
    }
}