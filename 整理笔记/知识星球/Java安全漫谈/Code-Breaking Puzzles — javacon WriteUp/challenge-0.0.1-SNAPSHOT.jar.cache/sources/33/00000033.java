package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/ContextNameAction.class */
public class ContextNameAction extends Action {
    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void body(InterpretationContext ec, String body) {
        String finalBody = ec.subst(body);
        addInfo("Setting logger context name as [" + finalBody + "]");
        try {
            this.context.setName(finalBody);
        } catch (IllegalStateException e) {
            addError("Failed to rename context [" + this.context.getName() + "] as [" + finalBody + "]", e);
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
    }
}