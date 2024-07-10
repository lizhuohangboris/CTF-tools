package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/HikariDriverConfigurationFailureAnalyzer.class */
class HikariDriverConfigurationFailureAnalyzer extends AbstractFailureAnalyzer<CannotGetJdbcConnectionException> {
    private static final String EXPECTED_MESSAGE = "Failed to obtain JDBC Connection: cannot use driverClassName and dataSourceClassName together.";

    HikariDriverConfigurationFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, CannotGetJdbcConnectionException cause) {
        if (!EXPECTED_MESSAGE.equals(cause.getMessage())) {
            return null;
        }
        return new FailureAnalysis("Configuration of the Hikari connection pool failed: 'dataSourceClassName' is not supported.", "Spring Boot auto-configures only a driver and can't specify a custom DataSource. Consider configuring the Hikari DataSource in your own configuration.", cause);
    }
}