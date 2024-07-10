package org.apache.tomcat.util.bcel.classfile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/JavaClass.class */
public class JavaClass {
    private final int access_flags;
    private final String class_name;
    private final String superclass_name;
    private final String[] interface_names;
    private final Annotations runtimeVisibleAnnotations;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaClass(String class_name, String superclass_name, int access_flags, ConstantPool constant_pool, String[] interface_names, Annotations runtimeVisibleAnnotations) {
        this.access_flags = access_flags;
        this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
        this.class_name = class_name;
        this.superclass_name = superclass_name;
        this.interface_names = interface_names;
    }

    public final int getAccessFlags() {
        return this.access_flags;
    }

    public AnnotationEntry[] getAnnotationEntries() {
        if (this.runtimeVisibleAnnotations != null) {
            return this.runtimeVisibleAnnotations.getAnnotationEntries();
        }
        return null;
    }

    public String getClassName() {
        return this.class_name;
    }

    public String[] getInterfaceNames() {
        return this.interface_names;
    }

    public String getSuperclassName() {
        return this.superclass_name;
    }
}