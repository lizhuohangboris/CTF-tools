package org.hibernate.validator.spi.scripting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.validator.Incubating;

@Incubating
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/spi/scripting/AbstractCachingScriptEvaluatorFactory.class */
public abstract class AbstractCachingScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    private final ConcurrentMap<String, ScriptEvaluator> scriptEvaluatorCache = new ConcurrentHashMap();

    protected abstract ScriptEvaluator createNewScriptEvaluator(String str) throws ScriptEvaluatorNotFoundException;

    @Override // org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory
    public ScriptEvaluator getScriptEvaluatorByLanguageName(String languageName) {
        return this.scriptEvaluatorCache.computeIfAbsent(languageName, this::createNewScriptEvaluator);
    }

    @Override // org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory
    public void clear() {
        this.scriptEvaluatorCache.clear();
    }
}