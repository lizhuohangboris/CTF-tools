package org.apache.el.parser;

import java.math.BigDecimal;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstFloatingPoint.class */
public final class AstFloatingPoint extends SimpleNode {
    private volatile Number number;

    public AstFloatingPoint(int id) {
        super(id);
    }

    public Number getFloatingPoint() {
        if (this.number == null) {
            try {
                this.number = Double.valueOf(this.image);
            } catch (ArithmeticException e) {
                this.number = new BigDecimal(this.image);
            }
        }
        return this.number;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getFloatingPoint();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return getFloatingPoint().getClass();
    }
}