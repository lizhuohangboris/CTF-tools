package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/VariableMapper.class */
public abstract class VariableMapper {
    public abstract ValueExpression resolveVariable(String str);

    public abstract ValueExpression setVariable(String str, ValueExpression valueExpression);
}