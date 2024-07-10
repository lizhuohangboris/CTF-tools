package org.springframework.scripting;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/ScriptCompilationException.class */
public class ScriptCompilationException extends NestedRuntimeException {
    @Nullable
    private final ScriptSource scriptSource;

    public ScriptCompilationException(String msg) {
        super(msg);
        this.scriptSource = null;
    }

    public ScriptCompilationException(String msg, Throwable cause) {
        super(msg, cause);
        this.scriptSource = null;
    }

    public ScriptCompilationException(ScriptSource scriptSource, String msg) {
        super("Could not compile " + scriptSource + ": " + msg);
        this.scriptSource = scriptSource;
    }

    public ScriptCompilationException(ScriptSource scriptSource, Throwable cause) {
        super("Could not compile " + scriptSource, cause);
        this.scriptSource = scriptSource;
    }

    public ScriptCompilationException(ScriptSource scriptSource, String msg, Throwable cause) {
        super("Could not compile " + scriptSource + ": " + msg, cause);
        this.scriptSource = scriptSource;
    }

    @Nullable
    public ScriptSource getScriptSource() {
        return this.scriptSource;
    }
}