package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstAssign.class */
public class AstAssign extends SimpleNode {
    public AstAssign(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return value;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Object value = this.children[1].getValue(ctx);
        this.children[0].setValue(ctx, value);
        return this.children[1].getType(ctx);
    }
}