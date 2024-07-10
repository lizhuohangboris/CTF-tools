package ch.qos.logback.classic.db;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/db/DBHelper.class */
public class DBHelper {
    public static final short PROPERTIES_EXIST = 1;
    public static final short EXCEPTION_EXISTS = 2;

    public static short computeReferenceMask(ILoggingEvent event) {
        short mask = 0;
        int mdcPropSize = 0;
        if (event.getMDCPropertyMap() != null) {
            mdcPropSize = event.getMDCPropertyMap().keySet().size();
        }
        int contextPropSize = 0;
        if (event.getLoggerContextVO().getPropertyMap() != null) {
            contextPropSize = event.getLoggerContextVO().getPropertyMap().size();
        }
        if (mdcPropSize > 0 || contextPropSize > 0) {
            mask = 1;
        }
        if (event.getThrowableProxy() != null) {
            mask = (short) (mask | 2);
        }
        return mask;
    }
}