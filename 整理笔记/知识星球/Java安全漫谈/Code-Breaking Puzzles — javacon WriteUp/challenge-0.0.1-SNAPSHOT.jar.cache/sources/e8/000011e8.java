package org.hibernate.validator.spi.resourceloading;

import java.util.Locale;
import java.util.ResourceBundle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/resourceloading/ResourceBundleLocator.class */
public interface ResourceBundleLocator {
    ResourceBundle getResourceBundle(Locale locale);
}