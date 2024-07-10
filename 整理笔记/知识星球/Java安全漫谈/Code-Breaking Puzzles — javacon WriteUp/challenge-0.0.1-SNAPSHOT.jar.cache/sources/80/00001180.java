package org.hibernate.validator.internal.util.logging;

import java.lang.invoke.MethodHandles;
import org.jboss.logging.Logger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/logging/LoggerFactory.class */
public final class LoggerFactory {
    public static Log make(MethodHandles.Lookup creationContext) {
        String className = creationContext.lookupClass().getName();
        return (Log) Logger.getMessageLogger(Log.class, className);
    }

    private LoggerFactory() {
    }
}