package org.jboss.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Messages.class */
public final class Messages {
    private Messages() {
    }

    public static <T> T getBundle(Class<T> type) {
        return (T) getBundle(type, LoggingLocale.getLocale());
    }

    public static <T> T getBundle(final Class<T> type, final Locale locale) {
        return (T) AccessController.doPrivileged(new PrivilegedAction<T>() { // from class: org.jboss.logging.Messages.1
            @Override // java.security.PrivilegedAction
            public T run() {
                String language = locale.getLanguage();
                String country = locale.getCountry();
                String variant = locale.getVariant();
                Class cls = null;
                if (variant != null && variant.length() > 0) {
                    try {
                        cls = Class.forName(Messages.join(type.getName(), "$bundle", language, country, variant), true, type.getClassLoader()).asSubclass(type);
                    } catch (ClassNotFoundException e) {
                    }
                }
                if (cls == null && country != null && country.length() > 0) {
                    try {
                        cls = Class.forName(Messages.join(type.getName(), "$bundle", language, country, null), true, type.getClassLoader()).asSubclass(type);
                    } catch (ClassNotFoundException e2) {
                    }
                }
                if (cls == null && language != null && language.length() > 0) {
                    try {
                        cls = Class.forName(Messages.join(type.getName(), "$bundle", language, null, null), true, type.getClassLoader()).asSubclass(type);
                    } catch (ClassNotFoundException e3) {
                    }
                }
                if (cls == null) {
                    try {
                        cls = Class.forName(Messages.join(type.getName(), "$bundle", null, null, null), true, type.getClassLoader()).asSubclass(type);
                    } catch (ClassNotFoundException e4) {
                        throw new IllegalArgumentException("Invalid bundle " + type + " (implementation not found)");
                    }
                }
                try {
                    java.lang.reflect.Field field = cls.getField("INSTANCE");
                    try {
                        return (T) type.cast(field.get(null));
                    } catch (IllegalAccessException e5) {
                        throw new IllegalArgumentException("Bundle implementation " + cls + " could not be instantiated", e5);
                    }
                } catch (NoSuchFieldException e6) {
                    throw new IllegalArgumentException("Bundle implementation " + cls + " has no instance field");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String join(String interfaceName, String a, String b, String c, String d) {
        StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }
}