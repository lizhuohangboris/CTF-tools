package org.apache.el;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.el.ELContext;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/ValueExpressionLiteral.class */
public final class ValueExpressionLiteral extends ValueExpression implements Externalizable {
    private static final long serialVersionUID = 1;
    private Object value;
    private String valueString;
    private Class<?> expectedType;

    public ValueExpressionLiteral() {
    }

    public ValueExpressionLiteral(Object value, Class<?> expectedType) {
        this.value = value;
        this.expectedType = expectedType;
    }

    @Override // javax.el.ValueExpression
    public Object getValue(ELContext context) {
        Object result;
        context.notifyBeforeEvaluation(getExpressionString());
        if (this.expectedType != null) {
            result = context.convertToType(this.value, this.expectedType);
        } else {
            result = this.value;
        }
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // javax.el.ValueExpression
    public void setValue(ELContext context, Object value) {
        context.notifyBeforeEvaluation(getExpressionString());
        throw new PropertyNotWritableException(MessageFactory.get("error.value.literal.write", this.value));
    }

    @Override // javax.el.ValueExpression
    public boolean isReadOnly(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        context.notifyAfterEvaluation(getExpressionString());
        return true;
    }

    @Override // javax.el.ValueExpression
    public Class<?> getType(ELContext context) {
        context.notifyBeforeEvaluation(getExpressionString());
        Class<?> result = this.value != null ? this.value.getClass() : null;
        context.notifyAfterEvaluation(getExpressionString());
        return result;
    }

    @Override // javax.el.ValueExpression
    public Class<?> getExpectedType() {
        return this.expectedType;
    }

    @Override // javax.el.Expression
    public String getExpressionString() {
        if (this.valueString == null) {
            this.valueString = this.value != null ? this.value.toString() : null;
        }
        return this.valueString;
    }

    @Override // javax.el.Expression
    public boolean equals(Object obj) {
        return (obj instanceof ValueExpressionLiteral) && equals((ValueExpressionLiteral) obj);
    }

    public boolean equals(ValueExpressionLiteral ve) {
        return (ve == null || this.value == null || ve.value == null || (this.value != ve.value && !this.value.equals(ve.value))) ? false : true;
    }

    @Override // javax.el.Expression
    public int hashCode() {
        if (this.value != null) {
            return this.value.hashCode();
        }
        return 0;
    }

    @Override // javax.el.Expression
    public boolean isLiteralText() {
        return true;
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.value);
        out.writeUTF(this.expectedType != null ? this.expectedType.getName() : "");
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.value = in.readObject();
        String type = in.readUTF();
        if (!"".equals(type)) {
            this.expectedType = ReflectionUtil.forName(type);
        }
    }
}