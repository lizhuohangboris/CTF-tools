package org.hibernate.validator.internal.xml.config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/xml/config/ResourceLoaderHelper.class */
final class ResourceLoaderHelper {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private ResourceLoaderHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InputStream getResettableInputStreamForPath(String path, ClassLoader externalClassLoader) {
        ClassLoader loader;
        String inputPath = path;
        if (inputPath.startsWith("/")) {
            inputPath = inputPath.substring(1);
        }
        InputStream inputStream = null;
        if (externalClassLoader != null) {
            LOG.debug("Trying to load " + path + " via user class loader");
            inputStream = externalClassLoader.getResourceAsStream(inputPath);
        }
        if (inputStream == null && (loader = (ClassLoader) run(GetClassLoader.fromContext())) != null) {
            LOG.debug("Trying to load " + path + " via TCCL");
            inputStream = loader.getResourceAsStream(inputPath);
        }
        if (inputStream == null) {
            LOG.debug("Trying to load " + path + " via Hibernate Validator's class loader");
            inputStream = ResourceLoaderHelper.class.getClassLoader().getResourceAsStream(inputPath);
        }
        if (inputStream == null) {
            return null;
        }
        if (inputStream.markSupported()) {
            return inputStream;
        }
        return new BufferedInputStream(inputStream);
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}