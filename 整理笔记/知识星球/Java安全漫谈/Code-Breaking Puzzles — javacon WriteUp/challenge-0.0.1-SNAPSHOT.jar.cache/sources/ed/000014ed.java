package org.springframework.boot;

import java.io.PrintStream;
import org.springframework.core.env.Environment;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/Banner.class */
public interface Banner {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/Banner$Mode.class */
    public enum Mode {
        OFF,
        CONSOLE,
        LOG
    }

    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);
}