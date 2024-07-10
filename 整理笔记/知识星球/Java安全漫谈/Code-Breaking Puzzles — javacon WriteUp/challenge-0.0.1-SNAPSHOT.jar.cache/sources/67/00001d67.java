package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/MessageSourceSupport.class */
public abstract class MessageSourceSupport {
    private static final MessageFormat INVALID_MESSAGE_FORMAT = new MessageFormat("");
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean alwaysUseMessageFormat = false;
    private final Map<String, Map<Locale, MessageFormat>> messageFormatsPerMessage = new HashMap();

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String renderDefaultMessage(String defaultMessage, @Nullable Object[] args, Locale locale) {
        return formatMessage(defaultMessage, args, locale);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String formatMessage(String msg, @Nullable Object[] args, Locale locale) {
        String format;
        if (!isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
            return msg;
        }
        MessageFormat messageFormat = null;
        synchronized (this.messageFormatsPerMessage) {
            Map<Locale, MessageFormat> messageFormatsPerLocale = this.messageFormatsPerMessage.get(msg);
            if (messageFormatsPerLocale != null) {
                messageFormat = messageFormatsPerLocale.get(locale);
            } else {
                messageFormatsPerLocale = new HashMap<>();
                this.messageFormatsPerMessage.put(msg, messageFormatsPerLocale);
            }
            if (messageFormat == null) {
                try {
                    messageFormat = createMessageFormat(msg, locale);
                } catch (IllegalArgumentException ex) {
                    if (isAlwaysUseMessageFormat()) {
                        throw ex;
                    }
                    messageFormat = INVALID_MESSAGE_FORMAT;
                }
                messageFormatsPerLocale.put(locale, messageFormat);
            }
        }
        if (messageFormat == INVALID_MESSAGE_FORMAT) {
            return msg;
        }
        synchronized (messageFormat) {
            format = messageFormat.format(resolveArguments(args, locale));
        }
        return format;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MessageFormat createMessageFormat(String msg, Locale locale) {
        return new MessageFormat(msg, locale);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        return args != null ? args : new Object[0];
    }
}