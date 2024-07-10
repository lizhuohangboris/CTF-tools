package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/ScriptAssertContext.class */
class ScriptAssertContext {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final String script;
    private final ScriptEvaluator scriptEvaluator;

    public ScriptAssertContext(String script, ScriptEvaluator scriptEvaluator) {
        this.script = script;
        this.scriptEvaluator = scriptEvaluator;
    }

    public boolean evaluateScriptAssertExpression(Object object, String alias) {
        Map<String, Object> bindings = CollectionHelper.newHashMap();
        bindings.put(alias, object);
        return evaluateScriptAssertExpression(bindings);
    }

    public boolean evaluateScriptAssertExpression(Map<String, Object> bindings) {
        try {
            Object result = this.scriptEvaluator.evaluate(this.script, bindings);
            return handleResult(result);
        } catch (ScriptEvaluationException e) {
            throw LOG.getErrorDuringScriptExecutionException(this.script, e);
        }
    }

    private boolean handleResult(Object evaluationResult) {
        if (evaluationResult == null) {
            throw LOG.getScriptMustReturnTrueOrFalseException(this.script);
        }
        if (!(evaluationResult instanceof Boolean)) {
            throw LOG.getScriptMustReturnTrueOrFalseException(this.script, evaluationResult, evaluationResult.getClass().getCanonicalName());
        }
        return Boolean.TRUE.equals(evaluationResult);
    }
}