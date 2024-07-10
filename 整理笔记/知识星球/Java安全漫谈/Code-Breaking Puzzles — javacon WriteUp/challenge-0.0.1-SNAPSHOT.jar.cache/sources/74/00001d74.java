package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/StaticMessageSource.class */
public class StaticMessageSource extends AbstractMessageSource {
    private final Map<String, String> messages = new HashMap();
    private final Map<String, MessageFormat> cachedMessageFormats = new HashMap();

    @Override // org.springframework.context.support.AbstractMessageSource
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return this.messages.get(code + '_' + locale.toString());
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageFormat messageFormat;
        String key = code + '_' + locale.toString();
        String msg = this.messages.get(key);
        if (msg == null) {
            return null;
        }
        synchronized (this.cachedMessageFormats) {
            MessageFormat messageFormat2 = this.cachedMessageFormats.get(key);
            if (messageFormat2 == null) {
                messageFormat2 = createMessageFormat(msg, locale);
                this.cachedMessageFormats.put(key, messageFormat2);
            }
            messageFormat = messageFormat2;
        }
        return messageFormat;
    }

    public void addMessage(String code, Locale locale, String msg) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(locale, "Locale must not be null");
        Assert.notNull(msg, "Message must not be null");
        this.messages.put(code + '_' + locale.toString(), msg);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        messages.forEach(code, msg -> {
            addMessage(code, locale, msg);
        });
    }

    public String toString() {
        return getClass().getName() + ": " + this.messages;
    }
}