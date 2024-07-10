package javax.el;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/Expression.class */
public abstract class Expression implements Serializable {
    private static final long serialVersionUID = -6663767980471823812L;

    public abstract String getExpressionString();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract boolean isLiteralText();
}