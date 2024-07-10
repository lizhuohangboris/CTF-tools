package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/AppenderRefAction.class */
public class AppenderRefAction<E> extends Action {
    boolean inError = false;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
        this.inError = false;
        Object o = ec.peekObject();
        if (!(o instanceof AppenderAttachable)) {
            String errMsg = "Could not find an AppenderAttachable at the top of execution stack. Near [" + tagName + "] line " + getLineNumber(ec);
            this.inError = true;
            addError(errMsg);
            return;
        }
        AppenderAttachable<E> appenderAttachable = (AppenderAttachable) o;
        String appenderName = ec.subst(attributes.getValue("ref"));
        if (OptionHelper.isEmpty(appenderName)) {
            this.inError = true;
            addError("Missing appender ref attribute in <appender-ref> tag.");
            return;
        }
        HashMap<String, Appender<E>> appenderBag = (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
        Appender<E> appender = appenderBag.get(appenderName);
        if (appender == null) {
            String msg = "Could not find an appender named [" + appenderName + "]. Did you define it below instead of above in the configuration file?";
            this.inError = true;
            addError(msg);
            addError("See http://logback.qos.ch/codes.html#appender_order for more details.");
            return;
        }
        addInfo("Attaching appender named [" + appenderName + "] to " + appenderAttachable);
        appenderAttachable.addAppender(appender);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String n) {
    }
}