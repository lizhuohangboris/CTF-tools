package org.springframework.asm;

import org.springframework.asm.Attribute;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/FieldWriter.class */
public final class FieldWriter extends FieldVisitor {
    private final SymbolTable symbolTable;
    private final int accessFlags;
    private final int nameIndex;
    private final int descriptorIndex;
    private int signatureIndex;
    private int constantValueIndex;
    private AnnotationWriter lastRuntimeVisibleAnnotation;
    private AnnotationWriter lastRuntimeInvisibleAnnotation;
    private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
    private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
    private Attribute firstAttribute;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldWriter(SymbolTable symbolTable, int access, String name, String descriptor, String signature, Object constantValue) {
        super(458752);
        this.symbolTable = symbolTable;
        this.accessFlags = access;
        this.nameIndex = symbolTable.addConstantUtf8(name);
        this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
        if (signature != null) {
            this.signatureIndex = symbolTable.addConstantUtf8(signature);
        }
        if (constantValue != null) {
            this.constantValueIndex = symbolTable.addConstant(constantValue).index;
        }
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        ByteVector annotation = new ByteVector();
        annotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        if (visible) {
            AnnotationWriter annotationWriter = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeVisibleAnnotation);
            this.lastRuntimeVisibleAnnotation = annotationWriter;
            return annotationWriter;
        }
        AnnotationWriter annotationWriter2 = new AnnotationWriter(this.symbolTable, annotation, this.lastRuntimeInvisibleAnnotation);
        this.lastRuntimeInvisibleAnnotation = annotationWriter2;
        return annotationWriter2;
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        ByteVector typeAnnotation = new ByteVector();
        TypeReference.putTarget(typeRef, typeAnnotation);
        TypePath.put(typePath, typeAnnotation);
        typeAnnotation.putShort(this.symbolTable.addConstantUtf8(descriptor)).putShort(0);
        if (visible) {
            AnnotationWriter annotationWriter = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeVisibleTypeAnnotation);
            this.lastRuntimeVisibleTypeAnnotation = annotationWriter;
            return annotationWriter;
        }
        AnnotationWriter annotationWriter2 = new AnnotationWriter(this.symbolTable, typeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);
        this.lastRuntimeInvisibleTypeAnnotation = annotationWriter2;
        return annotationWriter2;
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitAttribute(Attribute attribute) {
        attribute.nextAttribute = this.firstAttribute;
        this.firstAttribute = attribute;
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitEnd() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeFieldInfoSize() {
        int size = 8;
        if (this.constantValueIndex != 0) {
            this.symbolTable.addConstantUtf8("ConstantValue");
            size = 8 + 8;
        }
        if ((this.accessFlags & 4096) != 0 && this.symbolTable.getMajorVersion() < 49) {
            this.symbolTable.addConstantUtf8("Synthetic");
            size += 6;
        }
        if (this.signatureIndex != 0) {
            this.symbolTable.addConstantUtf8("Signature");
            size += 8;
        }
        if ((this.accessFlags & 131072) != 0) {
            this.symbolTable.addConstantUtf8("Deprecated");
            size += 6;
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            size += this.lastRuntimeVisibleAnnotation.computeAnnotationsSize("RuntimeVisibleAnnotations");
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            size += this.lastRuntimeInvisibleAnnotation.computeAnnotationsSize("RuntimeInvisibleAnnotations");
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            size += this.lastRuntimeVisibleTypeAnnotation.computeAnnotationsSize("RuntimeVisibleTypeAnnotations");
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            size += this.lastRuntimeInvisibleTypeAnnotation.computeAnnotationsSize("RuntimeInvisibleTypeAnnotations");
        }
        if (this.firstAttribute != null) {
            size += this.firstAttribute.computeAttributesSize(this.symbolTable);
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putFieldInfo(ByteVector output) {
        boolean useSyntheticAttribute = this.symbolTable.getMajorVersion() < 49;
        int mask = useSyntheticAttribute ? 4096 : 0;
        output.putShort(this.accessFlags & (mask ^ (-1))).putShort(this.nameIndex).putShort(this.descriptorIndex);
        int attributesCount = 0;
        if (this.constantValueIndex != 0) {
            attributesCount = 0 + 1;
        }
        if ((this.accessFlags & 4096) != 0 && useSyntheticAttribute) {
            attributesCount++;
        }
        if (this.signatureIndex != 0) {
            attributesCount++;
        }
        if ((this.accessFlags & 131072) != 0) {
            attributesCount++;
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            attributesCount++;
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            attributesCount++;
        }
        if (this.firstAttribute != null) {
            attributesCount += this.firstAttribute.getAttributeCount();
        }
        output.putShort(attributesCount);
        if (this.constantValueIndex != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ConstantValue")).putInt(2).putShort(this.constantValueIndex);
        }
        if ((this.accessFlags & 4096) != 0 && useSyntheticAttribute) {
            output.putShort(this.symbolTable.addConstantUtf8("Synthetic")).putInt(0);
        }
        if (this.signatureIndex != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("Signature")).putInt(2).putShort(this.signatureIndex);
        }
        if ((this.accessFlags & 131072) != 0) {
            output.putShort(this.symbolTable.addConstantUtf8("Deprecated")).putInt(0);
        }
        if (this.lastRuntimeVisibleAnnotation != null) {
            this.lastRuntimeVisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleAnnotations"), output);
        }
        if (this.lastRuntimeInvisibleAnnotation != null) {
            this.lastRuntimeInvisibleAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleAnnotations"), output);
        }
        if (this.lastRuntimeVisibleTypeAnnotation != null) {
            this.lastRuntimeVisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeVisibleTypeAnnotations"), output);
        }
        if (this.lastRuntimeInvisibleTypeAnnotation != null) {
            this.lastRuntimeInvisibleTypeAnnotation.putAnnotations(this.symbolTable.addConstantUtf8("RuntimeInvisibleTypeAnnotations"), output);
        }
        if (this.firstAttribute != null) {
            this.firstAttribute.putAttributes(this.symbolTable, output);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
        attributePrototypes.addAttributes(this.firstAttribute);
    }
}