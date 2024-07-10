package org.apache.el.parser;

import java.util.ArrayList;
import java.util.List;
import javax.el.ELException;
import javax.el.LambdaExpression;
import org.apache.el.ValueExpressionImpl;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstLambdaExpression.class */
public class AstLambdaExpression extends SimpleNode {
    private NestedState nestedState;

    public AstLambdaExpression(int id) {
        super(id);
        this.nestedState = null;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object result;
        NestedState state = getNestedState();
        int methodParameterSetCount = jjtGetNumChildren() - 2;
        if (methodParameterSetCount > state.getNestingCount()) {
            throw new ELException(MessageFactory.get("error.lambda.tooManyMethodParameterSets"));
        }
        AstLambdaParameters formalParametersNode = (AstLambdaParameters) this.children[0];
        Node[] formalParamNodes = formalParametersNode.children;
        ValueExpressionImpl ve = new ValueExpressionImpl("", this.children[1], ctx.getFunctionMapper(), ctx.getVariableMapper(), null);
        List<String> formalParameters = new ArrayList<>();
        if (formalParamNodes != null) {
            for (Node formalParamNode : formalParamNodes) {
                formalParameters.add(formalParamNode.getImage());
            }
        }
        LambdaExpression le = new LambdaExpression(formalParameters, ve);
        le.setELContext(ctx);
        if (jjtGetNumChildren() != 2) {
            int methodParameterIndex = 2;
            Object invoke = le.invoke(((AstMethodParameters) this.children[2]).getParameters(ctx));
            while (true) {
                result = invoke;
                methodParameterIndex++;
                if (!(result instanceof LambdaExpression) || methodParameterIndex >= jjtGetNumChildren()) {
                    break;
                }
                invoke = ((LambdaExpression) result).invoke(((AstMethodParameters) this.children[methodParameterIndex]).getParameters(ctx));
            }
            return result;
        } else if (state.getHasFormalParameters()) {
            return le;
        } else {
            return le.invoke(ctx, null);
        }
    }

    private NestedState getNestedState() {
        if (this.nestedState == null) {
            setNestedState(new NestedState());
        }
        return this.nestedState;
    }

    private void setNestedState(NestedState nestedState) {
        if (this.nestedState != null) {
            throw new IllegalStateException("nestedState may only be set once");
        }
        this.nestedState = nestedState;
        nestedState.incrementNestingCount();
        if (jjtGetNumChildren() > 1) {
            Node firstChild = jjtGetChild(0);
            if (firstChild instanceof AstLambdaParameters) {
                if (firstChild.jjtGetNumChildren() > 0) {
                    nestedState.setHasFormalParameters();
                }
                Node secondChild = jjtGetChild(1);
                if (secondChild instanceof AstLambdaExpression) {
                    ((AstLambdaExpression) secondChild).setNestedState(nestedState);
                }
            }
        }
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        Node[] nodeArr;
        StringBuilder result = new StringBuilder();
        for (Node n : this.children) {
            result.append(n.toString());
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstLambdaExpression$NestedState.class */
    public static class NestedState {
        private int nestingCount;
        private boolean hasFormalParameters;

        private NestedState() {
            this.nestingCount = 0;
            this.hasFormalParameters = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void incrementNestingCount() {
            this.nestingCount++;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getNestingCount() {
            return this.nestingCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setHasFormalParameters() {
            this.hasFormalParameters = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean getHasFormalParameters() {
            return this.hasFormalParameters;
        }
    }
}