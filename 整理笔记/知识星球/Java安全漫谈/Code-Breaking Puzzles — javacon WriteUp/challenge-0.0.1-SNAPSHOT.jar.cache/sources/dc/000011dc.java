package org.hibernate.validator.resourceloading;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/resourceloading/AggregateResourceBundleLocator.class */
public class AggregateResourceBundleLocator extends DelegatingResourceBundleLocator {
    private final List<String> bundleNames;
    private final ClassLoader classLoader;

    public AggregateResourceBundleLocator(List<String> bundleNames) {
        this(bundleNames, null);
    }

    public AggregateResourceBundleLocator(List<String> bundleNames, ResourceBundleLocator delegate) {
        this(bundleNames, delegate, null);
    }

    public AggregateResourceBundleLocator(List<String> bundleNames, ResourceBundleLocator delegate, ClassLoader classLoader) {
        super(delegate);
        Contracts.assertValueNotNull(bundleNames, "bundleNames");
        this.bundleNames = CollectionHelper.toImmutableList(bundleNames);
        this.classLoader = classLoader;
    }

    @Override // org.hibernate.validator.resourceloading.DelegatingResourceBundleLocator, org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
    public ResourceBundle getResourceBundle(Locale locale) {
        List<ResourceBundle> sourceBundles = new ArrayList<>();
        for (String bundleName : this.bundleNames) {
            ResourceBundleLocator resourceBundleLocator = new PlatformResourceBundleLocator(bundleName, this.classLoader);
            ResourceBundle resourceBundle = resourceBundleLocator.getResourceBundle(locale);
            if (resourceBundle != null) {
                sourceBundles.add(resourceBundle);
            }
        }
        ResourceBundle bundleFromDelegate = super.getResourceBundle(locale);
        if (bundleFromDelegate != null) {
            sourceBundles.add(bundleFromDelegate);
        }
        if (sourceBundles.isEmpty()) {
            return null;
        }
        return new AggregateBundle(sourceBundles);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/resourceloading/AggregateResourceBundleLocator$AggregateBundle.class */
    public static class AggregateBundle extends ResourceBundle {
        private final Map<String, Object> contents = new HashMap();

        public AggregateBundle(List<ResourceBundle> bundles) {
            if (bundles != null) {
                for (ResourceBundle bundle : bundles) {
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        String oneKey = keys.nextElement();
                        if (!this.contents.containsKey(oneKey)) {
                            this.contents.put(oneKey, bundle.getObject(oneKey));
                        }
                    }
                }
            }
        }

        @Override // java.util.ResourceBundle
        public Enumeration<String> getKeys() {
            return new IteratorEnumeration(this.contents.keySet().iterator());
        }

        @Override // java.util.ResourceBundle
        protected Object handleGetObject(String key) {
            return this.contents.get(key);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/resourceloading/AggregateResourceBundleLocator$IteratorEnumeration.class */
    private static class IteratorEnumeration<T> implements Enumeration<T> {
        private final Iterator<T> source;

        public IteratorEnumeration(Iterator<T> source) {
            if (source == null) {
                throw new IllegalArgumentException("Source must not be null");
            }
            this.source = source;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.source.hasNext();
        }

        @Override // java.util.Enumeration
        public T nextElement() {
            return this.source.next();
        }
    }
}