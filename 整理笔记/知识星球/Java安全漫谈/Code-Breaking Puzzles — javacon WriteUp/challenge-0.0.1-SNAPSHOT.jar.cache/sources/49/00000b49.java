package org.apache.logging.log4j.spi;

import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/log4j-api-2.11.1.jar:org/apache/logging/log4j/spi/AbstractLogger.class */
public abstract class AbstractLogger implements ExtendedLogger, Serializable {
    private static final long serialVersionUID = 2;
    private static final String THROWING = "Throwing";
    private static final String CATCHING = "Catching";
    protected final String name;
    private final MessageFactory2 messageFactory;
    private final FlowMessageFactory flowMessageFactory;
    public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
    public static final Marker ENTRY_MARKER = MarkerManager.getMarker("ENTER").setParents(FLOW_MARKER);
    public static final Marker EXIT_MARKER = MarkerManager.getMarker("EXIT").setParents(FLOW_MARKER);
    public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
    public static final Marker THROWING_MARKER = MarkerManager.getMarker("THROWING").setParents(EXCEPTION_MARKER);
    public static final Marker CATCHING_MARKER = MarkerManager.getMarker("CATCHING").setParents(EXCEPTION_MARKER);
    public static final Class<? extends MessageFactory> DEFAULT_MESSAGE_FACTORY_CLASS = createClassForProperty("log4j2.messageFactory", ReusableMessageFactory.class, ParameterizedMessageFactory.class);
    public static final Class<? extends FlowMessageFactory> DEFAULT_FLOW_MESSAGE_FACTORY_CLASS = createFlowClassForProperty("log4j2.flowMessageFactory", DefaultFlowMessageFactory.class);
    private static final String FQCN = AbstractLogger.class.getName();
    private static ThreadLocal<int[]> recursionDepthHolder = new ThreadLocal<>();

    public AbstractLogger() {
        this.name = getClass().getName();
        this.messageFactory = createDefaultMessageFactory();
        this.flowMessageFactory = createDefaultFlowMessageFactory();
    }

    public AbstractLogger(String name) {
        this(name, createDefaultMessageFactory());
    }

    public AbstractLogger(String name, MessageFactory messageFactory) {
        this.name = name;
        this.messageFactory = messageFactory == null ? createDefaultMessageFactory() : narrow(messageFactory);
        this.flowMessageFactory = createDefaultFlowMessageFactory();
    }

