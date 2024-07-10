package org.thymeleaf.spring5.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SPELContextMapWrapper.class */
public final class SPELContextMapWrapper implements Map {
    private static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";
    private final IContext context;
    private final IThymeleafEvaluationContext evaluationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SPELContextMapWrapper(IContext context, IThymeleafEvaluationContext evaluationContext) {
        this.context = context;
        this.evaluationContext = evaluationContext;
    }

    @Override // java.util.Map
    public int size() {
        throw new TemplateProcessingException("Cannot call #size() on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        throw new TemplateProcessingException("Cannot call #isEmpty() on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (this.evaluationContext.isVariableAccessRestricted() && REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(key)) {
            throw new TemplateProcessingException("Access to variable \"" + key + "\" is forbidden in this context. Note some restrictions apply to variable access. For example, direct access to request parameters is forbidden in preprocessing and unescaped expressions, in TEXT template mode, in fragment insertion specifications and in some specific attribute processors.");
        }
        return this.context != null;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        throw new TemplateProcessingException("Cannot call #containsValue(value) on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public Object get(Object key) {
        Object execInfoResult;
        if (this.context == null) {
            throw new TemplateProcessingException("Cannot read property on null target");
        }
        if (!StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(key) || (execInfoResult = SPELContextPropertyAccessor.checkExecInfo(key.toString(), this.evaluationContext)) == null) {
            return this.context.getVariable(key == null ? null : key.toString());
        }
        return execInfoResult;
    }

    @Override // java.util.Map
    public Object put(Object key, Object value) {
        throw new TemplateProcessingException("Cannot call #put(key,value) on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public Object remove(Object key) {
        throw new TemplateProcessingException("Cannot call #remove(key) on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public void putAll(Map m) {
        throw new TemplateProcessingException("Cannot call #putAll(m) on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public void clear() {
        throw new TemplateProcessingException("Cannot call #clear() on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public Set keySet() {
        throw new TemplateProcessingException("Cannot call #keySet() on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public Collection values() {
        throw new TemplateProcessingException("Cannot call #values() on an " + IContext.class.getSimpleName() + " implementation");
    }

    @Override // java.util.Map
    public Set<Map.Entry> entrySet() {
        throw new TemplateProcessingException("Cannot call #entrySet() on an " + IContext.class.getSimpleName() + " implementation");
    }
}