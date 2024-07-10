package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/AnnotationEntry.class */
public class AnnotationEntry {
    private final int type_index;
    private final ConstantPool constant_pool;
    private final List<ElementValuePair> element_value_pairs;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationEntry(DataInput input, ConstantPool constant_pool) throws IOException {
        this.constant_pool = constant_pool;
        this.type_index = input.readUnsignedShort();
        int num_element_value_pairs = input.readUnsignedShort();
        this.element_value_pairs = new ArrayList(num_element_value_pairs);
        for (int i = 0; i < num_element_value_pairs; i++) {
            this.element_value_pairs.add(new ElementValuePair(input, constant_pool));
        }
    }

    public String getAnnotationType() {
        ConstantUtf8 c = (ConstantUtf8) this.constant_pool.getConstant(this.type_index, (byte) 1);
        return c.getBytes();
    }

    public List<ElementValuePair> getElementValuePairs() {
        return this.element_value_pairs;
    }
}