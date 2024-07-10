package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantInteger.class */
public final class ConstantInteger extends Constant {
    private final int bytes;

    public ConstantInteger(DataInput file) throws IOException {
        super((byte) 3);
        this.bytes = file.readInt();
    }

    public final int getBytes() {
        return this.bytes;
    }
}