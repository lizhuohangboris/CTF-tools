package org.springframework.expression.spel.ast;

import org.springframework.asm.MethodVisitor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.CodeFlow;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.ast.Operator;
import org.springframework.expression.spel.support.BooleanTypedValue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/OpEQ.class */
public class OpEQ extends Operator {
    public OpEQ(int pos, SpelNodeImpl... operands) {
        super("==", pos, operands);
        this.exitTypeDescriptor = "Z";
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
        Object left = getLeftOperand().getValueInternal(state).getValue();
        Object right = getRightOperand().getValueInternal(state).getValue();
        this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(left);
        this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(right);
        return BooleanTypedValue.forValue(equalityCheck(state.getEvaluationContext(), left, right));
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public boolean isCompilable() {
        SpelNodeImpl left = getLeftOperand();
        SpelNodeImpl right = getRightOperand();
        if (!left.isCompilable() || !right.isCompilable()) {
            return false;
        }
        String leftDesc = left.exitTypeDescriptor;
        String rightDesc = right.exitTypeDescriptor;
        Operator.DescriptorComparison dc = Operator.DescriptorComparison.checkNumericCompatibility(leftDesc, rightDesc, this.leftActualDescriptor, this.rightActualDescriptor);
        return !dc.areNumbers || dc.areCompatible;
    }

    @Override // org.springframework.expression.spel.ast.SpelNodeImpl
    public void generateCode(MethodVisitor mv, CodeFlow cf) {
        cf.loadEvaluationContext(mv);
        String leftDesc = getLeftOperand().exitTypeDescriptor;
        String rightDesc = getRightOperand().exitTypeDescriptor;
        boolean leftPrim = CodeFlow.isPrimitive(leftDesc);
        boolean rightPrim = CodeFlow.isPrimitive(rightDesc);
        cf.enterCompilationScope();
        getLeftOperand().generateCode(mv, cf);
        cf.exitCompilationScope();
        if (leftPrim) {
            CodeFlow.insertBoxIfNecessary(mv, leftDesc.charAt(0));
        }
        cf.enterCompilationScope();
        getRightOperand().generateCode(mv, cf);
        cf.exitCompilationScope();
        if (rightPrim) {
            CodeFlow.insertBoxIfNecessary(mv, rightDesc.charAt(0));
        }
        String operatorClassName = Operator.class.getName().replace('.', '/');
        String evaluationContextClassName = EvaluationContext.class.getName().replace('.', '/');
        mv.visitMethodInsn(184, operatorClassName, "equalityCheck", "(L" + evaluationContextClassName + ";Ljava/lang/Object;Ljava/lang/Object;)Z", false);
        cf.pushDescriptor("Z");
    }
}