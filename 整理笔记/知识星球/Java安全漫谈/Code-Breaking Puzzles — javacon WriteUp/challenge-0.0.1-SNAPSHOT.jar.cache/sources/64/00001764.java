package org.springframework.boot.autoconfigure.liquibase;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.SchemaManagement;
import org.springframework.boot.jdbc.SchemaManagementProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseSchemaManagementProvider.class */
class LiquibaseSchemaManagementProvider implements SchemaManagementProvider {
    private final Iterable<SpringLiquibase> liquibaseInstances;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LiquibaseSchemaManagementProvider(ObjectProvider<SpringLiquibase> liquibases) {
        this.liquibaseInstances = liquibases;
    }

    @Override // org.springframework.boot.jdbc.SchemaManagementProvider
    public SchemaManagement getSchemaManagement(DataSource dataSource) {
        Stream map = StreamSupport.stream(this.liquibaseInstances.spliterator(), false).map((v0) -> {
            return v0.getDataSource();
        });
        dataSource.getClass();
        return (SchemaManagement) map.filter((v1) -> {
            return r1.equals(v1);
        }).findFirst().map(managedDataSource -> {
            return SchemaManagement.MANAGED;
        }).orElse(SchemaManagement.UNMANAGED);
    }
}