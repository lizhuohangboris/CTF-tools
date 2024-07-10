package org.springframework.boot.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection.class */
public enum EmbeddedDatabaseConnection {
    NONE(null, null, null),
    H2(EmbeddedDatabaseType.H2, "org.h2.Driver", "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"),
    DERBY(EmbeddedDatabaseType.DERBY, "org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:memory:%s;create=true"),
    HSQL(EmbeddedDatabaseType.HSQL, "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:%s");
    
    private final EmbeddedDatabaseType type;
    private final String driverClass;
    private final String url;

    EmbeddedDatabaseConnection(EmbeddedDatabaseType type, String driverClass, String url) {
        this.type = type;
        this.driverClass = driverClass;
        this.url = url;
    }

    public String getDriverClassName() {
        return this.driverClass;
    }

    public EmbeddedDatabaseType getType() {
        return this.type;
    }

    public String getUrl(String databaseName) {
        Assert.hasText(databaseName, "DatabaseName must not be empty");
        if (this.url != null) {
            return String.format(this.url, databaseName);
        }
        return null;
    }

    public static boolean isEmbedded(String driverClass) {
        return driverClass != null && (driverClass.equals(HSQL.driverClass) || driverClass.equals(H2.driverClass) || driverClass.equals(DERBY.driverClass));
    }

    public static boolean isEmbedded(DataSource dataSource) {
        try {
            return ((Boolean) new JdbcTemplate(dataSource).execute(new IsEmbedded())).booleanValue();
        } catch (DataAccessException e) {
            return false;
        }
    }

    public static EmbeddedDatabaseConnection get(ClassLoader classLoader) {
        EmbeddedDatabaseConnection[] values;
        for (EmbeddedDatabaseConnection candidate : values()) {
            if (candidate != NONE && ClassUtils.isPresent(candidate.getDriverClassName(), classLoader)) {
                return candidate;
            }
        }
        return NONE;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection$IsEmbedded.class */
    private static class IsEmbedded implements ConnectionCallback<Boolean> {
        private IsEmbedded() {
        }

        /* renamed from: doInConnection */
        public Boolean m1399doInConnection(Connection connection) throws SQLException, DataAccessException {
            String productName = connection.getMetaData().getDatabaseProductName();
            if (productName == null) {
                return false;
            }
            String productName2 = productName.toUpperCase(Locale.ENGLISH);
            EmbeddedDatabaseConnection[] candidates = EmbeddedDatabaseConnection.values();
            for (EmbeddedDatabaseConnection candidate : candidates) {
                if (candidate != EmbeddedDatabaseConnection.NONE && productName2.contains(candidate.name())) {
                    return true;
                }
            }
            return false;
        }
    }
}