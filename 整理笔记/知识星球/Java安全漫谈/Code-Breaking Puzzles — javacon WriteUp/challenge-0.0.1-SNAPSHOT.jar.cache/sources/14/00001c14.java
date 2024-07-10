package org.springframework.cglib.proxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.ClassInfo;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ObjectSwitchCallback;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.Transformer;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.CallbackGenerator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/proxy/MethodInterceptorGenerator.class */
public class MethodInterceptorGenerator implements CallbackGenerator {
    static final String EMPTY_ARGS_NAME = "CGLIB$emptyArgs";
    public static final MethodInterceptorGenerator INSTANCE = new MethodInterceptorGenerator();
    static final Class[] FIND_PROXY_TYPES = {Signature.class};
    private static final Type ABSTRACT_METHOD_ERROR = TypeUtils.parseType("AbstractMethodError");
    private static final Type METHOD = TypeUtils.parseType("java.lang.reflect.Method");
    private static final Type REFLECT_UTILS = TypeUtils.parseType("org.springframework.cglib.core.ReflectUtils");
    private static final Type METHOD_PROXY = TypeUtils.parseType("org.springframework.cglib.proxy.MethodProxy");
    private static final Type METHOD_INTERCEPTOR = TypeUtils.parseType("org.springframework.cglib.proxy.MethodInterceptor");
    private static final Signature GET_DECLARED_METHODS = TypeUtils.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
    private static final Signature GET_DECLARING_CLASS = TypeUtils.parseSignature("Class getDeclaringClass()");
    private static final Signature FIND_METHODS = TypeUtils.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
    private static final Signature MAKE_PROXY = new Signature("create", METHOD_PROXY, new Type[]{Constants.TYPE_CLASS, Constants.TYPE_CLASS, Constants.TYPE_STRING, Constants.TYPE_STRING, Constants.TYPE_STRING});
    private static final Signature INTERCEPT = new Signature("intercept", Constants.TYPE_OBJECT, new Type[]{Constants.TYPE_OBJECT, METHOD, Constants.TYPE_OBJECT_ARRAY, METHOD_PROXY});
    static final String FIND_PROXY_NAME = "CGLIB$findMethodProxy";
    private static final Signature FIND_PROXY = new Signature(FIND_PROXY_NAME, METHOD_PROXY, new Type[]{Constants.TYPE_SIGNATURE});
    private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
    private static final Transformer METHOD_TO_CLASS = new Transformer() { // from class: org.springframework.cglib.proxy.MethodInterceptorGenerator.1
        @Override // org.springframework.cglib.core.Transformer
        public Object transform(Object value) {
            return ((MethodInfo) value).getClassInfo();
        }
    };
    private static final Signature CSTRUCT_SIGNATURE = TypeUtils.parseConstructor("String, String");

    MethodInterceptorGenerator() {
    }

    private String getMethodField(Signature impl) {
        return impl.getName() + "$Method";
    }

