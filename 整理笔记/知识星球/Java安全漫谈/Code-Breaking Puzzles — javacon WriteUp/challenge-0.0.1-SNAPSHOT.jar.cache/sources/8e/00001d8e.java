package org.springframework.core;

import java.lang.annotation.Annotation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/KotlinDetector.class */
public abstract class KotlinDetector {
    private static final Log logger = LogFactory.getLog(KotlinDetector.class);
    @Nullable
    private static final Class<? extends Annotation> kotlinMetadata;
    private static final boolean kotlinReflectPresent;

    /* JADX WARN: Multi-variable type inference failed */
    static {
        Class<?> metadata;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            metadata = ClassUtils.forName("kotlin.Metadata", classLoader);
        } catch (ClassNotFoundException e) {
            metadata = null;
        }
        kotlinMetadata = metadata;
        kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
        if (kotlinMetadata != null && !kotlinReflectPresent) {
            logger.info("Kotlin reflection implementation not found at runtime, related features won't be available.");
        }
    }

    public static boolean isKotlinPresent() {
        return kotlinMetadata != null;
    }

    public static boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    public static boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata == null || clazz.getDeclaredAnnotation(kotlinMetadata) == null) ? false : true;
    }
}