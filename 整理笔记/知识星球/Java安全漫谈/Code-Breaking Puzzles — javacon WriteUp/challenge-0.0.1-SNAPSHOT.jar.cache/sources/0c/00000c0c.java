package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantUtf8.class */
public final class ConstantUtf8 extends Constant {
    private final String bytes;

    public static ConstantUtf8 getInstance(DataInput input) throws IOException {
        return new ConstantUtf8(input.readUTF());
    }

    private ConstantUtf8(String bytes) {
        super((byte) 1);
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null!");
        }
        this.bytes = bytes;
    }

    public final String getBytes() {
        return this.bytes;
    }
}