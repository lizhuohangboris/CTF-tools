package org.thymeleaf.spring5.expression;

import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.RestrictedRequestAccessUtils;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/ThymeleafEvaluationContext.class */
public final class ThymeleafEvaluationContext extends StandardEvaluationContext implements IThymeleafEvaluationContext {
    public static final String THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME = "thymeleaf::EvaluationContext";
    private static final MapAccessor MAP_ACCESSOR_INSTANCE = new MapAccessor();
    private final ApplicationContext applicationContext;
    private IExpressionObjects expressionObjects = null;
    private boolean variableAccessRestricted = false;

    public ThymeleafEvaluationContext(ApplicationContext applicationContext, ConversionService conversionService) {
        Validate.notNull(applicationContext, "Application Context cannot be null");
        this.applicationContext = applicationContext;
        setBeanResolver(new BeanFactoryResolver(applicationContext));
        if (conversionService != null) {
            setTypeConverter(new StandardTypeConverter(conversionService));
        }
        addPropertyAccessor(SPELContextPropertyAccessor.INSTANCE);
        addPropertyAccessor(MAP_ACCESSOR_INSTANCE);
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override // org.springframework.expression.spel.support.StandardEvaluationContext, org.springframework.expression.EvaluationContext
    public Object lookupVariable(String name) {
        Object result;
        if (this.expressionObjects != null && this.expressionObjects.containsObject(name) && (result = this.expressionObjects.getObject(name)) != null) {
            if (this.variableAccessRestricted && ("request".equals(name) || StandardExpressionObjectFactory.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(name))) {
                return RestrictedRequestAccessUtils.wrapRequestObject(result);
            }
            return result;
        }
        return super.lookupVariable(name);
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public boolean isVariableAccessRestricted() {
        return this.variableAccessRestricted;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public void setVariableAccessRestricted(boolean restricted) {
        this.variableAccessRestricted = restricted;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public IExpressionObjects getExpressionObjects() {
        return this.expressionObjects;
    }

    @Override // org.thymeleaf.spring5.expression.IThymeleafEvaluationContext
    public void setExpressionObjects(IExpressionObjects expressionObjects) {
        this.expressionObjects = expressionObjects;
    }
}