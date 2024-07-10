package ch.qos.logback.classic.db;

import ch.qos.logback.classic.db.names.DBNameResolver;
import ch.qos.logback.classic.db.names.DefaultDBNameResolver;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.db.DBAppenderBase;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/db/DBAppender.class */
public class DBAppender extends DBAppenderBase<ILoggingEvent> {
    protected String insertPropertiesSQL;
    protected String insertExceptionSQL;
    protected String insertSQL;
    protected static final Method GET_GENERATED_KEYS_METHOD;
    private DBNameResolver dbNameResolver;
    static final int TIMESTMP_INDEX = 1;
    static final int FORMATTED_MESSAGE_INDEX = 2;
    static final int LOGGER_NAME_INDEX = 3;
    static final int LEVEL_STRING_INDEX = 4;
    static final int THREAD_NAME_INDEX = 5;
    static final int REFERENCE_FLAG_INDEX = 6;
    static final int ARG0_INDEX = 7;
    static final int ARG1_INDEX = 8;
    static final int ARG2_INDEX = 9;
    static final int ARG3_INDEX = 10;
    static final int CALLER_FILENAME_INDEX = 11;
    static final int CALLER_CLASS_INDEX = 12;
    static final int CALLER_METHOD_INDEX = 13;
    static final int CALLER_LINE_INDEX = 14;
    static final int EVENT_ID_INDEX = 15;
    static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

