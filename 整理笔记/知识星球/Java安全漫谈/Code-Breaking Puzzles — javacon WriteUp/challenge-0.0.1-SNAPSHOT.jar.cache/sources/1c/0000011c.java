package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareImpl;
import org.xml.sax.Locator;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: Interpreter.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/CAI_WithLocatorSupport.class */
public class CAI_WithLocatorSupport extends ContextAwareImpl {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CAI_WithLocatorSupport(Context context, Interpreter interpreter) {
        super(context, interpreter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.spi.ContextAwareImpl
    public Object getOrigin() {
        Interpreter i = (Interpreter) super.getOrigin();
        Locator locator = i.locator;
        if (locator != null) {
            return Interpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return Interpreter.class.getName() + "@NA:NA";
    }
}