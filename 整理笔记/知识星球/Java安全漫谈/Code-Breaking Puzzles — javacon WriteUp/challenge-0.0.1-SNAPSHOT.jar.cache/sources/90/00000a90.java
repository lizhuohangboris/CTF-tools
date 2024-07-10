package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstBracketSuffix.class */
public final class AstBracketSuffix extends SimpleNode {
    public AstBracketSuffix(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.children[0].getValue(ctx);
    }
}