package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/ScriptFactory.class */
public interface ScriptFactory {
    String getScriptSourceLocator();

    @Nullable
    Class<?>[] getScriptInterfaces();

    boolean requiresConfigInterface();

    @Nullable
    Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... clsArr) throws IOException, ScriptCompilationException;

    @Nullable
    Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException;

    boolean requiresScriptedObjectRefresh(ScriptSource scriptSource);
}