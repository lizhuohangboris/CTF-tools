package org.springframework.boot.autoconfigure.flyway;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.SchemaManagement;
import org.springframework.boot.jdbc.SchemaManagementProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywaySchemaManagementProvider.class */
class FlywaySchemaManagementProvider implements SchemaManagementProvider {
    private final Iterable<Flyway> flywayInstances;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlywaySchemaManagementProvider(Iterable<Flyway> flywayInstances) {
        this.flywayInstances = flywayInstances;
    }

    @Override // org.springframework.boot.jdbc.SchemaManagementProvider
    public SchemaManagement getSchemaManagement(DataSource dataSource) {
        Stream map = StreamSupport.stream(this.flywayInstances.spliterator(), false).map(flyway -> {
            return flyway.getConfiguration().getDataSource();
        });
        dataSource.getClass();
        return (SchemaManagement) map.filter((v1) -> {
            return r1.equals(v1);
        }).findFirst().map(managedDataSource -> {
            return SchemaManagement.MANAGED;
        }).orElse(SchemaManagement.UNMANAGED);
    }
}