package ch.qos.logback.core.db.dialect;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/dialect/MySQLDialect.class */
public class MySQLDialect implements SQLDialect {
    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

    @Override // ch.qos.logback.core.db.dialect.SQLDialect
    public String getSelectInsertId() {
        return SELECT_LAST_INSERT_ID;
    }
}