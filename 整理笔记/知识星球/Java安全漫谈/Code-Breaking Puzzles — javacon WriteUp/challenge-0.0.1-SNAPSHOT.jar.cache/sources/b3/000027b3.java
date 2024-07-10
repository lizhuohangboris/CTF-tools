package org.thymeleaf.context;

import java.util.Locale;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IContext.class */
public interface IContext {
    Locale getLocale();

    boolean containsVariable(String str);

    Set<String> getVariableNames();

    Object getVariable(String str);
}