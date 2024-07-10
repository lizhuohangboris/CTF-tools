package org.hibernate.validator.spi.scripting;

import java.util.Map;
import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/ScriptEvaluator.class */
public interface ScriptEvaluator {
    Object evaluate(String str, Map<String, Object> map) throws ScriptEvaluationException;
}