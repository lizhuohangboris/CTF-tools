package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/AbstractEventEvaluatorAction.class */
public abstract class AbstractEventEvaluatorAction extends Action {
    EventEvaluator<?> evaluator;
    boolean inError = false;

    protected abstract String defaultClassName();

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        this.inError = false;
        this.evaluator = null;
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            className = defaultClassName();
            addInfo("Assuming default evaluator class [" + className + "]");
        }
        if (OptionHelper.isEmpty(className)) {
            defaultClassName();
            this.inError = true;
            addError("Mandatory \"class\" attribute not set for <evaluator>");
            return;
        }
        String evaluatorName = attributes.getValue("name");
        if (OptionHelper.isEmpty(evaluatorName)) {
            this.inError = true;
            addError("Mandatory \"name\" attribute not set for <evaluator>");
            return;
        }
        try {
            this.evaluator = (EventEvaluator) OptionHelper.instantiateByClassName(className, EventEvaluator.class, this.context);
            this.evaluator.setContext(this.context);
            this.evaluator.setName(evaluatorName);
            ec.pushObject(this.evaluator);
            addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create evaluator of type " + className + "].", oops);
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String e) {
        if (this.inError) {
            return;
        }
        if (this.evaluator instanceof LifeCycle) {
            this.evaluator.start();
            addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
        }
        Object o = ec.peekObject();
        if (o != this.evaluator) {
            addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
            return;
        }
        ec.popObject();
        try {
            Map<String, EventEvaluator<?>> evaluatorMap = (Map) this.context.getObject(CoreConstants.EVALUATOR_MAP);
            if (evaluatorMap == null) {
                addError("Could not find EvaluatorMap");
            } else {
                evaluatorMap.put(this.evaluator.getName(), this.evaluator);
            }
        } catch (Exception ex) {
            addError("Could not set evaluator named [" + this.evaluator + "].", ex);
        }
    }

    public void finish(InterpretationContext ec) {
    }
}