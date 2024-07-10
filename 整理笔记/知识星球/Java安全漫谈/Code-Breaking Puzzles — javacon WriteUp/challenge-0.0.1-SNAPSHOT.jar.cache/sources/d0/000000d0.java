package ch.qos.logback.core.db.dialect;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/dialect/OracleDialect.class */
public class OracleDialect implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT logging_event_id_seq.currval from dual";

    @Override // ch.qos.logback.core.db.dialect.SQLDialect
    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}