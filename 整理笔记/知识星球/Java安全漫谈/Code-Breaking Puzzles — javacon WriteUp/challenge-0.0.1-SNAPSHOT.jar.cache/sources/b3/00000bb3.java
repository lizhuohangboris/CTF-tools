package org.apache.naming;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/naming/StringManager.class */
public class StringManager {
    private final ResourceBundle bundle;
    private final Locale locale;
    private static final Hashtable<String, StringManager> managers = new Hashtable<>();

    private StringManager(String packageName) {
        String bundleName = packageName + ".LocalStrings";
        ResourceBundle tempBundle = null;
        try {
            tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
        } catch (MissingResourceException e) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                try {
                    tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), cl);
                } catch (MissingResourceException e2) {
                }
            }
        }
        if (tempBundle != null) {
            this.locale = tempBundle.getLocale();
        } else {
            this.locale = null;
        }
        this.bundle = tempBundle;
    }

    public String getString(String key) {
        String str;
        if (key == null) {
            throw new IllegalArgumentException("key may not have a null value");
        }
        try {
            str = this.bundle.getString(key);
        } catch (MissingResourceException e) {
            str = null;
        }
        return str;
    }

    public String getString(String key, Object... args) {
        String value = getString(key);
        if (value == null) {
            value = key;
        }
        MessageFormat mf = new MessageFormat(value);
        mf.setLocale(this.locale);
        return mf.format(args, new StringBuffer(), (FieldPosition) null).toString();
    }

    public static final synchronized StringManager getManager(String packageName) {
        StringManager mgr = managers.get(packageName);
        if (mgr == null) {
            mgr = new StringManager(packageName);
            managers.put(packageName, mgr);
        }
        return mgr;
    }

    public static final StringManager getManager(Class<?> clazz) {
        return getManager(clazz.getPackage().getName());
    }
}