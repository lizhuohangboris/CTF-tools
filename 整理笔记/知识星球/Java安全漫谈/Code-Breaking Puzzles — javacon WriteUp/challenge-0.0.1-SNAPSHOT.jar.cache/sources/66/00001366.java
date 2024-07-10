package org.springframework.asm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/ModuleWriter.class */
public final class ModuleWriter extends ModuleVisitor {
    private final SymbolTable symbolTable;
    private final int moduleNameIndex;
    private final int moduleFlags;
    private final int moduleVersionIndex;
    private int requiresCount;
    private final ByteVector requires;
    private int exportsCount;
    private final ByteVector exports;
    private int opensCount;
    private final ByteVector opens;
    private int usesCount;
    private final ByteVector usesIndex;
    private int providesCount;
    private final ByteVector provides;
    private int packageCount;
    private final ByteVector packageIndex;
    private int mainClassIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModuleWriter(SymbolTable symbolTable, int name, int access, int version) {
        super(458752);
        this.symbolTable = symbolTable;
        this.moduleNameIndex = name;
        this.moduleFlags = access;
        this.moduleVersionIndex = version;
        this.requires = new ByteVector();
        this.exports = new ByteVector();
        this.opens = new ByteVector();
        this.usesIndex = new ByteVector();
        this.provides = new ByteVector();
        this.packageIndex = new ByteVector();
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitMainClass(String mainClass) {
        this.mainClassIndex = this.symbolTable.addConstantClass(mainClass).index;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitPackage(String packaze) {
        this.packageIndex.putShort(this.symbolTable.addConstantPackage(packaze).index);
        this.packageCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitRequire(String module, int access, String version) {
        this.requires.putShort(this.symbolTable.addConstantModule(module).index).putShort(access).putShort(version == null ? 0 : this.symbolTable.addConstantUtf8(version));
        this.requiresCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitExport(String packaze, int access, String... modules) {
        this.exports.putShort(this.symbolTable.addConstantPackage(packaze).index).putShort(access);
        if (modules == null) {
            this.exports.putShort(0);
        } else {
            this.exports.putShort(modules.length);
            for (String module : modules) {
                this.exports.putShort(this.symbolTable.addConstantModule(module).index);
            }
        }
        this.exportsCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitOpen(String packaze, int access, String... modules) {
        this.opens.putShort(this.symbolTable.addConstantPackage(packaze).index).putShort(access);
        if (modules == null) {
            this.opens.putShort(0);
        } else {
            this.opens.putShort(modules.length);
            for (String module : modules) {
                this.opens.putShort(this.symbolTable.addConstantModule(module).index);
            }
        }
        this.opensCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitUse(String service) {
        this.usesIndex.putShort(this.symbolTable.addConstantClass(service).index);
        this.usesCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitProvide(String service, String... providers) {
        this.provides.putShort(this.symbolTable.addConstantClass(service).index);
        this.provides.putShort(providers.length);
        for (String provider : providers) {
            this.provides.putShort(this.symbolTable.addConstantClass(provider).index);
        }
        this.providesCount++;
    }

    @Override // org.springframework.asm.ModuleVisitor
    public void visitEnd() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAttributeCount() {
        return 1 + (this.packageCount > 0 ? 1 : 0) + (this.mainClassIndex > 0 ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int computeAttributesSize() {
        this.symbolTable.addConstantUtf8("Module");
        int size = 22 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
        if (this.packageCount > 0) {
            this.symbolTable.addConstantUtf8("ModulePackages");
            size += 8 + this.packageIndex.length;
        }
        if (this.mainClassIndex > 0) {
            this.symbolTable.addConstantUtf8("ModuleMainClass");
            size += 8;
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putAttributes(ByteVector output) {
        int moduleAttributeLength = 16 + this.requires.length + this.exports.length + this.opens.length + this.usesIndex.length + this.provides.length;
        output.putShort(this.symbolTable.addConstantUtf8("Module")).putInt(moduleAttributeLength).putShort(this.moduleNameIndex).putShort(this.moduleFlags).putShort(this.moduleVersionIndex).putShort(this.requiresCount).putByteArray(this.requires.data, 0, this.requires.length).putShort(this.exportsCount).putByteArray(this.exports.data, 0, this.exports.length).putShort(this.opensCount).putByteArray(this.opens.data, 0, this.opens.length).putShort(this.usesCount).putByteArray(this.usesIndex.data, 0, this.usesIndex.length).putShort(this.providesCount).putByteArray(this.provides.data, 0, this.provides.length);
        if (this.packageCount > 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ModulePackages")).putInt(2 + this.packageIndex.length).putShort(this.packageCount).putByteArray(this.packageIndex.data, 0, this.packageIndex.length);
        }
        if (this.mainClassIndex > 0) {
            output.putShort(this.symbolTable.addConstantUtf8("ModuleMainClass")).putInt(2).putShort(this.mainClassIndex);
        }
    }
}