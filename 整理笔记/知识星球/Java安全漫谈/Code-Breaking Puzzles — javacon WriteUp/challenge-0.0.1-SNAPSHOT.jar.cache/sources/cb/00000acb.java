package org.apache.el.stream;

import javax.el.ELException;
import javax.el.LambdaExpression;
import org.apache.el.util.MessageFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/stream/Optional.class */
public class Optional {
    private final Object obj;
    static final Optional EMPTY = new Optional(null);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Optional(Object obj) {
        this.obj = obj;
    }

    public Object get() throws ELException {
        if (this.obj == null) {
            throw new ELException(MessageFactory.get("stream.optional.empty"));
        }
        return this.obj;
    }

    public void ifPresent(LambdaExpression le) {
        if (this.obj != null) {
            le.invoke(this.obj);
        }
    }

    public Object orElse(Object other) {
        if (this.obj == null) {
            return other;
        }
        return this.obj;
    }

    public Object orElseGet(Object le) {
        if (this.obj == null) {
            if (le instanceof LambdaExpression) {
                return ((LambdaExpression) le).invoke(null);
            }
            return le;
        }
        return this.obj;
    }
}