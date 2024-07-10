package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.sift.SiftingAppenderBase;
import org.slf4j.Marker;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/sift/SiftingAppender.class */
public class SiftingAppender extends SiftingAppenderBase<ILoggingEvent> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    public long getTimestamp(ILoggingEvent event) {
        return event.getTimeStamp();
    }

    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    @DefaultClass(MDCBasedDiscriminator.class)
    public void setDiscriminator(Discriminator<ILoggingEvent> discriminator) {
        super.setDiscriminator(discriminator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.sift.SiftingAppenderBase
    public boolean eventMarksEndOfLife(ILoggingEvent event) {
        Marker marker = event.getMarker();
        if (marker == null) {
            return false;
        }
        return marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
    }
}