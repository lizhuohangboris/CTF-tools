package org.springframework.validation.beanvalidation;

import java.util.Locale;
import java.util.ResourceBundle;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/validation/beanvalidation/MessageSourceResourceBundleLocator.class */
public class MessageSourceResourceBundleLocator implements ResourceBundleLocator {
    private final MessageSource messageSource;

    public MessageSourceResourceBundleLocator(MessageSource messageSource) {
        Assert.notNull(messageSource, "MessageSource must not be null");
        this.messageSource = messageSource;
    }

    @Override // org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
    public ResourceBundle getResourceBundle(Locale locale) {
        return new MessageSourceResourceBundle(this.messageSource, locale);
    }
}