package org.springframework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.slf4j.Marker;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.cglib.core.Constants;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OpPlus.class */
public class OpPlus extends Operator {
    public OpPlus(int pos, SpelNodeImpl... operands) {
        super(Marker.ANY_NON_NULL_MARKER, pos, operands);
        Assert.notEmpty(operands, "Operands must not be empty");
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        SpelNodeImpl leftOp = getLeftOperand();
        if (this.children.length < 2) {
            Object operandOne = leftOp.getValueInternal(state).getValue();
            if (operandOne instanceof Number) {
                if (operandOne instanceof Double) {
                    this.exitTypeDescriptor = "D";
                } else if (operandOne instanceof Float) {
                    this.exitTypeDescriptor = "F";
                } else if (operandOne instanceof Long) {
                    this.exitTypeDescriptor = "J";
                } else if (operandOne instanceof Integer) {
                    this.exitTypeDescriptor = "I";
                }
                return new TypedValue(operandOne);
            }
            return state.operate(Operation.ADD, operandOne, null);
        }
        TypedValue operandOneValue = leftOp.getValueInternal(state);
        Object leftOperand = operandOneValue.getValue();
        TypedValue operandTwoValue = getRightOperand().getValueInternal(state);
        Object rightOperand = operandTwoValue.getValue();
        if ((leftOperand instanceof Number) && (rightOperand instanceof Number)) {
            Number leftNumber = (Number) leftOperand;
            Number rightNumber = (Number) rightOperand;
            if ((leftNumber instanceof BigDecimal) || (rightNumber instanceof BigDecimal)) {
                BigDecimal leftBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
                BigDecimal rightBigDecimal = (BigDecimal) NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
                return new TypedValue(leftBigDecimal.add(rightBigDecimal));
            } else if ((leftNumber instanceof Double) || (rightNumber instanceof Double)) {
                this.exitTypeDescriptor = "D";
                return new TypedValue(Double.valueOf(leftNumber.doubleValue() + rightNumber.doubleValue()));
            } else if ((leftNumber instanceof Float) || (rightNumber instanceof Float)) {
                this.exitTypeDescriptor = "F";
                return new TypedValue(Float.valueOf(leftNumber.floatValue() + rightNumber.floatValue()));
            } else if ((leftNumber instanceof BigInteger) || (rightNumber instanceof BigInteger)) {
                BigInteger leftBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
                BigInteger rightBigInteger = (BigInteger) NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
                return new TypedValue(leftBigInteger.add(rightBigInteger));
            } else if ((leftNumber instanceof Long) || (rightNumber instanceof Long)) {
                this.exitTypeDescriptor = "J";
                return new TypedValue(Long.valueOf(leftNumber.longValue() + rightNumber.longValue()));
            } else if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
                this.exitTypeDescriptor = "I";
                return new TypedValue(Integer.valueOf(leftNumber.intValue() + rightNumber.intValue()));
            } else {
                return new TypedValue(Double.valueOf(leftNumber.doubleValue() + rightNumber.doubleValue()));
            }
        } else if ((leftOperand instanceof String) && (rightOperand instanceof String)) {
            this.exitTypeDescriptor = "Ljava/lang/String";
            return new TypedValue(((String) leftOperand) + rightOperand);
        } else if (leftOperand instanceof String) {
            return new TypedValue(leftOperand + (rightOperand == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : convertTypedValueToString(operandTwoValue, state)));
        } else if (rightOperand instanceof String) {
            return new TypedValue((leftOperand == null ? BeanDefinitionParserDelegate.NULL_ELEMENT : convertTypedValueToString(operandOneValue, state)) + rightOperand);
        } else {
            return state.operate(Operation.ADD, leftOperand, rightOperand);
        }
    }

    @Override // org.springframework.expression.spel.ast.Operator, org.springframework.expression.spel.SpelNode
    public String toStringAST() {
        if (this.children.length < 2) {
            return Marker.ANY_NON_NULL_MARKER + getLeftOperand().toStringAST();
        }
        return super.toStringAST();
    }

    @Override // org.springframework.expression.spel.ast.Operator
    public SpelNodeImpl getRightOperand() {
        if (this.children.length < 2) {
            throw new IllegalStateException("No right operand");
        }
        return this.children[1];
    }

    private static String convertTypedValueToString(TypedValue value, ExpressionState state) {
        TypeConverter typeConverter = state.getEvaluationContext().getTypeConverter();
        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(String.class);
        if (typeConverter.canConvert(value.getTypeDescriptor(), typeDescriptor)) {
            return String.valueOf(typeConverter.convertValue(value.getValue(), value.getTypeDescriptor(), typeDescriptor));
        }
        return String.valueOf(value.getValue());
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        if (getLeftOperand().isCompilable()) {
            return (this.children.length <= 1 || getRightOperand().isCompilable()) && this.exitTypeDescriptor != null;
        }
        return false;
    }

    private void walk(MethodVisitor mv, CodeFlow cf, @Nullable SpelNodeImpl operand) {
        if (operand instanceof OpPlus) {
            OpPlus plus = (OpPlus) operand;
            walk(mv, cf, plus.getLeftOperand());
            walk(mv, cf, plus.getRightOperand());
        } else if (operand != null) {
            cf.enterCompilationScope();
            operand.generateCode(mv, cf);
            if (!"Ljava/lang/String".equals(cf.lastDescriptor())) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            }
            cf.exitCompilationScope();
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        }
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        if ("Ljava/lang/String".equals(this.exitTypeDescriptor)) {
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(89);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", Constants.CONSTRUCTOR_NAME, "()V", false);
            walk(mv, cf, getLeftOperand());
            walk(mv, cf, getRightOperand());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        } else {
            this.children[0].generateCode(mv, cf);
            String leftDesc = this.children[0].exitTypeDescriptor;
            String exitDesc = this.exitTypeDescriptor;
            Assert.state(exitDesc != null, "No exit type descriptor");
            char targetDesc = exitDesc.charAt(0);
            CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, targetDesc);
            if (this.children.length > 1) {
                cf.enterCompilationScope();
                this.children[1].generateCode(mv, cf);
                String rightDesc = this.children[1].exitTypeDescriptor;
                cf.exitCompilationScope();
                CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, targetDesc);
                switch (targetDesc) {
                    case 'D':
                        mv.visitInsn(99);
                        break;
                    case org.springframework.asm.TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                    case org.springframework.asm.TypeReference.CAST /* 71 */:
                    case 'H':
                    default:
                        throw new IllegalStateException("Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
                    case 'F':
                        mv.visitInsn(98);
                        break;
                    case 'I':
                        mv.visitInsn(96);
                        break;
                    case 'J':
                        mv.visitInsn(97);
                        break;
                }
            }
        }
        cf.pushDescriptor(this.exitTypeDescriptor);
    }
}