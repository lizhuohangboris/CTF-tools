package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/turbo/MDCFilter.class */
public class MDCFilter extends MatchingFilter {
    String MDCKey;
    String value;

    @Override // ch.qos.logback.classic.turbo.TurboFilter
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (this.MDCKey == null) {
            return FilterReply.NEUTRAL;
        }
        String value = MDC.get(this.MDCKey);
        if (this.value.equals(value)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMDCKey(String MDCKey) {
        this.MDCKey = MDCKey;
    }
}