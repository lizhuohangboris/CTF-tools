package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/conditional/IfAction.class */
public class IfAction extends Action {
    private static final String CONDITION_ATTR = "condition";
    public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
    public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
    Stack<IfState> stack = new Stack<>();

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        IfState state = new IfState();
        boolean emptyStack = this.stack.isEmpty();
        this.stack.push(state);
        if (!emptyStack) {
            return;
        }
        ic.pushObject(this);
        if (!EnvUtil.isJaninoAvailable()) {
            addError(MISSING_JANINO_MSG);
            addError(MISSING_JANINO_SEE);
            return;
        }
        state.active = true;
        Condition condition = null;
        String conditionAttribute = attributes.getValue(CONDITION_ATTR);
        if (!OptionHelper.isEmpty(conditionAttribute)) {
            String conditionAttribute2 = OptionHelper.substVars(conditionAttribute, ic, this.context);
            PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(ic);
            pesb.setContext(this.context);
            try {
                condition = pesb.build(conditionAttribute2);
            } catch (Exception e) {
                addError("Failed to parse condition [" + conditionAttribute2 + "]", e);
            }
            if (condition != null) {
                state.boolResult = Boolean.valueOf(condition.evaluate());
            }
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ic, String name) throws ActionException {
        IfState state = this.stack.pop();
        if (!state.active) {
            return;
        }
        Object o = ic.peekObject();
        if (o == null) {
            throw new IllegalStateException("Unexpected null object on stack");
        }
        if (!(o instanceof IfAction)) {
            throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
        }
        if (o != this) {
            throw new IllegalStateException("IfAction different then current one on stack");
        }
        ic.popObject();
        if (state.boolResult == null) {
            addError("Failed to determine \"if then else\" result");
            return;
        }
        Interpreter interpreter = ic.getJoranInterpreter();
        List<SaxEvent> listToPlay = state.thenSaxEventList;
        if (!state.boolResult.booleanValue()) {
            listToPlay = state.elseSaxEventList;
        }
        if (listToPlay != null) {
            interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
        }
    }

    public void setThenSaxEventList(List<SaxEvent> thenSaxEventList) {
        IfState state = this.stack.firstElement();
        if (state.active) {
            state.thenSaxEventList = thenSaxEventList;
            return;
        }
        throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
    }

    public void setElseSaxEventList(List<SaxEvent> elseSaxEventList) {
        IfState state = this.stack.firstElement();
        if (state.active) {
            state.elseSaxEventList = elseSaxEventList;
            return;
        }
        throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
    }

    public boolean isActive() {
        if (this.stack == null || this.stack.isEmpty()) {
            return false;
        }
        return this.stack.peek().active;
    }
}