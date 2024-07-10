package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstLessThanEqual.class */
public final class AstLessThanEqual extends BooleanNode {
    public AstLessThanEqual(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj0 = this.children[0].getValue(ctx);
        Object obj1 = this.children[1].getValue(ctx);
        if (obj0 == obj1) {
            return Boolean.TRUE;
        }
        if (obj0 == null || obj1 == null) {
            return Boolean.FALSE;
        }
        return compare(ctx, obj0, obj1) <= 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}