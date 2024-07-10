package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/RootLoggerAction.class */
public class RootLoggerAction extends Action {
    Logger root;
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        this.inError = false;
        LoggerContext loggerContext = (LoggerContext) this.context;
        this.root = loggerContext.getLogger("ROOT");
        String levelStr = ec.subst(attributes.getValue("level"));
        if (!OptionHelper.isEmpty(levelStr)) {
            Level level = Level.toLevel(levelStr);
            addInfo("Setting level of ROOT logger to " + level);
            this.root.setLevel(level);
        }
        ec.pushObject(this.root);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
        if (this.inError) {
            return;
        }
        Object o = ec.peekObject();
        if (o != this.root) {
            addWarn("The object on the top the of the stack is not the root logger");
            addWarn("It is: " + o);
            return;
        }
        ec.popObject();
    }

    public void finish(InterpretationContext ec) {
    }
}