    private String getMethodProxyField(Signature impl) {
        return impl.getName() + "$Proxy";
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
        Map sigMap = new HashMap();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            MethodInfo method = (MethodInfo) it.next();
            Signature sig = method.getSignature();
            Signature impl = context.getImplSignature(method);
            String methodField = getMethodField(impl);
            String methodProxyField = getMethodProxyField(impl);
            sigMap.put(sig.toString(), methodProxyField);
            ce.declare_field(26, methodField, METHOD, null);
            ce.declare_field(26, methodProxyField, METHOD_PROXY, null);
            ce.declare_field(26, EMPTY_ARGS_NAME, Constants.TYPE_OBJECT_ARRAY, null);
            CodeEmitter e = ce.begin_method(16, impl, method.getExceptionTypes());
            superHelper(e, method, context);
            e.return_value();
            e.end_method();
            CodeEmitter e2 = context.beginMethod(ce, method);
            Label nullInterceptor = e2.make_label();
            context.emitCallback(e2, context.getIndex(method));
            e2.dup();
            e2.ifnull(nullInterceptor);
            e2.load_this();
            e2.getfield(methodField);
            if (sig.getArgumentTypes().length == 0) {
                e2.getfield(EMPTY_ARGS_NAME);
            } else {
                e2.create_arg_array();
            }
            e2.getfield(methodProxyField);
            e2.invoke_interface(METHOD_INTERCEPTOR, INTERCEPT);
            e2.unbox_or_zero(sig.getReturnType());
            e2.return_value();
            e2.mark(nullInterceptor);
            superHelper(e2, method, context);
            e2.return_value();
            e2.end_method();
        }
        generateFindProxy(ce, sigMap);
    }

    private static void superHelper(CodeEmitter e, MethodInfo method, CallbackGenerator.Context context) {
        if (TypeUtils.isAbstract(method.getModifiers())) {
            e.throw_exception(ABSTRACT_METHOD_ERROR, method.toString() + " is abstract");
            return;
        }
        e.load_this();
        context.emitLoadArgsAndInvoke(e, method);
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods) throws Exception {
        e.push(0);
        e.newarray();
        e.putfield(EMPTY_ARGS_NAME);
        Local thisclass = e.make_local();
        Local declaringclass = e.make_local();
        EmitUtils.load_class_this(e);
        e.store_local(thisclass);
        Map methodsByClass = CollectionUtils.bucket(methods, METHOD_TO_CLASS);
        for (ClassInfo classInfo : methodsByClass.keySet()) {
            List classMethods = (List) methodsByClass.get(classInfo);
            e.push(2 * classMethods.size());
            e.newarray(Constants.TYPE_STRING);
            for (int index = 0; index < classMethods.size(); index++) {
                Signature sig = ((MethodInfo) classMethods.get(index)).getSignature();
                e.dup();
                e.push(2 * index);
                e.push(sig.getName());
                e.aastore();
                e.dup();
                e.push((2 * index) + 1);
                e.push(sig.getDescriptor());
                e.aastore();
            }
            EmitUtils.load_class(e, classInfo.getType());
            e.dup();
            e.store_local(declaringclass);
            e.invoke_virtual(Constants.TYPE_CLASS, GET_DECLARED_METHODS);
            e.invoke_static(REFLECT_UTILS, FIND_METHODS);
            for (int index2 = 0; index2 < classMethods.size(); index2++) {
                MethodInfo method = (MethodInfo) classMethods.get(index2);
                Signature sig2 = method.getSignature();
                Signature impl = context.getImplSignature(method);
                e.dup();
                e.push(index2);
                e.array_load(METHOD);
                e.putfield(getMethodField(impl));
                e.load_local(declaringclass);
                e.load_local(thisclass);
                e.push(sig2.getDescriptor());
                e.push(sig2.getName());
                e.push(impl.getName());
                e.invoke_static(METHOD_PROXY, MAKE_PROXY);
                e.putfield(getMethodProxyField(impl));
            }
            e.pop();
        }
    }

    public void generateFindProxy(ClassEmitter ce, final Map sigMap) {
        final CodeEmitter e = ce.begin_method(9, FIND_PROXY, null);
        e.load_arg(0);
        e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
        ObjectSwitchCallback callback = new ObjectSwitchCallback() { // from class: org.springframework.cglib.proxy.MethodInterceptorGenerator.2
            {
                MethodInterceptorGenerator.this = this;
            }

            @Override // org.springframework.cglib.core.ObjectSwitchCallback
            public void processCase(Object key, Label end) {
                e.getfield((String) sigMap.get(key));
                e.return_value();
            }

            @Override // org.springframework.cglib.core.ObjectSwitchCallback
            public void processDefault() {
                e.aconst_null();
                e.return_value();
            }
        };
        EmitUtils.string_switch(e, (String[]) sigMap.keySet().toArray(new String[0]), 1, callback);
        e.end_method();
    }
}