package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/Action.class */
public abstract class Action extends ContextAwareBase {
    public static final String NAME_ATTRIBUTE = "name";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String FILE_ATTRIBUTE = "file";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String PATTERN_ATTRIBUTE = "pattern";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

    public abstract void begin(InterpretationContext interpretationContext, String str, Attributes attributes) throws ActionException;

    public abstract void end(InterpretationContext interpretationContext, String str) throws ActionException;

    public void body(InterpretationContext ic, String body) throws ActionException {
    }

    public String toString() {
        return getClass().getName();
    }

    protected int getColumnNumber(InterpretationContext ic) {
        Interpreter ji = ic.getJoranInterpreter();
        Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getColumnNumber();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getLineNumber(InterpretationContext ic) {
        Interpreter ji = ic.getJoranInterpreter();
        Locator locator = ji.getLocator();
        if (locator != null) {
            return locator.getLineNumber();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getLineColStr(InterpretationContext ic) {
        return "line: " + getLineNumber(ic) + ", column: " + getColumnNumber(ic);
    }
}