package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.hook.ShutdownHookBase;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/ShutdownHookAction.class */
public class ShutdownHookAction extends Action {
    ShutdownHookBase hook;
    private boolean inError;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        this.hook = null;
        this.inError = false;
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            addError("Missing class name for shutdown hook. Near [" + name + "] line " + getLineNumber(ic));
            this.inError = true;
            return;
        }
        try {
            addInfo("About to instantiate shutdown hook of type [" + className + "]");
            this.hook = (ShutdownHookBase) OptionHelper.instantiateByClassName(className, ShutdownHookBase.class, this.context);
            this.hook.setContext(this.context);
            ic.pushObject(this.hook);
        } catch (Exception e) {
            this.inError = true;
            addError("Could not create a shutdown hook of type [" + className + "].", e);
            throw new ActionException(e);
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (this.inError) {
            return;
        }
        Object o = ic.peekObject();
        if (o != this.hook) {
            addWarn("The object at the of the stack is not the hook pushed earlier.");
            return;
        }
        ic.popObject();
        Thread hookThread = new Thread(this.hook, "Logback shutdown hook [" + this.context.getName() + "]");
        this.context.putObject(CoreConstants.SHUTDOWN_HOOK_THREAD, hookThread);
        Runtime.getRuntime().addShutdownHook(hookThread);
    }
}