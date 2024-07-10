package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/conditional/ThenOrElseActionBase.class */
public abstract class ThenOrElseActionBase extends Action {
    Stack<ThenActionState> stateStack = new Stack<>();

    abstract void registerEventList(IfAction ifAction, List<SaxEvent> list);

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        if (!weAreActive(ic)) {
            return;
        }
        ThenActionState state = new ThenActionState();
        if (ic.isListenerListEmpty()) {
            ic.addInPlayListener(state);
            state.isRegistered = true;
        }
        this.stateStack.push(state);
    }

    boolean weAreActive(InterpretationContext ic) {
        Object o = ic.peekObject();
        if (!(o instanceof IfAction)) {
            return false;
        }
        IfAction ifAction = (IfAction) o;
        return ifAction.isActive();
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (!weAreActive(ic)) {
            return;
        }
        ThenActionState state = this.stateStack.pop();
        if (state.isRegistered) {
            ic.removeInPlayListener(state);
            Object o = ic.peekObject();
            if (o instanceof IfAction) {
                IfAction ifAction = (IfAction) o;
                removeFirstAndLastFromList(state.eventList);
                registerEventList(ifAction, state.eventList);
                return;
            }
            throw new IllegalStateException("Missing IfAction on top of stack");
        }
    }

    void removeFirstAndLastFromList(List<SaxEvent> eventList) {
        eventList.remove(0);
        eventList.remove(eventList.size() - 1);
    }
}