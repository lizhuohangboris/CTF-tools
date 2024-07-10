package org.apache.logging.log4j;

import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/Logger.class */
public interface Logger {
    void catching(Level level, Throwable th);

    void catching(Throwable th);

    void debug(Marker marker, Message message);

    void debug(Marker marker, Message message, Throwable th);

    void debug(Marker marker, MessageSupplier messageSupplier);

    void debug(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void debug(Marker marker, CharSequence charSequence);

    void debug(Marker marker, CharSequence charSequence, Throwable th);

    void debug(Marker marker, Object obj);

    void debug(Marker marker, Object obj, Throwable th);

    void debug(Marker marker, String str);

    void debug(Marker marker, String str, Object... objArr);

    void debug(Marker marker, String str, Supplier<?>... supplierArr);

    void debug(Marker marker, String str, Throwable th);

    void debug(Marker marker, Supplier<?> supplier);

    void debug(Marker marker, Supplier<?> supplier, Throwable th);

    void debug(Message message);

    void debug(Message message, Throwable th);

    void debug(MessageSupplier messageSupplier);

    void debug(MessageSupplier messageSupplier, Throwable th);

    void debug(CharSequence charSequence);

    void debug(CharSequence charSequence, Throwable th);

    void debug(Object obj);

    void debug(Object obj, Throwable th);

    void debug(String str);

    void debug(String str, Object... objArr);

    void debug(String str, Supplier<?>... supplierArr);

    void debug(String str, Throwable th);

    void debug(Supplier<?> supplier);

    void debug(Supplier<?> supplier, Throwable th);

    void debug(Marker marker, String str, Object obj);

    void debug(Marker marker, String str, Object obj, Object obj2);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void debug(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void debug(String str, Object obj);

    void debug(String str, Object obj, Object obj2);

    void debug(String str, Object obj, Object obj2, Object obj3);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void debug(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    @Deprecated
    void entry();

    void entry(Object... objArr);

    void error(Marker marker, Message message);

    void error(Marker marker, Message message, Throwable th);

    void error(Marker marker, MessageSupplier messageSupplier);

    void error(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void error(Marker marker, CharSequence charSequence);

    void error(Marker marker, CharSequence charSequence, Throwable th);

    void error(Marker marker, Object obj);

    void error(Marker marker, Object obj, Throwable th);

    void error(Marker marker, String str);

    void error(Marker marker, String str, Object... objArr);

    void error(Marker marker, String str, Supplier<?>... supplierArr);

    void error(Marker marker, String str, Throwable th);

    void error(Marker marker, Supplier<?> supplier);

    void error(Marker marker, Supplier<?> supplier, Throwable th);

    void error(Message message);

    void error(Message message, Throwable th);

    void error(MessageSupplier messageSupplier);

    void error(MessageSupplier messageSupplier, Throwable th);

    void error(CharSequence charSequence);

    void error(CharSequence charSequence, Throwable th);

    void error(Object obj);

    void error(Object obj, Throwable th);

    void error(String str);

    void error(String str, Object... objArr);

    void error(String str, Supplier<?>... supplierArr);

    void error(String str, Throwable th);

    void error(Supplier<?> supplier);

    void error(Supplier<?> supplier, Throwable th);

    void error(Marker marker, String str, Object obj);

    void error(Marker marker, String str, Object obj, Object obj2);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void error(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void error(String str, Object obj);

    void error(String str, Object obj, Object obj2);

    void error(String str, Object obj, Object obj2, Object obj3);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void error(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    @Deprecated
    void exit();

    @Deprecated
    <R> R exit(R r);

    void fatal(Marker marker, Message message);

    void fatal(Marker marker, Message message, Throwable th);

    void fatal(Marker marker, MessageSupplier messageSupplier);

    void fatal(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void fatal(Marker marker, CharSequence charSequence);

    void fatal(Marker marker, CharSequence charSequence, Throwable th);

    void fatal(Marker marker, Object obj);

    void fatal(Marker marker, Object obj, Throwable th);

    void fatal(Marker marker, String str);

    void fatal(Marker marker, String str, Object... objArr);

    void fatal(Marker marker, String str, Supplier<?>... supplierArr);

    void fatal(Marker marker, String str, Throwable th);

    void fatal(Marker marker, Supplier<?> supplier);

    void fatal(Marker marker, Supplier<?> supplier, Throwable th);

    void fatal(Message message);

    void fatal(Message message, Throwable th);

    void fatal(MessageSupplier messageSupplier);

    void fatal(MessageSupplier messageSupplier, Throwable th);

    void fatal(CharSequence charSequence);

    void fatal(CharSequence charSequence, Throwable th);

    void fatal(Object obj);

    void fatal(Object obj, Throwable th);

    void fatal(String str);

    void fatal(String str, Object... objArr);

    void fatal(String str, Supplier<?>... supplierArr);

    void fatal(String str, Throwable th);

    void fatal(Supplier<?> supplier);

    void fatal(Supplier<?> supplier, Throwable th);

    void fatal(Marker marker, String str, Object obj);

    void fatal(Marker marker, String str, Object obj, Object obj2);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void fatal(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void fatal(String str, Object obj);

    void fatal(String str, Object obj, Object obj2);

    void fatal(String str, Object obj, Object obj2, Object obj3);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void fatal(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    Level getLevel();

    <MF extends MessageFactory> MF getMessageFactory();

    String getName();

    void info(Marker marker, Message message);

    void info(Marker marker, Message message, Throwable th);

    void info(Marker marker, MessageSupplier messageSupplier);

    void info(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void info(Marker marker, CharSequence charSequence);

    void info(Marker marker, CharSequence charSequence, Throwable th);

    void info(Marker marker, Object obj);

    void info(Marker marker, Object obj, Throwable th);

    void info(Marker marker, String str);

    void info(Marker marker, String str, Object... objArr);

    void info(Marker marker, String str, Supplier<?>... supplierArr);

    void info(Marker marker, String str, Throwable th);

    void info(Marker marker, Supplier<?> supplier);

    void info(Marker marker, Supplier<?> supplier, Throwable th);

    void info(Message message);

    void info(Message message, Throwable th);

    void info(MessageSupplier messageSupplier);

    void info(MessageSupplier messageSupplier, Throwable th);

    void info(CharSequence charSequence);

    void info(CharSequence charSequence, Throwable th);

    void info(Object obj);

    void info(Object obj, Throwable th);

    void info(String str);

    void info(String str, Object... objArr);

    void info(String str, Supplier<?>... supplierArr);

    void info(String str, Throwable th);

    void info(Supplier<?> supplier);

    void info(Supplier<?> supplier, Throwable th);

    void info(Marker marker, String str, Object obj);

    void info(Marker marker, String str, Object obj, Object obj2);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void info(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void info(String str, Object obj);

    void info(String str, Object obj, Object obj2);

    void info(String str, Object obj, Object obj2, Object obj3);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void info(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    boolean isDebugEnabled();

    boolean isDebugEnabled(Marker marker);

    boolean isEnabled(Level level);

    boolean isEnabled(Level level, Marker marker);

    boolean isErrorEnabled();

    boolean isErrorEnabled(Marker marker);

    boolean isFatalEnabled();

    boolean isFatalEnabled(Marker marker);

    boolean isInfoEnabled();

    boolean isInfoEnabled(Marker marker);

    boolean isTraceEnabled();

    boolean isTraceEnabled(Marker marker);

    boolean isWarnEnabled();

    boolean isWarnEnabled(Marker marker);

    void log(Level level, Marker marker, Message message);

    void log(Level level, Marker marker, Message message, Throwable th);

    void log(Level level, Marker marker, MessageSupplier messageSupplier);

    void log(Level level, Marker marker, MessageSupplier messageSupplier, Throwable th);

    void log(Level level, Marker marker, CharSequence charSequence);

    void log(Level level, Marker marker, CharSequence charSequence, Throwable th);

    void log(Level level, Marker marker, Object obj);

    void log(Level level, Marker marker, Object obj, Throwable th);

    void log(Level level, Marker marker, String str);

    void log(Level level, Marker marker, String str, Object... objArr);

    void log(Level level, Marker marker, String str, Supplier<?>... supplierArr);

    void log(Level level, Marker marker, String str, Throwable th);

    void log(Level level, Marker marker, Supplier<?> supplier);

    void log(Level level, Marker marker, Supplier<?> supplier, Throwable th);

    void log(Level level, Message message);

    void log(Level level, Message message, Throwable th);

    void log(Level level, MessageSupplier messageSupplier);

    void log(Level level, MessageSupplier messageSupplier, Throwable th);

    void log(Level level, CharSequence charSequence);

    void log(Level level, CharSequence charSequence, Throwable th);

    void log(Level level, Object obj);

    void log(Level level, Object obj, Throwable th);

    void log(Level level, String str);

    void log(Level level, String str, Object... objArr);

    void log(Level level, String str, Supplier<?>... supplierArr);

    void log(Level level, String str, Throwable th);

    void log(Level level, Supplier<?> supplier);

    void log(Level level, Supplier<?> supplier, Throwable th);

    void log(Level level, Marker marker, String str, Object obj);

    void log(Level level, Marker marker, String str, Object obj, Object obj2);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void log(Level level, Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void log(Level level, String str, Object obj);

    void log(Level level, String str, Object obj, Object obj2);

    void log(Level level, String str, Object obj, Object obj2, Object obj3);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void log(Level level, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void printf(Level level, Marker marker, String str, Object... objArr);

    void printf(Level level, String str, Object... objArr);

    <T extends Throwable> T throwing(Level level, T t);

    <T extends Throwable> T throwing(T t);

    void trace(Marker marker, Message message);

    void trace(Marker marker, Message message, Throwable th);

    void trace(Marker marker, MessageSupplier messageSupplier);

    void trace(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void trace(Marker marker, CharSequence charSequence);

    void trace(Marker marker, CharSequence charSequence, Throwable th);

    void trace(Marker marker, Object obj);

    void trace(Marker marker, Object obj, Throwable th);

    void trace(Marker marker, String str);

    void trace(Marker marker, String str, Object... objArr);

    void trace(Marker marker, String str, Supplier<?>... supplierArr);

    void trace(Marker marker, String str, Throwable th);

    void trace(Marker marker, Supplier<?> supplier);

    void trace(Marker marker, Supplier<?> supplier, Throwable th);

    void trace(Message message);

    void trace(Message message, Throwable th);

    void trace(MessageSupplier messageSupplier);

    void trace(MessageSupplier messageSupplier, Throwable th);

    void trace(CharSequence charSequence);

    void trace(CharSequence charSequence, Throwable th);

    void trace(Object obj);

    void trace(Object obj, Throwable th);

    void trace(String str);

    void trace(String str, Object... objArr);

    void trace(String str, Supplier<?>... supplierArr);

    void trace(String str, Throwable th);

    void trace(Supplier<?> supplier);

    void trace(Supplier<?> supplier, Throwable th);

    void trace(Marker marker, String str, Object obj);

    void trace(Marker marker, String str, Object obj, Object obj2);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void trace(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void trace(String str, Object obj);

    void trace(String str, Object obj, Object obj2);

    void trace(String str, Object obj, Object obj2, Object obj3);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void trace(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    EntryMessage traceEntry();

    EntryMessage traceEntry(String str, Object... objArr);

    EntryMessage traceEntry(Supplier<?>... supplierArr);

    EntryMessage traceEntry(String str, Supplier<?>... supplierArr);

    EntryMessage traceEntry(Message message);

    void traceExit();

    <R> R traceExit(R r);

    <R> R traceExit(String str, R r);

    void traceExit(EntryMessage entryMessage);

    <R> R traceExit(EntryMessage entryMessage, R r);

    <R> R traceExit(Message message, R r);

    void warn(Marker marker, Message message);

    void warn(Marker marker, Message message, Throwable th);

    void warn(Marker marker, MessageSupplier messageSupplier);

    void warn(Marker marker, MessageSupplier messageSupplier, Throwable th);

    void warn(Marker marker, CharSequence charSequence);

    void warn(Marker marker, CharSequence charSequence, Throwable th);

    void warn(Marker marker, Object obj);

    void warn(Marker marker, Object obj, Throwable th);

    void warn(Marker marker, String str);

    void warn(Marker marker, String str, Object... objArr);

    void warn(Marker marker, String str, Supplier<?>... supplierArr);

    void warn(Marker marker, String str, Throwable th);

    void warn(Marker marker, Supplier<?> supplier);

    void warn(Marker marker, Supplier<?> supplier, Throwable th);

    void warn(Message message);

    void warn(Message message, Throwable th);

    void warn(MessageSupplier messageSupplier);

    void warn(MessageSupplier messageSupplier, Throwable th);

    void warn(CharSequence charSequence);

    void warn(CharSequence charSequence, Throwable th);

    void warn(Object obj);

    void warn(Object obj, Throwable th);

    void warn(String str);

    void warn(String str, Object... objArr);

    void warn(String str, Supplier<?>... supplierArr);

    void warn(String str, Throwable th);

    void warn(Supplier<?> supplier);

    void warn(Supplier<?> supplier, Throwable th);

    void warn(Marker marker, String str, Object obj);

    void warn(Marker marker, String str, Object obj, Object obj2);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void warn(Marker marker, String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);

    void warn(String str, Object obj);

    void warn(String str, Object obj, Object obj2);

    void warn(String str, Object obj, Object obj2, Object obj3);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9);

    void warn(String str, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10);
}