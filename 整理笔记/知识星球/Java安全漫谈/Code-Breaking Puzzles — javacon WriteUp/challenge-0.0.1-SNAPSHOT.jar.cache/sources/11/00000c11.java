package org.apache.tomcat.util.bcel.classfile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/bcel/classfile/SimpleElementValue.class */
public class SimpleElementValue extends ElementValue {
    private final int index;

    public SimpleElementValue(int type, int index, ConstantPool cpool) {
        super(type, cpool);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        ConstantPool cpool = super.getConstantPool();
        int _type = super.getType();
        switch (_type) {
            case 66:
                ConstantInteger b = (ConstantInteger) cpool.getConstant(getIndex(), (byte) 3);
                return Integer.toString(b.getBytes());
            case 67:
                ConstantInteger ch2 = (ConstantInteger) cpool.getConstant(getIndex(), (byte) 3);
                return String.valueOf((char) ch2.getBytes());
            case 68:
                ConstantDouble d = (ConstantDouble) cpool.getConstant(getIndex(), (byte) 6);
                return Double.toString(d.getBytes());
            case 70:
                ConstantFloat f = (ConstantFloat) cpool.getConstant(getIndex(), (byte) 4);
                return Float.toString(f.getBytes());
            case 73:
                ConstantInteger c = (ConstantInteger) cpool.getConstant(getIndex(), (byte) 3);
                return Integer.toString(c.getBytes());
            case 74:
                ConstantLong j = (ConstantLong) cpool.getConstant(getIndex(), (byte) 5);
                return Long.toString(j.getBytes());
            case 83:
                ConstantInteger s = (ConstantInteger) cpool.getConstant(getIndex(), (byte) 3);
                return Integer.toString(s.getBytes());
            case 90:
                ConstantInteger bo = (ConstantInteger) cpool.getConstant(getIndex(), (byte) 3);
                if (bo.getBytes() == 0) {
                    return "false";
                }
                return "true";
            case 115:
                ConstantUtf8 cu8 = (ConstantUtf8) cpool.getConstant(getIndex(), (byte) 1);
                return cu8.getBytes();
            default:
                throw new RuntimeException("SimpleElementValue class does not know how to stringify type " + _type);
        }
    }
}