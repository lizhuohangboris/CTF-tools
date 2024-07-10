package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantFloat.class */
public final class ConstantFloat extends Constant {
    private final float bytes;

    public ConstantFloat(DataInput file) throws IOException {
        super((byte) 4);
        this.bytes = file.readFloat();
    }

    public final float getBytes() {
        return this.bytes;
    }
}