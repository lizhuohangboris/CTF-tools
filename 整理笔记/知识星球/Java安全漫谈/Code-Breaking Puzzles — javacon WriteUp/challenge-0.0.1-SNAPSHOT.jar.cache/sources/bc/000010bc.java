package org.hibernate.validator.internal.engine.scripting;

import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.spi.scripting.AbstractCachingScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEngineScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/scripting/DefaultScriptEvaluatorFactory.class */
public class DefaultScriptEvaluatorFactory extends AbstractCachingScriptEvaluatorFactory {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private ClassLoader classLoader;
    private volatile ScriptEngineManager scriptEngineManager;
    private volatile ScriptEngineManager threadContextClassLoaderScriptEngineManager;

    public DefaultScriptEvaluatorFactory(ClassLoader externalClassLoader) {
        this.classLoader = externalClassLoader == null ? DefaultScriptEvaluatorFactory.class.getClassLoader() : externalClassLoader;
    }

    @Override // org.hibernate.validator.spi.scripting.AbstractCachingScriptEvaluatorFactory, org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory
    public void clear() {
        super.clear();
        this.classLoader = null;
        this.scriptEngineManager = null;
        this.threadContextClassLoaderScriptEngineManager = null;
    }

    @Override // org.hibernate.validator.spi.scripting.AbstractCachingScriptEvaluatorFactory
    protected ScriptEvaluator createNewScriptEvaluator(String languageName) throws ScriptEvaluationException {
        ScriptEngine engine = getScriptEngineManager().getEngineByName(languageName);
        if (engine == null) {
            engine = getThreadContextClassLoaderScriptEngineManager().getEngineByName(languageName);
        }
        if (engine == null) {
            throw LOG.getUnableToFindScriptEngineException(languageName);
        }
        return new ScriptEngineScriptEvaluator(engine);
    }

    private ScriptEngineManager getScriptEngineManager() {
        if (this.scriptEngineManager == null) {
            synchronized (this) {
                if (this.scriptEngineManager == null) {
                    this.scriptEngineManager = new ScriptEngineManager(this.classLoader);
                }
            }
        }
        return this.scriptEngineManager;
    }

    private ScriptEngineManager getThreadContextClassLoaderScriptEngineManager() {
        if (this.threadContextClassLoaderScriptEngineManager == null) {
            synchronized (this) {
                if (this.threadContextClassLoaderScriptEngineManager == null) {
                    this.threadContextClassLoaderScriptEngineManager = new ScriptEngineManager((ClassLoader) run(GetClassLoader.fromContext()));
                }
            }
        }
        return this.threadContextClassLoaderScriptEngineManager;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? (T) AccessController.doPrivileged(action) : action.run();
    }
}