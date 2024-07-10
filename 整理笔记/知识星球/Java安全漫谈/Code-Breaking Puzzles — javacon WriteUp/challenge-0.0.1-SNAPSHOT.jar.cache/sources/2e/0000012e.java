package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.status.StatusManager;
import java.net.URL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/XMLUtil.class */
public class XMLUtil {
    public static final int ILL_FORMED = 1;
    public static final int UNRECOVERABLE_ERROR = 2;

    public static int checkIfWellFormed(URL url, StatusManager sm) {
        return 0;
    }
}