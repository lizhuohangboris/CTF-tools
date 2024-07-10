package org.springframework.core.style;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/style/StylerUtils.class */
public abstract class StylerUtils {
    static final ValueStyler DEFAULT_VALUE_STYLER = new DefaultValueStyler();

    public static String style(Object value) {
        return DEFAULT_VALUE_STYLER.style(value);
    }
}