package org.hibernate.validator.resourceloading;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/resourceloading/CachingResourceBundleLocator.class */
public class CachingResourceBundleLocator extends DelegatingResourceBundleLocator {
    private final ConcurrentMap<Locale, ResourceBundle> bundleCache;

    public CachingResourceBundleLocator(ResourceBundleLocator delegate) {
        super(delegate);
        this.bundleCache = new ConcurrentHashMap();
    }

    @Override // org.hibernate.validator.resourceloading.DelegatingResourceBundleLocator, org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
    public ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle;
        ResourceBundle cachedResourceBundle = this.bundleCache.get(locale);
        if (cachedResourceBundle == null && (bundle = super.getResourceBundle(locale)) != null) {
            cachedResourceBundle = this.bundleCache.putIfAbsent(locale, bundle);
            if (cachedResourceBundle == null) {
                return bundle;
            }
        }
        return cachedResourceBundle;
    }
}