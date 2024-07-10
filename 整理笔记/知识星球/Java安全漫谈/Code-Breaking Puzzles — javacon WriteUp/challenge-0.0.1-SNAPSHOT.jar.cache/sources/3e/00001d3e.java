package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/AbstractMessageSource.class */
public abstract class AbstractMessageSource extends MessageSourceSupport implements HierarchicalMessageSource {
    @Nullable
    private MessageSource parentMessageSource;
    @Nullable
    private Properties commonMessages;
    private boolean useCodeAsDefaultMessage = false;

    @Nullable
    protected abstract MessageFormat resolveCode(String str, Locale locale);

    @Override // org.springframework.context.HierarchicalMessageSource
    public void setParentMessageSource(@Nullable MessageSource parent) {
        this.parentMessageSource = parent;
    }

    @Override // org.springframework.context.HierarchicalMessageSource
    @Nullable
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    public void setCommonMessages(@Nullable Properties commonMessages) {
        this.commonMessages = commonMessages;
    }

    @Nullable
    protected Properties getCommonMessages() {
        return this.commonMessages;
    }

    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }

    protected boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    @Override // org.springframework.context.MessageSource
    public final String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        if (defaultMessage == null) {
            return getDefaultMessage(code);
        }
        return renderDefaultMessage(defaultMessage, args, locale);
    }

    @Override // org.springframework.context.MessageSource
    public final String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        String fallback = getDefaultMessage(code);
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchMessageException(code, locale);
    }

    @Override // org.springframework.context.MessageSource
    public final String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String[] codes = resolvable.getCodes();
        if (codes != null) {
            for (String code : codes) {
                String message = getMessageInternal(code, resolvable.getArguments(), locale);
                if (message != null) {
                    return message;
                }
            }
        }
        String defaultMessage = getDefaultMessage(resolvable, locale);
        if (defaultMessage != null) {
            return defaultMessage;
        }
        throw new NoSuchMessageException(!ObjectUtils.isEmpty((Object[]) codes) ? codes[codes.length - 1] : "", locale);
    }

    @Nullable
    protected String getMessageInternal(@Nullable String code, @Nullable Object[] args, @Nullable Locale locale) {
        String format;
        String commonMessage;
        if (code == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Object[] argsToUse = args;
        if (!isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
            String message = resolveCodeWithoutArguments(code, locale);
            if (message != null) {
                return message;
            }
        } else {
            argsToUse = resolveArguments(args, locale);
            MessageFormat messageFormat = resolveCode(code, locale);
            if (messageFormat != null) {
                synchronized (messageFormat) {
                    format = messageFormat.format(argsToUse);
                }
                return format;
            }
        }
        Properties commonMessages = getCommonMessages();
        if (commonMessages != null && (commonMessage = commonMessages.getProperty(code)) != null) {
            return formatMessage(commonMessage, args, locale);
        }
        return getMessageFromParent(code, argsToUse, locale);
    }

    @Nullable
    protected String getMessageFromParent(String code, @Nullable Object[] args, Locale locale) {
        MessageSource parent = getParentMessageSource();
        if (parent != null) {
            if (parent instanceof AbstractMessageSource) {
                return ((AbstractMessageSource) parent).getMessageInternal(code, args, locale);
            }
            return parent.getMessage(code, args, null, locale);
        }
        return null;
    }

    @Nullable
    protected String getDefaultMessage(MessageSourceResolvable resolvable, Locale locale) {
        String defaultMessage = resolvable.getDefaultMessage();
        String[] codes = resolvable.getCodes();
        if (defaultMessage != null) {
            if (!ObjectUtils.isEmpty((Object[]) codes) && defaultMessage.equals(codes[0])) {
                return defaultMessage;
            }
            return renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
        } else if (ObjectUtils.isEmpty((Object[]) codes)) {
            return null;
        } else {
            return getDefaultMessage(codes[0]);
        }
    }

    @Nullable
    protected String getDefaultMessage(String code) {
        if (isUseCodeAsDefaultMessage()) {
            return code;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.context.support.MessageSourceSupport
    public Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        if (ObjectUtils.isEmpty(args)) {
            return super.resolveArguments(args, locale);
        }
        List<Object> resolvedArgs = new ArrayList<>(args.length);
        for (Object arg : args) {
            if (arg instanceof MessageSourceResolvable) {
                resolvedArgs.add(getMessage((MessageSourceResolvable) arg, locale));
            } else {
                resolvedArgs.add(arg);
            }
        }
        return resolvedArgs.toArray();
    }

    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        String format;
        MessageFormat messageFormat = resolveCode(code, locale);
        if (messageFormat != null) {
            synchronized (messageFormat) {
                format = messageFormat.format(new Object[0]);
            }
            return format;
        }
        return null;
    }
}