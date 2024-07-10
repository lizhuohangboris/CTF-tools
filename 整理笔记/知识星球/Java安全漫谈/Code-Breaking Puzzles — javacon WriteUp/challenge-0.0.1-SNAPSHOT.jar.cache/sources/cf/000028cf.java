package org.thymeleaf.spring5.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SPELContextPropertyAccessor.class */
public final class SPELContextPropertyAccessor implements PropertyAccessor {
    private static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";
    private static final Logger LOGGER = LoggerFactory.getLogger(SPELContextPropertyAccessor.class);
    static final SPELContextPropertyAccessor INSTANCE = new SPELContextPropertyAccessor();
    private static final Class<?>[] TARGET_CLASSES = {IContext.class};

    SPELContextPropertyAccessor() {
    }

    @Override // org.springframework.expression.PropertyAccessor
    public Class<?>[] getSpecificTargetClasses() {
        return TARGET_CLASSES;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        if ((context instanceof IThymeleafEvaluationContext) && ((IThymeleafEvaluationContext) context).isVariableAccessRestricted() && REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(name)) {
            throw new AccessException("Access to variable \"" + name + "\" is forbidden in this context. Note some restrictions apply to variable access. For example, direct access to request parameters is forbidden in preprocessing and unescaped expressions, in TEXT template mode, in fragment insertion specifications and in some specific attribute processors.");
        }
        return target != null;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public TypedValue read(EvaluationContext evaluationContext, Object target, String name) throws AccessException {
        Object execInfoResult;
        if (target == null) {
            throw new AccessException("Cannot read property of null target");
        }
        try {
            if (StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(name) && (execInfoResult = checkExecInfo(name, evaluationContext)) != null) {
                return new TypedValue(execInfoResult);
            }
            IContext context = (IContext) target;
            return new TypedValue(context.getVariable(name));
        } catch (ClassCastException e) {
            throw new AccessException("Cannot read target of class " + target.getClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static Object checkExecInfo(String propertyName, EvaluationContext context) {
        if (StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(propertyName)) {
            if (!(context instanceof IThymeleafEvaluationContext)) {
                throw new TemplateProcessingException("Found Thymeleaf Standard Expression containing a call to the context variable \"execInfo\" (e.g. \"${execInfo.templateName}\"), which has been deprecated. The Execution Info should be now accessed as an expression object instead (e.g. \"${#execInfo.templateName}\"). Deprecated use is still allowed (will be removed in future versions of Thymeleaf) when the SpringEL EvaluationContext implements the " + IThymeleafEvaluationContext.class + " interface, but the current evaluation context of class " + context.getClass().getName() + " DOES NOT implement such interface.");
            }
            LOGGER.warn("[THYMELEAF][{}] Found Thymeleaf Standard Expression containing a call to the context variable \"execInfo\" (e.g. \"${execInfo.templateName}\"), which has been deprecated. The Execution Info should be now accessed as an expression object instead (e.g. \"${#execInfo.templateName}\"). Deprecated use is still allowed, but will be removed in future versions of Thymeleaf.", TemplateEngine.threadIndex());
            return ((IThymeleafEvaluationContext) context).getExpressionObjects().getObject(StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME);
        }
        return null;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return false;
    }

    @Override // org.springframework.expression.PropertyAccessor
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        throw new AccessException("Cannot write to " + IContext.class.getName());
    }
}