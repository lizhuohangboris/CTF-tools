package org.springframework.expression.spel.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.ast.ValueRef;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/Projection.class */
public class Projection extends SpelNodeImpl {
    private final boolean nullSafe;

    public Projection(boolean nullSafe, int pos, SpelNodeImpl expression) {
        super(pos, expression);
        this.nullSafe = nullSafe;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        return getValueRef(state).getValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public ValueRef getValueRef(ExpressionState state) throws EvaluationException {
        TypedValue op = state.getActiveContextObject();
        Object operand = op.getValue();
        boolean operandIsArray = ObjectUtils.isArray(operand);
        if (operand instanceof Map) {
            Map<?, ?> mapData = (Map) operand;
            List<Object> result = new ArrayList<>();
            for (Map.Entry<?, ?> entry : mapData.entrySet()) {
                try {
                    state.pushActiveContextObject(new TypedValue(entry));
                    state.enterScope();
                    result.add(this.children[0].getValueInternal(state).getValue());
                    state.popActiveContextObject();
                    state.exitScope();
                } catch (Throwable th) {
                    state.popActiveContextObject();
                    state.exitScope();
                    throw th;
                }
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
        } else if ((operand instanceof Iterable) || operandIsArray) {
            Iterable<?> data = operand instanceof Iterable ? (Iterable) operand : Arrays.asList(ObjectUtils.toObjectArray(operand));
            List<Object> result2 = new ArrayList<>();
            int idx = 0;
            Class<?> arrayElementType = null;
            for (Object element : data) {
                try {
                    state.pushActiveContextObject(new TypedValue(element));
                    state.enterScope(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, Integer.valueOf(idx));
                    Object value = this.children[0].getValueInternal(state).getValue();
                    if (value != null && operandIsArray) {
                        arrayElementType = determineCommonType(arrayElementType, value.getClass());
                    }
                    result2.add(value);
                    state.exitScope();
                    state.popActiveContextObject();
                    idx++;
                } catch (Throwable th2) {
                    state.exitScope();
                    state.popActiveContextObject();
                    throw th2;
                }
            }
            if (operandIsArray) {
                if (arrayElementType == null) {
                    arrayElementType = Object.class;
                }
                Object resultArray = Array.newInstance(arrayElementType, result2.size());
                System.arraycopy(result2.toArray(), 0, resultArray, 0, result2.size());
                return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
            }
            return new ValueRef.TypedValueHolderValueRef(new TypedValue(result2), this);
        } else if (operand == null) {
            if (this.nullSafe) {
                return ValueRef.NullValueRef.INSTANCE;
            }
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, BeanDefinitionParserDelegate.NULL_ELEMENT);
        } else {
            throw new SpelEvaluationException(getStartPosition(), SpelMessage.PROJECTION_NOT_SUPPORTED_ON_TYPE, operand.getClass().getName());
        }
    }

    @Override // org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        return "![" + getChild(0).toStringAST() + "]";
    }

    private Class<?> determineCommonType(@Nullable Class<?> oldType, Class<?> newType) {
        if (oldType == null) {
            return newType;
        }
        if (oldType.isAssignableFrom(newType)) {
            return oldType;
        }
        Class<?> cls = newType;
        while (true) {
            Class<?> nextType = cls;
            if (nextType != Object.class) {
                if (nextType.isAssignableFrom(oldType)) {
                    return nextType;
                }
                cls = nextType.getSuperclass();
            } else {
                for (Class<?> nextInterface : ClassUtils.getAllInterfacesForClassAsSet(newType)) {
                    if (nextInterface.isAssignableFrom(oldType)) {
                        return nextInterface;
                    }
                }
                return Object.class;
            }
        }
    }
}