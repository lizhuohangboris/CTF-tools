package org.thymeleaf.context;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/AbstractContext.class */
public abstract class AbstractContext implements IContext {
    private final Map<String, Object> variables;
    private Locale locale;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractContext() {
        this(null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractContext(Locale locale) {
        this(locale, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractContext(Locale locale, Map<String, Object> variables) {
        LinkedHashMap linkedHashMap;
        this.locale = locale == null ? Locale.getDefault() : locale;
        if (variables == null) {
            linkedHashMap = new LinkedHashMap(10);
        } else {
            linkedHashMap = new LinkedHashMap(variables);
        }
        this.variables = linkedHashMap;
    }

    @Override // org.thymeleaf.context.IContext
    public final Locale getLocale() {
        return this.locale;
    }

    @Override // org.thymeleaf.context.IContext
    public final boolean containsVariable(String name) {
        return this.variables.containsKey(name);
    }

    @Override // org.thymeleaf.context.IContext
    public final Set<String> getVariableNames() {
        return this.variables.keySet();
    }

    @Override // org.thymeleaf.context.IContext
    public final Object getVariable(String name) {
        return this.variables.get(name);
    }

    public void setLocale(Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        this.locale = locale;
    }

    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    public void setVariables(Map<String, Object> variables) {
        if (variables == null) {
            return;
        }
        this.variables.putAll(variables);
    }

    public void removeVariable(String name) {
        this.variables.remove(name);
    }

    public void clearVariables() {
        this.variables.clear();
    }
}