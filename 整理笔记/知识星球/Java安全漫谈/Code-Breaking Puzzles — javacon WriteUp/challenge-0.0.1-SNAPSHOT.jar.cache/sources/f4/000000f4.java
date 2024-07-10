package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/ContextPropertyAction.class */
public class ContextPropertyAction extends Action {
    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
        addError("The [contextProperty] element has been removed. Please use [substitutionProperty] element instead");
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) throws ActionException {
    }
}