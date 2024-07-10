package javax.el;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELContext.class */
public abstract class ELContext {
    private Locale locale;
    private Map<Class<?>, Object> map;
    private ImportHandler importHandler = null;
    private List<EvaluationListener> listeners = new ArrayList();
    private Deque<Map<String, Object>> lambdaArguments = new LinkedList();
    private boolean resolved = false;

    public abstract ELResolver getELResolver();

    public abstract FunctionMapper getFunctionMapper();

    public abstract VariableMapper getVariableMapper();

    public void setPropertyResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setPropertyResolved(Object base, Object property) {
        setPropertyResolved(true);
        notifyPropertyResolved(base, property);
    }

    public boolean isPropertyResolved() {
        return this.resolved;
    }

    public void putContext(Class key, Object contextObject) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(contextObject);
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(key, contextObject);
    }

    public Object getContext(Class key) {
        Objects.requireNonNull(key);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public ImportHandler getImportHandler() {
        if (this.importHandler == null) {
            this.importHandler = new ImportHandler();
        }
        return this.importHandler;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void addEvaluationListener(EvaluationListener listener) {
        this.listeners.add(listener);
    }

    public List<EvaluationListener> getEvaluationListeners() {
        return this.listeners;
    }

    public void notifyBeforeEvaluation(String expression) {
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.beforeEvaluation(this, expression);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyAfterEvaluation(String expression) {
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.afterEvaluation(this, expression);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public void notifyPropertyResolved(Object base, Object property) {
        for (EvaluationListener listener : this.listeners) {
            try {
                listener.propertyResolved(this, base, property);
            } catch (Throwable t) {
                Util.handleThrowable(t);
            }
        }
    }

    public boolean isLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            if (arguments.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public Object getLambdaArgument(String name) {
        for (Map<String, Object> arguments : this.lambdaArguments) {
            Object result = arguments.get(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void enterLambdaScope(Map<String, Object> arguments) {
        this.lambdaArguments.push(arguments);
    }

    public void exitLambdaScope() {
        this.lambdaArguments.pop();
    }

    public Object convertToType(Object obj, Class<?> type) {
        boolean originalResolved = isPropertyResolved();
        setPropertyResolved(false);
        try {
            ELResolver resolver = getELResolver();
            if (resolver != null) {
                Object result = resolver.convertToType(this, obj, type);
                if (isPropertyResolved()) {
                    return result;
                }
            }
            setPropertyResolved(originalResolved);
            return ELManager.getExpressionFactory().coerceToType(obj, type);
        } finally {
            setPropertyResolved(originalResolved);
        }
    }
}