package ch.qos.logback.core.db;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.db.dialect.SQLDialect;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/DBAppenderBase.class */
public abstract class DBAppenderBase<E> extends UnsynchronizedAppenderBase<E> {
    protected ConnectionSource connectionSource;
    protected boolean cnxSupportsGetGeneratedKeys = false;
    protected boolean cnxSupportsBatchUpdates = false;
    protected SQLDialect sqlDialect;

    protected abstract Method getGeneratedKeysMethod();

    protected abstract String getInsertSQL();

    protected abstract void subAppend(E e, Connection connection, PreparedStatement preparedStatement) throws Throwable;

    protected abstract void secondarySubAppend(E e, Connection connection, long j) throws Throwable;

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }
        this.sqlDialect = DBUtil.getDialectFromCode(this.connectionSource.getSQLDialectCode());
        if (getGeneratedKeysMethod() != null) {
            this.cnxSupportsGetGeneratedKeys = this.connectionSource.supportsGetGeneratedKeys();
        } else {
            this.cnxSupportsGetGeneratedKeys = false;
        }
        this.cnxSupportsBatchUpdates = this.connectionSource.supportsBatchUpdates();
        if (!this.cnxSupportsGetGeneratedKeys && this.sqlDialect == null) {
            throw new IllegalStateException("DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
        }
        super.start();
    }

    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase
    public void append(E eventObject) {
        long eventId;
        Connection connection = null;
        PreparedStatement insertStatement = null;
        try {
            connection = this.connectionSource.getConnection();
            connection.setAutoCommit(false);
            if (this.cnxSupportsGetGeneratedKeys) {
                String EVENT_ID_COL_NAME = "EVENT_ID";
                if (this.connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
                    EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
                }
                insertStatement = connection.prepareStatement(getInsertSQL(), new String[]{EVENT_ID_COL_NAME});
            } else {
                insertStatement = connection.prepareStatement(getInsertSQL());
            }
            synchronized (this) {
                subAppend(eventObject, connection, insertStatement);
                eventId = selectEventId(insertStatement, connection);
            }
            secondarySubAppend(eventObject, connection, eventId);
            connection.commit();
            DBHelper.closeStatement(insertStatement);
            DBHelper.closeConnection(connection);
        } catch (Throwable sqle) {
            try {
                addError("problem appending event", sqle);
                DBHelper.closeStatement(insertStatement);
                DBHelper.closeConnection(connection);
            } finally {
                DBHelper.closeStatement(insertStatement);
                DBHelper.closeConnection(connection);
            }
        }
    }

    protected long selectEventId(PreparedStatement insertStatement, Connection connection) throws SQLException, InvocationTargetException {
        ResultSet rs = null;
        Statement idStatement = null;
        try {
            boolean gotGeneratedKeys = false;
            if (this.cnxSupportsGetGeneratedKeys) {
                try {
                    rs = (ResultSet) getGeneratedKeysMethod().invoke(insertStatement, null);
                    gotGeneratedKeys = true;
                } catch (IllegalAccessException ex) {
                    addWarn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex);
                } catch (InvocationTargetException ex2) {
                    Throwable target = ex2.getTargetException();
                    if (target instanceof SQLException) {
                        throw ((SQLException) target);
                    }
                    throw ex2;
                }
            }
            if (!gotGeneratedKeys) {
                idStatement = connection.createStatement();
                idStatement.setMaxRows(1);
                String selectInsertIdStr = this.sqlDialect.getSelectInsertId();
                rs = idStatement.executeQuery(selectInsertIdStr);
            }
            rs.next();
            long eventId = rs.getLong(1);
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            DBHelper.closeStatement(idStatement);
            return eventId;
        } catch (Throwable th) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e2) {
                }
            }
            DBHelper.closeStatement(null);
            throw th;
        }
    }

    @Override // ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        super.stop();
    }
}