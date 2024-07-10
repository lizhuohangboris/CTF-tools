package org.apache.el.parser;

import java.util.HashMap;
import java.util.Map;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstMapData.class */
public class AstMapData extends SimpleNode {
    public AstMapData(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Node[] nodeArr;
        Map<Object, Object> result = new HashMap<>();
        if (this.children != null) {
            for (Node child : this.children) {
                AstMapEntry mapEntry = (AstMapEntry) child;
                Object key = mapEntry.children[0].getValue(ctx);
                Object value = mapEntry.children[1].getValue(ctx);
                result.put(key, value);
            }
        }
        return result;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Map.class;
    }
}