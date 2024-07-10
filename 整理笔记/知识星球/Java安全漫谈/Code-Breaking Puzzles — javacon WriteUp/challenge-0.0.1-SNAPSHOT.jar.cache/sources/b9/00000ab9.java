package org.apache.el.parser;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.LambdaExpression;
import javax.el.MethodInfo;
import javax.el.PropertyNotFoundException;
import javax.el.ValueReference;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.stream.Optional;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstValue.class */
public final class AstValue extends SimpleNode {
    private static final Object[] EMPTY_ARRAY = new Object[0];

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstValue$Target.class */
    public static class Target {
        protected Object base;
        protected Object property;

        protected Target() {
        }
    }

    public AstValue(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        Class<?> result = ctx.getELResolver().getType(ctx, t.base, t.property);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
        return result;
    }

    private final Target getTarget(EvaluationContext ctx) throws ELException {
        Object base = this.children[0].getValue(ctx);
        if (base == null) {
            throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.base", this.children[0].getImage()));
        }
        Object property = null;
        int propCount = jjtGetNumChildren();
        int i = 1;
        ELResolver resolver = ctx.getELResolver();
        while (i < propCount) {
            if (i + 2 < propCount && (this.children[i + 1] instanceof AstMethodParameters)) {
                base = resolver.invoke(ctx, base, this.children[i].getValue(ctx), null, ((AstMethodParameters) this.children[i + 1]).getParameters(ctx));
                i += 2;
            } else if (i + 2 == propCount && (this.children[i + 1] instanceof AstMethodParameters)) {
                ctx.setPropertyResolved(false);
                property = this.children[i].getValue(ctx);
                i += 2;
                if (property == null) {
                    throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
                }
            } else if (i + 1 < propCount) {
                property = this.children[i].getValue(ctx);
                ctx.setPropertyResolved(false);
                base = resolver.getValue(ctx, base, property);
                i++;
            } else {
                ctx.setPropertyResolved(false);
                property = this.children[i].getValue(ctx);
                i++;
                if (property == null) {
                    throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
                }
            }
            if (base == null) {
                throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
            }
        }
        Target t = new Target();
        t.base = base;
        t.property = property;
        return t;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object base = this.children[0].getValue(ctx);
        int propCount = jjtGetNumChildren();
        int i = 1;
        Object suffix = null;
        ELResolver resolver = ctx.getELResolver();
        while (base != null && i < propCount) {
            suffix = this.children[i].getValue(ctx);
            if (i + 1 < propCount && (this.children[i + 1] instanceof AstMethodParameters)) {
                AstMethodParameters mps = (AstMethodParameters) this.children[i + 1];
                if ((base instanceof Optional) && "orElseGet".equals(suffix) && mps.jjtGetNumChildren() == 1) {
                    Node paramFoOptional = mps.jjtGetChild(0);
                    if (!(paramFoOptional instanceof AstLambdaExpression) && !(paramFoOptional instanceof LambdaExpression)) {
                        throw new ELException(MessageFactory.get("stream.optional.paramNotLambda", suffix));
                    }
                }
                Object[] paramValues = mps.getParameters(ctx);
                base = resolver.invoke(ctx, base, suffix, getTypesFromValues(paramValues), paramValues);
                i += 2;
            } else if (suffix == null) {
                return null;
            } else {
                ctx.setPropertyResolved(false);
                base = resolver.getValue(ctx, base, suffix);
                i++;
            }
        }
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", base, suffix));
        }
        return base;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        boolean result = ctx.getELResolver().isReadOnly(ctx, t.base, t.property);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        Target t = getTarget(ctx);
        ctx.setPropertyResolved(false);
        ELResolver resolver = ctx.getELResolver();
        Class<?> targetClass = resolver.getType(ctx, t.base, t.property);
        resolver.setValue(ctx, t.base, t.property, ELSupport.coerceToType(ctx, value, targetClass));
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public MethodInfo getMethodInfo(EvaluationContext ctx, Class[] paramTypes) throws ELException {
        Target t = getTarget(ctx);
        Method m = ReflectionUtil.getMethod(ctx, t.base, t.property, paramTypes, null);
        return new MethodInfo(m.getName(), m.getReturnType(), m.getParameterTypes());
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object invoke(EvaluationContext ctx, Class[] paramTypes, Object[] paramValues) throws ELException {
        Object[] values;
        Class<?>[] types;
        Target t = getTarget(ctx);
        if (isParametersProvided()) {
            values = ((AstMethodParameters) jjtGetChild(jjtGetNumChildren() - 1)).getParameters(ctx);
            types = getTypesFromValues(values);
        } else {
            values = paramValues;
            types = paramTypes;
        }
        Method m = ReflectionUtil.getMethod(ctx, t.base, t.property, types, values);
        try {
            Object result = m.invoke(t.base, convertArgs(ctx, values, m));
            return result;
        } catch (IllegalAccessException iae) {
            throw new ELException(iae);
        } catch (IllegalArgumentException iae2) {
            throw new ELException(iae2);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof ThreadDeath) {
                throw ((ThreadDeath) cause);
            }
            if (cause instanceof VirtualMachineError) {
                throw ((VirtualMachineError) cause);
            }
            throw new ELException(cause);
        }
    }

