package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantClass.class */
public final class ConstantClass extends Constant {
    private final int name_index;

    public ConstantClass(DataInput dataInput) throws IOException {
        super((byte) 7);
        this.name_index = dataInput.readUnsignedShort();
    }

    public final int getNameIndex() {
        return this.name_index;
    }
}