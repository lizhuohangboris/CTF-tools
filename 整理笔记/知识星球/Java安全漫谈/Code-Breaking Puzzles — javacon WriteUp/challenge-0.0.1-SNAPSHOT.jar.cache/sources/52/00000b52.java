package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/ExtendedLogger.class */
public interface ExtendedLogger extends Logger {
    boolean isEnabled(Level level, Marker marker, Message message, Throwable th);

    boolean isEnabled(Level level, Marker marker, CharSequence charSequence, Throwable th);

    boolean isEnabled(Level level, Marker marker, Object obj, Throwable th);

    boolean isEnabled(Level level, Marker marker, String str, Throwable th);

    boolean isEnabled(Level level, Marker marker, String str);

    boolean isEnabled(Level level, Marker marker, String str, Object... objArr);

    boolean isEnabled(Level level, Marker marker, String str, Object obj);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    boolean isEnabled(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void logIfEnabled(String str, Level level, Marker marker, Message message, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, CharSequence charSequence, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, Object obj, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, String str2);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object... objArr);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void logMessage(String str, Level level, Marker marker, Message message, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, MessageSupplier messageSupplier, Throwable th);

    void logIfEnabled(String str, Level level, Marker marker, String str2, Supplier<?>... supplierArr);

    void logIfEnabled(String str, Level level, Marker marker, Supplier<?> supplier, Throwable th);
}