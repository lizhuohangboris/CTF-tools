package org.springframework.core.type.classreading;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/ClassMetadataReadingVisitor.class */
class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata {
    private String className;
    private boolean isInterface;
    private boolean isAnnotation;
    private boolean isAbstract;
    private boolean isFinal;
    @Nullable
    private String enclosingClassName;
    private boolean independentInnerClass;
    @Nullable
    private String superClassName;
    private String[] interfaces;
    private Set<String> memberClassNames;

    public ClassMetadataReadingVisitor() {
        super(458752);
        this.className = "";
        this.interfaces = new String[0];
        this.memberClassNames = new LinkedHashSet(4);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, @Nullable String supername, String[] interfaces) {
        this.className = ClassUtils.convertResourcePathToClassName(name);
        this.isInterface = (access & 512) != 0;
        this.isAnnotation = (access & 8192) != 0;
        this.isAbstract = (access & 1024) != 0;
        this.isFinal = (access & 16) != 0;
        if (supername != null && !this.isInterface) {
            this.superClassName = ClassUtils.convertResourcePathToClassName(supername);
        }
        this.interfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitOuterClass(String owner, String name, String desc) {
        this.enclosingClassName = ClassUtils.convertResourcePathToClassName(owner);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitInnerClass(String name, @Nullable String outerName, String innerName, int access) {
        if (outerName != null) {
            String fqName = ClassUtils.convertResourcePathToClassName(name);
            String fqOuterName = ClassUtils.convertResourcePathToClassName(outerName);
            if (this.className.equals(fqName)) {
                this.enclosingClassName = fqOuterName;
                this.independentInnerClass = (access & 8) != 0;
            } else if (this.className.equals(fqOuterName)) {
                this.memberClassNames.add(fqName);
            }
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitSource(String source, String debug) {
    }

    @Override // org.springframework.asm.ClassVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new EmptyAnnotationVisitor();
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitAttribute(Attribute attr) {
    }

    @Override // org.springframework.asm.ClassVisitor
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new EmptyFieldVisitor();
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new EmptyMethodVisitor();
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
    }

    public String getClassName() {
        return this.className;
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    public boolean isAnnotation() {
        return this.isAnnotation;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isConcrete() {
        return (this.isInterface || this.isAbstract) ? false : true;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public boolean isIndependent() {
        return this.enclosingClassName == null || this.independentInnerClass;
    }

    public boolean hasEnclosingClass() {
        return this.enclosingClassName != null;
    }

    @Nullable
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    public boolean hasSuperClass() {
        return this.superClassName != null;
    }

    @Nullable
    public String getSuperClassName() {
        return this.superClassName;
    }

    public String[] getInterfaceNames() {
        return this.interfaces;
    }

    public String[] getMemberClassNames() {
        return StringUtils.toStringArray(this.memberClassNames);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/ClassMetadataReadingVisitor$EmptyAnnotationVisitor.class */
    private static class EmptyAnnotationVisitor extends AnnotationVisitor {
        public EmptyAnnotationVisitor() {
            super(458752);
        }

        @Override // org.springframework.asm.AnnotationVisitor
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            return this;
        }

        @Override // org.springframework.asm.AnnotationVisitor
        public AnnotationVisitor visitArray(String name) {
            return this;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/ClassMetadataReadingVisitor$EmptyMethodVisitor.class */
    private static class EmptyMethodVisitor extends MethodVisitor {
        public EmptyMethodVisitor() {
            super(458752);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/ClassMetadataReadingVisitor$EmptyFieldVisitor.class */
    private static class EmptyFieldVisitor extends FieldVisitor {
        public EmptyFieldVisitor() {
            super(458752);
        }
    }
}