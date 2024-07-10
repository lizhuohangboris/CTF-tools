package ch.qos.logback.core.db.dialect;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/dialect/SQLiteDialect.class */
public class SQLiteDialect implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT last_insert_rowid();";

    @Override // ch.qos.logback.core.db.dialect.SQLDialect
    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}