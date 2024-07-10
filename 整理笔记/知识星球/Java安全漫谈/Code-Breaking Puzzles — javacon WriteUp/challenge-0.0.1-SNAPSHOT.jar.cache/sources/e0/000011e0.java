package org.hibernate.validator.resourceloading;

import java.util.Locale;
import java.util.ResourceBundle;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/resourceloading/DelegatingResourceBundleLocator.class */
public abstract class DelegatingResourceBundleLocator implements ResourceBundleLocator {
    private final ResourceBundleLocator delegate;

    public DelegatingResourceBundleLocator(ResourceBundleLocator delegate) {
        this.delegate = delegate;
    }

    @Override // org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
    public ResourceBundle getResourceBundle(Locale locale) {
        if (this.delegate == null) {
            return null;
        }
        return this.delegate.getResourceBundle(locale);
    }
}