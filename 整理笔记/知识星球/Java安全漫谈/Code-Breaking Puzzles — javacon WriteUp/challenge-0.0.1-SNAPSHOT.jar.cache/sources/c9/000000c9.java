package ch.qos.logback.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/db/JNDIConnectionSource.class */
public class JNDIConnectionSource extends ConnectionSourceBase {
    private String jndiLocation = null;
    private DataSource dataSource = null;

    @Override // ch.qos.logback.core.db.ConnectionSourceBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.jndiLocation == null) {
            addError("No JNDI location specified for JNDIConnectionSource.");
        }
        discoverConnectionProperties();
    }

    @Override // ch.qos.logback.core.db.ConnectionSource
    public Connection getConnection() throws SQLException {
        try {
            if (this.dataSource == null) {
                this.dataSource = lookupDataSource();
            }
            if (getUser() != null) {
                addWarn("Ignoring property [user] with value [" + getUser() + "] for obtaining a connection from a DataSource.");
            }
            Connection conn = this.dataSource.getConnection();
            return conn;
        } catch (NamingException e) {
            addError("Error while getting data source", e);
            throw new SQLException("NamingException while looking up DataSource: " + e.getMessage());
        } catch (ClassCastException cce) {
            addError("ClassCastException while looking up DataSource.", cce);
            throw new SQLException("ClassCastException while looking up DataSource: " + cce.getMessage());
        }
    }

    public String getJndiLocation() {
        return this.jndiLocation;
    }

    public void setJndiLocation(String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }

    private DataSource lookupDataSource() throws NamingException, SQLException {
        addInfo("Looking up [" + this.jndiLocation + "] in JNDI");
        Object obj = new InitialContext().lookup(this.jndiLocation);
        DataSource ds = (DataSource) obj;
        if (ds == null) {
            throw new SQLException("Failed to obtain data source from JNDI location " + this.jndiLocation);
        }
        return ds;
    }
}