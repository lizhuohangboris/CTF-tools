package org.apache.el.parser;

import java.util.ArrayList;
import java.util.List;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstMethodParameters.class */
public final class AstMethodParameters extends SimpleNode {
    public AstMethodParameters(int id) {
        super(id);
    }

    public Object[] getParameters(EvaluationContext ctx) {
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            params.add(jjtGetChild(i).getValue(ctx));
        }
        return params.toArray(new Object[params.size()]);
    }

    @Override // org.apache.el.parser.SimpleNode
    public String toString() {
        Node[] nodeArr;
        StringBuilder result = new StringBuilder();
        result.append('(');
        if (this.children != null) {
            for (Node n : this.children) {
                result.append(n.toString());
                result.append(',');
            }
        }
        result.append(')');
        return result.toString();
    }
}