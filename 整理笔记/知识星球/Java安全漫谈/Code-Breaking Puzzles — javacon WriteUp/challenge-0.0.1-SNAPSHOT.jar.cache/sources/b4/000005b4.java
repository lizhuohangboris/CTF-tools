package com.fasterxml.jackson.module.paramnames;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-module-parameter-names-2.9.7.jar:com/fasterxml/jackson/module/paramnames/ParameterExtractor.class */
class ParameterExtractor {
    public Parameter[] getParameters(Executable executable) {
        return executable.getParameters();
    }
}