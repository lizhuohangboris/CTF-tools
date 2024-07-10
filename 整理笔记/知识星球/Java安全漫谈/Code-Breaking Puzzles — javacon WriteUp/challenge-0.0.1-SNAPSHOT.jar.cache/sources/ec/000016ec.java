package org.springframework.boot.autoconfigure.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.config.SortedResourcesFactoryBean;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceInitializer.class */
class DataSourceInitializer {
    private static final Log logger = LogFactory.getLog(DataSourceInitializer.class);
    private final DataSource dataSource;
    private final DataSourceProperties properties;
    private final ResourceLoader resourceLoader;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataSourceInitializer(DataSource dataSource, DataSourceProperties properties, ResourceLoader resourceLoader) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
    }

    DataSourceInitializer(DataSource dataSource, DataSourceProperties properties) {
        this(dataSource, properties, null);
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public boolean createSchema() {
        List<Resource> scripts = getScripts("spring.datasource.schema", this.properties.getSchema(), "schema");
        if (!scripts.isEmpty()) {
            if (!isEnabled()) {
                logger.debug("Initialization disabled (not running DDL scripts)");
                return false;
            }
            String username = this.properties.getSchemaUsername();
            String password = this.properties.getSchemaPassword();
            runScripts(scripts, username, password);
        }
        return !scripts.isEmpty();
    }

    public void initSchema() {
        List<Resource> scripts = getScripts("spring.datasource.data", this.properties.getData(), "data");
        if (!scripts.isEmpty()) {
            if (!isEnabled()) {
                logger.debug("Initialization disabled (not running data scripts)");
                return;
            }
            String username = this.properties.getDataUsername();
            String password = this.properties.getDataPassword();
            runScripts(scripts, username, password);
        }
    }

    private boolean isEnabled() {
        DataSourceInitializationMode mode = this.properties.getInitializationMode();
        if (mode == DataSourceInitializationMode.NEVER) {
            return false;
        }
        if (mode == DataSourceInitializationMode.EMBEDDED && !isEmbedded()) {
            return false;
        }
        return true;
    }

    private boolean isEmbedded() {
        try {
            return EmbeddedDatabaseConnection.isEmbedded(this.dataSource);
        } catch (Exception ex) {
            logger.debug("Could not determine if datasource is embedded", ex);
            return false;
        }
    }

    private List<Resource> getScripts(String propertyName, List<String> resources, String fallback) {
        if (resources != null) {
            return getResources(propertyName, resources, true);
        }
        String platform = this.properties.getPlatform();
        List<String> fallbackResources = new ArrayList<>();
        fallbackResources.add(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + fallback + "-" + platform + ".sql");
        fallbackResources.add(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + fallback + ".sql");
        return getResources(propertyName, fallbackResources, false);
    }

    private List<Resource> getResources(String propertyName, List<String> locations, boolean validate) {
        Resource[] doGetResources;
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            for (Resource resource : doGetResources(location)) {
                if (resource.exists()) {
                    resources.add(resource);
                } else if (validate) {
                    throw new InvalidConfigurationPropertyValueException(propertyName, resource, "The specified resource does not exist.");
                }
            }
        }
        return resources;
    }

    private Resource[] doGetResources(String location) {
        try {
            SortedResourcesFactoryBean factory = new SortedResourcesFactoryBean(this.resourceLoader, Collections.singletonList(location));
            factory.afterPropertiesSet();
            return (Resource[]) factory.getObject();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load resources from " + location, ex);
        }
    }

    private void runScripts(List<Resource> resources, String username, String password) {
        if (resources.isEmpty()) {
            return;
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(this.properties.isContinueOnError());
        populator.setSeparator(this.properties.getSeparator());
        if (this.properties.getSqlScriptEncoding() != null) {
            populator.setSqlScriptEncoding(this.properties.getSqlScriptEncoding().name());
        }
        for (Resource resource : resources) {
            populator.addScript(resource);
        }
        DataSource dataSource = this.dataSource;
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            dataSource = DataSourceBuilder.create(this.properties.getClassLoader()).driverClassName(this.properties.determineDriverClassName()).url(this.properties.determineUrl()).username(username).password(password).build();
        }
        DatabasePopulatorUtils.execute(populator, dataSource);
    }
}