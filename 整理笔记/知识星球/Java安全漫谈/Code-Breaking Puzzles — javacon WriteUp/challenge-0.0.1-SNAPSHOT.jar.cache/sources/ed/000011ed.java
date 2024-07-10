package org.hibernate.validator.spi.scripting;

import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/ScriptEvaluatorFactory.class */
public interface ScriptEvaluatorFactory {
    ScriptEvaluator getScriptEvaluatorByLanguageName(String str);

    void clear();
}