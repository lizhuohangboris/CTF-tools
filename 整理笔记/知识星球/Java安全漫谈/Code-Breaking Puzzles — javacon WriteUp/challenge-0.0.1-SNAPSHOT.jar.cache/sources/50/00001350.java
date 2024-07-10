package org.springframework.asm;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/Attribute.class */
public class Attribute {
    public final String type;
    private byte[] content;
    Attribute nextAttribute;

    /* JADX INFO: Access modifiers changed from: protected */
    public Attribute(String type) {
        this.type = type;
    }

    public boolean isUnknown() {
        return true;
    }

    public boolean isCodeAttribute() {
        return false;
    }

    protected Label[] getLabels() {
        return new Label[0];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Attribute read(ClassReader classReader, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
        Attribute attribute = new Attribute(this.type);
        attribute.content = new byte[length];
        System.arraycopy(classReader.b, offset, attribute.content, 0, length);
        return attribute;
    }

    protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
        return new ByteVector(this.content);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getAttributeCount() {
        int count = 0;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                count++;
                attribute = attribute2.nextAttribute;
            } else {
                return count;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int computeAttributesSize(SymbolTable symbolTable) {
        return computeAttributesSize(symbolTable, null, 0, -1, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int computeAttributesSize(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals) {
        ClassWriter classWriter = symbolTable.classWriter;
        int size = 0;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                symbolTable.addConstantUtf8(attribute2.type);
                size += 6 + attribute2.write(classWriter, code, codeLength, maxStack, maxLocals).length;
                attribute = attribute2.nextAttribute;
            } else {
                return size;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void putAttributes(SymbolTable symbolTable, ByteVector output) {
        putAttributes(symbolTable, null, 0, -1, -1, output);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void putAttributes(SymbolTable symbolTable, byte[] code, int codeLength, int maxStack, int maxLocals, ByteVector output) {
        ClassWriter classWriter = symbolTable.classWriter;
        Attribute attribute = this;
        while (true) {
            Attribute attribute2 = attribute;
            if (attribute2 != null) {
                ByteVector attributeContent = attribute2.write(classWriter, code, codeLength, maxStack, maxLocals);
                output.putShort(symbolTable.addConstantUtf8(attribute2.type)).putInt(attributeContent.length);
                output.putByteArray(attributeContent.data, 0, attributeContent.length);
                attribute = attribute2.nextAttribute;
            } else {
                return;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/Attribute$Set.class */
    static final class Set {
        private static final int SIZE_INCREMENT = 6;
        private int size;
        private Attribute[] data = new Attribute[6];

        /* JADX INFO: Access modifiers changed from: package-private */
        public void addAttributes(Attribute attributeList) {
            Attribute attribute = attributeList;
            while (true) {
                Attribute attribute2 = attribute;
                if (attribute2 != null) {
                    if (!contains(attribute2)) {
                        add(attribute2);
                    }
                    attribute = attribute2.nextAttribute;
                } else {
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Attribute[] toArray() {
            Attribute[] result = new Attribute[this.size];
            System.arraycopy(this.data, 0, result, 0, this.size);
            return result;
        }

        private boolean contains(Attribute attribute) {
            for (int i = 0; i < this.size; i++) {
                if (this.data[i].type.equals(attribute.type)) {
                    return true;
                }
            }
            return false;
        }

        private void add(Attribute attribute) {
            if (this.size >= this.data.length) {
                Attribute[] newData = new Attribute[this.data.length + 6];
                System.arraycopy(this.data, 0, newData, 0, this.size);
                this.data = newData;
            }
            Attribute[] attributeArr = this.data;
            int i = this.size;
            this.size = i + 1;
            attributeArr[i] = attribute;
        }
    }
}