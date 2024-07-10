package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/Constant.class */
public abstract class Constant {
    protected final byte tag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Constant(byte tag) {
        this.tag = tag;
    }

    public final byte getTag() {
        return this.tag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Constant readConstant(DataInput dataInput) throws IOException, ClassFormatException {
        int skipSize;
        byte b = dataInput.readByte();
        switch (b) {
            case 1:
                return ConstantUtf8.getInstance(dataInput);
            case 2:
            case 13:
            case 14:
            default:
                throw new ClassFormatException("Invalid byte tag in constant pool: " + ((int) b));
            case 3:
                return new ConstantInteger(dataInput);
            case 4:
                return new ConstantFloat(dataInput);
            case 5:
                return new ConstantLong(dataInput);
            case 6:
                return new ConstantDouble(dataInput);
            case 7:
                return new ConstantClass(dataInput);
            case 8:
            case 16:
            case 19:
            case 20:
                skipSize = 2;
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 17:
            case 18:
                skipSize = 4;
                break;
            case 15:
                skipSize = 3;
                break;
        }
        Utility.skipFully(dataInput, skipSize);
        return null;
    }

    public String toString() {
        return PropertyAccessor.PROPERTY_KEY_PREFIX + ((int) this.tag) + "]";
    }
}