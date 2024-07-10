package ch.qos.logback.classic.gaffer;

import ch.qos.logback.core.joran.util.StringToObjectConverter;
import ch.qos.logback.core.joran.util.beans.BeanUtil;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

/* compiled from: PropertyUtil.groovy */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/PropertyUtil.class */
public class PropertyUtil implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PropertyUtil() {
        $getCallSiteArray();
        this.metaClass = $getStaticMetaClass();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != PropertyUtil.class) {
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

    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = $getStaticMetaClass();
        return this.metaClass;
    }

    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public /* synthetic */ Object invokeMethod(String str, Object obj) {
        return getMetaClass().invokeMethod(this, str, obj);
    }

    public /* synthetic */ Object getProperty(String str) {
        return getMetaClass().getProperty(this, str);
    }

    public /* synthetic */ void setProperty(String str, Object obj) {
        getMetaClass().setProperty(this, str, obj);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
        strArr[0] = "upperCaseFirstLetter";
        strArr[1] = "respondsTo";
        strArr[2] = "metaClass";
        strArr[3] = "toLowerCamelCase";
        strArr[4] = "hasProperty";
        strArr[5] = "followsTheValueOfConvention";
        strArr[6] = "getType";
        strArr[7] = "SINGLE_WITH_VALUE_OF_CONVENTION";
        strArr[8] = "SINGLE";
        strArr[9] = "hasAdderMethod";
        strArr[10] = "AS_COLLECTION";
        strArr[11] = "NA";
        strArr[12] = "toLowerCamelCase";
        strArr[13] = "hasProperty";
        strArr[14] = "getValueOfMethod";
        strArr[15] = "getType";
        strArr[16] = "invoke";
        strArr[17] = "SINGLE_WITH_VALUE_OF_CONVENTION";
        strArr[18] = "toLowerCamelCase";
        strArr[19] = "convertByValueMethod";
        strArr[20] = "SINGLE";
        strArr[21] = "toLowerCamelCase";
        strArr[22] = "AS_COLLECTION";
        strArr[23] = "upperCaseFirstLetter";
        strArr[24] = "length";
        strArr[25] = "length";
        strArr[26] = "<$constructor$>";
        strArr[27] = "getAt";
        strArr[28] = "call";
        strArr[29] = "length";
        strArr[30] = "plus";
        strArr[31] = "substring";
        strArr[32] = "transformFirstLetter";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[33];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(PropertyUtil.class, strArr);
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
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.PropertyUtil.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.PropertyUtil.$callSiteArray
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
            ch.qos.logback.classic.gaffer.PropertyUtil.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.PropertyUtil.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }

    public static boolean hasAdderMethod(Object obj, String name) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        String addMethod = (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) ? ShortTypeHandling.castToString(new GStringImpl(new Object[]{$getCallSiteArray[0].callStatic(PropertyUtil.class, name)}, new String[]{BeanUtil.PREFIX_ADDER, ""})) : ShortTypeHandling.castToString(new GStringImpl(new Object[]{upperCaseFirstLetter(name)}, new String[]{BeanUtil.PREFIX_ADDER, ""}));
        return DefaultTypeTransformation.booleanUnbox($getCallSiteArray[1].call($getCallSiteArray[2].callGetProperty(obj), obj, addMethod));
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public static ch.qos.logback.classic.gaffer.NestingType nestingType(java.lang.Object r5, java.lang.String r6, java.lang.Object r7) {
        /*
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = $getCallSiteArray()
            r8 = r0
            r0 = r8
            r1 = 3
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.core.joran.util.beans.BeanUtil> r1 = ch.qos.logback.core.joran.util.beans.BeanUtil.class
            r2 = r6
            java.lang.Object r0 = r0.call(r1, r2)
            r9 = r0
            r0 = r9
            r0 = r8
            r1 = 4
            r0 = r0[r1]
            r1 = r5
            r2 = r9
            java.lang.Object r0 = r0.call(r1, r2)
            java.lang.Class<groovy.lang.MetaProperty> r1 = groovy.lang.MetaProperty.class
            java.lang.Object r0 = org.codehaus.groovy.runtime.ScriptBytecodeAdapter.castToType(r0, r1)
            groovy.lang.MetaProperty r0 = (groovy.lang.MetaProperty) r0
            r10 = r0
            r0 = r10
            r0 = r10
            r1 = 0
            boolean r0 = org.codehaus.groovy.runtime.ScriptBytecodeAdapter.compareNotEqual(r0, r1)
            if (r0 == 0) goto L9a
            r0 = r7
            boolean r0 = r0 instanceof java.lang.String
            r11 = r0
            r0 = r11
            r0 = r11
            if (r0 == 0) goto L65
            r0 = r8
            r1 = 5
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.core.joran.util.StringToObjectConverter> r1 = ch.qos.logback.core.joran.util.StringToObjectConverter.class
            r2 = r8
            r3 = 6
            r2 = r2[r3]
            r3 = r10
            java.lang.Object r2 = r2.call(r3)
            java.lang.Object r0 = r0.call(r1, r2)
            boolean r0 = org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.booleanUnbox(r0)
            if (r0 == 0) goto L65
            r0 = 1
            goto L66
        L65:
            r0 = 0
        L66:
            if (r0 == 0) goto L83
            r0 = r8
            r1 = 7
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Object r0 = r0.callGetProperty(r1)
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Enum r0 = org.codehaus.groovy.runtime.typehandling.ShortTypeHandling.castToEnum(r0, r1)
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            return r0
            goto L9a
        L83:
            r0 = r8
            r1 = 8
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Object r0 = r0.callGetProperty(r1)
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Enum r0 = org.codehaus.groovy.runtime.typehandling.ShortTypeHandling.castToEnum(r0, r1)
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            return r0
        L9a:
            r0 = r8
            r1 = 9
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.classic.gaffer.PropertyUtil> r1 = ch.qos.logback.classic.gaffer.PropertyUtil.class
            r2 = r5
            r3 = r6
            java.lang.Object r0 = r0.callStatic(r1, r2, r3)
            boolean r0 = org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.booleanUnbox(r0)
            if (r0 == 0) goto Lc4
            r0 = r8
            r1 = 10
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Object r0 = r0.callGetProperty(r1)
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Enum r0 = org.codehaus.groovy.runtime.typehandling.ShortTypeHandling.castToEnum(r0, r1)
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            return r0
        Lc4:
            r0 = r8
            r1 = 11
            r0 = r0[r1]
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Object r0 = r0.callGetProperty(r1)
            java.lang.Class<ch.qos.logback.classic.gaffer.NestingType> r1 = ch.qos.logback.classic.gaffer.NestingType.class
            java.lang.Enum r0 = org.codehaus.groovy.runtime.typehandling.ShortTypeHandling.castToEnum(r0, r1)
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            ch.qos.logback.classic.gaffer.NestingType r0 = (ch.qos.logback.classic.gaffer.NestingType) r0
            return r0
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.PropertyUtil.nestingType(java.lang.Object, java.lang.String, java.lang.Object):ch.qos.logback.classic.gaffer.NestingType");
    }

    public static Object convertByValueMethod(Object component, String name, String value) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        Object decapitalizedName = $getCallSiteArray[12].call(BeanUtil.class, name);
        MetaProperty metaProperty = (MetaProperty) ScriptBytecodeAdapter.castToType($getCallSiteArray[13].call(component, decapitalizedName), MetaProperty.class);
        Method valueOfMethod = (Method) ScriptBytecodeAdapter.castToType($getCallSiteArray[14].call(StringToObjectConverter.class, $getCallSiteArray[15].call(metaProperty)), Method.class);
        return $getCallSiteArray[16].call(valueOfMethod, (Object) null, value);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public static void attach(ch.qos.logback.classic.gaffer.NestingType r11, java.lang.Object r12, java.lang.Object r13, java.lang.String r14) {
        /*
            Method dump skipped, instructions count: 330
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.PropertyUtil.attach(ch.qos.logback.classic.gaffer.NestingType, java.lang.Object, java.lang.Object, java.lang.String):void");
    }

    public static String transformFirstLetter(String s, Closure closure) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(s, (Object) null) || ScriptBytecodeAdapter.compareEqual($getCallSiteArray[24].call(s), 0)) {
                return s;
            }
        } else {
            if (ScriptBytecodeAdapter.compareEqual(s, (Object) null) || ScriptBytecodeAdapter.compareEqual($getCallSiteArray[25].call(s), 0)) {
                return s;
            }
        }
        String firstLetter = ShortTypeHandling.castToString($getCallSiteArray[26].callConstructor(String.class, $getCallSiteArray[27].call(s, 0)));
        String modifiedFistLetter = ShortTypeHandling.castToString($getCallSiteArray[28].call(closure, firstLetter));
        if (ScriptBytecodeAdapter.compareEqual($getCallSiteArray[29].call(s), 1)) {
            return modifiedFistLetter;
        }
        return ShortTypeHandling.castToString($getCallSiteArray[30].call(modifiedFistLetter, $getCallSiteArray[31].call(s, 1)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: PropertyUtil.groovy */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/PropertyUtil$_upperCaseFirstLetter_closure1.class */
    public class _upperCaseFirstLetter_closure1 extends Closure implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public _upperCaseFirstLetter_closure1(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
            $getCallSiteArray();
        }

        public Object call(String it) {
            return (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) ? $getCallSiteArray()[1].callCurrent(this, it) : doCall(it);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (getClass() != _upperCaseFirstLetter_closure1.class) {
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
            strArr[0] = "toUpperCase";
            strArr[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] strArr = new String[2];
            $createCallSiteArray_1(strArr);
            return new CallSiteArray(_upperCaseFirstLetter_closure1.class, strArr);
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
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.PropertyUtil._upperCaseFirstLetter_closure1.$callSiteArray
                if (r0 == 0) goto L14
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.PropertyUtil._upperCaseFirstLetter_closure1.$callSiteArray
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
                ch.qos.logback.classic.gaffer.PropertyUtil._upperCaseFirstLetter_closure1.$callSiteArray = r0
            L23:
                r0 = r4
                org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.PropertyUtil._upperCaseFirstLetter_closure1.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
        }

        public Object doCall(String it) {
            return $getCallSiteArray()[0].call(it);
        }
    }

    public static String upperCaseFirstLetter(String s) {
        return ShortTypeHandling.castToString($getCallSiteArray()[32].callStatic(PropertyUtil.class, s, new _upperCaseFirstLetter_closure1(PropertyUtil.class, PropertyUtil.class)));
    }
}