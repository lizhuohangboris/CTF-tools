package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/MethodInfo.class */
public class MethodInfo {
    private final String name;
    private final Class<?>[] paramTypes;
    private final Class<?> returnType;

    public MethodInfo(String name, Class<?> returnType, Class<?>[] paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getReturnType() {
        return this.returnType;
    }

    public Class<?>[] getParamTypes() {
        return this.paramTypes;
    }
}