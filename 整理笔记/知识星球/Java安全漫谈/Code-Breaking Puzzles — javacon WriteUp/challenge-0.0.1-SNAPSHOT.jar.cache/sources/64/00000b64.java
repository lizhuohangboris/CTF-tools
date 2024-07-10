package org.apache.logging.log4j.spi;

import java.util.EnumSet;
import java.util.Iterator;
import org.thymeleaf.standard.processor.StandardWithTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/StandardLevel.class */
public enum StandardLevel {
    OFF(0),
    FATAL(100),
    ERROR(200),
    WARN(300),
    INFO(400),
    DEBUG(500),
    TRACE(StandardWithTagProcessor.PRECEDENCE),
    ALL(Integer.MAX_VALUE);
    
    private static final EnumSet<StandardLevel> LEVELSET = EnumSet.allOf(StandardLevel.class);
    private final int intLevel;

    StandardLevel(int val) {
        this.intLevel = val;
    }

    public int intLevel() {
        return this.intLevel;
    }

    public static StandardLevel getStandardLevel(int intLevel) {
        StandardLevel level = OFF;
        Iterator i$ = LEVELSET.iterator();
        while (i$.hasNext()) {
            StandardLevel lvl = (StandardLevel) i$.next();
            if (lvl.intLevel() > intLevel) {
                break;
            }
            level = lvl;
        }
        return level;
    }
}