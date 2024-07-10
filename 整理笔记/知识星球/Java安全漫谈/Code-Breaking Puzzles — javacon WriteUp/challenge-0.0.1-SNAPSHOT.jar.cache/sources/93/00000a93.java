package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstConcatenation.class */
public class AstConcatenation extends SimpleNode {
    public AstConcatenation(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        String s1 = coerceToString(ctx, this.children[0].getValue(ctx));
        String s2 = coerceToString(ctx, this.children[1].getValue(ctx));
        return s1 + s2;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return String.class;
    }
}