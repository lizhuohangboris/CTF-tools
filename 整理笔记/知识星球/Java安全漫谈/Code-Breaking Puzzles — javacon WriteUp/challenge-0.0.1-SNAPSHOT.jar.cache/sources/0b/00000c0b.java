package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/ConstantPool.class */
public class ConstantPool {
    private final Constant[] constant_pool;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantPool(DataInput input) throws IOException, ClassFormatException {
        byte tag;
        int constant_pool_count = input.readUnsignedShort();
        this.constant_pool = new Constant[constant_pool_count];
        int i = 1;
        while (i < constant_pool_count) {
            this.constant_pool[i] = Constant.readConstant(input);
            if (this.constant_pool[i] != null && ((tag = this.constant_pool[i].getTag()) == 6 || tag == 5)) {
                i++;
            }
            i++;
        }
    }

    public Constant getConstant(int index) {
        if (index >= this.constant_pool.length || index < 0) {
            throw new ClassFormatException("Invalid constant pool reference: " + index + ". Constant pool size is: " + this.constant_pool.length);
        }
        return this.constant_pool[index];
    }

    public Constant getConstant(int index, byte tag) throws ClassFormatException {
        Constant c = getConstant(index);
        if (c == null) {
            throw new ClassFormatException("Constant pool at index " + index + " is null.");
        }
        if (c.getTag() != tag) {
            throw new ClassFormatException("Expected class `" + Const.getConstantName(tag) + "' at index " + index + " and got " + c);
        }
        return c;
    }
}