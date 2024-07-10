package org.springframework.boot.autoconfigure.flyway;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.flyway")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayProperties.class */
public class FlywayProperties {
    private int connectRetries;
    private String installedBy;
    private String target;
    private String url;
    private String user;
    private String password;
    private boolean baselineOnMigrate;
    private boolean cleanDisabled;
    private boolean cleanOnValidationError;
    private boolean group;
    private boolean ignoreMissingMigrations;
    private boolean ignoreIgnoredMigrations;
    private boolean ignorePendingMigrations;
    private boolean mixed;
    private boolean outOfOrder;
    private boolean skipDefaultCallbacks;
    private boolean skipDefaultResolvers;
    private boolean enabled = true;
    private boolean checkLocation = true;
    private List<String> locations = new ArrayList(Collections.singletonList("classpath:db/migration"));
    private Charset encoding = StandardCharsets.UTF_8;
    private List<String> schemas = new ArrayList();
    private String table = "flyway_schema_history";
    private String baselineDescription = "<< Flyway Baseline >>";
    private String baselineVersion = CustomBooleanEditor.VALUE_1;
    private Map<String, String> placeholders = new HashMap();
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";
    private boolean placeholderReplacement = true;
    private String sqlMigrationPrefix = "V";
    private List<String> sqlMigrationSuffixes = new ArrayList(Collections.singleton(".sql"));
    private String sqlMigrationSeparator = "__";
    private String repeatableSqlMigrationPrefix = "R";
    private List<String> initSqls = new ArrayList();
    private boolean ignoreFutureMigrations = true;
    private boolean validateOnMigrate = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCheckLocation() {
        return this.checkLocation;
    }

    public void setCheckLocation(boolean checkLocation) {
        this.checkLocation = checkLocation;
    }

    public List<String> getLocations() {
        return this.locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public int getConnectRetries() {
        return this.connectRetries;
    }

    public void setConnectRetries(int connectRetries) {
        this.connectRetries = connectRetries;
    }

    public List<String> getSchemas() {
        return this.schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getBaselineDescription() {
        return this.baselineDescription;
    }

    public void setBaselineDescription(String baselineDescription) {
        this.baselineDescription = baselineDescription;
    }

    public String getBaselineVersion() {
        return this.baselineVersion;
    }

    public void setBaselineVersion(String baselineVersion) {
        this.baselineVersion = baselineVersion;
    }

    public String getInstalledBy() {
        return this.installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public Map<String, String> getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getPlaceholderPrefix() {
        return this.placeholderPrefix;
    }

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public String getPlaceholderSuffix() {
        return this.placeholderSuffix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    public boolean isPlaceholderReplacement() {
        return this.placeholderReplacement;
    }

    public void setPlaceholderReplacement(boolean placeholderReplacement) {
        this.placeholderReplacement = placeholderReplacement;
    }

    public String getSqlMigrationPrefix() {
        return this.sqlMigrationPrefix;
    }

    public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
        this.sqlMigrationPrefix = sqlMigrationPrefix;
    }

    public List<String> getSqlMigrationSuffixes() {
        return this.sqlMigrationSuffixes;
    }

    public void setSqlMigrationSuffixes(List<String> sqlMigrationSuffixes) {
        this.sqlMigrationSuffixes = sqlMigrationSuffixes;
    }

    public String getSqlMigrationSeparator() {
        return this.sqlMigrationSeparator;
    }

    public void setSqlMigrationSeparator(String sqlMigrationSeparator) {
        this.sqlMigrationSeparator = sqlMigrationSeparator;
    }

    public String getRepeatableSqlMigrationPrefix() {
        return this.repeatableSqlMigrationPrefix;
    }

    public void setRepeatableSqlMigrationPrefix(String repeatableSqlMigrationPrefix) {
        this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isCreateDataSource() {
        return (this.url == null && this.user == null) ? false : true;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password != null ? this.password : "";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getInitSqls() {
        return this.initSqls;
    }

    public void setInitSqls(List<String> initSqls) {
        this.initSqls = initSqls;
    }

    public boolean isBaselineOnMigrate() {
        return this.baselineOnMigrate;
    }

    public void setBaselineOnMigrate(boolean baselineOnMigrate) {
        this.baselineOnMigrate = baselineOnMigrate;
    }

    public boolean isCleanDisabled() {
        return this.cleanDisabled;
    }

    public void setCleanDisabled(boolean cleanDisabled) {
        this.cleanDisabled = cleanDisabled;
    }

    public boolean isCleanOnValidationError() {
        return this.cleanOnValidationError;
    }

    public void setCleanOnValidationError(boolean cleanOnValidationError) {
        this.cleanOnValidationError = cleanOnValidationError;
    }

    public boolean isGroup() {
        return this.group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isIgnoreMissingMigrations() {
        return this.ignoreMissingMigrations;
    }

    public void setIgnoreMissingMigrations(boolean ignoreMissingMigrations) {
        this.ignoreMissingMigrations = ignoreMissingMigrations;
    }

    public boolean isIgnoreIgnoredMigrations() {
        return this.ignoreIgnoredMigrations;
    }

    public void setIgnoreIgnoredMigrations(boolean ignoreIgnoredMigrations) {
        this.ignoreIgnoredMigrations = ignoreIgnoredMigrations;
    }

    public boolean isIgnorePendingMigrations() {
        return this.ignorePendingMigrations;
    }

    public void setIgnorePendingMigrations(boolean ignorePendingMigrations) {
        this.ignorePendingMigrations = ignorePendingMigrations;
    }

    public boolean isIgnoreFutureMigrations() {
        return this.ignoreFutureMigrations;
    }

    public void setIgnoreFutureMigrations(boolean ignoreFutureMigrations) {
        this.ignoreFutureMigrations = ignoreFutureMigrations;
    }

    public boolean isMixed() {
        return this.mixed;
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public boolean isOutOfOrder() {
        return this.outOfOrder;
    }

    public void setOutOfOrder(boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public boolean isSkipDefaultCallbacks() {
        return this.skipDefaultCallbacks;
    }

    public void setSkipDefaultCallbacks(boolean skipDefaultCallbacks) {
        this.skipDefaultCallbacks = skipDefaultCallbacks;
    }

    public boolean isSkipDefaultResolvers() {
        return this.skipDefaultResolvers;
    }

    public void setSkipDefaultResolvers(boolean skipDefaultResolvers) {
        this.skipDefaultResolvers = skipDefaultResolvers;
    }

    public boolean isValidateOnMigrate() {
        return this.validateOnMigrate;
    }

    public void setValidateOnMigrate(boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
    }
}