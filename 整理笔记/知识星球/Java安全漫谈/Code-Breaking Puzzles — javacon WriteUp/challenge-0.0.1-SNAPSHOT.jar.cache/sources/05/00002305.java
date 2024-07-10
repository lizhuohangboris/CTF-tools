package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ui/Model.class */
public interface Model {
    Model addAttribute(String str, @Nullable Object obj);

    Model addAttribute(Object obj);

    Model addAllAttributes(Collection<?> collection);

    Model addAllAttributes(Map<String, ?> map);

    Model mergeAttributes(Map<String, ?> map);

    boolean containsAttribute(String str);

    Map<String, Object> asMap();
}