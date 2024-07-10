package org.springframework.validation.beanvalidation;

import java.util.Locale;
import javax.validation.MessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/LocaleContextMessageInterpolator.class */
public class LocaleContextMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator targetInterpolator;

    public LocaleContextMessageInterpolator(MessageInterpolator targetInterpolator) {
        Assert.notNull(targetInterpolator, "Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }

    @Override // javax.validation.MessageInterpolator
    public String interpolate(String message, MessageInterpolator.Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }

    @Override // javax.validation.MessageInterpolator
    public String interpolate(String message, MessageInterpolator.Context context, Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }
}