    public static void checkMessageFactory(ExtendedLogger logger, MessageFactory messageFactory) {
        String name = logger.getName();
        MessageFactory loggerMessageFactory = logger.getMessageFactory();
        if (messageFactory != null && !loggerMessageFactory.equals(messageFactory)) {
            StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with the message factory {}, which may create log events with unexpected formatting.", name, loggerMessageFactory, messageFactory);
        } else if (messageFactory == null && !loggerMessageFactory.getClass().equals(DEFAULT_MESSAGE_FACTORY_CLASS)) {
            StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with a null message factory (defaults to {}), which may create log events with unexpected formatting.", name, loggerMessageFactory, DEFAULT_MESSAGE_FACTORY_CLASS.getName());
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void catching(Level level, Throwable t) {
        catching(FQCN, level, t);
    }

    protected void catching(String fqcn, Level level, Throwable t) {
        if (isEnabled(level, CATCHING_MARKER, (Object) null, (Throwable) null)) {
            logMessageSafely(fqcn, level, CATCHING_MARKER, catchingMsg(t), t);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void catching(Throwable t) {
        if (isEnabled(Level.ERROR, CATCHING_MARKER, (Object) null, (Throwable) null)) {
            logMessageSafely(FQCN, Level.ERROR, CATCHING_MARKER, catchingMsg(t), t);
        }
    }

    protected Message catchingMsg(Throwable t) {
        return this.messageFactory.newMessage(CATCHING);
    }

    private static Class<? extends MessageFactory> createClassForProperty(String property, Class<ReusableMessageFactory> reusableParameterizedMessageFactoryClass, Class<ParameterizedMessageFactory> parameterizedMessageFactoryClass) {
        try {
            String fallback = Constants.ENABLE_THREADLOCALS ? reusableParameterizedMessageFactoryClass.getName() : parameterizedMessageFactoryClass.getName();
            String clsName = PropertiesUtil.getProperties().getStringProperty(property, fallback);
            return LoaderUtil.loadClass(clsName).asSubclass(MessageFactory.class);
        } catch (Throwable th) {
            return parameterizedMessageFactoryClass;
        }
    }

    private static Class<? extends FlowMessageFactory> createFlowClassForProperty(String property, Class<DefaultFlowMessageFactory> defaultFlowMessageFactoryClass) {
        try {
            String clsName = PropertiesUtil.getProperties().getStringProperty(property, defaultFlowMessageFactoryClass.getName());
            return LoaderUtil.loadClass(clsName).asSubclass(FlowMessageFactory.class);
        } catch (Throwable th) {
            return defaultFlowMessageFactoryClass;
        }
    }

    private static MessageFactory2 createDefaultMessageFactory() {
        try {
            MessageFactory result = DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
            return narrow(result);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static MessageFactory2 narrow(MessageFactory result) {
        if (result instanceof MessageFactory2) {
            return (MessageFactory2) result;
        }
        return new MessageFactory2Adapter(result);
    }

    private static FlowMessageFactory createDefaultFlowMessageFactory() {
        try {
            return DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Message msg) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(CharSequence message) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Object message) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object... params) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0) {
        logIfEnabled(FQCN, Level.DEBUG, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    protected EntryMessage enter(String fqcn, String format, Supplier<?>... paramSuppliers) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            Level level = Level.TRACE;
            Marker marker = ENTRY_MARKER;
            EntryMessage entryMsg2 = entryMsg(format, paramSuppliers);
            entryMsg = entryMsg2;
            logMessageSafely(fqcn, level, marker, entryMsg2, null);
        }
        return entryMsg;
    }

    @Deprecated
    protected EntryMessage enter(String fqcn, String format, MessageSupplier... paramSuppliers) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            Level level = Level.TRACE;
            Marker marker = ENTRY_MARKER;
            EntryMessage entryMsg2 = entryMsg(format, paramSuppliers);
            entryMsg = entryMsg2;
            logMessageSafely(fqcn, level, marker, entryMsg2, null);
        }
        return entryMsg;
    }

    protected EntryMessage enter(String fqcn, String format, Object... params) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            Level level = Level.TRACE;
            Marker marker = ENTRY_MARKER;
            EntryMessage entryMsg2 = entryMsg(format, params);
            entryMsg = entryMsg2;
            logMessageSafely(fqcn, level, marker, entryMsg2, null);
        }
        return entryMsg;
    }

