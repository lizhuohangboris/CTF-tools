package org.springframework.core.type;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/AnnotatedTypeMetadata.class */
public interface AnnotatedTypeMetadata {
    boolean isAnnotated(String str);

    @Nullable
    Map<String, Object> getAnnotationAttributes(String str);

    @Nullable
    Map<String, Object> getAnnotationAttributes(String str, boolean z);

    @Nullable
    MultiValueMap<String, Object> getAllAnnotationAttributes(String str);

    @Nullable
    MultiValueMap<String, Object> getAllAnnotationAttributes(String str, boolean z);
}