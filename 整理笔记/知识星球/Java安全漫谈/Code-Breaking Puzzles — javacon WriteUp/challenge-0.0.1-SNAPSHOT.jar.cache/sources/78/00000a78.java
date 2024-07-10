package org.apache.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.ExpressionBuilder;
import org.apache.el.stream.StreamELResolverImpl;
import org.apache.el.util.MessageFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/ExpressionFactoryImpl.class */
public class ExpressionFactoryImpl extends ExpressionFactory {
    @Override // javax.el.ExpressionFactory
    public Object coerceToType(Object obj, Class<?> type) {
        return ELSupport.coerceToType(null, obj, type);
    }

    @Override // javax.el.ExpressionFactory
    public MethodExpression createMethodExpression(ELContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createMethodExpression(expectedReturnType, expectedParamTypes);
    }

    @Override // javax.el.ExpressionFactory
    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createValueExpression(expectedType);
    }

    @Override // javax.el.ExpressionFactory
    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        return new ValueExpressionLiteral(instance, expectedType);
    }

    @Override // javax.el.ExpressionFactory
    public ELResolver getStreamELResolver() {
        return new StreamELResolverImpl();
    }
}