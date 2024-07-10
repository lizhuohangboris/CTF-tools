package org.springframework.boot.autoconfigure.jooq;

import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jooq")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jooq/JooqProperties.class */
public class JooqProperties {
    private SQLDialect sqlDialect;

    public SQLDialect getSqlDialect() {
        return this.sqlDialect;
    }

    public void setSqlDialect(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public SQLDialect determineSqlDialect(DataSource dataSource) {
        if (this.sqlDialect != null) {
            return this.sqlDialect;
        }
        return SqlDialectLookup.getDialect(dataSource);
    }
}