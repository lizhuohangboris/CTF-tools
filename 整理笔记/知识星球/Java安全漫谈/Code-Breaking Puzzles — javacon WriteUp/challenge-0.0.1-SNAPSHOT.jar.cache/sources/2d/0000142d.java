package org.springframework.beans.factory.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.Generated;
import groovy.xml.StreamingMarkupBuilder;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* compiled from: GroovyDynamicElementReader.groovy */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/groovy/GroovyDynamicElementReader.class */
class GroovyDynamicElementReader extends GroovyObjectSupport {
    private final String rootNamespace;
    private final Map<String, String> xmlNamespaces;
    private final BeanDefinitionParserDelegate delegate;
    private final GroovyBeanDefinitionWrapper beanDefinition;
    protected final boolean decorating;
    private boolean callAfterInvocation;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;
    private static /* synthetic */ Class $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper;

    protected void afterInvocation() {
        $getCallSiteArray();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != GroovyDynamicElementReader.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            ClassInfo classInfo2 = ClassInfo.getClassInfo(getClass());
            classInfo = classInfo2;
            $staticClassInfo = classInfo2;
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$2$invokeMethod(String str, Object obj) {
        return super.invokeMethod(str, obj);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
        strArr[0] = "equals";
        strArr[1] = "getAt";
        strArr[2] = "DELEGATE_FIRST";
        strArr[3] = "call";
        strArr[4] = "afterInvocation";
        strArr[5] = "<$constructor$>";
        strArr[6] = "DELEGATE_FIRST";
        strArr[7] = "bind";
        strArr[8] = "<$constructor$>";
        strArr[9] = "writeTo";
        strArr[10] = "documentElement";
        strArr[11] = "readDocumentFromString";
        strArr[12] = "readerContext";
        strArr[13] = "toString";
        strArr[14] = "initDefaults";
        strArr[15] = "beanDefinitionHolder";
        strArr[16] = "decorateIfRequired";
        strArr[17] = "setBeanDefinitionHolder";
        strArr[18] = "parseCustomElement";
        strArr[19] = "setBeanDefinition";
        strArr[20] = "afterInvocation";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[21];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(GroovyDynamicElementReader.class, strArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0011, code lost:
        if (r0 == null) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static /* synthetic */ org.codehaus.groovy.runtime.callsite.CallSite[] $getCallSiteArray() {
        /*
            java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray
            java.lang.Object r0 = r0.get()
            org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = (org.codehaus.groovy.runtime.callsite.CallSiteArray) r0
            r1 = r0
            r4 = r1
            if (r0 != 0) goto L23
        L14:
            org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = $createCallSiteArray()
            r4 = r0
            java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference
            r1 = r0
            r2 = r4
            r1.<init>(r2)
            org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }

    private static /* synthetic */ Class $get$$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper() {
        Class cls = $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper;
        if (cls == null) {
            Class class$ = class$("org.springframework.beans.factory.groovy.GroovyBeanDefinitionWrapper");
            $class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper = class$;
            return class$;
        }
        return cls;
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public GroovyDynamicElementReader(String namespace, Map<String, String> map, BeanDefinitionParserDelegate delegate, GroovyBeanDefinitionWrapper beanDefinition, boolean decorating) {
        $getCallSiteArray();
        this.callAfterInvocation = true;
        this.rootNamespace = ShortTypeHandling.castToString(namespace);
        this.xmlNamespaces = (Map) ScriptBytecodeAdapter.castToType(map, Map.class);
        this.delegate = (BeanDefinitionParserDelegate) ScriptBytecodeAdapter.castToType(delegate, BeanDefinitionParserDelegate.class);
        this.beanDefinition = (GroovyBeanDefinitionWrapper) ScriptBytecodeAdapter.castToType(beanDefinition, $get$$class$org$springframework$beans$factory$groovy$GroovyBeanDefinitionWrapper());
        this.decorating = DefaultTypeTransformation.booleanUnbox(Boolean.valueOf(decorating));
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public java.lang.Object invokeMethod(java.lang.String r11, java.lang.Object r12) {
        /*
            Method dump skipped, instructions count: 696
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.groovy.GroovyDynamicElementReader.invokeMethod(java.lang.String, java.lang.Object):java.lang.Object");
    }

    /* compiled from: GroovyDynamicElementReader.groovy */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/groovy/GroovyDynamicElementReader$_invokeMethod_closure1.class */
    public final class _invokeMethod_closure1 extends Closure implements GeneratedClosure {
        private /* synthetic */ Reference myNamespaces;
        private /* synthetic */ Reference args;
        private /* synthetic */ Reference builder;
        private /* synthetic */ Reference myNamespace;
        private /* synthetic */ Reference name;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public _invokeMethod_closure1(Object _outerInstance, Object _thisObject, Reference myNamespaces, Reference args, Reference builder, Reference myNamespace, Reference name) {
            super(_outerInstance, _thisObject);
            $getCallSiteArray();
            this.myNamespaces = myNamespaces;
            this.args = args;
            this.builder = builder;
            this.myNamespace = myNamespace;
            this.name = name;
        }

        public Object getMyNamespaces() {
            $getCallSiteArray();
            return this.myNamespaces.get();
        }

        public Object getArgs() {
            $getCallSiteArray();
            return this.args.get();
        }

        public StreamingMarkupBuilder getBuilder() {
            $getCallSiteArray();
            return (StreamingMarkupBuilder) ScriptBytecodeAdapter.castToType(this.builder.get(), StreamingMarkupBuilder.class);
        }

        public Object getMyNamespace() {
            $getCallSiteArray();
            return this.myNamespace.get();
        }

        public String getName() {
            $getCallSiteArray();
            return ShortTypeHandling.castToString(this.name.get());
        }

        @Generated
        public Object doCall() {
            $getCallSiteArray();
            return doCall(null);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (getClass() != _invokeMethod_closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                ClassInfo classInfo2 = ClassInfo.getClassInfo(getClass());
                classInfo = classInfo2;
                $staticClassInfo = classInfo2;
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
            strArr[0] = "iterator";
            strArr[1] = "declareNamespace";
            strArr[2] = "mkp";
            strArr[3] = "key";
            strArr[4] = "value";
            strArr[5] = "getAt";
            strArr[6] = "DELEGATE_FIRST";
            strArr[7] = "getAt";
            strArr[8] = "getAt";
            strArr[9] = "delegate";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] strArr = new String[10];
            $createCallSiteArray_1(strArr);
            return new CallSiteArray(_invokeMethod_closure1.class, strArr);
        }

        /* JADX WARN: Code restructure failed: missing block: B:5:0x0011, code lost:
            if (r0 == null) goto L8;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private static /* synthetic */ org.codehaus.groovy.runtime.callsite.CallSite[] $getCallSiteArray() {
            /*
                java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray
                if (r0 == 0) goto L14
                java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray
                java.lang.Object r0 = r0.get()
                org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = (org.codehaus.groovy.runtime.callsite.CallSiteArray) r0
                r1 = r0
                r4 = r1
                if (r0 != 0) goto L23
            L14:
                org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = $createCallSiteArray()
                r4 = r0
                java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference
                r1 = r0
                r2 = r4
                r1.<init>(r2)
                org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray = r0
            L23:
                r0 = r4
                org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
        }

        public Object doCall(Object it) {
            CallSite[] $getCallSiteArray = $getCallSiteArray();
            Iterator it2 = (Iterator) ScriptBytecodeAdapter.castToType($getCallSiteArray[0].call(this.myNamespaces.get()), Iterator.class);
            while (it2.hasNext()) {
                Object namespace = it2.next();
                $getCallSiteArray[1].call($getCallSiteArray[2].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.createMap(new Object[]{$getCallSiteArray[3].callGetProperty(namespace), $getCallSiteArray[4].callGetProperty(namespace)}));
            }
            if (DefaultTypeTransformation.booleanUnbox(this.args.get()) && ($getCallSiteArray[5].call(this.args.get(), -1) instanceof Closure)) {
                ScriptBytecodeAdapter.setProperty($getCallSiteArray[6].callGetProperty(Closure.class), (Class) null, $getCallSiteArray[7].call(this.args.get(), -1), "resolveStrategy");
                ScriptBytecodeAdapter.setProperty(this.builder.get(), (Class) null, $getCallSiteArray[8].call(this.args.get(), -1), "delegate");
            }
            return ScriptBytecodeAdapter.invokeMethodN(_invokeMethod_closure1.class, ScriptBytecodeAdapter.getProperty(_invokeMethod_closure1.class, $getCallSiteArray[9].callGroovyObjectGetProperty(this), ShortTypeHandling.castToString(new GStringImpl(new Object[]{this.myNamespace.get()}, new String[]{"", ""}))), ShortTypeHandling.castToString(new GStringImpl(new Object[]{this.name.get()}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{this.args.get()}, new int[]{0}));
        }
    }
}