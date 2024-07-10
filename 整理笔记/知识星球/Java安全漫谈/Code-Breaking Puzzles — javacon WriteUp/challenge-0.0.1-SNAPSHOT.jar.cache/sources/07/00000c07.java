package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantDouble.class */
public final class ConstantDouble extends Constant {
    private final double bytes;

    public ConstantDouble(DataInput file) throws IOException {
        super((byte) 6);
        this.bytes = file.readDouble();
    }

    public final double getBytes() {
        return this.bytes;
    }
}