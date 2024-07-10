package ch.qos.logback.classic.gaffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* compiled from: GafferConfigurator.groovy */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/GafferConfigurator.class */
public class GafferConfigurator implements GroovyObject {
    private LoggerContext context;
    private static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != GafferConfigurator.class) {
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

    public LoggerContext getContext() {
        return this.context;
    }

    public void setContext(LoggerContext loggerContext) {
        this.context = loggerContext;
    }

    public static final String getDEBUG_SYSTEM_PROPERTY_KEY() {
        return DEBUG_SYSTEM_PROPERTY_KEY;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
        strArr[0] = "setMainWatchURL";
        strArr[1] = "informContextOfURLUsedForConfiguration";
        strArr[2] = "run";
        strArr[3] = "text";
        strArr[4] = "informContextOfURLUsedForConfiguration";
        strArr[5] = "toURL";
        strArr[6] = "toURI";
        strArr[7] = "run";
        strArr[8] = "text";
        strArr[9] = "<$constructor$>";
        strArr[10] = "setProperty";
        strArr[11] = "localHostName";
        strArr[12] = "<$constructor$>";
        strArr[13] = "addCompilationCustomizers";
        strArr[14] = "importCustomizer";
        strArr[15] = "addCompilationCustomizers";
        strArr[16] = "getProperty";
        strArr[17] = "isEmpty";
        strArr[18] = "equalsIgnoreCase";
        strArr[19] = "equalsIgnoreCase";
        strArr[20] = "addNewInstanceToContext";
        strArr[21] = "addGroovyPackages";
        strArr[22] = "<$constructor$>";
        strArr[23] = "getFrameworkPackages";
        strArr[24] = "parse";
        strArr[25] = "<$constructor$>";
        strArr[26] = "mixin";
        strArr[27] = "metaClass";
        strArr[28] = "setContext";
        strArr[29] = "metaClass";
        strArr[30] = "run";
        strArr[31] = "<$constructor$>";
        strArr[32] = "addStarImports";
        strArr[33] = "addImports";
        strArr[34] = "name";
        strArr[35] = "addStaticStars";
        strArr[36] = "name";
        strArr[37] = "addStaticImport";
        strArr[38] = "name";
        strArr[39] = "addStaticImport";
        strArr[40] = "name";
        strArr[41] = "addStaticImport";
        strArr[42] = "name";
        strArr[43] = "addStaticImport";
        strArr[44] = "name";
        strArr[45] = "addStaticImport";
        strArr[46] = "name";
        strArr[47] = "addStaticImport";
        strArr[48] = "name";
        strArr[49] = "addStaticImport";
        strArr[50] = "name";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[51];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(GafferConfigurator.class, strArr);
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
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.GafferConfigurator.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.GafferConfigurator.$callSiteArray
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
            ch.qos.logback.classic.gaffer.GafferConfigurator.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.GafferConfigurator.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }

    public GafferConfigurator(LoggerContext context) {
        $getCallSiteArray();
        this.metaClass = $getStaticMetaClass();
        this.context = (LoggerContext) ScriptBytecodeAdapter.castToType(context, LoggerContext.class);
    }

    protected void informContextOfURLUsedForConfiguration(URL url) {
        $getCallSiteArray()[0].call(ConfigurationWatchListUtil.class, this.context, url);
    }

    public void run(URL url) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            $getCallSiteArray[1].callCurrent(this, url);
        } else {
            informContextOfURLUsedForConfiguration(url);
        }
        $getCallSiteArray[2].callCurrent(this, $getCallSiteArray[3].callGetProperty(url));
    }

    public void run(File file) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        $getCallSiteArray[4].callCurrent(this, $getCallSiteArray[5].call($getCallSiteArray[6].call(file)));
        $getCallSiteArray[7].callCurrent(this, $getCallSiteArray[8].callGetProperty(file));
    }

    public void run(String dslText) {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        Binding binding = (Binding) ScriptBytecodeAdapter.castToType($getCallSiteArray[9].callConstructor(Binding.class), Binding.class);
        $getCallSiteArray[10].call(binding, "hostname", $getCallSiteArray[11].callGetProperty(ContextUtil.class));
        Object configuration = $getCallSiteArray[12].callConstructor(CompilerConfiguration.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            $getCallSiteArray[13].call(configuration, $getCallSiteArray[14].callCurrent(this));
        } else {
            $getCallSiteArray[15].call(configuration, importCustomizer());
        }
        String debugAttrib = ShortTypeHandling.castToString($getCallSiteArray[16].call(System.class, DEBUG_SYSTEM_PROPERTY_KEY));
        if (!((DefaultTypeTransformation.booleanUnbox($getCallSiteArray[17].call(OptionHelper.class, debugAttrib)) || DefaultTypeTransformation.booleanUnbox($getCallSiteArray[18].call(debugAttrib, "false"))) || DefaultTypeTransformation.booleanUnbox($getCallSiteArray[19].call(debugAttrib, BeanDefinitionParserDelegate.NULL_ELEMENT)))) {
            $getCallSiteArray[20].call(OnConsoleStatusListener.class, this.context);
        }
        $getCallSiteArray[21].call($getCallSiteArray[22].callConstructor(ContextUtil.class, this.context), $getCallSiteArray[23].call(this.context));
        Reference dslScript = new Reference((Script) ScriptBytecodeAdapter.castToType($getCallSiteArray[24].call($getCallSiteArray[25].callConstructor(GroovyShell.class, binding, configuration), dslText), Script.class));
        $getCallSiteArray[26].call($getCallSiteArray[27].callGroovyObjectGetProperty((Script) dslScript.get()), ConfigurationDelegate.class);
        $getCallSiteArray[28].call((Script) dslScript.get(), this.context);
        ScriptBytecodeAdapter.setProperty(new _run_closure1(this, this, dslScript), (Class) null, $getCallSiteArray[29].callGroovyObjectGetProperty((Script) dslScript.get()), "getDeclaredOrigin");
        $getCallSiteArray[30].call((Script) dslScript.get());
    }

    /* compiled from: GafferConfigurator.groovy */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/gaffer/GafferConfigurator$_run_closure1.class */
    class _run_closure1 extends Closure implements GeneratedClosure {
        private /* synthetic */ Reference dslScript;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public _run_closure1(Object _outerInstance, Object _thisObject, Reference dslScript) {
            super(_outerInstance, _thisObject);
            $getCallSiteArray();
            this.dslScript = dslScript;
        }

        public Script getDslScript() {
            $getCallSiteArray();
            return (Script) ScriptBytecodeAdapter.castToType(this.dslScript.get(), Script.class);
        }

        public Object doCall() {
            $getCallSiteArray();
            return doCall(null);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (getClass() != _run_closure1.class) {
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

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            return new CallSiteArray(_run_closure1.class, new String[0]);
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
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.GafferConfigurator._run_closure1.$callSiteArray
                if (r0 == 0) goto L14
                java.lang.ref.SoftReference r0 = ch.qos.logback.classic.gaffer.GafferConfigurator._run_closure1.$callSiteArray
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
                ch.qos.logback.classic.gaffer.GafferConfigurator._run_closure1.$callSiteArray = r0
            L23:
                r0 = r4
                org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: ch.qos.logback.classic.gaffer.GafferConfigurator._run_closure1.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
        }

        public Object doCall(Object it) {
            $getCallSiteArray();
            return this.dslScript.get();
        }
    }

    protected ImportCustomizer importCustomizer() {
        CallSite[] $getCallSiteArray = $getCallSiteArray();
        Object customizer = $getCallSiteArray[31].callConstructor(ImportCustomizer.class);
        $getCallSiteArray[32].call(customizer, ArrayUtil.createArray("ch.qos.logback.core", new GStringImpl(new Object[]{"ch.qos.logback.core"}, new String[]{"", ".encoder"}), new GStringImpl(new Object[]{"ch.qos.logback.core"}, new String[]{"", ".read"}), new GStringImpl(new Object[]{"ch.qos.logback.core"}, new String[]{"", ".rolling"}), new GStringImpl(new Object[]{"ch.qos.logback.core"}, new String[]{"", ".status"}), "ch.qos.logback.classic.net"));
        $getCallSiteArray[33].call(customizer, $getCallSiteArray[34].callGetProperty(PatternLayoutEncoder.class));
        $getCallSiteArray[35].call(customizer, $getCallSiteArray[36].callGetProperty(Level.class));
        $getCallSiteArray[37].call(customizer, CustomBooleanEditor.VALUE_OFF, $getCallSiteArray[38].callGetProperty(Level.class), "OFF");
        $getCallSiteArray[39].call(customizer, "error", $getCallSiteArray[40].callGetProperty(Level.class), "ERROR");
        $getCallSiteArray[41].call(customizer, "warn", $getCallSiteArray[42].callGetProperty(Level.class), "WARN");
        $getCallSiteArray[43].call(customizer, "info", $getCallSiteArray[44].callGetProperty(Level.class), "INFO");
        $getCallSiteArray[45].call(customizer, "debug", $getCallSiteArray[46].callGetProperty(Level.class), "DEBUG");
        $getCallSiteArray[47].call(customizer, "trace", $getCallSiteArray[48].callGetProperty(Level.class), "TRACE");
        $getCallSiteArray[49].call(customizer, "all", $getCallSiteArray[50].callGetProperty(Level.class), "ALL");
        return (ImportCustomizer) ScriptBytecodeAdapter.castToType(customizer, ImportCustomizer.class);
    }
}