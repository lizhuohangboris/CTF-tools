package org.apache.el.lang;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.EvaluationListener;
import javax.el.FunctionMapper;
import javax.el.ImportHandler;
import javax.el.VariableMapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/lang/EvaluationContext.class */
public final class EvaluationContext extends ELContext {
    private final ELContext elContext;
    private final FunctionMapper fnMapper;
    private final VariableMapper varMapper;

    public EvaluationContext(ELContext elContext, FunctionMapper fnMapper, VariableMapper varMapper) {
        this.elContext = elContext;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
    }

    public ELContext getELContext() {
        return this.elContext;
    }

    @Override // javax.el.ELContext
    public FunctionMapper getFunctionMapper() {
        return this.fnMapper;
    }

    @Override // javax.el.ELContext
    public VariableMapper getVariableMapper() {
        return this.varMapper;
    }

    @Override // javax.el.ELContext
    public Object getContext(Class key) {
        return this.elContext.getContext(key);
    }

    @Override // javax.el.ELContext
    public ELResolver getELResolver() {
        return this.elContext.getELResolver();
    }

    @Override // javax.el.ELContext
    public boolean isPropertyResolved() {
        return this.elContext.isPropertyResolved();
    }

    @Override // javax.el.ELContext
    public void putContext(Class key, Object contextObject) {
        this.elContext.putContext(key, contextObject);
    }

    @Override // javax.el.ELContext
    public void setPropertyResolved(boolean resolved) {
        this.elContext.setPropertyResolved(resolved);
    }

    @Override // javax.el.ELContext
    public Locale getLocale() {
        return this.elContext.getLocale();
    }

    @Override // javax.el.ELContext
    public void setLocale(Locale locale) {
        this.elContext.setLocale(locale);
    }

    @Override // javax.el.ELContext
    public void setPropertyResolved(Object base, Object property) {
        this.elContext.setPropertyResolved(base, property);
    }

    @Override // javax.el.ELContext
    public ImportHandler getImportHandler() {
        return this.elContext.getImportHandler();
    }

    @Override // javax.el.ELContext
    public void addEvaluationListener(EvaluationListener listener) {
        this.elContext.addEvaluationListener(listener);
    }

    @Override // javax.el.ELContext
    public List<EvaluationListener> getEvaluationListeners() {
        return this.elContext.getEvaluationListeners();
    }

    @Override // javax.el.ELContext
    public void notifyBeforeEvaluation(String expression) {
        this.elContext.notifyBeforeEvaluation(expression);
    }

    @Override // javax.el.ELContext
    public void notifyAfterEvaluation(String expression) {
        this.elContext.notifyAfterEvaluation(expression);
    }

    @Override // javax.el.ELContext
    public void notifyPropertyResolved(Object base, Object property) {
        this.elContext.notifyPropertyResolved(base, property);
    }

    @Override // javax.el.ELContext
    public boolean isLambdaArgument(String name) {
        return this.elContext.isLambdaArgument(name);
    }

    @Override // javax.el.ELContext
    public Object getLambdaArgument(String name) {
        return this.elContext.getLambdaArgument(name);
    }

    @Override // javax.el.ELContext
    public void enterLambdaScope(Map<String, Object> arguments) {
        this.elContext.enterLambdaScope(arguments);
    }

    @Override // javax.el.ELContext
    public void exitLambdaScope() {
        this.elContext.exitLambdaScope();
    }

    @Override // javax.el.ELContext
    public Object convertToType(Object obj, Class<?> type) {
        return this.elContext.convertToType(obj, type);
    }
}