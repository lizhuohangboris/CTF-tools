package org.springframework.cglib.transform.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/AccessFieldTransformer.class */
public class AccessFieldTransformer extends ClassEmitterTransformer {
    private Callback callback;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/AccessFieldTransformer$Callback.class */
    public interface Callback {
        String getPropertyName(Type type, String str);
    }

    public AccessFieldTransformer(Callback callback) {
        this.callback = callback;
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public void declare_field(int access, String name, Type type, Object value) {
        super.declare_field(access, name, type, value);
        String property = TypeUtils.upperFirst(this.callback.getPropertyName(getClassType(), name));
        if (property != null) {
            CodeEmitter e = begin_method(1, new Signature(BeanUtil.PREFIX_GETTER_GET + property, type, Constants.TYPES_EMPTY), null);
            e.load_this();
            e.getfield(name);
            e.return_value();
            e.end_method();
            CodeEmitter e2 = begin_method(1, new Signature("set" + property, Type.VOID_TYPE, new Type[]{type}), null);
            e2.load_this();
            e2.load_arg(0);
            e2.putfield(name);
            e2.return_value();
            e2.end_method();
        }
    }
}