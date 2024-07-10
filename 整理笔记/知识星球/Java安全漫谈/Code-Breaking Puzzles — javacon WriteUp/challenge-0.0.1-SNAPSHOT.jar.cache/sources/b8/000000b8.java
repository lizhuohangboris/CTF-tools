package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import java.util.HashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/LifeCycleManager.class */
public class LifeCycleManager {
    private final Set<LifeCycle> components = new HashSet();

    public void register(LifeCycle component) {
        this.components.add(component);
    }

    public void reset() {
        for (LifeCycle component : this.components) {
            if (component.isStarted()) {
                component.stop();
            }
        }
        this.components.clear();
    }
}