package org.springframework.objenesis.instantiator.basic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.util.ClassDefinitionUtils;
import org.springframework.objenesis.instantiator.util.ClassUtils;

@Instantiator(Typology.STANDARD)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/basic/ProxyingInstantiator.class */
public class ProxyingInstantiator<T> implements ObjectInstantiator<T> {
    private static final int INDEX_CLASS_THIS = 1;
    private static final int INDEX_CLASS_SUPERCLASS = 2;
    private static final int INDEX_UTF8_CONSTRUCTOR_NAME = 3;
    private static final int INDEX_UTF8_CONSTRUCTOR_DESC = 4;
    private static final int INDEX_UTF8_CODE_ATTRIBUTE = 5;
    private static final int INDEX_UTF8_CLASS = 7;
    private static final int INDEX_UTF8_SUPERCLASS = 8;
    private static final int CONSTANT_POOL_COUNT = 9;
    private static final byte[] CODE = {42, -79};
    private static final int CODE_ATTRIBUTE_LENGTH = 12 + CODE.length;
    private static final String SUFFIX = "$$$Objenesis";
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String CONSTRUCTOR_DESC = "()V";
    private final Class<? extends T> newType;

    public ProxyingInstantiator(Class<T> type) {
        byte[] classBytes = writeExtendingClass(type);
        try {
            this.newType = ClassDefinitionUtils.defineClass(type.getName() + SUFFIX, classBytes, type, type.getClassLoader());
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        return (T) ClassUtils.newInstance(this.newType);
    }

    private static byte[] writeExtendingClass(Class<?> type) {
        String parentClazz = ClassUtils.classNameToInternalClassName(type.getName());
        String clazz = parentClazz + SUFFIX;
        ByteArrayOutputStream bIn = new ByteArrayOutputStream(1000);
        try {
            DataOutputStream in = new DataOutputStream(bIn);
            in.write(ClassDefinitionUtils.MAGIC);
            in.write(ClassDefinitionUtils.VERSION);
            in.writeShort(9);
            in.writeByte(7);
            in.writeShort(7);
            in.writeByte(7);
            in.writeShort(8);
            in.writeByte(1);
            in.writeUTF("<init>");
            in.writeByte(1);
            in.writeUTF(CONSTRUCTOR_DESC);
            in.writeByte(1);
            in.writeUTF("Code");
            in.writeByte(1);
            in.writeUTF("L" + clazz + ";");
            in.writeByte(1);
            in.writeUTF(clazz);
            in.writeByte(1);
            in.writeUTF(parentClazz);
            in.writeShort(33);
            in.writeShort(1);
            in.writeShort(2);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(1);
            in.writeShort(1);
            in.writeShort(3);
            in.writeShort(4);
            in.writeShort(1);
            in.writeShort(5);
            in.writeInt(CODE_ATTRIBUTE_LENGTH);
            in.writeShort(1);
            in.writeShort(1);
            in.writeInt(CODE.length);
            in.write(CODE);
            in.writeShort(0);
            in.writeShort(0);
            in.writeShort(0);
            if (in != null) {
                if (0 != 0) {
                    in.close();
                } else {
                    in.close();
                }
            }
            return bIn.toByteArray();
        } catch (IOException e) {
            throw new ObjenesisException(e);
        }
    }
}