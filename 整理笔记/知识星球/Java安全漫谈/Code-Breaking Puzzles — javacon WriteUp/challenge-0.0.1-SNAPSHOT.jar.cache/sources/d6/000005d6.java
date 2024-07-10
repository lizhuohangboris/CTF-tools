package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELClass.class */
public class ELClass {
    private final Class<?> clazz;

    public ELClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getKlass() {
        return this.clazz;
    }
}