    static {
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", null);
        } catch (Exception e) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }

    public void setDbNameResolver(DBNameResolver dbNameResolver) {
        this.dbNameResolver = dbNameResolver;
    }

    @Override // ch.qos.logback.core.db.DBAppenderBase, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.dbNameResolver == null) {
            this.dbNameResolver = new DefaultDBNameResolver();
        }
        this.insertExceptionSQL = SQLBuilder.buildInsertExceptionSQL(this.dbNameResolver);
        this.insertPropertiesSQL = SQLBuilder.buildInsertPropertiesSQL(this.dbNameResolver);
        this.insertSQL = SQLBuilder.buildInsertSQL(this.dbNameResolver);
        super.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.db.DBAppenderBase
    public void subAppend(ILoggingEvent event, Connection connection, PreparedStatement insertStatement) throws Throwable {
        bindLoggingEventWithInsertStatement(insertStatement, event);
        bindLoggingEventArgumentsWithPreparedStatement(insertStatement, event.getArgumentArray());
        bindCallerDataWithPreparedStatement(insertStatement, event.getCallerData());
        int updateCount = insertStatement.executeUpdate();
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.db.DBAppenderBase
    public void secondarySubAppend(ILoggingEvent event, Connection connection, long eventId) throws Throwable {
        Map<String, String> mergedMap = mergePropertyMaps(event);
        insertProperties(mergedMap, connection, eventId);
        if (event.getThrowableProxy() != null) {
            insertThrowable(event.getThrowableProxy(), connection, eventId);
        }
    }

    void bindLoggingEventWithInsertStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        stmt.setLong(1, event.getTimeStamp());
        stmt.setString(2, event.getFormattedMessage());
        stmt.setString(3, event.getLoggerName());
        stmt.setString(4, event.getLevel().toString());
        stmt.setString(5, event.getThreadName());
        stmt.setShort(6, DBHelper.computeReferenceMask(event));
    }

    void bindLoggingEventArgumentsWithPreparedStatement(PreparedStatement stmt, Object[] argArray) throws SQLException {
        int arrayLen = argArray != null ? argArray.length : 0;
        for (int i = 0; i < arrayLen && i < 4; i++) {
            stmt.setString(7 + i, asStringTruncatedTo254(argArray[i]));
        }
        if (arrayLen < 4) {
            for (int i2 = arrayLen; i2 < 4; i2++) {
                stmt.setString(7 + i2, null);
            }
        }
    }

    String asStringTruncatedTo254(Object o) {
        String s = null;
        if (o != null) {
            s = o.toString();
        }
        if (s == null) {
            return null;
        }
        if (s.length() <= 254) {
            return s;
        }
        return s.substring(0, 254);
    }

    void bindCallerDataWithPreparedStatement(PreparedStatement stmt, StackTraceElement[] callerDataArray) throws SQLException {
        StackTraceElement caller = extractFirstCaller(callerDataArray);
        stmt.setString(11, caller.getFileName());
        stmt.setString(12, caller.getClassName());
        stmt.setString(13, caller.getMethodName());
        stmt.setString(14, Integer.toString(caller.getLineNumber()));
    }

    private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
        StackTraceElement caller = EMPTY_CALLER_DATA;
        if (hasAtLeastOneNonNullElement(callerDataArray)) {
            caller = callerDataArray[0];
        }
        return caller;
    }

    private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
        return (callerDataArray == null || callerDataArray.length <= 0 || callerDataArray[0] == null) ? false : true;
    }

    Map<String, String> mergePropertyMaps(ILoggingEvent event) {
        Map<String, String> mergedMap = new HashMap<>();
        Map<String, String> loggerContextMap = event.getLoggerContextVO().getPropertyMap();
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (loggerContextMap != null) {
            mergedMap.putAll(loggerContextMap);
        }
        if (mdcMap != null) {
            mergedMap.putAll(mdcMap);
        }
        return mergedMap;
    }

    @Override // ch.qos.logback.core.db.DBAppenderBase
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    @Override // ch.qos.logback.core.db.DBAppenderBase
    protected String getInsertSQL() {
        return this.insertSQL;
    }

    protected void insertProperties(Map<String, String> mergedMap, Connection connection, long eventId) throws SQLException {
        Set<String> propertiesKeys = mergedMap.keySet();
        if (propertiesKeys.size() > 0) {
            PreparedStatement insertPropertiesStatement = null;
            try {
                insertPropertiesStatement = connection.prepareStatement(this.insertPropertiesSQL);
                for (String key : propertiesKeys) {
                    String value = mergedMap.get(key);
                    insertPropertiesStatement.setLong(1, eventId);
                    insertPropertiesStatement.setString(2, key);
                    insertPropertiesStatement.setString(3, value);
                    if (this.cnxSupportsBatchUpdates) {
                        insertPropertiesStatement.addBatch();
                    } else {
                        insertPropertiesStatement.execute();
                    }
                }
                if (this.cnxSupportsBatchUpdates) {
                    insertPropertiesStatement.executeBatch();
                }
                ch.qos.logback.core.db.DBHelper.closeStatement(insertPropertiesStatement);
            } catch (Throwable th) {
                ch.qos.logback.core.db.DBHelper.closeStatement(insertPropertiesStatement);
                throw th;
            }
        }
    }

    void updateExceptionStatement(PreparedStatement exceptionStatement, String txt, short i, long eventId) throws SQLException {
        exceptionStatement.setLong(1, eventId);
        exceptionStatement.setShort(2, i);
        exceptionStatement.setString(3, txt);
        if (this.cnxSupportsBatchUpdates) {
            exceptionStatement.addBatch();
        } else {
            exceptionStatement.execute();
        }
    }

    short buildExceptionStatement(IThrowableProxy tp, short baseIndex, PreparedStatement insertExceptionStatement, long eventId) throws SQLException {
        StringBuilder buf = new StringBuilder();
        ThrowableProxyUtil.subjoinFirstLine(buf, tp);
        short baseIndex2 = (short) (baseIndex + 1);
        updateExceptionStatement(insertExceptionStatement, buf.toString(), baseIndex, eventId);
        int commonFrames = tp.getCommonFrames();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        for (int i = 0; i < stepArray.length - commonFrames; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append('\t');
            ThrowableProxyUtil.subjoinSTEP(sb, stepArray[i]);
            short s = baseIndex2;
            baseIndex2 = (short) (baseIndex2 + 1);
            updateExceptionStatement(insertExceptionStatement, sb.toString(), s, eventId);
        }
        if (commonFrames > 0) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('\t').append("... ").append(commonFrames).append(" common frames omitted");
            short s2 = baseIndex2;
            baseIndex2 = (short) (baseIndex2 + 1);
            updateExceptionStatement(insertExceptionStatement, sb2.toString(), s2, eventId);
        }
        return baseIndex2;
    }

    protected void insertThrowable(IThrowableProxy tp, Connection connection, long eventId) throws SQLException {
        PreparedStatement exceptionStatement = null;
        try {
            exceptionStatement = connection.prepareStatement(this.insertExceptionSQL);
            short baseIndex = 0;
            while (tp != null) {
                baseIndex = buildExceptionStatement(tp, baseIndex, exceptionStatement, eventId);
                tp = tp.getCause();
            }
            if (this.cnxSupportsBatchUpdates) {
                exceptionStatement.executeBatch();
            }
            ch.qos.logback.core.db.DBHelper.closeStatement(exceptionStatement);
        } catch (Throwable th) {
            ch.qos.logback.core.db.DBHelper.closeStatement(exceptionStatement);
            throw th;
        }
    }
}