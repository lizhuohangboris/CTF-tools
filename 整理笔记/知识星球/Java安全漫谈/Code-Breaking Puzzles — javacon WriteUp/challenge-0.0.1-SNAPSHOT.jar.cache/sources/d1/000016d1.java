package org.springframework.boot.autoconfigure.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

@ConfigurationProperties(prefix = "spring.integration")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties.class */
public class IntegrationProperties {
    private final Jdbc jdbc = new Jdbc();

    public Jdbc getJdbc() {
        return this.jdbc;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/integration/IntegrationProperties$Jdbc.class */
    public static class Jdbc {
        private static final String DEFAULT_SCHEMA_LOCATION = "classpath:org/springframework/integration/jdbc/schema-@@platform@@.sql";
        private String schema = DEFAULT_SCHEMA_LOCATION;
        private DataSourceInitializationMode initializeSchema = DataSourceInitializationMode.EMBEDDED;

        public String getSchema() {
            return this.schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        public DataSourceInitializationMode getInitializeSchema() {
            return this.initializeSchema;
        }

        public void setInitializeSchema(DataSourceInitializationMode initializeSchema) {
            this.initializeSchema = initializeSchema;
        }
    }
}