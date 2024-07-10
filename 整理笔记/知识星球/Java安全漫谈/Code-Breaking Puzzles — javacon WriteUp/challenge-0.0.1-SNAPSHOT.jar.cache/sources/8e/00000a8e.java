package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstAnd.class */
public final class AstAnd extends BooleanNode {
    public AstAnd(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        Boolean b = coerceToBoolean(ctx, obj, true);
        if (!b.booleanValue()) {
            return b;
        }
        Object obj2 = this.children[1].getValue(ctx);
        return coerceToBoolean(ctx, obj2, true);
    }
}