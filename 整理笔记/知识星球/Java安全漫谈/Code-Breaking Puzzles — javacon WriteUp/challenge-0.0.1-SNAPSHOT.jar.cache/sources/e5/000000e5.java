package ch.qos.logback.core.hook;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/hook/ShutdownHookBase.class */
public abstract class ShutdownHookBase extends ContextAwareBase implements ShutdownHook {
    /* JADX INFO: Access modifiers changed from: protected */
    public void stop() {
        addInfo("Logback context being closed via shutdown hook");
        Context hookContext = getContext();
        if (hookContext instanceof ContextBase) {
            ContextBase context = (ContextBase) hookContext;
            context.stop();
        }
    }
}