package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/LoggerAction.class */
public class LoggerAction extends Action {
    public static final String LEVEL_ATTRIBUTE = "level";
    boolean inError = false;
    Logger logger;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        this.inError = false;
        this.logger = null;
        LoggerContext loggerContext = (LoggerContext) this.context;
        String loggerName = ec.subst(attributes.getValue("name"));
        if (OptionHelper.isEmpty(loggerName)) {
            this.inError = true;
            String aroundLine = getLineColStr(ec);
            String errorMsg = "No 'name' attribute in element " + name + ", around " + aroundLine;
            addError(errorMsg);
            return;
        }
        this.logger = loggerContext.getLogger(loggerName);
        String levelStr = ec.subst(attributes.getValue("level"));
        if (!OptionHelper.isEmpty(levelStr)) {
            if (ActionConst.INHERITED.equalsIgnoreCase(levelStr) || ActionConst.NULL.equalsIgnoreCase(levelStr)) {
                addInfo("Setting level of logger [" + loggerName + "] to null, i.e. INHERITED");
                this.logger.setLevel(null);
            } else {
                Level level = Level.toLevel(levelStr);
                addInfo("Setting level of logger [" + loggerName + "] to " + level);
                this.logger.setLevel(level);
            }
        }
        String additivityStr = ec.subst(attributes.getValue(ActionConst.ADDITIVITY_ATTRIBUTE));
        if (!OptionHelper.isEmpty(additivityStr)) {
            boolean additive = OptionHelper.toBoolean(additivityStr, true);
            addInfo("Setting additivity of logger [" + loggerName + "] to " + additive);
            this.logger.setAdditive(additive);
        }
        ec.pushObject(this.logger);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String e) {
        if (this.inError) {
            return;
        }
        Object o = ec.peekObject();
        if (o != this.logger) {
            addWarn("The object on the top the of the stack is not " + this.logger + " pushed earlier");
            addWarn("It is: " + o);
            return;
        }
        ec.popObject();
    }

    public void finish(InterpretationContext ec) {
    }
}