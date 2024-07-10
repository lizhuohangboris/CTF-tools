package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.joran.action.Action;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/RuleStore.class */
public interface RuleStore {
    void addRule(ElementSelector elementSelector, String str) throws ClassNotFoundException;

    void addRule(ElementSelector elementSelector, Action action);

    List<Action> matchActions(ElementPath elementPath);
}