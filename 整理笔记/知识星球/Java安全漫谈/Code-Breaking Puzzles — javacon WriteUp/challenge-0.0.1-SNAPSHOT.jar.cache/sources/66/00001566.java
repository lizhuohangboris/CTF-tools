package org.springframework.boot.autoconfigure.batch;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/batch/BasicBatchConfigurer.class */
public class BasicBatchConfigurer implements BatchConfigurer {
    private final BatchProperties properties;
    private final DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private final TransactionManagerCustomizers transactionManagerCustomizers;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    /* JADX INFO: Access modifiers changed from: protected */
    public BasicBatchConfigurer(BatchProperties properties, DataSource dataSource, TransactionManagerCustomizers transactionManagerCustomizers) {
        this.properties = properties;
        this.dataSource = dataSource;
        this.transactionManagerCustomizers = transactionManagerCustomizers;
    }

    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public JobLauncher getJobLauncher() {
        return this.jobLauncher;
    }

    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            this.transactionManager = buildTransactionManager();
            this.jobRepository = createJobRepository();
            this.jobLauncher = createJobLauncher();
            this.jobExplorer = createJobExplorer();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize Spring Batch", ex);
        }
    }

    protected JobExplorer createJobExplorer() throws Exception {
        PropertyMapper map = PropertyMapper.get();
        JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(this.dataSource);
        BatchProperties batchProperties = this.properties;
        batchProperties.getClass();
        PropertyMapper.Source whenHasText = map.from(this::getTablePrefix).whenHasText();
        factory.getClass();
        whenHasText.to(this::setTablePrefix);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        PropertyMapper map = PropertyMapper.get();
        PropertyMapper.Source from = map.from((PropertyMapper) this.dataSource);
        factory.getClass();
        from.to(this::setDataSource);
        PropertyMapper.Source whenNonNull = map.from(this::determineIsolationLevel).whenNonNull();
        factory.getClass();
        whenNonNull.to(this::setIsolationLevelForCreate);
        BatchProperties batchProperties = this.properties;
        batchProperties.getClass();
        PropertyMapper.Source whenHasText = map.from(this::getTablePrefix).whenHasText();
        factory.getClass();
        whenHasText.to(this::setTablePrefix);
        PropertyMapper.Source from2 = map.from(this::getTransactionManager);
        factory.getClass();
        from2.to(this::setTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    protected String determineIsolationLevel() {
        return null;
    }

    protected PlatformTransactionManager createTransactionManager() {
        return new DataSourceTransactionManager(this.dataSource);
    }

    private PlatformTransactionManager buildTransactionManager() {
        PlatformTransactionManager transactionManager = createTransactionManager();
        if (this.transactionManagerCustomizers != null) {
            this.transactionManagerCustomizers.customize(transactionManager);
        }
        return transactionManager;
    }
}