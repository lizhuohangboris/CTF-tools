package org.thymeleaf.standard.expression;

import java.util.Map;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import ognl.enhance.UnsupportedCompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLContextPropertyAccessor.class */
public final class OGNLContextPropertyAccessor implements PropertyAccessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OGNLContextPropertyAccessor.class);
    public static final String RESTRICT_REQUEST_PARAMETERS = "%RESTRICT_REQUEST_PARAMETERS%";
    static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";

    public Object getProperty(Map ognlContext, Object target, Object name) throws OgnlException {
        if (!(target instanceof IContext)) {
            throw new IllegalStateException("Wrong target type. This property accessor is only usable for " + IContext.class.getName() + " implementations, and in this case the target object is " + (target == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : "of class " + target.getClass().getName()));
        } else if (REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(name) && ognlContext != null && ognlContext.containsKey(RESTRICT_REQUEST_PARAMETERS)) {
            throw new OgnlException("Access to variable \"" + name + "\" is forbidden in this context. Note some restrictions apply to variable access. For example, direct access to request parameters is forbidden in preprocessing and unescaped expressions, in TEXT template mode, in fragment insertion specifications and in some specific attribute processors.");
        } else {
            String propertyName = name == null ? null : name.toString();
            Object execInfoResult = checkExecInfo(propertyName, ognlContext);
            if (execInfoResult != null) {
                return execInfoResult;
            }
            IContext context = (IContext) target;
            return context.getVariable(propertyName);
        }
    }

    @Deprecated
    private static Object checkExecInfo(String propertyName, Map<String, Object> context) {
        if (StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(propertyName)) {
            LOGGER.warn("[THYMELEAF][{}] Found Thymeleaf Standard Expression containing a call to the context variable \"execInfo\" (e.g. \"${execInfo.templateName}\"), which has been deprecated. The Execution Info should be now accessed as an expression object instead (e.g. \"${#execInfo.templateName}\"). Deprecated use is still allowed, but will be removed in future versions of Thymeleaf.", TemplateEngine.threadIndex());
            return context.get(StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME);
        }
        return null;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        throw new UnsupportedOperationException("Cannot set values into VariablesMap instances from OGNL Expressions");
    }

    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        context.setCurrentAccessor(IContext.class);
        context.setCurrentType(Object.class);
        return ".getVariable(" + index + ")";
    }

    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        throw new UnsupportedCompilationException("Setting expression for " + context.getCurrentObject() + " with index of " + index + " cannot be computed. IVariablesMap implementations are considered read-only by OGNL.");
    }
}