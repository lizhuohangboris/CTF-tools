package org.thymeleaf.context;

import java.util.Locale;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/Context.class */
public final class Context extends AbstractContext {
    public Context() {
    }

    public Context(Locale locale) {
        super(locale);
    }

    public Context(Locale locale, Map<String, Object> variables) {
        super(locale, variables);
    }
}