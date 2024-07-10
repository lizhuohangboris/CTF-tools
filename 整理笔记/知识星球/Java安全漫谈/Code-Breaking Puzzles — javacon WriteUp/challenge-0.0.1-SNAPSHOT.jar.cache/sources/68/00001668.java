package org.springframework.boot.autoconfigure.flyway;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcOperationsDependsOnPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({Flyway.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnBean({DataSource.class})
@ConditionalOnProperty(prefix = "spring.flyway", name = {"enabled"}, matchIfMissing = true)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration.class */
public class FlywayAutoConfiguration {
    @ConfigurationPropertiesBinding
    @Bean
    public StringOrNumberToMigrationVersionConverter stringOrNumberMigrationVersionConverter() {
        return new StringOrNumberToMigrationVersionConverter();
    }

    @Bean
    public FlywaySchemaManagementProvider flywayDefaultDdlModeProvider(ObjectProvider<Flyway> flyways) {
        return new FlywaySchemaManagementProvider(flyways);
    }

    @ConditionalOnMissingBean({Flyway.class})
    @EnableConfigurationProperties({DataSourceProperties.class, FlywayProperties.class})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration.class */
    public static class FlywayConfiguration {
        private final FlywayProperties properties;
        private final DataSourceProperties dataSourceProperties;
        private final ResourceLoader resourceLoader;
        private final DataSource dataSource;
        private final DataSource flywayDataSource;
        private final FlywayMigrationStrategy migrationStrategy;
        private final List<FlywayConfigurationCustomizer> configurationCustomizers;
        private final List<Callback> callbacks;
        private final List<FlywayCallback> flywayCallbacks;

        public FlywayConfiguration(FlywayProperties properties, DataSourceProperties dataSourceProperties, ResourceLoader resourceLoader, ObjectProvider<DataSource> dataSource, @FlywayDataSource ObjectProvider<DataSource> flywayDataSource, ObjectProvider<FlywayMigrationStrategy> migrationStrategy, ObjectProvider<FlywayConfigurationCustomizer> fluentConfigurationCustomizers, ObjectProvider<Callback> callbacks, ObjectProvider<FlywayCallback> flywayCallbacks) {
            this.properties = properties;
            this.dataSourceProperties = dataSourceProperties;
            this.resourceLoader = resourceLoader;
            this.dataSource = dataSource.getIfUnique();
            this.flywayDataSource = flywayDataSource.getIfAvailable();
            this.migrationStrategy = migrationStrategy.getIfAvailable();
            this.configurationCustomizers = (List) fluentConfigurationCustomizers.orderedStream().collect(Collectors.toList());
            this.callbacks = (List) callbacks.orderedStream().collect(Collectors.toList());
            this.flywayCallbacks = (List) flywayCallbacks.orderedStream().collect(Collectors.toList());
        }

        @Bean
        public Flyway flyway() {
            FluentConfiguration configuration = new FluentConfiguration();
            DataSource dataSource = configureDataSource(configuration);
            checkLocationExists(dataSource);
            configureProperties(configuration);
            configureCallbacks(configuration);
            this.configurationCustomizers.forEach(customizer -> {
                customizer.customize(configuration);
            });
            Flyway flyway = configuration.load();
            configureFlywayCallbacks(flyway);
            return flyway;
        }

        private DataSource configureDataSource(FluentConfiguration configuration) {
            if (this.properties.isCreateDataSource()) {
                FlywayProperties flywayProperties = this.properties;
                flywayProperties.getClass();
                Supplier<String> supplier = this::getUrl;
                DataSourceProperties dataSourceProperties = this.dataSourceProperties;
                dataSourceProperties.getClass();
                String url = getProperty(supplier, this::getUrl);
                FlywayProperties flywayProperties2 = this.properties;
                flywayProperties2.getClass();
                Supplier<String> supplier2 = this::getUser;
                DataSourceProperties dataSourceProperties2 = this.dataSourceProperties;
                dataSourceProperties2.getClass();
                String user = getProperty(supplier2, this::getUsername);
                FlywayProperties flywayProperties3 = this.properties;
                flywayProperties3.getClass();
                Supplier<String> supplier3 = this::getPassword;
                DataSourceProperties dataSourceProperties3 = this.dataSourceProperties;
                dataSourceProperties3.getClass();
                String password = getProperty(supplier3, this::getPassword);
                configuration.dataSource(url, user, password);
                if (!CollectionUtils.isEmpty(this.properties.getInitSqls())) {
                    String initSql = StringUtils.collectionToDelimitedString(this.properties.getInitSqls(), "\n");
                    configuration.initSql(initSql);
                }
            } else if (this.flywayDataSource != null) {
                configuration.dataSource(this.flywayDataSource);
            } else {
                configuration.dataSource(this.dataSource);
            }
            return configuration.getDataSource();
        }

        private void checkLocationExists(DataSource dataSource) {
            if (this.properties.isCheckLocation()) {
                String[] locations = new LocationResolver(dataSource).resolveLocations(this.properties.getLocations());
                Assert.state(locations.length != 0, "Migration script locations not configured");
                boolean exists = hasAtLeastOneLocation(locations);
                Assert.state(exists, () -> {
                    return "Cannot find migrations location in: " + Arrays.asList(locations) + " (please add migrations or check your Flyway configuration)";
                });
            }
        }

        private void configureProperties(FluentConfiguration configuration) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            String[] locations = new LocationResolver(configuration.getDataSource()).resolveLocations(this.properties.getLocations());
            PropertyMapper.Source from = map.from((PropertyMapper) locations);
            configuration.getClass();
            from.to(this::locations);
            PropertyMapper.Source from2 = map.from((PropertyMapper) this.properties.getEncoding());
            configuration.getClass();
            from2.to(this::encoding);
            PropertyMapper.Source from3 = map.from((PropertyMapper) Integer.valueOf(this.properties.getConnectRetries()));
            configuration.getClass();
            from3.to((v1) -> {
                r1.connectRetries(v1);
            });
            PropertyMapper.Source as = map.from((PropertyMapper) this.properties.getSchemas()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            });
            configuration.getClass();
            as.to(this::schemas);
            PropertyMapper.Source from4 = map.from((PropertyMapper) this.properties.getTable());
            configuration.getClass();
            from4.to(this::table);
            PropertyMapper.Source from5 = map.from((PropertyMapper) this.properties.getBaselineDescription());
            configuration.getClass();
            from5.to(this::baselineDescription);
            PropertyMapper.Source from6 = map.from((PropertyMapper) this.properties.getBaselineVersion());
            configuration.getClass();
            from6.to(this::baselineVersion);
            PropertyMapper.Source from7 = map.from((PropertyMapper) this.properties.getInstalledBy());
            configuration.getClass();
            from7.to(this::installedBy);
            PropertyMapper.Source from8 = map.from((PropertyMapper) this.properties.getPlaceholders());
            configuration.getClass();
            from8.to(this::placeholders);
            PropertyMapper.Source from9 = map.from((PropertyMapper) this.properties.getPlaceholderPrefix());
            configuration.getClass();
            from9.to(this::placeholderPrefix);
            PropertyMapper.Source from10 = map.from((PropertyMapper) this.properties.getPlaceholderSuffix());
            configuration.getClass();
            from10.to(this::placeholderSuffix);
            PropertyMapper.Source from11 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isPlaceholderReplacement()));
            configuration.getClass();
            from11.to((v1) -> {
                r1.placeholderReplacement(v1);
            });
            PropertyMapper.Source from12 = map.from((PropertyMapper) this.properties.getSqlMigrationPrefix());
            configuration.getClass();
            from12.to(this::sqlMigrationPrefix);
            PropertyMapper.Source as2 = map.from((PropertyMapper) this.properties.getSqlMigrationSuffixes()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            });
            configuration.getClass();
            as2.to(this::sqlMigrationSuffixes);
            PropertyMapper.Source from13 = map.from((PropertyMapper) this.properties.getSqlMigrationSeparator());
            configuration.getClass();
            from13.to(this::sqlMigrationSeparator);
            PropertyMapper.Source from14 = map.from((PropertyMapper) this.properties.getRepeatableSqlMigrationPrefix());
            configuration.getClass();
            from14.to(this::repeatableSqlMigrationPrefix);
            PropertyMapper.Source from15 = map.from((PropertyMapper) this.properties.getTarget());
            configuration.getClass();
            from15.to(this::target);
            PropertyMapper.Source from16 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isBaselineOnMigrate()));
            configuration.getClass();
            from16.to((v1) -> {
                r1.baselineOnMigrate(v1);
            });
            PropertyMapper.Source from17 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isCleanDisabled()));
            configuration.getClass();
            from17.to((v1) -> {
                r1.cleanDisabled(v1);
            });
            PropertyMapper.Source from18 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isCleanOnValidationError()));
            configuration.getClass();
            from18.to((v1) -> {
                r1.cleanOnValidationError(v1);
            });
            PropertyMapper.Source from19 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isGroup()));
            configuration.getClass();
            from19.to((v1) -> {
                r1.group(v1);
            });
            PropertyMapper.Source from20 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isIgnoreMissingMigrations()));
            configuration.getClass();
            from20.to((v1) -> {
                r1.ignoreMissingMigrations(v1);
            });
            PropertyMapper.Source from21 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isIgnoreIgnoredMigrations()));
            configuration.getClass();
            from21.to((v1) -> {
                r1.ignoreIgnoredMigrations(v1);
            });
            PropertyMapper.Source from22 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isIgnorePendingMigrations()));
            configuration.getClass();
            from22.to((v1) -> {
                r1.ignorePendingMigrations(v1);
            });
            PropertyMapper.Source from23 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isIgnoreFutureMigrations()));
            configuration.getClass();
            from23.to((v1) -> {
                r1.ignoreFutureMigrations(v1);
            });
            PropertyMapper.Source from24 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isMixed()));
            configuration.getClass();
            from24.to((v1) -> {
                r1.mixed(v1);
            });
            PropertyMapper.Source from25 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isOutOfOrder()));
            configuration.getClass();
            from25.to((v1) -> {
                r1.outOfOrder(v1);
            });
            PropertyMapper.Source from26 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isSkipDefaultCallbacks()));
            configuration.getClass();
            from26.to((v1) -> {
                r1.skipDefaultCallbacks(v1);
            });
            PropertyMapper.Source from27 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isSkipDefaultResolvers()));
            configuration.getClass();
            from27.to((v1) -> {
                r1.skipDefaultResolvers(v1);
            });
            PropertyMapper.Source from28 = map.from((PropertyMapper) Boolean.valueOf(this.properties.isValidateOnMigrate()));
            configuration.getClass();
            from28.to((v1) -> {
                r1.validateOnMigrate(v1);
            });
        }

        private void configureCallbacks(FluentConfiguration configuration) {
            if (!this.callbacks.isEmpty()) {
                configuration.callbacks((Callback[]) this.callbacks.toArray(new Callback[0]));
            }
        }

        private void configureFlywayCallbacks(Flyway flyway) {
            if (!this.flywayCallbacks.isEmpty()) {
                if (!this.callbacks.isEmpty()) {
                    throw new IllegalStateException("Found a mixture of Callback and FlywayCallback beans. One type must be used exclusively.");
                }
                flyway.setCallbacks((FlywayCallback[]) this.flywayCallbacks.toArray(new FlywayCallback[0]));
            }
        }

        private String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
            String value = property.get();
            return value != null ? value : defaultValue.get();
        }

        private boolean hasAtLeastOneLocation(String... locations) {
            for (String location : locations) {
                if (this.resourceLoader.getResource(normalizePrefix(location)).exists()) {
                    return true;
                }
            }
            return false;
        }

        private String normalizePrefix(String location) {
            return location.replace("filesystem:", ResourceUtils.FILE_URL_PREFIX);
        }

        @ConditionalOnMissingBean
        @Bean
        public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
            return new FlywayMigrationInitializer(flyway, this.migrationStrategy);
        }

        @Configuration
        @ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class})
        @ConditionalOnBean({AbstractEntityManagerFactoryBean.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration$FlywayInitializerJpaDependencyConfiguration.class */
        protected static class FlywayInitializerJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
            public FlywayInitializerJpaDependencyConfiguration() {
                super("flywayInitializer");
            }
        }

        @Configuration
        @ConditionalOnClass({JdbcOperations.class})
        @ConditionalOnBean({JdbcOperations.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration$FlywayInitializerJdbcOperationsDependencyConfiguration.class */
        protected static class FlywayInitializerJdbcOperationsDependencyConfiguration extends JdbcOperationsDependsOnPostProcessor {
            public FlywayInitializerJdbcOperationsDependencyConfiguration() {
                super("flywayInitializer");
            }
        }
    }

    @Configuration
    @ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class})
    @ConditionalOnBean({AbstractEntityManagerFactoryBean.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayJpaDependencyConfiguration.class */
    protected static class FlywayJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
        public FlywayJpaDependencyConfiguration() {
            super("flyway");
        }
    }

    @Configuration
    @ConditionalOnClass({JdbcOperations.class})
    @ConditionalOnBean({JdbcOperations.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayJdbcOperationsDependencyConfiguration.class */
    protected static class FlywayJdbcOperationsDependencyConfiguration extends JdbcOperationsDependsOnPostProcessor {
        public FlywayJdbcOperationsDependencyConfiguration() {
            super("flyway");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$LocationResolver.class */
    public static class LocationResolver {
        private static final String VENDOR_PLACEHOLDER = "{vendor}";
        private final DataSource dataSource;

        LocationResolver(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public String[] resolveLocations(Collection<String> locations) {
            return resolveLocations(StringUtils.toStringArray(locations));
        }

        public String[] resolveLocations(String[] locations) {
            if (usesVendorLocation(locations)) {
                DatabaseDriver databaseDriver = getDatabaseDriver();
                return replaceVendorLocations(locations, databaseDriver);
            }
            return locations;
        }

        private String[] replaceVendorLocations(String[] locations, DatabaseDriver databaseDriver) {
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                return locations;
            }
            String vendor = databaseDriver.getId();
            return (String[]) Arrays.stream(locations).map(location -> {
                return location.replace(VENDOR_PLACEHOLDER, vendor);
            }).toArray(x$0 -> {
                return new String[x$0];
            });
        }

        private DatabaseDriver getDatabaseDriver() {
            try {
                String url = (String) JdbcUtils.extractDatabaseMetaData(this.dataSource, "getURL");
                return DatabaseDriver.fromJdbcUrl(url);
            } catch (MetaDataAccessException ex) {
                throw new IllegalStateException((Throwable) ex);
            }
        }

        private boolean usesVendorLocation(String... locations) {
            for (String location : locations) {
                if (location.contains(VENDOR_PLACEHOLDER)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$StringOrNumberToMigrationVersionConverter.class */
    private static class StringOrNumberToMigrationVersionConverter implements GenericConverter {
        private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_TYPES;

        private StringOrNumberToMigrationVersionConverter() {
        }

        static {
            Set<GenericConverter.ConvertiblePair> types = new HashSet<>(2);
            types.add(new GenericConverter.ConvertiblePair(String.class, MigrationVersion.class));
            types.add(new GenericConverter.ConvertiblePair(Number.class, MigrationVersion.class));
            CONVERTIBLE_TYPES = Collections.unmodifiableSet(types);
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return CONVERTIBLE_TYPES;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String value = ObjectUtils.nullSafeToString(source);
            return MigrationVersion.fromVersion(value);
        }
    }
}