    @Deprecated
    protected EntryMessage enter(String fqcn, MessageSupplier msgSupplier) {
        EntryMessage message = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            Level level = Level.TRACE;
            Marker marker = ENTRY_MARKER;
            EntryMessage newEntryMessage = this.flowMessageFactory.newEntryMessage(msgSupplier.get());
            message = newEntryMessage;
            logMessageSafely(fqcn, level, marker, newEntryMessage, null);
        }
        return message;
    }

    protected EntryMessage enter(String fqcn, Message message) {
        EntryMessage flowMessage = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            Level level = Level.TRACE;
            Marker marker = ENTRY_MARKER;
            EntryMessage newEntryMessage = this.flowMessageFactory.newEntryMessage(message);
            flowMessage = newEntryMessage;
            logMessageSafely(fqcn, level, marker, newEntryMessage, null);
        }
        return flowMessage;
    }

    @Override // org.apache.logging.log4j.Logger
    @Deprecated
    public void entry() {
        entry(FQCN, null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void entry(Object... params) {
        entry(FQCN, params);
    }

    protected void entry(String fqcn, Object... params) {
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, (Throwable) null)) {
            if (params == null) {
                logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg((String) null, (Supplier<?>[]) null), null);
            } else {
                logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg((String) null, params), null);
            }
        }
    }

    protected EntryMessage entryMsg(String format, Object... params) {
        int count = params == null ? 0 : params.length;
        if (count == 0) {
            if (Strings.isEmpty(format)) {
                return this.flowMessageFactory.newEntryMessage(null);
            }
            return this.flowMessageFactory.newEntryMessage(new SimpleMessage(format));
        } else if (format != null) {
            return this.flowMessageFactory.newEntryMessage(new ParameterizedMessage(format, params));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("params(");
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                Object parm = params[i];
                sb.append(parm instanceof Message ? ((Message) parm).getFormattedMessage() : String.valueOf(parm));
            }
            sb.append(')');
            return this.flowMessageFactory.newEntryMessage(new SimpleMessage(sb));
        }
    }

    protected EntryMessage entryMsg(String format, MessageSupplier... paramSuppliers) {
        int count = paramSuppliers == null ? 0 : paramSuppliers.length;
        Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            params[i] = paramSuppliers[i].get();
            params[i] = params[i] != null ? ((Message) params[i]).getFormattedMessage() : null;
        }
        return entryMsg(format, params);
    }

    protected EntryMessage entryMsg(String format, Supplier<?>... paramSuppliers) {
        int count = paramSuppliers == null ? 0 : paramSuppliers.length;
        Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            params[i] = paramSuppliers[i].get();
            if (params[i] instanceof Message) {
                params[i] = ((Message) params[i]).getFormattedMessage();
            }
        }
        return entryMsg(format, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.ERROR, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Message msg) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(CharSequence message) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Object message) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object... params) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.ERROR, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.ERROR, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0) {
        logIfEnabled(FQCN, Level.ERROR, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    @Deprecated
    public void exit() {
        exit(FQCN, null);
    }

    @Override // org.apache.logging.log4j.Logger
    @Deprecated
    public <R> R exit(R result) {
        return (R) exit(FQCN, result);
    }

    protected <R> R exit(String fqcn, R result) {
        if (isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence) null, (Throwable) null)) {
            logMessageSafely(fqcn, Level.TRACE, EXIT_MARKER, exitMsg(null, result), null);
        }
        return result;
    }

    protected <R> R exit(String fqcn, String format, R result) {
        if (isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence) null, (Throwable) null)) {
            logMessageSafely(fqcn, Level.TRACE, EXIT_MARKER, exitMsg(format, result), null);
        }
        return result;
    }

    protected Message exitMsg(String format, Object result) {
        if (result == null) {
            if (format == null) {
                return this.messageFactory.newMessage("Exit");
            }
            return this.messageFactory.newMessage("Exit: " + format);
        } else if (format == null) {
            return this.messageFactory.newMessage("Exit with(" + result + ')');
        } else {
            return this.messageFactory.newMessage("Exit: " + format, result);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.FATAL, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Message msg) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(CharSequence message) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Object message) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object... params) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.FATAL, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.FATAL, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0) {
        logIfEnabled(FQCN, Level.FATAL, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public <MF extends MessageFactory> MF getMessageFactory() {
        return this.messageFactory;
    }

    @Override // org.apache.logging.log4j.Logger
    public String getName() {
        return this.name;
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.INFO, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.INFO, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Message msg) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(CharSequence message) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Object message) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object... params) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.INFO, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.INFO, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.INFO, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0) {
        logIfEnabled(FQCN, Level.INFO, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG, null, null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isDebugEnabled(Marker marker) {
        return isEnabled(Level.DEBUG, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isEnabled(Level level) {
        return isEnabled(level, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isEnabled(Level level, Marker marker) {
        return isEnabled(level, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isErrorEnabled(Marker marker) {
        return isEnabled(Level.ERROR, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isFatalEnabled() {
        return isEnabled(Level.FATAL, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isFatalEnabled(Marker marker) {
        return isEnabled(Level.FATAL, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return isEnabled(Level.INFO, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isTraceEnabled(Marker marker) {
        return isEnabled(Level.TRACE, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN, (Marker) null, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public boolean isWarnEnabled(Marker marker) {
        return isEnabled(Level.WARN, marker, (Object) null, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Message msg) {
        logIfEnabled(FQCN, level, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, level, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, CharSequence message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, CharSequence message, Throwable t) {
        if (isEnabled(level, marker, message, t)) {
            logMessage(FQCN, level, marker, message, t);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Object message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Object message, Throwable t) {
        if (isEnabled(level, marker, message, t)) {
            logMessage(FQCN, level, marker, message, t);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, level, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, level, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Message msg) {
        logIfEnabled(FQCN, level, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Message msg, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, CharSequence message) {
        logIfEnabled(FQCN, level, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Object message) {
        logIfEnabled(FQCN, level, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Object message, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message) {
        logIfEnabled(FQCN, level, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object... params) {
        logIfEnabled(FQCN, level, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, level, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, level, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, level, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, level, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, level, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, level, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, level, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, level, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, level, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, level, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, level, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0) {
        logIfEnabled(FQCN, level, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, level, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, Message msg, Throwable t) {
        if (isEnabled(level, marker, msg, t)) {
            logMessageSafely(fqcn, level, marker, msg, t);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, MessageSupplier msgSupplier, Throwable t) {
        if (isEnabled(level, marker, msgSupplier, t)) {
            logMessage(fqcn, level, marker, msgSupplier, t);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, Object message, Throwable t) {
        if (isEnabled(level, marker, message, t)) {
            logMessage(fqcn, level, marker, message, t);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, CharSequence message, Throwable t) {
        if (isEnabled(level, marker, message, t)) {
            logMessage(fqcn, level, marker, message, t);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, Supplier<?> msgSupplier, Throwable t) {
        if (isEnabled(level, marker, msgSupplier, t)) {
            logMessage(fqcn, level, marker, msgSupplier, t);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message) {
        if (isEnabled(level, marker, message)) {
            logMessage(fqcn, level, marker, message);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Supplier<?>... paramSuppliers) {
        if (isEnabled(level, marker, message)) {
            logMessage(fqcn, level, marker, message, paramSuppliers);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object... params) {
        if (isEnabled(level, marker, message, params)) {
            logMessage(fqcn, level, marker, message, params);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0) {
        if (isEnabled(level, marker, message, p0)) {
            logMessage(fqcn, level, marker, message, p0);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1) {
        if (isEnabled(level, marker, message, p0, p1)) {
            logMessage(fqcn, level, marker, message, p0, p1);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        if (isEnabled(level, marker, message, p0, p1, p2)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logIfEnabled(String fqcn, Level level, Marker marker, String message, Throwable t) {
        if (isEnabled(level, marker, message, t)) {
            logMessage(fqcn, level, marker, message, t);
        }
    }

    protected void logMessage(String fqcn, Level level, Marker marker, CharSequence message, Throwable t) {
        logMessageSafely(fqcn, level, marker, this.messageFactory.newMessage(message), t);
    }

    protected void logMessage(String fqcn, Level level, Marker marker, Object message, Throwable t) {
        logMessageSafely(fqcn, level, marker, this.messageFactory.newMessage(message), t);
    }

    protected void logMessage(String fqcn, Level level, Marker marker, MessageSupplier msgSupplier, Throwable t) {
        Message message = LambdaUtil.get(msgSupplier);
        logMessageSafely(fqcn, level, marker, message, (t != null || message == null) ? t : message.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, Supplier<?> msgSupplier, Throwable t) {
        Message message = LambdaUtil.getMessage(msgSupplier, this.messageFactory);
        logMessageSafely(fqcn, level, marker, message, (t != null || message == null) ? t : message.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Throwable t) {
        logMessageSafely(fqcn, level, marker, this.messageFactory.newMessage(message), t);
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message) {
        Message msg = this.messageFactory.newMessage(message);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object... params) {
        Message msg = this.messageFactory.newMessage(message, params);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0) {
        Message msg = this.messageFactory.newMessage(message, p0);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1) {
        Message msg = this.messageFactory.newMessage(message, p0, p1);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        Message msg = this.messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(String fqcn, Level level, Marker marker, String message, Supplier<?>... paramSuppliers) {
        Message msg = this.messageFactory.newMessage(message, LambdaUtil.getAll(paramSuppliers));
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    @Override // org.apache.logging.log4j.Logger
    public void printf(Level level, Marker marker, String format, Object... params) {
        if (isEnabled(level, marker, format, params)) {
            Message msg = new StringFormattedMessage(format, params);
            logMessageSafely(FQCN, level, marker, msg, msg.getThrowable());
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public void printf(Level level, String format, Object... params) {
        if (isEnabled(level, (Marker) null, format, params)) {
            Message msg = new StringFormattedMessage(format, params);
            logMessageSafely(FQCN, level, null, msg, msg.getThrowable());
        }
    }

    @PerformanceSensitive
    private void logMessageSafely(String fqcn, Level level, Marker marker, Message msg, Throwable throwable) {
        try {
            logMessageTrackRecursion(fqcn, level, marker, msg, throwable);
            ReusableMessageFactory.release(msg);
        } catch (Throwable th) {
            ReusableMessageFactory.release(msg);
            throw th;
        }
    }

    @PerformanceSensitive
    private void logMessageTrackRecursion(String fqcn, Level level, Marker marker, Message msg, Throwable throwable) {
        try {
            incrementRecursionDepth();
            tryLogMessage(fqcn, level, marker, msg, throwable);
        } finally {
            decrementRecursionDepth();
        }
    }

    private static int[] getRecursionDepthHolder() {
        int[] result = recursionDepthHolder.get();
        if (result == null) {
            result = new int[1];
            recursionDepthHolder.set(result);
        }
        return result;
    }

    private static void incrementRecursionDepth() {
        int[] recursionDepthHolder2 = getRecursionDepthHolder();
        recursionDepthHolder2[0] = recursionDepthHolder2[0] + 1;
    }

    private static void decrementRecursionDepth() {
        int[] depth = getRecursionDepthHolder();
        depth[0] = depth[0] - 1;
        if (depth[0] < 0) {
            throw new IllegalStateException("Recursion depth became negative: " + depth[0]);
        }
    }

    public static int getRecursionDepth() {
        return getRecursionDepthHolder()[0];
    }

    @PerformanceSensitive
    private void tryLogMessage(String fqcn, Level level, Marker marker, Message msg, Throwable throwable) {
        try {
            logMessage(fqcn, level, marker, msg, throwable);
        } catch (Exception e) {
            handleLogMessageException(e, fqcn, msg);
        }
    }

    private void handleLogMessageException(Exception exception, String fqcn, Message msg) {
        if (exception instanceof LoggingException) {
            throw ((LoggingException) exception);
        }
        String format = msg.getFormat();
        int formatLength = format == null ? 4 : format.length();
        StringBuilder sb = new StringBuilder(formatLength + 100);
        sb.append(fqcn);
        sb.append(" caught ");
        sb.append(exception.getClass().getName());
        sb.append(" logging ");
        sb.append(msg.getClass().getSimpleName());
        sb.append(": ");
        sb.append(format);
        StatusLogger.getLogger().warn(sb.toString(), (Throwable) exception);
    }

    @Override // org.apache.logging.log4j.Logger
    public <T extends Throwable> T throwing(T t) {
        return (T) throwing(FQCN, Level.ERROR, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public <T extends Throwable> T throwing(Level level, T t) {
        return (T) throwing(FQCN, level, t);
    }

    protected <T extends Throwable> T throwing(String fqcn, Level level, T t) {
        if (isEnabled(level, THROWING_MARKER, (Object) null, (Throwable) null)) {
            logMessageSafely(fqcn, level, THROWING_MARKER, throwingMsg(t), t);
        }
        return t;
    }

    protected Message throwingMsg(Throwable t) {
        return this.messageFactory.newMessage(THROWING);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.TRACE, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Message msg) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(CharSequence message) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Object message) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object... params) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.TRACE, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.TRACE, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0) {
        logIfEnabled(FQCN, Level.TRACE, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public EntryMessage traceEntry() {
        return enter(FQCN, (String) null, (Object[]) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public EntryMessage traceEntry(String format, Object... params) {
        return enter(FQCN, format, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public EntryMessage traceEntry(Supplier<?>... paramSuppliers) {
        return enter(FQCN, (String) null, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public EntryMessage traceEntry(String format, Supplier<?>... paramSuppliers) {
        return enter(FQCN, format, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public EntryMessage traceEntry(Message message) {
        return enter(FQCN, message);
    }

    @Override // org.apache.logging.log4j.Logger
    public void traceExit() {
        exit(FQCN, null, null);
    }

    @Override // org.apache.logging.log4j.Logger
    public <R> R traceExit(R result) {
        return (R) exit(FQCN, null, result);
    }

    @Override // org.apache.logging.log4j.Logger
    public <R> R traceExit(String format, R result) {
        return (R) exit(FQCN, format, result);
    }

    @Override // org.apache.logging.log4j.Logger
    public void traceExit(EntryMessage message) {
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, (Message) message, (Throwable) null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(message), null);
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public <R> R traceExit(EntryMessage message, R result) {
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, (Message) message, (Throwable) null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage((Object) result, message), null);
        }
        return result;
    }

    @Override // org.apache.logging.log4j.Logger
    public <R> R traceExit(Message message, R result) {
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, message, (Throwable) null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(result, message), null);
        }
        return result;
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Message msg) {
        logIfEnabled(FQCN, Level.WARN, marker, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, CharSequence message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Object message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Object message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object... params) {
        logIfEnabled(FQCN, Level.WARN, marker, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Message msg) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msg, msg != null ? msg.getThrowable() : null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Message msg, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msg, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(CharSequence message) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(CharSequence message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Object message) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Object message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object... params) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, params);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Supplier<?> msgSupplier) {
        logIfEnabled(FQCN, Level.WARN, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.WARN, marker, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, Supplier<?> msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, paramSuppliers);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.WARN, marker, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, marker, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(MessageSupplier msgSupplier) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msgSupplier, (Throwable) null);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(MessageSupplier msgSupplier, Throwable t) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, msgSupplier, t);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0) {
        logIfEnabled(FQCN, Level.WARN, (Marker) null, message, p0);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override // org.apache.logging.log4j.Logger
    public void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}