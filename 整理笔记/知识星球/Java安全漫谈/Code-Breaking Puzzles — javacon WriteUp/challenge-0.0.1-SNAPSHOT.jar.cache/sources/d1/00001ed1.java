package org.springframework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/StandardClassMetadata.class */
public class StandardClassMetadata implements ClassMetadata {
    private final Class<?> introspectedClass;

    public StandardClassMetadata(Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String getClassName() {
        return this.introspectedClass.getName();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAnnotation() {
        return this.introspectedClass.isAnnotation();
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isConcrete() {
        return (isInterface() || isAbstract()) ? false : true;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean isIndependent() {
        return !hasEnclosingClass() || (this.introspectedClass.getDeclaringClass() != null && Modifier.isStatic(this.introspectedClass.getModifiers()));
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean hasEnclosingClass() {
        return this.introspectedClass.getEnclosingClass() != null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getEnclosingClassName() {
        Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass.getName();
        }
        return null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public boolean hasSuperClass() {
        return this.introspectedClass.getSuperclass() != null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    @Nullable
    public String getSuperClassName() {
        Class<?> superClass = this.introspectedClass.getSuperclass();
        if (superClass != null) {
            return superClass.getName();
        }
        return null;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getInterfaceNames() {
        Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; i++) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }

    @Override // org.springframework.core.type.ClassMetadata
    public String[] getMemberClassNames() {
        Class<?>[] declaredClasses;
        LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
        for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
            memberClassNames.add(nestedClass.getName());
        }
        return StringUtils.toStringArray(memberClassNames);
    }
}