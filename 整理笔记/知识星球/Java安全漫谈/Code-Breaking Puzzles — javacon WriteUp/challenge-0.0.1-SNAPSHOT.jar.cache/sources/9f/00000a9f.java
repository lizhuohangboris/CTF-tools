package org.apache.el.parser;

import javax.el.ELClass;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.el.VariableMapper;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.Validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstIdentifier.class */
public final class AstIdentifier extends SimpleNode {
    public AstIdentifier(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.getType(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        Class<?> result = ctx.getELResolver().getType(ctx, null, this.image);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        ValueExpression expr;
        if (ctx.isLambdaArgument(this.image)) {
            return ctx.getLambdaArgument(this.image);
        }
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.getValue(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        if (this.parent instanceof AstValue) {
            ctx.putContext(getClass(), Boolean.FALSE);
        } else {
            ctx.putContext(getClass(), Boolean.TRUE);
        }
        try {
            Object result = ctx.getELResolver().getValue(ctx, null, this.image);
            ctx.putContext(getClass(), Boolean.FALSE);
            if (ctx.isPropertyResolved()) {
                return result;
            }
            Object result2 = ctx.getImportHandler().resolveClass(this.image);
            if (result2 != null) {
                return new ELClass((Class) result2);
            }
            Object result3 = ctx.getImportHandler().resolveStatic(this.image);
            if (result3 != null) {
                try {
                    return ((Class) result3).getField(this.image).get(null);
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    throw new ELException(e);
                }
            }
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        } catch (Throwable th) {
            ctx.putContext(getClass(), Boolean.FALSE);
            throw th;
        }
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.isReadOnly(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        boolean result = ctx.getELResolver().isReadOnly(ctx, null, this.image);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            expr.setValue(ctx.getELContext(), value);
            return;
        }
        ctx.setPropertyResolved(false);
        ctx.getELResolver().setValue(ctx, null, this.image, value);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object invoke(EvaluationContext ctx, Class<?>[] paramTypes, Object[] paramValues) throws ELException {
        return getMethodExpression(ctx).invoke(ctx.getELContext(), paramValues);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public MethodInfo getMethodInfo(EvaluationContext ctx, Class<?>[] paramTypes) throws ELException {
        return getMethodExpression(ctx).getMethodInfo(ctx.getELContext());
    }

    @Override // org.apache.el.parser.SimpleNode
    public void setImage(String image) {
        if (!Validation.isIdentifier(image)) {
            throw new ELException(MessageFactory.get("error.identifier.notjava", image));
        }
        this.image = image;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public ValueReference getValueReference(EvaluationContext ctx) {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper == null || (expr = varMapper.resolveVariable(this.image)) == null) {
            return null;
        }
        return expr.getValueReference(ctx);
    }

    private final MethodExpression getMethodExpression(EvaluationContext ctx) throws ELException {
        Object obj = null;
        VariableMapper varMapper = ctx.getVariableMapper();
        ValueExpression ve = null;
        if (varMapper != null) {
            ve = varMapper.resolveVariable(this.image);
            if (ve != null) {
                obj = ve.getValue(ctx);
            }
        }
        if (ve == null) {
            ctx.setPropertyResolved(false);
            obj = ctx.getELResolver().getValue(ctx, null, this.image);
        }
        if (obj instanceof MethodExpression) {
            return (MethodExpression) obj;
        }
        if (obj == null) {
            throw new MethodNotFoundException("Identity '" + this.image + "' was null and was unable to invoke");
        }
        throw new ELException("Identity '" + this.image + "' does not reference a MethodExpression instance, returned type: " + obj.getClass().getName());
    }
}