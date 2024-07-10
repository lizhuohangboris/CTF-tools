package ch.qos.logback.core.db;

import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/DataSourceConnectionSource.class */
public class DataSourceConnectionSource extends ConnectionSourceBase {
    private DataSource dataSource;

    @Override // ch.qos.logback.core.db.ConnectionSourceBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.dataSource == null) {
            addWarn("WARNING: No data source specified");
        } else {
            discoverConnectionProperties();
            if (!supportsGetGeneratedKeys() && getSQLDialectCode() == SQLDialectCode.UNKNOWN_DIALECT) {
                addWarn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
            }
        }
        super.start();
    }

    @Override // ch.qos.logback.core.db.ConnectionSource
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            addError("WARNING: No data source specified");
            return null;
        } else if (getUser() == null) {
            return this.dataSource.getConnection();
        } else {
            return this.dataSource.getConnection(getUser(), getPassword());
        }
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}