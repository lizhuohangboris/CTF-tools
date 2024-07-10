package ch.qos.logback.core.rolling.helper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/PeriodicityType.class */
public enum PeriodicityType {
    ERRONEOUS,
    TOP_OF_MILLISECOND,
    TOP_OF_SECOND,
    TOP_OF_MINUTE,
    TOP_OF_HOUR,
    HALF_DAY,
    TOP_OF_DAY,
    TOP_OF_WEEK,
    TOP_OF_MONTH;
    
    static PeriodicityType[] VALID_ORDERED_LIST = {TOP_OF_MILLISECOND, TOP_OF_SECOND, TOP_OF_MINUTE, TOP_OF_HOUR, TOP_OF_DAY, TOP_OF_WEEK, TOP_OF_MONTH};
}