package org.thymeleaf.spring5.util;

import org.springframework.core.SpringVersion;
import org.thymeleaf.util.ClassLoaderUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/SpringVersionUtils.class */
public final class SpringVersionUtils {
    private static final int SPRING_VERSION_MAJOR;
    private static final int SPRING_VERSION_MINOR;
    private static final boolean SPRING_WEB_MVC_PRESENT;
    private static final boolean SPRING_WEB_REACTIVE_PRESENT;

    static {
        String springVersion = SpringVersion.getVersion();
        String corePackageName = SpringVersion.class.getPackage().getName();
        String springPackageName = corePackageName.substring(0, corePackageName.length() - 5);
        if (springVersion != null) {
            try {
                int separatorIdx = springVersion.indexOf(46);
                SPRING_VERSION_MAJOR = Integer.parseInt(springVersion.substring(0, separatorIdx));
                int separator2Idx = springVersion.indexOf(46, separatorIdx + 1);
                SPRING_VERSION_MINOR = Integer.parseInt(springVersion.substring(separatorIdx + 1, separator2Idx));
            } catch (Exception e) {
                throw new ExceptionInInitializerError("Exception during initialization of Spring versioning utilities. Identified Spring version is '" + springVersion + "', which does not follow the {major}.{minor}.{...} scheme");
            }
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.io.buffer.DataBuffer")) {
            SPRING_VERSION_MAJOR = 5;
            SPRING_VERSION_MINOR = 0;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".context.annotation.ComponentScans")) {
            SPRING_VERSION_MAJOR = 4;
            SPRING_VERSION_MINOR = 3;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.annotation.AliasFor")) {
            SPRING_VERSION_MAJOR = 4;
            SPRING_VERSION_MINOR = 2;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".cache.annotation.CacheConfig")) {
            SPRING_VERSION_MAJOR = 4;
            SPRING_VERSION_MINOR = 1;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".core.io.PathResource")) {
            SPRING_VERSION_MAJOR = 4;
            SPRING_VERSION_MINOR = 0;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.context.request.async.DeferredResult")) {
            SPRING_VERSION_MAJOR = 3;
            SPRING_VERSION_MINOR = 2;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.servlet.support.RequestDataValueProcessor")) {
            SPRING_VERSION_MAJOR = 3;
            SPRING_VERSION_MINOR = 1;
        } else if (ClassLoaderUtils.isClassPresent(springPackageName + ".web.bind.annotation.RequestBody")) {
            SPRING_VERSION_MAJOR = 3;
            SPRING_VERSION_MINOR = 0;
        } else {
            SPRING_VERSION_MAJOR = 2;
            SPRING_VERSION_MINOR = 5;
        }
        SPRING_WEB_MVC_PRESENT = ClassLoaderUtils.isClassPresent(springPackageName + ".web.servlet.View");
        SPRING_WEB_REACTIVE_PRESENT = SPRING_VERSION_MAJOR >= 5 && ClassLoaderUtils.isClassPresent(new StringBuilder().append(springPackageName).append(".web.reactive.result.view.View").toString());
    }

    public static int getSpringVersionMajor() {
        return SPRING_VERSION_MAJOR;
    }

    public static int getSpringVersionMinor() {
        return SPRING_VERSION_MINOR;
    }

    public static boolean isSpring30AtLeast() {
        return SPRING_VERSION_MAJOR >= 3;
    }

    public static boolean isSpring31AtLeast() {
        return SPRING_VERSION_MAJOR > 3 || (SPRING_VERSION_MAJOR == 3 && SPRING_VERSION_MINOR >= 1);
    }

    public static boolean isSpring32AtLeast() {
        return SPRING_VERSION_MAJOR > 3 || (SPRING_VERSION_MAJOR == 3 && SPRING_VERSION_MINOR >= 2);
    }

    public static boolean isSpring40AtLeast() {
        return SPRING_VERSION_MAJOR >= 4;
    }

    public static boolean isSpring41AtLeast() {
        return SPRING_VERSION_MAJOR > 4 || (SPRING_VERSION_MAJOR == 4 && SPRING_VERSION_MINOR >= 1);
    }

    public static boolean isSpring42AtLeast() {
        return SPRING_VERSION_MAJOR > 4 || (SPRING_VERSION_MAJOR == 4 && SPRING_VERSION_MINOR >= 2);
    }

    public static boolean isSpring43AtLeast() {
        return SPRING_VERSION_MAJOR > 4 || (SPRING_VERSION_MAJOR == 4 && SPRING_VERSION_MINOR >= 3);
    }

    public static boolean isSpring50AtLeast() {
        return SPRING_VERSION_MAJOR >= 5;
    }

    public static boolean isSpringWebMvcPresent() {
        return SPRING_WEB_MVC_PRESENT;
    }

    public static boolean isSpringWebFluxPresent() {
        return SPRING_WEB_REACTIVE_PRESENT;
    }

    private SpringVersionUtils() {
    }
}