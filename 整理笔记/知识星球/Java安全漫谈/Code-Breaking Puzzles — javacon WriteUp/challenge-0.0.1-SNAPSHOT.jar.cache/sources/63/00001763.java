package org.springframework.boot.autoconfigure.liquibase;

import java.io.File;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.liquibase", ignoreUnknownFields = false)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/liquibase/LiquibaseProperties.class */
public class LiquibaseProperties {
    private String contexts;
    private String defaultSchema;
    private String liquibaseSchema;
    private String liquibaseTablespace;
    private boolean dropFirst;
    private String user;
    private String password;
    private String url;
    private String labels;
    private Map<String, String> parameters;
    private File rollbackFile;
    private boolean testRollbackOnUpdate;
    private String changeLog = "classpath:/db/changelog/db.changelog-master.yaml";
    private boolean checkChangeLogLocation = true;
    private String databaseChangeLogTable = "DATABASECHANGELOG";
    private String databaseChangeLogLockTable = "DATABASECHANGELOGLOCK";
    private boolean enabled = true;

    public String getChangeLog() {
        return this.changeLog;
    }

    public void setChangeLog(String changeLog) {
        Assert.notNull(changeLog, "ChangeLog must not be null");
        this.changeLog = changeLog;
    }

    public boolean isCheckChangeLogLocation() {
        return this.checkChangeLogLocation;
    }

    public void setCheckChangeLogLocation(boolean checkChangeLogLocation) {
        this.checkChangeLogLocation = checkChangeLogLocation;
    }

    public String getContexts() {
        return this.contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public String getDefaultSchema() {
        return this.defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public String getLiquibaseSchema() {
        return this.liquibaseSchema;
    }

    public void setLiquibaseSchema(String liquibaseSchema) {
        this.liquibaseSchema = liquibaseSchema;
    }

    public String getLiquibaseTablespace() {
        return this.liquibaseTablespace;
    }

    public void setLiquibaseTablespace(String liquibaseTablespace) {
        this.liquibaseTablespace = liquibaseTablespace;
    }

    public String getDatabaseChangeLogTable() {
        return this.databaseChangeLogTable;
    }

    public void setDatabaseChangeLogTable(String databaseChangeLogTable) {
        this.databaseChangeLogTable = databaseChangeLogTable;
    }

    public String getDatabaseChangeLogLockTable() {
        return this.databaseChangeLogLockTable;
    }

    public void setDatabaseChangeLogLockTable(String databaseChangeLogLockTable) {
        this.databaseChangeLogLockTable = databaseChangeLogLockTable;
    }

    public boolean isDropFirst() {
        return this.dropFirst;
    }

    public void setDropFirst(boolean dropFirst) {
        this.dropFirst = dropFirst;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabels() {
        return this.labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public File getRollbackFile() {
        return this.rollbackFile;
    }

    public void setRollbackFile(File rollbackFile) {
        this.rollbackFile = rollbackFile;
    }

    public boolean isTestRollbackOnUpdate() {
        return this.testRollbackOnUpdate;
    }

    public void setTestRollbackOnUpdate(boolean testRollbackOnUpdate) {
        this.testRollbackOnUpdate = testRollbackOnUpdate;
    }
}