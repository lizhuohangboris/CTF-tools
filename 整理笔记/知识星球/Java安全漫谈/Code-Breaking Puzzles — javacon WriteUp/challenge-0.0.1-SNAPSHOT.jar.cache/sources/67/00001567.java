package org.springframework.boot.autoconfigure.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({BatchProperties.class})
@Configuration
@ConditionalOnClass({JobLauncher.class, DataSource.class, JdbcOperations.class})
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class})
@ConditionalOnBean({JobLauncher.class})
@Import({BatchConfigurerConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.class */
public class BatchAutoConfiguration {
    private final BatchProperties properties;
    private final JobParametersConverter jobParametersConverter;

    public BatchAutoConfiguration(BatchProperties properties, ObjectProvider<JobParametersConverter> jobParametersConverter) {
        this.properties = properties;
        this.jobParametersConverter = jobParametersConverter.getIfAvailable();
    }

    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class})
    @Bean
    public BatchDataSourceInitializer batchDataSourceInitializer(DataSource dataSource, ResourceLoader resourceLoader) {
        return new BatchDataSourceInitializer(dataSource, resourceLoader, this.properties);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    public JobLauncherCommandLineRunner jobLauncherCommandLineRunner(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository) {
        JobLauncherCommandLineRunner runner = new JobLauncherCommandLineRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = this.properties.getJob().getNames();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobNames(jobNames);
        }
        return runner;
    }

    @ConditionalOnMissingBean({ExitCodeGenerator.class})
    @Bean
    public JobExecutionExitCodeGenerator jobExecutionExitCodeGenerator() {
        return new JobExecutionExitCodeGenerator();
    }

    @ConditionalOnMissingBean({JobOperator.class})
    @Bean
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer, JobLauncher jobLauncher, ListableJobLocator jobRegistry, JobRepository jobRepository) throws Exception {
        SimpleJobOperator factory = new SimpleJobOperator();
        factory.setJobExplorer(jobExplorer);
        factory.setJobLauncher(jobLauncher);
        factory.setJobRegistry(jobRegistry);
        factory.setJobRepository(jobRepository);
        if (this.jobParametersConverter != null) {
            factory.setJobParametersConverter(this.jobParametersConverter);
        }
        return factory;
    }
}