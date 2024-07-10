package org.springframework.scripting.support;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/support/StandardScriptFactory.class */
public class StandardScriptFactory implements ScriptFactory, BeanClassLoaderAware {
    @Nullable
    private final String scriptEngineName;
    private final String scriptSourceLocator;
    @Nullable
    private final Class<?>[] scriptInterfaces;
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private volatile ScriptEngine scriptEngine;

    public StandardScriptFactory(String scriptSourceLocator) {
        this(null, scriptSourceLocator, null);
    }

    public StandardScriptFactory(String scriptSourceLocator, Class<?>... scriptInterfaces) {
        this(null, scriptSourceLocator, scriptInterfaces);
    }

    public StandardScriptFactory(String scriptEngineName, String scriptSourceLocator) {
        this(scriptEngineName, scriptSourceLocator, null);
    }

    public StandardScriptFactory(@Nullable String scriptEngineName, String scriptSourceLocator, @Nullable Class<?>... scriptInterfaces) {
        this.beanClassLoader = ClassUtils.getDefaultClassLoader();
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptEngineName = scriptEngineName;
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = scriptInterfaces;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?>[] getScriptInterfaces() {
        return this.scriptInterfaces;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public boolean requiresConfigInterface() {
        return false;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... actualInterfaces) throws IOException, ScriptCompilationException {
        int i;
        Object script = evaluateScript(scriptSource);
        if (!ObjectUtils.isEmpty((Object[]) actualInterfaces)) {
            boolean adaptationRequired = false;
            for (Class<?> requestedIfc : actualInterfaces) {
                if (!(script instanceof Class)) {
                    i = requestedIfc.isInstance(script) ? i + 1 : 0;
                    adaptationRequired = true;
                } else {
                    if (requestedIfc.isAssignableFrom((Class) script)) {
                    }
                    adaptationRequired = true;
                }
            }
            if (adaptationRequired) {
                script = adaptToInterfaces(script, scriptSource, actualInterfaces);
            }
        }
        if (script instanceof Class) {
            Class<?> scriptClass = (Class) script;
            try {
                return ReflectionUtils.accessibleConstructor(scriptClass, new Class[0]).newInstance(new Object[0]);
            } catch (IllegalAccessException ex) {
                throw new ScriptCompilationException(scriptSource, "Could not access script constructor: " + scriptClass.getName(), ex);
            } catch (InstantiationException ex2) {
                throw new ScriptCompilationException(scriptSource, "Unable to instantiate script class: " + scriptClass.getName(), ex2);
            } catch (NoSuchMethodException ex3) {
                throw new ScriptCompilationException("No default constructor on script class: " + scriptClass.getName(), ex3);
            } catch (InvocationTargetException ex4) {
                throw new ScriptCompilationException("Failed to invoke script constructor: " + scriptClass.getName(), ex4.getTargetException());
            }
        }
        return script;
    }

    protected Object evaluateScript(ScriptSource scriptSource) {
        try {
            ScriptEngine scriptEngine = this.scriptEngine;
            if (scriptEngine == null) {
                scriptEngine = retrieveScriptEngine(scriptSource);
                if (scriptEngine == null) {
                    throw new IllegalStateException("Could not determine script engine for " + scriptSource);
                }
                this.scriptEngine = scriptEngine;
            }
            return scriptEngine.eval(scriptSource.getScriptAsString());
        } catch (Exception ex) {
            throw new ScriptCompilationException(scriptSource, ex);
        }
    }

    @Nullable
    protected ScriptEngine retrieveScriptEngine(ScriptSource scriptSource) {
        String filename;
        String extension;
        ScriptEngine engine;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(this.beanClassLoader);
        if (this.scriptEngineName != null) {
            return StandardScriptUtils.retrieveEngineByName(scriptEngineManager, this.scriptEngineName);
        }
        if ((scriptSource instanceof ResourceScriptSource) && (filename = ((ResourceScriptSource) scriptSource).getResource().getFilename()) != null && (extension = StringUtils.getFilenameExtension(filename)) != null && (engine = scriptEngineManager.getEngineByExtension(extension)) != null) {
            return engine;
        }
        return null;
    }

    @Nullable
    protected Object adaptToInterfaces(@Nullable Object script, ScriptSource scriptSource, Class<?>... actualInterfaces) {
        Class<?> adaptedIfc;
        if (actualInterfaces.length == 1) {
            adaptedIfc = actualInterfaces[0];
        } else {
            adaptedIfc = ClassUtils.createCompositeInterface(actualInterfaces, this.beanClassLoader);
        }
        if (adaptedIfc != null) {
            Invocable invocable = this.scriptEngine;
            if (!(invocable instanceof Invocable)) {
                throw new ScriptCompilationException(scriptSource, "ScriptEngine must implement Invocable in order to adapt it to an interface: " + invocable);
            }
            Invocable invocable2 = invocable;
            if (script != null) {
                script = invocable2.getInterface(script, adaptedIfc);
            }
            if (script == null) {
                script = invocable2.getInterface(adaptedIfc);
                if (script == null) {
                    throw new ScriptCompilationException(scriptSource, "Could not adapt script to interface [" + adaptedIfc.getName() + "]");
                }
            }
        }
        return script;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        return null;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public boolean requiresScriptedObjectRefresh(ScriptSource scriptSource) {
        return scriptSource.isModified();
    }

    public String toString() {
        return "StandardScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}