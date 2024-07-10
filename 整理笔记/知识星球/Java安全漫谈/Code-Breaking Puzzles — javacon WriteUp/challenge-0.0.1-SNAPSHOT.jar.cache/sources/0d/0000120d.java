package org.jboss.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Deprecated
@Retention(RetentionPolicy.CLASS)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Message.class */
public @interface Message {
    public static final int NONE = 0;
    public static final int INHERIT = -1;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jboss-logging-3.3.2.Final.jar:org/jboss/logging/Message$Format.class */
    public enum Format {
        PRINTF,
        MESSAGE_FORMAT,
        NO_FORMAT
    }

    int id() default -1;

    String value();

    Format format() default Format.PRINTF;
}