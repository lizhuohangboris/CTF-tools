package org.springframework.boot.autoconfigure.jdbc;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.datasource")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties.class */
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
    private ClassLoader classLoader;
    private String name;
    private boolean generateUniqueName;
    private Class<? extends DataSource> type;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String jndiName;
    private List<String> schema;
    private String schemaUsername;
    private String schemaPassword;
    private List<String> data;
    private String dataUsername;
    private String dataPassword;
    private Charset sqlScriptEncoding;
    private String uniqueName;
    private DataSourceInitializationMode initializationMode = DataSourceInitializationMode.EMBEDDED;
    private String platform = "all";
    private boolean continueOnError = false;
    private String separator = ";";
    private EmbeddedDatabaseConnection embeddedDatabaseConnection = EmbeddedDatabaseConnection.NONE;
    private Xa xa = new Xa();

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        this.embeddedDatabaseConnection = EmbeddedDatabaseConnection.get(this.classLoader);
    }

    public DataSourceBuilder<?> initializeDataSourceBuilder() {
        return DataSourceBuilder.create(getClassLoader()).type(getType()).driverClassName(determineDriverClassName()).url(determineUrl()).username(determineUsername()).password(determinePassword());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGenerateUniqueName() {
        return this.generateUniqueName;
    }

    public void setGenerateUniqueName(boolean generateUniqueName) {
        this.generateUniqueName = generateUniqueName;
    }

    public Class<? extends DataSource> getType() {
        return this.type;
    }

    public void setType(Class<? extends DataSource> type) {
        this.type = type;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String determineDriverClassName() {
        if (StringUtils.hasText(this.driverClassName)) {
            Assert.state(driverClassIsLoadable(), () -> {
                return "Cannot load driver class: " + this.driverClassName;
            });
            return this.driverClassName;
        }
        String driverClassName = null;
        if (StringUtils.hasText(this.url)) {
            driverClassName = DatabaseDriver.fromJdbcUrl(this.url).getDriverClassName();
        }
        if (!StringUtils.hasText(driverClassName)) {
            driverClassName = this.embeddedDatabaseConnection.getDriverClassName();
        }
        if (!StringUtils.hasText(driverClassName)) {
            throw new DataSourceBeanCreationException("Failed to determine a suitable driver class", this, this.embeddedDatabaseConnection);
        }
        return driverClassName;
    }

    private boolean driverClassIsLoadable() {
        try {
            ClassUtils.forName(this.driverClassName, null);
            return true;
        } catch (UnsupportedClassVersionError ex) {
            throw ex;
        } catch (Throwable th) {
            return false;
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String determineUrl() {
        if (StringUtils.hasText(this.url)) {
            return this.url;
        }
        String databaseName = determineDatabaseName();
        String url = databaseName != null ? this.embeddedDatabaseConnection.getUrl(databaseName) : null;
        if (!StringUtils.hasText(url)) {
            throw new DataSourceBeanCreationException("Failed to determine suitable jdbc url", this, this.embeddedDatabaseConnection);
        }
        return url;
    }

    public String determineDatabaseName() {
        if (this.generateUniqueName) {
            if (this.uniqueName == null) {
                this.uniqueName = UUID.randomUUID().toString();
            }
            return this.uniqueName;
        } else if (StringUtils.hasLength(this.name)) {
            return this.name;
        } else {
            if (this.embeddedDatabaseConnection != EmbeddedDatabaseConnection.NONE) {
                return "testdb";
            }
            return null;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String determineUsername() {
        if (StringUtils.hasText(this.username)) {
            return this.username;
        }
        if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName())) {
            return "sa";
        }
        return null;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String determinePassword() {
        if (StringUtils.hasText(this.password)) {
            return this.password;
        }
        if (EmbeddedDatabaseConnection.isEmbedded(determineDriverClassName())) {
            return "";
        }
        return null;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public DataSourceInitializationMode getInitializationMode() {
        return this.initializationMode;
    }

    public void setInitializationMode(DataSourceInitializationMode initializationMode) {
        this.initializationMode = initializationMode;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getSchema() {
        return this.schema;
    }

    public void setSchema(List<String> schema) {
        this.schema = schema;
    }

    public String getSchemaUsername() {
        return this.schemaUsername;
    }

    public void setSchemaUsername(String schemaUsername) {
        this.schemaUsername = schemaUsername;
    }

    public String getSchemaPassword() {
        return this.schemaPassword;
    }

    public void setSchemaPassword(String schemaPassword) {
        this.schemaPassword = schemaPassword;
    }

    public List<String> getData() {
        return this.data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getDataUsername() {
        return this.dataUsername;
    }

    public void setDataUsername(String dataUsername) {
        this.dataUsername = dataUsername;
    }

    public String getDataPassword() {
        return this.dataPassword;
    }

    public void setDataPassword(String dataPassword) {
        this.dataPassword = dataPassword;
    }

    public boolean isContinueOnError() {
        return this.continueOnError;
    }

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public String getSeparator() {
        return this.separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Charset getSqlScriptEncoding() {
        return this.sqlScriptEncoding;
    }

    public void setSqlScriptEncoding(Charset sqlScriptEncoding) {
        this.sqlScriptEncoding = sqlScriptEncoding;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Xa getXa() {
        return this.xa;
    }

    public void setXa(Xa xa) {
        this.xa = xa;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties$Xa.class */
    public static class Xa {
        private String dataSourceClassName;
        private Map<String, String> properties = new LinkedHashMap();

        public String getDataSourceClassName() {
            return this.dataSourceClassName;
        }

        public void setDataSourceClassName(String dataSourceClassName) {
            this.dataSourceClassName = dataSourceClassName;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceProperties$DataSourceBeanCreationException.class */
    public static class DataSourceBeanCreationException extends BeanCreationException {
        private final DataSourceProperties properties;
        private final EmbeddedDatabaseConnection connection;

        DataSourceBeanCreationException(String message, DataSourceProperties properties, EmbeddedDatabaseConnection connection) {
            super(message);
            this.properties = properties;
            this.connection = connection;
        }

        public DataSourceProperties getProperties() {
            return this.properties;
        }

        public EmbeddedDatabaseConnection getConnection() {
            return this.connection;
        }
    }
}