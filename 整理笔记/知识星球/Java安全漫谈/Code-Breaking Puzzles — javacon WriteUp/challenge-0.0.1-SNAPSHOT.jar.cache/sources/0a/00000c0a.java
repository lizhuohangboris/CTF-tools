package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantLong.class */
public final class ConstantLong extends Constant {
    private final long bytes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantLong(DataInput input) throws IOException {
        super((byte) 5);
        this.bytes = input.readLong();
    }

    public final long getBytes() {
        return this.bytes;
    }
}