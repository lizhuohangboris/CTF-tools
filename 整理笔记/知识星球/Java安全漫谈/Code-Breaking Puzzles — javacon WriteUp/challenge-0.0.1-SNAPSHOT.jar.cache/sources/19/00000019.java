package ch.qos.logback.classic.gaffer;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.spi.AppenderAttachable;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

/* compiled from: AppenderDelegate.groovy */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/AppenderDelegate.class */
public class AppenderDelegate extends ComponentDelegate {
    private Map<String, Appender<?>> appendersByName;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.gaffer.ComponentDelegate
    public /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != AppenderDelegate.class) {
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

    public Map<String, Appender<?>> getAppendersByName() {
        return this.appendersByName;
    }

    public void setAppendersByName(Map<String, Appender<?>> map) {
        this.appendersByName = map;
    }

    public /* synthetic */ String super$3$getLabel() {
        return super.getLabel();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
        strArr[0] = "collectEntries";
        strArr[1] = "isAssignableFrom";
        strArr[2] = "class";
        strArr[3] = "component";
        strArr[4] = "plus";
        strArr[5] = "plus";
        strArr[6] = "plus";
        strArr[7] = "name";
        strArr[8] = "class";
        strArr[9] = "component";
        strArr[10] = "name";
        strArr[11] = "<$constructor$>";
        strArr[12] = "addAppender";
        strArr[13] = "component";
        strArr[14] = "getAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[15];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(AppenderDelegate.class, strArr);
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
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.AppenderDelegate.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.AppenderDelegate.$callSiteArray
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
            ch.qos.logback.classic.gaffer.AppenderDelegate.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.AppenderDelegate.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AppenderDelegate(Appender appender) {
        super(appender);
        $getCallSiteArray();
        this.appendersByName = ScriptBytecodeAdapter.createMap(new Object[0]);
    }

    /* compiled from: AppenderDelegate.groovy */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/AppenderDelegate$_closure1.class */
    class _closure1 extends Closure implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public _closure1(Object _outerInstance, Object _thisObject) {
            super(_outerInstance, _thisObject);
            $getCallSiteArray();
        }

        public Object doCall() {
            $getCallSiteArray();
            return doCall(null);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (getClass() != _closure1.class) {
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
            strArr[0] = "name";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] strArr = new String[1];
            $createCallSiteArray_1(strArr);
            return new CallSiteArray(_closure1.class, strArr);
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
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.AppenderDelegate._closure1.$callSiteArray
                if (r0 == 0) goto L14
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.AppenderDelegate._closure1.$callSiteArray
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
                ch.qos.logback.classic.gaffer.AppenderDelegate._closure1.$callSiteArray = r0
            L23:
                r0 = r4
                org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.AppenderDelegate._closure1.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
        }

        public Object doCall(Object it) {
            return ScriptBytecodeAdapter.createMap(new Object[]{$getCallSiteArray()[0].callGetProperty(it), it});
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AppenderDelegate(Appender appender, List<Appender<?>> list) {
        super(appender);
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        this.appendersByName = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.appendersByName = (Map) ScriptBytecodeAdapter.castToType($getCallSiteArray[0].call(list, new _closure1(this, this)), Map.class);
    }

    @Override // ch.qos.logback.classic.gaffer.ComponentDelegate
    public String getLabel() {
        $getCallSiteArray();
        return ActionConst.APPENDER_TAG;
    }

    public void appenderRef(String name) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox($getCallSiteArray[1].call(AppenderAttachable.class, $getCallSiteArray[2].callGetProperty($getCallSiteArray[3].callGroovyObjectGetProperty(this))))) {
            Object errorMessage = $getCallSiteArray[4].call($getCallSiteArray[5].call($getCallSiteArray[6].call($getCallSiteArray[7].callGetProperty($getCallSiteArray[8].callGetProperty($getCallSiteArray[9].callGroovyObjectGetProperty(this))), " does not implement "), $getCallSiteArray[10].callGetProperty(AppenderAttachable.class)), ".");
            throw ((Throwable) $getCallSiteArray[11].callConstructor(IllegalArgumentException.class, errorMessage));
        } else {
            $getCallSiteArray[12].call($getCallSiteArray[13].callGroovyObjectGetProperty(this), $getCallSiteArray[14].call(this.appendersByName, name));
        }
    }
}