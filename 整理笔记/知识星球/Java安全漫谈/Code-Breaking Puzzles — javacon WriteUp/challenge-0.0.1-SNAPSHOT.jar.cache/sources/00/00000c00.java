package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/Annotations.class */
public class Annotations {
    private final AnnotationEntry[] annotation_table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Annotations(DataInput input, ConstantPool constant_pool) throws IOException {
        int annotation_table_length = input.readUnsignedShort();
        this.annotation_table = new AnnotationEntry[annotation_table_length];
        for (int i = 0; i < annotation_table_length; i++) {
            this.annotation_table[i] = new AnnotationEntry(input, constant_pool);
        }
    }

    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotation_table;
    }
}