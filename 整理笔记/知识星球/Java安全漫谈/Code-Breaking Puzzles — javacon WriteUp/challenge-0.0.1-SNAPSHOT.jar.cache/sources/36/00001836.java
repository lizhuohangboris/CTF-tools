package org.springframework.boot.autoconfigure.transaction.jta;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jta", ignoreUnknownFields = true)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/transaction/jta/JtaProperties.class */
public class JtaProperties {
    private String logDir;
    private String transactionManagerId;

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getLogDir() {
        return this.logDir;
    }

    public String getTransactionManagerId() {
        return this.transactionManagerId;
    }

    public void setTransactionManagerId(String transactionManagerId) {
        this.transactionManagerId = transactionManagerId;
    }
}