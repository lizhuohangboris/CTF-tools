package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/ConversionRuleAction.class */
public class ConversionRuleAction extends Action {
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        this.inError = false;
        String conversionWord = attributes.getValue(ActionConst.CONVERSION_WORD_ATTRIBUTE);
        String converterClass = attributes.getValue(ActionConst.CONVERTER_CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(conversionWord)) {
            this.inError = true;
            addError("No 'conversionWord' attribute in <conversionRule>");
        } else if (OptionHelper.isEmpty(converterClass)) {
            this.inError = true;
            ec.addError("No 'converterClass' attribute in <conversionRule>");
        } else {
            try {
                Map<String, String> ruleRegistry = (Map) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
                if (ruleRegistry == null) {
                    ruleRegistry = new HashMap<>();
                    this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
                }
                addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
                ruleRegistry.put(conversionWord, converterClass);
            } catch (Exception e) {
                this.inError = true;
                addError("Could not add conversion rule to PatternLayout.");
            }
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String n) {
    }

    public void finish(InterpretationContext ec) {
    }
}