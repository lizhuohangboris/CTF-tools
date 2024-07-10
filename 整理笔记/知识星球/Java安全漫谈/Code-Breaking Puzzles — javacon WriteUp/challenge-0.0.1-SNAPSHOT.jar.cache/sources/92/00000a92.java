package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstCompositeExpression.class */
public final class AstCompositeExpression extends SimpleNode {
    public AstCompositeExpression(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        StringBuilder sb = new StringBuilder(16);
        if (this.children != null) {
            for (int i = 0; i < this.children.length; i++) {
                Object obj = this.children[i].getValue(ctx);
                if (obj != null) {
                    sb.append(ELSupport.coerceToString(ctx, obj));
                }
            }
        }
        return sb.toString();
    }
}