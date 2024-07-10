package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/DefinePropertyAction.class */
public class DefinePropertyAction extends Action {
    String scopeStr;
    ActionUtil.Scope scope;
    String propertyName;
    PropertyDefiner definer;
    boolean inError;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String localName, Attributes attributes) throws ActionException {
        this.scopeStr = null;
        this.scope = null;
        this.propertyName = null;
        this.definer = null;
        this.inError = false;
        this.propertyName = attributes.getValue("name");
        this.scopeStr = attributes.getValue("scope");
        this.scope = ActionUtil.stringToScope(this.scopeStr);
        if (OptionHelper.isEmpty(this.propertyName)) {
            addError("Missing property name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
            this.inError = true;
            return;
        }
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            addError("Missing class name for property definer. Near [" + localName + "] line " + getLineNumber(ec));
            this.inError = true;
            return;
        }
        try {
            addInfo("About to instantiate property definer of type [" + className + "]");
            this.definer = (PropertyDefiner) OptionHelper.instantiateByClassName(className, PropertyDefiner.class, this.context);
            this.definer.setContext(this.context);
            if (this.definer instanceof LifeCycle) {
                ((LifeCycle) this.definer).start();
            }
            ec.pushObject(this.definer);
        } catch (Exception oops) {
            this.inError = true;
            addError("Could not create an PropertyDefiner of type [" + className + "].", oops);
            throw new ActionException(oops);
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
        if (this.inError) {
            return;
        }
        Object o = ec.peekObject();
        if (o != this.definer) {
            addWarn("The object at the of the stack is not the property definer for property named [" + this.propertyName + "] pushed earlier.");
            return;
        }
        addInfo("Popping property definer for property named [" + this.propertyName + "] from the object stack");
        ec.popObject();
        String propertyValue = this.definer.getPropertyValue();
        if (propertyValue != null) {
            ActionUtil.setProperty(ec, this.propertyName, propertyValue, this.scope);
        }
    }
}