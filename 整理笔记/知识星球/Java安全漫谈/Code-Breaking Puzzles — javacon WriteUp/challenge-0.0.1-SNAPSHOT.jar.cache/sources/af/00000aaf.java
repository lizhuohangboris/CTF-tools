package org.apache.el.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstNegative.class */
public final class AstNegative extends SimpleNode {
    public AstNegative(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Number.class;
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object obj = this.children[0].getValue(ctx);
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).negate();
        }
        if (obj instanceof BigInteger) {
            return ((BigInteger) obj).negate();
        }
        if (obj instanceof String) {
            if (isStringFloat((String) obj)) {
                return Double.valueOf(-Double.parseDouble((String) obj));
            }
            return Long.valueOf(-Long.parseLong((String) obj));
        } else if (obj instanceof Long) {
            return Long.valueOf(-((Long) obj).longValue());
        } else {
            if (obj instanceof Double) {
                return Double.valueOf(-((Double) obj).doubleValue());
            }
            if (obj instanceof Integer) {
                return Integer.valueOf(-((Integer) obj).intValue());
            }
            if (obj instanceof Float) {
                return Float.valueOf(-((Float) obj).floatValue());
            }
            if (obj instanceof Short) {
                return Short.valueOf((short) (-((Short) obj).shortValue()));
            }
            if (obj instanceof Byte) {
                return Byte.valueOf((byte) (-((Byte) obj).byteValue()));
            }
            Long num = (Long) coerceToNumber(ctx, obj, Long.class);
            return Long.valueOf(-num.longValue());
        }
    }
}