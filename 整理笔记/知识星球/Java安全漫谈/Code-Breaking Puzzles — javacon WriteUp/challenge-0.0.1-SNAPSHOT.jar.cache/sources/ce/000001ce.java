package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.GenericConfigurator;
import ch.qos.logback.core.joran.action.DefinePropertyAction;
import ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import ch.qos.logback.core.joran.action.PropertyAction;
import ch.qos.logback.core.joran.action.TimestampAction;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.Interpreter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.spi.RuleStore;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/sift/SiftingJoranConfiguratorBase.class */
public abstract class SiftingJoranConfiguratorBase<E> extends GenericConfigurator {
    protected final String key;
    protected final String value;
    protected final Map<String, String> parentPropertyMap;
    static final String ONE_AND_ONLY_ONE_URL = "http://logback.qos.ch/codes.html#1andOnly1";
    int errorEmmissionCount = 0;

    public abstract Appender<E> getAppender();

    /* JADX INFO: Access modifiers changed from: protected */
    public SiftingJoranConfiguratorBase(String key, String value, Map<String, String> parentPropertyMap) {
        this.key = key;
        this.value = value;
        this.parentPropertyMap = parentPropertyMap;
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    protected void addImplicitRules(Interpreter interpreter) {
        NestedComplexPropertyIA nestedComplexIA = new NestedComplexPropertyIA(getBeanDescriptionCache());
        nestedComplexIA.setContext(this.context);
        interpreter.addImplicitAction(nestedComplexIA);
        NestedBasicPropertyIA nestedSimpleIA = new NestedBasicPropertyIA(getBeanDescriptionCache());
        nestedSimpleIA.setContext(this.context);
        interpreter.addImplicitAction(nestedSimpleIA);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.joran.GenericConfigurator
    public void addInstanceRules(RuleStore rs) {
        rs.addRule(new ElementSelector("configuration/property"), new PropertyAction());
        rs.addRule(new ElementSelector("configuration/timestamp"), new TimestampAction());
        rs.addRule(new ElementSelector("configuration/define"), new DefinePropertyAction());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void oneAndOnlyOneCheck(Map<?, ?> appenderMap) {
        String errMsg = null;
        if (appenderMap.size() == 0) {
            this.errorEmmissionCount++;
            errMsg = "No nested appenders found within the <sift> element in SiftingAppender.";
        } else if (appenderMap.size() > 1) {
            this.errorEmmissionCount++;
            errMsg = "Only and only one appender can be nested the <sift> element in SiftingAppender. See also http://logback.qos.ch/codes.html#1andOnly1";
        }
        if (errMsg != null && this.errorEmmissionCount < 4) {
            addError(errMsg);
        }
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    public void doConfigure(List<SaxEvent> eventList) throws JoranException {
        super.doConfigure(eventList);
    }

    public String toString() {
        return getClass().getName() + "{" + this.key + "=" + this.value + '}';
    }
}