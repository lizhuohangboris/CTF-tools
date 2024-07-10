package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/ScriptSource.class */
public interface ScriptSource {
    String getScriptAsString() throws IOException;

    boolean isModified();

    @Nullable
    String suggestedClassName();
}