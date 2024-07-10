package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/LevelAction.class */
public class LevelAction extends Action {
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        Object o = ec.peekObject();
        if (!(o instanceof Logger)) {
            this.inError = true;
            addError("For element <level>, could not find a logger at the top of execution stack.");
            return;
        }
        Logger l = (Logger) o;
        String loggerName = l.getName();
        String levelStr = ec.subst(attributes.getValue("value"));
        if (ActionConst.INHERITED.equalsIgnoreCase(levelStr) || ActionConst.NULL.equalsIgnoreCase(levelStr)) {
            l.setLevel(null);
        } else {
            l.setLevel(Level.toLevel(levelStr, Level.DEBUG));
        }
        addInfo(loggerName + " level set to " + l.getLevel());
    }

    public void finish(InterpretationContext ec) {
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String e) {
    }
}