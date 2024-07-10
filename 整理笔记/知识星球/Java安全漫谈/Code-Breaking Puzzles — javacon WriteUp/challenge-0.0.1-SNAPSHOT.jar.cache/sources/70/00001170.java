package org.hibernate.validator.internal.util;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/Version.class */
public final class Version {
    static {
        LoggerFactory.make(MethodHandles.lookup()).version(getVersionString());
    }

    public static String getVersionString() {
        return Version.class.getPackage().getImplementationVersion();
    }

    public static void touch() {
    }

    private Version() {
    }
}