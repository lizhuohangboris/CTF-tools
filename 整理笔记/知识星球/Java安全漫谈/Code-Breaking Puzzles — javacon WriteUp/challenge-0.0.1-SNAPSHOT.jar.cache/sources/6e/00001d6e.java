package org.springframework.context.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ResourceBundleMessageSource.class */
public class ResourceBundleMessageSource extends AbstractResourceBasedMessageSource implements BeanClassLoaderAware {
    @Nullable
    private ClassLoader bundleClassLoader;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final Map<String, Map<Locale, ResourceBundle>> cachedResourceBundles = new ConcurrentHashMap();
    private final Map<ResourceBundle, Map<String, Map<Locale, MessageFormat>>> cachedBundleMessageFormats = new ConcurrentHashMap();
    @Nullable
    private volatile MessageSourceControl control = new MessageSourceControl();

    public ResourceBundleMessageSource() {
        setDefaultEncoding("ISO-8859-1");
    }

    public void setBundleClassLoader(ClassLoader classLoader) {
        this.bundleClassLoader = classLoader;
    }

    @Nullable
    protected ClassLoader getBundleClassLoader() {
        return this.bundleClassLoader != null ? this.bundleClassLoader : this.beanClassLoader;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        String result;
        Set<String> basenames = getBasenameSet();
        for (String basename : basenames) {
            ResourceBundle bundle = getResourceBundle(basename, locale);
            if (bundle != null && (result = getStringOrNull(bundle, code)) != null) {
                return result;
            }
        }
        return null;
    }

