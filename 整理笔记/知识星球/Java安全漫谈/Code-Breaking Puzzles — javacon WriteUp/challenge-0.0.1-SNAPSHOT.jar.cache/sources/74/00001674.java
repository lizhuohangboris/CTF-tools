package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.Flyway;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayMigrationStrategy.class */
public interface FlywayMigrationStrategy {
    void migrate(Flyway flyway);
}