package ch.qos.logback.core.db;

import ch.qos.logback.core.db.dialect.SQLDialectCode;
import ch.qos.logback.core.spi.LifeCycle;
import java.sql.Connection;
import java.sql.SQLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/ConnectionSource.class */
public interface ConnectionSource extends LifeCycle {
    Connection getConnection() throws SQLException;

    SQLDialectCode getSQLDialectCode();

    boolean supportsGetGeneratedKeys();

    boolean supportsBatchUpdates();
}