    @Override // org.springframework.context.support.AbstractMessageSource
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageFormat messageFormat;
        Set<String> basenames = getBasenameSet();
        for (String basename : basenames) {
            ResourceBundle bundle = getResourceBundle(basename, locale);
            if (bundle != null && (messageFormat = getMessageFormat(bundle, code, locale)) != null) {
                return messageFormat;
            }
        }
        return null;
    }

    @Nullable
    protected ResourceBundle getResourceBundle(String basename, Locale locale) {
        ResourceBundle bundle;
        if (getCacheMillis() >= 0) {
            return doGetBundle(basename, locale);
        }
        Map<Locale, ResourceBundle> localeMap = this.cachedResourceBundles.get(basename);
        if (localeMap != null && (bundle = localeMap.get(locale)) != null) {
            return bundle;
        }
        try {
            ResourceBundle bundle2 = doGetBundle(basename, locale);
            if (localeMap == null) {
                localeMap = new ConcurrentHashMap<>();
                Map<Locale, ResourceBundle> existing = this.cachedResourceBundles.putIfAbsent(basename, localeMap);
                if (existing != null) {
                    localeMap = existing;
                }
            }
            localeMap.put(locale, bundle2);
            return bundle2;
        } catch (MissingResourceException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("ResourceBundle [" + basename + "] not found for MessageSource: " + ex.getMessage());
                return null;
            }
            return null;
        }
    }

    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        ClassLoader classLoader = getBundleClassLoader();
        Assert.state(classLoader != null, "No bundle ClassLoader set");
        MessageSourceControl control = this.control;
        if (control != null) {
            try {
                return ResourceBundle.getBundle(basename, locale, classLoader, control);
            } catch (UnsupportedOperationException ex) {
                this.control = null;
                String encoding = getDefaultEncoding();
                if (encoding != null && this.logger.isInfoEnabled()) {
                    this.logger.info("ResourceBundleMessageSource is configured to read resources with encoding '" + encoding + "' but ResourceBundle.Control not supported in current system environment: " + ex.getMessage() + " - falling back to plain ResourceBundle.getBundle retrieval with the platform default encoding. Consider setting the 'defaultEncoding' property to 'null' for participating in the platform default and therefore avoiding this log message.");
                }
            }
        }
        return ResourceBundle.getBundle(basename, locale, classLoader);
    }

    protected ResourceBundle loadBundle(Reader reader) throws IOException {
        return new PropertyResourceBundle(reader);
    }

    protected ResourceBundle loadBundle(InputStream inputStream) throws IOException {
        return new PropertyResourceBundle(inputStream);
    }

    @Nullable
    protected MessageFormat getMessageFormat(ResourceBundle bundle, String code, Locale locale) throws MissingResourceException {
        MessageFormat result;
        Map<String, Map<Locale, MessageFormat>> codeMap = this.cachedBundleMessageFormats.get(bundle);
        Map<Locale, MessageFormat> localeMap = null;
        if (codeMap != null) {
            localeMap = codeMap.get(code);
            if (localeMap != null && (result = localeMap.get(locale)) != null) {
                return result;
            }
        }
        String msg = getStringOrNull(bundle, code);
        if (msg != null) {
            if (codeMap == null) {
                codeMap = new ConcurrentHashMap<>();
                Map<String, Map<Locale, MessageFormat>> existing = this.cachedBundleMessageFormats.putIfAbsent(bundle, codeMap);
                if (existing != null) {
                    codeMap = existing;
                }
            }
            if (localeMap == null) {
                localeMap = new ConcurrentHashMap<>();
                Map<Locale, MessageFormat> existing2 = codeMap.putIfAbsent(code, localeMap);
                if (existing2 != null) {
                    localeMap = existing2;
                }
            }
            MessageFormat result2 = createMessageFormat(msg, locale);
            localeMap.put(locale, result2);
            return result2;
        }
        return null;
    }

    @Nullable
    protected String getStringOrNull(ResourceBundle bundle, String key) {
        if (bundle.containsKey(key)) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                return null;
            }
        }
        return null;
    }

    public String toString() {
        return getClass().getName() + ": basenames=" + getBasenameSet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ResourceBundleMessageSource$MessageSourceControl.class */
    public class MessageSourceControl extends ResourceBundle.Control {
        private MessageSourceControl() {
        }

        @Override // java.util.ResourceBundle.Control
        @Nullable
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (format.equals("java.properties")) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");
                try {
                    InputStream inputStream = (InputStream) AccessController.doPrivileged(() -> {
                        URLConnection connection;
                        InputStream is = null;
                        if (reload) {
                            URL url = loader.getResource(resourceName);
                            if (url != null && (connection = url.openConnection()) != null) {
                                connection.setUseCaches(false);
                                is = connection.getInputStream();
                            }
                        } else {
                            is = loader.getResourceAsStream(resourceName);
                        }
                        return is;
                    });
                    if (inputStream != null) {
                        String encoding = ResourceBundleMessageSource.this.getDefaultEncoding();
                        if (encoding != null) {
                            InputStreamReader bundleReader = new InputStreamReader(inputStream, encoding);
                            Throwable th = null;
                            try {
                                ResourceBundle loadBundle = ResourceBundleMessageSource.this.loadBundle(bundleReader);
                                if (bundleReader != null) {
                                    if (0 != 0) {
                                        try {
                                            bundleReader.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    } else {
                                        bundleReader.close();
                                    }
                                }
                                return loadBundle;
                            } finally {
                            }
                        } else {
                            Throwable th3 = null;
                            try {
                                ResourceBundle loadBundle2 = ResourceBundleMessageSource.this.loadBundle(inputStream);
                                if (inputStream != null) {
                                    if (0 != 0) {
                                        try {
                                            inputStream.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        inputStream.close();
                                    }
                                }
                                return loadBundle2;
                            } finally {
                            }
                        }
                    } else {
                        return null;
                    }
                } catch (PrivilegedActionException ex) {
                    throw ((IOException) ex.getException());
                }
            } else {
                return super.newBundle(baseName, locale, format, loader, reload);
            }
        }

        @Override // java.util.ResourceBundle.Control
        @Nullable
        public Locale getFallbackLocale(String baseName, Locale locale) {
            if (ResourceBundleMessageSource.this.isFallbackToSystemLocale()) {
                return super.getFallbackLocale(baseName, locale);
            }
            return null;
        }

        @Override // java.util.ResourceBundle.Control
        public long getTimeToLive(String baseName, Locale locale) {
            long cacheMillis = ResourceBundleMessageSource.this.getCacheMillis();
            return cacheMillis >= 0 ? cacheMillis : super.getTimeToLive(baseName, locale);
        }

        @Override // java.util.ResourceBundle.Control
        public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
            if (super.needsReload(baseName, locale, format, loader, bundle, loadTime)) {
                ResourceBundleMessageSource.this.cachedBundleMessageFormats.remove(bundle);
                return true;
            }
            return false;
        }
    }
}