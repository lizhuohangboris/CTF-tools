package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/MethodExpression.class */
public abstract class MethodExpression extends Expression {
    private static final long serialVersionUID = 8163925562047324656L;

    public abstract MethodInfo getMethodInfo(ELContext eLContext);

    public abstract Object invoke(ELContext eLContext, Object[] objArr);

    public boolean isParametersProvided() {
        return false;
    }

    @Deprecated
    public boolean isParmetersProvided() {
        return false;
    }
}