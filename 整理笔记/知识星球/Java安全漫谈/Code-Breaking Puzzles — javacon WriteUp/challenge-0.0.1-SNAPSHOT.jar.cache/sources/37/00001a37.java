package org.springframework.boot.logging.logback;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.core.env.Environment;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/logback/SpringPropertyAction.class */
class SpringPropertyAction extends Action {
    private static final String SOURCE_ATTRIBUTE = "source";
    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";
    private final Environment environment;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringPropertyAction(Environment environment) {
        this.environment = environment;
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext context, String elementName, Attributes attributes) throws ActionException {
        String name = attributes.getValue("name");
        String source = attributes.getValue(SOURCE_ATTRIBUTE);
        ActionUtil.Scope scope = ActionUtil.stringToScope(attributes.getValue("scope"));
        String defaultValue = attributes.getValue(DEFAULT_VALUE_ATTRIBUTE);
        if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
            addError("The \"name\" and \"source\" attributes of <springProperty> must be set");
        }
        ActionUtil.setProperty(context, name, getValue(source, defaultValue), scope);
    }

    private String getValue(String source, String defaultValue) {
        if (this.environment == null) {
            addWarn("No Spring Environment available to resolve " + source);
            return defaultValue;
        }
        String value = this.environment.getProperty(source);
        if (value != null) {
            return value;
        }
        int lastDot = source.lastIndexOf(46);
        if (lastDot > 0) {
            String prefix = source.substring(0, lastDot + 1);
            return this.environment.getProperty(prefix + source.substring(lastDot + 1), defaultValue);
        }
        return defaultValue;
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext context, String name) throws ActionException {
    }
}