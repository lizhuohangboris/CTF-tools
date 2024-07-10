package org.springframework.core.style;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/style/ToStringStyler.class */
public interface ToStringStyler {
    void styleStart(StringBuilder sb, Object obj);

    void styleEnd(StringBuilder sb, Object obj);

    void styleField(StringBuilder sb, String str, @Nullable Object obj);

    void styleValue(StringBuilder sb, Object obj);

    void styleFieldSeparator(StringBuilder sb);
}