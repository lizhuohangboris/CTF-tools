package org.apache.el.parser;

import java.math.BigInteger;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstInteger.class */
public final class AstInteger extends SimpleNode {
    private volatile Number number;

    public AstInteger(int id) {
        super(id);
    }

    protected Number getInteger() {
        if (this.number == null) {
            try {
                this.number = Long.valueOf(this.image);
            } catch (ArithmeticException e) {
                this.number = new BigInteger(this.image);
            }
        }
        return this.number;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return getInteger().getClass();
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return getInteger();
    }
}