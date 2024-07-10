package org.apache.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.FunctionMapper;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.el.VariableMapper;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.lang.ExpressionBuilder;
import org.apache.el.parser.AstLiteralExpression;
import org.apache.el.parser.Node;
import org.apache.el.util.ReflectionUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/ValueExpressionImpl.class */
public final class ValueExpressionImpl extends ValueExpression implements Externalizable {
    private Class<?> expectedType;
    private String expr;
    private FunctionMapper fnMapper;
    private VariableMapper varMapper;
    private transient Node node;

    public ValueExpressionImpl() {
    }

    public ValueExpressionImpl(String expr, Node node, FunctionMapper fnMapper, VariableMapper varMapper, Class<?> expectedType) {
        this.expr = expr;
        this.node = node;
        this.fnMapper = fnMapper;
        this.varMapper = varMapper;
        this.expectedType = expectedType;
    }

    @Override // javax.el.Expression
    public boolean equals(Object obj) {
        if (!(obj instanceof ValueExpressionImpl) || obj.hashCode() != hashCode()) {
            return false;
        }
        return getNode().equals(((ValueExpressionImpl) obj).getNode());
    }

    @Override // javax.el.ValueExpression
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    @Override // javax.el.Expression
    public String getExpressionString() {
        return this.expr;
    }

    private Node getNode() throws ELException {
        if (this.node == null) {
            this.node = ExpressionBuilder.createNode(this.expr);
        }
        return this.node;
    }

    @Override // javax.el.ValueExpression
    public Class<?> getType(ELContext context) throws PropertyNotFoundException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        context.notifyBeforeEvaluation(getExpressionString());
        Class<?> result = getNode().getType(ctx);
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // javax.el.ValueExpression
    public Object getValue(ELContext context) throws PropertyNotFoundException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        context.notifyBeforeEvaluation(getExpressionString());
        Object value = getNode().getValue(ctx);
        if (this.expectedType != null) {
            value = context.convertToType(value, this.expectedType);
        }
        context.notifyAfterEvaluation(getExpressionString());
        return value;
    }

    @Override // javax.el.Expression
    public int hashCode() {
        return getNode().hashCode();
    }

    @Override // javax.el.Expression
    public boolean isLiteralText() {
        try {
            return getNode() instanceof AstLiteralExpression;
        } catch (ELException e) {
            return false;
        }
    }

    @Override // javax.el.ValueExpression
    public boolean isReadOnly(ELContext context) throws PropertyNotFoundException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        context.notifyBeforeEvaluation(getExpressionString());
        boolean result = getNode().isReadOnly(ctx);
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.expr = in.readUTF();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
        this.fnMapper = (FunctionMapper) in.readObject();
        this.varMapper = (VariableMapper) in.readObject();
    }

    @Override // javax.el.ValueExpression
    public void setValue(ELContext context, Object value) throws PropertyNotFoundException, PropertyNotWritableException, ELException {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        context.notifyBeforeEvaluation(getExpressionString());
        getNode().setValue(ctx, value);
        context.notifyAfterEvaluation(getExpressionString());
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.expr);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
        out.writeObject(this.fnMapper);
        out.writeObject(this.varMapper);
    }

    public String toString() {
        return "ValueExpression[" + this.expr + "]";
    }

    @Override // javax.el.ValueExpression
    public ValueReference getValueReference(ELContext context) {
        EvaluationContext ctx = new EvaluationContext(context, this.fnMapper, this.varMapper);
        context.notifyBeforeEvaluation(getExpressionString());
        ValueReference result = getNode().getValueReference(ctx);
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }
}