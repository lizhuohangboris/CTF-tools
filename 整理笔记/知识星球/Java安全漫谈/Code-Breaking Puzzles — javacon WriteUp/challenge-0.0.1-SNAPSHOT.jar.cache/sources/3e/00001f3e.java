package org.springframework.expression.spel.ast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelNode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/InlineMap.class */
public class InlineMap extends SpelNodeImpl {
    @Nullable
    private TypedValue constant;

    public InlineMap(int pos, SpelNodeImpl... args) {
        super(pos, args);
        checkIfConstant();
    }

    private void checkIfConstant() {
        Object key;
        boolean isConstant = true;
        int c = 0;
        int max = getChildCount();
        while (true) {
            if (c >= max) {
                break;
            }
            SpelNode child = getChild(c);
            if (!(child instanceof Literal)) {
                if (child instanceof InlineList) {
                    InlineList inlineList = (InlineList) child;
                    if (!inlineList.isConstant()) {
                        isConstant = false;
                        break;
                    }
                } else if (child instanceof InlineMap) {
                    InlineMap inlineMap = (InlineMap) child;
                    if (!inlineMap.isConstant()) {
                        isConstant = false;
                        break;
                    }
                } else if (c % 2 != 0 || !(child instanceof PropertyOrFieldReference)) {
                    break;
                }
            }
            c++;
        }
        isConstant = false;
        if (isConstant) {
            Map<Object, Object> constantMap = new LinkedHashMap<>();
            int childCount = getChildCount();
            int c2 = 0;
            while (c2 < childCount) {
                int i = c2;
                int c3 = c2 + 1;
                SpelNode keyChild = getChild(i);
                SpelNode valueChild = getChild(c3);
                Object value = null;
                if (keyChild instanceof Literal) {
                    key = ((Literal) keyChild).getLiteralValue().getValue();
                } else if (keyChild instanceof PropertyOrFieldReference) {
                    key = ((PropertyOrFieldReference) keyChild).getName();
                } else {
                    return;
                }
                if (valueChild instanceof Literal) {
                    value = ((Literal) valueChild).getLiteralValue().getValue();
                } else if (valueChild instanceof InlineList) {
                    value = ((InlineList) valueChild).getConstantValue();
                } else if (valueChild instanceof InlineMap) {
                    value = ((InlineMap) valueChild).getConstantValue();
                }
                constantMap.put(key, value);
                c2 = c3 + 1;
            }
            this.constant = new TypedValue(Collections.unmodifiableMap(constantMap));
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState expressionState) throws EvaluationException {
        Object value;
        if (this.constant != null) {
            return this.constant;
        }
        Map<Object, Object> returnValue = new LinkedHashMap<>();
        int childcount = getChildCount();
        int c = 0;
        while (c < childcount) {
            int i = c;
            int c2 = c + 1;
            SpelNode keyChild = getChild(i);
            if (keyChild instanceof PropertyOrFieldReference) {
                PropertyOrFieldReference reference = (PropertyOrFieldReference) keyChild;
                value = reference.getName();
            } else {
                value = keyChild.getValue(expressionState);
            }
            Object key = value;
            Object value2 = getChild(c2).getValue(expressionState);
            returnValue.put(key, value2);
            c = c2 + 1;
        }
        return new TypedValue(returnValue);
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        StringBuilder sb = new StringBuilder("{");
        int count = getChildCount();
        int c = 0;
        while (c < count) {
            if (c > 0) {
                sb.append(",");
            }
            int i = c;
            int c2 = c + 1;
            sb.append(getChild(i).toStringAST());
            sb.append(":");
            sb.append(getChild(c2).toStringAST());
            c = c2 + 1;
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean isConstant() {
        return this.constant != null;
    }

    @Nullable
    public Map<Object, Object> getConstantValue() {
        Assert.state(this.constant != null, "No constant");
        return (Map) this.constant.getValue();
    }
}