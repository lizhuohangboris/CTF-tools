package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/EvaluatorAction.class */
public class EvaluatorAction extends AbstractEventEvaluatorAction {
    @Override // ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction
    protected String defaultClassName() {
        return JaninoEventEvaluator.class.getName();
    }
}