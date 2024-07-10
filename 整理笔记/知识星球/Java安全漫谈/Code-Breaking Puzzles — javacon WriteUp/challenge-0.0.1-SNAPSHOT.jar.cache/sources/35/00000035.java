package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.util.JNDIUtil;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import javax.naming.Context;
import javax.naming.NamingException;
import org.springframework.beans.PropertyAccessor;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/InsertFromJNDIAction.class */
public class InsertFromJNDIAction extends Action {
    public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
    public static final String AS_ATTR = "as";

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) {
        int errorCount = 0;
        String envEntryName = ec.subst(attributes.getValue(ENV_ENTRY_NAME_ATTR));
        String asKey = ec.subst(attributes.getValue(AS_ATTR));
        String scopeStr = attributes.getValue("scope");
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (OptionHelper.isEmpty(envEntryName)) {
            String lineColStr = getLineColStr(ec);
            addError("[env-entry-name] missing, around " + lineColStr);
            errorCount = 0 + 1;
        }
        if (OptionHelper.isEmpty(asKey)) {
            String lineColStr2 = getLineColStr(ec);
            addError("[as] missing, around " + lineColStr2);
            errorCount++;
        }
        if (errorCount != 0) {
            return;
        }
        try {
            Context ctx = JNDIUtil.getInitialContext();
            String envEntryValue = JNDIUtil.lookup(ctx, envEntryName);
            if (OptionHelper.isEmpty(envEntryValue)) {
                addError(PropertyAccessor.PROPERTY_KEY_PREFIX + envEntryName + "] has null or empty value");
            } else {
                addInfo("Setting variable [" + asKey + "] to [" + envEntryValue + "] in [" + scope + "] scope");
                ActionUtil.setProperty(ec, asKey, envEntryValue, scope);
            }
        } catch (NamingException e) {
            addError("Failed to lookup JNDI env-entry [" + envEntryName + "]");
        }
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
    }
}