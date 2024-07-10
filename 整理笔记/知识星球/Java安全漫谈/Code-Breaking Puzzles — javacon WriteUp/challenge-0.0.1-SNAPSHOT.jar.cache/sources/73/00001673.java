package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayMigrationInitializer.class */
public class FlywayMigrationInitializer implements InitializingBean, Ordered {
    private final Flyway flyway;
    private final FlywayMigrationStrategy migrationStrategy;
    private int order;

    public FlywayMigrationInitializer(Flyway flyway) {
        this(flyway, null);
    }

    public FlywayMigrationInitializer(Flyway flyway, FlywayMigrationStrategy migrationStrategy) {
        this.order = 0;
        Assert.notNull(flyway, "Flyway must not be null");
        this.flyway = flyway;
        this.migrationStrategy = migrationStrategy;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.migrationStrategy != null) {
            this.migrationStrategy.migrate(this.flyway);
        } else {
            this.flyway.migrate();
        }
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}