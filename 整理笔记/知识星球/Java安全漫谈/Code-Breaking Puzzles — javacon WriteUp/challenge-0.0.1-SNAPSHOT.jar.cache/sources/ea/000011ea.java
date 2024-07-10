package org.hibernate.validator.spi.scripting;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/ScriptEngineScriptEvaluator.class */
public class ScriptEngineScriptEvaluator implements ScriptEvaluator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ScriptEngine engine;

    public ScriptEngineScriptEvaluator(ScriptEngine engine) {
        this.engine = engine;
    }

    @Override // org.hibernate.validator.spi.scripting.ScriptEvaluator
    public Object evaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
        Object doEvaluate;
        if (engineAllowsParallelAccessFromMultipleThreads()) {
            return doEvaluate(script, bindings);
        }
        synchronized (this.engine) {
            doEvaluate = doEvaluate(script, bindings);
        }
        return doEvaluate;
    }

    private Object doEvaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
        try {
            return this.engine.eval(script, new SimpleBindings(bindings));
        } catch (Exception e) {
            throw LOG.getErrorExecutingScriptException(script, e);
        }
    }

    private boolean engineAllowsParallelAccessFromMultipleThreads() {
        String threadingType = (String) this.engine.getFactory().getParameter("THREADING");
        return "THREAD-ISOLATED".equals(threadingType) || "STATELESS".equals(threadingType);
    }
}