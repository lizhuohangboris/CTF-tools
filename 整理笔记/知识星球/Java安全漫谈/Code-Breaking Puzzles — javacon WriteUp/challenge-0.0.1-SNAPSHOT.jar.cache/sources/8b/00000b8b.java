package org.apache.logging.log4j.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/util/ProviderUtil.class */
public final class ProviderUtil {
    protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
    private static final String API_VERSION = "Log4jAPIVersion";
    private static volatile ProviderUtil instance;
    protected static final Collection<Provider> PROVIDERS = new HashSet();
    protected static final Lock STARTUP_LOCK = new ReentrantLock();
    private static final String[] COMPATIBLE_API_VERSIONS = {"2.6.0"};
    private static final Logger LOGGER = StatusLogger.getLogger();

    private ProviderUtil() {
        ClassLoader[] arr$ = LoaderUtil.getClassLoaders();
        for (ClassLoader classLoader : arr$) {
            try {
                loadProviders(classLoader);
            } catch (Throwable ex) {
                LOGGER.debug("Unable to retrieve provider from ClassLoader {}", classLoader, ex);
            }
        }
        for (LoaderUtil.UrlResource resource : LoaderUtil.findUrlResources(PROVIDER_RESOURCE)) {
            loadProvider(resource.getUrl(), resource.getClassLoader());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void addProvider(Provider provider) {
        PROVIDERS.add(provider);
        LOGGER.debug("Loaded Provider {}", provider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void loadProvider(URL url, ClassLoader cl) {
        try {
            Properties props = PropertiesUtil.loadClose(url.openStream(), url);
            if (validVersion(props.getProperty(API_VERSION))) {
                Provider provider = new Provider(props, url, cl);
                PROVIDERS.add(provider);
                LOGGER.debug("Loaded Provider {}", provider);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to open {}", url, e);
        }
    }

    protected static void loadProviders(ClassLoader classLoader) {
        ServiceLoader<Provider> serviceLoader = ServiceLoader.load(Provider.class, classLoader);
        Iterator i$ = serviceLoader.iterator();
        while (i$.hasNext()) {
            Provider provider = i$.next();
            if (validVersion(provider.getVersions()) && !PROVIDERS.contains(provider)) {
                PROVIDERS.add(provider);
            }
        }
    }

    @Deprecated
    protected static void loadProviders(Enumeration<URL> urls, ClassLoader cl) {
        if (urls != null) {
            while (urls.hasMoreElements()) {
                loadProvider(urls.nextElement(), cl);
            }
        }
    }

    public static Iterable<Provider> getProviders() {
        lazyInit();
        return PROVIDERS;
    }

    public static boolean hasProviders() {
        lazyInit();
        return !PROVIDERS.isEmpty();
    }

    protected static void lazyInit() {
        if (instance == null) {
            try {
                STARTUP_LOCK.lockInterruptibly();
                if (instance == null) {
                    instance = new ProviderUtil();
                }
                STARTUP_LOCK.unlock();
            } catch (InterruptedException e) {
                LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", (Throwable) e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static ClassLoader findClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
    }

    private static boolean validVersion(String version) {
        String[] arr$ = COMPATIBLE_API_VERSIONS;
        for (String v : arr$) {
            if (version.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
}