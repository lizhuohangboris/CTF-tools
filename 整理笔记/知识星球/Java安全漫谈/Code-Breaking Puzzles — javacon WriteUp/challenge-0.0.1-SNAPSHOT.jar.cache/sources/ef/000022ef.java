package org.springframework.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import java.io.IOException;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scripting/groovy/GroovyScriptEvaluator.class */
public class GroovyScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {
    @Nullable
    private ClassLoader classLoader;
    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

    public GroovyScriptEvaluator() {
    }

    public GroovyScriptEvaluator(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setCompilerConfiguration(@Nullable CompilerConfiguration compilerConfiguration) {
        this.compilerConfiguration = compilerConfiguration != null ? compilerConfiguration : new CompilerConfiguration();
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return this.compilerConfiguration;
    }

    public void setCompilationCustomizers(CompilationCustomizer... compilationCustomizers) {
        this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script) {
        return evaluate(script, null);
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script, @Nullable Map<String, Object> arguments) {
        GroovyShell groovyShell = new GroovyShell(this.classLoader, new Binding(arguments), this.compilerConfiguration);
        try {
            String filename = script instanceof ResourceScriptSource ? ((ResourceScriptSource) script).getResource().getFilename() : null;
            if (filename != null) {
                return groovyShell.evaluate(script.getScriptAsString(), filename);
            }
            return groovyShell.evaluate(script.getScriptAsString());
        } catch (GroovyRuntimeException ex) {
            throw new ScriptCompilationException(script, (Throwable) ex);
        } catch (IOException ex2) {
            throw new ScriptCompilationException(script, "Cannot access Groovy script", ex2);
        }
    }
}