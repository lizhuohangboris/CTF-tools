package ch.qos.logback.classic.gaffer;

import ch.qos.logback.core.CoreConstants;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.ImmutableASTTransformation;
import org.springframework.web.servlet.tags.form.InputTag;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* compiled from: NestedType.groovy */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/NestingType.class */
public final class NestingType implements GroovyObject {
    public static final NestingType NA = (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray()[13].callStatic(NestingType.class, "NA", 0), NestingType.class);
    public static final NestingType SINGLE = (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray()[14].callStatic(NestingType.class, "SINGLE", 1), NestingType.class);
    public static final NestingType SINGLE_WITH_VALUE_OF_CONVENTION = (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray()[15].callStatic(NestingType.class, "SINGLE_WITH_VALUE_OF_CONVENTION", 2), NestingType.class);
    public static final NestingType AS_COLLECTION = (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray()[16].callStatic(NestingType.class, "AS_COLLECTION", 3), NestingType.class);
    public static final NestingType MIN_VALUE = NA;
    public static final NestingType MAX_VALUE = AS_COLLECTION;
    private static final /* synthetic */ NestingType[] $VALUES = {NA, SINGLE, SINGLE_WITH_VALUE_OF_CONVENTION, AS_COLLECTION};
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public NestingType(String __str, int __int, LinkedHashMap __namedArgs) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        this.metaClass = $getStaticMetaClass();
        if (ScriptBytecodeAdapter.compareEqual(__namedArgs, (Object) null)) {
            throw ((Throwable) $getCallSiteArray[0].callConstructor(IllegalArgumentException.class, "One of the enum constants for enum ch.qos.logback.classic.gaffer.NestingType was initialized with null. Please use a non-null value or define your own constructor."));
        }
        $getCallSiteArray[1].callStatic(ImmutableASTTransformation.class, this, __namedArgs);
    }

    public NestingType(String __str, int __int) {
        this(__str, __int, (LinkedHashMap) ScriptBytecodeAdapter.castToType($getCallSiteArray()[2].callConstructor(LinkedHashMap.class), LinkedHashMap.class));
    }

    public static final NestingType[] values() {
        $getCallSiteArray();
        return (NestingType[]) ScriptBytecodeAdapter.castToType($VALUES.clone(), NestingType[].class);
    }

    public /* synthetic */ NestingType next() {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        Object ordinal = $getCallSiteArray[3].call($getCallSiteArray[4].callCurrent(this));
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(ordinal, $getCallSiteArray[5].call($VALUES))) {
            ordinal = 0;
        }
        return (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray[6].call($VALUES, ordinal), NestingType.class);
    }

    public /* synthetic */ NestingType previous() {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        Object ordinal = $getCallSiteArray[7].call($getCallSiteArray[8].callCurrent(this));
        if (ScriptBytecodeAdapter.compareLessThan(ordinal, 0)) {
            ordinal = $getCallSiteArray[9].call($getCallSiteArray[10].call($VALUES), 1);
        }
        return (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray[11].call($VALUES, ordinal), NestingType.class);
    }

    public static NestingType valueOf(String name) {
        return (NestingType) ShortTypeHandling.castToEnum($getCallSiteArray()[12].callStatic(NestingType.class, NestingType.class, name), NestingType.class);
    }

    public static final /* synthetic */ NestingType $INIT(Object... para) {
        NestingType nestingType;
        NestingType nestingType2;
        Object[] objArr;
        $getCallSiteArray();
        Object[] despreadList = ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{para}, new int[]{0});
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(despreadList, -1, NestingType.class)) {
            case -1348271900:
                nestingType2 = nestingType;
                objArr = despreadList;
                new NestingType(ShortTypeHandling.castToString(objArr[0]), DefaultTypeTransformation.intUnbox(objArr[1]));
                break;
            case -242181752:
                nestingType2 = nestingType;
                objArr = despreadList;
                new NestingType(ShortTypeHandling.castToString(objArr[0]), DefaultTypeTransformation.intUnbox(objArr[1]), (LinkedHashMap) ScriptBytecodeAdapter.castToType(objArr[2], LinkedHashMap.class));
                break;
            default:
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
        }
        return nestingType2;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != NestingType.class) {
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
        strArr[0] = "<$constructor$>";
        strArr[1] = "checkPropNames";
        strArr[2] = "<$constructor$>";
        strArr[3] = "next";
        strArr[4] = "ordinal";
        strArr[5] = InputTag.SIZE_ATTRIBUTE;
        strArr[6] = "getAt";
        strArr[7] = "previous";
        strArr[8] = "ordinal";
        strArr[9] = "minus";
        strArr[10] = InputTag.SIZE_ATTRIBUTE;
        strArr[11] = "getAt";
        strArr[12] = CoreConstants.VALUE_OF;
        strArr[13] = "$INIT";
        strArr[14] = "$INIT";
        strArr[15] = "$INIT";
        strArr[16] = "$INIT";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[17];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(NestingType.class, strArr);
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
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.NestingType.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.NestingType.$callSiteArray
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
            ch.qos.logback.classic.gaffer.NestingType.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.NestingType.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }
}