package org.apache.catalina.realm;

import ch.qos.logback.classic.ClassicConstants;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.RealmBase;
import org.apache.tomcat.util.ExceptionUtils;
import org.thymeleaf.spring5.processor.SpringInputPasswordFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/realm/JDBCRealm.class */
public class JDBCRealm extends RealmBase {
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    protected Connection dbConnection = null;
    protected Driver driver = null;
    protected String driverName = null;
    protected PreparedStatement preparedCredentials = null;
    protected PreparedStatement preparedRoles = null;
    protected String roleNameCol = null;
    protected String userCredCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userTable = null;

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRoleNameCol() {
        return this.roleNameCol;
    }

    public void setRoleNameCol(String roleNameCol) {
        this.roleNameCol = roleNameCol;
    }

    public String getUserCredCol() {
        return this.userCredCol;
    }

    public void setUserCredCol(String userCredCol) {
        this.userCredCol = userCredCol;
    }

    public String getUserNameCol() {
        return this.userNameCol;
    }

    public void setUserNameCol(String userNameCol) {
        this.userNameCol = userNameCol;
    }

    public String getUserRoleTable() {
        return this.userRoleTable;
    }

    public void setUserRoleTable(String userRoleTable) {
        this.userRoleTable = userRoleTable;
    }

    public String getUserTable() {
        return this.userTable;
    }

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public synchronized Principal authenticate(String username, String credentials) {
        for (int numberOfTries = 2; numberOfTries > 0; numberOfTries--) {
            try {
                open();
                Principal principal = authenticate(this.dbConnection, username, credentials);
                return principal;
            } catch (SQLException e) {
                this.containerLog.error(sm.getString("jdbcRealm.exception"), e);
                if (this.dbConnection != null) {
                    close(this.dbConnection);
                }
            }
        }
        return null;
    }

    public synchronized Principal authenticate(Connection dbConnection, String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("jdbcRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        String dbCredentials = getPassword(username);
        if (dbCredentials == null) {
            getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("jdbcRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, dbCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("jdbcRealm.authenticateSuccess", username));
            }
            ArrayList<String> roles = getRoles(username);
            return new GenericPrincipal(username, credentials, roles);
        } else if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("jdbcRealm.authenticateFailure", username));
            return null;
        } else {
            return null;
        }
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        return this.dbConnection != null;
    }

    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            this.preparedCredentials.close();
        } catch (Throwable f) {
            ExceptionUtils.handleThrowable(f);
        }
        this.preparedCredentials = null;
        try {
            this.preparedRoles.close();
        } catch (Throwable f2) {
            ExceptionUtils.handleThrowable(f2);
        }
        this.preparedRoles = null;
        try {
            dbConnection.close();
        } catch (SQLException e) {
            this.containerLog.warn(sm.getString("jdbcRealm.close"), e);
        } finally {
            this.dbConnection = null;
        }
    }

    protected PreparedStatement credentials(Connection dbConnection, String username) throws SQLException {
        if (this.preparedCredentials == null) {
            StringBuilder sb = new StringBuilder("SELECT ");
            sb.append(this.userCredCol);
            sb.append(" FROM ");
            sb.append(this.userTable);
            sb.append(" WHERE ");
            sb.append(this.userNameCol);
            sb.append(" = ?");
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("credentials query: " + sb.toString());
            }
            this.preparedCredentials = dbConnection.prepareStatement(sb.toString());
        }
        if (username == null) {
            this.preparedCredentials.setNull(1, 12);
        } else {
            this.preparedCredentials.setString(1, username);
        }
        return this.preparedCredentials;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected synchronized String getPassword(String username) {
        String dbCredentials = null;
        for (int numberOfTries = 2; numberOfTries > 0; numberOfTries--) {
            try {
                open();
                PreparedStatement stmt = credentials(this.dbConnection, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dbCredentials = rs.getString(1);
                }
                this.dbConnection.commit();
                if (dbCredentials != null) {
                    dbCredentials = dbCredentials.trim();
                }
                String str = dbCredentials;
                if (rs != null) {
                    if (0 != 0) {
                        rs.close();
                    } else {
                        rs.close();
                    }
                }
                return str;
            } catch (SQLException e) {
                this.containerLog.error(sm.getString("jdbcRealm.exception"), e);
                if (this.dbConnection != null) {
                    close(this.dbConnection);
                }
            }
        }
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected synchronized Principal getPrincipal(String username) {
        return new GenericPrincipal(username, getPassword(username), getRoles(username));
    }

    protected ArrayList<String> getRoles(String username) {
        if (this.allRolesMode != RealmBase.AllRolesMode.STRICT_MODE && !isRoleStoreDefined()) {
            return null;
        }
        for (int numberOfTries = 2; numberOfTries > 0; numberOfTries--) {
            try {
                open();
                PreparedStatement stmt = roles(this.dbConnection, username);
                ResultSet rs = stmt.executeQuery();
                Throwable th = null;
                try {
                    ArrayList<String> roleList = new ArrayList<>();
                    while (rs.next()) {
                        String role = rs.getString(1);
                        if (null != role) {
                            roleList.add(role.trim());
                        }
                    }
                    if (rs != null) {
                        if (0 != 0) {
                            try {
                                rs.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            rs.close();
                        }
                    }
                    this.dbConnection.commit();
                    return roleList;
                } finally {
                }
            } catch (SQLException e) {
                this.containerLog.error(sm.getString("jdbcRealm.exception"), e);
                if (this.dbConnection != null) {
                    close(this.dbConnection);
                }
            }
        }
        return null;
    }

    protected Connection open() throws SQLException {
        if (this.dbConnection != null) {
            return this.dbConnection;
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                throw new SQLException(e.getMessage(), e);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put(ClassicConstants.USER_MDC_KEY, this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put(SpringInputPasswordFieldTagProcessor.PASSWORD_INPUT_TYPE_ATTR_VALUE, this.connectionPassword);
        }
        this.dbConnection = this.driver.connect(this.connectionURL, props);
        if (this.dbConnection == null) {
            throw new SQLException(sm.getString("jdbcRealm.open.invalidurl", this.driverName, this.connectionURL));
        }
        this.dbConnection.setAutoCommit(false);
        return this.dbConnection;
    }

    protected synchronized PreparedStatement roles(Connection dbConnection, String username) throws SQLException {
        if (this.preparedRoles == null) {
            this.preparedRoles = dbConnection.prepareStatement("SELECT " + this.roleNameCol + " FROM " + this.userRoleTable + " WHERE " + this.userNameCol + " = ?");
        }
        this.preparedRoles.setString(1, username);
        return this.preparedRoles;
    }

    private boolean isRoleStoreDefined() {
        return (this.userRoleTable == null && this.roleNameCol == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        try {
            open();
        } catch (SQLException e) {
            this.containerLog.error(sm.getString("jdbcRealm.open"), e);
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        close(this.dbConnection);
    }
}