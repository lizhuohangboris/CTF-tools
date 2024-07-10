package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import ch.qos.logback.core.util.FileUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilationFailedException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/boolex/GEventEvaluator.class */
public class GEventEvaluator extends EventEvaluatorBase<ILoggingEvent> {
    String expression;
    IEvaluator delegateEvaluator;
    Script script;

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override // ch.qos.logback.core.boolex.EventEvaluatorBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errors = 0;
        if (this.expression == null || this.expression.length() == 0) {
            addError("Empty expression");
            return;
        }
        addInfo("Expression to evaluate [" + this.expression + "]");
        ClassLoader classLoader = getClass().getClassLoader();
        String currentPackageName = getClass().getPackage().getName();
        String currentPackageName2 = currentPackageName.replace('.', '/');
        FileUtil fileUtil = new FileUtil(getContext());
        String scriptText = fileUtil.resourceAsString(classLoader, currentPackageName2 + "/EvaluatorTemplate.groovy");
        if (scriptText == null) {
            return;
        }
        String scriptText2 = scriptText.replace("//EXPRESSION", this.expression);
        GroovyClassLoader gLoader = new GroovyClassLoader(classLoader);
        try {
            Class scriptClass = gLoader.parseClass(scriptText2);
            this.delegateEvaluator = (GroovyObject) scriptClass.newInstance();
        } catch (Exception e) {
            addError("Failed to compile expression [" + this.expression + "]", e);
            errors = 0 + 1;
        } catch (CompilationFailedException cfe) {
            addError("Failed to compile expression [" + this.expression + "]", cfe);
            errors = 0 + 1;
        }
        if (errors == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.boolex.EventEvaluator
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        if (this.delegateEvaluator == null) {
            return false;
        }
        return this.delegateEvaluator.doEvaluate(event);
    }
}