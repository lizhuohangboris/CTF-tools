package org.springframework.boot.autoconfigure.quartz;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

@EnableConfigurationProperties({QuartzProperties.class})
@Configuration
@ConditionalOnClass({Scheduler.class, SchedulerFactoryBean.class, PlatformTransactionManager.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration.class */
public class QuartzAutoConfiguration {
    private final QuartzProperties properties;
    private final ObjectProvider<SchedulerFactoryBeanCustomizer> customizers;
    private final JobDetail[] jobDetails;
    private final Map<String, Calendar> calendars;
    private final Trigger[] triggers;
    private final ApplicationContext applicationContext;

    public QuartzAutoConfiguration(QuartzProperties properties, ObjectProvider<SchedulerFactoryBeanCustomizer> customizers, ObjectProvider<JobDetail[]> jobDetails, ObjectProvider<Map<String, Calendar>> calendars, ObjectProvider<Trigger[]> triggers, ApplicationContext applicationContext) {
        this.properties = properties;
        this.customizers = customizers;
        this.jobDetails = jobDetails.getIfAvailable();
        this.calendars = calendars.getIfAvailable();
        this.triggers = triggers.getIfAvailable();
        this.applicationContext = applicationContext;
    }

    @ConditionalOnMissingBean
    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(this.applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (this.properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(this.properties.getSchedulerName());
        }
        schedulerFactoryBean.setAutoStartup(this.properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) this.properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(this.properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(this.properties.isOverwriteExistingJobs());
        if (!this.properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(asProperties(this.properties.getProperties()));
        }
        if (this.jobDetails != null && this.jobDetails.length > 0) {
            schedulerFactoryBean.setJobDetails(this.jobDetails);
        }
        if (this.calendars != null && !this.calendars.isEmpty()) {
            schedulerFactoryBean.setCalendars(this.calendars);
        }
        if (this.triggers != null && this.triggers.length > 0) {
            schedulerFactoryBean.setTriggers(this.triggers);
        }
        customize(schedulerFactoryBean);
        return schedulerFactoryBean;
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    private void customize(SchedulerFactoryBean schedulerFactoryBean) {
        this.customizers.orderedStream().forEach(customizer -> {
            customizer.customize(schedulerFactoryBean);
        });
    }

    @Configuration
    @ConditionalOnSingleCandidate(DataSource.class)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration$JdbcStoreTypeConfiguration.class */
    protected static class JdbcStoreTypeConfiguration {
        protected JdbcStoreTypeConfiguration() {
        }

        @Bean
        @Order(0)
        public SchedulerFactoryBeanCustomizer dataSourceCustomizer(QuartzProperties properties, DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, ObjectProvider<PlatformTransactionManager> transactionManager) {
            return schedulerFactoryBean -> {
                if (properties.getJobStoreType() == JobStoreType.JDBC) {
                    DataSource dataSourceToUse = getDataSource(dataSource, quartzDataSource);
                    schedulerFactoryBean.setDataSource(dataSourceToUse);
                    PlatformTransactionManager txManager = (PlatformTransactionManager) transactionManager.getIfUnique();
                    if (txManager != null) {
                        schedulerFactoryBean.setTransactionManager(txManager);
                    }
                }
            };
        }

        private DataSource getDataSource(DataSource dataSource, ObjectProvider<DataSource> quartzDataSource) {
            DataSource dataSourceIfAvailable = quartzDataSource.getIfAvailable();
            return dataSourceIfAvailable != null ? dataSourceIfAvailable : dataSource;
        }

        @ConditionalOnMissingBean
        @Bean
        public QuartzDataSourceInitializer quartzDataSourceInitializer(DataSource dataSource, @QuartzDataSource ObjectProvider<DataSource> quartzDataSource, ResourceLoader resourceLoader, QuartzProperties properties) {
            DataSource dataSourceToUse = getDataSource(dataSource, quartzDataSource);
            return new QuartzDataSourceInitializer(dataSourceToUse, resourceLoader, properties);
        }

        @Bean
        public static DataSourceInitializerSchedulerDependencyPostProcessor dataSourceInitializerSchedulerDependencyPostProcessor() {
            return new DataSourceInitializerSchedulerDependencyPostProcessor();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/quartz/QuartzAutoConfiguration$JdbcStoreTypeConfiguration$DataSourceInitializerSchedulerDependencyPostProcessor.class */
        private static class DataSourceInitializerSchedulerDependencyPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
            DataSourceInitializerSchedulerDependencyPostProcessor() {
                super(Scheduler.class, SchedulerFactoryBean.class, "quartzDataSourceInitializer");
            }
        }
    }
}