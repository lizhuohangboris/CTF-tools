package ch.qos.logback.classic.db.names;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/db/names/DefaultDBNameResolver.class */
public class DefaultDBNameResolver implements DBNameResolver {
    @Override // ch.qos.logback.classic.db.names.DBNameResolver
    public <N extends Enum<?>> String getTableName(N tableName) {
        return tableName.toString().toLowerCase();
    }

    @Override // ch.qos.logback.classic.db.names.DBNameResolver
    public <N extends Enum<?>> String getColumnName(N columnName) {
        return columnName.toString().toLowerCase();
    }
}