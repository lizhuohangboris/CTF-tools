package javax.el;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/FunctionMapper.class */
public abstract class FunctionMapper {
    public abstract Method resolveFunction(String str, String str2);

    public void mapFunction(String prefix, String localName, Method method) {
    }
}