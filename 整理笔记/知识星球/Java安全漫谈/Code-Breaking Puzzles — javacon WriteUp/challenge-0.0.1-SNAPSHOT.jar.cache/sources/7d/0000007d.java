package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.sift.AbstractDiscriminator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/sift/JNDIBasedContextDiscriminator.class */
public class JNDIBasedContextDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
    private static final String KEY = "contextName";
    private String defaultValue;

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getDiscriminatingValue(ILoggingEvent event) {
        ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        if (selector == null) {
            return this.defaultValue;
        }
        LoggerContext lc = selector.getLoggerContext();
        if (lc == null) {
            return this.defaultValue;
        }
        return lc.getName();
    }

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getKey() {
        return KEY;
    }

    public void setKey(String key) {
        throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}