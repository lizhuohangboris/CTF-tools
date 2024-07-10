package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jdbc/DataSourceInitializerInvoker.class */
class DataSourceInitializerInvoker implements ApplicationListener<DataSourceSchemaCreatedEvent>, InitializingBean {
    private static final Log logger = LogFactory.getLog(DataSourceInitializerInvoker.class);
    private final ObjectProvider<DataSource> dataSource;
    private final DataSourceProperties properties;
    private final ApplicationContext applicationContext;
    private DataSourceInitializer dataSourceInitializer;
    private boolean initialized;

    DataSourceInitializerInvoker(ObjectProvider<DataSource> dataSource, DataSourceProperties properties, ApplicationContext applicationContext) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        DataSourceInitializer initializer = getDataSourceInitializer();
        if (initializer != null) {
            boolean schemaCreated = this.dataSourceInitializer.createSchema();
            if (schemaCreated) {
                initialize(initializer);
            }
        }
    }

    private void initialize(DataSourceInitializer initializer) {
        try {
            this.applicationContext.publishEvent((ApplicationEvent) new DataSourceSchemaCreatedEvent(initializer.getDataSource()));
            if (!this.initialized) {
                this.dataSourceInitializer.initSchema();
                this.initialized = true;
            }
        } catch (IllegalStateException ex) {
            logger.warn("Could not send event to complete DataSource initialization (" + ex.getMessage() + ")");
        }
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(DataSourceSchemaCreatedEvent event) {
        DataSourceInitializer initializer = getDataSourceInitializer();
        if (!this.initialized && initializer != null) {
            initializer.initSchema();
            this.initialized = true;
        }
    }

    private DataSourceInitializer getDataSourceInitializer() {
        DataSource ds;
        if (this.dataSourceInitializer == null && (ds = this.dataSource.getIfUnique()) != null) {
            this.dataSourceInitializer = new DataSourceInitializer(ds, this.properties, this.applicationContext);
        }
        return this.dataSourceInitializer;
    }
}