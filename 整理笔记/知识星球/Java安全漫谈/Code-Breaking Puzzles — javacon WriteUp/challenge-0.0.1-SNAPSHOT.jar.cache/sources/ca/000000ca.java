package ch.qos.logback.core.db.dialect;

import ch.qos.logback.core.spi.ContextAwareBase;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/dialect/DBUtil.class */
public class DBUtil extends ContextAwareBase {
    private static final String POSTGRES_PART = "postgresql";
    private static final String MYSQL_PART = "mysql";
    private static final String ORACLE_PART = "oracle";
    private static final String MSSQL_PART = "microsoft";
    private static final String HSQL_PART = "hsql";
    private static final String H2_PART = "h2";
    private static final String SYBASE_SQLANY_PART = "sql anywhere";
    private static final String SQLITE_PART = "sqlite";

    public static SQLDialectCode discoverSQLDialect(DatabaseMetaData meta) {
        SQLDialectCode dialectCode = SQLDialectCode.UNKNOWN_DIALECT;
        try {
            String dbName = meta.getDatabaseProductName().toLowerCase();
            if (dbName.indexOf(POSTGRES_PART) != -1) {
                return SQLDialectCode.POSTGRES_DIALECT;
            }
            if (dbName.indexOf(MYSQL_PART) != -1) {
                return SQLDialectCode.MYSQL_DIALECT;
            }
            if (dbName.indexOf(ORACLE_PART) != -1) {
                return SQLDialectCode.ORACLE_DIALECT;
            }
            if (dbName.indexOf(MSSQL_PART) != -1) {
                return SQLDialectCode.MSSQL_DIALECT;
            }
            if (dbName.indexOf(HSQL_PART) != -1) {
                return SQLDialectCode.HSQL_DIALECT;
            }
            if (dbName.indexOf(H2_PART) != -1) {
                return SQLDialectCode.H2_DIALECT;
            }
            if (dbName.indexOf(SYBASE_SQLANY_PART) != -1) {
                return SQLDialectCode.SYBASE_SQLANYWHERE_DIALECT;
            }
            if (dbName.indexOf(SQLITE_PART) != -1) {
                return SQLDialectCode.SQLITE_DIALECT;
            }
            return SQLDialectCode.UNKNOWN_DIALECT;
        } catch (SQLException e) {
            return dialectCode;
        }
    }

    public static SQLDialect getDialectFromCode(SQLDialectCode sqlDialectType) {
        SQLDialect sqlDialect = null;
        switch (sqlDialectType) {
            case POSTGRES_DIALECT:
                sqlDialect = new PostgreSQLDialect();
                break;
            case MYSQL_DIALECT:
                sqlDialect = new MySQLDialect();
                break;
            case ORACLE_DIALECT:
                sqlDialect = new OracleDialect();
                break;
            case MSSQL_DIALECT:
                sqlDialect = new MsSQLDialect();
                break;
            case HSQL_DIALECT:
                sqlDialect = new HSQLDBDialect();
                break;
            case H2_DIALECT:
                sqlDialect = new H2Dialect();
                break;
            case SYBASE_SQLANYWHERE_DIALECT:
                sqlDialect = new SybaseSqlAnywhereDialect();
                break;
            case SQLITE_DIALECT:
                sqlDialect = new SQLiteDialect();
                break;
        }
        return sqlDialect;
    }

    public boolean supportsGetGeneratedKeys(DatabaseMetaData meta) {
        try {
            return ((Boolean) DatabaseMetaData.class.getMethod("supportsGetGeneratedKeys", null).invoke(meta, null)).booleanValue();
        } catch (Throwable th) {
            addInfo("Could not call supportsGetGeneratedKeys method. This may be recoverable");
            return false;
        }
    }

    public boolean supportsBatchUpdates(DatabaseMetaData meta) {
        try {
            return meta.supportsBatchUpdates();
        } catch (Throwable th) {
            addInfo("Missing DatabaseMetaData.supportsBatchUpdates method.");
            return false;
        }
    }
}