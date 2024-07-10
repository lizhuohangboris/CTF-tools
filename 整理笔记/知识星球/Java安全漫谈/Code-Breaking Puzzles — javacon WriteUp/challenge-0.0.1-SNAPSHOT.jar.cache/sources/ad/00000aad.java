package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.ELArithmetic;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstMod.class */
public final class AstMod extends ArithmeticNode {
    public AstMod(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Object obj1 = this.children[1].getValue(ctx);
        return ELArithmetic.mod(obj0, obj1);
    }
}