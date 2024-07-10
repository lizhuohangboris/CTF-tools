package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Properties;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/ActionUtil.class */
public class ActionUtil {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/ActionUtil$Scope.class */
    public enum Scope {
        LOCAL,
        CONTEXT,
        SYSTEM
    }

    public static Scope stringToScope(String scopeStr) {
        if (Scope.SYSTEM.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.SYSTEM;
        }
        if (Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr)) {
            return Scope.CONTEXT;
        }
        return Scope.LOCAL;
    }

    public static void setProperty(InterpretationContext ic, String key, String value, Scope scope) {
        switch (scope) {
            case LOCAL:
                ic.addSubstitutionProperty(key, value);
                return;
            case CONTEXT:
                ic.getContext().putProperty(key, value);
                return;
            case SYSTEM:
                OptionHelper.setSystemProperty(ic, key, value);
                return;
            default:
                return;
        }
    }

    public static void setProperties(InterpretationContext ic, Properties props, Scope scope) {
        switch (scope) {
            case LOCAL:
                ic.addSubstitutionProperties(props);
                return;
            case CONTEXT:
                ContextUtil cu = new ContextUtil(ic.getContext());
                cu.addProperties(props);
                return;
            case SYSTEM:
                OptionHelper.setSystemProperties(ic, props);
                return;
            default:
                return;
        }
    }
}