    private Object[] convertArgs(EvaluationContext ctx, Object[] src, Method m) {
        String msg;
        Class<?>[] types = m.getParameterTypes();
        if (types.length == 0) {
            return EMPTY_ARRAY;
        }
        int paramCount = types.length;
        if ((m.isVarArgs() && paramCount > 1 && (src == null || paramCount > src.length)) || (!m.isVarArgs() && ((paramCount > 0 && src == null) || (src != null && src.length != paramCount)))) {
            String srcCount = null;
            if (src != null) {
                srcCount = Integer.toString(src.length);
            }
            if (m.isVarArgs()) {
                msg = MessageFactory.get("error.invoke.tooFewParams", m.getName(), srcCount, Integer.toString(paramCount));
            } else {
                msg = MessageFactory.get("error.invoke.wrongParams", m.getName(), srcCount, Integer.toString(paramCount));
            }
            throw new IllegalArgumentException(msg);
        } else if (src == null) {
            return new Object[1];
        } else {
            Object[] dest = new Object[paramCount];
            for (int i = 0; i < paramCount - 1; i++) {
                dest[i] = ELSupport.coerceToType(ctx, src[i], types[i]);
            }
            if (m.isVarArgs()) {
                Class<?> varArgType = m.getParameterTypes()[paramCount - 1].getComponentType();
                Object[] varArgs = (Object[]) Array.newInstance(varArgType, src.length - (paramCount - 1));
                for (int i2 = 0; i2 < src.length - (paramCount - 1); i2++) {
                    varArgs[i2] = ELSupport.coerceToType(ctx, src[(paramCount - 1) + i2], varArgType);
                }
                dest[paramCount - 1] = varArgs;
            } else {
                dest[paramCount - 1] = ELSupport.coerceToType(ctx, src[paramCount - 1], types[paramCount - 1]);
            }
            return dest;
        }
    }

    private Class<?>[] getTypesFromValues(Object[] values) {
        if (values == null) {
            return null;
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                result[i] = null;
            } else {
                result[i] = values[i].getClass();
            }
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public ValueReference getValueReference(EvaluationContext ctx) {
        if (this.children.length > 2 && (jjtGetChild(2) instanceof AstMethodParameters)) {
            return null;
        }
        Target t = getTarget(ctx);
        return new ValueReference(t.base, t.property);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public boolean isParametersProvided() {
        int len = this.children.length;
        if (len > 2 && (jjtGetChild(len - 1) instanceof AstMethodParameters)) {
            return true;
        }
        return false;
    }
}