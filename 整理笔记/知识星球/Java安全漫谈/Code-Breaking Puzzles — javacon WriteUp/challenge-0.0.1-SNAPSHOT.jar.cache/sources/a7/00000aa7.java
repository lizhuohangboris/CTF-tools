package org.apache.el.parser;

import java.util.ArrayList;
import java.util.List;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstListData.class */
public class AstListData extends SimpleNode {
    public AstListData(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Node[] nodeArr;
        List<Object> result = new ArrayList<>();
        if (this.children != null) {
            for (Node child : this.children) {
                result.add(child.getValue(ctx));
            }
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return List.class;
    }
}